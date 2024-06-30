public class TimeManager {
    public static void main(String[] args) {
        //System.out.println(evaluateGraph(10));

        System.out.println();
        for(int i = 24; i > 0; i--){
            System.out.println(i + ": " + calculateTime(2 * 60 * 1000, i));
        }
    }

    static int calculateTime(int remainingTime, int figureCount){
        float maxFraction = ((float)remainingTime) / 25f; // maximum relative fraction of remaining time to be used
        //return (int)(evaluateGraph(countFigures()) * maxFraction);
        return Math.round(evaluateGraph(figureCount) * maxFraction);
    }

    static int countFigures(){
        int figureCount = 0;
        for(int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Field currentField = GameManager.board[y][x];
                if(currentField.figure != null)
                    figureCount++;
                if(currentField.topFigure != null)
                    figureCount++;
            }
        }
        return figureCount;
    }

    static float evaluateGraph(int figureCount){
        float startFigureCount = 24;
        float startThinkTime = 0.5f;
        float criticalFigureCount = 18;
        float criticalThinkTime = 1f;

        float range1 = criticalFigureCount - startFigureCount;
        float range2 = 0f - criticalFigureCount;

        float t = (float) figureCount;
        float t_rel;
        if(t >= criticalFigureCount){
            t_rel = (t - startFigureCount) / range1;
            //System.out.println(figureCount + ": " + t_rel);
            return lerp(startThinkTime, criticalThinkTime, t_rel);
        }
        else{
            t_rel = (t - criticalFigureCount) / range2;
            //System.out.println(figureCount + ": " + t_rel);
            return lerp(criticalThinkTime, 0f, t_rel);
        }
    }

    static float lerp(float a, float b, float t) {
        float res = a + t * (b - a);
        //System.out.println("    " + res);
        return res;
    }
}
