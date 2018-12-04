package amazons;
import org.junit.Test;
import static org.junit.Assert.*;
import ucb.junit.textui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** Junit tests for our Board iterators.
 *  @author Mandi Zhao
 */
public class IteratorTests {

    /** Run the JUnit tests in this package. */
    public static void main(String[] ignored) {
        textui.runClasses(IteratorTests.class);
    }

    /** Tests reachableFromIterator to make sure it returns all reachable
     *  Squares. This method may need to be changed based on
     *   your implementation. */
    @Test
    public void testReachableFrom() {
        Board b = new Board();
        buildBoard(b, REACHABLE_FROM_BOARD);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 4), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(REACHABLETEST.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(REACHABLETEST.size(), numSquares);
        assertEquals(REACHABLETEST.size(), squares.size());
    }

    @Test
    public void testReachableFrom2() {
        Board b = new Board();
        b.put(Piece.BLACK, Square.sq(4, 0));
        b.put(Piece.BLACK, Square.sq(4, 1));
        b.put(Piece.BLACK, Square.sq(3, 4));
        System.out.println(b);
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom =
                b.reachableFrom(Square.sq(3, 0),
                        Square.sq(3, 4));
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            System.out.println(s);
            squares.add(s);
        }
        assertEquals(13, squares.size());
    }

    @Test
    public void testReachableFrom3() {
        Board b = new Board();
        buildBoard(b, LEGALMOVES_BOARD);
        b.put(Piece.WHITE, Square.sq("j6"));
        b.put(Piece.WHITE, Square.sq("e6"));
        System.out.println(b);
        Iterator<Square> toTest =
                b.reachableFrom(Square.sq("d6"), Square.sq("e6"));
        while (toTest.hasNext()) {
            System.out.println(toTest.next());
        }
    }



    /** Tests legalMovesIterator to make sure it returns all legal Moves.
     *  This method needs to be finished and may need to be changed
     *  based on your implementation. */
    @Test
    public void testLegalMoves() {
        Board b = new Board();
        buildBoard(b, LEGALMOVES_BOARD);
        int numMoves = 0;
        Set<Move> moves = new HashSet<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            System.out.println(m);
            assertTrue(EXPECTED_LEGALMOVES.contains(m));
            numMoves += 1;
            moves.add(m);
        }
        assertEquals(9, numMoves);
        assertEquals(9, moves.size());
    }

    @Test
    public void testLegalMoves2() {
        Board b = new Board();
        System.out.println(b);
        Iterator<Move> legalMoves = b.legalMoves(Piece.BLACK);
        int count = 0;
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            count += 1;
        }
        System.out.println(count);
        assertEquals(count, 2176);
    }

    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - row - 1][col];
                b.put(piece, Square.sq(col, row));
            }
        }
        System.out.println(b);
    }

    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;

    static final Piece[][] REACHABLE_FROM_BOARD = {
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, W, W },
            { E, E, E, E, E, E, E, S, E, S },
            { E, E, E, S, S, S, S, E, E, S },
            { E, E, E, S, E, E, E, E, B, E },
            { E, E, E, S, E, W, E, E, B, E },
            { E, E, E, S, S, S, B, W, B, E },
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, E, E },
            { E, E, E, E, E, E, E, E, E, E },
    };

    static final Set<Square> REACHABLETEST =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 5),
                    Square.sq(4, 5),
                    Square.sq(4, 4),
                    Square.sq(6, 4),
                    Square.sq(7, 4),
                    Square.sq(6, 5),
                    Square.sq(7, 6),
                    Square.sq(8, 7)));
    static final Piece[][] LEGALMOVES_BOARD = {
            { B, B, B, B, B, B, B, B, B, B },
            { B, B, B, B, B, B, B, B, B, B },
            { B, B, B, B, B, B, B, B, B, B },
            { B, B, B, B, B, B, B, B, B, B },
            { E, E, E, W, B, E, E, E, E, E },
            { B, B, B, B, B, B, B, B, B, B },
            { B, B, B, B, B, B, B, B, B, B },
            { B, B, B, B, B, B, B, B, B, B },
            { B, B, B, B, B, B, B, B, B, B },
            { B, B, B, B, B, B, B, B, B, B },
    };

    static final Set<Move> EXPECTED_LEGALMOVES =
            new HashSet<>(Arrays.asList(
                    Move.mv(Square.sq(3, 5), Square.sq(2, 5), Square.sq(1, 5)),
                    Move.mv(Square.sq(3, 5), Square.sq(2, 5), Square.sq(3, 5)),
                    Move.mv(Square.sq(3, 5), Square.sq(2, 5), Square.sq(0, 5)),
                    Move.mv(Square.sq(3, 5), Square.sq(1, 5), Square.sq(2, 5)),
                    Move.mv(Square.sq(3, 5), Square.sq(1, 5), Square.sq(3, 5)),
                    Move.mv(Square.sq(3, 5), Square.sq(1, 5), Square.sq(0, 5)),
                    Move.mv(Square.sq(3, 5), Square.sq(0, 5), Square.sq(1, 5)),
                    Move.mv(Square.sq(3, 5), Square.sq(0, 5), Square.sq(2, 5)),
                    Move.mv(Square.sq(3, 5), Square.sq(0, 5), Square.sq(3, 5))
            ));
}
