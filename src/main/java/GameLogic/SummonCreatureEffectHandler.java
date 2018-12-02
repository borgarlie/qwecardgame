package GameLogic;

import Pojos.Card;
import Pojos.SummonCreatureEffect;
import Utils.HandleSummonCreatureEffect;
import Utils.ReflectionUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static Pojos.SummonCreatureEffect.*;

public class SummonCreatureEffectHandler {

    private static final String TYPE = "type";
    private static final String DRAW_CARD = "draw_card";
    private static final String DRAWN_CARD = "drawn_card";

    // map of all summon creature effect handlers
    private static Map<SummonCreatureEffect, Method> summonCreatureEffectHandlers
            = ReflectionUtil.findSummonCreatureEffectMethods(
                    SummonCreatureEffectHandler.class, HandleSummonCreatureEffect.class);

    public static void handleEffect(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card summonedCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError, IOException {
        SummonCreatureEffect effect = summonedCard.getSummonCreatureEffect();
        try {
            summonCreatureEffectHandlers.get(effect)
                    .invoke(null, currentPlayer, mainGameLoop, summonedCard, useOnOpponentCards, useOnOwnCards);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @HandleSummonCreatureEffect(NONE)
    public static void handleNoneEffect(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card summonedCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) {
        System.out.println("'NONE' effect on summoned creature.");
    }

    @HandleSummonCreatureEffect(RETURN_TWO_CARDS_TO_HAND)
    public static void handleReturnTwoOpponentCardsToHand(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card summonedCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError {
        int totalSize = useOnOpponentCards.size() + useOnOwnCards.size();
        if (totalSize > 2) {
            throw new GameError(
                    GameError.ErrorCode.NOT_ALLOWED,
                    "Can not use summon creature effect " + RETURN_TWO_CARDS_TO_HAND + " on more than 2 creatures");
        }
        System.out.println("'RETURN_TWO_CARDS_TO_HAND' effect on summoned creature.");
        PlayerState currentPlayerState = mainGameLoop.getCurrentPlayerState(currentPlayer);
        for (int battlezonePosition : useOnOwnCards) {
            currentPlayerState.returnCardFromBattlezoneToHand(battlezonePosition);
        }
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
        for (int battlezonePosition : useOnOpponentCards) {
            otherPlayerState.returnCardFromBattlezoneToHand(battlezonePosition);
        }
    }

    @HandleSummonCreatureEffect(DESTROY_ALL_BLOCKERS)
    public static void handleDestroyAllBlockers(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card summonedCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError {
        System.out.println("'DESTROY_ALL_BLOCKERS' effect on summoned creature.");
        PlayerState currentPlayerState = mainGameLoop.getCurrentPlayerState(currentPlayer);
        for (int i = 0; i < currentPlayerState.battlezone.size(); i++) {
            Card card = currentPlayerState.getCardInBattleZonePosition(i);
            if (card.isBlocker()) {
                currentPlayerState.killCardInBattlezone(i);
            }
        }
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
        for (int i = 0; i < otherPlayerState.battlezone.size(); i++) {
            Card card = otherPlayerState.getCardInBattleZonePosition(i);
            if (card.isBlocker()) {
                otherPlayerState.killCardInBattlezone(i);
            }
        }
    }

    @HandleSummonCreatureEffect(TAP_ONE_CREATURE)
    public static void handleTapOneCreature(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card summonedCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError {
        if (useOnOpponentCards.size() > 1) {
            throw new GameError(
                    GameError.ErrorCode.NOT_ALLOWED,
                    "Can not use summon creature effect " + TAP_ONE_CREATURE + " on more than 1 creature");
        }
        System.out.println("'TAP_ONE_CREATURE' effect on summoned creature.");
        PlayerState otherPlayerState = mainGameLoop.getOtherPlayerState(currentPlayer);
        // Tap one of opponent's creatures
        useOnOpponentCards.forEach(otherPlayerState::tapCreature);
    }

    @HandleSummonCreatureEffect(DRAW_CARD_EFFECT)
    public static void handleDrawCard(
            MainGameLoop.Player currentPlayer,
            MainGameLoop mainGameLoop,
            Card summonedCard,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError, IOException {
        System.out.println("'DRAW_CARD_EFFECT' effect on summoned creature.");
        PlayerState currentPlayerState = mainGameLoop.getCurrentPlayerState(currentPlayer);
        Card drawnCard = currentPlayerState.drawCard();
        String json = new JSONObject()
                .put(TYPE, DRAW_CARD)
                .put(DRAWN_CARD, drawnCard.toJson())
                .toString();
        currentPlayerState.session.getRemote().sendString(json);
    }
}
