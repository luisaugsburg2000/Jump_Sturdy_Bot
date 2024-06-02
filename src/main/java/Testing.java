import java.time.Duration;
import java.time.Instant;

public class Testing {
    public static void main(String[] args) {
        GameManager.generateBoard("b0b01b02/3bbb0bb2/2b03bb1/8/2b01r03/5r02/1rr1r0rr1rr1/1rr4 b");

        Instant start = Instant.now();

        for(int i = 0; i < 1000; i++){
            new GameState('b', true);
        }

        System.out.println();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Time Elapsed: " + duration.toMillis() + " milliseconds");
    }
}
