import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Snakes on a square
 */
public class EvoSolver {

    ExecutorService executor = Executors.newFixedThreadPool(4);
    SquareSnakeSolver sSolver;
    List<SnakeResult> results;
    private int growSize = 2;
    private int populationSize = 500;
    private Date startDate;

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        EvoSolver solver = new EvoSolver();
        solver.solve();

        System.exit(0);
    }


    private void solve() throws ExecutionException, InterruptedException {

        results = new ArrayList<>(populationSize);

        //date for performance concerns
        startDate = new Date();

        sSolver = new SquareSnakeSolver();

        //We start the Snake with a single direction, checking the other direction is not necessary (LLRRL == RRLLR)
        SnakeResult start = new SnakeResult();
        start.setSnake("L");
        start.setValid(true);
        start.setSize(1);

        //add this start point to the results to have a start point
        results.add(start);

        //keep on growing until we reached the desired length
        processList(start.getSize());
    }


    /**
     * Takes the current snake and create a new two headed snake.
     * For each valid snake it will do this again and again until we reach the given length
     * @param snakeResult the Snake to grow
     * @param length the length to continue to
     */
    private void process(SnakeResult snakeResult, final int length) throws ExecutionException, InterruptedException {

        final String a = snakeResult.getSnake() + "L";
        final String b = snakeResult.getSnake() + "R";

        Future<SnakeResult> futureA = executor.submit(new Callable<SnakeResult>() {
            public SnakeResult call() {
                return sSolver.solve(a);
            }});

        Future<SnakeResult> futureB = executor.submit(new Callable<SnakeResult>() {
            public SnakeResult call() {
                return sSolver.solve(b);
            }});

        handleResult(futureA.get(), length);
        handleResult(futureB.get(), length);

    }


    public void handleResult(SnakeResult result, int desiredLength) throws ExecutionException, InterruptedException {

        //when the result is valid we continue to let the snake grow
        if(result.isValid()) {
            //continue if we did not reached the desiredLength yet and the current result is not worse than the worst result in the current population
            if(result.getSize() < desiredLength && (results.size() < populationSize || result.getSquareSide() < results.get(populationSize-1).getSquareSide())) {
                process(result, desiredLength);
            }
            else {
                //valid result - add it and sort the list
                results.add(result);
                Collections.sort(results);
            }
        }
    }

    public void writeResult(SnakeResult result, Integer step) {
        //write the result to the file so we don't forgot the result
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("evo_results.txt", true))) {
            bw.newLine();
            Date now = new Date();
            long secs = (now.getTime() - startDate.getTime()) / 1000l;
            bw.append("Finished in: ").append(String.valueOf(secs)).append("s. With params:" ).append("growSize: ").append(String.valueOf(growSize)).append("population: ").append(String.valueOf(populationSize));
            bw.newLine();
            if (step == null) {
                bw.append(now.toString()).append(" / ").append(String.valueOf(result.getSquareSide())).append(" / ").append(result.getSnake());
            }
            else {
                bw.append(now.toString()).append(" / ").append("no results anymore at step ").append(String.valueOf(step));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void processList(int currentLength) throws ExecutionException, InterruptedException {

        int desiredLength = currentLength + growSize;

        if(desiredLength > 669) {
            desiredLength = 669;
        }

        System.out.println(desiredLength);

        //keep only the best
        Collections.sort(results);
        int popSize = Math.min(results.size(), populationSize);
        List<SnakeResult> bestResults = new ArrayList<>(results.subList(0,popSize));
        results.clear();

        for(SnakeResult s : bestResults) {
            process(s, desiredLength);
        }



        if(results.size() == 0) {
            System.out.println("No longer valid results at step " + desiredLength);
            writeResult(null, desiredLength);
            return;
        }

        if(desiredLength < 669) {
            processList(desiredLength);
        }
        else {
            //reached the final length - get the best
            Collections.sort(results);
            writeResult(results.get(0), null);
            System.out.println(results.get(0).getSquareSide() +  "/" + results.get(0).getSnake());
            return;
        }
    }
}
