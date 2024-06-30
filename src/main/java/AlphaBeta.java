import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlphaBeta extends Thread{
    String startFEN;
    char startPlayer;
    ArrayList<GameState> results = new ArrayList<>();
    boolean stopped;
    int moveIndex = 1;

    int transpositionsUsed;

    EvaluationValues evaluationValues;

    public AlphaBeta(String FEN, char player, EvaluationValues evaluationValues) {
        startFEN = FEN;
        startPlayer = player;
        this.evaluationValues = evaluationValues;
    }
    public AlphaBeta(String FEN, char player) {
        startFEN = FEN;
        startPlayer = player;
        this.evaluationValues = new EvaluationValues(player);
    }

    @Override
    public void run() {
        int d = 1; // d = 1
        while(!stopped){ // !stopped
            Instant start = Instant.now();
            transpositionsUsed = 0;
            GameManager.generateBoard(startFEN);
            GameState originState = new GameState(startPlayer, true, d, moveIndex, evaluationValues);
            alpha_beta(originState, 0, d, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            System.out.println("Depth " + d + ": " + duration.toMillis() + " millis, T: " + transpositionsUsed + ", TTS: " + Transposition.table.size() + ", Best Move: " + originState.topMove);
            if(!stopped){
                results.add(originState);
                Testing.LayerInfo layerInfoIndex = Testing.layerInfoMap.get(d);
                if(layerInfoIndex != null){
                    layerInfoIndex.qty++;
                    layerInfoIndex.duration += duration.toMillis();
                    layerInfoIndex.transpositionsUsed += transpositionsUsed;
                }
                else{
                    Testing.LayerInfo layerInfo = new Testing.LayerInfo(d, 1, duration.toMillis(), transpositionsUsed);
                    Testing.layerInfoMap.put(d, layerInfo);
                }
            }

            if(d >= 25)
                stopped = true;
            d++;
        }
    }

    void stopExecution(){
        stopped = true;
    }

    int alpha_beta(GameState gameState, int depth, int maxDepth, int alpha, int beta, boolean isMax) {
        Instant start = null;
        Instant end = null;

        boolean enableTranspositions = false;
        boolean print = false;
        if(print){
            GameManager.print(gameState.FEN, depth);
            GameManager.printBoard(depth);
            if(isMax)
                GameManager.print(gameState.evaluation + "", depth);
            else
                GameManager.print(-gameState.evaluation + "", depth);
        }

        int v;
        boolean containsAbortedSubstates = false;
        if(depth == maxDepth | gameState.gameFinished | stopped){
            if(print)
                System.out.println();
            if(stopped)
                gameState.transpositionInfo.aborted = true;
            return gameState.evaluate();
        }


        // search in transposition table
        if(enableTranspositions){
            GameState transpositionState = Transposition.table.get(gameState.FEN);
            if(transpositionState != null & transpositionState != gameState){
                if(transpositionState.transpositionInfo.viewDepth >= (maxDepth - depth)){
                    if(print){
                        GameManager.print("TRANSPOSITION", depth);
                        GameManager.print("View Depth: " + transpositionState.transpositionInfo.viewDepth, depth);
                        GameManager.print("Depth Eval: " + transpositionState.transpositionInfo.depthEvaluation, depth);
                        System.out.println();
                    }
                    transpositionsUsed++;
                    start = Instant.now();
                    gameState.topMove = transpositionState.topMove;
                    if(gameState.transpositionInfo.perspective == transpositionState.transpositionInfo.perspective)
                        return transpositionState.transpositionInfo.depthEvaluation;
                    else
                        return -transpositionState.transpositionInfo.depthEvaluation;
                }
            }
        }

        if(print)
            System.out.println();

        if(isMax){
            v = alpha;
            for(Move move : gameState.possibleMoves){
                if(print)
                    GameManager.print("D" + (depth + 1) + "/" + move + " (" + gameState.player + ")", depth + 1);
                move.execute();
                GameState subGameState = new GameState(otherPlayer(gameState.player), false, maxDepth - (depth + 1), moveIndex, evaluationValues);
                int evaluation = alpha_beta(subGameState, depth + 1, maxDepth, v, beta, false);
                if(evaluation > v | gameState.topMove == null){
                    gameState.topMove = move;
                    gameState.nextGameState = subGameState;
                    v = evaluation;
                }
                if(subGameState.transpositionInfo.aborted)
                    containsAbortedSubstates = true;
                move.revert();
                if(v >= beta)
                    break;
            }
            if(enableTranspositions & !containsAbortedSubstates){
                gameState.transpositionInfo.depthEvaluation = v;
                GameState existingState = Transposition.table.get(gameState.FEN);
                if(existingState != null){
                    if(gameState.transpositionInfo.viewDepth > existingState.transpositionInfo.viewDepth)
                        Transposition.table.put(gameState.FEN, gameState);
                }
                else
                    Transposition.table.put(gameState.FEN, gameState);
            }
            if(print){
                GameManager.print("evaluation: " + v, depth);
                GameManager.print("best move: " + gameState.topMove, depth);
                System.out.println();
            }
            end = Instant.now();
            if(start != null)
                Testing.transpositionSearchTime += Duration.between(start, end).toMillis();
            return v;
        }
        else{
            v = beta;
            for(Move move : gameState.possibleMoves){
                if(print)
                    GameManager.print("D" + (depth + 1) + "/" + move + " (" + gameState.player + ")", depth + 1);
                move.execute();
                GameState subGameState = new GameState(otherPlayer(gameState.player), true, maxDepth - (depth + 1), moveIndex, evaluationValues);
                int evaluation = alpha_beta(subGameState, depth + 1, maxDepth, alpha, v, true);
                if(evaluation < v | gameState.topMove == null){
                    gameState.topMove = move;
                    gameState.nextGameState = subGameState;
                    v = evaluation;
                }
                if(subGameState.transpositionInfo.aborted)
                    containsAbortedSubstates = true;
                move.revert();
                if(v <= alpha)
                    break;
            }
            if(enableTranspositions & !containsAbortedSubstates){
                gameState.transpositionInfo.depthEvaluation = v;
                GameState existingState = Transposition.table.get(gameState.FEN);
                if(existingState != null){
                    if(gameState.transpositionInfo.viewDepth > existingState.transpositionInfo.viewDepth)
                        Transposition.table.put(gameState.FEN, gameState);
                }
                else
                    Transposition.table.put(gameState.FEN, gameState);
            }
            if(print){
                GameManager.print("evaluation: " + v, depth);
                GameManager.print("best move: " + gameState.topMove, depth);
                System.out.println();
            }
            end = Instant.now();
            if(start != null)
                Testing.transpositionSearchTime += Duration.between(start, end).toMillis();
            return v;
        }
    }

    static char otherPlayer(char player){
        if(player == 'b')
            return 'r';
        else
            return 'b';
    }
}
