package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity17Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchCity17Encounter() {
        super(17, LocationType.CITY);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1, () -> {
            gainThisClue();
        }, () -> {

        }) {
            @Override
            protected void onFail() {
                ServicePlatform.get().getEncounterService().typeFlavor(getTextBuilder().withFail().withFlavor().build());

                boolean canGetDarkPact = ServicePlatform.get().getConditionsDeck().canGetConditionId(getActiveInvestigatorId(), ConditionId.DARK_PACT);
                if (canGetDarkPact) {
                    ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_ONE);
                    ServicePlatform.get().getEncounterService().displayButtons(
                            getTextBuilder().withFail().withOption(1).build(),
                            getTextBuilder().withFail().withOption(2).build()
                    ).subscribe(option -> {
                        ServicePlatform.get().getEncounterService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        if (option == 0) {
                            ServicePlatform.get().getDoomOmenService().advanceDoom(2);
                        } else {
                            ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
                        }
                        ServicePlatform.get().getEncounterService().release();
                    });
                } else {
                    TypewriterUtils.confirmInfos(getTextBuilder().withFail().withOption(1).build()).subscribe(() -> {
                        ServicePlatform.get().getEncounterService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getDoomOmenService().advanceDoom(2);
                        ServicePlatform.get().getEncounterService().release();
                    });
                }
            }

        }.withResearchFlavor().execute();


    }
}
