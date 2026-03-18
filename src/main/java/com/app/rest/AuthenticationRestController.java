package com.app.rest;

import com.app.dto.ApiResponse;
import com.app.dto.LoginRequest;
import com.app.dto.LoginResponse;
import com.app.model.User;
import com.app.service.IAuthenticationService;
import com.app.service.impl.AuthenticationServiceImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationRestController {
    
    private IAuthenticationService authService;

    public AuthenticationRestController() {
        this.authService = new AuthenticationServiceImpl();
    }


    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        try {
            // Validate input
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Username and password are required"))
                        .build();
            }

            // Attempt login
            User user = authService.login(loginRequest.getUsername(), loginRequest.getPassword());

            if (user != null) {
                LoginResponse loginResponse = new LoginResponse(
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole()
                );
                return Response.ok(ApiResponse.success("Login successful", loginResponse)).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ApiResponse.error("Invalid username or password"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("An error occurred during login: " + e.getMessage()))
                    .build();
        }
    }


    @POST
    @Path("/register")
    public Response register(User user) {
        try {
            // Validate input (using fullName as placeholder for validation)
            String validationError = authService.validateStaffRegistration(
                user.getUsername(), 
                user.getPassword(), 
                user.getFullName() != null ? user.getFullName() : "",
                "", // email not in User model
                user.getRole() != null ? user.getRole() : "STAFF"
            );

            if (validationError != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error(validationError))
                        .build();
            }

            // Register user
            boolean success = authService.registerStaff(
                user.getUsername(),
                user.getPassword(),
                user.getFullName() != null ? user.getFullName() : "",
                "", // email parameter
                user.getRole() != null ? user.getRole() : "STAFF"
            );

            if (success) {
                return Response.status(Response.Status.CREATED)
                        .entity(ApiResponse.success("User registered successfully", null))
                        .build();
            } else {
                return Response.status(Response.Status.CONFLICT)
                        .entity(ApiResponse.error("Username or email already exists"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("An error occurred during registration: " + e.getMessage()))
                    .build();
        }
    }


    @GET
    @Path("/check-username")
    public Response checkUsername(@QueryParam("username") String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Username is required"))
                        .build();
            }

            boolean exists = authService.userExists(username);
            return Response.ok(ApiResponse.success("Username check", exists)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("An error occurred: " + e.getMessage()))
                    .build();
        }
    }
}
