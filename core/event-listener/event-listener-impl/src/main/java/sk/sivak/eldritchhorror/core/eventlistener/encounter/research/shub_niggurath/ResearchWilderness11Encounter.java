package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ResearchWilderness11Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness11Encounter() {
        super(11, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                getTextBuilder().withInfo(1).build(),
                getTextBuilder().withInfo(2).build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            discardDefeatedInvestigator();
            ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.GOAT_SPAWN).subscribe(combatData -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                if (!combatData.getMonsterInfo().isAlive()) { // PASS
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().typeInfo("[#GOOD]" + combatData.getMonsterInfo().getName() + " was defeated.[]");
                        ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withPass().withFlavor().build());
                        TypewriterUtils.confirmInfos(
                                getTextBuilder().withPass().withInfo(1).build(),
                                getTextBuilder().withPass().withInfo(2).build()).subscribe(() -> {
                                    ServicePlatform.get().getService().hold();
                                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                    ServicePlatform.get().getTokenService().loseSanity(1);
                                    gainThisClue();
                                    ServicePlatform.get().getService().release();
                        });
                        ServicePlatform.get().getService().release();
                } else {
                    TypewriterUtils.confirmInfos(
                            "[#BAD]"+combatData.getMonsterInfo().getName()+" was not defeated.[]",
                            getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                        ServicePlatform.get().getEncounterService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getDoomOmenService().advanceDoom();
                        ServicePlatform.get().getEncounterService().release();
                    });
                }
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getEncounterService().release();
        });

    }

    private void discardDefeatedInvestigator() {
        if (!ServicePlatform.get().getInvestigators().getDefeatedInvestigators().isEmpty()) {
            ServicePlatform.get().getService().hideBackground();
            LinkedList<? extends InvestigatorRead> defeatedInvestigators = new LinkedList<>(ServicePlatform.get().getInvestigators().getDefeatedInvestigators());
            Collections.shuffle(defeatedInvestigators);
            ServicePlatform.get().getInvestigatorService().discardDefeatedInvestigator(defeatedInvestigators.get(0).getInfo().getInvestigatorId());
            ServicePlatform.get().getService().showResearchBackground(LocationType.SEA);
        }
    }
}
