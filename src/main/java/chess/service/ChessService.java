package chess.service;

import chess.dao.BoardDao;
import chess.dao.GameDao;
import chess.domain.chessboard.ChessBoard;
import chess.domain.command.GameCommand;
import chess.domain.game.ChessGame;
import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.generator.NormalPiecesGenerator;
import chess.domain.position.Position;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChessService {

    private final BoardService boardService;
    private final GameService gameService;
    private final ChessGame chessGame;

    public ChessService() {
        this.boardService = new BoardService(new BoardDao());
        this.gameService = new GameService(new GameDao());
        this.chessGame = initChessGame();
    }

    private ChessGame initChessGame() {
        final Map<Position, Piece> board = boardService.loadBoard();
        if (board.isEmpty()) {
            return new ChessGame(new NormalPiecesGenerator());
        }
        return new ChessGame(gameService.loadState(), new ChessBoard(board));
    }

    public Map<String, Object> ready() {
        final Map<String, Object> model = new HashMap<>();
        model.putAll(boardService.findBoard());

        if (chessGame.isEndGameByPiece()) {
            model.put("winner", chessGame.getWinner() + " 승리!!");
            gameService.delete();
            chessGame.init(new NormalPiecesGenerator());
            return model;
        }
        return model;
    }

    public Map<String, Object> start() {
        final Map<String, Object> model = new HashMap<>();
        chessGame.playGameByCommand(new GameCommand("start"));
        save();
        model.putAll(boardService.findBoard());
        return model;
    }

    private void save() {
        gameService.saveGame(chessGame);
        boardService.saveBoard(chessGame, gameService.findGameId());
    }

    public void move(final String from, final String to) {
        chessGame.playGameByCommand(
                new GameCommand("move", from.toLowerCase(Locale.ROOT), to.toLowerCase(Locale.ROOT)));
        update(from, to);
    }

    private void update(final String from, final String to) {
        gameService.update(chessGame);
        boardService.update(chessGame, from);
        boardService.update(chessGame, to);
    }

    public Map<String, Object> status() {
        final Map<String, Object> model = new HashMap<>();
        chessGame.playGameByCommand(new GameCommand("status"));
        model.putAll(boardService.findBoard());

        final Map<Color, Double> scores = chessGame.calculateScore();
        model.put("white", "WHITE : " + scores.get(Color.WHITE));
        model.put("black", "BLACK : " + scores.get(Color.BLACK));
        return model;
    }

    public void end() {
        chessGame.playGameByCommand(new GameCommand("end"));
        gameService.delete();
        chessGame.init(new NormalPiecesGenerator());
    }

    public Map<String, Object> error(final String errorMessage) {
        final Map<String, Object> model = new HashMap<>();
        model.putAll(boardService.findBoard());
        model.put("error", errorMessage);
        return model;
    }
}
