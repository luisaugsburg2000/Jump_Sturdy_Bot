public class Coordinate {
    public int x;
    public int y;

    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Coordinate(String coordString){
        x = coordString.substring(0, 1).toUpperCase().charAt(0) - 65;
        y = Integer.parseInt(coordString.substring(1, 2)) - 1;
    }

    public boolean isValid(){
        if(x < 0 | x > 7)
            return false;
        if(y < 0 | y > 7)
            return false;   
        if((y == 0 & (x == 0 | x == 7)) | (y == 7 & (x == 0 | x == 7)))
            return false;

        return true;
    }

    public String toString(){
        return "" + ((char)(x + 65)) + (y + 1);
    }
}
