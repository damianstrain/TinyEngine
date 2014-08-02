package tiny.engine.core;

import tiny.engine.utils.DebugUtils;
import tiny.engine.utils.GraphicsUtils;

import java.awt.*;
import java.awt.image.BufferStrategy;

/**
 * @author Damian Strain
 */
public final class GameLoop implements Runnable {

    // If you're worried about visual hitches more than perfect timing, set this to 1
    private static final int MAX_UPDATES = 5;           // Update this many times before a new render
    private static final int NUM_BUFFERS = 2;           // Number of buffers, 2 for double, 3 for triple
    private static final int DEFAULT_FPS = 60;          // Default fps is 60
    private static final int DEFAULT_UPS = 30;          // Default ups is 30

    private static final long MS_TO_SEC = (long) 1E3;   // 1000 (one second)
    private static final long NS_TO_MS = (long) 1E6;   // 1000,000
    private static final long NS_TO_SEC = (long) 1E9;   // 1000,000,000

    private volatile boolean running = false;   // Control of the game
    private volatile boolean paused = false;   // Control of pausing
    private volatile boolean isFixedTimeStep = true;    // Choose loop type (fixed/variable)

    private volatile boolean debug = true;              // Display debug info

    private int targetGameFps = 0;                      // Target number of renders
    private int targetGameUps = 0;                      // Target number of updates

    private Thread animator = null;             // The main game thread
    private BufferStrategy strategy = null;             // Used for double buffering and page flipping
    private Game game = null;

    /**
     * Initialises the GameLoop and attempts to set the frame rate to the
     * monitors refresh rate. If this is unsuccessful, a default of 60fps is
     * used along with a default 30ups (if fixed time step is true).
     *
     * @param game the current game
     */
    public void init(Game game) {
        int refreshRate = GraphicsUtils.getRefreshRate();
        if (refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN) {
            targetGameFps = DEFAULT_FPS;// 60 fps
            targetGameUps = DEFAULT_UPS;// 30 ups
        } else {
            setFrameRate(refreshRate);
            setUpdateRate(refreshRate / 2);
        }

        this.game = game;

        this.game.getGui().getWindow().getCanvas().addKeyListener(this.game.getInput().getKeyboard());
        this.game.getGui().getWindow().getCanvas().addMouseListener(this.game.getInput().getMouse());
        this.game.getGui().getWindow().getCanvas().addMouseMotionListener(this.game.getInput().getMouse());
        this.game.getGui().getWindow().getCanvas().addMouseWheelListener(this.game.getInput().getMouseWheel());
    }

    /**
     * Starts the game by initialising the game thread and calling it's start
     * method.
     */
    public void startGame() {
        if (animator == null || !running) {
            animator = new Thread(this, "TinyEngine thread");
            animator.start();
        }
    }

    /**
     * Stops the game.
     */
    public void stopGame() {
        running = false;
    }

    /**
     * Pauses the game.
     *
     * @param pauseState True to pause the game, false to resume.
     */
    public void pauseGame(boolean pauseState) {
        paused = pauseState;
    }

    /**
     * Exits the game and closes all windows.
     */
    public void exitGame() {
        try {
            System.exit(0);
        } catch (SecurityException se) {
            // Unable to exit with the specified status, exit code "0"
        }
    }

    /**
     * Returns the current frame rate in frames per second (fps).
     *
     * @return The current frame rate
     */
    public double getFrameRate() {
        return targetGameFps;
    }

    /**
     * Sets the desired frame rate in frames per second (fps). This determines
     * the number of times the game gets rendered. The higher the frame rate,
     * the smoother the game play. Depending on your system, it is advised not
     * to set this too high.
     * <p/>
     * This should be set before Game.init() is called.
     *
     * @param newGameFps The new frame rate to render at
     */
    public void setFrameRate(int newGameFps) {
        if (newGameFps >= 0) {
            targetGameFps = newGameFps;
        }
    }

    /**
     * Returns the current update rate in updates per second (ups).
     *
     * @return The current update rate
     */
    public double getUpdateRate() {
        return targetGameUps;
    }

