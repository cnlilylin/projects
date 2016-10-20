package loa;

import ucb.junit.textui;
import org.junit.Test;

import static loa.Piece.BP;
import static loa.Piece.EMP;
import static loa.Piece.WP;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the loa package.
 *  @author Lily
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void placeholderTest() {
    }

    /** Tests initialization. */
    @Test
    public void initializationTest() {
        Board newBoard = new Board();
        assertEquals(newBoard.get(1, 2), newBoard.get(1, 3));
    }

    /** Tests copyFrom and get methods. */
    @Test
    public void copyFromAndGetTest() {
        Piece[][] copy = {
            { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
            { WP,  EMP, WP, EMP, WP, EMP, WP, WP  },
            { WP,  EMP, EMP, WP, EMP, WP, EMP, WP  },
            { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
            { WP,  EMP, EMP, BP, EMP, EMP, EMP, WP  },
            { WP,  EMP, BP, EMP, EMP, EMP, EMP, WP  },
            { WP,  BP, EMP, EMP, EMP, EMP, EMP, WP  },
            { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
        };
        Board curGame = new Board();
        Board copyBoard = new Board(copy, WP);
        curGame.copyFrom(copyBoard);
        for (int i = 1; i <= Board.M; i++) {
            for (int j = 1; j <= Board.M; j++) {
                assertEquals(curGame.get(i, j), copyBoard.get(i, j));
            }
        }
    }

    @Test
    public void congruityTest() {
        Piece[][] tp1 = {
            { EMP, BP,  BP,  EMP,  BP,  EMP,  EMP,  EMP },
            { WP,  EMP, BP, EMP, BP, EMP, EMP, WP  },
            { WP,  BP, EMP, BP, EMP, EMP, EMP, WP  },
            { EMP,  BP, WP, EMP, EMP, EMP, WP, WP  },
            { BP,  EMP, EMP, WP, EMP, EMP, EMP, BP  },
            { BP,  EMP, EMP, EMP, WP, EMP, EMP, BP  },
            { BP,  EMP, EMP, EMP, EMP, EMP, BP, WP  },
            { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
        };
        Board tb1 = new Board(tp1, BP);
        assertEquals(true, tb1.piecesContiguous(BP));
        assertEquals(false, tb1.piecesContiguous(WP));
        Piece[][] initial = {
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
                { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
                { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
                { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
                { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
                { EMP,  BP, EMP, EMP, EMP, EMP, EMP, EMP  },
                { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
                { EMP, EMP,  BP,  BP,  BP,  BP,  BP,  EMP }
            };
        Board tb2 = new Board(initial, BP);
        assertEquals(false, tb2.piecesContiguous(BP));
    }

    @Test
    public void legalMovesTest() {
        Piece[][] initial = {
            { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
            { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
            { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
            { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
            { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
            { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
            { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
            { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
        };
        Board tb1 = new Board(initial, BP);
        assertEquals(false, tb1.piecesContiguous(BP));
    }

    @Test
    public void findForcedWin() {
        Piece[][] easyWin = {
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
                { BP,  EMP, EMP, EMP, WP, EMP, EMP, EMP  },
                { WP,  EMP, EMP, BP, EMP, WP, EMP, WP  },
                { WP,  EMP, EMP, BP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, BP, EMP, EMP, EMP, WP  },
                { WP,  EMP, BP, EMP, EMP, EMP, EMP, WP  },
                { WP,  BP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
            };
        Board easy1 = new Board(easyWin, BP);
        MachinePlayer machine = new MachinePlayer(BP, null);
        easy1.makeMove(machine.bestMove(BP, easy1));
        assertEquals(true, easy1.piecesContiguous(BP));
        Piece[][] whiteHasMoves =  {
                { WP, BP,  WP,  WP,  BP,  WP,  BP,  WP },
                { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
                { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
                { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
                { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
                { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, EMP  },
                { EMP,  EMP, EMP, EMP, EMP, EMP, EMP, BP  },
                { EMP, EMP,  EMP,  EMP,  EMP,  EMP,  EMP,  BP }
            };
        Board easy2 = new Board(whiteHasMoves, WP);
        MachinePlayer machine2 = new MachinePlayer(WP, null);
        assertEquals(true, easy2.isLegalMove());
    }

}


