import java.io.File;
import java.util.Scanner;
public class PgnReader {
    private static char[][] chessboard = new char[8][8];
    private static char origPiece;
    private static int[] origPos = new int[2];
    private static char newPiece;
    private static int[] newPos = new int[2];
    private static int[] enPassantWPosition = new int[2];
    private static int[] enPassantWStartPos1 = new int[2];
    private static int[] enPassantWStartPos2 = new int[2];
    private static int[] enPassantBPosition = new int[2];
    private static int[] enPassantBStartPos1 = new int[2];
    private static int[] enPassantBStartPos2 = new int[2];
    private static boolean enPassantWhite = false;
    private static boolean enPassantBlack = false;

    //Returns the value of the tag corresponding to tagName
    public static String tagValue(String tagName, String file) {
        if (file.indexOf(tagName) == -1) {
            return "NOT FOUND";
        }
        String cutString = file.substring(file.indexOf(tagName));
        String result;
        int endIndex = cutString.indexOf("]");
        result = cutString.substring(tagName.length() + 2, endIndex - 1);
        return result;
    }
    public static void main(String[] args) throws Exception {
        Scanner fileReader = new Scanner(new File(args[0]));
        String fileString = "";
        while (fileReader.hasNext()) {
            fileString += fileReader.nextLine() + " ";
        }
        fileString = fileString.replace("\n", "");
        System.out.println("Event: " + tagValue("Event", fileString));
        System.out.println("Site: " + tagValue("Site", fileString));
        System.out.println("Date: " + tagValue("Date", fileString));
        System.out.println("Round: " + tagValue("Round", fileString));
        System.out.println("White: " + tagValue("White", fileString));
        System.out.println("Black: " + tagValue("Black", fileString));
        System.out.println("Result: " + tagValue("Result", fileString));
        fillBoard();
        System.out.println(finalPosition(fileString));


    }
    //Returns a String with the FEN final position of game denoted by fileString
    public static String finalPosition(String fileString) {
        String allMoves = removeTag(fileString);
        allMoves = allMoves.replace("1-0", "");
        allMoves = allMoves.replace("0-1", "");
        allMoves = allMoves.replace("1/2-1/2", "");
        allMoves = allMoves.replace("e.p.", "");
        String[] moves = parseMoves(allMoves);
        String[] moveset;
        String[] moveInfo = new String[10];
        for (String s : moves) {
            //System.out.println(printBoard()); //DEBUGGING
            moveset = s.split(" ");
            if (moveset.length == 2) {
                moveInfo = parseMove(moveset[0]);
                whiteMove(moveInfo);
                moveInfo = parseMove(moveset[1]);
                blackMove(moveInfo);
            } else {
                moveInfo = parseMove(moveset[0]);
                whiteMove(moveInfo);
            }
        }


        return printBoard();
    }
    //Returns true if target is an element of moves, false otherwise
    private static boolean containing(int[][] moves,
                                      int[] target) {

        for (int i = 0; i < moves.length; i++) {

            if (moves[i][0] == target[0] && moves[i][1] == target[1]) {
                return true;
            }
        }
        return false;
    }

    //All updatePiece methods take in specific information about a move
    //and update the piece accordingly, taking into account legality
    //and en passant possibility

