package loa;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;

import java.awt.event.MouseEvent;

/** A top-level GUI for Line of Action.
 *  @author Lily Lin
 */
class LineOfActionGUI extends TopLevel {

    /** A new window with given TITLE and displaying GAME. */
    LineOfActionGUI(String title, GUIgame game) {
        super(title, true);
        _game = game;

        addButton("Start", "start", new LayoutSpec("y", 0, "x", 0));
        addButton("Quit", "quit", new LayoutSpec("y", 0, "x", 1));
        _display = new GameDisplay(_game);
        add(_display, new LayoutSpec("y", 2, "width", 2));
        _display.setMouseHandler("click", this, "mouseClicked");
        display(true);
    }

    /** Respond to "Start" button. */
    public void start(String dummy) {
        _game.start();
        _display.repaint();
    }

    /** Respond to "Quit" button. */
    public void quit(String dummy) {
        System.exit(1);
    }

    /** Action in response to mouse-clicking event EVENT. */
    public void mouseClicked(MouseEvent event) {
        if (!_game.playing()) {
            return;
        }
        int x = event.getX(), y = event.getY();
        Piece existing =
            _display.findPiece(x, y);
        if (existing == null) {
            _clickedPiece = null;
        } else if (_clickedPiece == null && existing == GUIPIECE) {
            _clickedPiece = existing;
            _clickedRow = _display.findRow(x, y);
            _clickedCol = _display.findCol(x, y);
        } else if (_clickedPiece != existing) {
            Move m = Move.create(_clickedCol, _clickedRow,
                    _display.findCol(x, y),
                    _display.findRow(x, y),
                    _game.getBoard());
            if (_game.getBoard().isLegal(m)) {
                _game.guiPlayer().addToQueue(m);
            }
            _clickedPiece = null;
        } else {
            _clickedPiece = null;
        }
        _display.repaint();
    }

    /** Action in response to mouse-released event EVENT. */
    public synchronized void mouseReleased(MouseEvent event) {
        return;
    }

    /** Action in response to mouse-dragging event EVENT. */
    public synchronized void mouseDragged(MouseEvent event) {
        return;
    }

    /** The board widget. */
    private final GameDisplay _display;

    /** Last point clicked on. */
    private Piece _clickedPiece;

    /** Last column clicked on. */
    private int _clickedCol;
    /** Last row clicked on. */
    private int _clickedRow;

    /** The official game board. */
    private GUIgame _game;
    /** The default side of GUIplayer. */
    private static final Piece GUIPIECE = Piece.BP;

}
