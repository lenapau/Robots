package gui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import locale.Translatable;
import java.util.ResourceBundle;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.io.Serializable;

public class GameWindow extends JInternalFrame implements Settable, Serializable, Translatable
{
    public GameWindow(RobotController controller)
    {
        super("Игровое поле", true, true, true, true);
        GameVisualizer m_visualizer = new GameVisualizer(controller);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    public void translate(ResourceBundle bundle) {
        setTitle(bundle.getString("gameArea"));
    }

    private Object writeReplace() {
        int state = isIcon() ? 1 : 0;
        Point location = isIcon() ? null : getLocation();
        Settings settings = new Settings(getSize(), location, state, getClass().getSimpleName());
        return settings;
    }


    public void setSettings(Settings settings) {
        if (settings.state == 1) {
            try {
                setIcon(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
        if (settings.location == null) {
            setSize(settings.screenSize);
        } else {
            setBounds(settings.location.x, settings.location.y,
                    settings.screenSize.width,
                    settings.screenSize.height);
        }
    }
}