package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.AmbushTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness2Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchWilderness2Encounter() {
        super(2, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {

            TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                if (ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()).isEmpty()) { // No Spells
                    acquireClue(2);
                    TypewriterUtils.confirmInfos("End of Encounter.").subscribe(() -> {
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    });
                } else {
                    acquireClue(3);
                    new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0, () -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getTokenService().loseSanity(1);
                        ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);
                        ServicePlatform.get().getService().release();
                    }).withTwoFailInfos().withResearchFlavor().execute();
                }
                ServicePlatform.get().getService().release();

            });
    }

    private void acquireClue(int flavor) {
        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
        gainThisClue();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
        ServicePlatform.get().getService().showResearchBackground(LocationType.WILDERNESS);
        ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withFlavor(flavor).build());
    }
}
