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

    private void checkCoordinates(Coordinate coordinate, Move.MoveType moveType){
        if(coordinate.isValid()){
            Field newField = GameManager.getField(coordinate);
            Move move = new Move(this, field, newField, moveType);
            if(move.isAllowed())
                possibleMoves.add(move);

            //String move_ = Coord
        }
    }

    private void walkForward(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b')
            newCoordinate.y += 1;
        if(side == 'r')
            newCoordinate.y -= 1;
        checkCoordinates(newCoordinate, Move.MoveType.Walk);
    }
    private void walkLeft(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b')
            newCoordinate.x -= 1;
        if(side == 'r')
            newCoordinate.x += 1;
        checkCoordinates(newCoordinate, Move.MoveType.Walk);
    }
    private void walkRight(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b')
            newCoordinate.x += 1;
        if(side == 'r')
            newCoordinate.x -= 1;
        checkCoordinates(newCoordinate, Move.MoveType.Walk);
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
        checkCoordinates(newCoordinate, Move.MoveType.Beat);
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
        checkCoordinates(newCoordinate, Move.MoveType.Beat);
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
        checkCoordinates(newCoordinate, Move.MoveType.Jump);
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
        checkCoordinates(newCoordinate, Move.MoveType.Jump);
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
        checkCoordinates(newCoordinate, Move.MoveType.Jump);
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
        checkCoordinates(newCoordinate, Move.MoveType.Jump);
    }
}
