package sk.sivak.eldritchhorror.core.action.impl.investigator;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class DiscardDefeatedInvestigatorAction implements Action<Object, Void> {

    private final InvestigatorId investigatorId;

    public DiscardDefeatedInvestigatorAction(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            // TODO move camera to that location

            InvestigatorRead defeatedInvestigator = Stream.findFirstOrException(ServicePlatform.get().getInvestigators().getDefeatedInvestigators(),
                    investigator -> investigator.getInfo().getInvestigatorId() == investigatorId);
            ServicePlatform.get().getGameController().moveCameraToLocation(defeatedInvestigator.getCurrentLocationId()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEventListenerProvider().unregisterDefeatedListener(investigatorId);
                discardAssets(investigatorId);
                discardArtifacts(investigatorId);
                discardSpells(investigatorId);
                discardClues(investigatorId);

                ServicePlatform.get().getInvestigatorService().removeDefeatedInvestigator(investigatorId);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
                onSub.onSuccess(null);
            });
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
        List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(investigatorId);
        for (SpellInfo spell : spells) {
            ServicePlatform.get().getService().discardSpellFromInvestigator(activeInvestigatorId, spell);
        }
    }


    @Override
    public void setInput(Object input) {

    }


}
