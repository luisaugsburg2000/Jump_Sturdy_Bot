import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Player extends Thread{
    GameManager gameManager;
    Network network;
    char side;

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
        gameManager = new GameManager();

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


                gameManager.initialize(FEN);
                gameManager.printBoard();

                // calculate and execute move
                Move move = calculateMoves();
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

                    Debug.invalidMove(gameManager.FEN, move.figure);

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

    Move calculateMoves(){
        ArrayList<Figure> figures = getFigures();

        // select random figure
        Random random = new Random();
        Figure randomFigure = figures.get(random.nextInt(figures.size()));
        randomFigure.calculatePossibleMoves();

        if(randomFigure.possibleMoves.isEmpty())
            Debug.noMoves(gameManager.FEN, randomFigure);
        int randomMoveIndex = random.nextInt(randomFigure.possibleMoves.size());
        Move randomMove = randomFigure.possibleMoves.get(randomMoveIndex);

        return randomMove;
    }

    String executeMove(Move move){
        String moveString = move.toString();
        return network.send("\"" + moveString + "\"");
    }

    ArrayList<Figure> getFigures(){
        ArrayList<Figure> result = new ArrayList<>();
        for(Figure figure : gameManager.figures) {
            if (figure.side == side & figure.canMove())
                result.add(figure);
        }
        return result;
    }
}
