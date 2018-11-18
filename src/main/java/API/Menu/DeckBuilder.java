package API.Menu;

import Database.DeckDatabase;
import Pojos.CardIdWithAmount;
import Pojos.Deck;
import ThirdParty.javalinjwt.JavalinJWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Strings;
import io.javalin.Handler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static API.authentication.JWTUtil.USER_ID_CLAIM;
import static Database.DeckDatabase.userHasAccessToDeck;

public class DeckBuilder {
    public static Handler createNew = ctx -> {
        String body = ctx.body();

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(body);
        JSONObject jsonObject = (JSONObject) obj;

        String deckName = (String) jsonObject.get("deck_name");

        DecodedJWT jwt = JavalinJWT.getDecodedFromContext(ctx);
        String userId = jwt.getClaim(USER_ID_CLAIM).asString();

        JSONArray cards = (JSONArray) jsonObject.get("card_ids");
        ArrayList<CardIdWithAmount> cardIds = getCardsFromJsonArray(cards);

        System.out.println("Saving new deck: ");
        System.out.println("Deck name: " + deckName);
        System.out.println("UserId: " + userId);
        System.out.println("Card Ids: " + cardIds);

        Deck deck = Deck.builder()
                .name(deckName)
                .userId(userId)
                .cardIds(cardIds)
                .build();

        int deck_id = DeckDatabase.create(deck);
        if (deck_id < 0) {
            ctx.status(400);
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("deck_id", deck_id);
        ctx.json(map);
    };

    private static ArrayList<CardIdWithAmount> getCardsFromJsonArray(JSONArray cards) {
        ArrayList<CardIdWithAmount> cardIds = new ArrayList<>();
        for (Object card : cards) {
            JSONObject innerCard = (JSONObject) card;
            int card_id = ((Long) innerCard.get("card_id")).intValue();
            int amount = ((Long) innerCard.get("amount")).intValue();
            CardIdWithAmount cardIdWithAmount = new CardIdWithAmount(card_id, amount);
            cardIds.add(cardIdWithAmount);
        }
        return cardIds;
    }

    public static Handler getDecksForUser = ctx -> {
        DecodedJWT jwt = JavalinJWT.getDecodedFromContext(ctx);
        String userId = jwt.getClaim(USER_ID_CLAIM).asString();
        List<Deck> decks = DeckDatabase.getAllForUser(userId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("decks", decks);
        ctx.json(map);
    };

    public static Handler update = ctx -> {
        String body = ctx.body();

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(body);
        JSONObject jsonObject = (JSONObject) obj;

        String deckIdParam = ctx.pathParam("id");
        if (Strings.isNullOrEmpty(deckIdParam)) {
            ctx.status(400);
            ctx.json(false);
            return;
        }

        int deckId = Integer.parseInt(deckIdParam);
        String deckName = (String) jsonObject.get("deck_name");

        DecodedJWT jwt = JavalinJWT.getDecodedFromContext(ctx);
        String userId = jwt.getClaim(USER_ID_CLAIM).asString();

        boolean userOwnsDeck = userHasAccessToDeck(deckId, userId);
        if (!userOwnsDeck) {
            ctx.status(403);
            ctx.json(false);
            return;
        }

        JSONArray cards = (JSONArray) jsonObject.get("card_ids");
        ArrayList<CardIdWithAmount> cardIds = getCardsFromJsonArray(cards);

        System.out.println("Updating deck: ");
        System.out.println("Deck Id: " + deckId);
        System.out.println("Deck name: " + deckName);
        System.out.println("Card Ids: " + cardIds);

        Deck deck = Deck.builder()
                .id(deckId)
                .name(deckName)
                .userId(userId)
                .cardIds(cardIds)
                .build();

        DeckDatabase.update(deck);
        ctx.json(true);
    };

    public static Handler delete = ctx -> {
        String deckIdParam = ctx.pathParam("id");
        if (Strings.isNullOrEmpty(deckIdParam)) {
            ctx.status(400);
            ctx.json(false);
            return;
        }
        int deckId = Integer.parseInt(deckIdParam);

        DecodedJWT jwt = JavalinJWT.getDecodedFromContext(ctx);
        String userId = jwt.getClaim(USER_ID_CLAIM).asString();
        boolean userOwnsDeck = userHasAccessToDeck(deckId, userId);
        if (!userOwnsDeck) {
            ctx.status(403);
            ctx.json(false);
            return;
        }

        System.out.println("Deleting deck with id: " + deckId);
        boolean deleted = DeckDatabase.delete(deckId);
        if (!deleted) {
            ctx.status(500);
            ctx.json(false);
            return;
        }
        ctx.json(true);
    };
}
