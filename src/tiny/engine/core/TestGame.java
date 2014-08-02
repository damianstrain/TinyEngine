package tiny.engine.core;

/**
 * @author Damian Strain
 */
public class TestGame extends Game {

    public static void main(String[] args) {
        new TestGame().init("", false, 600, 400);
    }

    @Override
    public GameScreen getStartScreen() {
        return new TestScreen(this);
    }
}