    private static boolean updatePiece(String finalPos, boolean isWhite) {
        int[] position = new int[2];
        Integer[] finalIndex = intToInteger(posToIndex(finalPos));
        int[] primFinalIndex = posToIndex(finalPos);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                position[0] = i;
                position[1] = j;
                if ((isWhitePiece(i, j) && isWhite)
                    || (isBlackPiece(i, j) && !isWhite)) {
                    if (containing(availableMoves(position), primFinalIndex)) {
                        newPiece = chessboard[primFinalIndex[0]]
                            [primFinalIndex[1]];
                        newPos = primFinalIndex;
                        origPiece = chessboard[i][j];
                        origPos = position;
                        chessboard[primFinalIndex[0]][primFinalIndex[1]] =
                            chessboard[i][j];
                        chessboard[i][j] = '\u0000';
                        if (isLegal(isWhite)) {
                            didEnPassant(isWhite);
                            removeEnPassant(isWhite);
                            enPassantPossible(isWhite);
                            return true;
                        } else {
                            reverseMove();
                        }
                    }
                }
            }
        }
        return false;
    }
    private static boolean updatePiece(String startingPiece, String finalPos,
                                    boolean isWhite) {
        char piece = startingPiece.charAt(0);
        piece = (!isWhite) ? Character.toLowerCase(piece)  : piece;
        int[] position = new int[2];
        int[] primFinalIndex = posToIndex(finalPos);
        Integer[] finalIndex = intToInteger(primFinalIndex);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                position[0] = i;
                position[1] = j;
                if (chessboard[i][j] == piece
                    && containing(availableMoves(position), primFinalIndex)) {
                    newPiece = chessboard[primFinalIndex[0]]
                        [primFinalIndex[1]];
                    newPos = primFinalIndex;
                    origPiece = chessboard[i][j];
                    origPos = position;
                    chessboard[primFinalIndex[0]][primFinalIndex[1]] =
                            chessboard[i][j];
                    chessboard[i][j] = '\u0000';
                    if (isLegal(isWhite)) {
                        didEnPassant(isWhite);
                        removeEnPassant(isWhite);
                        enPassantPossible(isWhite);
                        return true;
                    } else {
                        reverseMove();
                    }
                }
            }
        }
        return false;
    }
    private static boolean updatePiece(char startingFile, String finalPos,
                                    boolean isWhite) {
        int file = (int) (startingFile) - 97;
        int[] position = new int[2];
        int[] primFinalIndex = posToIndex(finalPos);
        Integer[] finalIndex = intToInteger(primFinalIndex);
        for (int i = 0; i < 8; i++) {
            position[0] = i;
            position[1] = file;
            if ((isWhitePiece(i, file) && isWhite)
                || (isBlackPiece(i, file) && !isWhite)) {
                if (containing(availableMoves(position), primFinalIndex)) {
                    newPiece = chessboard[primFinalIndex[0]]
                        [primFinalIndex[1]];
                    newPos = primFinalIndex;
                    origPiece = chessboard[i][file];
                    origPos = position;
                    chessboard[primFinalIndex[0]][primFinalIndex[1]] =
                        chessboard[i][file];
                    chessboard[i][file] = '\u0000';
                    if (isLegal(isWhite)) {
                        didEnPassant(isWhite);
                        removeEnPassant(isWhite);
                        enPassantPossible(isWhite);
                        return true;
                    } else {
                        reverseMove();
                    }
                }
            }
        }
        return false;
    }
    private static boolean updatePiece(int startingRank, String finalPos,
                                    boolean isWhite) {
        int rank = 8 - startingRank;
        int[] position = new int[2];
        int[] primFinalIndex = posToIndex(finalPos);
        Integer[] finalIndex = intToInteger(primFinalIndex);
        for (int i = 0; i < 8; i++) {
            position[0] = rank;
            position[1] = i;
            if ((isWhitePiece(rank, i) && isWhite)
                || (isBlackPiece(rank, i) && !isWhite)) {
                if (containing(availableMoves(position), primFinalIndex)) {
                    newPiece = chessboard[primFinalIndex[0]]
                        [primFinalIndex[1]];
                    newPos = primFinalIndex;
                    origPiece = chessboard[rank][i];
                    origPos = position;
                    chessboard[primFinalIndex[0]][primFinalIndex[1]] =
                        chessboard[rank][i];
                    chessboard[rank][i] = '\u0000';
                    if (isLegal(isWhite)) {
                        didEnPassant(isWhite);
                        removeEnPassant(isWhite);
                        enPassantPossible(isWhite);
                        return true;
                    } else {
                        reverseMove();
                    }
                }
            }
        }
        return false;
    }
    private static boolean updatePiece(String startingPiece, int startingRank,
                                    String finalPos, boolean isWhite) {
        char piece = startingPiece.charAt(0);
        piece = (!isWhite) ? Character.toLowerCase(piece)  : piece;
        int[] position = new int[2];
        int rank = 8 - startingRank;
        int[] primFinalIndex = posToIndex(finalPos);
        Integer[] finalIndex = intToInteger(primFinalIndex);
        for (int i = 0; i < 8; i++) {
            position[0] = rank;
            position[1] = i;
            if (chessboard[rank][i] == piece
                && containing(availableMoves(position), primFinalIndex)) {
                newPiece = chessboard[primFinalIndex[0]]
                    [primFinalIndex[1]];
                newPos = primFinalIndex;
                origPiece = chessboard[rank][i];
                origPos = position;
                chessboard[primFinalIndex[0]][primFinalIndex[1]] =
                    chessboard[rank][i];
                chessboard[rank][i] = '\u0000';
                if (isLegal(isWhite)) {
                    didEnPassant(isWhite);
                    removeEnPassant(isWhite);
                    enPassantPossible(isWhite);
                    return true;
                } else {
                    reverseMove();
                }
            }
        }
        return false;
    }
    private static boolean updatePiece(String startingPiece, char startingFile,
                                    String finalPos, boolean isWhite) {
        char piece = startingPiece.charAt(0);
        piece = (!isWhite) ? Character.toLowerCase(piece)  : piece;
        int[] position = new int[2];
        int file = (int) (startingFile) - 97;
        int[] primFinalIndex = posToIndex(finalPos);
        Integer[] finalIndex = intToInteger(primFinalIndex);
        for (int i = 0; i < 8; i++) {
            position[0] = i;
            position[1] = file;
            if (chessboard[i][file] == piece
                && containing(availableMoves(position), primFinalIndex)) {
                newPiece = chessboard[primFinalIndex[0]]
                    [primFinalIndex[1]];
                newPos = primFinalIndex;
                origPiece = chessboard[i][file];
                origPos = position;
                chessboard[primFinalIndex[0]][primFinalIndex[1]] =
                    chessboard[i][file];
                chessboard[i][file] = '\u0000';
                if (isLegal(isWhite)) {
                    didEnPassant(isWhite);
                    removeEnPassant(isWhite);
                    enPassantPossible(isWhite);
                    return true;
                } else {
                    reverseMove();
                }
            }
        }
        return false;
    }
    private static boolean updatePiece(String startingPiece, char startingFile,
                                       int startingRank, String finalPos,
                                       boolean isWhite) {
        char piece = startingPiece.charAt(0);
        piece = (!isWhite) ? Character.toLowerCase(piece)  : piece;
        int file = (int) (startingFile) - 97;
        int rank = 8 - startingRank;
        int[] position = {rank, file};
        int[] primFinalIndex = posToIndex(finalPos);
        Integer[] finalIndex = intToInteger(primFinalIndex);
        if (chessboard[rank][file] == piece
            && containing(availableMoves(position), primFinalIndex)) {
            newPiece = chessboard[primFinalIndex[0]]
                [primFinalIndex[1]];
            newPos = primFinalIndex;
            origPiece = chessboard[rank][file];
            origPos = position;
            chessboard[primFinalIndex[0]][primFinalIndex[1]] =
                chessboard[rank][file];
            chessboard[rank][file] = '\u0000';
            if (isLegal(isWhite)) {
                didEnPassant(isWhite);
                removeEnPassant(isWhite);
                enPassantPossible(isWhite);
                return true;
            } else {
                reverseMove();
            }
        }
        return false;
    }

    //Returns a String with the FEN notation of the current board state
    private static String  printBoard() {
        String s = "";
        int blankCounter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessboard[i][j] == '\u0000') {
                    blankCounter++;
                } else {
                    if (blankCounter > 0) {
                        s += blankCounter;
                    }
                    s += Character.toString(chessboard[i][j]);
                    blankCounter = 0;
                }
            }
            if (blankCounter > 0) {
                s += blankCounter;
                blankCounter = 0;
            }
            s += "/";
        }
        return s.substring(0, s.length() - 1);
    }
    private static String removeTag(String s) {
        while (s.indexOf("]") != -1) {
            s = s.substring(s.indexOf("]") + 1);
        }

        return s;
    }
    //Create chessboard in starting position with starting pieces
    private static void fillBoard() {
        chessboard[0][0] = 'r';
        chessboard[0][1] = 'n';
        chessboard[0][2] = 'b';
        chessboard[0][3] = 'q';
        chessboard[0][4] = 'k';
        chessboard[0][5] = 'b';
        chessboard[0][6] = 'n';
        chessboard[0][7] = 'r';
        for (int i = 0; i < 8; i++) {
            chessboard[1][i] = 'p';
        }
        for (int i = 0; i < 8; i++) {
            chessboard[6][i] = 'P';
        }
        chessboard[7][0] = 'R';
        chessboard[7][1] = 'N';
        chessboard[7][2] = 'B';
        chessboard[7][3] = 'Q';
        chessboard[7][4] = 'K';
        chessboard[7][5] = 'B';
        chessboard[7][6] = 'N';
        chessboard[7][7] = 'R';

    }
    //Takes String containing all the moves of the game (no tag)
    //and returns a String[] containing each turn as an element of
    //the array
    private static String[] parseMoves(String allMoves) {
        String[] moves = new String[0];
        int currentMarker = 1;
        while (allMoves.indexOf(((currentMarker + 1) + ".")) != -1) {
            String start = currentMarker + ".";
            String end = (currentMarker + 1) + ".";
            int startIndex = allMoves.indexOf(start)
                + ((currentMarker > 9) ? 3 : 2);
            int endIndex = allMoves.indexOf(end);
            moves = addTo(moves,
                          allMoves.substring(startIndex, endIndex).trim());
            currentMarker++;
        }
        moves = addTo(moves, allMoves.substring(allMoves.indexOf(currentMarker
                                                                 + ".")
                                     + ((currentMarker > 9) ? 3 : 2)).trim());
        return moves;
    }
    //Takes a String containing the information about one move, and
    //returns a String[] containing information about what was in the move
    //and what information is available
    private static String[] parseMove(String fullMove) {
        String cleanedMove = fullMove.replace("!", "");
        cleanedMove = cleanedMove.replace("?", "");
        cleanedMove = cleanedMove.replace("#", "");
        cleanedMove = cleanedMove.replace("+", "");
        cleanedMove = cleanedMove.replace("x", "");
        String[] moveInfo = {"", "", "", "", "", "", "", "", "", ""};
        //0-finalPos 1-isStartingPiece 2-startingPiece 3-isStartingFile
        //4-startingFile 5-isStartingRank 6-startingRank 7-isPromotion
        //8-promotion 9-castlingInfo
        if (cleanedMove.equals("O-O")) {
            moveInfo[9] = "kCastle";
            return moveInfo;
        } else if (cleanedMove.equals("O-O-O")) {
            moveInfo[9] = "qCastle";
            return moveInfo;
        }
        if (cleanedMove.indexOf("=") != -1) {
            moveInfo[7] = "true";
            moveInfo[8] = Character.
                         toString(cleanedMove.
                                  charAt(cleanedMove.indexOf("=") + 1));
            cleanedMove = cleanedMove.substring(0, cleanedMove.indexOf("="));
        }
        if (cleanedMove.length() == 2) {
            moveInfo[0] = cleanedMove;
        } else if (cleanedMove.length() == 3) {
            if (Character.isUpperCase(cleanedMove.charAt(0))) {
                moveInfo[1] = "true";
                moveInfo[2] = cleanedMove.substring(0, 1);
                moveInfo[0] = cleanedMove.substring(1);
            } else if (Character.isLowerCase(cleanedMove.charAt(0))) {
                moveInfo[3] = "true";
                moveInfo[4] = cleanedMove.substring(0, 1);
                moveInfo[0] = cleanedMove.substring(1);
            } else if (Character.isDigit(cleanedMove.charAt(0))) {
                moveInfo[5] = "true";
                moveInfo[6] = cleanedMove.substring(0, 1);
                moveInfo[0] = cleanedMove.substring(1);
            }
        } else if (cleanedMove.length() == 4) {
            moveInfo[1] = "true";
            moveInfo[2] = cleanedMove.substring(0, 1);
            if (Character.isDigit(cleanedMove.charAt(1))) {
                moveInfo[5] = "true";
                moveInfo[6] = cleanedMove.substring(1, 2);
                moveInfo[0] = cleanedMove.substring(2);
            } else if (Character.isLowerCase(cleanedMove.charAt(1))) {
                moveInfo[3] = "true";
                moveInfo[4] = cleanedMove.substring(1, 2);
                moveInfo[0] = cleanedMove.substring(2);
            }
        } else if (cleanedMove.length() == 5) {
            moveInfo[1] = "true";
            moveInfo[2] = cleanedMove.substring(0, 1);
            moveInfo[3] = "true";
            moveInfo[4] = cleanedMove.substring(1, 2);
            moveInfo[5] = "true";
            moveInfo[6] = cleanedMove.substring(2, 3);
            moveInfo[0] = cleanedMove.substring(3);
        }

        return moveInfo;
    }
    //Takes SAN notated square and returns int[] representing
    //position on the chessboard
    private static int[] posToIndex(String reference) {
        int[] position = new int[2];
        position[1] = (int) (reference.charAt(0)) - 97;
        position[0] = 8 - Integer.parseInt(reference.substring(1));
        return position;
    }
    //Returns int[] containing the same repective elements as iArray
    private static int[] integerToInt(Integer[] iArray) {
        int[] result = new int[iArray.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = iArray[i].intValue();
        }
        return result;
    }
    //Returns Integer[] containing the same respective elements as iArray
    private static Integer[] intToInteger(int[] iArray) {
        Integer[] result = new Integer[iArray.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.valueOf(iArray[i]);
        }
        return result;
    }
    //Returns true if location on chessboard notated by position is empty
    //false otherwise
    private static boolean isEmpty(int[] position) {
        return (chessboard[position[0]][position[1]] == '\u0000');
    }
    //Returns true if location on chessboard notated by pos0 and pos1
    //is empty, false otherwise
    private static boolean isEmpty(int pos0, int pos1) {
        return (chessboard[pos0][pos1] == '\u0000');
    }
    //Returns true if pos0,pos1 represents a black piece on the chessboard
    private static boolean isBlackPiece(int pos0, int pos1) {
        char piece = chessboard[pos0][pos1];
        return (piece == 'p' || piece == 'r' || piece == 'n' || piece == 'b'
                || piece == 'q' || piece == 'k');
    }
    //Returns true if pos0,pos1 represents a white piece on the chessboard
    private static boolean isWhitePiece(int pos0, int pos1) {
        char piece = chessboard[pos0][pos1];
        return (piece == 'P' || piece == 'R' || piece == 'N' || piece == 'B'
                || piece == 'Q' || piece == 'K');
    }
    //Returns an int[][] where each int[] element contains a possible move of
    //the piece located at position
    private static int[][] availableMoves(int[] position) {
        char piece = chessboard[position[0]][position[1]];
        if (piece == 'P') {
            return whitePawnMoves(position);
        }
        if (piece == 'p') {
            return blackPawnMoves(position);
        }
        if (piece == 'N') {
            return whiteKnightMoves(position);
        }
        if (piece == 'n') {
            return blackKnightMoves(position);
        }
        if (piece == 'B') {
            return whiteBishopMoves(position);
        }
        if (piece == 'b') {
            return blackBishopMoves(position);
        }
        if (piece == 'R') {
            return whiteRookMoves(position);
        }
        if (piece == 'r') {
            return blackRookMoves(position);
        }
        if (piece == 'Q') {
            return whiteQueenMoves(position);
        }
        if (piece == 'q') {
            return blackQueenMoves(position);
        }
        if (piece == 'K') {
            return isWhiteKLegal(whiteKingMoves(position));
        }
        if (piece == 'k') {
            return isBlackKLegal(blackKingMoves(position));
        }

        return null;

    }

    //Next methods all return the possible moves of a specific type of piece
    //located at a certain position based on the presence or absence of other
    //pieces

    private static int[][]  whitePawnMoves(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        if (position[0] == 6 && isEmpty(position[0] - 1, position[1])
            && isEmpty(position[0] - 2, position[1])) {
            possiblePos[0] = position[0] - 2;
            possiblePos[1] = position[1];
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (isEmpty(position[0] - 1, position[1])) {
            possiblePos[0] = position[0] - 1;
            possiblePos[1] = position[1];
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (position[1] != 0 && isBlackPiece(position[0] - 1,
                                             position[1] - 1)) {
            possiblePos[0] = position[0] - 1;
            possiblePos[1] = position[1] - 1;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (position[1] != 7 && isBlackPiece(position[0] - 1,
                                             position[1] + 1)) {
            possiblePos[0] = position[0] - 1;
            possiblePos[1] = position[1] + 1;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (enPassantWhite
            && ((position[0] == enPassantWStartPos1[0]
                 && position[1] == enPassantWStartPos1[1])
                || (position[0] == enPassantWStartPos2[0]
                    && position[1] == enPassantWStartPos2[1]))) {
            possiblePos[0] = enPassantWPosition[0];
            possiblePos[1] = enPassantWPosition[1];
            possibleMoves = addTo(possibleMoves, possiblePos);


        }

        return possibleMoves;
    }
    private static int[][] whitePawnCaptures(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        if (position[1] != 0 && position[0] != 0
            && isBlackPiece(position[0] - 1,
                            position[1] - 1)) {
            possiblePos[0] = position[0] - 1;
            possiblePos[1] = position[1] - 1;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (position[1] != 7 && position[0] != 0
            && isBlackPiece(position[0] - 1,
                            position[1] + 1)) {
            possiblePos[0] = position[0] - 1;
            possiblePos[1] = position[1] + 1;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        return possibleMoves;
    }
    private static int[][] blackPawnMoves(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        if (position[0] == 1 && isEmpty(position[0] + 1, position[1])
            && isEmpty(position[0] + 2, position[1])) {
            possiblePos[0] = position[0] + 2;
            possiblePos[1] = position[1];
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (isEmpty(position[0] + 1, position[1])) {
            possiblePos[0] = position[0] + 1;
            possiblePos[1] = position[1];
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (position[1] != 0 && isWhitePiece(position[0] + 1,
                                             position[1] - 1)) {
            possiblePos[0] = position[0] + 1;
            possiblePos[1] = position[1] - 1;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (position[1] != 7 && isWhitePiece(position[0] + 1,
                                             position[1] + 1)) {
            possiblePos[0] = position[0] + 1;
            possiblePos[1] = position[1] + 1;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (enPassantBlack
            && ((position[0] == enPassantBStartPos1[0]
                 && position[1] == enPassantBStartPos1[1])
                || (position[0] == enPassantBStartPos2[0]
                    && position[1] == enPassantBStartPos2[1]))) {
            possiblePos[0] = enPassantBPosition[0];
            possiblePos[1] = enPassantBPosition[1];
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        return possibleMoves;
    }
    private static int[][] blackPawnCaptures(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        if (position[1] != 0 && position[0] != 7
            && isWhitePiece(position[0] + 1,
                            position[1] - 1)) {
            possiblePos[0] = position[0] + 1;
            possiblePos[1] = position[1] - 1;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (position[1] != 7 && position[0] != 7
            && isWhitePiece(position[0] + 1,
                            position[1] + 1)) {
            possiblePos[0] = position[0] + 1;
            possiblePos[1] = position[1] + 1;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        return possibleMoves;
    }
    private static int[][] whiteKnightMoves(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        if (position[0] + 2 < 8 && position[1] + 1 < 8
            && (isEmpty(position[0] + 2, position[1] + 1)
                || isBlackPiece(position[0] + 2, position[1] + 1))) {
            possiblePos[0] = position[0] + 2;
            possiblePos[1] = position[1] + 1;
            possibleMoves = addTo(possibleMoves, possiblePos);

        }
        if (position[0] + 1 < 8 && position[1] + 2 < 8
            && (isEmpty(position[0] + 1, position[1] + 2)
                || isBlackPiece(position[0] + 1, position[1] + 2))) {
            possiblePos[0] = position[0] + 1;
            possiblePos[1] = position[1] + 2;
            possibleMoves = addTo(possibleMoves, possiblePos);

        }
        if (position[0] - 2 >= 0 && position[1] - 1 >= 0
            && (isEmpty(position[0] - 2, position[1] - 1)
                || isBlackPiece(position[0] - 2, position[1] - 1))) {
            possiblePos[0] = position[0] - 2;
            possiblePos[1] = position[1] - 1;
            possibleMoves = addTo(possibleMoves, possiblePos);

        }
        if (position[0] - 1 >= 0 && position[1] - 2 >= 0
            && (isEmpty(position[0] - 1, position[1] - 2)
                || isBlackPiece(position[0] - 1, position[1] - 2))) {
            possiblePos[0] = position[0] - 1;
            possiblePos[1] = position[1] - 2;
            possibleMoves = addTo(possibleMoves, possiblePos);

        }
        if (position[0] + 2 < 8 && position[1] - 1 >= 0
            && (isEmpty(position[0] + 2, position[1] - 1)
                || isBlackPiece(position[0] + 2, position[1] - 1))) {
            possiblePos[0] = position[0] + 2;
            possiblePos[1] = position[1] - 1;
            possibleMoves = addTo(possibleMoves, possiblePos);


        }
        if (position[0] - 1 >= 0 && position[1] + 2 < 8
            && (isEmpty(position[0] - 1, position[1] + 2)
                || isBlackPiece(position[0] - 1, position[1] + 2))) {
            possiblePos[0] = position[0] - 1;
            possiblePos[1] = position[1] + 2;
            possibleMoves = addTo(possibleMoves, possiblePos);

        }
        if (position[0] - 2 >= 0 && position[1] + 1 < 8
            && (isEmpty(position[0] - 2, position[1] + 1)
                || isBlackPiece(position[0] - 2, position[1] + 1))) {
            possiblePos[0] = position[0] - 2;
            possiblePos[1] = position[1] + 1;
            possibleMoves = addTo(possibleMoves, possiblePos);

        }
        if (position[0] + 1 < 8 && position[1] - 2 >= 0
            && (isEmpty(position[0] + 1, position[1] - 2)
                || isBlackPiece(position[0] + 1, position[1] - 2))) {
            possiblePos[0] = position[0] + 1;
            possiblePos[1] = position[1] - 2;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        return possibleMoves;
    }
    private static int[][] blackKnightMoves(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        if (position[0] + 2 < 8 && position[1] + 1 < 8
            && (isEmpty(position[0] + 2, position[1] + 1)
                || isWhitePiece(position[0] + 2, position[1] + 1))) {
            possiblePos[0] = position[0] + 2;
            possiblePos[1] = position[1] + 1;
            possibleMoves = addTo(possibleMoves, possiblePos);
            //printArray(possibleMoves);
            //System.out.println("Look here ^ and here v");
        }

        if (position[0] + 1 < 8 && position[1] + 2 < 8
            && (isEmpty(position[0] + 1, position[1] + 2)
                || isWhitePiece(position[0] + 1, position[1] + 2))) {
            possiblePos[0] = position[0] + 1;
            possiblePos[1] = position[1] + 2;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (position[0] - 2 >= 0 && position[1] - 1 >= 0
            && (isEmpty(position[0] - 2, position[1] - 1)
                || isWhitePiece(position[0] - 2, position[1] - 1))) {
            possiblePos[0] = position[0] - 2;
            possiblePos[1] = position[1] - 1;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (position[0] - 1 >= 0 && position[1] - 2 >= 0
            && (isEmpty(position[0] - 1, position[1] - 2)
                || isWhitePiece(position[0] - 1, position[1] - 2))) {
            possiblePos[0] = position[0] - 1;
            possiblePos[1] = position[1] - 2;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        //printArray(possibleMoves);
        if (position[0] + 2 < 8 && position[1] - 1 >= 0
            && (isEmpty(position[0] + 2, position[1] - 1)
                || isWhitePiece(position[0] + 2, position[1] - 1))) {
            possiblePos[0] = position[0] + 2;
            possiblePos[1] = position[1] - 1;
            //printArray(possibleMoves);
            possibleMoves = addTo(possibleMoves, possiblePos);

        }
        if (position[0] - 1 >= 0 && position[1] + 2 < 8
            && (isEmpty(position[0] - 1, position[1] + 2)
                || isWhitePiece(position[0] - 1, position[1] + 2))) {
            possiblePos[0] = position[0] - 1;
            possiblePos[1] = position[1] + 2;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (position[0] - 2 >= 0 && position[1] + 1 < 8
            && (isEmpty(position[0] - 2, position[1] + 1)
                || isWhitePiece(position[0] - 2, position[1] + 1))) {
            possiblePos[0] = position[0] - 2;
            possiblePos[1] = position[1] + 1;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        if (position[0] + 1 < 8 && position[1] - 2 >= 0
            && (isEmpty(position[0] + 1, position[1] - 2)
                || isWhitePiece(position[0] + 1, position[1] - 2))) {
            possiblePos[0] = position[0] + 1;
            possiblePos[1] = position[1] - 2;
            possibleMoves = addTo(possibleMoves, possiblePos);
        }
        return possibleMoves;
    }
    private static int[][] whiteBishopMoves(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        boolean test = true;
        int dist = 1;
        while (test) {
            if (position[0] + dist < 8 && position[1] + dist < 8) {
                if (isEmpty(position[0] + dist, position[1] + dist)) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isBlackPiece(position[0] + dist,
                                       position[1] + dist)) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        test = true;
        dist = 1;
        while (test) {
            if (position[0] + dist < 8 && position[1] - dist >= 0) {
                if (isEmpty(position[0] + dist, position[1] - dist)) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isBlackPiece(position[0] + dist,
                                        position[1] - dist)) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        test = true;
        dist = 1;
        while (test) {
            if (position[0] - dist >= 0 && position[1] - dist >= 0) {
                if (isEmpty(position[0] - dist, position[1] - dist)) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isBlackPiece(position[0] - dist,
                                        position[1] - dist)) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        test = true;
        dist = 1;
        while (test) {
            if (position[0] - dist >= 0 && position[1] + dist < 8) {
                if (isEmpty(position[0] - dist, position[1] + dist)) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isBlackPiece(position[0] - dist,
                                        position[1] + dist)) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        return possibleMoves;
    }
    private static int[][] blackBishopMoves(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        boolean test = true;
        int dist = 1;
        while (test) {
            if (position[0] + dist < 8 && position[1] + dist < 8) {
                if (isEmpty(position[0] + dist, position[1] + dist)) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isWhitePiece(position[0] + dist,
                                       position[1] + dist)) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        test = true;
        dist = 1;
        while (test) {
            if (position[0] + dist < 8 && position[1] - dist >= 0) {
                if (isEmpty(position[0] + dist, position[1] - dist)) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isWhitePiece(position[0] + dist,
                                        position[1] - dist)) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        test = true;
        dist = 1;
        while (test) {
            if (position[0] - dist >= 0 && position[1] - dist >= 0) {
                if (isEmpty(position[0] - dist, position[1] - dist)) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isWhitePiece(position[0] - dist,
                                        position[1] - dist)) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        test = true;
        dist = 1;
        while (test) {
            if (position[0] - dist >= 0 && position[1] + dist < 8) {
                if (isEmpty(position[0] - dist, position[1] + dist)) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isWhitePiece(position[0] - dist,
                                        position[1] + dist)) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        return possibleMoves;
    }
    private static int[][] whiteRookMoves(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        int dist = 1;
        boolean test = true;
        while (test) {
            if (position[0] + dist < 8) {
                if (isEmpty(position[0] + dist, position[1])) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1];
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isBlackPiece(position[0] + dist, position[1])) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1];
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        dist = 1;
        test = true;
        while (test) {
            if (position[1] + dist < 8) {
                if (isEmpty(position[0], position[1] + dist)) {
                    possiblePos[0] = position[0];
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isBlackPiece(position[0], position[1] + dist)) {
                    possiblePos[0] = position[0];
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        dist = 1;
        test = true;
        while (test) {
            if (position[1] - dist >= 0) {
                if (isEmpty(position[0], position[1] - dist)) {
                    possiblePos[0] = position[0];
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isBlackPiece(position[0], position[1] - dist)) {
                    possiblePos[0] = position[0];
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        dist = 1;
        test = true;
        while (test) {
            if (position[0] - dist >= 0) {
                if (isEmpty(position[0] - dist, position[1])) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1];
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isBlackPiece(position[0] - dist, position[1])) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1];
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        return possibleMoves;
    }
    private static int[][] blackRookMoves(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        int dist = 1;
        boolean test = true;
        while (test) {
            if (position[0] + dist < 8) {
                if (isEmpty(position[0] + dist, position[1])) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1];
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isWhitePiece(position[0] + dist, position[1])) {
                    possiblePos[0] = position[0] + dist;
                    possiblePos[1] = position[1];
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        dist = 1;
        test = true;
        while (test) {
            if (position[1] + dist < 8) {
                if (isEmpty(position[0], position[1] + dist)) {
                    possiblePos[0] = position[0];
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isWhitePiece(position[0], position[1] + dist)) {
                    possiblePos[0] = position[0];
                    possiblePos[1] = position[1] + dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        dist = 1;
        test = true;
        while (test) {
            if (position[1] - dist >= 0) {
                if (isEmpty(position[0], position[1] - dist)) {
                    possiblePos[0] = position[0];
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isWhitePiece(position[0], position[1] - dist)) {
                    possiblePos[0] = position[0];
                    possiblePos[1] = position[1] - dist;
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        dist = 1;
        test = true;
        while (test) {
            if (position[0] - dist >= 0) {
                if (isEmpty(position[0] - dist, position[1])) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1];
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    dist++;
                } else if (isWhitePiece(position[0] - dist, position[1])) {
                    possiblePos[0] = position[0] - dist;
                    possiblePos[1] = position[1];
                    possibleMoves = addTo(possibleMoves, possiblePos);
                    test = false;
                } else {
                    test = false;
                }
            } else {
                test = false;
            }
        }
        return possibleMoves;
    }
    private static int[][] whiteQueenMoves(int[] position) {
        int[][] possibleMoves = combine(whiteBishopMoves(position),
                                        whiteRookMoves(position));
        return possibleMoves;
    }
    private static int[][] blackQueenMoves(int[] position) {
        int[][] possibleMoves = combine(blackBishopMoves(position),
                                        blackRookMoves(position));
        return possibleMoves;
    }
    private static int[][] whiteKingMoves(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        if (position[0] + 1 < 8) {
            if (isEmpty(position[0] + 1, position[1])
                || isBlackPiece(position[0] + 1, position[1])) {
                possiblePos[0] = position[0] + 1;
                possiblePos[1] = position[1];
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[1] + 1 < 8) {
            if (isEmpty(position[0], position[1] + 1)
                || isBlackPiece(position[0], position[1] + 1)) {
                possiblePos[0] = position[0];
                possiblePos[1] = position[1] + 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[0] + 1 < 8 && position[1] + 1 < 8) {
            if (isEmpty(position[0] + 1, position[1] + 1)
                || isBlackPiece(position[0] + 1, position[1] + 1)) {
                possiblePos[0] = position[0] + 1;
                possiblePos[1] = position[1] + 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[0] - 1 >= 0) {
            if (isEmpty(position[0] - 1, position[1])
                || isBlackPiece(position[0] - 1, position[1])) {
                possiblePos[0] = position[0] - 1;
                possiblePos[1] = position[1];
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[0] + 1 < 8 && position[1] - 1 >= 0) {
            if (isEmpty(position[0] + 1, position[1] - 1)
                || isBlackPiece(position[0] + 1, position[1] - 1)) {
                possiblePos[0] = position[0] + 1;
                possiblePos[1] = position[1] - 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[1] + 1 < 8 && position[0] - 1 >= 0) {
            if (isEmpty(position[0] - 1, position[1] + 1)
                || isBlackPiece(position[0] - 1, position[1] + 1)) {
                possiblePos[0] = position[0] - 1;
                possiblePos[1] = position[1] + 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[0] - 1 >= 0 && position[1] - 1 >= 0) {
            if (isEmpty(position[0] - 1, position[1] - 1)
                || isBlackPiece(position[0] - 1, position[1] - 1)) {
                possiblePos[0] = position[0] - 1;
                possiblePos[1] = position[1] - 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[1] - 1 >= 0) {
            if (isEmpty(position[0], position[1] - 1)
                || isBlackPiece(position[0], position[1] - 1)) {
                possiblePos[0] = position[0];
                possiblePos[1] = position[1] - 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        return possibleMoves;
    }
    private static int[][] blackKingMoves(int[] position) {
        int[] possiblePos = new int[2];
        int[][] possibleMoves = new int[0][];
        if (position[0] + 1 < 8) {
            if (isEmpty(position[0] + 1, position[1])
                || isWhitePiece(position[0] + 1, position[1])) {
                possiblePos[0] = position[0] + 1;
                possiblePos[1] = position[1];
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[1] + 1 < 8) {
            if (isEmpty(position[0], position[1] + 1)
                || isWhitePiece(position[0], position[1] + 1)) {
                possiblePos[0] = position[0];
                possiblePos[1] = position[1] + 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[0] + 1 < 8 && position[1] + 1 < 8) {
            if (isEmpty(position[0] + 1, position[1] + 1)
                || isWhitePiece(position[0] + 1, position[1] + 1)) {
                possiblePos[0] = position[0] + 1;
                possiblePos[1] = position[1] + 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[0] - 1 >= 0) {
            if (isEmpty(position[0] - 1, position[1])
                || isWhitePiece(position[0] - 1, position[1])) {
                possiblePos[0] = position[0] - 1;
                possiblePos[1] = position[1];
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[0] + 1 < 8 && position[1] - 1 >= 0) {
            if (isEmpty(position[0] + 1, position[1] - 1)
                || isWhitePiece(position[0] + 1, position[1] - 1)) {
                possiblePos[0] = position[0] + 1;
                possiblePos[1] = position[1] - 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[1] + 1 < 8 && position[0] - 1 >= 0) {
            if (isEmpty(position[0] - 1, position[1] + 1)
                || isWhitePiece(position[0] - 1, position[1] + 1)) {
                possiblePos[0] = position[0] - 1;
                possiblePos[1] = position[1] + 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[0] - 1 >= 0 && position[1] - 1 >= 0) {
            if (isEmpty(position[0] - 1, position[1] - 1)
                || isWhitePiece(position[0] - 1, position[1] - 1)) {
                possiblePos[0] = position[0] - 1;
                possiblePos[1] = position[1] - 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        if (position[1] - 1 >= 0) {
            if (isEmpty(position[0], position[1] - 1)
                || isWhitePiece(position[0], position[1] - 1)) {
                possiblePos[0] = position[0];
                possiblePos[1] = position[1] - 1;
                possibleMoves = addTo(possibleMoves, possiblePos);
            }
        }
        return possibleMoves;
    }
    //Takes int[][] of all of the king's moves and returns an int[][]
    //with all illegal moves removed

    private static int[][] isWhiteKLegal(int[][] moves) {
        int[][] legalMoves = moves;
        int size = legalMoves.length;
        int[] move;
        int[] position = new int[2];
        boolean test = true;
        for (int i = 0; i < size; i++) {
            move = legalMoves[i];
            test = true;
            for (int j = 0; j < 8 && test; j++) {
                for (int k = 0; k < 8 && test; k++) {
                    position[0] = j;
                    position[1] = k;
                    if (isBlackPiece(j, k) && chessboard[j][k] != 'k') {
                        if (chessboard[j][k] == 'p') {
                            if (containing(blackPawnCaptures(position), move)) {
                                legalMoves = remove(legalMoves, i);
                                size--;
                                i--;
                                test = false;
                            }
                        } else if (containing(availableMoves(position),
                                             move)) {
                            legalMoves = remove(legalMoves, i);
                            size--;
                            i--;
                            test = false;
                        }
                    } else if (isBlackPiece(j, k) && chessboard[j][k] == 'k') {
                        if (containing(blackKingMoves(position), move)) {
                            legalMoves = remove(legalMoves, i);
                            size--;
                            i--;
                            test = false;
                        }
                    }
                }
            }
        }

        return legalMoves;
    }
    private static int[][]
        isBlackKLegal(int[][] moves) {
        int[][] legalMoves = moves;
        int size = legalMoves.length;
        int[] move;
        int[] position = new int[2];
        boolean test = true;
        for (int i = 0; i < size; i++) {
            move = legalMoves[i];
            test = true;
            for (int j = 0; j < 8 && test; j++) {
                for (int k = 0; k < 8 && test; k++) {
                    position[0] = j;
                    position[1] = k;
                    if (isWhitePiece(j, k) && chessboard[j][k] != 'K') {
                        if (chessboard[j][k] == 'P') {
                            if (containing(whitePawnCaptures(position), move)) {
                                legalMoves = remove(legalMoves, i);
                                size--;
                                i--;
                                test = false;
                            }
                        } else if (containing(availableMoves(position),
                                             move)) {
                            legalMoves = remove(legalMoves, i);
                            size--;
                            i--;
                            test = false;
                        }
                    } else if (isWhitePiece(j, k) && chessboard[j][k] == 'K') {
                        if (containing(whiteKingMoves(position), move)) {
                            legalMoves = remove(legalMoves, i);
                            size--;
                            i--;
                            test = false;
                        }
                    }
                }
            }
        }

        return legalMoves;
    }
    //Performs the piece movements of a king side castle
    private static void kingCastle(boolean isWhite) {
        if (isWhite) {
            chessboard[7][6] = 'K';
            chessboard[7][5] = 'R';
            chessboard[7][4] = '\u0000';
            chessboard[7][7] = '\u0000';
        } else {
            chessboard[0][6] = 'k';
            chessboard[0][5] = 'r';
            chessboard[0][4] = '\u0000';
            chessboard[0][7] = '\u0000';
        }
    }
    //Performs the piece movements of a queen side castle
    private static void queenCastle(boolean isWhite) {
        if (isWhite) {
            chessboard[7][4] = '\u0000';
            chessboard[7][0] = '\u0000';
            chessboard[7][2] = 'K';
            chessboard[7][3] = 'R';
        } else {
            chessboard[0][4] = '\u0000';
            chessboard[0][0] = '\u0000';
            chessboard[0][2] = 'k';
            chessboard[0][3] = 'r';
        }
    }

    //promotes an eligible pawn
    private static void promotion(String target, boolean isWhite) {
        int row = (isWhite) ? 0 : 7;
        char pawn = (isWhite) ? 'P' : 'p';
        char promotedTo = (isWhite) ? target.charAt(0)
            : Character.toLowerCase(target.charAt(0));
        for (int i = 0; i < 8; i++) {
            if (chessboard[row][i] == pawn) {
                chessboard[row][i] = promotedTo;
            }
        }
    }
    //Executes white's move with as much information as given by moveInfo
    private static void whiteMove(String[] moveInfo) {
        if (moveInfo[1].equals("true")) {
            if (moveInfo[3].equals("true")) {
                if (moveInfo[5].equals("true")) {
                    updatePiece(moveInfo[2],
                                moveInfo[4].
                                charAt(0), Integer.
                                parseInt(moveInfo[6]),
                                moveInfo[0],
                                true);
                } else {
                    updatePiece(moveInfo[2],
                                moveInfo[4].
                                charAt(0),
                                moveInfo[0], true);
                }
            } else if (moveInfo[5].equals("true")) {
                updatePiece(moveInfo[2],
                            Integer.parseInt(moveInfo[6]),
                            moveInfo[0], true);
            } else {
                updatePiece(moveInfo[2],
                            moveInfo[0], true);
            }
        } else if (moveInfo[3].equals("true")) {
            if (!updatePiece("P",
                             moveInfo[4].charAt(0),
                             moveInfo[0], true)) {
                updatePiece(moveInfo[4].charAt(0),
                            moveInfo[0], true);
            }
        } else if (moveInfo[5].equals("true")) {
            if (!updatePiece("P",
                             Integer.
                             parseInt(moveInfo[6]),
                             moveInfo[0], true)) {
                updatePiece(Integer.
                            parseInt(moveInfo[6]),
                            moveInfo[0], true);
            }
        } else if (moveInfo[9].equals("kCastle")) {
            kingCastle(true);
        } else if (moveInfo[9].equals("qCastle")) {
            queenCastle(true);
        } else {
            if (!updatePiece("P", moveInfo[0], true)) {
                updatePiece(moveInfo[0], true);
            }
        }
        if (moveInfo[7].equals("true")) {
            promotion(moveInfo[8], true);
        }
    }
    //Executes black's move with as much information as given by moveInfo
    private static void blackMove(String[] moveInfo) {
        if (moveInfo[1].equals("true")) {
            if (moveInfo[3].equals("true")) {
                if (moveInfo[5].equals("true")) {
                    updatePiece(moveInfo[2],
                                moveInfo[4].
                                charAt(0), Integer.
                                parseInt(moveInfo[6]),
                                moveInfo[0],
                                false);
                } else {
                    updatePiece(moveInfo[2],
                                moveInfo[4].
                                charAt(0),
                                moveInfo[0], false);
                }
            } else if (moveInfo[5].equals("true")) {
                updatePiece(moveInfo[2],
                            Integer.parseInt(moveInfo[6]),
                            moveInfo[0], false);
            } else {
                updatePiece(moveInfo[2],
                            moveInfo[0], false);
            }
        } else if (moveInfo[3].equals("true")) {
            if (!updatePiece("p",
                             moveInfo[4].charAt(0),
                             moveInfo[0], false)) {
                updatePiece(moveInfo[4].charAt(0),
                            moveInfo[0], false);
            }
        } else if (moveInfo[5].equals("true")) {
            if (!updatePiece("p",
                             Integer.
                             parseInt(moveInfo[6]),
                             moveInfo[0], false)) {
                updatePiece(Integer.
                            parseInt(moveInfo[6]),
                            moveInfo[0], false);
            }
        } else if (moveInfo[9].equals("kCastle")) {
            kingCastle(false);
        } else if (moveInfo[9].equals("qCastle")) {
            queenCastle(false);
        } else {
            if (!updatePiece("p", moveInfo[0], false)) {
                updatePiece(moveInfo[0], false);
            }
        }
        if (moveInfo[7].equals("true")) {
            promotion(moveInfo[8], false);
        }
    }
    //Determines if a move exposes the king or not
    private static boolean isLegal(boolean isWhite) {
        int[] kingPos = new int[2];
        char target = (isWhite) ? 'K' : 'k';
        int[] position = new int[2];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessboard[i][j] == target) {
                    kingPos[0] = i;
                    kingPos[1] = j;
                }
            }
        }
        if (isWhite) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    position[0] = i;
                    position[1] = j;
                    if (isBlackPiece(i, j)) {
                        if (containing(availableMoves(position), kingPos)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } else  {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    position[0] = i;
                    position[1] = j;
                    if (isWhitePiece(i, j)) {
                        if (containing(availableMoves(position), kingPos)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }
    //Reverses the most recently done move if it was illegal
    private static void reverseMove() {
        chessboard[origPos[0]][origPos[1]] = origPiece;
        chessboard[newPos[0]][newPos[1]] = newPiece;
    }
    //Determines if en passant is now possible
    private static void enPassantPossible(boolean isWhite) {
        int startingRow = (isWhite) ? 6 : 1;
        int endingRow = (isWhite) ? 4 : 3;
        char piece = (isWhite) ? 'P' : 'p';
        if (origPiece == piece && origPos[0] == startingRow
            && newPos[0] == endingRow) {
            if (!isWhite) {
                enPassantWhite = true;
                enPassantWPosition[0] = (startingRow + endingRow) / 2;
                enPassantWPosition[1] = origPos[1];
                enPassantWStartPos1[0] = endingRow;
                enPassantWStartPos1[1] = origPos[1] + 1;
                enPassantWStartPos2[0] = endingRow;
                enPassantWStartPos2[1] = origPos[1] - 1;
            } else {
                enPassantBlack = true;
                enPassantBPosition[0] = (startingRow + endingRow) / 2;
                enPassantBPosition[1] = origPos[1];
                enPassantBStartPos1[0] = endingRow;
                enPassantBStartPos1[1] = origPos[1] + 1;
                enPassantBStartPos2[0] = endingRow;
                enPassantBStartPos2[1] = origPos[1] - 1;
            }

        }
    }
    //Determines if a pawn has just executed en passant
    private static void didEnPassant(boolean isWhite) {
        char piece = (isWhite) ? 'P' : 'p';
        if (origPiece == piece && origPos[1] != newPos[1]
            && newPiece == '\u0000') {
            chessboard[origPos[0]][newPos[1]] = '\u0000';

        }
    }
    //Resets en passant flags
    private static void removeEnPassant(boolean isWhite) {
        if (isWhite) {
            enPassantWhite = false;
        } else {
            enPassantBlack = false;
        }


    }
    //Adds the element addOn as a new element to the int[][] orig
    private static int[][] addTo(int[][] orig, int[] addOn) {

        //System.out.println("Current moves");
        //printArray(orig);
        int[][] newArray = new int[orig.length + 1][2];
        int[] addedMove = new int[2];
        int index = 0;
        while (index < orig.length) {
            addedMove[0] = orig[index][0];
            addedMove[1] = orig[index][1];
            newArray[index][0] = addedMove[0];
            newArray[index][1] = addedMove[1];
            index++;
        }
        addedMove[0] = addOn[0];
        addedMove[1] = addOn[1];
        newArray[index][0] = addedMove[0];
        newArray[index][1] = addedMove[1];
        //System.out.println("Adding move " + addOn[0] + " " + addOn[1]);
        //printArray(newArray);
        return newArray;

    }
    //Adds the element addOn as a new element to the String[] orig
    private static String[] addTo(String[] orig, String addOn) {
        String[] newArray = new String[orig.length + 1];
        String s;
        for (int i = 0; i < orig.length; i++) {
            s = orig[i];
            newArray[i] = s;
        }
        s = addOn;
        newArray[newArray.length - 1] = s;
        return newArray;
    }
    //Removes the element at index from orig and returns the new int[][]
    private static int[][] remove(int[][] orig, int index) {
        int[][] newArray = new int[orig.length - 1][2];
        int marker = 0;
        for (int i = 0; i < orig.length; i++) {
            if (i == index) {
                i++;
            }
            if (i < orig.length) {
                newArray[marker][0] = orig[i][0];
                newArray[marker][1] = orig[i][1];
                marker++;
            }
        }
        return newArray;
    }
    //Returns the array formed by appending arr2 to arr1
    private static int[][] combine(int[][] arr1, int[][] arr2) {
        int[][] newArray = new int[arr1.length + arr2.length][];
        int index = 0;
        for (int i = 0; i < arr1.length; i++) {
            newArray[index] = arr1[i];
            index++;
        }
        for (int i = 0; i < arr2.length; i++) {
            newArray[index] = arr2[i];
            index++;
        }
        return newArray;
    }
    //Prints a representation of an int[][] array, used for debugging
    private static void printArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }
}
