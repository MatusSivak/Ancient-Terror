package sk.sivak.eldritchhorror.core.service;

import java8.features.function.Consumer;
import java8.features.function.Function;
import java8.features.function.Supplier;
import rx.Single;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

/**
 * @author msivak
 */
public interface CommonService {

    void startInsertingBeforeCommand(BeforeAfterEvent command);

    void startInsertingAfterCommand(BeforeAfterEvent command);

    void startInsertingAfterCommand(String commandName);

    void endInsertingAtCommand();

    <In, Out> void convertFromTo(Class<In> inClass, Class<Out> outClass, Function<In, Out> convertFn);

    <Out> void convertTo(Class<Out> outClass, Supplier<Out> supplier);

//    <Out> void convertTo(Class<Out> outClass, Out value);

    void convertToNull();

    <InOut> void skipBeforeEvent(BeforeAfterEvent beforeEvent, InOut inputOutput);

    <InOut> void skipAfterEvent(BeforeAfterEvent afterEvent, InOut inputOutput);

    void hold();

    void clearQueue();

    void release();

    <InOut> void addEventCommand(Consumer<InOut> unaryOperator);

    <InOut> void addEventCommand(Function<InOut, Single<InOut>> fn);
}
