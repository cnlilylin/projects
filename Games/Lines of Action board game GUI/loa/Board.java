package loa;

import static loa.Direction.NOWHERE;
import static loa.Piece.BP;
import static loa.Piece.EMP;
import static loa.Piece.WP;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/** Represents the state of a game of Lines of Action.
 *  @author Lily Lin
 */
class Board implements Iterable<Move> {

    /** Size of a board. */
    static final int M = 8;

    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /** A Board whose initial contents are taken from INITIALCONTENTS
     *  and in which the player playing TURN is to move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row-1][col-1]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is MxM.
     *
     *  CAUTION: The natural written notation for arrays initializers puts
     *  the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {
        clear();
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        copyFrom(board);
    }

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        _moves.clear();
        pieces = new Piece[contents.length][contents[0].length];
        for (int r = 1; r <= M; r += 1) {
            for (int c = 1; c <= M; c += 1) {
                set(c, r, contents[r - 1][c - 1]);
            }
        }
        _turn = side;
    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        if (board == this) {
            return;
        }
        _moves.clear();
        _moves.addAll(board._moves);
        _turn = board._turn;
        for (int r = 1; r <= M; r += 1) {
            for (int c = 1; c <= M; c += 1) {
                set(c, r, board.get(c, r));
            }
        }
    }

    /** Return the contents of column C, row R, where 1 <= C,R <= 8,
     *  where column 1 corresponds to column 'a' in the standard
     *  notation. */
    Piece get(int c, int r) {
        if (!isLegalLocation(c, r)) {
            return null;
        }
        return this.pieces[r - 1][c - 1];

    }

    /** Return the contents of the square SQ.  SQ must be the
     *  standard printed designation of a square (having the form cr,
     *  where c is a letter from a-h and r is a digit from 1-8). */
    Piece get(String sq) {
        return get(col(sq), row(sq));
    }

    /** Return the column number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int col(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(0) - 'a' + 1;
    }

    /** Return the row number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int row(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(1) - '0';
    }

    /** Set the square at column C, row R to V, and make NEXT the next side
     *  to move, if it is not null. */
    void set(int c, int r, Piece v, Piece next) {
        this.pieces[r - 1][c - 1] = v;
        if (next != null) {
            _turn = next;
        }
    }

    /** Set the square at column C, row R to V. */
    void set(int c, int r, Piece v) {
        set(c, r, v, null);
    }

    /** Assuming isLegal(MOVE), make MOVE. */
    void makeMove(Move move) {
        assert isLegal(move);
        _moves.add(move);
        Piece replaced = move.replacedPiece();
        int c0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        if (replaced != EMP) {
            set(c1, r1, EMP);
        }
        set(c1, r1, move.movedPiece());
        set(c0, r0, EMP);
        _turn = _turn.opposite();
    }

    /** Retract (unmake) one move, returning to the state immediately before
     *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        Move move = _moves.remove(_moves.size() - 1);
        Piece replaced = move.replacedPiece();
        int c0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        Piece movedPiece = move.movedPiece();
        set(c1, r1, replaced);
        set(c0, r0, movedPiece);
        _turn = _turn.opposite();
    }

    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Return true iff MOVE is legal for the player currently on move. */
    boolean isLegal(Move move) {
        if (move == null || blocked(move)) {
            return false;
        }
        return move.length() == pieceCountAlong(move);
    }

    /** Return a sequence of all legal moves from this position.
     * Should only be returned by iterator(). */
    Iterator<Move> legalMoves() {
        return new MoveIterator();
    }

    @Override
    public Iterator<Move> iterator() {
        return legalMoves();
    }

    /** Return true if there is at least one legal move for the player
     *  on move. */
    public boolean isLegalMove() {
        return iterator().hasNext();
    }

    /** Return true iff either player has all his pieces continguous. */
    boolean gameOver() {
        boolean a = piecesContiguous(BP);
        boolean b = piecesContiguous(WP);
        return a || b;
    }

