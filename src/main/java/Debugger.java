import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Debugger {
    public static void main(String[] args){
        System.out.println();
        System.out.println("START");
        System.out.println();

        //GameManager.generateBoard("3b02/3r01r02/8/8/8/8/8/2r03");
        //GameManager.generateBoard("6/8/4b03/3r01r02/2r05/8/8/6");
        //GameManager.generateBoard("6/2b05/1r02b03/3r01r02/8/8/8/6");
        //GameManager.generateBoard("3b03/8/2r05/5b02/4r03/8/8/6");

        Instant start = Instant.now();
        //AlphaBeta alphaBeta = new AlphaBeta("1b04/1bb2b0bb2/2bb1b03/3rr4/2r02b01r0/1b02r0rr2/1rr2r02b0/6", 'b');
        //AlphaBeta alphaBeta = new AlphaBeta("1b04/1bb2b0bb2/2bb1b03/3rr4/2r02b01r0/1b02r0rr1b0/1rr2r03/6", 'b');
        //AlphaBeta alphaBeta = new AlphaBeta("1b04/1bb2brbb2/2bb5/3r0b03/2r02b01r0/1b02r0rr1b0/1rr2r03/6", 'r');
        AlphaBeta alphaBeta = new AlphaBeta("1b04/1b02brbb2/2b0b04/3r0bb1r01/2r01r0b01r0/1b03r01b0/1rr2r03/6", 'r');
        //AlphaBeta alphaBeta = new AlphaBeta("3b02/8/2r05/5b02/4r03/8/8/6", 'b');
        //AlphaBeta alphaBeta = new AlphaBeta("6/4b03/2r02bb2/8/4r03/8/8/6", 'b');
        //AlphaBeta alphaBeta = new AlphaBeta("1b04/8/2r05/5r02/8/8/8/6", 'r');
        //AlphaBeta alphaBeta = new AlphaBeta("4b01/8/2r05/8/8/8/8/6", 'b');
        //AlphaBeta alphaBeta = new AlphaBeta("3b02/8/2r05/8/8/8/8/6", 'r');
        //AlphaBeta alphaBeta = new AlphaBeta("3b02/8/8/8/4r03/8/8/6", 'b');
        //AlphaBeta alphaBeta = new AlphaBeta("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r0/r0r0r0r0r0r0", 'b');
        alphaBeta.start();

        try {
            Thread.sleep(3000);
            System.out.println("time is up");
            alphaBeta.stopExecution();
            alphaBeta.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println();
        System.out.println("Results:");
        for (int i = 0; i < alphaBeta.results.size(); i++) {
            System.out.println("Depth: " + (i + 1) + ", Top Move: " + alphaBeta.results.get(i).topMove);
        }

        System.out.println();
        System.out.println("Moves:");
        GameState g = alphaBeta.results.get(alphaBeta.results.size() - 1);
        printMoves(g);

        System.out.println();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Time Elapsed: " + duration.toMillis() + " milliseconds");

        System.out.println("Transpositions: " + Transposition.table.size());
        System.out.println("Transposition search time: " + Testing.transpositionSearchTime);

        Testing.printPerformance();

//        GameState g2 = alphaBeta.results.get(0);
//        GameManager.generateBoard(g2.FEN);
//        GameManager.printBoard();
//        System.out.println("Evaluation " + g2.player + ":" + g2.evaluation);
//        for(String s : g2.evaluationList){
//            System.out.print(s);
//        }
    }

    static void printMoves(GameState gameState){
        while(true){
            //System.out.println(gameState.FEN);
            GameManager.generateBoard(gameState.FEN);
            GameManager.printBoard();
            System.out.println("FEN: " + gameState.FEN);
            System.out.println("Player: " + gameState.player);
            System.out.println("Evaluation: " + gameState.evaluation);
            System.out.println("Perspective: " + gameState.transpositionInfo.perspective);
            System.out.println("View Depth: " + gameState.transpositionInfo.viewDepth);
            System.out.println("Depth Evaluation: " + gameState.transpositionInfo.depthEvaluation);
            if(gameState.topMove != null)
                System.out.println(gameState.topMove);
            System.out.println();
            if(gameState.nextGameState == null)
                break;
            gameState = gameState.nextGameState;
        }
    }
}
