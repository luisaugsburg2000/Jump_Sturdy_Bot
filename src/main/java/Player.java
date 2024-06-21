import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class Player extends Thread{
    Network network;
    char side;

    GameState gameState;
    boolean gameStarted;
    boolean waitingForOtherPlayer;

    int moveIndex = 1;

    public Player(char side){
        new GUI();

        network = new Network();
        if(network.p.charAt(0) == '0')
            this.side = 'r';
        else
            this.side = 'b';
    }

    public static void main(String[] args){
        Player p = new Player('s');
        p.execute();
    }

    void execute(){
        System.out.println("Side: " + side);

        while(true){
            //System.out.println(".");
            String jsonString = network.send("\"get\"");
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(jsonString);
                boolean bothConnected = jsonNode.get("bothConnected").asBoolean();
                String FEN = jsonNode.get("board").asText();
                char currentSide = FEN.charAt(FEN.length() - 1);

                if(!bothConnected){
                    if(gameStarted){
                        System.out.println("game finished");
                        break;
                    }
                    continue;
                }
                else
                    gameStarted = true;
                if(currentSide != this.side){
                    if(!waitingForOtherPlayer)
                        System.out.println("waiting for other player...");
                    waitingForOtherPlayer = true;
                    continue;
                }

                System.out.println();
                System.out.println("its my turn");
                waitingForOtherPlayer = false;

                GameManager.generateBoard(FEN);
                GameManager.printBoard(0);

                // calculate and execute move
                int maxThinkTime = 3000;
                Move move = calculateMoves(maxThinkTime);

                // confirm move
//                System.out.println("confirm move: " + move);
//                Scanner in = new Scanner(System.in);
//                String line = in.nextLine();

//                switch(line){
//                    case "FEN":
//                        System.out.println(FEN);
//                        break;
//                    case "MOVES":
//                        System.out.println("possible moves:");
//                        move.figure.printPossibleMoves();
//                        break;
//                }

                String moveResult = executeMove(move);

                // check if move was valid
                JsonNode jsonNode_confirm = objectMapper.readTree(moveResult);
                boolean bothConnected_confirm = jsonNode_confirm.get("bothConnected").asBoolean();
                String FEN_confirm = jsonNode_confirm.get("board").asText();
                char currentSide_confirm = FEN_confirm.charAt(FEN_confirm.length() - 1);
                if(!bothConnected_confirm){
                    System.out.println("invalid move");
                    System.out.println("game lost :(");
                    Debug.invalidMove(gameState.FEN, move.figure);

                    break;
                }
//                if(currentSide_confirm == this.side)
//                    System.out.println("invalid move");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }
    }

    Move calculateMoves(int maxThinkTime){
        //return dummy();
        return alpha_beta(maxThinkTime);
    }

    Move dummy(){
        GameState currentGameState = new GameState(side, true, 0, 0);

        Random random = new Random();
        Move randomMove = currentGameState.possibleMoves.get(random.nextInt(currentGameState.possibleMoves.size()));
        System.out.println(randomMove);
        //randomMove.execute();
        return randomMove;
    }

    Move alpha_beta(int maxThinkTime){
        String currentFEN = GameManager.generateFEN();
        AlphaBeta alphaBeta = new AlphaBeta(currentFEN, side);
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
        GameManager.generateBoard(currentFEN);
        Move topMove = alphaBeta.results.get(alphaBeta.results.size() - 1).topMove;
        System.out.println(topMove + " @depth " + alphaBeta.results.size());
        //topMove.execute();

        int prevSize = Transposition.table.size();
        System.out.println("Transposition table size: " + prevSize);
        Transposition.table.entrySet().removeIf(entry -> ((moveIndex - entry.getValue().transpositionInfo.moveIndex)) >= 3);
        Iterator<Map.Entry<String, GameState>> iterator = Transposition.table.entrySet().iterator();

        System.out.println("Transpositions deleted: " + (prevSize - Transposition.table.size()));
        moveIndex++;

        if(topMove != null)
            return topMove;
        return dummy();
    }

    String executeMove(Move move){
        String moveString = move.toString();
        return network.send("\"" + moveString + "\"");
    }
}
