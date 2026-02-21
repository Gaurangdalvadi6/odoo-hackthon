package com.fleetflow.service;

import com.fleetflow.dto.user.ForgotPasswordRequest;
import com.fleetflow.dto.user.ForgotPasswordResponse;
import com.fleetflow.dto.user.LoginRequest;
import com.fleetflow.dto.user.RegisterUserRequest;
import com.fleetflow.dto.user.ResetPasswordRequest;
import com.fleetflow.dto.user.UserResponse;

public interface AuthService {

    UserResponse register(RegisterUserRequest request);

    String login(LoginRequest request);

    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    String resetPassword(ResetPasswordRequest request);
}
