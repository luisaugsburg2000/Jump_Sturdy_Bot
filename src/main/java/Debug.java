public class Debug {
    static void noMoves(String FEN, Figure figure){
        System.out.println("DEBUG 0 possible moves:");
        System.out.println(FEN);
        debugFigure(figure);
    }
    static void invalidMove(String FEN, Figure figure){
        System.out.println("DEBUG invalid move:");
        System.out.println(FEN);
        debugFigure(figure);
    }

    static void debugFigure(Figure figure){
        System.out.println("side: " + figure);
        System.out.println("field: " + figure.field);
        System.out.println("can move: " + figure.canMove());
        System.out.println("is on top: " + figure.isOnTop());
    }
}
