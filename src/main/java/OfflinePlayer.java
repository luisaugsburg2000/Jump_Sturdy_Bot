import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class OfflinePlayer {
    char side;
    int moveIndex = 1;
    int remainingTimeMillis;
    EvaluationValues evaluationValues;

    public OfflinePlayer(char side, int totalTimeMillis) {
        this.side = side;
        this.remainingTimeMillis = totalTimeMillis;
        evaluationValues = new EvaluationValues(side);
    }

    void executeMove(){
        //dummy();
        alpha_beta();
    }

    void dummy(){
        GameState currentGameState = new GameState(side, true, 0, 0, evaluationValues);

        Random random = new Random();
        Move randomMove = currentGameState.possibleMoves.get(random.nextInt(currentGameState.possibleMoves.size()));
        System.out.println(randomMove);
        randomMove.execute();
    }

    void alpha_beta(){
        Instant start = Instant.now();
        // calculate max think time
        int maxThinkTime = TimeManager.calculateTime(remainingTimeMillis);
        System.out.println("remaining time: " + remainingTimeMillis);
        System.out.println("max think time: " + maxThinkTime);

        String currentFEN = GameManager.generateFEN();
        AlphaBeta alphaBeta = new AlphaBeta(currentFEN, side, evaluationValues);
        alphaBeta.moveIndex = moveIndex;
        alphaBeta.start();
        try {
            Thread.sleep(maxThinkTime);
            System.out.println("time is up");
            alphaBeta.stopExecution();
            alphaBeta.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //GameManager.printBoard();
        GameManager.generateBoard(currentFEN);
        Move topMove = alphaBeta.results.get(alphaBeta.results.size() - 1).topMove;
        System.out.println(topMove + " @depth " + alphaBeta.results.size());
        topMove.execute();

        int prevSize = Transposition.table.size();
        System.out.println("Transposition table size: " + prevSize);
        Transposition.table.entrySet().removeIf(entry -> ((moveIndex - entry.getValue().transpositionInfo.moveIndex)) >= 3);
        Iterator<Map.Entry<String, GameState>> iterator = Transposition.table.entrySet().iterator();

        System.out.println("Transpositions deleted: " + (prevSize - Transposition.table.size()));
        moveIndex++;

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        remainingTimeMillis -= duration.toMillis();
    }
}
