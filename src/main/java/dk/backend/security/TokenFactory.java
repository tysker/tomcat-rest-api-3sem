package dk.backend.security;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.SimpleSecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import dk.backend.dtos.UserDTO;
import dk.backend.exceptions.authentication.AuthenticationException;
import dk.backend.exceptions.API_Exception;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class TokenFactory {

    private static TokenFactory instance;
    private final Dotenv dotenv;

    //   https://github.com/cdimascio/dotenv-java
    private TokenFactory() {
        dotenv = Dotenv
                .configure()
                .load();
    }

    public static TokenFactory getInstance() {
        if (instance == null) {
            instance = new TokenFactory();
        }
        return new TokenFactory();
    }

    public String createToken(String userName, Set<String> roles) throws API_Exception {

        try {
            StringBuilder res = new StringBuilder();
            for (String string : roles) {
                res.append(string);
                res.append(",");
            }

            String rolesAsString = res.length() > 0 ? res.substring(0, res.length() - 1) : "";
            String issuer = dotenv.get("ISSUER");

            Date date = new Date();
            // https://dzone.com/articles/using-nimbus-jose-jwt-in-spring-applications-why-a
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userName)
                    .issuer(issuer)
                    .claim("username", userName)
                    .claim("roles", rolesAsString)
                    .expirationTime(new Date(date.getTime() + Integer.parseInt(dotenv.get("TOKEN_EXPIRE_TIME"))))
                    .build();

            Payload payload = new Payload(claims.toJSONObject());
            JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256);

            DirectEncrypter encrypter = new DirectEncrypter(dotenv.get("SECRET_KEY").getBytes());

            JWEObject jweObject = new JWEObject(header, payload);
            jweObject.encrypt(encrypter);

            return jweObject.serialize();
        } catch (Exception exe) {
            throw new API_Exception("Could not create token", 500, exe);
        }

    }

    public String[] parseJsonObject(String jsonString, Boolean tryLogin) throws API_Exception {
        try {
            List<String> roles = Arrays.asList("user", "admin");
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            String username = json.get("username").getAsString();
            String password = json.get("password").getAsString();
            String role = "";

            if (!tryLogin) {
                role = json.get("role").getAsString();
                if (!roles.contains(role)) throw new API_Exception("Role not valid");
            }

            return new String[]{username, password, role};
        } catch (JsonSyntaxException | NullPointerException e) {
            throw new API_Exception("Malformed JSON Supplied", 400, e);
        } catch (API_Exception e) {
            throw new API_Exception(e.getMessage(), 400, e);
        }
    }


    public UserDTO verifyToken(String token) throws AuthenticationException {
        try {
            ConfigurableJWTProcessor<SimpleSecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            JWKSource<SimpleSecurityContext> jweKeySource = new ImmutableSecret<>(dotenv.get("SECRET_KEY").getBytes());
            JWEKeySelector<SimpleSecurityContext> jweKeySelector = new JWEDecryptionKeySelector<>(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256, jweKeySource);
            jwtProcessor.setJWEKeySelector(jweKeySelector);
            JWTClaimsSet claimsSet = jwtProcessor.process(token, null);

            if (new Date().after(claimsSet.getExpirationTime())) throw new AuthenticationException("Token is expired");

            String username = claimsSet.getClaim("username").toString();
            String roles = claimsSet.getClaim("roles").toString();

            String[] rolesArray = roles.split(",");

            return new UserDTO(username, rolesArray);

        } catch (Exception e) {
            throw new AuthenticationException("Could not validate token");
        }
    }
}
