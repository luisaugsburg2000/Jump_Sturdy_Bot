import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class EvaluationValues {
    char side;

    int winPoints = 1000000;
    int figurePoints = 100;
    int dfhrPoints = 10;        // distance from home row
    int dflrePoints = 5;        // distance from left / right edge
    int moveCountPoints = 10;
    int attackingPoints = 50;   // if figure could beat opponent figure
    int protectedPoints = 50;   // if figure is protected by other figure

    int winPoints_c;
    int figurePoints_c;
    int dfhrPoints_c;
    int dflrePoints_c;
    int moveCountPoints_c;
    int attackingPoints_c;
    int protectedPoints_c;

    static ArrayList<Integer> generations = new ArrayList<>();

    static ArrayList<Integer> winPoints_arr_b = new ArrayList<>();
    static ArrayList<Integer> figurePoints_arr_b = new ArrayList<>();
    static ArrayList<Integer> dfhrPoints_arr_b = new ArrayList<>();
    static ArrayList<Integer> dflrePoints_arr_b = new ArrayList<>();
    static ArrayList<Integer> moveCountPoints_arr_b = new ArrayList<>();
    static ArrayList<Integer> attackingPoints_arr_b = new ArrayList<>();
    static ArrayList<Integer> protectedPoints_arr_b = new ArrayList<>();

    static ArrayList<Integer> winPoints_arr_r = new ArrayList<>();
    static ArrayList<Integer> figurePoints_arr_r = new ArrayList<>();
    static ArrayList<Integer> dfhrPoints_arr_r = new ArrayList<>();
    static ArrayList<Integer> dflrePoints_arr_r = new ArrayList<>();
    static ArrayList<Integer> moveCountPoints_arr_r = new ArrayList<>();
    static ArrayList<Integer> attackingPoints_arr_r = new ArrayList<>();
    static ArrayList<Integer> protectedPoints_arr_r = new ArrayList<>();

    EvaluationValues(char side){
        this.side = side;
    }

    // apply
    void apply(){
        winPoints = winPoints_c;
        figurePoints = figurePoints_c;
        dfhrPoints = dfhrPoints_c;
        dflrePoints = dflrePoints_c;
        moveCountPoints = moveCountPoints_c;
        attackingPoints = attackingPoints_c;
        protectedPoints = protectedPoints_c;
    }
    // revert
    void revert(){
        winPoints_c = winPoints;
        figurePoints_c = figurePoints;
        dfhrPoints_c = dfhrPoints;
        dflrePoints_c = dflrePoints;
        moveCountPoints_c = moveCountPoints;
        attackingPoints_c = attackingPoints;
        protectedPoints_c = protectedPoints;
    }
    // nextGen
    static void nextGen_(){
        generations.add(generations.size() + 1);
    }
    void nextGen(){
        winPoints_c = winPoints;
        figurePoints_c = figurePoints + random(20);
        dfhrPoints_c = dfhrPoints + random(2);
        dflrePoints_c = dflrePoints + random(1);
        moveCountPoints_c = moveCountPoints + random(2);
        attackingPoints_c = attackingPoints + random(10);
        protectedPoints_c = protectedPoints + random(10);


        if(side == 'b'){
            winPoints_arr_b.add(winPoints_c);
            figurePoints_arr_b.add(figurePoints_c);
            dfhrPoints_arr_b.add(dfhrPoints_c);
            dflrePoints_arr_b.add(dflrePoints_c);
            moveCountPoints_arr_b.add(moveCountPoints_c);
            attackingPoints_arr_b.add(attackingPoints_c);
            protectedPoints_arr_b.add(protectedPoints_c);
        }
        if(side == 'r'){
            winPoints_arr_r.add(winPoints_c);
            figurePoints_arr_r.add(figurePoints_c);
            dfhrPoints_arr_r.add(dfhrPoints_c);
            dflrePoints_arr_r.add(dflrePoints_c);
            moveCountPoints_arr_r.add(moveCountPoints_c);
            attackingPoints_arr_r.add(attackingPoints_c);
            protectedPoints_arr_r.add(protectedPoints_c);
        }
    }

    int random(int range){
        Random rand = new Random();
        return rand.nextInt((range * 2) + 1) - range;
    }

    void print(String side){
        System.out.println(side);
        System.out.println("winPoints: " + winPoints);
        System.out.println("figurePoints: " + figurePoints);
        System.out.println("dfhrPoints: " + dfhrPoints);
        System.out.println("dflrePoints: " + dflrePoints);
        System.out.println("moveCountPoints: " + moveCountPoints);
        System.out.println("attackingPoints: " + attackingPoints);
        System.out.println("protectedPoints: " + protectedPoints);
    }

    static void generateCSV(){
        String csvFile = "evaluation.csv";

        try (FileWriter writer = new FileWriter(csvFile)) {
            writeLine("Generation", generations, writer);

            writeLine("B win", winPoints_arr_b, writer);
            writeLine("B fig", figurePoints_arr_b, writer);
            writeLine("B dfhr", dfhrPoints_arr_b, writer);
            writeLine("B dflre", dflrePoints_arr_b, writer);
            writeLine("B moveCount", moveCountPoints_arr_b, writer);
            writeLine("B attacking", attackingPoints_arr_b, writer);
            writeLine("B protected", protectedPoints_arr_b, writer);

            writeLine("R win", winPoints_arr_r, writer);
            writeLine("R fig", figurePoints_arr_r, writer);
            writeLine("R dfhr", dfhrPoints_arr_r, writer);
            writeLine("R dflre", dflrePoints_arr_r, writer);
            writeLine("R moveCount", moveCountPoints_arr_r, writer);
            writeLine("R attacking", attackingPoints_arr_r, writer);
            writeLine("R protected", protectedPoints_arr_r, writer);

            System.out.println("CSV-Datei wurde erfolgreich erstellt.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void writeLine(String title, ArrayList<Integer> list, FileWriter writer) throws IOException {
        writer.append(title + ";");
        for(int i = 0; i < list.size(); i++)
            writer.append(list.get(i) + ";");
        writer.append("\n");
    }
}
