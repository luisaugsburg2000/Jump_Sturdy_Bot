public class OfflineGame {
    public static void main(String[] args) {
        OfflinePlayer blue = new OfflinePlayer('b');
        OfflinePlayer red = new OfflinePlayer('r');
        GameManager.generateBoard("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r0/r0r0r0r0r0r0");
        GameState controlState;

        System.out.println("START");
        GameManager.printBoard();
        System.out.println();

        while (true) {
            System.out.println("BLUE");
            blue.executeMove();
            GameManager.printBoard();
            System.out.println();
            controlState = new GameState('b', true, 0);
            if(controlState.gameFinished){
                System.out.println("GAME FINISHED");
                System.out.println("Winner: " + controlState.winner);
                break;
            }

            System.out.println("RED");
            red.executeMove();
            GameManager.printBoard();
            System.out.println();
            controlState = new GameState('r', true, 0);
            if(controlState.gameFinished){
                System.out.println("GAME FINISHED");
                System.out.println("Winner: " + controlState.winner);
                break;
            }
        }
    }
}
