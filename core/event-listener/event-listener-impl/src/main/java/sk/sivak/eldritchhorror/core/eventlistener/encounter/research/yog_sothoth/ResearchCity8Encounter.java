package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity8Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchCity8Encounter() {
        super(8, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                () -> {

                    if (ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) {
                        TypewriterUtils.confirmInfos("You don't have any Spells.").subscribe(() -> {
                            ServicePlatform.get().getService().hold();
                            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                            ServicePlatform.get().getService().release();
                        });
                        return;
                    }

                    TypewriterUtils.noYesQuestion(" \n"+getTextBuilder().withPass().withQuestion().build(), () -> {
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    }, () -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();

                        SelectCardData selectCardData = new SelectCardData();
                        selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));
                        selectCardData.setHideText("Display Spells?");
                        selectCardData.setTitleText("Select Spell");
                        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(spell -> {
                            ServicePlatform.get().getService().hold();
                            EncounterUtils.onSelectCardToDiscardByChoice(spell);
                            gainThisClue();
                            ServicePlatform.get().getTokenService().gainClueFromPool();
                            ServicePlatform.get().getService().release();
                        });

                        ServicePlatform.get().getService().release();
                    });
                },
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().hideBackground();
                    ServicePlatform.get().getTokenService().discardClue(getLocationId());
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
                    ServicePlatform.get().getService().release();
                }
        ).withoutHidingPaperBeforePass().withoutPassInfo().withTwoFailInfos().withResearchFlavor().execute();
    }
}
