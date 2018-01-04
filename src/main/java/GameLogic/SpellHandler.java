package GameLogic;


import Pojos.Card;
import Pojos.SpellEffect;
import Utils.HandleSpellEffect;
import Utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static Pojos.SpellEffect.*;

public class SpellHandler {

    // map of all spell effect handlers
    private static Map<SpellEffect, Method> spellHandlers
            = ReflectionUtil.findSpellEffectMethods(SpellHandler.class, HandleSpellEffect.class);

    public static void handleSpell(MainGameLoop.Player currentPlayer, MainGameLoop mainGameLoop, Card spellCard) {
        SpellEffect spellEffect = spellCard.getSpellEffect();
        System.out.println(spellEffect);
        System.out.println(spellHandlers);
        try {
            spellHandlers.get(spellEffect).invoke(null, currentPlayer, mainGameLoop, spellCard);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @HandleSpellEffect(NONE)
    public static void handleNoneSpell(MainGameLoop.Player currentPlayer, MainGameLoop mainGameLoop, Card spellCard)
            throws GameError {
        System.out.println("'None' spell used");
        throw new GameError(GameError.ErrorCode.BAD_STATE, "All spells should have an effect");
    }

    @HandleSpellEffect(TAP_ALL_OPPONENT_CREATURES)
    public static void handleTapAllOpponentCreatures(
            MainGameLoop.Player currentPlayer, MainGameLoop mainGameLoop, Card spellCard) {
        System.out.println("Using spell effect TAP_ALL_OPPONENT_CREATURES");
        PlayerState currentPlayerState = mainGameLoop.getCurrentPlayerState(currentPlayer);
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
    }

    @HandleSpellEffect(TEMP_CANT_BLOCK_X2)
    public static void handleTempCantBlockX2(
            MainGameLoop.Player currentPlayer, MainGameLoop mainGameLoop, Card spellCard) {
        System.out.println("Using spell effect TEMP_CANT_BLOCK_X2");
        PlayerState currentPlayerState = mainGameLoop.getCurrentPlayerState(currentPlayer);
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
    }

    @HandleSpellEffect(TAP_TWO_OPPONENT_CREATURES)
    public static void handleTapTwoOpponentCreatures(
            MainGameLoop.Player currentPlayer, MainGameLoop mainGameLoop, Card spellCard) {
        System.out.println("Using spell effect TAP_TWO_OPPONENT_CREATURES");
        PlayerState currentPlayerState = mainGameLoop.getCurrentPlayerState(currentPlayer);
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
    }

}
