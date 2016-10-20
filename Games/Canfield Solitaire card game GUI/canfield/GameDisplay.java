package canfield;

import ucb.gui.Pad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.imageio.ImageIO;

import java.io.InputStream;
import java.io.IOException;

/** A widget that displays a Pinball playfield.
 *  @author P. N. Hilfinger
 */
class GameDisplay extends Pad {

    /** Color of display field. */
    private static final Color BACKGROUND_COLOR = Color.cyan;

    /* Coordinates and lengths in pixels unless otherwise stated. */

    /** Preferred dimensions of the playing surface. */
    private static final int BOARD_WIDTH = 600, BOARD_HEIGHT = 800;

    /** Preferred spacing between cards. */
    static final int HORIZONTAL_GAP = 5, VERTICAL_GAP = 20;

    /** Displayed dimensions of a card image. */
    static final int CARD_HEIGHT = 125, CARD_WIDTH = 90;

    /** Coordinates of Key Cards. */
    static final int FOUN1_X = 200, FOUN1_Y = 20,
            TAB1_X = FOUN1_X, TAB1_Y = 200,
            RES_X = HORIZONTAL_GAP, RES_Y = TAB1_Y,
            STOCK_X = RES_X, STOCK_Y = 350,
            WASTE_X = STOCK_X + 2 * HORIZONTAL_GAP + CARD_WIDTH,
            WASTE_Y = STOCK_Y;

    /** A graphical representation of GAME. */
    public GameDisplay(Game game) {
        _game = game;
        setPreferredSize(BOARD_WIDTH, BOARD_HEIGHT);
    }

    /** Return an Image read from the resource named NAME. */
    private Image getImage(String name) {
        InputStream in =
            getClass().getResourceAsStream("/canfield/resources/" + name);
        try {
            return ImageIO.read(in);
        } catch (IOException excp) {
            return null;
        }
    }

    /** Return an Image of CARD. */
    private Image getCardImage(Card card) {
        return getImage("playing-cards/" + card + ".png");
    }

    /** Return an Image of the back of a card. */
    private Image getBackImage() {
        return getImage("playing-cards/blue-back.png");
    }

    /** Draw CARD at X, Y on G. */
    private void paintCard(Graphics2D g, Card card, int x, int y) {
        if (card != null) {
            g.drawImage(getCardImage(card), x, y,
                        CARD_WIDTH, CARD_HEIGHT, null);
        }
    }

    /** Draw card back at X, Y on G. */
    private void paintBack(Graphics2D g, int x, int y) {
        g.drawImage(getBackImage(), x, y, CARD_WIDTH, CARD_HEIGHT, null);
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        g.setColor(BACKGROUND_COLOR);
        Rectangle b = g.getClipBounds();
        g.fillRect(0, 0, b.width, b.height);
        paintReserve(g);
        paintStock(g);
        paintWaste(g);
        for (int i = 1; i < Card.NUM_SUITS + 1; i += 1) {
            paintFoundation(g, i);
        }
        for (int i = 1; i < Game.TABLEAU_SIZE + 1; i += 1) {
            paintTableau(g, i);
        }
    }

    /** Draw tableau of index K on G. */
    private void paintTableau(Graphics2D g, int k) {
        if (_game.topTableau(k) == null) {
            return;
        }
        int x = 0, y = TAB1_Y;
        if (k == 1) {
            x = TAB1_X;
        } else if (k == 2) {
            x = TAB1_X + CARD_WIDTH + HORIZONTAL_GAP;
        } else if (k == 3) {
            x = TAB1_X + (CARD_WIDTH + HORIZONTAL_GAP) * 2;
        } else if (k == 4) {
            x = TAB1_X + (CARD_WIDTH + HORIZONTAL_GAP) * 3;
        }
        for (int c = _game.tableauSize(k) - 1; c >= 0; c--) {
            paintCard(g, _game.getTableau(k, c), x, y);
            y += VERTICAL_GAP;
        }
    }

    /** Draw reserve pile on G. */
    private void paintReserve(Graphics2D g) {
        if (_game.topReserve() == null) {
            paintBack(g, RES_X, RES_Y);
        } else {
            paintCard(g, _game.topReserve(), RES_X, RES_Y);
        }
    }

    /** Draw stock pile on G. */
    private void paintStock(Graphics2D g) {
        paintBack(g, STOCK_X, STOCK_Y);
    }

    /** Draw waste pile on G. */
    private void paintWaste(Graphics2D g) {
        if (_game.topWaste() == null) {
            return;
        } else {
            paintCard(g, _game.topWaste(), WASTE_X, WASTE_Y);
        }
    }

    /** Draw foundation pile of index K on G. */
    private void paintFoundation(Graphics2D g, int k) {
        int x = 0, y = FOUN1_Y;
        if (k == 1) {
            x = FOUN1_X;
        } else if (k == 2) {
            x = FOUN1_X + CARD_WIDTH + HORIZONTAL_GAP;
        } else if (k == 3) {
            x = FOUN1_X + (CARD_WIDTH + HORIZONTAL_GAP) * 2;
        } else if (k == 4) {
            x = FOUN1_X + (CARD_WIDTH + HORIZONTAL_GAP) * 3;
        }
        if (_game.topFoundation(k) != null) {
            paintCard(g, _game.topFoundation(k), x, y);
        }
    }

    /** Game I am displaying. */
    private final Game _game;

}
