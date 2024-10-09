package dk.backend.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dk.backend.entities.User;
import dk.backend.exceptions.API_Exception;
import dk.backend.exceptions.authentication.AuthenticationException;
import dk.backend.facade.user.UserFacade;
import dk.backend.security.TokenFactory;
import dk.backend.utilty.EMF_Creator;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManagerFactory;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("auth")
public class AuthenticationResource {

    private final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    private final TokenFactory tokenFactory = TokenFactory.getInstance();

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonString) throws API_Exception, AuthenticationException {

        String[] userInfos = tokenFactory.parseJsonObject(jsonString, true);
        User user = USER_FACADE.getVerifiedUser(userInfos[0], userInfos[1]);
        String token = tokenFactory.createToken(userInfos[0], user.getRolesAsStrings());
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("username", userInfos[0]);
        responseJson.addProperty("token", token);

        return Response.ok(new Gson().toJson(responseJson)).build();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(String jsonString) throws API_Exception {
            String[] userInfos = tokenFactory.parseJsonObject(jsonString, false);

            User user = USER_FACADE.createUser(userInfos[0], userInfos[1], userInfos[2]);
            String token = tokenFactory.createToken(userInfos[0], user.getRolesAsStrings());

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("username", userInfos[0]);
            responseJson.addProperty("token", token);

            return Response.status(201).entity(new Gson().toJson(responseJson)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response isServerRunning() {
        return Response.ok("{\"message\" : \"Server is running!\"}").build();
    }

    @DenyAll
    @GET
    @Path("denyall")
    @Produces(MediaType.APPLICATION_JSON)
    public Response denyAll() {
        return Response.status(401).entity("{\"message\" : \"You are not authenticated to perform the requested operation\"}").build();
    }

    @RolesAllowed("admin")
    @GET
    @Path("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response admin() {
        return Response.ok("{\"message\" : \"You are authenticated to access this endpoint\"}").build();
    }

    @RolesAllowed("user")
    @GET
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response user() {
        return Response.ok("{\"message\" : \"You are authenticated to access this endpoint\"}").build();
    }

    @PermitAll
    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response all() {
        return Response.ok("{\"message\" : \"You are authenticated to access this endpoint\"}").build();
    }

    @RolesAllowed({"admin", "user"})
    @GET
    @Path("multiplyRoles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response multiplyRoles() {
        return Response.ok("{\"message\" : \"Multiply roles allowed\"}").build();
    }

    @GET
    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() {
        return Response.ok("{\"message\" : \"This is just a test.\"}").build();
    }
}

