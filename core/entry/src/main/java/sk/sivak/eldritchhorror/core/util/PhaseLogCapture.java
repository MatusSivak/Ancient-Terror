package sk.sivak.eldritchhorror.core.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationLogger;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import sk.sivak.eldritchhorror.core.constants.log.PhaseLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Mirrors log4j and Gdx.app log output into {@link PhaseLog}. */
public final class PhaseLogCapture {
    private static final String APPENDER_NAME = "phase-log";
    private static final ThreadLocal<SimpleDateFormat> TIME =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("HH:mm:ss.SSS", Locale.US));

    private PhaseLogCapture() { }

    public static void install(Application app) {
        try {
            Logger root = Logger.getRootLogger();
            if (root.getAppender(APPENDER_NAME) == null) root.addAppender(new Log4jAppender());
        } catch (Throwable ignored) { /* Diagnostics must never stop the game. */ }
        try {
            ApplicationLogger current = app.getApplicationLogger();
            if (current != null && !(current instanceof GdxLogger)) app.setApplicationLogger(new GdxLogger(current));
        } catch (Throwable ignored) { /* Diagnostics must never stop the game. */ }
    }

    static String line(String level, String tag, String message, Throwable error) {
        StringBuilder text = new StringBuilder()
                .append(TIME.get().format(new Date())).append(' ').append(level).append(' ')
                .append(tag).append(": ").append(message);
        if (error != null) text.append('\n').append(error);
        return text.toString();
    }

    private static final class Log4jAppender extends AppenderSkeleton {
        Log4jAppender() { setName(APPENDER_NAME); }

        @Override
        protected void append(LoggingEvent event) {
            StringBuilder message = new StringBuilder(String.valueOf(event.getRenderedMessage()));
            String[] trace = event.getThrowableStrRep();
            if (trace != null) {
                for (int i = 0; i < trace.length && i < 15; i++) message.append('\n').append(trace[i]);
            }
            String logger = event.getLoggerName();
            int dot = logger == null ? -1 : logger.lastIndexOf('.');
            PhaseLog.append(line(String.valueOf(event.getLevel()), dot < 0 ? String.valueOf(logger)
                    : logger.substring(dot + 1), message.toString(), null));
        }

        @Override public void close() { }
        @Override public boolean requiresLayout() { return false; }
    }

    private static final class GdxLogger implements ApplicationLogger {
        private final ApplicationLogger delegate;

        GdxLogger(ApplicationLogger delegate) { this.delegate = delegate; }

        @Override public void log(String tag, String message) {
            PhaseLog.append(line("INFO", tag, message, null));
            delegate.log(tag, message);
        }
        @Override public void log(String tag, String message, Throwable exception) {
            PhaseLog.append(line("INFO", tag, message, exception));
            delegate.log(tag, message, exception);
        }
        @Override public void error(String tag, String message) {
            PhaseLog.append(line("ERROR", tag, message, null));
            delegate.error(tag, message);
        }
        @Override public void error(String tag, String message, Throwable exception) {
            PhaseLog.append(line("ERROR", tag, message, exception));
            delegate.error(tag, message, exception);
        }
        @Override public void debug(String tag, String message) {
            PhaseLog.append(line("DEBUG", tag, message, null));
            delegate.debug(tag, message);
        }
        @Override public void debug(String tag, String message, Throwable exception) {
            PhaseLog.append(line("DEBUG", tag, message, exception));
            delegate.debug(tag, message, exception);
        }
    }
}