    /**
     * Sets the desired update rate in updates per second (ups). This determines
     * the number of times the game logic is updated. This is only valid if the
     * isFixedTimeStep boolean is set to True.
     * <p/>
     * This should be set before Game.init() is called.
     *
     * @param newGameHz The new update rate
     */
    public void setUpdateRate(int newGameHz) {
        if (newGameHz >= 0 && isFixedTimeStep) {
            targetGameUps = newGameHz;
        }
    }

    /**
     * This method sets whether the game engine should use a fixed time step
     * loop or not. If you wish to use a variable time step loop, set this to
     * False.
     * <p/>
     * This should be set before startGame is called, otherwise the default is
     * True.
     *
     * @param isFixedTimeStep True for a fixed time step loop, False for a
     * variable time step loop
     */
    public void setFixedTimeStep(boolean isFixedTimeStep) {
        this.isFixedTimeStep = isFixedTimeStep;
    }

    /**
     * Returns whether the game engine is using a fixed time step loop or not.
     *
     * @return True if using a fixed time step loop, False if using a variable
     * time step loop
     */
    public boolean isFixedTimeStep() {
        return isFixedTimeStep;
    }

    /**
     * Returns whether the game is currently running or not.
     *
     * @return True if the game is running, False otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Returns whether the game is paused or not.
     *
     * @return True if paused, False otherwise
     */
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void run() {
        try {
            loopInit();// initialise the loop

            if (isFixedTimeStep) {
                fixedTimeStep();
            } else {
                variableTimeStep();
            }
        } catch (Exception e) {
            e.printStackTrace();// Delete in production
        } finally {
            exitGame();// If all else fails
        }
    }

    /**
     * Initialises the game loop by requesting focus in the window, creating the
     * buffer strategy, and setting the running flag to true.
     */
    private void loopInit() {
        game.getGui().getWindow().getCanvas().requestFocusInWindow();
        if (strategy == null) {
            game.getGui().getWindow().getCanvas().createBufferStrategy(NUM_BUFFERS);
            strategy = game.getGui().getWindow().getCanvas().getBufferStrategy();
        }
        running = true;
    }

    /**
     * The fixed time step loop uses a fixed delta to vary the movement of
     * objects.
     * <p/>
     * Fixed time step is different in that every call to update() has the same
     * elapsed time (hence, it is "fixed"). It is also different from variable
     * time step in the potential order of update() and render() calls. While in
     * variable time step, you get one update for every draw call; in fixed time
     * step, you potentially get numerous update() calls in between each Draw.
     *
     * @since Version 1.0
     */
    private void fixedTimeStep() {
        double timeBetweenUpdates = NS_TO_SEC / targetGameUps;
        double timeBetweenRenders = NS_TO_SEC / targetGameFps;

        double lastUpdateTime = System.nanoTime();
        double lastRenderTime = System.nanoTime();

        while (running) {
            double now = System.nanoTime();
            int updateCount = 0;

            // Do as many game updates as we need to, potentially playing catchup
            while (now - lastUpdateTime > timeBetweenUpdates && updateCount < MAX_UPDATES) {
                update(0);
                lastUpdateTime += timeBetweenUpdates;
                updateCount++;
            }

            /**
             * If for some reason an update takes forever, we don't want to do
             * an insane number of catch ups. If you were doing some sort of
             * game that needed to keep EXACT time, you would get rid of this.
             */
            if (now - lastUpdateTime > timeBetweenUpdates) {
                lastUpdateTime = now - timeBetweenUpdates;
            }

            // Calculate interpolation and render
            float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / timeBetweenUpdates));
            render(interpolation);
            lastRenderTime = now;

