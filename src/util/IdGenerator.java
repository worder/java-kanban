package util;

public class IdGenerator {
    private int counter = 1;

    public int getNewId() {
        return counter++;
    }

    public void actualizeNextId(int id) {
        if (id > counter) {
            counter = id + 1;
        }
    }
}
