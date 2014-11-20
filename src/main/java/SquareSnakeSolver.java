import java.util.ArrayList;
import java.util.List;

public class SquareSnakeSolver {

	private static final long serialVersionUID = 1L;
    private static List<Integer> primeGaps;


    public SquareSnakeSolver() {
            if(primeGaps == null) {
                primeGaps = sieveGaps(5000);
            }
    }


    public SnakeResult solve(SnakeResult snakeResult) {

        Snake snake = snakeResult.getSnake();

        if (snakeResult.getDirectionString().length() != 0) {
            //make the turn based on the last L/R in the string
            snake.turn(snakeResult.getDirectionString().charAt(snakeResult.getDirectionString().length()-1));
        }

		//Check the last L/R in the direction string
        if (snakeResult.getDirectionString().length()  < 669) {
            int stepsUntilNextTurn = primeGaps.get(snakeResult.getDirectionString().length());
            try {
                snake.step(stepsUntilNextTurn);
            } catch (IllegalArgumentException e) {
                //can not make the step
                snakeResult.setValid(false);
                return snakeResult;
            }
            snakeResult.setStepsTaken(snakeResult.getStepsTaken() + stepsUntilNextTurn);
        } else {
            // Take the final steps to create a snake of the desired total length:
            //do this when we have a complete solution to determine the correct square size
            //this could still go wrong
            try {
                snake.step(5000 - snakeResult.getStepsTaken());
            } catch (IllegalArgumentException e) {
                //can not make the last step - so close...
                snakeResult.setValid(false);
                return snakeResult;
            }
        }

        //set the max square side based on the information in the snake
        snakeResult.setSquareSide(Math.max(snake.getXmax() - snake.getXmin(), snake.getYmax() - snake.getYmin()));

        return snakeResult;
	}


	/**
	 * Sieve all the primes up to a certain number and return all the gaps.
	 */
	private List<Integer> sieveGaps(int N) {
		
		// Sieve of Eratosthenes
		boolean[] isPrime = new boolean[N + 1];
		for (int i = 2; i <= N; i++) {
			isPrime[i] = true;
		}
		for (int i = 2; i * i <= N; i++) {
			if (isPrime[i]) {
				for (int j = i; i * j <= N; j++) {
					isPrime[i * j] = false;
				}
			}
		}

		// Return the gaps:
		List<Integer> gaps = new ArrayList<Integer>();
		int lastPrime = 0;
		for (int i = 2; i <= N; i++) {
			if (isPrime[i]) {
				gaps.add(i - lastPrime);
				lastPrime = i;
			}
		}
		return gaps;
	}
}