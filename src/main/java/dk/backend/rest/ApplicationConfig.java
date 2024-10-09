package dk.backend.rest;

import dk.backend.exceptions.API_ExceptionMapper;
import dk.backend.exceptions.authentication.AuthenticationExceptionMapper;
import dk.backend.exceptions.authentication.NotAuthorizedExceptionMapper;
import dk.backend.security.JWTAuthenticationFilter;
import dk.backend.security.RolesAllowedFilter;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
        resources.add(RolesAllowedFilter.class);
        resources.add(JWTAuthenticationFilter.class);
        resources.add(ContainerReqResFilter.class);
        resources.add(AuthenticationResource.class);
        resources.add(AuthenticationExceptionMapper.class);
        resources.add(API_ExceptionMapper.class);
        resources.add(NotAuthorizedExceptionMapper.class);
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("jersey.config.server.wadl.disableWadl", "true");
        return properties;
    }
}