package sk.sivak.eldritchhorror.core.service.util;

import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public class CommandNameResolver {

    public static String createBeforeEventName(BeforeAfterEvent beforeAfterEvent) {
        return "BEFORE_" + beforeAfterEvent + "<" + beforeAfterEvent.getInputClass().getSimpleName() + ">";
    }

    public static String createAfterEventName(BeforeAfterEvent beforeAfterEvent) {
        return "AFTER_" + beforeAfterEvent + "<" + beforeAfterEvent.getOutputClass().getSimpleName() + ">";
    }

    static String createMainEventName(BeforeAfterEvent beforeAfterEvent) {
        return beforeAfterEvent + "<" + beforeAfterEvent.getInputClass().getSimpleName() + "," + beforeAfterEvent.getOutputClass().getSimpleName() + ">";
    }

    public static <InOut> String createInitValueName(InOut data) {
        return "INIT<" + data.getClass().getSimpleName() + ">";
    }

    public static String createSkipCommandName(String nextCommandName) {
        return "SKIP_TO_" + nextCommandName;
    }

    public static String createCollectorCommandName(Class<?> a, Class<?> b) {
        return "COLLECT<" + a.getSimpleName() + "," + b.getSimpleName() + ">";
    }

    public static <In, Out> String createConvertorCommandName(Class<In> inClass, Class<Out> outClass) {
        return "CONVERT<" + inClass.getSimpleName() + "," + outClass.getSimpleName() + ">";
    }
}
