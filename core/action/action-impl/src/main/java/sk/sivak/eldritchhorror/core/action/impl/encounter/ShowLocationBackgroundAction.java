package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.model.BackgroundData;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead;

public class ShowLocationBackgroundAction implements Action<Object, Void> {

    private final LocationId locationId;

    public ShowLocationBackgroundAction(LocationId locationId) {

        this.locationId = locationId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().showLocationBackground(locationId).subscribe(() -> {
                InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
                BackgroundData backgroundData = new BackgroundData(BackgroundModelRead.BackgroundType.LOCATION, locationId,
                        "encounter/background/" + locationId.lastWordToCamelCase() + ".jpg");
                ServicePlatform.get().getModel().getBackgroundModel().pushBackground(activeInvestigatorId, backgroundData);
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
