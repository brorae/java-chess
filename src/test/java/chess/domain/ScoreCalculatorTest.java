package chess.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import chess.domain.piece.Color;
import chess.domain.piece.Piece;
import chess.domain.piece.fixedmovablepiece.King;
import chess.domain.piece.pawn.BlackPawn;
import chess.domain.piece.pawn.WhitePawn;
import chess.domain.piece.straightmovablepiece.Bishop;
import chess.domain.piece.straightmovablepiece.Rook;
import chess.domain.position.Position;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ScoreCalculatorTest {

    @Test
    @DisplayName("여러 컬럼의 검은색 점수를 계산한다.")
    void calculateColumnsForBlack() {
        final Map<Position, Piece> testPieces = new HashMap<>(Map.ofEntries(
                Map.entry(Position.of("a1"), new King(Color.WHITE)),
                Map.entry(Position.of("a8"), new King(Color.BLACK)),
                Map.entry(Position.of("b3"), new WhitePawn()),
                Map.entry(Position.of("c4"), new WhitePawn()),
                Map.entry(Position.of("a4"), new BlackPawn()),
                Map.entry(Position.of("a7"), new BlackPawn()),
                Map.entry(Position.of("c5"), new BlackPawn()),
                Map.entry(Position.of("b8"), new BlackPawn())
        ));
        ChessBoard chessBoard = new ChessBoard(() -> testPieces);

        final ScoreCalculator calculator = new ScoreCalculator(chessBoard.getPieces());
        final double score = calculator.calculate(Color.BLACK);

        assertThat(score).isEqualTo(3);
    }

    @Test
    @DisplayName("여러 컬럼의 한약색 점수를 계산한다.")
    void calculateColumnsForWhite() {
        final Map<Position, Piece> testPieces = new HashMap<>(Map.ofEntries(
                Map.entry(Position.of("a1"), new King(Color.WHITE)),
                Map.entry(Position.of("a8"), new King(Color.BLACK)),
                Map.entry(Position.of("b3"), new WhitePawn()),
                Map.entry(Position.of("b4"), new WhitePawn()),
                Map.entry(Position.of("a4"), new Bishop(Color.WHITE)),
                Map.entry(Position.of("a7"), new Rook(Color.WHITE)),
                Map.entry(Position.of("c5"), new BlackPawn()),
                Map.entry(Position.of("b8"), new BlackPawn())
        ));
        ChessBoard chessBoard = new ChessBoard(() -> testPieces);

        final ScoreCalculator calculator = new ScoreCalculator(chessBoard.getPieces());
        final double score = calculator.calculate(Color.WHITE);

        assertThat(score).isEqualTo(9);
    }
}
