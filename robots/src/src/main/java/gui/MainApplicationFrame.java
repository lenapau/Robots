package gui;

import java.awt.*;
import java.io.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import locale.Translatable;

import logic.IObjectState;
import logic.LocalConfig;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import log.Logger;


public class MainApplicationFrame extends JFrame implements Serializable, Settable {
    private final JDesktopPane desktopPane = new JDesktopPane();
    RobotModel robotModel = new RobotModel();
    RobotController controller = new RobotController(robotModel, new RobotView());
    private final GameWindow gameWindow = new GameWindow(controller);
    private final LogWindow logWindow = createLogWindow();
    CoordinatesWindow coordWindow = new CoordinatesWindow();
    private ResourceBundle currentBundle = getDefaultBundle();
    private final IObjectState configManager = new LocalConfig();
    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);
        setJMenuBar(generateMenuBar());

        addWindow(coordWindow, 300, 300);
        robotModel.addObserver(coordWindow);

        addWindow(logWindow, 200, 200);
        addWindow(gameWindow, 400, 400);
        applyConfig();

        saveConfiguration();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                saveConfiguration();
                exitProgram();
            }
        });
        translate();
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame, int height, int width) {
        frame.setSize(width, height);
        desktopPane.add(frame);
        frame.setVisible(true);
    }


    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        menuBar.add(createExitMenu());
        menuBar.add(generateLangMenu());

        return menuBar;
    }

    private JMenu generateLangMenu() {
        JMenu langMenu = new JMenu(currentBundle.getString("lang"));
        langMenu.setMnemonic(KeyEvent.VK_L);
        {
            JMenuItem russian = new JMenuItem("Русский");
            russian.addActionListener((event) -> {
                Locale.setDefault(new Locale("ru"));
                currentBundle = ResourceBundle.getBundle("main.java.locale.Resource", Locale.getDefault());
                translate();
            });

            JMenuItem english = new JMenuItem("English");
            english.addActionListener((event) -> {
                Locale.setDefault(new Locale("en"));
                currentBundle = ResourceBundle.getBundle("locale.Resource", Locale.getDefault());
                translate();
            });

            langMenu.add(russian);
            langMenu.add(english);
        }
        return langMenu;
    }

    private static ResourceBundle getDefaultBundle() {
        String defLang = Locale.getDefault().getLanguage();
        if ("en".equals(defLang) || "ru".equals(defLang)) {
            return ResourceBundle.getBundle("locale.Resource", Locale.getDefault());
        }
        return ResourceBundle.getBundle("locale.Resource", new Locale("en"));
    }


    private void translate() {
        for (JInternalFrame frame: desktopPane.getAllFrames()) {
            ((Translatable)frame).translate(currentBundle);
        }
        setJMenuBar(generateMenuBar());
    }



    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu(currentBundle.getString("mode"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);


        lookAndFeelMenu.add(
                createJMenuItem(currentBundle.getString("system"),
                        KeyEvent.VK_S,
                        UIManager.getSystemLookAndFeelClassName()));


        lookAndFeelMenu.add(createJMenuItem(currentBundle.getString("universal"),
                KeyEvent.VK_S,
                UIManager.getCrossPlatformLookAndFeelClassName()));

        return lookAndFeelMenu;

    }

    private JMenuItem createJMenuItem(String name, int key, String action) {
        JMenuItem item = new JMenuItem(name, key);
        item.addActionListener((event) -> {
            setLookAndFeel(action);
            this.invalidate();
        });
        return item;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu(currentBundle.getString("tests"));
        testMenu.setMnemonic(KeyEvent.VK_T);

            JMenuItem addLogMessageItem = new JMenuItem(currentBundle.getString("logMessage"), KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug(currentBundle.getString("testMessage"));
            });
            testMenu.add(addLogMessageItem);

        return testMenu;
    }


    private JMenu createExitMenu() {
        JMenu exitMenu = new JMenu(currentBundle.getString("exit"));
        exitMenu.setMnemonic(KeyEvent.VK_E);
        exitMenu.getAccessibleContext().setAccessibleDescription(
                currentBundle.getString("exitButton")   );


        exitMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                saveConfiguration();
                exitProgram();
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                saveConfiguration();
            }

            @Override
            public void menuCanceled(MenuEvent e) {
                saveConfiguration();
            }
        });

        return exitMenu;
    }


    public void exitProgram() {
        Object[] options = {currentBundle.getString("accept"), currentBundle.getString("dispose")};
        if (JOptionPane.showOptionDialog(this.getContentPane(),
                currentBundle.getString("exitMessage"), currentBundle.getString("exit"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                null) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private Object writeReplace() {
        return new Settings(getSize(), getLocationOnScreen(), getState(), getClass().getSimpleName());
    }
    public void setSettings(Settings settings) {
        setState(settings.state);
        setBounds(settings.location.x, settings.location.y,
                settings.screenSize.width,
                settings.screenSize.height);
    }


    protected void saveConfiguration() {
        JSONObject json = new JSONObject();
        saveConfigurationOfElement("gameWindow", json, gameWindow);
        saveConfigurationOfElement("logWindow", json, logWindow);
        saveConfigurationOfElement("CoordinatesWindow", json, coordWindow);
        configManager.save(json);
    }

    protected void saveConfigurationOfElement(String name, JSONObject json, JInternalFrame window) {
        json.put(name+ "X", window.getX());
        json.put(name+ "Y", window.getY());
        json.put(name + "Width", window.getWidth());
        json.put(name+ "Height", window.getHeight());
    }

    protected void applyConfig() {
        JSONObject config = configManager.load();
        if (config == null) {
            return;
        }
        gameWindow.setSize(((Long) config.get("gameWindowWidth")).intValue(), ((Long) config.get("gameWindowHeight")).intValue());
        logWindow.setSize(((Long) config.get("logWindowWidth")).intValue(), ((Long) config.get("logWindowHeight")).intValue());
        coordWindow.setSize(((Long) config.get("CoordinatesWindowWidth")).intValue(), ((Long) config.get("CoordinatesWindowHeight")).intValue());
        gameWindow.setLocation(((Long) config.get("gameWindowX")).intValue(), ((Long) config.get("gameWindowY")).intValue());
        logWindow.setLocation(((Long) config.get("logWindowX")).intValue(), ((Long) config.get("logWindowY")).intValue());
        coordWindow.setLocation(((Long) config.get("CoordinatesWindowX")).intValue(), ((Long) config.get("CoordinatesWindowY")).intValue());
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }
}
