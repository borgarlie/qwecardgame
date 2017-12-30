package GameLogic;

import org.json.JSONObject;

public class GameError extends Exception {

    public enum ErrorCode {
        INIT_ERROR, NOT_ENOUGH_MANA, WRONG_CARD_TYPE, NOT_ALLOWED,
        NOT_ENOUGH_CARDS, SUMMONING_SICKNESS, ALREADY_TAPPED
    }

    ErrorCode error;
    String message;

    public GameError(ErrorCode error) {
        this.error = error;
        this.message = "No Message provided";
    }

    public GameError(ErrorCode error, String message) {
        this.error = error;
        this.message = message;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("error_code", this.error)
                .put("message", this.message);
    }
}
