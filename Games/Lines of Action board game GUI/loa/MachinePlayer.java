package loa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/** An automated Player.
 *  @author Lily Lin */
class MachinePlayer extends Player {

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
        _side = side;
    }

    @Override
    Move makeMove() {
        Board replica = new Board();
        replica.copyFrom(this.getBoard());
        Move thismove = bestMove(_side, replica);
        if (thismove != null) {
            char abbr = (_side == Piece.BP) ? 'B' : 'W';
            System.out.println(abbr + "::" + thismove.toString());
        }
        return thismove;
    }

    /** Returns the best move for SIDE on BOARD. */
    Move bestMove(Piece side, Board board) {
        Iterator<Move> iter = board.iterator();
        ArrayList<Move> legalMoves = new ArrayList<Move>();
        while (iter.hasNext()) {
            Move cur = iter.next();
            board.makeMove(cur);
            if (forcedWin(board, side)) {
                board.retract();
                return cur;
            } else {
                board.retract();
                legalMoves.add(cur);
            }
        }
        if (legalMoves.size() == 0) {
            return null;
        } else {
            while (true) {
                return legalMoves.get(new Random().nextInt(legalMoves.size()));
            }
        }
    }

    /** Returns true iff. the situation guarantees a win for SIDE on B,
     *  aka no matter how the opponent moves SIDE can win. */
    boolean forcedWin(Board b, Piece side) {
        if (b.piecesContiguous(side)) {
            return true;
        } else {
            Iterator<Move> iter = b.iterator();
            while (iter.hasNext()) {
                Move moveOp = iter.next();
                b.makeMove(moveOp);
                if (!b.piecesContiguous(side)) {
                    b.retract();
                    return false;
                }
                b.retract();
            }
            return true;
        }
    }

    /** This player's side. */
    private final Piece _side;

}
