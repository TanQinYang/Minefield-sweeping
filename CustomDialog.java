import javax.swing.*;
import java.awt.*;

public class CustomDialog {
    public static class Params {
        public final int rows, cols, mines;
        Params(int r, int c, int m) { rows = r; cols = c; mines = m; }
    }

    public static Params ask(java.awt.Component parent, int defR, int defC, int defM) {
        JTextField rField = new JTextField(String.valueOf(defR));
        JTextField cField = new JTextField(String.valueOf(defC));
        JTextField mField = new JTextField(String.valueOf(defM));
        JPanel p = new JPanel(new GridLayout(0,2,6,6));
        p.add(new JLabel("Rows:")); p.add(rField);
        p.add(new JLabel("Columns:")); p.add(cField);
        p.add(new JLabel("Mines:")); p.add(mField);
        int res = JOptionPane.showConfirmDialog(parent, p, "Custom Game", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int r = clamp(Integer.parseInt(rField.getText()), 1, 60);
                int c = clamp(Integer.parseInt(cField.getText()), 1, 60);
                int maxM = Math.max(1, r * c - 1);
                int m = clamp(Integer.parseInt(mField.getText()), 1, maxM);
                return new Params(r, c, m);
            } catch (Exception ignored) {
                JOptionPane.showMessageDialog(parent, "Invalid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private static int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }
}
