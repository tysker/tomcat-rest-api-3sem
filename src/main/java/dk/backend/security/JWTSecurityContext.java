package dk.backend.security;

import dk.backend.dtos.UserDTO;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

public class JWTSecurityContext implements SecurityContext {
    UserDTO user;
    ContainerRequestContext request;

    public JWTSecurityContext(UserDTO user, ContainerRequestContext request) {
        this.user = user;
        this.request = request;
    }

    @Override
    public boolean isUserInRole(String role) {
        return user.isUserInRole(role);
    }

    @Override
    public boolean isSecure() {
        return request.getUriInfo().getBaseUri().getScheme().equals("https");
    }

    @Override
    public Principal getUserPrincipal() {
        return user;
    }

    @Override
    public String getAuthenticationScheme() {return "JWT";}
}