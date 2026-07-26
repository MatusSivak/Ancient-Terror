package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Sydney16Encounter extends AbstractLocationEncounter{

    public Sydney16Encounter() {
        super(16, LocationEncounter.LocationEncounterType.SYDNEY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                () -> ServicePlatform.get().getGameService().gainAsset(ServicePlatform.get().getAssetDeck().findFirst(AssetTrait.WEAPON)),
                () -> {
                    ServicePlatform.get().getTokenService().loseHealth(1);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.LEG_INJURY);
                }
        ).withTwoFailInfos().execute();
    }

}
