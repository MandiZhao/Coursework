package amazons;

import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import static amazons.Piece.*;
import static amazons.Move.mv;


/** The state of an Amazons Game.
 *  @author Mandi Zhao
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        _board = new Piece[10][10];
        _turn = model.turn();
        _winner = model.winner();
        _moves = model.moves();
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = model.get(col, row);
                put(piece, Square.sq(col, row));
            }
        }
    }

    /** Clears the board to the initial position. */
    void init() {
        _turn = WHITE;
        _winner = EMPTY;
        _board = new Piece[10][10];
        _moves = new Stack<>();
        for (int i = 0; i < SIZE * SIZE; i++) {
            put(EMPTY, Square.sq(i));
        }
        put(WHITE, Square.sq("a4"));
        put(WHITE, Square.sq("d1"));
        put(WHITE, Square.sq("g1"));
        put(WHITE, Square.sq("j4"));
        put(BLACK, Square.sq("a7"));
        put(BLACK, Square.sq("d10"));
        put(BLACK, Square.sq("g10"));
        put(BLACK, Square.sq("j7"));
    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return _moves.size();
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        if (!legalMoves().hasNext()) {
            if (_turn == BLACK) {
                _winner = WHITE;
            } else {
                _winner = BLACK;
            }
            return _winner;
        } else {
            return null;
        }

    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return _board[s.row()][s.col()];
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return get(Square.sq(col, row));
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        _board[row][col] = p;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (from.isQueenMove(to)) {
            if ((get(to) != EMPTY) && (to != asEmpty)) {
                return false;
            }

            int dir = from.direction(to);
            int steps = 1;
            while (from.queenMove(dir, steps) != to) {
                Square next = from.queenMove(dir, steps);
                if ((get(next) != EMPTY) && (next != asEmpty)) {
                    return false;
                }
                steps++;
            }
            return true;
        }
        return false;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return Square.exists(from.col(), from.row())
                && get(from) == turn();
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        return isLegal(from) && isUnblockedMove(from, to, null);
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        return (isLegal(from, to)
                && isUnblockedMove(to, spear, from));
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        if (move == null) {
            return false;
        }
        return isLegal(move.from(), move.to(), move.spear());
    }

    /** Return my current stack of moves. */
    Stack<Move> moves() {
        return _moves;
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        put(EMPTY, from);
        put(_turn, to);
        put(SPEAR, spear);
        _moves.add(mv(from, to, spear));
        _turn = _turn.opponent();
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moves.size() == 0) {
            return;
        }
        Move toss = _moves.pop();
        Piece chess = get(toss.to());
        put(EMPTY, toss.spear());
        put(EMPTY, toss.to());
        put(chess, toss.from());
        if (_turn == WHITE) {
            _turn = BLACK;
        } else {
            _turn = WHITE;
        }
    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = 0;
            _steps = 0;
            _asEmpty = asEmpty;
        }

        @Override
        public boolean hasNext() {
            if (_dir >= 8) {
                return false;
            } else if (isUnblockedMove(_from,
                    _from.queenMove(_dir, _steps + 1), _asEmpty)) {
                _steps++;
                return true;
            } else {
                _dir++;
                _steps = 0;
                return hasNext();
            }
        }

        @Override
        public Square next() {
            return _from.queenMove(_dir, _steps);
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;

        }

        @Override
        public boolean hasNext() {
            if (_spearThrows.hasNext()) {
                return true;
            } else if (_pieceMoves.hasNext()) {
                _nextSquare = _pieceMoves.next();
                _spearThrows = reachableFrom(_nextSquare, _start);
                return hasNext();
            }
            while (_startingSquares.hasNext()) {
                _start = _startingSquares.next();
                if (get(_start) == _fromPiece) {
                    _pieceMoves = reachableFrom(_start, null);
                    return hasNext();
                }
            }
            return false;

        }

        @Override
        public Move next() {
            return Move.mv(_start, _nextSquare, _spearThrows.next());
        }


        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. Didn't use it just rot in hell thanks. */
        private void toNext() { }


        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
    }

    @Override
    public String toString() {
        String result = "";
        for (int row = 9; row > -1; row--) {
            String line = "   ";
            for (int col = 0; col < 9; col++) {
                line += _board[row][col].toString() + " ";
            }
            line += _board[row][9].toString() + "\n";
            result += line;
        }
        return result;
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;

    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;

    /** An 2D array representation of the board. */
    private Piece[][] _board;

    /** A stack to keep track of moves. */
    private Stack<Move> _moves;
}
