package sk.sivak.eldritchhorror.core.eventlistener.encounter.defeated;

import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.DefeatedEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DefeatedInvestigatorEncounterData;

import static sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId.THE_ROOKIE_COP;

public class DefeatedInvestigatorEncounterTemplate {
    private static final int ANY_RESERVE_VALUE = 99;
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
        Completable confirmGainAllPossessions;
        if (data.getInvestigatorId() == THE_ROOKIE_COP && !data.isHealth()) {
            confirmGainAllPossessions = TypewriterUtils.confirmInfos(
                    textBuilder.withInfo(1).build(),
                    textBuilder.withInfo(2).build());
        } else {
            confirmGainAllPossessions = TypewriterUtils.confirmInfos(textBuilder.withInfo().build());
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
        if (data.getInvestigatorId() == InvestigatorId.THE_VIOLINIST && !data.isHealth()
                && ServicePlatform.get().getGateStackRead().getSpawnedGates().isEmpty()) {
            ServicePlatform.get().getEncounterService().typeFlavor(textBuilder.withPass().withFlavor(2).build());
            TypewriterUtils.confirmInfos("There are no Gates to close.", textBuilder.withPass().withInfo(2).build())
                    .subscribe(() -> ServicePlatform.get().getEncounterService().finishTypewriterPaper());
            return;
        }
        ServicePlatform.get().getEncounterService().typeFlavor(textBuilder.withPass().withFlavor().build());
        TypewriterUtils.confirmInfos(
                textBuilder.withPass().withInfo(1).build(),
                textBuilder.withPass().withInfo(2).build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            if (data.getInvestigatorId() == InvestigatorId.THE_REPORTER && !data.isHealth()) {
                offerCurseForDoom();
                ServicePlatform.get().getService().release();
                return;
            }
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (data.getInvestigatorId() == InvestigatorId.THE_VIOLINIST && !data.isHealth()) {
                closeOneGate();
            } else if (data.getInvestigatorId() == InvestigatorId.THE_WAITRESS && !data.isHealth()) {
                gainTwoSpells();
            } else if (data.getInvestigatorId() == InvestigatorId.THE_ROOKIE_COP && !data.isHealth()) {
                gainCarbineRifle();
            } else if (data.getInvestigatorId() == InvestigatorId.THE_EXPLORER && !data.isHealth()) {
                ServicePlatform.get().getGameService().gainArtifact();
            } else if (data.getInvestigatorId() == InvestigatorId.THE_PRIEST && data.isHealth()) {
                ServicePlatform.get().getGameService().gainCondition(sk.sivak.eldritchhorror.core.constants.condition.ConditionId.BLESSED);
            } else if ((data.getInvestigatorId() == InvestigatorId.THE_DILETTANTE && data.isHealth())
                    || (data.getInvestigatorId() == InvestigatorId.THE_SALESMAN && !data.isHealth())) {
                ServicePlatform.get().getCardService().acquireAssets(ANY_RESERVE_VALUE);
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

    private void offerCurseForDoom() {
        TypewriterUtils.noYesQuestion("Gain a Cursed Condition to retreat Doom by 3?",
                () -> finishBargain(false), () -> finishBargain(true));
    }

    private void finishBargain(boolean accepted) {
        ServicePlatform.get().getEncounterService().typeFlavor(
                textBuilder.withPass().withOption(accepted ? 1 : 2).withFlavor().build());
        ServicePlatform.get().getEncounterService().displayButtons("OK").subscribe(button -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (accepted) {
                ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
                for (int i = 0; i < 3; i++) ServicePlatform.get().getDoomOmenService().retreatDoom();
            }
            ServicePlatform.get().getService().release();
        });
    }

    private void closeOneGate() {
        SelectSingleGateData selectSingleGateData = new SelectSingleGateData(ServicePlatform.get().getGateStackRead().getSpawnedGates());
        selectSingleGateData.setTitleText("Select Gate to discard");
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getGameService().selectSingleGate(selectSingleGateData).subscribe(gateInfo -> {
            if (gateInfo != null) ServicePlatform.get().getService().closeGate(gateInfo.getLocationId(), false);
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
