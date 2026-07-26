package sk.sivak.eldritchhorror.core.action.impl.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import java.util.List;

public class DevourInvestigatorAction extends AbstractHookableAction<Object, Void> {

    private final InvestigatorId investigatorId;

    public DevourInvestigatorAction(InvestigatorId investigatorId) {
        super(BeforeAfterEvent.DEVOUR_INVESTIGATOR);
        this.investigatorId = investigatorId;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getDoomOmenService().advanceDoom();
        ServicePlatform.get().getEventListenerProvider().unregisterInvestigatorListeners(investigatorId);
        discardAssets(investigatorId);
        discardArtifacts(investigatorId);
        discardConditions(investigatorId);
        discardSpells(investigatorId);
        discardClues(investigatorId);
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator();
        ServicePlatform.get().getService().addEventCommand(in -> {
            if (ServicePlatform.get().getInvestigators().getLeadInvestigator() == null) {
                ServicePlatform.get().getInvestigatorService().selectNewLeadInvestigator(false);
            }
        });
        ServicePlatform.get().getService().convertToNull();
        ServicePlatform.get().getService().release();

        ServicePlatform.get().getInvestigators().devourInvestigator(investigatorId);

        ServicePlatform.get().getGameController().devourInvestigator(investigatorId).subscribe(() -> {
            ss.onSuccess(null);
        });
    }

    private void discardClues(InvestigatorId activeInvestigatorId) {
        int clueCount = ServicePlatform.get().getCluePool().getClueCount(activeInvestigatorId);
        for (int i = 0; i < clueCount; i++) {
            ServicePlatform.get().getCluePool().loseClue(activeInvestigatorId);
        }
    }

    private void discardAssets(InvestigatorId activeInvestigatorId) {
        List<AssetInfo> assets = ServicePlatform.get().getAssetsDeck().getAssets(activeInvestigatorId);
        for (AssetInfo asset : assets) {
            ServicePlatform.get().getService().discardAssetFromInvestigator(activeInvestigatorId, asset, false);
        }
    }

    private void discardArtifacts(InvestigatorId activeInvestigatorId) {
        List<ArtifactInfo> artifacts= ServicePlatform.get().getArtifactsDeck().getArtifacts(activeInvestigatorId);
        for (ArtifactInfo artifact : artifacts) {
            ServicePlatform.get().getService().discardArtifactFromInvestigator(activeInvestigatorId, artifact, true);
        }
    }

    private void discardSpells(InvestigatorId activeInvestigatorId) {
        List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(activeInvestigatorId);
        for (SpellInfo spell : spells) {
            ServicePlatform.get().getService().discardSpellFromInvestigator(activeInvestigatorId, spell);
        }
    }

    private void discardConditions(InvestigatorId activeInvestigatorId) {
        List<ConditionInfo> conditions = ServicePlatform.get().getConditionsDeck().getConditions(activeInvestigatorId);
        for (ConditionInfo condition : conditions) {
            ServicePlatform.get().getService().discardConditionFromInvestigator(activeInvestigatorId, condition);
        }
    }
}
