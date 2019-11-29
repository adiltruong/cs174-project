import javax.swing.*;
import java.awt.*;

public final class guifactory {

    public static void popupWindow(String title, String text) {
        JFrame frame = new JFrame();
        JTextArea textArea = new JTextArea(5, 10);
        JScrollPane scrollPane = new JScrollPane(textArea);

        textArea.setEditable(false);
        textArea.append(text);

        frame.add(scrollPane);
        frame.setTitle(title);
        frame.setSize(800, 700);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public static void alert(String text, boolean success) {
        JFrame frame = new JFrame();
        JLabel label = new JLabel(text);

        label.setForeground(success ? Color.black : Color.white);
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
        frame.getContentPane().setBackground(success ? Color.green : Color.red);
        frame.add(label);
        frame.setSize(500, 100);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public static void clearPanel(JPanel panel) {
        panel.removeAll();
        updatePanel(panel);
    }

    public static void updatePanel(JPanel panel) {
        panel.repaint();
        panel.revalidate();
    }
}

