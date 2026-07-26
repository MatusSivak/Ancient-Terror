package sk.sivak.eldritchhorror.core.view.components.investigator;

import rx.SingleSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.components.button.HideOkButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class SelectSingleInvestigator {
    private List<InvestigatorId> availableInvestigators;
    private SelectInvestigatorComponent selectInvestigatorComponent;
    private SingleSubscriber<? super InvestigatorId> subscriber;
    private HideOkButtons hideOkButtons;

    public SelectSingleInvestigator(List<InvestigatorId> availableInvestigators,
                                    SingleSubscriber<? super InvestigatorId> subscriber) {
        this.availableInvestigators = availableInvestigators;
        this.subscriber = subscriber;
        hideOkButtons = new HideOkButtons();
        selectInvestigatorComponent = new SelectInvestigatorComponent(0.85f) {
            @Override
            protected void hideOkButton() {
                hideOkButtons.disableOkButton();
            }

            @Override
            protected void showOkButton() {
                hideOkButtons.enableOkButton();
            }
        };
    }

    public void show() {

        selectInvestigatorComponent.init(availableInvestigators.toArray(new InvestigatorId[availableInvestigators.size()]));
        selectInvestigatorComponent.setPosition(
                VIEWPORT_WIDTH / 2 - selectInvestigatorComponent.getWidth() / 2,
                VIEWPORT_HEIGHT / 2 - selectInvestigatorComponent.getHeight() / 2);
        InfoStage.showActor(selectInvestigatorComponent);

        hideOkButtons.init("Display investigators?", getOnConfirmAction(), selectInvestigatorComponent);
        hideOkButtons.showButtons();
        hideOkButtons.disableOkButton();
    }

    private Action0 getOnConfirmAction() {
        return () -> onSelect(selectInvestigatorComponent.getSelectedInvestigatorId());
    }


    private void onSelect(InvestigatorId investigatorId) {
        selectInvestigatorComponent.disable(investigatorId);
        InfoStage.hideActor(selectInvestigatorComponent, () -> subscriber.onSuccess(investigatorId), 0.25f);
    }
}
