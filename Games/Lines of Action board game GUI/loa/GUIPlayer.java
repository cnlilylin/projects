package loa;

import java.util.concurrent.ArrayBlockingQueue;

/** A type of player that gets input from the mouse, and reports
 *  game positions and reports errors on a GUI.
 *  @author Lily Lin
 */
class GUIPlayer extends Player {

    /** A GUIPlayer playing SIDE that makes moves on GAME. */
    GUIPlayer(Piece side, GUIgame game) {
        super(side, game);
    }

    @Override
    Move makeMove() {
    	
    }

}
