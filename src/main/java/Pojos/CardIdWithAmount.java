package Pojos;


import lombok.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder
public class CardIdWithAmount {
    int id;
    int amount;
}
