public class Field {
    Coordinate coordinate;
    public Coordinate getCoordinate(){ return coordinate; }
    boolean locked;
    public boolean isLocked(){ return locked; }
    Figure figure;
    public Figure getFigure(){ return figure; }
    Figure topFigure;
    public Figure getTopFigure(){ return topFigure; }

    public Field(int x, int y, boolean locked){
        coordinate = new Coordinate(x, y);
        this.locked = locked;
    }

    public boolean isEmpty(){
        return (figure != null) & (topFigure != null);
    }
    public void initializeFigures(Figure... figures){
        if(figures[0] != null)
            this.figure = figures[0];
        if(figures[1] != null)
            this.topFigure = figures[1];
    }

    public boolean figureAllowed(Figure figure){
        return true;
    }

    public String toString(){
        if(locked)
            return "" + coordinate + ": locked";
        else if(topFigure != null)
            return "" + coordinate + ": " + figure + topFigure;
        return "" + coordinate + ": " + figure;
    }
}
