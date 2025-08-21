package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

/* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */
    /**
     * Adds a product to a specific category.
     *
     * @param categoryId The ID of the category to add the product to.
     * @param productDTO The details of the product to add.
     * @return The details of the newly added product.
     */
/* <<<<<<<<<<  2a2a3ca6-64d7-425c-b602-bab98f460d96  >>>>>>>>>>> */
    @PostMapping("/admin/categories/{categoryId}/products")
    public ResponseEntity<ProductDTO> addProduct(
            @PathVariable Long categoryId,
            @RequestBody ProductDTO productDTO
    ) {
        ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);
        return new ResponseEntity <>(savedProductDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")

    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                          @RequestParam(name="pageSize" ,defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                          @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_BY,required = false) String sortBy,
                                                          @RequestParam(name="sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir) {
        ProductResponse products = productService.getAllProducts(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                                 @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                 @RequestParam(name="pageSize" ,defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                                 @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_BY,required = false) String sortBy,
                                                                 @RequestParam(name="sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir) {
        ProductResponse products = productService.searchByCategory(categoryId,pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(@PathVariable String keyword,
                                                               @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                               @RequestParam(name="pageSize" ,defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                               @RequestParam(name="sortBy",defaultValue = AppConstants.SORT_BY,required = false) String sortBy,
                                                               @RequestParam(name="sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir) {

        ProductResponse products= productService.searchProductByKeyword(keyword,pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(products, HttpStatus.OK);

    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO,
                                                    @PathVariable Long productId){

        ProductDTO updatedProduct= productService.updateProduct(productId,productDTO);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);

    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
      ProductDTO deletedProduct=  productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct,HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> uploadProductImage(@PathVariable Long productId,@RequestParam("image") MultipartFile file){
       ProductDTO updatedProduct =productService.uploadProductImage(productId,file);

       return new ResponseEntity<>(updatedProduct,HttpStatus.OK);

    }

}
