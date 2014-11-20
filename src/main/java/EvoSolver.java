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
 * Only the strongest Snakes on a square survive!
 */
public class EvoSolver {

    ExecutorService executor = Executors.newFixedThreadPool(4);
    SquareSnakeSolver sSolver;
    List<SnakeResult> results;

    //change these to see what are the best settings
    /**
     * The amount of L/R's to grow before we cut the population down to their size
     */
    private int growSize = 1;
    /**
     * The amount of best L/R Strings to keep after each step.
     */
    private int populationSize = 20000;

    /**
     * The best score we already know, no need to go above this
     */
    private int threshold = 250;

    //keep track of the time
    private Date startDate;

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //create the solver and go!
        EvoSolver solver = new EvoSolver();
        solver.solve();

        System.exit(0);
    }


    /**
     * Method to start the solving process
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void solve() throws ExecutionException, InterruptedException {

        results = new ArrayList<>(200000);

        //date for performance concerns
        startDate = new Date();

        sSolver = new SquareSnakeSolver();

        //Create the first SnakeResult
        SnakeResult start = new SnakeResult();
        start.setSnake(new Snake());
        start.setValid(true);
        start.setDirectionString("");

        sSolver.solve(start);

        //We start the Snake with a single direction, checking the other direction is not necessary (LLRRL == RRLLR)
        SnakeResult startLeft = SnakeResult.grow("L", start, true);
        sSolver.solve(startLeft);

        //add this start point to the results to have a start point
        results.add(startLeft);

        //keep on growing until we reached the desired length
        processList(startLeft.getSize());

    }


    /**
     * Takes the current snake and create a new two headed snake.
     * For each valid snake it will do this again and again until we reach the given length of the solution string
     *
     * @param snakeResult the Snake to grow
     * @param length the length of the solution string to continue to
     */
    private void process(SnakeResult snakeResult, final int length) throws ExecutionException, InterruptedException {


        //snake a does not
        final SnakeResult a = SnakeResult.grow("L", snakeResult, false);
        final SnakeResult b = SnakeResult.grow("R", snakeResult, true);

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

        if (threshold > 0 && threshold > result.getSquareSide()) {
            //when the result is valid we continue to let the snake grow
            if(result.isValid()) {
                //continue if we did not reached the desiredLength yet and the current result is not worse than the worst result in the current population
                if(result.getSize() < desiredLength) {
                    process(result, desiredLength);
                }
                else {
                    //valid result - add it and sort the list
                    results.add(result);
                }
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
                bw.append(now.toString()).append(" / ").append(String.valueOf(result.getSquareSide())).append(" / ").append(result.getDirectionString());
            }
            else {
                bw.append(now.toString()).append(" / ").append("no results anymore at step ").append(String.valueOf(step)).append(" for threshold ").append(String.valueOf(threshold));
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

//        System.out.println(desiredLength);
        System.out.println(results.size());

        //keep only the best
        List<SnakeResult> bestResults;
        if (populationSize > 0) {
            Collections.sort(results);
            int popSize = Math.min(results.size(), populationSize);
            bestResults = new ArrayList<>(results.subList(0,popSize));
        } else {
            //no reducing of the population, we ignore darwin
            bestResults = new ArrayList<>(results);
        }
        results.clear();

        for(SnakeResult s : bestResults) {
            process(s, desiredLength);
        }

        if(results.size() == 0) {
            System.out.println("No longer valid results at step " + desiredLength + "for threshold " + threshold );
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
            System.out.println(results.get(0).getSquareSide() +  "/" + results.get(0).getDirectionString());
            return;
        }
    }
}
