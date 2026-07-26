package sk.sivak.eldritchhorror.core.action.impl.investigator;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

public class RemoveDefeatedInvestigatorAction implements Action<Object, Void> {

    private final InvestigatorId investigatorId;

    public RemoveDefeatedInvestigatorAction(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            InvestigatorRead defeatedInvestigator = Stream.findFirstOrException(ServicePlatform.get().getInvestigators().getDefeatedInvestigators(),
                    di -> di.getInfo().getInvestigatorId() == investigatorId);
            ServicePlatform.get().getInvestigators().removeDefeatedInvestigator(investigatorId);
            ServicePlatform.get().getGameController().removeDefeatedInvestigator(investigatorId, defeatedInvestigator.getCurrentLocationId()).subscribe( () -> {
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
