package canfield;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;

import java.awt.event.MouseEvent;

/** A top-level GUI for Canfield solitaire.
 *  @author Lily
 */
class CanfieldGUI extends TopLevel {

    /** A new window with given TITLE and displaying GAME. */
    CanfieldGUI(String title, Game game) {
        super(title, true);
        _game = game;
        addButton("Quit", "quit", new LayoutSpec("y", 0, "x", 1));
        addButton("Undo", "undo", new LayoutSpec("y", 0, "x", 0));
        _display = new GameDisplay(game);
        add(_display, new LayoutSpec("y", 2, "width", 2));
        _display.setMouseHandler("click", this, "mouseClicked");
        display(true);
    }

    /** Respond to "Quit" button. */
    public void quit(String dummy) {
        System.exit(1);
    }

    /** Respond to "Undo" button. */
    public void undo(String dummy) {
        _game.undo();
        _display.repaint();
    }

    /**Returns the type of pile selected using the coordinates X, Y. */
    private String typePile(int x, int y)  {
        if (x > GameDisplay.STOCK_X && x < GameDisplay.STOCK_X
                + GameDisplay.CARD_WIDTH) {
            if (y > GameDisplay.RES_Y && y < GameDisplay.RES_Y
                + GameDisplay.CARD_HEIGHT) {
                return "RESERVE";
            } else if (y > GameDisplay.STOCK_Y && y < GameDisplay.STOCK_Y
                    + GameDisplay.CARD_HEIGHT) {
                return "STOCK";
            }
        } else if (x > GameDisplay.WASTE_X && x < GameDisplay.WASTE_X
                    + GameDisplay.CARD_WIDTH && y > GameDisplay.WASTE_Y
                    && x < GameDisplay.WASTE_Y + GameDisplay.CARD_HEIGHT) {
            return "WASTE";
        } else if (x > GameDisplay.FOUN1_X && x < GameDisplay.FOUN1_X
                   + GameDisplay.CARD_WIDTH + (GameDisplay.CARD_WIDTH
                   + GameDisplay.HORIZONTAL_GAP) * 3) {
            if (y > GameDisplay.FOUN1_Y && y < GameDisplay.FOUN1_Y
                   + GameDisplay.CARD_HEIGHT) {
                return "FOUNDATION";
            } else if (y > GameDisplay.TAB1_Y) {
                return "TABLEAU";
            }
        }
        return null;
    }

    /**Returns the index of the pile selected using the X coordinate. */
    private int findPile(int x) {
        if (x < GameDisplay.FOUN1_X + GameDisplay.CARD_WIDTH) {
            return 1;
        } else {
            return 1 + findPile(x - GameDisplay.CARD_WIDTH
                                - GameDisplay.HORIZONTAL_GAP);
        }
    }

    /** Action in response to mouse-clicking event EVENT. */
    public synchronized void mouseClicked(MouseEvent event) {
        try {
            int x = event.getX(), y = event.getY();
            String pileType = typePile(x, y);
            switch (pileType) {
            case "RESERVE":
                _clickedCard = _game.topReserve();
                _typeClickedCard = "RESERVE";
                break;
            case "STOCK":
                _game.stockToWaste();
                break;
            case "WASTE":
                _clickedCard = _game.topWaste();
                _typeClickedCard = "WASTE";
                break;
            case "FOUNDATION":
                handleCaseFoundation(findPile(x));
                break;
            case "TABLEAU":
                handleCaseTableau(findPile(x));
                break;
            default:
            }
            _display.repaint();
        } catch (NullPointerException e) {
            _clickedCard = null;
        } catch (IllegalArgumentException e) {
            _clickedCard = null;
        }
    }

    /** Handles the case when the tableau with index TPILE is selected. */
    private void handleCaseTableau(int tPile) {
        if (_clickedCard == null) {
            _clickedCard = _game.topTableau(tPile);
            _typeClickedCard = "TABLEAU";
            _numPile = tPile;
        } else {
            switch (_typeClickedCard) {
            case "RESERVE":
                _game.reserveToTableau(tPile);
                break;
            case "WASTE":
                _game.wasteToTableau(tPile);
                break;
            case "TABLEAU":
                _game.tableauToTableau(_numPile, tPile);
                break;
            case "FOUNDATION":
                _game.foundationToTableau(_numPile, tPile);
                break;
            default:
                return;
            }
            _clickedCard = null;
        }
    }

    /** Handles the case when the foundation with index FPILE is selected. */
    private void handleCaseFoundation(int fPile) {
        if (_clickedCard == null) {
            _clickedCard = _game.topFoundation(fPile);
            _typeClickedCard = "FOUNDATION";
            _numPile = fPile;
        } else {
            switch (_typeClickedCard) {
            case "RESERVE":
                _game.reserveToFoundation();
                break;
            case "WASTE":
                _game.wasteToFoundation();
                break;
            case "TABLEAU":
                _game.tableauToFoundation(_numPile);
                break;
            default:
                return;
            }
        }
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

    /** The game I am consulting. */
    private final Game _game;

    /** Last point clicked on. */
    private Card _clickedCard;

    /** Type of last point clicked on. */
    private String _typeClickedCard;

    /** Type of last point clicked on. */
    private int _numPile;

}
