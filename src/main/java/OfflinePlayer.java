import java.util.*;

public class OfflinePlayer {
    char side;
    int moveIndex = 1;

    public OfflinePlayer(char side) {
        this.side = side;
    }

    void executeMove(){
        //dummy();
        alpha_beta();
    }

    void dummy(){
        GameState currentGameState = new GameState(side, true, 0, 0);

        Random random = new Random();
        Move randomMove = currentGameState.possibleMoves.get(random.nextInt(currentGameState.possibleMoves.size()));
        System.out.println(randomMove);
        randomMove.execute();
    }

    void alpha_beta(){
        String currentFEN = GameManager.generateFEN();
        AlphaBeta alphaBeta = new AlphaBeta(currentFEN, side);
        alphaBeta.moveIndex = moveIndex;
        alphaBeta.start();
        try {
            Thread.sleep(3000);
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
//        while (iterator.hasNext()) {
//            Map.Entry<String, GameState> entry = iterator.next();
//            if (((moveIndex - entry.getValue().transpositionInfo.moveIndex)) >= 2) {
//                iterator.remove();
//            }
//        }

        System.out.println("Transpositions deleted: " + (prevSize - Transposition.table.size()));
        moveIndex++;
    }
}
