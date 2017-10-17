/**
 *Represents a specific chess piece, the queen
 *
 *@author jallen317
 *@version 1.0.0
*/
public class Queen extends Piece {
    /**
     * Creates a Queen with the given color
     *
     * @param color the color of the piece
     */
    public Queen(Color color) {
        super(color);
    }
    /**
     * @return the algebraic name of the piece, which is Q for a queen
     */
    public String algebraicName() {
        return "Q";
    }
    /**
     * @return the name of the piece in FEN notation, which is lowercase for
     * black and uppercase for white
     */
    public String fenName() {
        return (getColor() == Color.BLACK) ? "q" : "Q";
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
                if (!(i == 0 && j == 0)) {
                    for (int dist = 1; dist <= 7; dist++) {
                        newFile = (char) ((int) file + (i * dist));
                        newRank = (char) ((int) rank + (j * dist));
                        if (Square.isValid(newFile, newRank)) {
                            moves = ArrayTools.addTo(moves,
                                                      new Square(newFile,
                                                                 newRank));
                        }
                    }

                }
            }
        }
        return moves;
    }
}
