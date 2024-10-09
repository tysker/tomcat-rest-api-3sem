package dk.backend.security;

import dk.backend.dtos.UserDTO;
import dk.backend.exceptions.authentication.AuthenticationException;
import dk.backend.exceptions.authentication.NotAuthorizedExceptionMapper;
import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthenticationFilter implements ContainerRequestFilter {

    private static final List<Class<? extends Annotation>> securityAnnotations = Arrays.asList(DenyAll.class, PermitAll.class, RolesAllowed.class);

    private final TokenFactory tokenFactory = TokenFactory.getInstance();

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext request) {
        if (isSecuredResource()) {

            // Bear in mind that the header value is prefixed with "Bearer" in the Authorization header
            String token = request.getHeaderString("Authorization").split(" ")[1];

            if (token == null) {
                request.abortWith(NotAuthorizedExceptionMapper.makeErrRes("Not authenticated - do login", 403));
                return;
            }
            try {
                UserDTO user = tokenFactory.verifyToken(token);
                request.setSecurityContext(new JWTSecurityContext(user, request));
            } catch (AuthenticationException ex) {
                request.abortWith(NotAuthorizedExceptionMapper.makeErrRes("Not authenticated - do login", 403));
            }
        }
    }

    private boolean isSecuredResource() {

        for (Class<? extends Annotation> securityClass : securityAnnotations) {
            if (resourceInfo.getResourceMethod().isAnnotationPresent(securityClass)) {
                return true;
            }
        }
        for (Class<? extends Annotation> securityClass : securityAnnotations) {
            if (resourceInfo.getResourceClass().isAnnotationPresent(securityClass)) {
                return true;
            }
        }
        return false;
    }
}
