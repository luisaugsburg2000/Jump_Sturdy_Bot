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
        int d = 1;
        while(!stopped){
            Instant start = Instant.now();
            GameManager.generateBoard(startFEN);
            GameState originState = new GameState(startPlayer, true);
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
        boolean print = false;
        if(depth >= 3)
            print = false;
        //print = false;
        if(print){
            GameManager.print(gameState.FEN, depth);
            GameManager.printBoard(depth);
            if(isMax)
                GameManager.print(gameState.evaluation + "", depth);
            else
                GameManager.print(-gameState.evaluation + "", depth);
            System.out.println();
        }

        int v;
        if(depth == maxDepth | gameState.gameFinished | stopped)
            return gameState.evaluation;
        if(isMax){
            v = alpha;
            for(Move move : gameState.possibleMoves){
                if(print)
                    GameManager.print("D" + (depth + 1) + "/" + move, depth + 1);
                move.execute();
                GameState subGameState = new GameState(otherPlayer(gameState.player), false);
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
                    GameManager.print("D" + (depth + 1) + "/" + move, depth + 1);
                move.execute();
                GameState subGameState = new GameState(otherPlayer(gameState.player), true);
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
