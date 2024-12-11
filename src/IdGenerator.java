public class IdGenerator {
    private static int counter = 1;

    public static int getNewId() {
        return counter++;
    }
}
