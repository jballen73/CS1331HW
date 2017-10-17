/**
 *  Contains a static method useful due to the prohitibition of the Arrays class
 *
 * @author jallen317
 * @version 1.0.0
 */
public class ArrayTools {
    /**
     * @param orig the original array to be extended
     * @param addOn the square to be appended to the end
     *
     * @return orig with addOn appended to the end
     */
    public static Square[] addTo(Square[] orig, Square addOn) {
        Square[] newArray = new Square[orig.length + 1];
        for (int i = 0; i < orig.length; i++) {
            newArray[i] = new Square(orig[i].toString());
        }
        newArray[orig.length] = new Square(addOn.toString());
        return newArray;
    }
}
