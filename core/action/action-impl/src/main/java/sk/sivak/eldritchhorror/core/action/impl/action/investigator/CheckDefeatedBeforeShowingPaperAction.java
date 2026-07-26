package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import java.util.List;
import java.util.Random;

public class CheckDefeatedBeforeShowingPaperAction implements Action<Object, Object> {

    private Object input;

    @Override
    public Single<Object> execute() {
        return Single.create(this::execute);
    }

    private void execute(SingleSubscriber<? super Object> onSub) {
        if (ServicePlatform.get().getPhase().getPhaseType() != PhaseType.ENCOUNTER) {
            onSub.onSuccess(input);
            return;
        }
        List<? extends InvestigatorWrite> onBoardInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        for (InvestigatorRead investigator : onBoardInvestigators) {
            execute(investigator);
        }
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (activeInvestigator == null || !onBoardInvestigators.contains(activeInvestigator) &&
                (activeInvestigator.getCurrentSanity() <= 0 ||
                activeInvestigator.getCurrentHealth() <= 0)) {
            ServicePlatform.get().getService().hold();
            finishTypewriterPaper();
            ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.END_OF_ENCOUNTER, null);
            ServicePlatform.get().getService().release();
        }
        onSub.onSuccess(input);
    }

    private void execute(InvestigatorRead investigator) {
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

        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (activeInvestigator == investigator) {
            defeatActiveInvestigator(investigator, defeatedByHealth);
        } else {
            defeatNotActiveInvestigator(investigator, defeatedByHealth);
        }
    }

    private void defeatActiveInvestigator(InvestigatorRead investigator, Boolean defeatedByHealth) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getService().startInsertingAfterCommand("INSERT_DEFEAT_SEQUENCE_POINT");
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            ServicePlatform.get().getInvestigatorService().defeatInvestigator(investigator.getInfo().getInvestigatorId(), defeatedByHealth);
            ServicePlatform.get().getService().release();
            ServicePlatform.get().getService().endInsertingAtCommand();
        });
        finishTypewriterPaper();
        ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.END_OF_ENCOUNTER, null);
        ServicePlatform.get().getService().release();
    }

    protected void finishTypewriterPaper() {
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
    }

    private void defeatNotActiveInvestigator(InvestigatorRead investigator, Boolean defeatedByHealth) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        ServicePlatform.get().getInvestigatorService().defeatInvestigator(investigator.getInfo().getInvestigatorId(), defeatedByHealth);
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        ServicePlatform.get().getService().release();
    }

    @Override
    public void setInput(Object input) {
        this.input = input;
    }
}
