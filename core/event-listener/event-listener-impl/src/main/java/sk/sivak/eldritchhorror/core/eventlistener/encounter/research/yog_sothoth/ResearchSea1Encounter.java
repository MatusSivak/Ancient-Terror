package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea1Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchSea1Encounter() {
        super(1, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                this::gainThisClue,
                () -> {
                    ServicePlatform.get().getService().hold();
                    chooseOneFail(
                            () -> {
                                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                ServicePlatform.get().getService().hideBackground();
                                ServicePlatform.get().getTokenService().discardClue(getLocationId());
                            },
                            () -> {
                                if (ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) { // No Spells
                                    TypewriterUtils.confirmInfos("You don't have any Spells").subscribe(() -> {
                                        ServicePlatform.get().getService().hold();
                                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                        ServicePlatform.get().getService().hideBackground();
                                        ServicePlatform.get().getTokenService().discardClue(getLocationId());
                                        ServicePlatform.get().getService().release();
                                    });
                                } else {
                                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                    SelectCardData selectCardData = new SelectCardData();
                                    selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));
                                    selectCardData.setHideText("Display Spells?");
                                    selectCardData.setTitleText("Select Spell");
                                    ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscardByChoice);
                                }
                            });
                    ServicePlatform.get().getService().release();
                }
        ).withoutFailInfo().withoutHidingPaperBeforeFail().withResearchFlavor().execute();
    }



}
