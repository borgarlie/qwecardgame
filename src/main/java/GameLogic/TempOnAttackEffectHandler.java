package GameLogic;

import Pojos.Card;
import Pojos.TempOnAttackEffect;
import Utils.HandleTempOnAttackEffect;
import Utils.ReflectionUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static Pojos.TempOnAttackEffect.NONE;
import static Pojos.TempOnAttackEffect.PLUS_POWER_ANGEL_COMMAND;

public class TempOnAttackEffectHandler {

    // map of all summon creature effect handlers
    private static Map<TempOnAttackEffect, Method> tempOnAttackEffectHandlers
            = ReflectionUtil.findTempOnAttackEffectMethods(
                    TempOnAttackEffectHandler.class, HandleTempOnAttackEffect.class);

    /*
        attackingCreatureInPosition = -1 means attacking player
     */
    public static void handleEffect(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            int battlezonePosition,
            int attackingCreatureInPosition)
            throws GameError, IOException {
        PlayerState currentPlayerState = mainGameLoop.getCurrentPlayerState(currentPlayer);
        // This can be the attacking card or it can be the blocking card during a blocker interaction
        Card card = currentPlayerState.getCardInBattleZonePosition(battlezonePosition);
        TempOnAttackEffect effect = card.getTempOnAttackEffect();
        try {
            tempOnAttackEffectHandlers.get(effect)
                    .invoke(null, currentPlayer, mainGameLoop, battlezonePosition, attackingCreatureInPosition);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @HandleTempOnAttackEffect(NONE)
    public static void handleNoneEffect(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            int battlezonePosition,
            int attackingCreatureInPosition) {
        System.out.println("'NONE' effect on attacking creature.");
    }

    @HandleTempOnAttackEffect(PLUS_POWER_ANGEL_COMMAND)
    public static void handlePlusPowerAngelCommand(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            int battlezonePosition,
            int attackingCreatureInPosition) {
        System.out.println("'PLUS_POWER_ANGEL_COMMAND' effect on attacking creature.");
        PlayerState currentPlayerState = mainGameLoop.getCurrentPlayerState(currentPlayer);
        String race = "Angel Command";
        if (currentPlayerState.hasCreatureRaceInBattlezone(race)) {
            currentPlayerState.battlezone.get(battlezonePosition).setTemp_on_attack_power(2000);
        }
    }
}
