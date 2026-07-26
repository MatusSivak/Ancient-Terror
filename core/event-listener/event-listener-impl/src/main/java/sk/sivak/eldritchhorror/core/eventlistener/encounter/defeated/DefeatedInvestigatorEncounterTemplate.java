package sk.sivak.eldritchhorror.core.eventlistener.encounter.defeated;

import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.DefeatedEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DefeatedInvestigatorEncounterData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId.THE_ROOKIE_COP;

public class DefeatedInvestigatorEncounterTemplate {
    private final DefeatedEncounterTextBuilder textBuilder;
    private final DefeatedInvestigatorEncounterData data;

    public DefeatedInvestigatorEncounterTemplate(DefeatedInvestigatorEncounterData data) {
        this.data = data;
        textBuilder = new DefeatedEncounterTextBuilder(data.getInvestigatorId(), data.isHealth());
    }

    public void execute() {

        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader((data.isHealth() ? "Crippled " : "Insane ") +  data.getInvestigatorId().toString());
        ServicePlatform.get().getEncounterService().typeFlavor(textBuilder.withFlavor().build());
        Completable confirmGainAllPossessions = TypewriterUtils.confirmInfos(textBuilder.withInfo().build());
        if (data.getInvestigatorId() == THE_ROOKIE_COP && !data.isHealth()) {
            confirmGainAllPossessions = TypewriterUtils.confirmInfos(
                    textBuilder.withInfo(1).build(),
                    textBuilder.withInfo(2).build());
        }
        confirmGainAllPossessions.subscribe(() -> {

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            ServicePlatform.get().getService().addEventCommand(in -> {
                data.getTransferItemsAction().run();
            });
            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
            ServicePlatform.get().getEncounterService().typeFlavor(textBuilder.withFlavor(2).build());
            TypewriterUtils.displayTestButton(data.isHealth() ? data.getCrippledTestStat() : data.getInsaneTestStat(), 0, this::onPass, this::onFail);
            ServicePlatform.get().getService().release();
        });
    }

    private void onPass() {
        ServicePlatform.get().getEncounterService().typeFlavor(textBuilder.withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(
                textBuilder.withPass().withInfo(1).build(),
                textBuilder.withPass().withInfo(2).build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (data.getInvestigatorId() == InvestigatorId.THE_VIOLINIST && !data.isHealth()) {
                closeOneGate();
            } else if (data.getInvestigatorId() == InvestigatorId.THE_WAITRESS && !data.isHealth()) {
                gainTwoSpells();
            } else if (data.getInvestigatorId() == InvestigatorId.THE_ROOKIE_COP && !data.isHealth()) {
                gainCarbineRifle();
            } else {
                ServicePlatform.get().getDoomOmenService().retreatDoom();
            }

            ServicePlatform.get().getService().release();
        });
    }


    private void onFail() {
        ServicePlatform.get().getEncounterService().typeFlavor(textBuilder.withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(textBuilder.withFail().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().release();
        });
    }

    private void closeOneGate() {
        SelectSingleGateData selectSingleGateData = new SelectSingleGateData(ServicePlatform.get().getGateStackRead().getSpawnedGates());
        selectSingleGateData.setTitleText("Select Gate to discard");
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getGameService().selectSingleGate(selectSingleGateData).subscribe(gateInfo -> {
            ServicePlatform.get().getService().closeGate(gateInfo.getLocationId(), false);
        });
    }

    private void gainTwoSpells() {
        ServicePlatform.get().getGameService().gainSpell();
        ServicePlatform.get().getGameService().gainSpell();
    }

    private void gainCarbineRifle() {
        ServicePlatform.get().getGameService().gainAsset(ServicePlatform.get().getAssetDeck().find(AssetId.CARBINE_RIFLE));
    }


}
