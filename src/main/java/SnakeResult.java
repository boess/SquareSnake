public class SnakeResult implements  Comparable<SnakeResult> {

    public String snake;
    public int size;
    public int squareSide;
    public boolean valid;


    public String getSnake() {

        return snake;
    }


    public void setSnake(String snake) {

        this.snake = snake;
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


    @Override
    public int compareTo(SnakeResult o) {
        return this.getSquareSide() - o.getSquareSide();

    }
}
