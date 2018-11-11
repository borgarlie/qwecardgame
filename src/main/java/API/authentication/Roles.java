package API.authentication;

import io.javalin.security.Role;

import java.util.*;

public enum Roles implements Role {
    ANYONE,
    USER,
    ADMIN;

    public static Map<String, Role> rolesMapping = new HashMap<String, Role>() {{
        put("USER", Roles.USER);
        put("ADMIN", Roles.ADMIN);
    }};

    public static Set<Role> roles(Roles ... roles) {
        return new HashSet<>(Arrays.asList(roles));
    }
}