            // Yield until it has been at least the target time between renders. This saves the CPU from hogging
            while (now - lastRenderTime < timeBetweenRenders && now - lastUpdateTime < timeBetweenUpdates) {
                Thread.yield();

                /**
                 * This stops the app from consuming all your CPU. It makes this
                 * slightly less accurate, but is worth it. You can remove this
                 * line and it will still work (better), your CPU just climbs on
                 * certain OSes.
                 *
                 * FYI on some OS's this can cause pretty bad stuttering. Scroll
                 * down and have a look at different peoples' solutions to this.
                 */
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                now = System.nanoTime();
            }
        }
    }

    /**
     * The variable time step loop uses the delta time between each update to
     * vary the movement of objects. Variable time step means that the amount of
     * time between frames is not constant. Your game gets one Update, then one
     * Draw, and then it repeats until the game exits.
     *
     * @since Version 1.0
     */
    private void variableTimeStep() {
        long startTime = System.nanoTime();
        long targetElapsedTime = NS_TO_SEC / targetGameFps;

        while (running) {
            // Determine the elapsed time since the last update call
            long currentTime = System.nanoTime();
            long elapsedTime = currentTime - startTime;
            startTime = currentTime;

            /**
             * Calculate deltaTime:
             *
             * A delta of 1.0 means that your loop took as long as you normally
             * expect (an "optimal" amount of time), where as a delta of < 1
             * means that the loop is going faster than optimal, and a delta of
             * > 1 means that it's slower.
             */
            double deltaTime = elapsedTime / ((double) targetElapsedTime);

            update(deltaTime);
            render(0);

            /**
             * We want each frame to take 16 milliseconds, to do this we've
             * recorded when we started the frame. We then add 16 milliseconds
             * to this and then factor in the current time to give us our final
             * value to wait for. Remember this is in ms, whereas our
             * currentTime etc. vars are in ns.
             */
            try {
                Thread.sleep((startTime - System.nanoTime() + targetElapsedTime) / NS_TO_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();// Delete in production
            }
        }
    }

    /**
     * The render method is responsible for rendering the game to the screen
     * using either fixed or variable time steps. Each call to this method gets
     * the Graphics object from the buffer strategy used for drawing. The
     * graphics object is then disposed of after the buffer has been rendered.
     * <p/>
     * The parameters deltaTime and interpolation are used to vary the movement
     * or interpolate between frames depending on the type of game loop chosen.
     *
     * @param interpolation The value used to interpolate between two frame
     * states
     */
    private void render(float interpolation) {
        Graphics g = null;
        try {
            g = strategy.getDrawGraphics();
            draw(g, interpolation);
        } catch (Exception e) {
            e.printStackTrace();// Delete in production
        } finally {
            if (g != null) {
                g.dispose();
            }
        }

        if (!strategy.contentsLost()) {
            strategy.show();
            Toolkit.getDefaultToolkit().sync(); // Sync display needed on some systems
        } else {
            System.out.println("Contents Lost");
        }
    }

    /**
     * Updates the game logic.
     *
     * @param deltaTime the time passed since the last update call
     */
    private void update(double deltaTime) {
        game.getInput().getKeyboard().update();
        game.getInput().getMouse().update();
        game.getInput().getMouseWheel().update();
        game.getCurrentScreen().update();
        game.getCurrentScreen().update(deltaTime);
    }

    /**
     * Renders the game to the screen.
     *
     * @param g the Graphics to render with
     * @param interpolation the value used to interpolate between two frame
     * states
     */
    private void draw(Graphics g, float interpolation) {
        /**
         * Graphics2D provides more sophisticated control over geometry,
         * coordinate transformations, color management, and text layout.
         */
        Graphics2D g2d = (Graphics2D) g;

        // Clear the buffer/screen
        g2d.setColor(game.getGui().getWindow().getCanvas().getBackground());
        g2d.fillRect(0, 0, game.getGui().getWindow().getCanvas().getWidth(), game.getGui().getWindow().getCanvas().getHeight());

        // Render the current Screen
        game.getCurrentScreen().render(g2d, interpolation);
        game.getCurrentScreen().render(g2d);

        if (debug) {
            DebugUtils.showCurrentFps(g2d);
            DebugUtils.showMemoryUsage(g2d);
            DebugUtils.showLoopType(g2d, isFixedTimeStep());
        }
    }
}
