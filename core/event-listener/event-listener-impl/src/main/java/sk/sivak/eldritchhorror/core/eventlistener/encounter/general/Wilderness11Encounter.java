package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Wilderness11Encounter extends AbstractGeneralEncounter {

    public Wilderness11Encounter() {
        super(11, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);
    }

    private void onYes() {
        boolean hasMadness = ServicePlatform.get().getConditionsDeck().hasTrait(
                ServicePlatform.get().getInvestigators().getActiveInvestigatorId(),
                ConditionTrait.MADNESS);
        if (hasMadness) {
            onHasMadness();
        } else {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
            if (!ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
                ServicePlatform.get().getTokenService().gainSanity(3);
            }
            ServicePlatform.get().getService().release();
        }
    }

    private void onHasMadness() {
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_REWARD);
        ServicePlatform.get().getEncounterService().displayButtons(
                getTextBuilder().withOption(1).build(),
                getTextBuilder().withOption(2).build()
        ).subscribe(x -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
            if (!ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
                if (x == 0) {
                    ServicePlatform.get().getTokenService().gainSanity(3);
                } else {
                    selectMadnessToDiscard();
                }
            }
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private void selectMadnessToDiscard() {
        List<ConditionInfo> madnessConditions = ServicePlatform.get().getConditionsDeck()
                .getCondition(getActiveInvestigatorId(), ConditionTrait.MADNESS);
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(madnessConditions);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Madness Condition");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }
}
