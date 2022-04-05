package chess.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import chess.domain.piece.Bishop;
import chess.domain.piece.Color;
import chess.domain.piece.EmptyPiece;
import chess.domain.piece.King;
import chess.domain.piece.Pawn;
import chess.domain.piece.Piece;
import chess.domain.piece.Queen;
import chess.domain.piece.Rook;
import chess.domain.piece.Symbol;
import chess.domain.piece.generator.NormalPiecesGenerator;
import chess.domain.piece.generator.PiecesGenerator;
import chess.domain.position.Position;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ChessBoardTest {

    @Test
    @DisplayName("체스판을 생성한다.")
    void construct() {
        final Map<Position, Piece> pieces = new HashMap<>(Map.ofEntries(
                Map.entry(Position.of("a1"), new Pawn(Color.WHITE)),
                Map.entry(Position.of("a2"), new Pawn(Color.BLACK))
        ));
        final ChessBoard chessBoard = new ChessBoard(() -> pieces);
        final Map<Position, Piece> actual = chessBoard.getPieces();

        assertThat(actual).containsAllEntriesOf(pieces);
    }

    @Test
    @DisplayName("체스판을 생성할 때 빈 칸은 EmptyPiece를 삽입한다.")
    void constructEmptyPieces() {
        final Map<Position, Piece> pieces = new HashMap<>(Map.ofEntries(
                Map.entry(Position.of("a1"), new Pawn(Color.WHITE)),
                Map.entry(Position.of("a2"), new Pawn(Color.BLACK))
        ));
        final ChessBoard chessBoard = new ChessBoard(() -> pieces);
        final Map<Position, Piece> actual = chessBoard.getPieces();

        assertThat(actual).contains(Map.entry(Position.of("a3"), EmptyPiece.getInstance()));
    }

    @Test
    @DisplayName("위치가 들어왔을 때 해당 위치의 말이 어떤 말인지 확인한다.")
    void selectPiece() {
        final PiecesGenerator piecesGenerator = new NormalPiecesGenerator();
        final ChessBoard chessBoard = new ChessBoard(piecesGenerator);
        final Piece piece = chessBoard.selectPiece(Position.of("a1"));

        assertThat(piece).isInstanceOf(Rook.class);
    }

    @Nested
    class Move {

        @Test
        @DisplayName("킹을 이동시킬 수 있다.")
        void king() {
            final Map<Position, Piece> pieces = new HashMap<>(Map.ofEntries(
                    Map.entry(Position.of("d4"), new King(Color.WHITE)),
                    Map.entry(Position.of("d3"), new Pawn(Color.WHITE)),
                    Map.entry(Position.of("d5"), new Pawn(Color.BLACK))
            ));

            final ChessBoard chessBoard = new ChessBoard(() -> pieces);
            chessBoard.move(Position.of("d4"), Position.of("d5"));

            final Piece fromPiece = chessBoard.selectPiece(Position.of("d4"));
            final Piece toPiece = chessBoard.selectPiece(Position.of("d5"));

            assertAll(
                    () -> assertThat(fromPiece.isSameSymbol(Symbol.EMPTY)).isTrue(),
                    () -> assertThat(toPiece.isSameSymbol(Symbol.KING)).isTrue()
            );
        }

        @Test
        @DisplayName("퀸을 이동시킬 수 있다.")
        void queen() {
            final Map<Position, Piece> pieces = new HashMap<>(Map.ofEntries(
                    Map.entry(Position.of("d4"), new Queen(Color.WHITE)),
                    Map.entry(Position.of("g4"), new Pawn(Color.WHITE)),
                    Map.entry(Position.of("d2"), new Pawn(Color.BLACK))
            ));

            final ChessBoard chessBoard = new ChessBoard(() -> pieces);
            chessBoard.move(Position.of("d4"), Position.of("d2"));

            final Piece fromPiece = chessBoard.selectPiece(Position.of("d4"));
            final Piece toPiece = chessBoard.selectPiece(Position.of("d2"));

            assertAll(
                    () -> assertThat(fromPiece.isSameSymbol(Symbol.EMPTY)).isTrue(),
                    () -> assertThat(toPiece.isSameSymbol(Symbol.QUEEN)).isTrue()
            );
        }

        @Test
        @DisplayName("룩을 이동시킬 수 있다.")
        void rook() {
            final Map<Position, Piece> pieces = new HashMap<>(Map.ofEntries(
                    Map.entry(Position.of("d4"), new Rook(Color.WHITE)),
                    Map.entry(Position.of("g4"), new Pawn(Color.WHITE)),
                    Map.entry(Position.of("d2"), new Pawn(Color.BLACK))
            ));

            final ChessBoard chessBoard = new ChessBoard(() -> pieces);
            chessBoard.move(Position.of("d4"), Position.of("d2"));

            final Piece fromPiece = chessBoard.selectPiece(Position.of("d4"));
            final Piece toPiece = chessBoard.selectPiece(Position.of("d2"));

            assertAll(
                    () -> assertThat(fromPiece.isSameSymbol(Symbol.EMPTY)).isTrue(),
                    () -> assertThat(toPiece.isSameSymbol(Symbol.ROOK)).isTrue()
            );
        }

        @Test
        @DisplayName("비숍을 이동시킬 수 있다.")
        void bishop() {
            Map<Position, Piece> pieces = new HashMap<>(Map.ofEntries(
                    Map.entry(Position.of("d4"), new Bishop(Color.WHITE)),
                    Map.entry(Position.of("g7"), new Pawn(Color.WHITE)),
                    Map.entry(Position.of("f2"), new Pawn(Color.BLACK))
            ));

            final ChessBoard chessBoard = new ChessBoard(() -> pieces);
            final GameCommand gameCommand = new GameCommand("move", "d4", "f2");

            chessBoard.move(gameCommand.getFromPosition(), gameCommand.getToPosition());

            final Piece fromPiece = chessBoard.selectPiece(gameCommand.getFromPosition());
            final Piece toPiece = chessBoard.selectPiece(gameCommand.getToPosition());

            assertAll(
                    () -> assertThat(fromPiece.isSameSymbol(Symbol.EMPTY)).isTrue(),
                    () -> assertThat(toPiece.isSameSymbol(Symbol.BISHOP)).isTrue()
            );
        }

        @ParameterizedTest
        @CsvSource(value = {"b1,c3", "b8,c6"})
        @DisplayName("나이트를 이동시킬 수 있다.")
        void knight(final String from, final String to) {
            final PiecesGenerator piecesGenerator = new NormalPiecesGenerator();
            final ChessBoard chessBoard = new ChessBoard(piecesGenerator);

            chessBoard.move(Position.of(from), Position.of(to));

            final Piece fromPiece = chessBoard.selectPiece(Position.of(from));
            final Piece toPiece = chessBoard.selectPiece(Position.of(to));

            assertAll(
                    () -> assertThat(fromPiece.isSameSymbol(Symbol.EMPTY)).isTrue(),
                    () -> assertThat(toPiece.isSameSymbol(Symbol.KNIGHT)).isTrue()
            );
        }

        @Test
        @DisplayName("폰을 이동시킬 수 있다.")
        void pawn() {
            final PiecesGenerator piecesGenerator = new NormalPiecesGenerator();
            final ChessBoard chessBoard = new ChessBoard(piecesGenerator);
            final GameCommand gameCommand = new GameCommand("move", "b2", "b4");

            chessBoard.move(gameCommand.getFromPosition(), gameCommand.getToPosition());
            final Piece fromPiece = chessBoard.selectPiece(gameCommand.getFromPosition());
            final Piece toPiece = chessBoard.selectPiece(gameCommand.getToPosition());

            assertAll(
                    () -> assertThat(fromPiece.isSameSymbol(Symbol.EMPTY)).isTrue(),
                    () -> assertThat(toPiece.isSameSymbol(Symbol.PAWN)).isTrue()
            );
        }

        @Test
        @DisplayName("이동할 수 없는 곳으로 이동명령을 내렸을 때 예외가 발생한다.")
        void pawnCannotMoveThrowException() {
            final PiecesGenerator piecesGenerator = new NormalPiecesGenerator();
            final ChessBoard chessBoard = new ChessBoard(piecesGenerator);
            final GameCommand gameCommand = new GameCommand("move", "b2", "c3");

            assertThatThrownBy(() -> chessBoard.move(gameCommand.getFromPosition(), gameCommand.getToPosition()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("폰은 해당 위치로 이동할 수 없습니다.");
        }
    }

    @Test
    @DisplayName("킹이 1개일 때, 게임은 끝난다.")
    void isEndTrue() {
        final Map<Position, Piece> testPieces = new HashMap<>(Map.ofEntries(
                Map.entry(Position.of("a1"), new King(Color.WHITE)),
                Map.entry(Position.of("b3"), new Pawn(Color.WHITE)),
                Map.entry(Position.of("c4"), new Pawn(Color.WHITE)),
                Map.entry(Position.of("a4"), new Pawn(Color.BLACK)),
                Map.entry(Position.of("a7"), new Pawn(Color.BLACK)),
                Map.entry(Position.of("c5"), new Pawn(Color.BLACK)),
                Map.entry(Position.of("b8"), new Pawn(Color.BLACK))
        ));
        final ChessBoard chessBoard = new ChessBoard(() -> testPieces);
        final boolean actual = chessBoard.isEnd();

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("킹이 2개일 때, 게임은 끝나지 않는다.")
    void isEndFalse() {
        final Map<Position, Piece> testPieces = new HashMap<>(Map.ofEntries(
                Map.entry(Position.of("a1"), new King(Color.WHITE)),
                Map.entry(Position.of("b3"), new Pawn(Color.WHITE)),
                Map.entry(Position.of("c4"), new Pawn(Color.WHITE)),
                Map.entry(Position.of("a4"), new King(Color.BLACK)),
                Map.entry(Position.of("a7"), new Pawn(Color.BLACK)),
                Map.entry(Position.of("c5"), new Pawn(Color.BLACK)),
                Map.entry(Position.of("b8"), new Pawn(Color.BLACK))
        ));
        final ChessBoard chessBoard = new ChessBoard(() -> testPieces);
        final boolean actual = chessBoard.isEnd();

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("검은색 킹이 없으면, 승자는 흰색이다.")
    void getWinner() {
        final Map<Position, Piece> testPieces = new HashMap<>(Map.ofEntries(
                Map.entry(Position.of("a1"), new King(Color.WHITE)),
                Map.entry(Position.of("b3"), new Pawn(Color.WHITE)),
                Map.entry(Position.of("c4"), new Pawn(Color.WHITE)),
                Map.entry(Position.of("a7"), new Pawn(Color.BLACK)),
                Map.entry(Position.of("c5"), new Pawn(Color.BLACK)),
                Map.entry(Position.of("b8"), new Pawn(Color.BLACK))
        ));
        final ChessBoard chessBoard = new ChessBoard(() -> testPieces);
        final Color winner = chessBoard.getWinner();

        assertThat(winner).isEqualTo(Color.WHITE);
    }
}
