package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.CartItemDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.project.util.AuthUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtils authUtils;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

        @Override
        public CartDTO addProductToCart(Long productId, Integer quantity) {

            Cart cart= createCart();

            Product product= productRepository.findById(productId)
                    .orElseThrow(()-> new ResourceNotFoundException("Product","id",productId));


            CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),product.getProductId());

            if(cartItem!=null) {
                throw new APIException("Product already exists in cart");
            }
            
            // Create new cart item if it doesn't exist
            cartItem = new CartItem();

            if(product.getProductQuantity() ==0) {
                throw new APIException("Product is out of stock");
            }

            if(product.getProductQuantity() < quantity) {
                throw new APIException("Product quantity is less than required quantity");
            }

            // Set cart item properties
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getProductSpecialPrice());
            cartItem.setDiscount(product.getDiscount());

            // Save the cart item
            cartItem = cartItemRepository.save(cartItem);
            
            // Add the cart item to the cart's items list if not already present
            if (cart.getCartItems() == null) {
                cart.setCartItems(new ArrayList<>());
            }
            if (!cart.getCartItems().contains(cartItem)) {
                cart.getCartItems().add(cartItem);
            }
            
            // Update the total price
            double itemTotal = product.getProductSpecialPrice() * quantity;
            cart.setTotalPrice(cart.getTotalPrice() + itemTotal);
            
            // Save the updated cart
            cart = cartRepository.save(cart);
            
            // Map to DTO
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            
            // Get the updated cart items
            List<CartItem> cartItems = cart.getCartItems();

            Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
                ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
                map.setProductQuantity(item.getQuantity());
                return map;
            });

            cartDTO.setProducts(productStream.toList());

            return cartDTO;
        }

    @Override
    public List<CartDTO> getAllCart() {
       List<Cart> carts =cartRepository.findAll();
        if(carts.isEmpty()) {
            throw new APIException("No cart found");
        }
        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                productDTO.setProductQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                return productDTO;
            }).collect(Collectors.toList());

            cartDTO.setProducts(products);
            return cartDTO;
        }).collect(Collectors.toList());


         return cartDTOs;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findByEmailAndCartId(emailId, cartId);

        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "id", cartId);
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        List<ProductDTO> products = cartItems.stream().map(cartItem -> {
            ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
            productDTO.setProductQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
            return productDTO;
        }).collect(Collectors.toList());

        cartDTO.setProducts(products);

        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        // Input validation
        if (quantity == 0) {
            throw new APIException("Quantity cannot be zero. Use remove item instead.");
        }

        String emailId = authUtils.loggedInEmail();
        Cart cart = cartRepository.findByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Check product availability
        if (product.getProductQuantity() <= 0) {
            throw new APIException(product.getProductName() + " is out of stock");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not found in cart");
        }

        // Calculate new quantity
        int newQuantity = cartItem.getQuantity() + quantity;

        // Validate new quantity
        if (newQuantity <= 0) {
            // Remove item if quantity becomes 0 or negative
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            // Check stock availability
            if (newQuantity > product.getProductQuantity()) {
                throw new APIException("Only " + product.getProductQuantity() + " items available in stock");
            }

            // Update cart item
            cartItem.setQuantity(newQuantity);
            cartItem.setProductPrice(product.getProductSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }

        // Recalculate cart total
        double newTotal = cart.getCartItems().stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(newTotal);
        cart = cartRepository.save(cart);

        // Map to DTO
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(item -> {
                    ProductDTO dto = modelMapper.map(item.getProduct(), ProductDTO.class);
                    dto.setProductQuantity(item.getQuantity());
                    return dto;
                })
                .collect(Collectors.toList());
        cartDTO.setProducts(products);

        return cartDTO;
    }

    @Override
    @Transactional
    public String deleteProductFromCart(Long cartId, Long productId) {

            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));

            CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
            if (cartItem == null) {
                throw new ResourceNotFoundException("Product", "id", productId);
            }

            cart.setTotalPrice(cart.getTotalPrice() - cartItem.getProductPrice() * cartItem.getQuantity());

            cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);
            cartRepository.save(cart);

            return "Product removed from cart successfully";

    }

    private Cart createCart() {
            Cart userCart= cartRepository.findByEmail((authUtils.loggedInEmail()));

            if(userCart!=null) {
                return userCart;
            }
            Cart newCart= new Cart();
            newCart.setTotalPrice(0.0);
            newCart.setUser(authUtils.loggedInUser());
            return cartRepository.save(newCart);
        }


    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        // Calculate price before updating
        double oldItemTotal = cartItem.getProductPrice() * cartItem.getQuantity();

        // Update both price and discount from the product
        cartItem.setProductPrice(product.getProductSpecialPrice());
        cartItem.setDiscount(product.getDiscount());  // This line is needed!

        // Calculate new total
        double newItemTotal = cartItem.getProductPrice() * cartItem.getQuantity();
        double newCartTotal = cart.getTotalPrice() - oldItemTotal + newItemTotal;

        // Update cart total
        cart.setTotalPrice(newCartTotal);

        cartItemRepository.save(cartItem);
    }

    @Transactional
    @Override
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        // Get user's email
        String emailId = authUtils.loggedInEmail();

        // Check if an existing cart is available or create a new one
        Cart existingCart = cartRepository.findByEmail(emailId);
        if (existingCart == null) {
            existingCart = new Cart();
            existingCart.setTotalPrice(0.00);
            existingCart.setUser(authUtils.loggedInUser());
            existingCart = cartRepository.save(existingCart);
        } else {
            // Clear all current items in the existing cart
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        double totalPrice = 0.00;

        // Process each item in the request to add to the cart
        for (CartItemDTO cartItemDTO : cartItems) {
            Long productId = cartItemDTO.getCartItemId();
            Integer quantity = cartItemDTO.getQuantity();

            // Find the product by ID
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

            // Directly update product stock and total price
            // product.setQuantity(product.getQuantity() - quantity);
            totalPrice += product.getProductSpecialPrice() * quantity;

            // Create and save cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getProductSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }

        // Update the cart's total price and save
        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);
        return "Cart created/updated with the new items successfully";
    }




}
