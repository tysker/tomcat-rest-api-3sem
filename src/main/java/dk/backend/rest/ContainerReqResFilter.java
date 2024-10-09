package dk.backend.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ContainerReqResFilter implements ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerReqResFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {

        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();
        int status = responseContext.getStatus();
        Object entity = responseContext.getEntity(); // This is the response body

        LOGGER.info("Request path: {}, method: {}, status: {}", path, method, status);
        LOGGER.info("Response body: {}", entity);

        responseContext.getHeaders().add(
                "Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}
