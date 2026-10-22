package sk.sivak.eldritchhorror.core.constants.log;

import java.util.ArrayDeque;

/** In-memory tail of log lines written since the current game phase started; attached to bug reports. */
public final class PhaseLog {
    public static final int MAX_CHARS = 128 * 1024;
    private static final ArrayDeque<String> LINES = new ArrayDeque<>();
    private static int chars;

    private PhaseLog() { }

    public static synchronized void append(String line) {
        if (line == null) return;
        if (line.length() > MAX_CHARS - 1) line = line.substring(line.length() - (MAX_CHARS - 1));
        LINES.addLast(line);
        chars += line.length() + 1;
        while (chars > MAX_CHARS && !LINES.isEmpty()) {
            chars -= LINES.removeFirst().length() + 1;
        }
    }

    public static synchronized void startPhase(String phase) {
        clear();
        append("=== Phase started: " + phase + " ===");
    }

    public static synchronized void clear() {
        LINES.clear();
        chars = 0;
    }

    public static synchronized String snapshot() {
        StringBuilder text = new StringBuilder(chars);
        for (String line : LINES) text.append(line).append('\n');
        return text.toString();
    }
}
