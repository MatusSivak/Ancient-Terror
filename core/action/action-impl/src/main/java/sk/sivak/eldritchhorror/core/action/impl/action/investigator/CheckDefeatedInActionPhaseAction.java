package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import java.util.Random;

public class CheckDefeatedInActionPhaseAction implements Action<Object, Object> {

    private Object input;
    private InvestigatorWrite focusedInvestigator;
    private InvestigatorWrite activeToEndWith;

    @Override
    public Single<Object> execute() {
        return Single.create(this::execute);
    }

    private void execute(SingleSubscriber<? super Object> onSub) {
        activeToEndWith = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        focusedInvestigator = activeToEndWith;
        ServicePlatform.get().getService().hold();
        for (InvestigatorWrite investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
            execute(investigator);
        }
        if (activeToEndWith != null) { // if previously there was someone focused
            ServicePlatform.get().getService().addEventCommand(in -> {
                showActiveInvestigator();
            });
        }
        ServicePlatform.get().getService().release();
        onSub.onSuccess(input);
    }

    private void execute(InvestigatorWrite investigator) {
        Boolean defeatedByHealth = null;
        if (investigator.getCurrentHealth() <= 0 && investigator.getCurrentSanity() <= 0) {
            defeatedByHealth = new Random().nextBoolean();
        } else if (investigator.getCurrentHealth() <= 0) {
            defeatedByHealth = true;
        } else if (investigator.getCurrentSanity() <= 0) {
            defeatedByHealth = false;
        }

        if (defeatedByHealth == null) {
            return;
        }


        ServicePlatform.get().getService().hold();
        changeToDefeatedInvestigator(investigator);

        ServicePlatform.get().getInvestigatorService().defeatInvestigator(investigator.getInfo().getInvestigatorId(), defeatedByHealth);

        ServicePlatform.get().getService().release();
    }

    private void changeToDefeatedInvestigator(InvestigatorWrite investigator) {
        ServicePlatform.get().getService().addEventCommand(in -> {
            if (focusedInvestigator == null || focusedInvestigator != investigator) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                focusedInvestigator = investigator;
                ServicePlatform.get().getService().release();
            }
        });
    }

    private void showActiveInvestigator() {
        if (activeToEndWith != null && ServicePlatform.get().getInvestigators().getToBeReplacedInvestigators().contains(activeToEndWith.getInfo().getInvestigatorId())) {
            ServicePlatform.get().getInvestigators().changeActiveInvestigator();
            activeToEndWith = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        }

        if (activeToEndWith != focusedInvestigator) {
            ServicePlatform.get().getService().hold();
            if (activeToEndWith != null) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeToEndWith.getInfo().getInvestigatorId());
            }
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getService().release();
        }

    }

    @Override
    public void setInput(Object input) {
        this.input = input;
    }
}
