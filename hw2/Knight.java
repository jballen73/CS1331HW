/**
 *  Represents a specific chess piece, the knight
 *
 * @author jallen317
 * @version 1.0.0
*/
public class Knight extends Piece {
    /**
     *  Creates a Knight with the given color
     *
     * @param color the color of the piece
     */
    public Knight(Color color) {
        super(color);
    }
    /**
     * @return the algebraic name of the piece, which is N for a knight
     */
    public String algebraicName() {
        return "N";
    }
    /**
     *  @return the name of the piece in FEN notation, which is lowercase for
     * black and uppercase for white
     */
    public String fenName() {
        return (getColor() == Color.BLACK) ? "n" : "N";
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
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (Math.abs(i) != Math.abs(j) && i != 0 && j != 0) {
                    newFile = (char) ((int) file + i);
                    newRank = (char) ((int) rank + j);
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
