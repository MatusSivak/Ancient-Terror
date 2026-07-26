package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SpendCluePassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Sydney13Encounter extends AbstractLocationEncounter{

    public Sydney13Encounter() {
        super(13, LocationEncounter.LocationEncounterType.SYDNEY);
    }

    @Override
    protected void execute() {
       new SpendCluePassFlavorInfoTemplate(getTextBuilder(), () ->
               ServicePlatform.get().getGameService().gainAsset(ServicePlatform.get().getAssetDeck().findFirst(AssetTrait.WEAPON))).execute();
    }

}
