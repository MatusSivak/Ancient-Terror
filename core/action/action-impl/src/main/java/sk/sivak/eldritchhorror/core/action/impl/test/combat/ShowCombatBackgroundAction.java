package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.model.BackgroundData;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead;

import java.util.Locale;

public class ShowCombatBackgroundAction implements Action<Object, Void> {

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform platform = ServicePlatform.get();
            InvestigatorId activeInvestigatorId = platform.getInvestigators().getActiveInvestigatorId();
            LocationId location = platform.getInvestigators().getActiveInvestigator().getCurrentLocationId();
            String variant = platform.getGateStack().isGateAtLocation(location)
                    ? platform.getGateStack().getGateColor(location).name()
                    : platform.getLocationMap().getLocationInfo(location).getLocationType().name();
            String backgroundName = "encounter/background/combat_" + variant.toLowerCase(Locale.ROOT) + ".jpg";
            platform.getGameController().showCombatBackground(backgroundName).subscribe(() -> {
                BackgroundData backgroundData = new BackgroundData(BackgroundModelRead.BackgroundType.COMBAT, null, backgroundName);
                platform.getModel().getBackgroundModel().pushBackground(activeInvestigatorId, backgroundData);
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
