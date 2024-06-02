import java.util.ArrayList;

public class GameManager{
    static boolean initilaized;
    static Field[][] board = new Field[8][8];
    static ArrayList<Figure> figures = new ArrayList<>();

    private static void initialize(){
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                boolean locked = false;
                if((y == 0 & (x == 0 | x == 7)) | (y == 7 & (x == 0 | x == 7)))
                    locked = true;
                board[y][x] = new Field(x, y, locked);
            }
        }
        initilaized = true;
    }

    static void generateBoard(String FEN){
        if(!initilaized)
            initialize();

        // reset fields
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                board[y][x].initializeFigures(new ArrayList<>());
            }
        }
        // reset figures
        figures.clear();

        // place figures
        String boardString = FEN.split(" ")[0];
        String[] rows = boardString.split("/");
        //int figureCount = 0;
        for(int y = 0; y < 8; y++){
            String row = rows[y];

            int x = 0;
            Field currentField = board[y][x];
            if(currentField.isLocked())
                x += 1;

            ArrayList<Figure> newFigures = new ArrayList<>();
            for(int c = 0; c < row.length(); c++){
                currentField = board[y][x];
                char character = row.substring(c, c + 1).charAt(0);
                try {
                    int num = Integer.parseInt(String.valueOf(character));
                    currentField.initializeFigures(newFigures);
                    newFigures.clear();
                    x += Math.max(num, 1);
                } catch (NumberFormatException e) {
//                    Figure newFigure = figures.get(figureCount);
//                    figureCount++;
//                    newFigure.initialize(character, currentField);
                    newFigures.add(new Figure(character, currentField));
                    if(newFigures.size() == 2){
                        currentField.initializeFigures(newFigures);
                        newFigures.clear();
                        x++;
                    }
                }
            }
        }
    }

    static String generateFEN(){
        StringBuilder result = new StringBuilder();
        int emptyFieldCount = 0;
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                Field currentField = board[y][x];
                if(currentField.isLocked())
                    continue;
                if(currentField.isEmpty())
                    emptyFieldCount++;
                else{
                    if(emptyFieldCount > 0)
                        result.append(emptyFieldCount);
                    emptyFieldCount = 0;
                    if(currentField.figure != null)
                        result.append(currentField.figure.side);
                    if(currentField.topFigure != null)
                        result.append(currentField.topFigure.side);
                    else
                        result.append("0");
                }
            }
            if(emptyFieldCount > 0)
                result.append(emptyFieldCount);
            emptyFieldCount = 0;
            if(y < 7)
                result.append("/");
        }

        return result.toString();
    }

    static void printBoard(){ printBoard(0); }
    static void printBoard(int indent){
        String RESET = "\033[0m";
        String RED = "\033[0;31m";
        String BLUE = "\033[0;34m";

        String indentString = "          ";
        String totalIndent = "";
        for(int i = 0; i < indent; i++)
            totalIndent += indentString;

        for(int y = 7; y >= 0; y--){
            String row = totalIndent + (y + 1) + " |";
            
            for(int x = 0; x < 8; x++){
                if(board[y][x].isLocked())
                    row += "XX";
                else if(board[y][x].figure == null)
                    row += "  ";
                else if(board[y][x].topFigure == null){
                    if(board[y][x].figure.side == 'r')
                        row += RED;
                    if(board[y][x].figure.side == 'b')
                        row += BLUE;
                    row += board[y][x].figure + " ";
                    row += RESET;
                }
                else{
                    if(board[y][x].figure.side == 'r')
                        row += RED;
                    if(board[y][x].figure.side == 'b')
                        row += BLUE;
                    row += board[y][x].figure + "";

                    if(board[y][x].topFigure.side == 'r')
                        row += RED;
                    if(board[y][x].topFigure.side == 'b')
                        row += BLUE;
                    row += board[y][x].topFigure + "";
                    row += RESET;
                }

                row += "|";
            }
            System.out.println(row);
        }
        System.out.println(totalIndent + "   A  B  C  D  E  F  G  H  ");
    }

    static void print(String s, int indent){
        String indentString = "          ";
        String totalIndent = "";
        for(int i = 0; i < indent; i++)
            totalIndent += indentString;
        System.out.println(totalIndent + s);
    }

    static Field getField(Coordinate coordinate){
        return board[coordinate.y][coordinate.x];
    }
    private static Figure getFigure(Coordinate coordinate){
        Field field = getField(coordinate);
        if(field.topFigure != null)
            return field.topFigure;
        return field.figure;
    }
}
