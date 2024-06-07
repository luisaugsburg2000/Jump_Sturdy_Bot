import java.util.Objects;

public class Move {
    Figure figure;
    Field origin;
    Field target;
    MoveType moveType;
    enum MoveType { Walk, Beat, Jump }

    boolean executed;
    Figure destroyedFigure;

    public Move(Figure figure, Field origin, Field target, MoveType moveType){
        this.figure = figure;
        this.origin = origin;
        this.target = target;
        this.moveType = moveType;
    }

    public boolean isAllowed(){
        if(moveType == MoveType.Walk){
            if(target.figure == null)
                return true;
            if(target.figure.side == figure.side & target.topFigure == null)
                return true;
            return false;
        }
        else if(moveType == MoveType.Beat){
            if(target.topFigure != null){
                if(target.topFigure.side != figure.side)
                    return true;
            }
            else if(target.figure != null){
                if(target.figure.side != figure.side)
                    return true;
            }
            return false;
        }
        else if(moveType == MoveType.Jump){
            if(target.figure == null)
                return true;
            if(target.topFigure == null)
                return true;
            if(target.topFigure.side != figure.side)
                return true;
            return false;
        }
        return false;
    }

    public void execute(){
        // remove from old field
//        if(origin.figure == figure)
//            origin.figure = null;
//        else if(origin.topFigure == figure)
//            origin.topFigure = null;
        if(origin.topFigure != null)
            origin.topFigure = null;
        else if (origin.figure != null)
            origin.figure = null;

        // update figure
        figure.field = target;

        // place on new field
        if(moveType == MoveType.Walk){
            if(target.figure != null)
                target.topFigure = figure;
            else
                target.figure = figure;
        }
        if(moveType == MoveType.Beat){
            if(target.topFigure != null){
                destroyedFigure = target.topFigure;
                target.topFigure = figure;
            }
            else if(target.figure != null){
                destroyedFigure = target.figure;
                target.figure = figure;
            }
        }
        if(moveType == MoveType.Jump){
            if(target.topFigure != null){
                destroyedFigure = target.topFigure;
                target.topFigure = figure;
            }
            else if(target.figure != null)
                target.topFigure = figure;
            else
                target.figure = figure;
        }

        executed = true;
    }
    public void execute(boolean p){
        System.out.println("FIG: " + origin.figure);
        System.out.println("T-FIG: " + origin.topFigure);

        // remove from old field
        if(origin.figure == figure){
            origin.figure = null;
            System.out.println("found figure");
        }
        else if(origin.topFigure == figure){
            origin.topFigure = null;
            System.out.println("found topFigure");
        }

        System.out.println("FIG: " + origin.figure);
        System.out.println(figure.field);
        System.out.println("T-FIG: " + origin.topFigure);

        // update figure
        figure.field = target;

        // place on new field
        if(moveType == MoveType.Walk){
            if(target.figure != null)
                target.topFigure = figure;
            else
                target.figure = figure;
        }
        if(moveType == MoveType.Beat){
            if(target.topFigure != null){
                destroyedFigure = target.topFigure;
                target.topFigure = figure;
            }
            else if(target.figure != null){
                destroyedFigure = target.figure;
                target.figure = figure;
            }
        }
        if(moveType == MoveType.Jump){
            if(target.topFigure != null){
                destroyedFigure = target.topFigure;
                target.topFigure = figure;
            }
            else if(target.figure != null)
                target.topFigure = figure;
            else
                target.figure = figure;
        }

        executed = true;
    }
    public void revert(){
        if(executed){
            // remove figure from target
            if(target.figure == figure)
                target.figure = null;
            else if(target.topFigure == figure)
                target.topFigure = null;

            // update figure
            figure.field = origin;

            // place figure back to origin
            if(origin.figure != null)
                origin.topFigure = figure;
            else
                origin.figure = figure;

            // respawn destroyed figure
            if(destroyedFigure != null){
                if(target.figure == null)
                    target.figure = destroyedFigure;
                else if(target.topFigure == null)
                    target.topFigure = destroyedFigure;
            }

            executed = false;
        }
    }

    public String toString(){
        return origin.coordinate + "-" + target.coordinate;
    }
}
