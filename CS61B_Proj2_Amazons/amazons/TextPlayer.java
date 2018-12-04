package amazons;

import static amazons.Move.MOVE_PATTERN;
import static amazons.Move.mv;

/** A Player that takes input as text commands from the standard input.
 *  @author Mandi Zhao
 */
class TextPlayer extends Player {

    /** A new TextPlayer with no piece or controller (intended to produce
     *  a template). */
    TextPlayer() {
        this(null, null);
    }

    /** A new TextPlayer playing PIECE under control of CONTROLLER. */
    private TextPlayer(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new TextPlayer(piece, controller);
    }

    @Override
    String myMove() {
        while (true) {
            String line = _controller.readLine();
            if (line == null) {
                return "quit";
            } else if (MOVE_PATTERN.matcher(line).matches()
                    && !board().isLegal(mv(line))) {
                _controller.reportError("Invalid move. "
                                        + "Please try again.");
                continue;
            } else {
                return line;
            }
        }
    }
}
