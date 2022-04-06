package chess.domain.state;

import chess.domain.chessboard.ChessBoard;
import chess.domain.command.GameCommand;

public interface State {

    State proceed(final ChessBoard chessBoard, final GameCommand gameCommand);

    static State of(String value) {
        if (value.equals("blackrunning")) {
            return new BlackRunning();
        }
        if (value.equals("whiterunning")) {
            return new WhiteRunning();
        }
        throw new IllegalArgumentException();
    }

    boolean isFinished();
}
