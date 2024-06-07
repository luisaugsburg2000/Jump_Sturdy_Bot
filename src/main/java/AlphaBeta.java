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

    public AlphaBeta(String FEN, char player) {
        startFEN = FEN;
        startPlayer = player;
    }

    @Override
    public void run() {
        int d = 6; // d = 1
        while(d == 6){ // !stopped
            Instant start = Instant.now();
            GameManager.generateBoard(startFEN);
            GameState originState = new GameState(startPlayer, true, d);
            alpha_beta(originState, 0, d, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            System.out.println("Depth " + d + ": " + duration.toMillis() + " milliseconds");
            if(!stopped)
                results.add(originState);
            d++;
        }
    }

    void stopExecution(){
        stopped = true;
    }

    int alpha_beta(GameState gameState, int depth, int maxDepth, int alpha, int beta, boolean isMax) {
        boolean print = true;
        if(print){
            GameManager.print(gameState.FEN, depth);
            GameManager.printBoard(depth);
            if(isMax)
                GameManager.print(gameState.evaluation + "", depth);
            else
                GameManager.print(-gameState.evaluation + "", depth);
        }

        int v;
        if(depth == maxDepth | gameState.gameFinished | stopped){
            if(print)
                System.out.println();
            return gameState.evaluate();
        }


        // search in transposition table
        GameState transpositionState = Transposition.table.get(gameState.FEN);
        if(transpositionState != null & transpositionState != gameState){
            if(transpositionState.transpositionInfo.viewDepth >= (maxDepth - depth)){
                if(print){
                    GameManager.print("TRANSPOSITION", depth);
                    GameManager.print("View Depth: " + transpositionState.transpositionInfo.viewDepth, depth);
                    GameManager.print("Depth Eval: " + transpositionState.transpositionInfo.depthEvaluation, depth);
                    System.out.println();
                }
                return transpositionState.evaluate();
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
                GameState subGameState = new GameState(otherPlayer(gameState.player), false, maxDepth - (depth + 1));
                int evaluation = alpha_beta(subGameState, depth + 1, maxDepth, v, beta, false);
                if(evaluation > v){
                    gameState.topMove = move;
                    gameState.nextGameState = subGameState;

                    v = evaluation;
                }
                move.revert();
                if(v >= beta)
                    break;
            }
            //gameState.transpositionInfo.depthEvaluation = isMax ? v : -v;
            gameState.transpositionInfo.depthEvaluation = v;
            Transposition.table.put(gameState.FEN, gameState);
            if(print){
                GameManager.print("evaluation: " + v, depth);
                GameManager.print("best move: " + gameState.topMove + "", depth);
                System.out.println();
            }
            return v;
        }
        else{
            v = beta;
            for(Move move : gameState.possibleMoves){
                if(print)
                    GameManager.print("D" + (depth + 1) + "/" + move + " (" + gameState.player + ")", depth + 1);
                move.execute();
                GameState subGameState = new GameState(otherPlayer(gameState.player), true, maxDepth - (depth + 1));
                int evaluation = alpha_beta(subGameState, depth + 1, maxDepth, alpha, v, true);
                if(evaluation < v){
                    gameState.topMove = move;
                    gameState.nextGameState = subGameState;
                    v = evaluation;
                }

                move.revert();
                if(v <= alpha)
                    break;
            }

            //gameState.transpositionInfo.depthEvaluation = isMax ? v : -v;
            gameState.transpositionInfo.depthEvaluation = v;
            Transposition.table.put(gameState.FEN, gameState);
            if(print){
                GameManager.print("evaluation: " + v, depth);
                GameManager.print("best move: " + gameState.topMove + "", depth);
                System.out.println();
            }
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
