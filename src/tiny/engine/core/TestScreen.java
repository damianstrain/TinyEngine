package tiny.engine.core;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import tiny.engine.input.Keyboard;
import tiny.engine.input.Mouse;
import tiny.engine.input.MouseWheel;

/**
 * @author Damian Strain
 */
public final class TestScreen extends GameScreen {

    private final Game game;
    private final Keyboard keyboard;
    private final Mouse mouse;
    private final MouseWheel mouseWheel;

    private BufferedImage sprite = null;
    //private Entity        pacman = null;

    public TestScreen(Game game) {
        super(game);
        this.game = game;
        keyboard = game.getInput().getKeyboard();
        mouse = game.getInput().getMouse();
        mouseWheel = game.getInput().getMouseWheel();

        try {
            sprite = ImageIO.read(game.getFileIO().loadFile().getResource("res/sprite.gif"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //pacman = new Entity(sprite, 200, 200, true, 12, 1, 1000);
        game.start();
    }

    @Override
    public void update() {
        if (keyboard.isKeyPressed(KeyEvent.VK_UP)) {
            //pacman.setDx(0);
            //pacman.setDy(-3);
        } else if (keyboard.isKeyPressed(KeyEvent.VK_DOWN)) {
            //pacman.setDx(0);
            //pacman.setDy(3);
        } else if (keyboard.isKeyPressed(KeyEvent.VK_LEFT)) {
            //pacman.setDx(-3);
            //pacman.setDy(0);
        } else if (keyboard.isKeyPressed(KeyEvent.VK_RIGHT)) {
            //pacman.setDx(3);
            //pacman.setDy(0);
        }
        //pacman.update();
    }

    @Override
    public void render(Graphics2D g2d, float interpolation) {
        //pacman.render(g2d, interpolation);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
