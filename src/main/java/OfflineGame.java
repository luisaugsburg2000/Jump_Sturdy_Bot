public class OfflineGame {
    static int timePerPlayer = 120 * 1000;

    static OfflinePlayer blue = new OfflinePlayer('b',  timePerPlayer);
    static OfflinePlayer red = new OfflinePlayer('r',  timePerPlayer);

    static OfflinePlayer winner;
    static OfflinePlayer looser;

    public static void main(String[] args) {
        for(int i = 0; i < 10; i++){
            GameManager.generateBoard("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r0/r0r0r0r0r0r0");
            //GameManager.generateBoard("3b02/8/8/8/4r03/8/8/6");

            System.out.println("<<<<<<<<<< START ROUND " + i + " >>>>>>>>>>");
            System.out.println();

            EvaluationValues.nextGen_();
            blue.evaluationValues.nextGen();
            red.evaluationValues.nextGen();
            blue.evaluationValues.printCurrent(">>> BLUE GEN " + i);
            System.out.println();
            red.evaluationValues.printCurrent(">>> RED GEN " + i);
            System.out.println();

            blue.remainingTimeMillis = timePerPlayer;
            red.remainingTimeMillis = timePerPlayer;

            GameManager.printBoard();
            System.out.println();

            int x = 1;
            while (true) {
                if(i % 2 == 0){
                    if(blueMove())
                        break;
                    if(redMove())
                        break;
                }
                else{
                    if(redMove())
                        break;
                    if(blueMove())
                        break;
                }

                x++;
            }
            Testing.printPerformance();

            winner.evaluationValues.apply();
            looser.evaluationValues.revert();
        }

        EvaluationValues.generateCSV();
    }

    static boolean blueMove(){
        System.out.println("BLUE");
        blue.executeMove();
        GameManager.printBoard();
        System.out.println();
        GameState controlState = new GameState('b', true, 0, 0, blue.evaluationValues);
        if(controlState.gameFinished){
            System.out.println("GAME FINISHED");
            System.out.println("Winner: " + controlState.winner);
            winner = blue;
            looser = red;
            return true;
        }
        if(blue.remainingTimeMillis <= 0){
            System.out.println("GAME FINISHED");
            System.out.println("blue ran out of time");
            System.out.println("Winner: red");
            winner = red;
            looser = blue;
            return true;
        }
        //System.out.println("Transposition Table Size: " + Transposition.table.size());
        //Transposition.table.clear();
        return false;
    }
    static boolean redMove(){
        System.out.println("RED");
        red.executeMove();
        GameManager.printBoard();
        System.out.println();
        GameState controlState = new GameState('r', true, 0, 0, red.evaluationValues);
        if(controlState.gameFinished){
            System.out.println("GAME FINISHED");
            System.out.println("Winner: " + controlState.winner);
            winner = red;
            looser = blue;
            return true;
        }
        if(red.remainingTimeMillis <= 0){
            System.out.println("GAME FINISHED");
            System.out.println("red ran out of time");
            System.out.println("Winner: blue");
            winner = blue;
            looser = red;
            return true;
        }
        //System.out.println("Transposition Table Size: " + Transposition.table.size());
        //Transposition.table.clear();
        return false;
    }
}
