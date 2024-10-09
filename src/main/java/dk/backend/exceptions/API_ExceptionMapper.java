package dk.backend.exceptions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class API_ExceptionMapper implements ExceptionMapper<API_Exception> {

    @Context
    private UriInfo uriInfo;

    @Context
    ServletContext context;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Response toResponse(API_Exception exception) {
        String requestPath = uriInfo.getPath();
        String errorMessage = String.format("Error processing request for path '%s': %s", requestPath, exception.getMessage());
        ExceptionDTO error = new ExceptionDTO(exception.getErrorCode(), errorMessage);
        return Response
                .status(exception.getErrorCode())
                .entity(gson.toJson(error))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}