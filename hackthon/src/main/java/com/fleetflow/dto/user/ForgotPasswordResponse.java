package com.fleetflow.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ForgotPasswordResponse {

    private String message;
    private String resetToken;  // In production, send via email; for demo returned here
}
