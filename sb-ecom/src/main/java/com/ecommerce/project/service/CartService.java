package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.CartItemDTO;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCart();

    CartDTO getCart(String emailId, Long cartId);

    CartDTO updateProductQuantityInCart(Long productId, Integer delete);

    String deleteProductFromCart(Long cardId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);

    @Transactional
    String createOrUpdateCartWithItems(List<CartItemDTO> cartItems);
}
