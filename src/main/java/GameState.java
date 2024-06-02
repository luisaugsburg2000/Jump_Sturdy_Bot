import java.util.ArrayList;
import java.util.Collections;

public class GameState {
    String FEN;
    char player;
    boolean isMax;

    int evaluation;
    boolean gameFinished;
    ArrayList<Move> possibleMoves = new ArrayList<>();

    Move topMove;
    GameState nextGameState;

    public GameState(char player, boolean isMax) {
        this.FEN = GameManager.generateFEN();
        this.player = player;
        this.isMax = isMax;
        evaluation = evaluate();
    }

    private int evaluate(){
        int ownFigures = 0;
        int oppFigures = 0;
        int gameEndEvaluation = 0;

        // find moveable figures
        ArrayList<Figure> moveableFigures = new ArrayList<>();
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                Field currentField = GameManager.board[y][x];

                if(currentField.figure != null){
                    if(currentField.figure.side == player){
                        ownFigures++;
                        if(currentField.figure.canMove())
                            moveableFigures.add(currentField.figure);
                    }
                    else if(currentField.figure.side != 0)
                        oppFigures++;

                    if(y == 0 & currentField.figure.side == 'r')
                        gameEndEvaluation = (player == 'r') ? 100 : -100;
                    if(y == 7 & currentField.figure.side == 'b')
                        gameEndEvaluation = (player == 'b') ? 100 : -100;
                }
                if(currentField.topFigure != null){
                    if(currentField.topFigure.side == player){
                        ownFigures++;
                        if(currentField.topFigure.canMove())
                            moveableFigures.add(currentField.topFigure);
                    }
                    else if(currentField.topFigure.side != 0)
                        oppFigures++;

                    if(y == 0 & currentField.topFigure.side == 'r')
                        gameEndEvaluation = (player == 'r') ? 100 : -100;
                    if(y == 7 & currentField.topFigure.side == 'b')
                        gameEndEvaluation = (player == 'b') ? 100 : -100;
                }
            }
        }

        for(Figure figure : moveableFigures){
            figure.calculatePossibleMoves();
            possibleMoves.addAll(figure.possibleMoves);
        }
        Collections.shuffle(possibleMoves);

        // game finished due to figure on last row
        if(Math.abs(gameEndEvaluation) >= 50)
            gameFinished = true;

        // game finished because one side has no possible moves
        if(possibleMoves.isEmpty()){
            gameFinished = true;
            gameEndEvaluation = -100;
        }

        if(isMax)
            return (ownFigures - oppFigures) + gameEndEvaluation;
        else
            return -((ownFigures - oppFigures) + gameEndEvaluation);
    }

    void printPossibleMoves(){
        for(Move move : possibleMoves)
            System.out.println(move);
    }

    @Override
    public String toString() {
        return "Player: " + player + ", Ev: " + evaluation;
    }
}
