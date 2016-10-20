package loa;

/** A Player that prompts for moves and reads them from its Game.
 *  @author Lily*/
class HumanPlayer extends Player {

    /** A HumanPlayer that plays the SIDE pieces in GAME.  It uses
     *  GAME.getMove() as a source of moves.  */
    HumanPlayer(Piece side, Game game) {
        super(side, game);
        _game = game;
    }

    @Override
    Move makeMove() {
        return _game.getMove();
    }

    /** The game this player is part of. */
    private Game _game;
}
