/**
 * Represents a specific chess piece, the pawn
 *
 * @author jallen317
 * @version 1.0.0
*/
public class Pawn extends Piece {


    /**
     *  Creates a Pawn with the given color
     *
     *  @param color the color of the piece
     */
    public Pawn(Color color) {
        super(color);
    }
    /**
     *  @return the algebraic name of the piece, which is nothing for a pawn
     */
    public String algebraicName() {
        return "";
    }
    /**
     *  @return the name of the piece in FEN notation, which is lowercase for
     *  black and uppercase for white
    */
    public String fenName() {
        return (getColor() == Color.BLACK) ? "p" : "P";
    }
    /**
     *  @param square the square the piece is on
     *
     *  @return all Squares the piece can move to from square in a Square[]
    */
    public Square[] movesFrom(Square square) {
        Square[] moves = new Square[0];
        char file = square.toString().charAt(0);
        char rank = square.toString().charAt(1);
        char newRank;
        if (getColor() == Color.BLACK) {
            if (rank == '7') {
                moves = ArrayTools.addTo(moves, new Square(file, '5'));
            }
            newRank = (char) ((int) rank - 1);
            if (Square.isValid(file, newRank)) {
                moves = ArrayTools.addTo(moves, new Square(file, newRank));
            }
        } else {
            if (rank == '2') {
                moves = ArrayTools.addTo(moves, new Square(file, '4'));
            }
            newRank = (char) ((int) rank + 1);
            if (Square.isValid(file, newRank)) {
                moves = ArrayTools.addTo(moves, new Square(file, newRank));
            }
        }
        return moves;
    }

}