    /** Return true iff SIDE's pieces are continguous. */
    boolean piecesContiguous(Piece side) {
        boolean[][] checkmap = new boolean[M + 1][M + 1];
        int startingC = findFirstOccurence(side)[0];
        int startingR = findFirstOccurence(side)[1];
        findConnection(checkmap, startingC, startingR, side);
        for (int i = 1; i <= M; i++) {
            for (int j = 1; j <= M; j++) {
                if (get(i, j) == side && !checkmap[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Returns the location of the first occurence of SIDE,
     *  assuming there has a to at least be one. */
    int[] findFirstOccurence(Piece side) {
        for (int i = 1; i <= M; i++) {
            for (int j = 1; j <= M; j++) {
                if (get(i, j) == side) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    /** Marks CHECKED so that all SIDE on C, R is true. */
    void findConnection(boolean[][] checked, int c, int r, Piece side) {
        if (!isLegalLocation(c, r) || get(c, r) != side) {
            return;
        } else if (!checked[c][r]) {
            checked[c][r] = true;
            Direction d = NOWHERE;
            while (d.succ() != null) {
                d = d.succ();
                int c1 = next(c, r, d)[0];
                int r1 = next(c, r, d)[1];
                findConnection(checked, c1, r1, side);
            }
        }
    }

    /** Return true iff location with current R and C is legal. */
    boolean isLegalLocation(int c, int r) {
        return 1 <= c && c <= M && 1 <= r && r <= M;
    }

    /** Return true iff SIDE  on COL and ROW has a contiguous piece. */
    boolean hasContiguousPiece(Piece side, int col, int row) {
        Direction dir = NOWHERE;
        while (dir.succ() != null) {
            dir = dir.succ();
            if (isLegalLocation(dir.dc + col, dir.dr + row)) {
                if (get(dir.dc + col, dir.dr + row) == side) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        int sum = 0;
        int var;
        for (int c = 1; c < M + 1; c++) {
            for (int r = 1; r < M + 1; r++) {
                if (this.get(c, r) == EMP) {
                    var = 0;
                } else if (this.get(c, r) == BP) {
                    var = 1;
                } else {
                    var = 2;
                }
                sum += c * 1000 + r * 10 + var;
            }
        }
        return sum;
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = M; r >= 1; r -= 1) {
            out.format("    ");
            for (int c = 1; c <= M; c += 1) {
                out.format("%s ", get(c, r).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Returns the board representation with column index. */
    public String toBoard() {
        Formatter out = new Formatter();
        for (int r = M; r >= 1; r -= 1) {
            out.format("%s ", r);
            for (int c = 1; c <= M; c += 1) {
                out.format("%s ", get(c, r).abbrev());
            }
            out.format("%n");
        }
        out.format(" ");
        int i = 1;
        char cur = 'a';
        while (i <= M) {
            out.format(" %s", cur);
            cur++;
            i++;
        }
        out.format("%nNext move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Return the number of pieces in the line of action indicated by MOVE. */
    private int pieceCountAlong(Move move) {
        return pieceCountAlong(move.getCol0(), move.getRow0(), move.moveDir());
    }

    /** Return the number of pieces in the line of action in direction DIR and
     *  containing the square at column C0 and row R0. */
    private int pieceCountAlong(int c0, int r0, Direction dir) {
        int sum = 0;
        int c = c0, r = r0;
        while (isLegalLocation(c, r)) {
            Piece cur = get(c, r);
            if (cur != EMP) {
                sum++;
            }
            c += dir.dc;
            r += dir.dr;
        }
        c = c0;
        r = r0;
        while (isLegalLocation(c - dir.dc, r - dir.dr)) {
            c -= dir.dc;
            r -= dir.dr;
            if (get(c, r) == _turn || get(c, r) == _turn.opposite()) {
                sum++;
            }
        }
        return sum;
    }

    /** Return true iff MOVE is blocked by an opposing piece or by a
     *  friendly piece on the target square. */
    private boolean blocked(Move move) {
        int c = move.getCol0();
        int r = move.getRow0();
        int k = 0;
        Piece cur = move.movedPiece();
        while (cur != null && k < move.length()) {
            if (cur != EMP && cur != _turn) {
                return true;
            }
            c = next(c, r, move.moveDir())[0];
            r = next(c, r, move.moveDir())[1];
            k++;
            cur = get(c, r);
        }
        if (cur == _turn) {
            return true;
        }
        return false;
    }

    /** Return the coordinate of next piece in DIR direction
     *  and coordinates (C,R). */
    private int[] next(int c, int r, Direction dir) {
        return new int[]{c + dir.dc, r + dir.dr};
    }

    /** The standard initial configuration for Lines of Action. */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };

    /** List of all unretracted moves on this board, in order. */
    private final ArrayList<Move> _moves = new ArrayList<>();
    /** Current side on move. */
    private Piece _turn;
    /** Current configuration of the Pieces. */
    private Piece[][] pieces;

    /** An iterator returning the legal moves from the current board. */
    private class MoveIterator implements Iterator<Move> {
        /** Current piece under consideration. */
        private int _c, _r;
        /** Next direction of current piece to return. */
        private Direction _dir;
        /** Next move. */
        private Move _move;

        /** A new move iterator for turn(). */
        MoveIterator() {
            _c = 1; _r = 1; _dir = NOWHERE;
            incr();
        }

        @Override
        public boolean hasNext() {
            return _move != null;
        }

        @Override
        public Move next() {
            if (_move == null) {
                throw new NoSuchElementException("no legal move");
            }
            Move move = _move;
            incr();
            return move;
        }

        @Override
        public void remove() {
        }

        /** Advance to the next legal move. */
        private void incr() {
            while (_c <= Board.M) {
                while (_r <= Board.M) {
                    if (get(_c, _r) != _turn) {
                        _r++;
                    } else {
                        while (_dir.succ() != null) {
                            _dir = _dir.succ();
                            int steps = pieceCountAlong(_c, _r, _dir);
                            Move move = Move.create(_c, _r,
                                    steps, _dir, Board.this);
                            if (isLegal(move)) {
                                _move = move;
                                return;
                            }
                        }
                        _r++;
                        _dir = NOWHERE;
                    }
                }
                _c++;
                _r = 1;
            }
            _move = null;
            return;
        }

    }
}
