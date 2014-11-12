import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Snakes on a square
 */
public class Solver {

    ExecutorService executor = Executors.newFixedThreadPool(32);
    SquareSnakeSolver sSolver;
    private SnakeResult best = null;

    private double checked = 0;

    public static void main(String[] args) {

        Solver solver = new Solver();
        solver.solve();
    }


    private void solve() {

        sSolver = new SquareSnakeSolver();

        //We start the Snake with a single direction, checking the other direction is not necessary
        SnakeResult start = new SnakeResult();
        start.setDirectionString("L");
        //already found one with a score
        start.setSquareSide(Integer.MAX_VALUE);

        best = start;

        //Let the snake grow
        try {
            process(start);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Takes the current snake and create a new two headed snake.
     * For each valid snake it will do this again and again until we reach the desired length of 669 characters
     * @param snakeResult the Snake to grow
     */
    private void process(SnakeResult snakeResult) throws ExecutionException, InterruptedException {

//        System.out.println(snakeResult.getSnake());
        final String a = snakeResult.getDirectionString() + "L";
        final String b = snakeResult.getDirectionString() + "R";

//        Future<SnakeResult> futureA = executor.submit(new Callable<SnakeResult>() {
//            public SnakeResult call() {
////                return sSolver.solve(a, 669);
//            }});
//
//        Future<SnakeResult> futureB = executor.submit(new Callable<SnakeResult>() {
//            public SnakeResult call() {
////                return sSolver.solve(b, 669);
//            }});
//
////        SnakeResult resultA = sSolver.solve(a);
////        SnakeResult resultB = sSolver.solve(b);
//
////        handleResult(resultA);
////        handleResult(resultB);
//
//        handleResult(futureA.get());
//        handleResult(futureB.get());

    }


    public void handleResult(SnakeResult result) throws ExecutionException, InterruptedException {
        //when the result is valid we continue to let the snake grow
//        double old = checked;

        if(result.isValid() && result.getSquareSide() < best.getSquareSide()) {
            if(result.getSize() < 669) {
                process(result);
            }
            else {
                //valid result
//                checked = checked + 1;
                if(result.getSquareSide() < best.getSquareSide()) {
                    //found a new better result
                    best = result;
                    Date now = new Date();
                    System.out.println(now.toString() + "/ " + best.getSquareSide() + "/" + best.getDirectionString());
                    writeResult(result);
                }
            }
        }
        else {
            //not valid or the result was already not smaller then the best - add the branches we will no longer investigate to the checked
//            int power = 669 - (result.getSize());
//            checked = checked + Math.pow(2, power);
        }


//        if(old != checked) {
//            System.out.println(checked);
//        }


    }

    public void writeResult(SnakeResult result) {
        //write the result to the file so we don't forgot the result
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("results.txt", true))) {
            bw.newLine();
            Date now = new Date();
            bw.append(now.toString()).append(" / ").append(String.valueOf(result.getSquareSide())).append(" / ").append(result.getDirectionString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
