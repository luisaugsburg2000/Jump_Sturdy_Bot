import java.util.ArrayList;

public class GameManager{
    public static GameManager instance;

    public String FEN = "1b0b02b0/1b01bbb0b02/2b04bb/8/2b03r01/1r02r01r01/3rr1r0r01/r0r0rr3 r";
    public Field[][] board;
    public Player bluePlayer;
    public Player redPlayer;
    public ArrayList<Figure> figures = new ArrayList<>();

    public GameManager(){
        instance = this;
    }
    public void initialize(String FEN){
        this.FEN = FEN;
        figures.clear();
        generateBoard();
    }

    public static void main(String[] args){
        System.out.println();
        System.out.println("START");
        System.out.println();
        GameManager gameManager = new GameManager();

        gameManager.generateBoard();
        gameManager.printBoard();
        System.out.println();

//        Figure figure1 = gameManager.getFigure(new Coordinate("F7"));
//        //System.out.println(figure1.field);
//        figure1.calculatePossibleMoves();
//        figure1.printPossibleMoves();
//        figure1.possibleMoves.get(1).execute();
//        System.out.println();
//        gameManager.printBoard();
    }

    void generateBoard(){
        Field[][] result = new Field[8][8];

        // initialize result board
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                boolean locked = false;
                if((y == 0 & (x == 0 | x == 7)) | (y == 7 & (x == 0 | x == 7)))
                    locked = true;
                result[y][x] = new Field(x, y, locked);
            }
        }
        
        //String nextPlayer = FEN.substring(FEN.length() - 1);
        String boardString = FEN.substring(0, FEN.length() - 2);

        String[] rows = boardString.split("/");
        for(int y = 0; y < 8; y++){
            String row = rows[y];

            int x = 0;
            Field currentField = result[y][x];
            if(currentField.isLocked())
                x += 1;

            Figure[] figures = new Figure[2];
            int figureCounter = 0;
            for(int c = 0; c < row.length(); c++){
                currentField = result[y][x];
                String character = row.substring(c, c + 1);
                try {
                    int num = Integer.parseInt(character);
                    currentField.initializeFigures(figures);
                    figures = new Figure[2];
                    figureCounter = 0;
                    x += Math.max(num, 1);
                } catch (NumberFormatException e) {
                    figures[figureCounter] = new Figure(character, result[y][x]);
                    figureCounter++;
                    if(figureCounter == 2){
                        currentField.initializeFigures(figures);
                        figures = new Figure[2];
                        figureCounter = 0;
                        x++;
                    }
                }
            }
        }

        board = result;
    }

    void printBoard(){
        String RESET = "\033[0m";
        String RED = "\033[0;31m";
        String BLUE = "\033[0;34m";

        for(int y = 7; y >= 0; y--){
            String row = (y + 1) + " |";
            
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

                if(x != 7)
                    row += "|";
            }
            System.out.println(row);
        }
        System.out.println("  |A |B |C |D |E |F |G |H ");
    }

    Field getField(Coordinate coordinate){
        return board[coordinate.y][coordinate.x];
    }
    Figure getFigure(Coordinate coordinate){
        Field field = getField(coordinate);
        if(field.topFigure != null)
            return field.topFigure;
        return field.figure;
    }
}
