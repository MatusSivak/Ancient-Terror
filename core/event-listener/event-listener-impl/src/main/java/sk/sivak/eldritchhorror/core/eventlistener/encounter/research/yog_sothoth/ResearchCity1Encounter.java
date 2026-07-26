package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity1Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchCity1Encounter() {
        super(1, LocationType.CITY);
    }

    @Override
    protected void execute() {
        Runnable onSuccess = () -> {
            if (ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainThisClue();
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                gainThisClue();
                ServicePlatform.get().getService().showResearchBackground(LocationType.CITY);
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
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
                        ServicePlatform.get().getGameService().gainArtifact(ArtifactId.THE_SILVER_KEY);
                        ServicePlatform.get().getService().release();
                    });

                    ServicePlatform.get().getService().release();
                });
                ServicePlatform.get().getService().release();
            }
        };
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                onSuccess,
                () -> {
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED);
                }
        ).withoutHidingPaperBeforePass().withResearchFlavor().execute();
    }
}
