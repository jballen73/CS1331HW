/**
 *Represents a specific chess piece, the king
 *
 * @author jallen317
 * @version 1.0.0
*/
public class King extends Piece {
    /**
     * Creates a King with the given color
     *
     *  @param color the color of the piece
     */
    public King(Color color) {
        super(color);
    }
    /**
     *  @return the algebraic name of the piece, which is K for a king
    */
    public String algebraicName() {
        return "K";
    }
    /**
     *   @return the name of the piece in FEN notation, which is lowercase for
     * black and uppercase for white
    */
    public String fenName() {
        return (getColor() == Color.BLACK) ? "k" : "K";
    }
    /**
     * @param square the square the piece is on
     *
     * @return all Squares the piece can move to from square in a Square[]
    */
    public Square[] movesFrom(Square square) {
        Square[] moves = new Square[0];
        char file = square.toString().charAt(0);
        char rank = square.toString().charAt(1);
        char newFile;
        char newRank;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                newFile = (char) ((int) file + i);
                newRank = (char) ((int) rank + j);
                if (!(i == 0 && j == 0) && Square.isValid(newFile, newRank)) {
                    moves = ArrayTools.addTo(moves, new Square(newFile,
                                                               newRank));
                }
            }
        }
        return moves;
    }


}
