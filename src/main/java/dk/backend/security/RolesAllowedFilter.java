package dk.backend.security;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.Method;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class RolesAllowedFilter implements ContainerRequestFilter {

  @Context
  private ResourceInfo resourceInfo;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    Method resourceMethod = resourceInfo.getResourceMethod();

    // DenyAll on the method take precedence over RolesAllowed and PermitAll
    if (resourceMethod.isAnnotationPresent(DenyAll.class)) {
       throw new NotAuthorizedException("Resource Not Found");
      
    }

    // RolesAllowed on the method takes precedence over PermitAll
    RolesAllowed ra = resourceMethod.getAnnotation(RolesAllowed.class);
    if (assertRole(requestContext, ra)) {
      return;
    }

    // PermitAll takes precedence over RolesAllowed on the class
    if (resourceMethod.isAnnotationPresent(PermitAll.class)) {
      return;
    }

    if (resourceInfo.getResourceClass().isAnnotationPresent(DenyAll.class)) {
      //requestContext.abortWith(NOT_FOUND);
      throw new NotAuthorizedException("Resource Not Found");
    }

    // RolesAllowed on the class takes precedence over PermitAll
    ra = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
    if (assertRole(requestContext, ra)) {
      return;
    }
  }

  private boolean assertRole(ContainerRequestContext requestContext, RolesAllowed ra) {
    if (ra != null) {
      String[] roles = ra.value();

      for (String role : roles) {
        if (requestContext.getSecurityContext().isUserInRole(role)) {
          return true;
        }
      }
      throw new NotAuthorizedException("You are not authorized to perform the requested operation", Response.Status.FORBIDDEN);
    }
    return false;
  }
}