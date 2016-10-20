package canfield;

import static org.junit.Assert.*;
import org.junit.Test;

/** Tests of the Game class.
 *  @author
 */

public class GameTest {

    /** Example. */
    @Test
    public void testInitialScore() {
        Game g = new Game();
        g.deal();
        assertEquals(5, g.getScore());
    }

    @Test
    public void testUndo() {
        Game a = new Game();
        a.deal();
        Card foundation = a.topFoundation(1);
        Card tab1 = a.topTableau(1);
        Card tab2 = a.topTableau(2);
        Card waste0 = a.topWaste();
        Card reserve0 = a.topReserve();
        a.stockToWaste();
        Card waste1 = a.topWaste();
        Card reserve1 = a.topReserve();
        a.stockToWaste();
        Card waste2 = a.topWaste();
        Card reserve2 = a.topReserve();
        a.stockToWaste();
        a.undo();
        assertEquals(waste2, a.topWaste());
        assertEquals(reserve2, a.topReserve());
        a.undo();
        assertEquals(waste1, a.topWaste());
        assertEquals(reserve1, a.topReserve());
        a.undo();
        assertEquals(waste0, a.topWaste());
        assertEquals(reserve0, a.topReserve());
        a.undo();
        assertEquals(waste0, a.topWaste());
        assertEquals(reserve0, a.topReserve());
        assertEquals(foundation, a.topFoundation(1));
        assertEquals(tab1, a.topTableau(1));
        assertEquals(tab2, a.topTableau(2));
    }

}
