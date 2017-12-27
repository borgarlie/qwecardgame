package API.Game;

import Database.CardDatabase;
import Pojos.Card;
import io.javalin.Handler;

import java.util.HashMap;
import java.util.List;

public class Cards {

    public static Handler getAll = ctx -> {
        List<Card> cards = CardDatabase.getAll();
        cards.forEach(System.out::println);
        // wrap in map to create named entities in json
        HashMap<String, Object> map = new HashMap<>();
        map.put("cards", cards);
        ctx.json(map);
    };

}
