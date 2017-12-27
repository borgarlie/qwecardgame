package Pojos;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder(toBuilder=true)
public class Deck {
    int id;
    String name;
    String username;
    List<CardWithAmount> cards; // Used to get card information when viewing a deck
    List<CardIdWithAmount> cardIds; // alternative when creating a new deck or updating

    public ArrayList<Card> initialiseDeck() {
        ArrayList<Card> newDeck = new ArrayList<>();
        cards.forEach(cardWithAmount -> {
            for (int i = 0; i < cardWithAmount.getAmount(); i++) {
                Card copy = cardWithAmount.card.toBuilder().build();
                newDeck.add(copy);
            }
        });
        return newDeck;
    }
}
