import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class OfflinePlayer {
    char side;

    public OfflinePlayer(char side) {
        this.side = side;
    }

    void executeMove(){
        //dummy();
        alpha_beta();
    }

    void dummy(){
        GameState currentGameState = new GameState(side, true, 0);

        Random random = new Random();
        Move randomMove = currentGameState.possibleMoves.get(random.nextInt(currentGameState.possibleMoves.size()));
        System.out.println(randomMove);
        randomMove.execute();
    }

    void alpha_beta(){
        String currentFEN = GameManager.generateFEN();
        AlphaBeta alphaBeta = new AlphaBeta(currentFEN, side);
        alphaBeta.start();
        try {
            Thread.sleep(2000);
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
    }
}
