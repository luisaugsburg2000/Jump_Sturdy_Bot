public class Move {
    Figure figure;
    Field origin;
    Field target;
    public MoveType moveType;
    public enum MoveType { Walk, Beat, Jump }

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
        if(origin.figure == figure)
            origin.figure = null;
        if(origin.topFigure == figure)
            origin.topFigure = null;

        // update figure
        figure.field = target;

        // place on new field
        if(target.figure != null){
            target.topFigure = figure;
        }
        else
            target.figure = figure;
    }

    public String toString(){
        return origin.coordinate + "-" + target.coordinate;
    }
}
