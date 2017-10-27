/**
 *  Represents a square on a chessboard with a rank and file
 *
 *  @author jallen317
 * @version 1.0.0
*/

public class Square {

    private char rank;
    private char file;
    private String name;
    /**
     *  Creates a square with required parameters
     * @param file the file the square is on
     * @param rank the rank the square is on
    */
    public Square(char file, char rank) {
        if (!isValid(file, rank)) {
            throw new InvalidSquareException(file + "" + rank);
        } else {
            this.rank = rank;
            this.file = file;
            name = file + "" + rank;
        }
    }
    /**
     * Creates a square with a single parameter
     * @param name the combined file and rank of the square i.e. "a1"
     */
    public Square(String name) {
        this(name.charAt(0), name.charAt(1));
        if (name.length() > 2) {
            throw new InvalidSquareException(name);
        }
    }
    /**
     * @return the name of the square i.e. "a1"
     */
    public String toString() {
        return name;
    }
    /**
     *  Overrides Object.equals method
     *  @param o the object to be compared to
     * @return whether o is a Square with the same name
     */
    public boolean equals(Object o) {
        if (o != null && (o == this || o instanceof Square))  {
            Square newSquare = (Square) o;
            return (newSquare.toString()).equals(name);
        } else {
            return false;
        }
    }
    /**
     * @return the file character
     */
    public char getFile() {
        return file;
    }
    /**
     * @return the rank character
     */
    public char getRank() {
        return rank;
    }
    /**
     *   Overrides Object.hashCode method
     * @return the concatenated integer representation of the file and rank
     */
    public int hashCode() {
        return Integer.parseInt((int) file + "" + (int) rank);
    }
    /**
     *  Determines if params make a valid square name
     * @param file file char to be checked if valid
     * @param rank rank char to be checked if valid
     * @return if file and rank are both in valid range
     */
    public static boolean isValid(char file, char rank) {
        return !(file < 'a' || file > 'h' || rank < '1' || rank > '8');
    }
}
