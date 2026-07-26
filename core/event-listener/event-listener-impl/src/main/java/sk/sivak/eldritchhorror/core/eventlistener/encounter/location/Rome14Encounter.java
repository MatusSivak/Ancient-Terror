package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Rome14Encounter extends AbstractLocationEncounter{

    public Rome14Encounter() {
        super(14, LocationEncounter.LocationEncounterType.ROME);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), null, this::onYes);
    }

    private void onYes() {
        List<? extends CardInfo> investigatorItems = findInvestigatorItems();
        if (investigatorItems.isEmpty()) {
            TypewriterUtils.confirmInfos("You don't have any Item.").subscribe(() ->{
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
            return;
        }
        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
        selectItemToDiscard(investigatorItems);
        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
        ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().improveSkill(Stat.WILL);
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private void selectItemToDiscard(List<? extends CardInfo> investigatorItems) {
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(investigatorItems);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Item");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscardByChoice);
    }

    private List<? extends CardInfo> findInvestigatorItems() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM);
    }

}
