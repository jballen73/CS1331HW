/**
 * This exception occurs when parameters for a Square class are
 * provided that do not represent an actual square on a chessboard.
 * This exception is unchecked because other helper methods can be used
 * to ensure that this exception never occurs.  The only time this
 * exception would occur is if bad data is fed in from a file or Piece
 * methods attempt to create Squares that do not exist.  This exception is also
 * very similar to other runtime exceptions such as ArrayIndexOutOfBounds and
 * InputMismatchException.
 * @author jallen317
 * @version 1.0.0
 */


public class InvalidSquareException extends RuntimeException {
    /**
     * Creates an InvalidSquareException with a given message using the
     * super constructor.
     * @param message the message associated with the Exception
     */
    public InvalidSquareException(String message) {
        super(message);
    }
}
