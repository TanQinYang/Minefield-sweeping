
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MinesweeperPanel extends JPanel {

    private UIEngine engine;
    private JButton[][] buttons;
    private int cellSize = 36;
    private boolean debug = false;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private Runnable onFirstReveal = () -> {};
    private java.util.function.Consumer<Boolean> onGameEnd = win -> {};
    private java.util.function.Consumer<Integer> onFlagCountChanged = left -> {};

    // Distinct looks
    private static final Color COLOR_UNREVEALED_BG = new Color(0xB0B0B0);
    private static final Color COLOR_REVEALED_BG   = new Color(0xF4F4F4);
    private static final Color COLOR_FLAG_BG       = new Color(0xFFE8C2);
    private static final Color COLOR_MINE_LOSS_BG  = new Color(255, 235, 238);
    private static final Border BORDER_RAISED  = new BevelBorder(BevelBorder.RAISED);
    private static final Border BORDER_LOWERED = new BevelBorder(BevelBorder.LOWERED);

    // Classic number palette
    private static final Color[] NUM_COLORS = {
            Color.BLACK,
            new Color(0x0000FF), // 1
            new Color(0x008000), // 2
            new Color(0xFF0000), // 3
            new Color(0x000080), // 4
            new Color(0x800000), // 5
            new Color(0x008080), // 6
            new Color(0x000000), // 7
            new Color(0x808080)  // 8
    };

    public MinesweeperPanel(int rows, int cols, int mines) {
        setBackground(new Color(0xF3F4F6));
        setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        newBoard(rows, cols, mines);
    }

    public void newBoard(int rows, int cols, int mines) {
        removeAll();
        engine = new UIEngine(rows, cols, mines);
        setLayout(new GridLayout(rows, cols, 2, 2));
        buttons = new JButton[rows][cols];

        Font f = getFont().deriveFont(Font.BOLD, Math.max(14f, cellSize / 2f));
        MouseAdapter cellMouse = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                JButton b = (JButton) e.getSource();
                Point p = (Point) b.getClientProperty("rc");
                int rr = p.x, cc = p.y;

                if (SwingUtilities.isRightMouseButton(e)) rightPressed = true;
                if (SwingUtilities.isLeftMouseButton(e)) leftPressed = true;

                if (leftPressed && rightPressed) chord(rr, cc);
                else if (SwingUtilities.isRightMouseButton(e) || e.isControlDown()) toggleFlag(rr, cc);
                else if (SwingUtilities.isLeftMouseButton(e)) reveal(rr, cc);

                b.requestFocusInWindow();
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) rightPressed = false;
                if (SwingUtilities.isLeftMouseButton(e)) leftPressed = false;
            }
        };

        KeyAdapter cellKeys = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                JButton b = (JButton) e.getSource();
                Point p = (Point) b.getClientProperty("rc");
                int rr = p.x, cc = p.y;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_ENTER:
                        UIEngine.ViewCell vc = engine.viewCell(rr, cc);
                        if (vc.revealed && vc.adj > 0) chord(rr, cc);
                        else reveal(rr, cc);
                        break;
                    case KeyEvent.VK_F:
                        toggleFlag(rr, cc);
                        break;
                    default: // no-op
                }
            }
        };

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton b = new JButton();
                b.putClientProperty("rc", new Point(r, c));
                b.setFocusable(true);
                b.setFont(f);
                b.setPreferredSize(new Dimension(cellSize, cellSize));
                b.setMargin(new Insets(0,0,0,0));
                b.setBackground(COLOR_UNREVEALED_BG);
                b.setBorder(BORDER_RAISED);
                b.addMouseListener(cellMouse);
                b.addKeyListener(cellKeys);
                buttons[r][c] = b;
                add(b);
            }
        }
        revalidate();
        renderAll();
        if (buttons.length > 0 && buttons[0].length > 0) buttons[0][0].requestFocusInWindow();
    }

    public void setPreferredCellSize(int size) { this.cellSize = size; }

    public void setDebug(boolean debug) {
        this.debug = debug;
        renderAll();
    }

    public int flagsLeft() { return engine.flagsRemaining(); }

    public void setOnGameEnd(java.util.function.Consumer<Boolean> c) { this.onGameEnd = c != null ? c : win -> {}; }
    public void setOnFlagCountChanged(java.util.function.Consumer<Integer> c) { this.onFlagCountChanged = c != null ? c : left -> {}; }
    public void setOnFirstReveal(Runnable r) { this.onFirstReveal = r != null ? r : () -> {}; }

    private void reveal(int r, int c) {
        if (engine.isGameOver()) return;
        boolean wasFirst = !engine.isFirstClickDone();
        boolean hitMine = engine.reveal(r, c);
        if (wasFirst && engine.isFirstClickDone()) onFirstReveal.run();
        onFlagCountChanged.accept(engine.flagsRemaining());
        renderAll();
        if (hitMine) end(false);
        else if (engine.isGameOver()) end(engine.isWin());
    }

    private void chord(int r, int c) {
        if (engine.isGameOver()) return;
        UIEngine.ViewCell vc = engine.viewCell(r, c);
        if (!vc.revealed || vc.adj <= 0) return;

        int flags = 0;
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr, nc = c + dc;
                if (nr < 0 || nr >= engine.rows() || nc < 0 || nc >= engine.cols()) continue;
                if (engine.viewCell(nr, nc).flagged) flags++;
            }
        if (flags != vc.adj) { Toolkit.getDefaultToolkit().beep(); return; }

        boolean hit = false;
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr, nc = c + dc;
                if (nr < 0 || nr >= engine.rows() || nc < 0 || nc >= engine.cols()) continue;
                UIEngine.ViewCell nb = engine.viewCell(nr, nc);
                if (!nb.flagged && !nb.revealed) hit |= engine.reveal(nr, nc);
            }
        onFlagCountChanged.accept(engine.flagsRemaining());
        renderAll();
        if (hit) end(false);
        else if (engine.isGameOver()) end(engine.isWin());
    }

    private void toggleFlag(int r, int c) {
        if (engine.isGameOver()) return;
        engine.toggleFlag(r, c);
        onFlagCountChanged.accept(engine.flagsRemaining());
        renderCell(r, c);
    }

    private void end(boolean win) {
        renderAll();
        onGameEnd.accept(win);
    }

    private void renderAll() {
        for (int r = 0; r < engine.rows(); r++)
            for (int c = 0; c < engine.cols(); c++) renderCell(r, c);
    }

    private void renderCell(int r, int c) {
        JButton b = buttons[r][c];
        UIEngine.ViewCell vc = engine.viewCell(r, c);

        b.setEnabled(!engine.isGameOver());
        b.setText("");

        if (!vc.revealed) {
            b.setBackground(vc.flagged ? COLOR_FLAG_BG : COLOR_UNREVEALED_BG);
            b.setBorder(BORDER_RAISED);
            if (!vc.flagged && (debug || engine.isGameOver()) && vc.isMine) {
                b.setText("💣");
                b.setForeground(new Color(128, 0, 0));
            }
        } else {
            b.setBackground(vc.isMine ? COLOR_MINE_LOSS_BG : COLOR_REVEALED_BG);
            b.setBorder(BORDER_LOWERED);
            if (vc.isMine) {
                b.setText("💣");
            } else if (vc.adj > 0) {
                b.setText(String.valueOf(vc.adj));
                b.setForeground(NUM_COLORS[Math.min(vc.adj, 8)]);
            }
        }
    }
}
