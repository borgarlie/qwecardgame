package Pojos;


import lombok.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder
public class CardWithAmount {
    Card card;
    int amount;
}
