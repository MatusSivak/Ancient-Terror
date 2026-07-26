package sk.sivak.eldritchhorror.core.service.command;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.commandqueue.AbstractCommand;

/**
 * @author msivak
 */
public class ActionCommand<In, Out> extends AbstractCommand<In, Out> {

    private String name;
    private Action<In, Out> action;
    private Single<Out> single;

    public ActionCommand(Action<In, Out> action, String name) {
        this.action = action;
        single = action.execute();
        setName(name);
    }

    @Override
    public Single<Out> execute() {
        return single;
    }

    @Override
    public void setInput(In input) {
        action.setInput(input);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
