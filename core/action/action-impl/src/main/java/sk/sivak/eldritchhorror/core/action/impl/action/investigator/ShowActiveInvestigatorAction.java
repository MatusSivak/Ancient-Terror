package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.BackgroundData;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead;

public class ShowActiveInvestigatorAction extends AbstractHookableAction<InvestigatorId, Void> {

    private final boolean displayTransition;

    public ShowActiveInvestigatorAction(boolean displayTransition) {
        super(BeforeAfterEvent.SHOW_ACTIVE_INVESTIGATOR);
        this.displayTransition = displayTransition;
    }


    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        if (input == null) {
            ss.onSuccess(null);
            return;
        }
        BackgroundModelRead.BackgroundDataRead backgroundData = ServicePlatform.get().getModel().getBackgroundModel().peekBackground(input);
        BackgroundModelRead.BackgroundDataRead currentBackground = ServicePlatform.get().getModel().getBackgroundModel().getCurrentBackground();
        Completable changeBackground;

        if (currentBackground != null && currentBackground.equals(backgroundData)) {
            changeBackground = Completable.complete();
        } else if (currentBackground == null && backgroundData == null) {
            changeBackground = Completable.complete();
        } else if (backgroundData == null) {
            changeBackground = ServicePlatform.get().getGameController().showWorldBackground();
            ServicePlatform.get().getModel().getBackgroundModel().setCurrentBackground(null);
        } else {
            ServicePlatform.get().getModel().getBackgroundModel().setCurrentBackground(backgroundData);
            switch (backgroundData.getBackgroundType()) {
                case LOST_IN_TIME_AND_SPACE:
                    changeBackground = ServicePlatform.get().getGameController().showInvestigatorLostInTimeAndSpace(input);
                    break;
                case CUSTOM:
                    changeBackground = ServicePlatform.get().getGameController().showCustomBackground(((String) backgroundData.getAdditionalData()));
                    break;
                case LOCATION:
                    changeBackground = ServicePlatform.get().getGameController().showLocationBackground((LocationId) backgroundData.getAdditionalData());
                    break;
                case LOCATION_TYPE:
                    changeBackground = ServicePlatform.get().getGameController().showLocationTypeBackground((LocationType) backgroundData.getAdditionalData());
                    break;
                case RESEARCH:
                    changeBackground = ServicePlatform.get().getGameController().showResearchBackground((LocationType) backgroundData.getAdditionalData());
                    break;
                case COMBAT:
                    changeBackground = ServicePlatform.get().getGameController().showCombatBackground();
                    break;
                default:
                    throw new IllegalArgumentException("Background type not recognized: " + backgroundData.getBackgroundType());
            }

        }

        changeBackground.subscribe(() -> {
            boolean lostInTimeAndSpace = ServicePlatform.get().getInvestigators().getInvestigator(input).isLostInTimeAndSpace();
            ServicePlatform.get().getGameController().showActiveInvestigator(displayTransition, lostInTimeAndSpace)
                    .subscribe(() -> ss.onSuccess(null));
        });
    }
}
