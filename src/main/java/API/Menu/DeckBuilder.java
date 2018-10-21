package API.Menu;

import Database.DeckDatabase;
import Pojos.CardIdWithAmount;
import Pojos.Deck;
import io.javalin.Handler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DeckBuilder {
    public static Handler createNew = ctx -> {
        String body = ctx.body();

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(body);
        JSONObject jsonObject = (JSONObject) obj;

        String deck_name = (String) jsonObject.get("deck_name");
        String username = (String) jsonObject.get("username");
        JSONArray cards = (JSONArray) jsonObject.get("card_ids");
        ArrayList<CardIdWithAmount> card_ids = new ArrayList<>();

        Iterator i = cards.iterator();
        while (i.hasNext()) {
            JSONObject innerCard = (JSONObject) i.next();
            int card_id = ((Long) innerCard.get("id")).intValue();
            int amount = ((Long) innerCard.get("amount")).intValue();
            CardIdWithAmount cardIdWithAmount = new CardIdWithAmount(card_id, amount);
            card_ids.add(cardIdWithAmount);
        }

        System.out.println("Saving new deck: ");
        System.out.println("Deck name: " + deck_name);
        System.out.println("Username: " + username);
        System.out.println("Card Ids: " + card_ids);

        Deck deck = Deck.builder()
                .name(deck_name)
                .username(username)
                .cardIds(card_ids)
                .build();

        int deck_id = DeckDatabase.create(deck);
        HashMap<String, Object> map = new HashMap<>();
        map.put("deck_id", deck_id);
        ctx.json(map);
    };

    public static Handler getDecksByUsername = ctx -> {
        String username = ctx.pathParam("username");
        List<Deck> decks = DeckDatabase.getAllForUsername(username);
        HashMap<String, Object> map = new HashMap<>();
        map.put("decks", decks);
        ctx.json(map);
    };

    public static Handler update = ctx -> {
        String body = ctx.body();

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(body);
        JSONObject jsonObject = (JSONObject) obj;

        String deck_id_param = ctx.pathParam("id");
        if (deck_id_param == null) {
            return;
            // TODO: error handling
        }

        int deck_id = Integer.parseInt(deck_id_param);
        String deck_name = (String) jsonObject.get("deck_name");
        String username = (String) jsonObject.get("username");
        JSONArray cards = (JSONArray) jsonObject.get("card_ids");
        ArrayList<CardIdWithAmount> card_ids = new ArrayList<>();

        Iterator i = cards.iterator();
        while (i.hasNext()) {
            JSONObject innerCard = (JSONObject) i.next();
            int card_id = ((Long) innerCard.get("id")).intValue();
            int amount = ((Long) innerCard.get("amount")).intValue();
            CardIdWithAmount cardIdWithAmount = new CardIdWithAmount(card_id, amount);
            card_ids.add(cardIdWithAmount);
        }

        System.out.println("Updating deck: ");
        System.out.println("Deck Id: " + deck_id);
        System.out.println("Deck name: " + deck_name);
        System.out.println("Username: " + username);
        System.out.println("Card Ids: " + card_ids);

        Deck deck = Deck.builder()
                .id(deck_id)
                .name(deck_name)
                .username(username)
                .cardIds(card_ids)
                .build();

        DeckDatabase.update(deck);
        ctx.json(true);
    };
}
