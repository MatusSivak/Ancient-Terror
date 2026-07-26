package sk.sivak.eldritchhorror.core.commandqueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;

/**
 * @author msivak
 */
public class SimpleSyncCommand<In, Out> extends AbstractCommand<In, Out> {

    private static final Logger logger = LogManager.getLogger(SimpleASyncCommand.class);

    private Out output;
    private String name;

    SimpleSyncCommand(String name, Out output) {
        this.output = output;
        this.name = name;
    }

    @Override
    public Single<Out> execute() {
        return Single.fromCallable(() -> {
            logger.info("Execution of sync command '" + getName() + "'");
            return output;
        }).cache();
    }

    @Override
    public String getName() {
        return name;
    }
}
