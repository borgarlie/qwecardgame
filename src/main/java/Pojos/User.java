package Pojos;

import API.authentication.Roles;
import lombok.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder
public class User {
    String userId;
    String email;
    String name;
    String username;
    Roles role;
}
