import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

public class Testing {
    static int zustaende;
    static float transpositionSearchTime = 0;
    static HashMap<Integer, LayerInfo> layerInfoMap = new HashMap();

    static class LayerInfo{
        int depth;
        int qty;
        long duration;
        int transpositionsUsed;

        LayerInfo(int depth, int qty, long duration, int transpositionsUsed){
            this.depth = depth;
            this.qty = qty;
            this.duration = duration;
            this.transpositionsUsed = transpositionsUsed;
        }
    }

    public static void main(String[] args) {
//        GameManager.generateBoard("6/1b02br3/6bb1/2b0b04/2r04r0/8/1rr1r0rr1r01/6 b");

        Instant start = Instant.now();

        // Bewertungsfunktion
//        for(int i = 0; i < 1000; i++){
//            new GameState('b', true);
//        }

        // Alpha Beta
        AlphaBeta alphaBeta = new AlphaBeta("6/8/8/3b04/3b04/8/2r01r03/6 b", 'b');
        alphaBeta.start();

        try {
//            Thread.sleep(maxThinkTime);
//            alphaBeta.stopExecution();
            alphaBeta.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Time Elapsed: " + duration.toMillis() + " milliseconds");
        System.out.println("Zustände: " + zustaende);
        System.out.println("Zustände/ms: " + ((float)zustaende / (float)duration.toMillis()));

        System.out.println(alphaBeta.results.get(alphaBeta.results.size() - 1).topMove);
        System.out.println();
        GameState g = alphaBeta.results.get(alphaBeta.results.size() - 1);
        Debugger.printMoves(g);
    }

    static void printPerformance(){
        for (Testing.LayerInfo value : Testing.layerInfoMap.values()) {
            double duration = round3(value.duration / (double)value.qty);
            double transpositionsUsed = round3((double)value.transpositionsUsed / (double)value.qty);
            System.out.println("Depth: " + value.depth + ", Reached: " + value.qty + ", Duration: " + duration + ", TranspositionsUsed: " + transpositionsUsed);
        }
    }

    static double round3(double value) {
        long factor = (long) Math.pow(10, 3);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
