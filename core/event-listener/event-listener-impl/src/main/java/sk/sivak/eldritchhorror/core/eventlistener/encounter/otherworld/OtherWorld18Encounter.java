package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class OtherWorld18Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld18Encounter() {
        super(18);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                this::closeThisGateAndEnd,
                () -> loseSanity(1))
                .withOtherWorldFlavor().withoutPassFlavor();

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), this::onNo, this::onYes);
        };

        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.INFLUENCE, 0, passTemplate, failTemplate).execute();
    }

    private void onNo() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeFlavor(" \n" +getTextBuilder().withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(
                getTextBuilder().withFail().withInfo(1).build(),
                getTextBuilder().withFail().withInfo(2).build()).subscribe(this::loseHealthAndGainInjury);
        ServicePlatform.get().getService().release();
    }



    private void onYes() {
        List<? extends CardInfo> investigatorItems = findInvestigatorItems();
        if (investigatorItems.isEmpty()) {
            TypewriterUtils.confirmInfos("[#BAD]You don't have any Item.[]").subscribe(this::onNo);
            return;
        }

        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        selectItemToDiscard(investigatorItems);
        closeThisGateAndEnd();
    }

    private void selectItemToDiscard(List<? extends CardInfo> investigatorItems) {
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(investigatorItems);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Item");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscardByChoice);
    }

    private void loseHealthAndGainInjury() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        loseHealth(1);
        gainCondition(ConditionTrait.INJURY);
        ServicePlatform.get().getService().release();
    }

    private List<? extends CardInfo> findInvestigatorItems() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM);
    }
}
