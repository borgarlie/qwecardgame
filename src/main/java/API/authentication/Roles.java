package API.authentication;

import io.javalin.security.Role;

import java.util.HashMap;
import java.util.Map;

public enum Roles implements Role {
    ANYONE,
    USER,
    ADMIN;

    public static Map<String, Role> rolesMapping = new HashMap<String, Role>() {{
        put("USER", Roles.USER);
        put("ADMIN", Roles.ADMIN);
    }};
}
