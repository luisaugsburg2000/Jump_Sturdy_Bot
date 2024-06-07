import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Player extends Thread{
    Network network;
    char side;

    GameState gameState;
    boolean gameStarted;
    boolean waitingForOtherPlayer;

    public Player(char side){
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
                        System.out.println("game won :)");
                        break;
                    }
                    sleep(); continue;
                }
                else
                    gameStarted = true;
                if(currentSide != this.side){
                    if(!waitingForOtherPlayer)
                        System.out.println("waiting for other player...");
                    waitingForOtherPlayer = true;
                    sleep();
                    continue;
                }

                System.out.println();
                System.out.println("its my turn");
                waitingForOtherPlayer = false;

                GameManager.generateBoard(FEN);
                GameManager.printBoard(0);

                // calculate and execute move
                int maxThinkTime = 2000;
                Move move = calculateMoves(maxThinkTime);
                System.out.println("confirm move: " + move);
                Scanner in = new Scanner(System.in);
                String line = in.nextLine();
                switch(line){
                    case "FEN":
                        System.out.println(FEN);
                        break;
                    case "MOVES":
                        System.out.println("possible moves:");
                        move.figure.printPossibleMoves();
                        break;
                }
                String moveResult = executeMove(move);
//                System.out.println("confirmed:");
//                System.out.println(moveResult);

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
                if(move.figure.isOnTop())
                    System.out.println("JUMP");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            sleep();
        }
    }

    void sleep(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Move calculateMoves(int maxThinkTime){
//        ArrayList<Figure> figures = getFigures();
//
//        // select random figure
//        Random random = new Random();
//        Figure randomFigure = figures.get(random.nextInt(figures.size()));
//        randomFigure.calculatePossibleMoves();
//
//        if(randomFigure.possibleMoves.isEmpty())
//            Debug.noMoves(gameState.FEN, randomFigure);
//        int randomMoveIndex = random.nextInt(randomFigure.possibleMoves.size());
//        Move randomMove = randomFigure.possibleMoves.get(randomMoveIndex);
//
//        return randomMove;

        AlphaBeta alphaBeta = new AlphaBeta("1b04/1bb2brbb2/2bb5/3r0b03/2r02b01r0/1b02r0rr1b0/1rr2r03/6", 'r');
        alphaBeta.start();

        try {
            Thread.sleep(maxThinkTime);
            alphaBeta.stopExecution();
            alphaBeta.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return alphaBeta.results.get(alphaBeta.results.size() - 1).topMove;
    }

    String executeMove(Move move){
        String moveString = move.toString();
        return network.send("\"" + moveString + "\"");
    }

//    ArrayList<Figure> getFigures(){
//        ArrayList<Figure> result = new ArrayList<>();
//        for(Figure figure : GameManager.figures) {
//            if (figure.side == side & figure.canMove())
//                result.add(figure);
//        }
//        return result;
//    }
}
