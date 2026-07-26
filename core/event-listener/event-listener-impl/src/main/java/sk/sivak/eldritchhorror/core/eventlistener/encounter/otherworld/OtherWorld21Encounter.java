package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SomethingUnlessSpendTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class OtherWorld21Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld21Encounter() {
        super(21);
    }

    @Override
    protected void execute() {
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::gainTwoClues,
                new SomethingUnlessSpendTemplate(getTextBuilder(), new ClueFocusHealthSanity(0, 0, 1, 0), this::becomeDelayed)
                        .setComplementaryOnSpendAction(() -> {
                            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                            ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withPass().withFlavor().build());
                            TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                                ServicePlatform.get().getEncounterService().hold();
                                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                closeThisGateAndEnd();
                                ServicePlatform.get().getEncounterService().release();
                            });
                        }));

        EncounterTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(), () -> loseHealth(1),
                new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                        () -> {
                            List<? extends CardInfo> investigatorItems = findInvestigatorItems();
                            if (investigatorItems.isEmpty()) {
                                return;
                            }
                            selectItemToDiscard(investigatorItems);
                        })
                        .withOtherWorldFlavor().withoutFailFlavor());

        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.LORE, 0, passTemplate, failTemplate).execute();
    }

    private void selectItemToDiscard(List<? extends CardInfo> investigatorItems) {
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(investigatorItems);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Item");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }

    private List<? extends CardInfo> findInvestigatorItems() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM);
    }
}
