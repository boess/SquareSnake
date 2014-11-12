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


    public SnakeResult solve(String solution) {

        SnakeResult result = new SnakeResult();
        result.setValid(true);
        result.setSnake(solution);
        result.setSize(solution.length());

        Snake snake = new Snake();
		char[] input = solution.toCharArray();


		// Apply input solution to the snake:
		int stepsTaken = 0;
		for (int i = 0; i < input.length; i++) {

			int stepsUntilNextTurn = primeGaps.get(i);
            try {
                snake.step(stepsUntilNextTurn);
            } catch (IllegalArgumentException e) {
                //can not make the step
                result.setValid(false);
                break;
            }
            snake.turn(input[i]);

			stepsTaken += stepsUntilNextTurn;
		}

//		// Take the final steps to create a snake of the desired total length:
        if (solution.length()  == 669) {
            //do this when we have a complete solution to determine the correct square size
            //this could still go wrong
            try {
                snake.step(5000 - stepsTaken);
            } catch (IllegalArgumentException e) {
                //can not make the last step - so close...
                result.setValid(false);
            }
        }

        // Calculate the final bounding square:
		int xmin = 0, ymin = 0, xmax = 0, ymax = 0;
		for(Coordinate coordinate:snake.allLocations) {
			xmax = Math.max(xmax,  coordinate.x);
			xmin = Math.min(xmin,  coordinate.x);
			ymax = Math.max(ymax,  coordinate.y);
			ymin = Math.min(ymin,  coordinate.y);
		}

        result.setSquareSide(Math.max(xmax - xmin, ymax - ymin));

        return result;
	}

	/**
	 * Store the state of the snake
	 */
	private class Snake {

		private final int LEFT = -1;
		private final int RIGHT = 1;
		
		private Coordinate[] DIRECTIONS = new Coordinate[] {
				new Coordinate(0, -1), // North 
				new Coordinate(1, 0),  // East
				new Coordinate(0, 1),  // South
				new Coordinate(-1, 0)  // West
		};

		// Our current heading (pointer into DIRECTIONS array), start going north
		private int currentHeading = 0;
		
		// Our current location:
		private Coordinate currentLocation = new Coordinate(0, 0);
		
		// All the previously visited locations:
		private List<Coordinate> allLocations = new ArrayList<Coordinate>();

		public Snake() {
			//Add initial position:
			allLocations.add(currentLocation);
		}

		/**
		 * Take N steps in the current direction
		 */
		private void step(int length) {
			for (int i = 0; i < length; i++) {
				
				// New location:
				currentLocation = new Coordinate(
						currentLocation.x + DIRECTIONS[currentHeading].x,
						currentLocation.y + DIRECTIONS[currentHeading].y);
				
				// Check if there is a crossing (slow method, going through a list)
				if (allLocations.contains(currentLocation)) {
					throw new IllegalArgumentException("Crossing detected at: "
							+ currentLocation + " after "
							+ allLocations.size() + " steps");
				}
				allLocations.add(currentLocation);
			}
		}

		/**
		 * Turn the snake [L]eft or [R]ight
		 * 
		 * @param direction L or R
		 */
		private void turn(char direction) {
			if (direction == 'L') {
				currentHeading = (4 + (currentHeading + LEFT)) % 4;
			} else {
				currentHeading = (currentHeading + RIGHT) % 4;
			}
		}
	}

	private class Coordinate {

		private int x;
		private int y;

		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Coordinate)) {
				return false;
			}
			Coordinate other = (Coordinate) obj;
			return other.x == x && other.y == y;
		}
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