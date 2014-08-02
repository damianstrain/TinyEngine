package tiny.engine.core;

import java.net.URL;

/**
 * @author Damian Strain
 */
public abstract class Game {

    private final GameGui gui;
    private final GameLoop loop;
    private final GameAudio audio;
    private final GameInput input;
    private final GameFileIO fileIO;
    private GameScreen screen = null;

    /**
     * Constructs a new game and initialises it's components.
     */
    public Game() {
        gui = new GameGui();
        loop = new GameLoop();
        audio = new GameAudio();
        input = new GameInput();
        fileIO = new GameFileIO();
    }

    /**
     * Initialises the game with no title and a re-sizable window.
     */
    public final void init() {
        this.init("", true);
    }

    /**
     * Initialises the game with a title and whether the game window is
     * re-sizable.
     *
     * @param title the game title to set
     * @param isResizable true if the window is re-sizable, false otherwise
     */
    public final void init(String title, boolean isResizable) {
        init(title, isResizable, 0, 0);
    }

    /**
     * Initialises the game with a title and whether the game window is
     * re-sizable. The width and height parameters sets the size of the main
     * window.
     *
     * @param title the game title to set
     * @param isResizable true if the window is re-sizable, false otherwise
     * @param width the width of the game
     * @param height the height of the game
     */
    public final void init(String title, boolean isResizable, int width, int height) {
        this.init(title, isResizable, width, height, null, null, null);
    }

    /**
     * Initialises the game with a title and whether the game window is
     * re-sizable. The width and height parameters sets the size of the main
     * window.
     * <p/>
     * Optionally, you can pass in the URL for your games icon, it's version
     * number, and copyright notice or game author. This information is shown in
     * the about dialog.
     *
     * @param title the window title to set
     * @param isResizable true if the window is re-sizable, false otherwise
     * @param width the width of the main window
     * @param height the height of the main window
     * @param iconURL the URL for your games icon
     * @param version the version of your game
     * @param copyright the games copyright notice or author
     */
    public final void init(String title, boolean isResizable, int width, int height, URL iconURL, String version, String copyright) {
        gui.init(title, isResizable, width, height, iconURL, version, copyright);
        loop.init(this);
        audio.init();

        gui.setVisible(true);
        screen = getStartScreen();
    }

    /**
     * Starts the game.
     */
    public void start() {
        loop.startGame();
    }

    /**
     * Stops the game.
     */
    public void stop() {
        loop.stopGame();
    }

    /**
     * Pauses the game.
     *
     * @param pauseState true to pause the game, false to resume.
     */
    public void pause(boolean pauseState) {
        loop.pauseGame(pauseState);
    }

    /**
     * Returns whether the game is paused or not.
     *
     * @return true if paused, false otherwise
     */
    public boolean isPaused() {
        return loop.isPaused();
    }

    /**
     * Exits the game.
     */
    public void exit() {
        loop.exitGame();
    }

    /**
     * Returns a reference to the game gui component. Use this component to
     * construct your games main window and menus etc.
     *
     * @return a gui component reference
     */
    public final GameGui getGui() {
        return gui;
    }

    /**
     * Returns a reference to the game audio component. Use this component to
     * load audio resources and manage playback.
     *
     * @return An audio component reference
     */
    public final GameAudio getAudio() {
        return audio;
    }

    /**
     * Returns a reference to the game input component. Use this component to
     * gain access to the keyboard, mouse, and mouse wheel.
     *
     * @return an input component reference
     */
    public final GameInput getInput() {
        return input;
    }

    /**
     * Returns a reference to the game fileIO component. Use this component to
     * load/save file either from the class path, or the file system.
     *
     * @return a fileIO component reference
     */
    public final GameFileIO getFileIO() {
        return fileIO;
    }

    /**
     * Sets the current screen to the specified screen and renders it.
     *
     * @param screen the new screen to be rendered
     */
    public final void setScreen(GameScreen screen) {
        if (screen == null) {
            throw new NullPointerException("ERROR: Parameter GameScreen must not be null");
        }

        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }

    /**
     * Returns the current screen being rendered.
     *
     * @return the current screen
     */
    public final GameScreen getCurrentScreen() {
        return screen;
    }

    /**
     * Returns the screen used upon first execution of your game and renders it.
     * This method needs to be overridden in your main class when you extend the
     * Game class.
     * <p/>
     * Use this method to return your games start screen. This could be a menu,
     * title or intro screen which the player progresses from to the main game.
     *
     * @return the games start screen
     */
    public abstract GameScreen getStartScreen();
}
