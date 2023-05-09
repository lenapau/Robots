package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.io.Serializable;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import java.util.ResourceBundle;
import locale.Translatable;

public class LogWindow extends JInternalFrame implements LogChangeListener, Serializable, Settable, Translatable
{
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) 
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }

    public void translate(ResourceBundle bundle) {
        setTitle(bundle.getString("logWindow"));
    }

    private Object writeReplace() {
        int state = isIcon() ? 1 : 0;
        Point location = isIcon() ? null : getLocation();
        return new Settings(getSize(), location, state, getClass().getSimpleName());
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
