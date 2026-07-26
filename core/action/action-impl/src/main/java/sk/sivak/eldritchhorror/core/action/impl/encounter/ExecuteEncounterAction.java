package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.CombatEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.ConditionEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.DefeatedInvestigatorEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.ExpeditionEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.GeneralEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.LocationEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.MysteryEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.OtherWorldEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.ResearchEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.RlyehRisenEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.RumorEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.SkipEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.TheKeyAndTheGateEncounterExecutor;
import sk.sivak.eldritchhorror.core.action.impl.encounter.execute.VoidBetweenWorldsEncounterExecutor;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.CombatEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ConditionEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.DefeatedInvestigatorEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.EncounterResult;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ExpeditionEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.GeneralEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.MysteryEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.OtherWorldEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ResearchEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.RumorEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.SkipEncounter;

public class ExecuteEncounterAction implements Action<Encounter, EncounterResult> {


    private Encounter input;

    private boolean canHaveAnotherEncounter = false;

    @Override
    public Single<EncounterResult> execute() {

        return Single.create(sub -> {
            if (input == null) {
                EncounterResult encounterResult = new EncounterResult();
                encounterResult.setCanHaveAnotherEncounter(false);
                encounterResult.setLast(ServicePlatform.get().getInvestigators().isActiveLast());
                sub.onSuccess(encounterResult);
                return;
            }

            ServicePlatform.get().getGameController().hideCityInfoLabels();
            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            ServicePlatform.get().getPerformedEncounters()
                    .perform(activeInvestigatorId, input.getEncounterType(), input.getUuid());

            ServicePlatform.get().getService().hold();
            switch (input.getEncounterType()) {
                case GENERAL:
                    GeneralEncounterExecutor.execute((GeneralEncounter) input);
                    break;
                case LOCATION:
                    LocationEncounterExecutor.execute((LocationEncounter) input);
                    break;
                case OTHER_WORLD:
                    OtherWorldEncounterExecutor.execute((OtherWorldEncounter) input);
                    break;
                case RLYEH_RISEN:
                    RlyehRisenEncounterExecutor.execute((MysteryEncounter) input);
                    break;
                case VOID_BETWEEN_WORLDS:
                    VoidBetweenWorldsEncounterExecutor.execute((MysteryEncounter) input);
                    break;
                case THE_KEY_AND_THE_GATE:
                    TheKeyAndTheGateEncounterExecutor.execute((MysteryEncounter) input);
                    break;
                case EXPEDITION:
                    ExpeditionEncounterExecutor.execute((ExpeditionEncounter)input);
                    break;
                case MYSTERY:
                    MysteryEncounterExecutor.execute((MysteryEncounter) input);
                    break;
                case RUMOR:
                    RumorEncounterExecutor.execute((RumorEncounter) input);
                    break;
                case RESEARCH:
                    ResearchEncounterExecutor.execute((ResearchEncounter)input);
                    break;
                case COMBAT:
                    canHaveAnotherEncounter = true;
                    CombatEncounterExecutor.execute((CombatEncounter) input);
                    break;
                case CONDITION:
                    ConditionEncounterExecutor.execute((ConditionEncounter)input);
                    break;
                case DEFEATED_INVESTIGATOR:
                    DefeatedInvestigatorEncounterExecutor.execute((DefeatedInvestigatorEncounter)input);
                    break;
                case SKIP:
                    SkipEncounterExecutor.execute();
                    break;
            }
            ServicePlatform.get().getService().convertTo(Object.class, () -> {
                ServicePlatform.get().getGameController().showCityInfoLabels();
                EncounterResult encounterResult = new EncounterResult();
                if (!ServicePlatform.get().getPerformedEncounters().canHaveAnotherEncounter(activeInvestigatorId)) {
                    encounterResult.setCanHaveAnotherEncounter(false);
                } else if (ServicePlatform.get().getInvestigators().getToBeReplacedInvestigators().contains(activeInvestigatorId)) {
                    encounterResult.setCanHaveAnotherEncounter(false);
                } else {
                    encounterResult.setCanHaveAnotherEncounter(true);
                }

                encounterResult.setLast(ServicePlatform.get().getInvestigators().isActiveLast());
                return encounterResult;
            });
            ServicePlatform.get().getService().release();
            sub.onSuccess(null);
        });

    }

    @Override
    public void setInput(Encounter input) {
        this.input = input;
    }
}
