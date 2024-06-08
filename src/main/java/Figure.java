import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Figure {
    char side;
    Field field;
    ArrayList<Move> possibleMoves = new ArrayList<>();

    public Figure(char side, Field field) {
        this.side = side;
        this.field = field;
    }

    boolean canMove(){
        return (field.topFigure == null) | (field.topFigure == this);
    }
    boolean isOnTop(){
        return (field.topFigure == this);
    }

    public String toString(){
        return Character.toString(side);
    }

    void calculatePossibleMoves(){
        possibleMoves.clear();

        if(canMove()){
            if(!isOnTop()) {
                // walk
                walkForward();
                walkLeft();
                walkRight();
                // beat
                beatLeftFwd();
                beatRightFwd();
            }

            // jump
            if(isOnTop()){
                jumpLeft();
                jumpLeftFwd();
                jumpRight();
                jumpRightFwd();
            }
        }
    }

    public void printPossibleMoves(){
        for(Move move : possibleMoves){
            System.out.println(move);
        }
    }

    private void checkCoordinates(Coordinate coordinate, Move.MoveType moveType, int value){
        if(coordinate.isValid()){
            Field newField = GameManager.getField(coordinate);
            if(newField.topFigure != null){
                if(newField.topFigure.side != side)
                    value += 10;
            }
            else if(newField.figure != null){
                if(newField.figure.side != side)
                    value += 10;
            }
            Move move = new Move(this, field, newField, moveType, value);
            if(move.isAllowed())
                possibleMoves.add(move);
        }
    }

    private void walkForward(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b')
            newCoordinate.y += 1;
        if(side == 'r')
            newCoordinate.y -= 1;
        checkCoordinates(newCoordinate, Move.MoveType.Walk, 1);
    }
    private void walkLeft(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b')
            newCoordinate.x -= 1;
        if(side == 'r')
            newCoordinate.x += 1;
        checkCoordinates(newCoordinate, Move.MoveType.Walk, 0);
    }
    private void walkRight(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b')
            newCoordinate.x += 1;
        if(side == 'r')
            newCoordinate.x -= 1;
        checkCoordinates(newCoordinate, Move.MoveType.Walk, 0);
    }
    private void beatLeftFwd(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b'){
            newCoordinate.x -= 1;
            newCoordinate.y += 1;
        }
        if(side == 'r'){
            newCoordinate.x += 1;
            newCoordinate.y -= 1;
        }
        checkCoordinates(newCoordinate, Move.MoveType.Beat, 1);
    }
    private void beatRightFwd(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b'){
            newCoordinate.x += 1;
            newCoordinate.y += 1;
        }
        if(side == 'r'){
            newCoordinate.x -= 1;
            newCoordinate.y -= 1;
        }
        checkCoordinates(newCoordinate, Move.MoveType.Beat, 1);
    }
    private void jumpLeft(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b'){
            newCoordinate.x -= 2;
            newCoordinate.y += 1;
        }
        if(side == 'r'){
            newCoordinate.x += 2;
            newCoordinate.y -= 1;
        }
        checkCoordinates(newCoordinate, Move.MoveType.Jump, 1);
    }
    private void jumpLeftFwd(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b'){
            newCoordinate.x -= 1;
            newCoordinate.y += 2;
        }
        if(side == 'r'){
            newCoordinate.x += 1;
            newCoordinate.y -= 2;
        }
        checkCoordinates(newCoordinate, Move.MoveType.Jump, 2);
    }
    private void jumpRight(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b'){
            newCoordinate.x += 2;
            newCoordinate.y += 1;
        }
        if(side == 'r'){
            newCoordinate.x -= 2;
            newCoordinate.y -= 1;
        }
        checkCoordinates(newCoordinate, Move.MoveType.Jump, 1);
    }
    private void jumpRightFwd(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b'){
            newCoordinate.x += 1;
            newCoordinate.y += 2;
        }
        if(side == 'r'){
            newCoordinate.x -= 1;
            newCoordinate.y -= 2;
        }
        checkCoordinates(newCoordinate, Move.MoveType.Jump, 2);
    }
}
