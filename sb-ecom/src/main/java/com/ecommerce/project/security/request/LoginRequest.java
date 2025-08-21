package com.ecommerce.project.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank
    private String userName;

    @NotBlank
    private String password;

}