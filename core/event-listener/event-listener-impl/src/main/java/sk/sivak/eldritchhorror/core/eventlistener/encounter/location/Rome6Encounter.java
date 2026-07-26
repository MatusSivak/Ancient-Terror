package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Rome6Encounter extends AbstractLocationEncounter{

    public Rome6Encounter() {
        super(6, LocationEncounter.LocationEncounterType.ROME);
    }

    @Override
    protected void execute() {

        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.BLESSED),
                () -> {
                    ServicePlatform.get().getTokenService().loseSanity(1);
                    discardBlessedCondition();
                }).withTwoFailInfos().execute();
    }

    private void discardBlessedCondition() {
        InvestigatorId activeInvestigatorId = getActiveInvestigatorId();
        boolean isBlessed = ServicePlatform.get().getConditionsDeck().hasCondition(activeInvestigatorId, ConditionId.BLESSED);
        if (!isBlessed) {
            return;
        }
        ConditionInfo blessedCondition = ServicePlatform.get().getConditionsDeck().getCondition(activeInvestigatorId, ConditionId.BLESSED);
        ShowCardRequest request = new ShowCardRequest();
        request.setHideType(HideType.DISCARD_ALWAYS);
        request.setConditionInfo(blessedCondition);
        request.setTitle("Discard Blessed Condition");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
        ServicePlatform.get().getGameService().showCard(request).subscribe(out -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(activeInvestigatorId, blessedCondition);
        });

    }

}
