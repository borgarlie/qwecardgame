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
            List<Integer> useOnOwnCards) throws IOException {
        System.out.println("Using spell effect TAP_ALL_OPPONENT_CREATURES");
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
        // Tap all of opponent's creatures
        for (int i = 0; i < otherPlayerState.battlezone.size(); i++) {
            otherPlayerState.tapCreature(i);
        }
        // Send json to other player
        sendJsonMessageToOtherPlayer(currentPlayer, mainGameLoop, spellCard, useOnOpponentCards, useOnOwnCards);
    }

    @HandleSpellEffect(TEMP_CANT_BLOCK_X2)
    public static void handleTempCantBlockX2(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card spellCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError, IOException {
        if (useOnOwnCards.size() > 2) {
            throw new GameError(
                    GameError.ErrorCode.NOT_ALLOWED,
                    "Can not use spell effect " + TEMP_CANT_BLOCK_X2 + " on more than 2 creatures");
        }
        System.out.println("Using spell effect TEMP_CANT_BLOCK_X2");
        PlayerState currentPlayerState = mainGameLoop.getCurrentPlayerState(currentPlayer);
        useOnOwnCards.forEach(battleZonePosition -> {
            Card card = currentPlayerState.battlezone.get(battleZonePosition);
            card.setTemp_can_not_be_blocked(true);
        });
        // Send json to other player
        sendJsonMessageToOtherPlayer(currentPlayer, mainGameLoop, spellCard, useOnOpponentCards, useOnOwnCards);
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

    @HandleSpellEffect(TAP_ONE_OPPONENT_CREATURE)
    public static void handleTapOneOpponentCreature(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card spellCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError, IOException {
        if (useOnOpponentCards.size() > 1) {
            throw new GameError(
                    GameError.ErrorCode.NOT_ALLOWED,
                    "Can not use spell effect " + TAP_ONE_OPPONENT_CREATURE + " on more than 1 creature");
        }
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
        System.out.println("Using spell effect TAP_ONE_OPPONENT_CREATURE");
        // Tap one opponent creature
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
                .put(CARD, spellCard.toJson())
                .put(USE_ON_OPPONENT_CARDS, useOnOpponentCards)
                .put(USE_ON_OWN_CARDS, useOnOwnCards)
                .toString();
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
        otherPlayerState.session.getRemote().sendString(json);
    }

}
