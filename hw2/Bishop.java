/**
 * Represents a specific chess piece, the bishop
 *
 * @author jallen317
 * @version 1.0.0
*/
public class Bishop extends Piece {
    /**
     *  Creates a Bishop with the given color
     *
     *  @param color the color of the piece
     */
    public Bishop(Color color) {
        super(color);
    }
    /**
     *  @return the algebraic name of the piece, which is B for a bishop
     */
    public String algebraicName() {
        return "B";
    }
    /**
     *   @return the name of the piece in FEN notation, which is lowercase for
     * black and uppercase for white
    */
    public String fenName() {
        return (getColor() == Color.BLACK) ? "b" : "B";
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
        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                for (int dist = 1; dist <= 7; dist++) {
                    newFile = (char) ((int) file + (i * dist));
                    newRank = (char) ((int) rank + (j * dist));
                    if (Square.isValid(newFile, newRank)) {
                        moves = ArrayTools.addTo(moves, new Square(newFile,
                                                                   newRank));
                    }
                }
            }
        }
        return moves;
    }

}
