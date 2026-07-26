package sk.sivak.eldritchhorror.core.service.command;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.commandqueue.AbstractCommand;
import sk.sivak.eldritchhorror.core.commandqueue.CommandQueueFacade;
import sk.sivak.eldritchhorror.core.service.util.CommandNameResolver;

/**
 * @author msivak
 */
public class SkipCommand<In> extends AbstractCommand<In, In> {

    private static final Logger logger = LogManager.getLogger(SkipCommand.class);

    private CommandQueueFacade commandQueueFacade;
    private String nextCommandName;

    public void setCommandQueueFacade(CommandQueueFacade commandQueueFacade) {
        this.commandQueueFacade = commandQueueFacade;
    }

    public void setNextCommandName(String nextCommandName) {
        this.nextCommandName = nextCommandName;
    }

    @Override
    public Single<In> execute() {
        return Single.fromCallable(() -> {
            logger.info("Skipping to '" + nextCommandName + "'");
            commandQueueFacade.skipToCommand(nextCommandName);
            return input;
        }).cache();
    }

    @Override
    public String getName() {
        return CommandNameResolver.createSkipCommandName(nextCommandName);
    }

    @Override
    public void setInput(In input) {
        if (this.input == null) {
            this.input = input;
        } else {
            // Input is ignored if present
            logger.info("Input to skip command is ignored");
        }
    }
}
