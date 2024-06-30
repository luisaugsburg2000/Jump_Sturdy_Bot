import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameState {
    String FEN;
    char player;
    boolean isMax;

    int evaluation;
    boolean gameFinished;
    char winner;
    ArrayList<Move> possibleMoves = new ArrayList<>();

    Move topMove;
    GameState nextGameState;

    TranspositionInfo transpositionInfo;
    public class TranspositionInfo {
        char perspective;
        int viewDepth;
        int depthEvaluation;
        int moveIndex;
        boolean aborted = false;
    }

    // evaluation
    ArrayList<String> evaluationList = new ArrayList<>();
    EvaluationValues evaluationValues;

    public GameState(char player, boolean isMax, int viewDepth, int moveIndex, EvaluationValues evaluationValues) {
        this.FEN = GameManager.generateFEN();
        this.player = player;
        this.isMax = isMax;

        this.transpositionInfo = new TranspositionInfo();
        if(player == 'b'){
            if(isMax)
                this.transpositionInfo.perspective = 'b';
            else
                this.transpositionInfo.perspective = 'r';
        }
        if(player == 'r'){
            if(isMax)
                this.transpositionInfo.perspective = 'r';
            else
                this.transpositionInfo.perspective = 'b';
        }

        this.transpositionInfo.viewDepth = viewDepth;
        this.transpositionInfo.moveIndex = moveIndex;

        this.evaluationValues = evaluationValues;
        evaluation = initialize();
//        if(Transposition.table.containsKey(this.FEN))
//            System.out.println("TRANS");
    }

    private int initialize(){
        //int evaluation = evaluate_old();
        int evaluation = evaluate_new();

        if(isMax)
            return evaluation;
        return -evaluation;

//        if(isMax)
//            return (ownFigures - oppFigures) + gameEndEvaluation;
//        else
//            return -((ownFigures - oppFigures) + gameEndEvaluation);
    }

    int evaluate_old(){
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

                    if(y == 0 & currentField.figure.side == 'r'){
                        gameFinished = true;
                        winner = 'r';
                        gameEndEvaluation = (player == 'r') ? 100 : -100;
                    }
                    if(y == 7 & currentField.figure.side == 'b'){
                        gameFinished = true;
                        winner = 'b';
                        gameEndEvaluation = (player == 'b') ? 100 : -100;
                    }
                }
                if(currentField.topFigure != null){
                    if(currentField.topFigure.side == player){
                        ownFigures++;
                        if(currentField.topFigure.canMove())
                            moveableFigures.add(currentField.topFigure);
                    }
                    else if(currentField.topFigure.side != 0)
                        oppFigures++;

                    if(y == 0 & currentField.topFigure.side == 'r'){
                        gameFinished = true;
                        winner = 'r';
                        gameEndEvaluation = (player == 'r') ? 100 : -100;
                    }
                    if(y == 7 & currentField.topFigure.side == 'b'){
                        gameFinished = true;
                        winner = 'b';
                        gameEndEvaluation = (player == 'b') ? 100 : -100;
                    }
                }
            }
        }

        // Zugsortierung
        for(Figure figure : moveableFigures){
            figure.calculatePossibleMoves();
            possibleMoves.addAll(figure.possibleMoves);
        }
        Collections.shuffle(possibleMoves);
        possibleMoves.sort((m1, m2) -> Integer.compare(m2.value, m1.value));

        // game finished because one side has no possible moves
        if(possibleMoves.isEmpty()){
            gameFinished = true;
            if(player == 'r')
                winner = 'b';
            else
                winner = 'r';
            gameEndEvaluation = -100;
        }

        return (ownFigures - oppFigures) + gameEndEvaluation;
    }

    int evaluate_new(){
        int rEval = 0;
        int bEval = 0;

        ArrayList<Figure> moveableFigures_red = new ArrayList<>();
        ArrayList<Figure> moveableFigures_blue = new ArrayList<>();

        StringBuilder evalInfo = new StringBuilder();
        for(int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Field currentField = GameManager.board[y][x];

                for(int i = 0; i < 2; i++){
                    Figure currentFigure = null;

                    int f_winPoints = 0;
                    int f_figurePoints = 0;
                    int f_dfhrPoints = 0;
                    int f_dflrePoints = 0;
                    int f_moveCountPoints = 0;
                    int f_attackingPoints = 0;
                    int f_protectedPoints = 0;

                    if(i == 0){
                        currentFigure = currentField.figure;
                        evalInfo.append("\n" + currentField.getCoordinate() + " BASE: ");
                    }
                    else{
                        currentFigure = currentField.topFigure;
                        evalInfo.append("\n   TOP:  ");
                    }


                    if(currentFigure != null){
                        if(currentFigure.side == 'r'){
                            evalInfo.append("R, ");
                            f_figurePoints += evaluationValues.figurePoints;
                            evalInfo.append("figurePoints: " + f_figurePoints + ", ");
                            if(currentFigure.canMove()){
                                moveableFigures_red.add(currentFigure);
                                currentFigure.calculatePossibleMoves();
                                f_moveCountPoints += currentFigure.possibleMoves.size() * evaluationValues.moveCountPoints;
                                evalInfo.append("moveCountPoints: " + f_moveCountPoints + ", ");
                            }
                            f_dfhrPoints += (8 - y) * evaluationValues.dfhrPoints;
                            evalInfo.append("dfhrPoints: " + f_dfhrPoints + ", ");
                            if((x + 1) <= 4){
                                f_dflrePoints += (x + 1) * evaluationValues.dflrePoints;
                                evalInfo.append("dflrePoints: " + f_dflrePoints + ", ");
                            }
                            else{
                                f_dflrePoints += (5 - ((x + 1) - 4)) * evaluationValues.dflrePoints;
                                evalInfo.append("dflrePoints: " + f_dflrePoints + ", ");
                            }

                            if(currentFigure.canAttack){
                                f_attackingPoints += evaluationValues.attackingPoints;
                                evalInfo.append("attackingPoints: " + f_attackingPoints + ", ");
                            }
                            if(currentFigure.isProtected){
                                f_protectedPoints += evaluationValues.protectedPoints;
                                evalInfo.append("protectedPoints: " + f_protectedPoints + ", ");
                            }

                            if(y == 0){
                                f_winPoints += evaluationValues.winPoints;
                                evalInfo.append("winPoints: " + f_winPoints);
                                gameFinished = true;
                                winner = 'r';
                            }

                            rEval += f_winPoints;
                            rEval += f_figurePoints;
                            rEval += f_dfhrPoints;
                            rEval += f_dflrePoints;
                            rEval += f_moveCountPoints;
                            rEval += f_attackingPoints;
                            rEval += f_protectedPoints;
                        }
                        else{
                            evalInfo.append("B, ");
                            f_figurePoints += evaluationValues.figurePoints;
                            evalInfo.append("figurePoints: " + f_figurePoints + ", ");
                            if(currentFigure.canMove()){
                                moveableFigures_blue.add(currentFigure);
                                currentFigure.calculatePossibleMoves();
                                f_moveCountPoints += currentFigure.possibleMoves.size() * evaluationValues.moveCountPoints;
                                evalInfo.append("moveCountPoints: " + f_moveCountPoints + ", ");
                            }
                            f_dfhrPoints += (y + 1) * evaluationValues.dfhrPoints;
                            evalInfo.append("dfhrPoints: " + f_dfhrPoints + ", ");
                            if((x + 1) <= 4){
                                f_dflrePoints += (x + 1) * evaluationValues.dflrePoints;
                                evalInfo.append("dflrePoints: " + f_dflrePoints + ", ");
                            }
                            else{
                                f_dflrePoints += (5 - ((x + 1) - 4)) * evaluationValues.dflrePoints;
                                evalInfo.append("dflrePoints: " + f_dflrePoints + ", ");
                            }

                            if(currentFigure.canAttack){
                                f_attackingPoints += evaluationValues.attackingPoints;
                                evalInfo.append("attackingPoints: " + f_attackingPoints + ", ");
                            }
                            if(currentFigure.isProtected){
                                f_protectedPoints += evaluationValues.protectedPoints;
                                evalInfo.append("protectedPoints: " + f_protectedPoints + ", ");
                            }

                            if(y == 7){
                                f_winPoints += evaluationValues.winPoints;
                                evalInfo.append("winPoints: " + f_winPoints);
                                gameFinished = true;
                                winner = 'b';
                            }

                            bEval += f_winPoints;
                            bEval += f_figurePoints;
                            bEval += f_dfhrPoints;
                            bEval += f_dflrePoints;
                            bEval += f_moveCountPoints;
                            bEval += f_attackingPoints;
                            bEval += f_protectedPoints;
                        }
                    }
                }
            }
        }

        evaluationList.add(evalInfo.toString());

        // Zugsortierung
        ArrayList<Figure> moveableFigures;
        ArrayList<Figure> moveableFigures_opp;
        if(player == 'r'){
            moveableFigures = moveableFigures_red;
            moveableFigures_opp = moveableFigures_blue;
        }
        else{
            moveableFigures = moveableFigures_blue;
            moveableFigures_opp = moveableFigures_red;
        }

        for(Figure figure : moveableFigures){
            figure.calculatePossibleMoves();
            possibleMoves.addAll(figure.possibleMoves);
        }
        Collections.shuffle(possibleMoves);
        possibleMoves.sort((m1, m2) -> Integer.compare(m2.value, m1.value));

        ArrayList<Move> possibleMoves_opp = new ArrayList<>();
        for(Figure figure : moveableFigures_opp){
            figure.calculatePossibleMoves();
            possibleMoves_opp.addAll(figure.possibleMoves);
        }
        Collections.shuffle(possibleMoves_opp);
        possibleMoves_opp.sort((m1, m2) -> Integer.compare(m2.value, m1.value));

        // game finished because one side has no possible moves
        if(possibleMoves.isEmpty()){
            gameFinished = true;
            if(player == 'r'){
                winner = 'b';
                bEval += evaluationValues.winPoints;
            }
            else{
                winner = 'r';
                rEval += evaluationValues.winPoints;
            }
        }
        if(possibleMoves_opp.isEmpty()){
            gameFinished = true;
            if(player == 'r'){
                winner = 'r';
                rEval += evaluationValues.winPoints;
            }
            else{
                winner = 'b';
                bEval += evaluationValues.winPoints;
            }
        }


        if(player == 'r')
            return rEval - bEval;
        return bEval - rEval;
    }

    int evaluate(){
        Testing.zustaende++;
        return evaluation;
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
