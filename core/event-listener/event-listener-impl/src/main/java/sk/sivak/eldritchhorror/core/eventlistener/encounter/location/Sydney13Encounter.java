package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SpendCluePassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Sydney13Encounter extends AbstractLocationEncounter{

    public Sydney13Encounter() {
        super(13, LocationEncounter.LocationEncounterType.SYDNEY);
    }

    @Override
    protected void execute() {
       SpendCluePassFlavorInfoTemplate encounter = new SpendCluePassFlavorInfoTemplate(getTextBuilder(), () ->
               ServicePlatform.get().getGameService().gainAsset(ServicePlatform.get().getAssetDeck().findFirst(AssetTrait.WEAPON)))
               .withOnNoAction(() -> finishAfterFlavor(getTextBuilder().withFail().withFlavor().build()))
               .withOnMissingClue(() -> finishAfterFlavor(getTextBuilder().withFail().withFlavor(2).build()));
       if (!ServicePlatform.get().getAssetDeck().isAvailable(AssetTrait.WEAPON)) encounter.withPassWithoutEffect(2);
       encounter.execute();
    }

}
