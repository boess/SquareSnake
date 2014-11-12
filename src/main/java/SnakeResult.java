public class SnakeResult implements  Comparable<SnakeResult> {

    public String directionString;
    public int size;
    public int squareSide;
    public boolean valid = true;
    public int stepsTaken;
    public Snake snake;
//    public List<Coordinate> allLocations


    public String getDirectionString() {

        return directionString;
    }


    public void setDirectionString(String directionString) {

        this.directionString = directionString;
    }


    public int getSize() {

        return size;
    }


    public void setSize(int size) {

        this.size = size;
    }


    public int getSquareSide() {

        return squareSide;
    }


    public void setSquareSide(int squareSide) {

        this.squareSide = squareSide;
    }


    public boolean isValid() {

        return valid;
    }


    public void setValid(boolean valid) {

        this.valid = valid;
    }


    public int getStepsTaken() {

        return stepsTaken;
    }


    public void setStepsTaken(int stepsTaken) {

        this.stepsTaken = stepsTaken;
    }


    public Snake getSnake() {

        return snake;
    }


    public void setSnake(Snake snake) {

        this.snake = snake;
    }


    @Override
    public int compareTo(SnakeResult o) {
        return this.getSquareSide() - o.getSquareSide();

    }


    public static SnakeResult grow(String r, SnakeResult source) {
        SnakeResult result = new SnakeResult();

        result.setStepsTaken(source.getStepsTaken());
        result.setDirectionString(source.getDirectionString() + r);
        result.setSize(result.getDirectionString().length());
        result.setSnake(new Snake(source.getSnake()));
        result.setSquareSide(source.getSquareSide());
        result.setValid(source.isValid());

        return  result;

    }
}
