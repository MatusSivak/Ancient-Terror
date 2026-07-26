package sk.sivak.eldritchhorror.core.service;

import java8.features.function.BiConsumer;
import java8.features.function.Consumer;
import java8.features.function.Function;
import java8.features.function.Supplier;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.action.provider.ActionProvider;
import sk.sivak.eldritchhorror.core.commandqueue.Command;
import sk.sivak.eldritchhorror.core.commandqueue.CommandQueueFacade;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;
import sk.sivak.eldritchhorror.core.service.command.CollectorCommand;
import sk.sivak.eldritchhorror.core.service.command.ConvertorCommand;
import sk.sivak.eldritchhorror.core.service.command.SkipCommand;
import sk.sivak.eldritchhorror.core.service.util.CommandNameResolver;
import sk.sivak.eldritchhorror.core.service.util.ServiceHelper;

import java.util.List;

/**
 * @author msivak
 */
public abstract class AbstractCommonService implements CommonService {

    protected ActionProvider actionProvider;
    protected CommandQueueFacade commandQueueFacade;
    private ServiceHelper serviceHelper;

    public AbstractCommonService() {
        serviceHelper = new ServiceHelper();
    }

    public void setActionProvider(ActionProvider actionProvider) {
        this.actionProvider = actionProvider;
        serviceHelper.setActionProvider(actionProvider);
    }

    public void setCommandQueueFacade(CommandQueueFacade commandQueueFacade) {
        this.commandQueueFacade = commandQueueFacade;
    }

    protected <In, Out> void executeConvertor(Class<In> inClass, Class<Out> outClass, Function<In, Out> convertFn) {
        ConvertorCommand<In, Out> convertorCommand =
                new ConvertorCommand<>(inClass, outClass, convertFn);
        execute(convertorCommand);
    }

    protected <In, Out> void executeCollector(Class<In> inClass, Class<Out> outClass, Out initValue,
                                              BiConsumer<In, Out> conversionFn) {
        CollectorCommand<In, Out> collectorCommand =
                new CollectorCommand<>(inClass, outClass, conversionFn);
        collectorCommand.init(initValue);
        execute(collectorCommand);
    }

    protected <In, Out> void executeHookable(HookableAction<In, Out> hookableAction) {
        executeHookable(null, hookableAction, null);
    }

    protected <In, Out> void executeHookable(In data, HookableAction<In, Out> hookableAction) {
        executeHookable(data, hookableAction, null);
    }

    protected <In, Out> void executeHookable(HookableAction<In, Out> hookableAction, SingleSubscriber<? super Out> singleSubscriber) {
        executeHookable(null, hookableAction, singleSubscriber);
    }

    protected <In, Out> void executeHookable(In data, HookableAction<In, Out> hookableAction, SingleSubscriber<? super Out> singleSubscriber) {
        List<ActionCommand<?, ?>> commands = serviceHelper.createHookableCommands(hookableAction, data);
        if (singleSubscriber != null) {
            Command<Out, Out> lastCommand = (Command<Out, Out>) commands.get(commands.size() - 1);
            lastCommand.addOnCompletedListener(singleSubscriber);
        }
        execute(commands.toArray(new ActionCommand[commands.size()]));
    }

    protected void execute(Command... commands) {
        commandQueueFacade.execute(commands);
    }

    @Override
    public void hold() {
        commandQueueFacade.hold();
    }

    @Override
    public void clearQueue() {
        commandQueueFacade.clearQueue();
    }

    @Override
    public void release() {
        commandQueueFacade.release();
    }

    @Override
    public void startInsertingAfterCommand(BeforeAfterEvent command) {
        commandQueueFacade.startInsertingAtCommand(CommandNameResolver.createAfterEventName(command));
    }

    @Override
    public void startInsertingBeforeCommand(BeforeAfterEvent command) {
        commandQueueFacade.startInsertingAtCommand(CommandNameResolver.createBeforeEventName(command));
    }

    @Override
    public void startInsertingAfterCommand(String commandName) {
        commandQueueFacade.startInsertingAtCommand(commandName);
    }

    @Override
    public void endInsertingAtCommand() {
        commandQueueFacade.endInsertingAtCommand();
    }

    @Override
    public <In, Out> void convertFromTo(Class<In> inClass, Class<Out> outClass, Function<In, Out> convertFn) {
        executeConvertor(inClass, outClass, convertFn);
    }

    @Override
    public <Out> void convertTo(Class<Out> outClass, Supplier<Out> supplier) {
        executeConvertor(Object.class, outClass, out -> supplier.get());
    }

    @Override
    public void convertToNull() {
        executeConvertor(Object.class, Object.class, out -> null);
    }

    @Override
    public <InOut> void skipAfterEvent(BeforeAfterEvent beforeAfterEvent, InOut output) {
        skipEvent(beforeAfterEvent, output, false);
    }

    @Override
    public <InOut> void skipBeforeEvent(BeforeAfterEvent beforeAfterEvent, InOut output) {
        skipEvent(beforeAfterEvent, output, true);
    }

    private <InOut> void skipEvent(BeforeAfterEvent beforeAfterEvent, InOut output, boolean before) {
        SkipCommand<InOut> skipCommand = new SkipCommand<>();
        if (output != null) {
            skipCommand.setInput(output);
        }
        skipCommand.setNextCommandName(before ? CommandNameResolver.createBeforeEventName(beforeAfterEvent) :
                CommandNameResolver.createAfterEventName(beforeAfterEvent));
        skipCommand.setCommandQueueFacade(commandQueueFacade);
        commandQueueFacade.execute(skipCommand);
    }

    @Override
    public <InOut> void addEventCommand(Consumer<InOut> consumer) {
        ActionCommand<InOut, InOut> eventCommand = new ActionCommand<>(new Action<InOut, InOut>() {

            private InOut input;

            @Override
            public Single<InOut> execute() {
                return Single.create(onSub -> {
                    consumer.accept(input);
                    onSub.onSuccess(input);
                });
            }

            @Override
            public void setInput(InOut input) {
                this.input = input;
            }
        }, "EVENT_COMMAND");
        commandQueueFacade.execute(eventCommand);
    }

    @Override
    public <InOut> void addEventCommand(Function<InOut, Single<InOut>> fn) {
        ActionCommand<InOut, InOut> eventCommand = new ActionCommand<>(new Action<InOut, InOut>() {

            private InOut input;

            @Override
            public Single<InOut> execute() {
                return fn.apply(input);
            }

            @Override
            public void setInput(InOut input) {
                this.input = input;
            }
        }, "EVENT_COMMAND");
        commandQueueFacade.execute(eventCommand);
    }

}
