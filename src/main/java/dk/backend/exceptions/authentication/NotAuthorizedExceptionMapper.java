package dk.backend.exceptions.authentication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.backend.exceptions.ExceptionDTO;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final int ERROR_CODE = 401;

    @Context
    private UriInfo uriInfo;

    @Context
    ServletContext context;

    public static Response makeErrRes(String s, int i) {
        ExceptionDTO err = new ExceptionDTO(i, s);
        return Response.status(i).entity(gson.toJson(err)).type(MediaType.APPLICATION_JSON).build();
    }

    @Override
    public Response toResponse(NotAuthorizedException exception) {
        String requestPath = uriInfo.getPath();
        String errorMessage = String.format("Error processing request for path '%s': %s", requestPath, exception.getMessage());
        ExceptionDTO error = new ExceptionDTO(ERROR_CODE, errorMessage);
        return Response.status(ERROR_CODE).entity(gson.toJson(error)).type(MediaType.APPLICATION_JSON).build();
    }
}