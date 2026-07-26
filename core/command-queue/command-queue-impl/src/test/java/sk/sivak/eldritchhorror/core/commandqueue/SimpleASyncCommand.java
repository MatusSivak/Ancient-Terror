package sk.sivak.eldritchhorror.core.commandqueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;

/**
 * @author msivak
 */
public class SimpleASyncCommand<In, Out> extends AbstractCommand<In, Out> {

    private static final Logger logger = LogManager.getLogger(SimpleASyncCommand.class);

    private Out output;
    private String name;

    SimpleASyncCommand(String name, Out output) {
        this.output = output;
        this.name = name;
    }

    @Override
    public Single<Out> execute() {
        return Single.create(s -> {
            new Thread(() -> {
                logger.info("Execution of async command '" + getName() + "'");
                s.onSuccess(output);
            }).start();
        });
    }

    @Override
    public String getName() {
        return name;
    }
}
