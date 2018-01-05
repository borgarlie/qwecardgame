package GameLogic;

import Pojos.Card;
import Pojos.DestroyCreatureEffect;
import Utils.HandleDestroyCreatureEffect;
import Utils.ReflectionUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static Pojos.DestroyCreatureEffect.NONE;
import static Pojos.DestroyCreatureEffect.RETURN_TO_HAND;

public class DestroyCreatureEffectHandler {

    // map of all summon creature effect handlers
    private static Map<DestroyCreatureEffect, Method> destroyCreatureEffectHandlers
            = ReflectionUtil.findDestroyCreatureEffectMethods(
                    DestroyCreatureEffectHandler.class, HandleDestroyCreatureEffect.class);

    /*
        Assuming that the client can handle the effect without getting any information from the server
        Reason: The client has knowledge about the cards that are destroyed and can check for effect and handle it
     */
    public static void handleEffect(PlayerState playerState, int battlezonePosition) throws GameError, IOException {
        Card destroyedCard = playerState.getCardInBattleZonePosition(battlezonePosition);
        DestroyCreatureEffect effect = destroyedCard.getDestroyCreatureEffect();
        try {
            destroyCreatureEffectHandlers.get(effect).invoke(null, playerState, battlezonePosition);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /*
        No effect: Send to graveyard
     */
    @HandleDestroyCreatureEffect(NONE)
    public static void handleNoneEffect(PlayerState playerState, int battlezonePosition) throws GameError {
        System.out.println("'NONE' effect on destroyed creature.");
        playerState.killCardInBattlezone(battlezonePosition);
    }

    /*
        RETURN_TO_HAND ("When this creature would be destroyed, return it to your hand instead.");
     */
    @HandleDestroyCreatureEffect(RETURN_TO_HAND)
    public static void handleReturnToHand(PlayerState playerState, int battlezonePosition) throws GameError {
        System.out.println("'RETURN_TO_HAND' effect on destroyed creature.");
        Card card = playerState.getCardInBattleZonePosition(battlezonePosition);
        card.setTapped(false); // Card should be untapped when added to hand
        playerState.battlezone.remove(battlezonePosition);
        playerState.hand.add(card);
    }

}
