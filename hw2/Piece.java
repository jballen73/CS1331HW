/**
 *  Describes a chess piece with a color and declares abstract methods
 *
 *  @author jallen317
 * @version 1.0.0
 */
public abstract class Piece {

    private Color color;
    /**
    * Creates a piece with a specific color
    *
    * @param color the color of the piece
     */
    public Piece(Color color) {
        this.color = color;
    }
    /**
     * @return the color of the piece
     */
    public Color getColor() {
        return color;
    }
    /**
     * @return the algebraic name of the piece
     */
    public abstract String algebraicName();
    /**
     *   @return the FEN notation name of the piece
     */
    public abstract String fenName();
    /**
     *  @param square the current position of the piece
     *  @return a Square[] containing all Squares the piece can move to
     *  from it's current position
     */
    public abstract Square[] movesFrom(Square square);

}
