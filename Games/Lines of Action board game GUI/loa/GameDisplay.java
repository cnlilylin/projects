package loa;

import ucb.gui.Pad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.imageio.ImageIO;

import java.io.InputStream;
import java.io.IOException;

/* DataDisplay is the view part of the Model-View-Controller pattern.
 * It displays information present in the model (SampleData). */

/* A Pad is kind of Widget (something displayable in a TopLevel).  It
 * has a number of methods for intercepting mouse and keyboard events
 * that happen within it. Calling the method
 * <tt>setMouseHandler(T, OBJ, NAME)</tt> on a Pad arranges that
 * whenever a mouse event of type T happens within the Pad, the method
 * named NAME is called on object OBJ.  T is a String that identifies
 * a type of event (mouse pressed, mouse released, mouse clicked,
 * mouse moved, or mouse dragged).
 *
 * Repainting does not happen immediately; a separate thread (a kind
 * of mini-program that runs independently) executes .paintComponent on
 * whatever components need it in an orderly, serial fashion.  It
 * creates a special kind of object (a subtype of java.awt.Graphics)
 * that defines numerous methods for drawing on bitmapped displays.
 * The same thread executes the specified action routines for mouse
 * and keyboard events.
 */


/** A widget that displays a set of points and connecting line segments.
 *  @author P. N. Hilfinger
 */
public class GameDisplay extends Pad {

    /** Name resource file that denotes points. */
    private static final String BOARD_IMAGE_NAME = "board.jpg";

    /** Image to use for points. */
    private static final Image BOARD_IMAGE = getImage(BOARD_IMAGE_NAME);

    /** Width of the board. */
    private static final int BOARD_WIDTH = 600;
    /** Height of the board. */
    private static final int BOARD_HEIGHT = 600;

    /** Diameter of the Pieces. */
    private static final int PIECE_DIAM = 40;
    /** Gap between the Pieces. */
    static final int GAP = 66;
    /** Initial coordinated of piece at (1,1). */
    static final int INITIAL = 50;

    /** Distance from point accepted as designating the point. */
    static final double MOUSE_TOLERANCE = PIECE_DIAM;

    /** A graphical representation of DATA on a field of size WIDTH x HEIGHT
     *  pixels for GAME. */
    public GameDisplay(GUIgame game) {
        _game = game;
        setPreferredSize(BOARD_WIDTH, BOARD_HEIGHT);
    }

    /** Returns the row value of (X,Y). */
    int findRow(int x, int y) {
        return (y - INITIAL) / GAP + 1;
    }

    /** Returns the col value of (X,Y). */
    int findCol(int x, int y) {
        return (x - INITIAL) / GAP + 1;
    }

    /** Returns the piece value of (X,Y). */
    Piece findPiece(int x, int y) {
        return _game.getBoard().get(findCol(x, y),
            findRow(x, y));
    }

    /** Paint piece SIDE at (C, R) on G, assuming SIDE is
    /*  either black or white. */
    void paintPiece(Graphics2D g, Piece side, int c, int r) {
        g.drawOval(INITIAL + GAP * (c - 1), INITIAL
            + GAP * (r - 1), PIECE_DIAM, PIECE_DIAM);
        if (side == Piece.BP) {
            g.setColor(Color.black);
        } else {
            g.setColor(Color.white);
        }
        g.fillOval(INITIAL + GAP * (c - 1), INITIAL
            + GAP * (r - 1), PIECE_DIAM, PIECE_DIAM);

    }

    /** Return an Image read from the resource named NAME. */
    private static Image getImage(String name) {
        InputStream in =
            GameDisplay.class.getResourceAsStream("/GUIresources/" + name);
        try {
            return ImageIO.read(in);
        } catch (IOException excp) {
            return null;
        }
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        g.drawImage(BOARD_IMAGE, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        paintBoard(g);
    }

    /** Paints the board on G. */
    public void paintBoard(Graphics2D g) {
        Board gameboard = _game.getBoard();
        for (int c = 1; c < Board.M + 1; c++) {
            for (int r = 1; r < Board.M + 1; r++) {
                Piece side = gameboard.get(c, r);
                if (side != Piece.EMP) {
                    paintPiece(g, side, c, r);
                }
            }
        }
    }

    /** Game I am playing. */
    private final GUIgame _game;
}
