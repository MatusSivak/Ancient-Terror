package sk.sivak.eldritchhorror.core.eventlistener.asset;

import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public abstract class AbstractAssetEventListener<DATA> extends EventListenerImpl<DATA> {

    protected AbstractAssetListener abstractAssetListener;

    public AbstractAssetEventListener(AbstractAssetListener abstractAssetListener) {
        this.abstractAssetListener = abstractAssetListener;
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
        return abstractAssetListener.getOwner().equals(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
    }

    @Override
    public abstract Class<DATA> getDataClass();

    protected UsableAsset addUsableAsset(TestData testData, AssetInfo assetInfo, int statBonus) {
        return addUsableAsset(testData, assetInfo, statBonus, 0);
    }

    protected UsableAsset addUsableAsset(TestData testData, AssetInfo assetInfo, int statBonus, int dicePoolBonus) {
        UsableAsset usableAsset = new UsableAsset();
        usableAsset.setCardInfo(assetInfo);
        usableAsset.setStatBonus(statBonus);
        usableAsset.setDicePoolBonus(dicePoolBonus);
        usableAsset.setUpByDefault(true);
        testData.addUsableAsset(usableAsset);
        return usableAsset;
    }


}
