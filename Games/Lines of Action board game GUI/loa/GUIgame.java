package loa;

import static loa.Piece.BP;
import static loa.Piece.WP;

import java.util.concurrent.ArrayBlockingQueue;

/** Sets up game that gets input from the mouse, and reports
 *  game positions and reports errors on a GUI.
 *  @author Lily Lin
 */
class GUIgame extends Game {

    /** A new series of Games. */
    GUIgame() {
        _players = new Player[2];
        _players[0] = new GUIPlayer(BP, this);
        _players[1] = new MachinePlayer(WP, this);
        _playing = false;
        _GUImovequeue = new ArrayBlockingQueue<Move>(1);
    }

    /** Return the current board. */
    Board getBoard() {
        return _board;
    }

    /** Starts current game. */
    void start() {
        _board.clear();
        _playing = true;
    }

    /** Returns true IFF _playing is true. */
    boolean playing() {
        return _playing;
    }
    
    void addToQueue(Move m) {
    	try {
			_GUImovequeue.put(m);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /** The official game board. */
    private Board _board;

    /** The _players of this game. */
    private Player[] _players = new Player[2];

    /** True if actually playing (game started and not stopped or finished).
     */
    private boolean _playing;
    
    private ArrayBlockingQueue<Move> _GUImovequeue;
}
