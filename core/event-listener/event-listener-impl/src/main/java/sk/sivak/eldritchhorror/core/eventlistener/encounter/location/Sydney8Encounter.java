package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Sydney8Encounter extends AbstractLocationEncounter{

    public Sydney8Encounter() {
        super(8, LocationEncounter.LocationEncounterType.SYDNEY);
    }

    @Override
    protected void execute() {
        BecomeDelayedPassFlavorInfoTemplate encounter = new BecomeDelayedPassFlavorInfoTemplate(getTextBuilder(), () ->
                ServicePlatform.get().getGameService().gainAsset(ServicePlatform.get().getAssetDeck().findFirst(AssetTrait.WEAPON))
        ).withAnswerFlavors();
        if (!ServicePlatform.get().getAssetDeck().isAvailable(AssetTrait.WEAPON)) encounter.withPassWithoutEffect(3);
        encounter.execute();
    }

}
