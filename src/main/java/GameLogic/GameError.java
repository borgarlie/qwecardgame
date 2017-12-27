package GameLogic;

public class GameError extends Exception {

    public enum ErrorCode {
        INIT_ERROR
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
}
