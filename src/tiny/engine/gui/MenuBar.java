package tiny.engine.gui;

import javax.swing.*;

/**
 * The MenuBar class can be used to add a default menu to the main window. This
 * is optional.
 *
 * @author Damian Strain
 */
public final class MenuBar {

    private JMenuBar menuBar = null;

    /**
     * Initialises a new menu bar for the main game window.
     */
    public void init() {
        menuBar = new JMenuBar();

        createGameMenu();
        createFileMenu();
    }

    /**
     * Initialises the game menu and adds it to the menu bar.
     */
    private void createGameMenu() {
        // Create menu and menu items
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem pauseGame = new JMenuItem("Pause");
        JMenuItem resumeGame = new JMenuItem("Resume");
        JMenuItem exitGame = new JMenuItem("Exit");

        // Add items to game menu
        gameMenu.add(newGame);
        gameMenu.add(pauseGame);
        gameMenu.add(resumeGame);
        gameMenu.addSeparator();
        gameMenu.add(exitGame);

        // Add game menu to menu bar
        menuBar.add(gameMenu);
    }

    /**
     * Initialises the file menu and adds it to the menu bar.
     */
    private void createFileMenu() {
        // Create menu and menu items
        JMenu fileMenu = new JMenu("File");
        JMenuItem item1 = new JMenuItem("Item 1");
        JMenuItem item2 = new JMenuItem("Item 2");
        JMenuItem item3 = new JMenuItem("Item 3");
        JMenuItem item4 = new JMenuItem("Item 4");

        // Add items to game menu
        fileMenu.add(item1);
        fileMenu.addSeparator();
        fileMenu.add(item2);
        fileMenu.add(item3);
        fileMenu.add(item4);

        // Add game menu to menu bar
        menuBar.add(fileMenu);
    }

    /**
     * Returns the underlying menu bar of this menu as a JMenuBar.
     *
     * @return the menu's menu bar
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }
}
