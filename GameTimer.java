import javax.swing.*;

public class GameTimer {
    private final Timer timer;
    private final java.util.function.IntConsumer onTick;
    private int seconds = 0;

    public GameTimer(int ms, java.util.function.IntConsumer onTick) {
        this.onTick = onTick != null ? onTick : s -> {};
        this.timer = new Timer(ms, e -> {
            seconds++;
            this.onTick.accept(seconds);
        });
        this.timer.setRepeats(true);
    }

    public void start() {
        if (!timer.isRunning()) timer.start();
    }

    public void stop() { timer.stop(); }

    public void reset() {
        stop();
        seconds = 0;
        onTick.accept(seconds);
    }
}
