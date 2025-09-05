package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private com.ecommerce.project.util.AuthUtils authUtils;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private FileService fileService;

    @Value("${projects.image}")
    private String path;

    @Value("${images.url}")
    private String imageBaseUrl;


    @Override
    public ProductDTO addProduct(Long categoryId, @Valid ProductDTO productDTO) {

        Category category= categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","id",categoryId));

        boolean isProductNotPresent = false;

        List<Product> products = category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = true;
                break;
            }
        }

        if(!isProductNotPresent) {

            Product product=modelMapper.map(productDTO,Product.class);

            product.setCategory(category);
            Double specialPrice=product.getProductPrice()-(product.getProductPrice()*product.getDiscount()*0.01);
            product.setProductSpecialPrice(specialPrice);
            product.setProductImage("default.jpg");
            Product savedProduct=productRepository.save(product);
            return modelMapper.map(savedProduct,ProductDTO.class);}
        else {
            throw new APIException("Product with name"+ " "+productDTO.getProductName()+ " " + "already exists");
        }

    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails=PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts=productRepository.findAll(pageDetails);

        List<Product> products=pageProducts.getContent();


        if(products.isEmpty()) {
            throw new APIException("No products found");
        }
        else {
            List<ProductDTO> productDTOs=products.stream().map(product->{
                ProductDTO productDTO=modelMapper.map(product,ProductDTO.class);
                productDTO.setProductImage(getImageUrl(product.getProductImage()));
                return productDTO;
            }).toList();
            ProductResponse productResponse=new ProductResponse();
            productResponse.setContent(productDTOs);
            return productResponse;
        }

    }
    private String getImageUrl(String imageName){
        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByProductPriceAsc(category, pageDetails);

        List<Product> products = pageProducts.getContent();

        if(products.isEmpty()){
            throw new APIException(category.getCategoryName() + " category does not have any products");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);

        List<Product> products = pageProducts.getContent();

        if(products.isEmpty()){
            throw new APIException(keyword + " keyword does not have any products");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Product product = modelMapper.map(productDTO, Product.class);

        productFromDb.setProductName(product.getProductName());
        productFromDb.setProductDescription(product.getProductDescription());
        productFromDb.setProductQuantity(product.getProductQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setProductPrice(product.getProductPrice());
        double specialPrice=productDTO.getProductPrice()-(productDTO.getProductPrice()*product.getDiscount()*0.01);
        productFromDb.setProductSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(productFromDb);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(toList());

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // DELETE
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO uploadProductImage(Long productId, MultipartFile file) {


        Product product=productRepository.findById(productId).orElseThrow(()->
                new ResourceNotFoundException("Product","id",productId));


        String fileName= null;
        try {
            fileName =  fileService.uploadImage(path, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        product.setProductImage(fileName);

        //save the updated product
        Product savedProduct=productRepository.save(product);
        // return the updated product
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

}