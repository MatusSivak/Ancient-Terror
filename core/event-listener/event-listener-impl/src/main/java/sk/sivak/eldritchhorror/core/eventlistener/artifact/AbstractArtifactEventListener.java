package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public abstract class AbstractArtifactEventListener<DATA> extends EventListenerImpl<DATA> {

    protected AbstractArtifactListener abstractArtifactListene;

    public AbstractArtifactEventListener(AbstractArtifactListener abstractArtifactListene) {
        this.abstractArtifactListene = abstractArtifactListene;
    }

    @Override
    public void onNotify(DATA eventData) {
        if (!isOwner()) {
            return;
        }
        ServicePlatform.get().getService().<DATA>addEventCommand((input) -> {
            getEventAction(input).run();
        });
    }

    protected abstract Runnable getEventAction(DATA input);

    private boolean isOwner() {
        return abstractArtifactListene.getOwner().equals(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
    }

    @Override
    public abstract Class<DATA> getDataClass();

    protected UsableAsset addUsableArtifact(TestData testData, ArtifactInfo artifactInfo, int statBonus) {
        return addUsableAsset(testData, artifactInfo, statBonus, 0);
    }

    protected UsableAsset addUsableAsset(TestData testData, ArtifactInfo artifactInfo, int statBonus, int dicePoolBonus) {
        UsableAsset usableAsset = new UsableAsset();
        usableAsset.setCardInfo(artifactInfo);
        usableAsset.setStatBonus(statBonus);
        usableAsset.setDicePoolBonus(dicePoolBonus);
        usableAsset.setUpByDefault(true);
        testData.addUsableAsset(usableAsset);
        return usableAsset;
    }


}
