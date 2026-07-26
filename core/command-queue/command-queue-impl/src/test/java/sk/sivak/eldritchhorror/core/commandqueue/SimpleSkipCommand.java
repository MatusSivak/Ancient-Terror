package sk.sivak.eldritchhorror.core.commandqueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;

/**
 * @author msivak
 */
public class SimpleSkipCommand<In> extends AbstractCommand<In, In> {

    private static final Logger logger = LogManager.getLogger(SimpleSkipCommand.class);

    private String name;
    private CommandQueue commandQueue;
    private String nextCommandName;

    SimpleSkipCommand(String name) {
        this.name = name;
    }

    public void setCommandQueue(CommandQueue commandQueue) {
        this.commandQueue = commandQueue;
    }

    public void setNextCommandName(String nextCommandName) {
        this.nextCommandName = nextCommandName;
    }

    @Override
    public Single<In> execute() {
        return Single.fromCallable(() -> {
            logger.info("Execution of skip command '" + getName() + "'");
            commandQueue.skipToCommand(nextCommandName);
            return input;
        }).cache();
    }

    @Override
    public String getName() {
        return name + "->" + nextCommandName;
    }
}
