import java.util.ArrayList;
import java.util.List;

/**
 * Store the state of the snake
 */
public class Snake {

    private static final int LEFT = -1;
    private static final int RIGHT = 1;


    private static Coordinate[] DIRECTIONS = new Coordinate[]{
            new Coordinate(0, -1), // North
            new Coordinate(1, 0),  // East
            new Coordinate(0, 1),  // South
            new Coordinate(-1, 0)  // West
    };

    private int xmin = 0, ymin = 0, xmax = 0, ymax = 0;

    // Our current heading (pointer into DIRECTIONS array), start going north
    private int currentHeading = 0;

    // Our current location:
    private Coordinate currentLocation = new Coordinate(0, 0);

    // All the previously visited locations:
    private List<Coordinate> allLocations = new ArrayList<>(5000);


    public Snake() {
        //Add initial position:
        allLocations.add(currentLocation);
    }


    public Snake(Snake snake) {
        //Add initial position:
        xmin = snake.getXmin();
        ymin = snake.getYmin();
        xmax = snake.getXmax();
        ymax = snake.getYmax();
        allLocations = new ArrayList<>(snake.getAllLocations());
        currentHeading = snake.getCurrentHeading();
        currentLocation = new Coordinate(snake.getCurrentLocation().x, snake.getCurrentLocation().y);
    }


    /**
     * Take N steps in the current direction
     */
    public void step(int length) {

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
            //determine the new min/max
            xmin = Math.min(xmin, currentLocation.x);
            xmax = Math.max(xmax, currentLocation.x);
            ymin = Math.min(ymin, currentLocation.y);
            ymax = Math.max(ymax, currentLocation.y);

            allLocations.add(currentLocation);
        }
    }


    /**
     * Turn the snake [L]eft or [R]ight
     *
     * @param direction L or R
     */
    public void turn(char direction) {

        if (direction == 'L') {
            currentHeading = (4 + (currentHeading + LEFT)) % 4;
        } else {
            currentHeading = (currentHeading + RIGHT) % 4;
        }
    }


    public int getXmin() {

        return xmin;
    }


    public int getYmin() {

        return ymin;
    }


    public int getXmax() {

        return xmax;
    }


    public int getYmax() {

        return ymax;
    }


    public List<Coordinate> getAllLocations() {

        return allLocations;
    }


    public int getCurrentHeading() {

        return currentHeading;
    }


    public Coordinate getCurrentLocation() {

        return currentLocation;
    }
}

