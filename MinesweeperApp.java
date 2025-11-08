
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MinesweeperApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new MinesweeperApp().show();
        });
    }

    private JFrame frame;
    private JLabel flagsLabel;
    private JLabel timerLabel;
    private JCheckBoxMenuItem debugItem;
    private JCheckBoxMenuItem autoStartItem;
    private MinesweeperPanel panel;
    private GameTimer gameTimer;
    private JButton faceBtn;

    private int rows = 9, cols = 9, mines = 10;
    private boolean autoStartOnFirstClick = true;

    private void show() {
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Top bar
        JPanel top = new JPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 8, 0, 8);

        faceBtn = new JButton("🙂");
        faceBtn.setFocusable(false);
        faceBtn.setFont(faceBtn.getFont().deriveFont(Font.PLAIN, 18f));
        faceBtn.addActionListener(e -> newGame(rows, cols, mines));

        flagsLabel = new JLabel("🚩 0");
        flagsLabel.setFont(flagsLabel.getFont().deriveFont(Font.BOLD, 16f));

        timerLabel = new JLabel("⏱ 0");
        timerLabel.setFont(timerLabel.getFont().deriveFont(Font.BOLD, 16f));

        gc.gridx = 0; top.add(flagsLabel, gc);
        gc.gridx = 1; top.add(faceBtn, gc);
        gc.gridx = 2; top.add(timerLabel, gc);

        frame.add(top, BorderLayout.NORTH);

        // Board
        panel = new MinesweeperPanel(rows, cols, mines);
        panel.setPreferredCellSize(36);
        panel.setDebug(false);
        panel.setOnGameEnd(win -> {
            gameTimer.stop();
            faceBtn.setText(win ? "😎" : "😵");
            JOptionPane.showMessageDialog(frame, win ? "You won!" : "Boom! You lost.",
                    "Game Over",
                    win ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });
        panel.setOnFlagCountChanged(left -> flagsLabel.setText("🚩 " + left));
        panel.setOnFirstReveal(() -> { if (autoStartOnFirstClick) gameTimer.start(); });
        frame.add(panel, BorderLayout.CENTER);

        // Timer
        gameTimer = new GameTimer(1000, seconds -> timerLabel.setText("⏱ " + seconds));

        // Menu
        frame.setJMenuBar(buildMenuBar());

        // Shortcuts
        frame.getRootPane().registerKeyboardAction(e -> newGame(rows, cols, mines),
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        newGame(rows, cols, mines);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu game = new JMenu("Game");
        JMenuItem newItem = new JMenuItem("New (F2)");
        newItem.addActionListener(e -> newGame(rows, cols, mines));

        JMenu difficulty = new JMenu("Difficulty");
        ButtonGroup group = new ButtonGroup();

        JRadioButtonMenuItem beg = new JRadioButtonMenuItem("Beginner (9×9, 10)", true);
        JRadioButtonMenuItem inter = new JRadioButtonMenuItem("Intermediate (16×16, 40)");
        JRadioButtonMenuItem expert = new JRadioButtonMenuItem("Expert (16×30, 99)");
        JRadioButtonMenuItem custom = new JRadioButtonMenuItem("Custom...");
        for (JRadioButtonMenuItem it : new JRadioButtonMenuItem[]{beg, inter, expert, custom}) group.add(it);

        beg.addActionListener(e -> setDifficulty(9, 9, 10));
        inter.addActionListener(e -> setDifficulty(16, 16, 40));
        expert.addActionListener(e -> setDifficulty(16, 30, 99));
        custom.addActionListener(e -> {
            CustomDialog.Params p = CustomDialog.ask(frame, rows, cols, mines);
            if (p != null) setDifficulty(p.rows, p.cols, p.mines);
        });

        difficulty.add(beg); difficulty.add(inter); difficulty.add(expert);
        difficulty.addSeparator(); difficulty.add(custom);

        debugItem = new JCheckBoxMenuItem("Debug: show mines");
        debugItem.addActionListener(e -> panel.setDebug(debugItem.isSelected()));

        JMenu settings = new JMenu("Settings");
        autoStartItem = new JCheckBoxMenuItem("Start timer on first click", true);
        autoStartItem.addActionListener(e -> autoStartOnFirstClick = autoStartItem.isSelected());
        settings.add(autoStartItem);

        game.add(newItem);
        game.add(difficulty);
        game.addSeparator();
        game.add(debugItem);
        mb.add(game);
        mb.add(settings);
        return mb;
    }

    private void setDifficulty(int r, int c, int m) {
        rows = r; cols = c; mines = m;
        newGame(rows, cols, mines);
    }

    private void newGame(int r, int c, int m) {
        gameTimer.reset();
        panel.newBoard(r, c, m);
        flagsLabel.setText("🚩 " + panel.flagsLeft());
        debugItem.setSelected(false);
        panel.setDebug(false);
        faceBtn.setText("🙂");
        frame.pack();
        if (!autoStartOnFirstClick) gameTimer.start();
    }
}
