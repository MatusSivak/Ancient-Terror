package sk.sivak.eldritchhorror.core.service.util;

import sk.sivak.eldritchhorror.core.action.AfterEventAction;
import sk.sivak.eldritchhorror.core.action.BeforeEventAction;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.action.InitValueAction;
import sk.sivak.eldritchhorror.core.action.provider.ActionProvider;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

import java.util.Arrays;
import java.util.List;

/**
 * @author msivak
 */
public class ServiceHelper {

    private ActionProvider actionProvider;

    public void setActionProvider(ActionProvider actionProvider) {
        this.actionProvider = actionProvider;
    }

    public <In, Out> List<ActionCommand<?, ?>> createHookableCommands(HookableAction<In, Out> hookableAction, In initData) {
        BeforeAfterEvent beforeAfterEvent = hookableAction.getBeforeAfterEvent();

        BeforeEventAction<In> beforeEventAction = actionProvider.getBeforeEventAction(beforeAfterEvent);
        AfterEventAction<Out> afterEventAction = actionProvider.getAfterEventAction(beforeAfterEvent);

        String beforeActionName = CommandNameResolver.createBeforeEventName(beforeAfterEvent);
        String afterActionName = CommandNameResolver.createAfterEventName(beforeAfterEvent);
        String mainActionName = CommandNameResolver.createMainEventName(beforeAfterEvent);

        ActionCommand<In, In> initValueCommand = createInitValueCommand(initData);
        ActionCommand<In, In> beforeCommand = new ActionCommand<>(beforeEventAction, beforeActionName);
        ActionCommand<In, Out> mainCommand = new ActionCommand<>(hookableAction, mainActionName);
        ActionCommand<Out, Out> afterCommand = new ActionCommand<>(afterEventAction, afterActionName);

        if (initValueCommand != null) {
            return Arrays.asList(initValueCommand, beforeCommand, mainCommand, afterCommand);
        } else {
            return Arrays.asList(beforeCommand, mainCommand, afterCommand);
        }
    }

    private <InOut> ActionCommand<InOut, InOut> createInitValueCommand(InOut initData) {
        if (initData != null) {
            InitValueAction<InOut> initValueAction = actionProvider.getInitValueAction(initData);
            String initValueName = CommandNameResolver.createInitValueName(initData);
            return new ActionCommand<>(initValueAction, initValueName);
        }
        return null;
    }
}
