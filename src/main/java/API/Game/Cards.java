package API.Game;

import Database.CardDatabase;
import Pojos.Card;
import io.javalin.Context;
import io.javalin.Handler;

import java.util.List;

public class Cards {

    public static Handler getAll = ctx -> {
        List<Card> cards = CardDatabase.getAll();
        cards.forEach(System.out::println);
        ctx.json(cards);
    };

}
