package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea7Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchSea7Encounter() {
        super(7, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                    gainThisClue();
                    ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                    ServicePlatform.get().getService().showResearchBackground(LocationType.SEA);
                    TypewriterUtils.noYesQuestion(" \n"+getTextBuilder().withPass().withQuestion().build(),
                            () ->  ServicePlatform.get().getEncounterService().finishTypewriterPaper(),
                            () -> {
                                if (ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) {
                                    TypewriterUtils.confirmInfos("You don't have any Spells.").subscribe(() -> {
                                        ServicePlatform.get().getService().hold();
                                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                        ServicePlatform.get().getService().release();
                                    });
                                    return;
                                }
                                ServicePlatform.get().getService().hold();
                                ServicePlatform.get().getEncounterService().finishTypewriterPaper();

                                SelectCardData selectCardData = new SelectCardData();
                                selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));
                                selectCardData.setHideText("Display Spells?");
                                selectCardData.setTitleText("Select Spell");
                                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(spell -> {
                                    ServicePlatform.get().getService().hold();
                                    EncounterUtils.onSelectCardToDiscardByChoice(spell);

                                    ServicePlatform.get().getService().hideBackground();
                                    List<GateInfo> spawnedGates = ServicePlatform.get().getGateStackRead().getSpawnedGates();
                                    ServicePlatform.get().getGameService().selectSingleGate(new SelectSingleGateData(spawnedGates)).subscribe(selectedGate -> {
                                        ServicePlatform.get().getService().hold();
                                        ServicePlatform.get().getService().discardGate(selectedGate.getLocationId());
                                        ServicePlatform.get().getService().release();
                                    });

                                    ServicePlatform.get().getService().release();
                                });

                                ServicePlatform.get().getService().release();
                            });
                    ServicePlatform.get().getService().release();
                }).withoutPassFlavor().withoutHidingPaperBeforePass().withResearchFlavor().execute();
    }
}
