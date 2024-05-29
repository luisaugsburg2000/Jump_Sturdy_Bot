import java.util.ArrayList;

public class Figure {
    public char side;
    public Field field;
    public ArrayList<Move> possibleMoves = new ArrayList<>();

    public Figure(String side, Field field){
        this.side = side.charAt(0);
        this.field = field;
        GameManager.instance.figures.add(this);
    }
    public Figure(char side, Field field){
        this.side = side;
        this.field = field;
        GameManager.instance.figures.add(this);
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

    public void calculatePossibleMoves(){
        possibleMoves.clear();

        if(canMove()){
            // walk
            if(!isOnTop()) {
                walkForward();
                walkLeft();
                walkRight();
            }

            // beat
            beatLeftFwd();
            beatRightFwd();

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

    void checkCoordinates(Coordinate coordinate, Move.MoveType moveType){
        if(coordinate.isValid()){
            Field newField = GameManager.instance.getField(coordinate);
            Move move = new Move(this, field, newField, moveType);
            if(move.isAllowed())
                possibleMoves.add(move);
        }
    }

    void walkForward(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b')
            newCoordinate.y += 1;
        if(side == 'r')
            newCoordinate.y -= 1;
        checkCoordinates(newCoordinate, Move.MoveType.Walk);
    }
    void walkLeft(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b')
            newCoordinate.x -= 1;
        if(side == 'r')
            newCoordinate.x += 1;
        checkCoordinates(newCoordinate, Move.MoveType.Walk);
    }
    void walkRight(){
        Coordinate newCoordinate = new Coordinate(field.coordinate.x, field.coordinate.y);
        if(side == 'b')
            newCoordinate.x += 1;
        if(side == 'r')
            newCoordinate.x -= 1;
        checkCoordinates(newCoordinate, Move.MoveType.Walk);
    }
    void beatLeftFwd(){
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
    void beatRightFwd(){
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
    void jumpLeft(){
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
    void jumpLeftFwd(){
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
    void jumpRight(){
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
    void jumpRightFwd(){
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
