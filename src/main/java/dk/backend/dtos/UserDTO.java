package dk.backend.dtos;

import dk.backend.entities.User;

import java.security.Principal;
import java.util.Set;

public class UserDTO implements Principal {

    private final String username;

    private String password;

    private Set<String> roles;

    public UserDTO(User user) {
        this.username = user.getUserName();
        this.roles = user.getRolesAsStrings();
    }

    public UserDTO(String username, String[] roles) {
        this.username = username;
        this.roles = Set.of(roles);
    }

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String getName() {
        return username;
    }

    public boolean isUserInRole(String role) {
        return this.roles.contains(role);
    }
}
