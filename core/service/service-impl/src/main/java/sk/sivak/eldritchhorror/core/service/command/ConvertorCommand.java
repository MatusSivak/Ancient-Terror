package sk.sivak.eldritchhorror.core.service.command;

import java8.features.function.Function;
import rx.Single;
import sk.sivak.eldritchhorror.core.commandqueue.AbstractCommand;
import sk.sivak.eldritchhorror.core.service.util.CommandNameResolver;

/**
 * @author msivak
 */
public class ConvertorCommand<In, Out> extends AbstractCommand<In, Out> {

    private final Class<In> inClass;
    private final Class<Out> outClass;
    private Function<In, Out> convertFn;

    public ConvertorCommand(Class<In> inClass, Class<Out> outClass, Function<In, Out> convertFn) {
        this.convertFn = convertFn;
        this.inClass = inClass;
        this.outClass = outClass;
    }

    @Override
    public Single<Out> execute() {
        return Single.fromCallable(() -> convertFn.apply(input)).cache();
    }

    @Override
    public String getName() {
        return CommandNameResolver.createConvertorCommandName(inClass, outClass);
    }
}
