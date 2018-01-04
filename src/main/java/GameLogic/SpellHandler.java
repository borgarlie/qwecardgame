package GameLogic;


import Pojos.Card;
import Pojos.SpellEffect;
import Utils.HandleSpellEffect;
import Utils.ReflectionUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static Pojos.SpellEffect.*;

public class SpellHandler {

    private static final String TYPE = "type";
    private static final String USE_SPELL = "use_spell";
    private static final String CARD = "card";
    private static final String USE_ON_OWN_CARDS = "use_on_own_cards"; // list
    private static final String USE_ON_OPPONENT_CARDS = "use_on_opponent_cards"; // list

    // map of all spell effect handlers
    private static Map<SpellEffect, Method> spellHandlers
            = ReflectionUtil.findSpellEffectMethods(SpellHandler.class, HandleSpellEffect.class);

    public static void handleSpell(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card spellCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError, IOException {
        SpellEffect spellEffect = spellCard.getSpellEffect();
        System.out.println(spellEffect);
        System.out.println(spellHandlers);
        try {
            spellHandlers.get(spellEffect)
                    .invoke(null, currentPlayer, mainGameLoop, spellCard, useOnOpponentCards, useOnOwnCards);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @HandleSpellEffect(NONE)
    public static void handleNoneSpell(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card spellCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError {
        System.out.println("'None' spell used");
        throw new GameError(GameError.ErrorCode.BAD_STATE, "All spells should have an effect");
    }

    @HandleSpellEffect(TAP_ALL_OPPONENT_CREATURES)
    public static void handleTapAllOpponentCreatures(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card spellCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) {
        System.out.println("Using spell effect TAP_ALL_OPPONENT_CREATURES");
        PlayerState currentPlayerState = mainGameLoop.getCurrentPlayerState(currentPlayer);
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
    }

    @HandleSpellEffect(TEMP_CANT_BLOCK_X2)
    public static void handleTempCantBlockX2(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card spellCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) {
        System.out.println("Using spell effect TEMP_CANT_BLOCK_X2");
        PlayerState currentPlayerState = mainGameLoop.getCurrentPlayerState(currentPlayer);
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
    }

    @HandleSpellEffect(TAP_TWO_OPPONENT_CREATURES)
    public static void handleTapTwoOpponentCreatures(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card spellCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError, IOException {
        if (useOnOpponentCards.size() > 2) {
            throw new GameError(
                    GameError.ErrorCode.NOT_ALLOWED,
                    "Can not use spell effect " + TAP_TWO_OPPONENT_CREATURES + " on more than 2 creatures");
        }
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
        System.out.println("Using spell effect TAP_TWO_OPPONENT_CREATURES");
        // Tap two of opponent's creatures
        useOnOpponentCards.forEach(otherPlayerState::tapCreature);
        // Send json to other player
        sendJsonMessageToOtherPlayer(currentPlayer, mainGameLoop, spellCard, useOnOpponentCards, useOnOwnCards);
    }

    // Only send after making sure the spell effect is triggered
    private static void sendJsonMessageToOtherPlayer(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card spellCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws IOException {
        String json = new JSONObject()
                .put(TYPE, USE_SPELL)
                .put(CARD, spellCard)
                .put(USE_ON_OPPONENT_CARDS, useOnOpponentCards)
                .put(USE_ON_OWN_CARDS, useOnOwnCards)
                .toString();
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
        otherPlayerState.session.getRemote().sendString(json);
    }

}
