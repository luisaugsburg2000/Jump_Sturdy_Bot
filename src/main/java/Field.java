import java.util.ArrayList;

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
        return (figure == null) & (topFigure == null);
    }
    public void initializeFigures(ArrayList<Figure> figures){
        if(figures.isEmpty())
            this.figure = null;
        else
            this.figure = figures.get(0);
        if(figures.size() < 2)
            this.topFigure = null;
        else
            this.topFigure = figures.get(1);
    }

    public String toString(){
        if(locked)
            return "" + coordinate + ": locked";
        else if(topFigure != null)
            return "" + coordinate + ": " + figure + topFigure;
        return "" + coordinate + ": " + figure;
    }
}
