package sk.sivak.eldritchhorror.core.service.command;

import java8.features.function.BiConsumer;
import rx.Single;
import sk.sivak.eldritchhorror.core.commandqueue.AbstractCommand;
import sk.sivak.eldritchhorror.core.service.util.CommandNameResolver;

/**
 * @author msivak
 */
public class CollectorCommand<In, Out> extends AbstractCommand<In, Out> {

    private final Class<In> inClass;
    private final Class<Out> outClass;
    private Out init;
    private BiConsumer<In, Out> collectFn;

    public CollectorCommand(Class<In> inClass, Class<Out> outClass, BiConsumer<In, Out> collectFn) {
        this.collectFn = collectFn;
        this.inClass = inClass;
        this.outClass = outClass;
    }

    public void init(Out out) {
        this.init = out;
    }

    @Override
    public Single<Out> execute() {
        return Single.fromCallable(() -> init).cache();
    }

    @Override
    public void setInput(In input) {
        collectFn.accept(input, init);
    }

    @Override
    public String getName() {
        return CommandNameResolver.createCollectorCommandName(inClass, outClass);
    }
}
