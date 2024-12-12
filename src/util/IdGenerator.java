package util;

public class IdGenerator {
    private int counter = 1;

    public int getNewId() {
        return counter++;
    }
}
