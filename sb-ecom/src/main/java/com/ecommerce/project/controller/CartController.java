package com.ecommerce.project.controller;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.project.util.AuthUtils;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    AuthUtils authUtil;

    @Autowired
    CartRepository cartRepository;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity) {
        CartDTO cart= cartService.addProductToCart(productId,quantity);
        return new ResponseEntity<CartDTO>(cart, HttpStatus.OK);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCart() {
       List<CartDTO> carts= cartService.getAllCart();
        return new ResponseEntity<List<CartDTO>>(carts, HttpStatus.OK);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId, cartId);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable String operation) {

        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cardId}/products/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cardId,
                                                        @PathVariable Long productId) {
        String status=cartService.deleteProductFromCart(cardId, productId);
        return new ResponseEntity<String>(status, HttpStatus.OK);
    }

}
