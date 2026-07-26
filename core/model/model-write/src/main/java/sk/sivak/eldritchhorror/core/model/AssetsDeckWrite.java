package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.save.AssetsDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

import java.util.List;

/**
 * @author msivak
 */
public interface AssetsDeckWrite extends AssetDeckRead {

    void removeTeamworkAssets();

    void createDeck();

    AssetInfo removeFromDeck(AssetId assetId);

    boolean removeFromReserve(AssetInfo assetInfo);

    boolean removeFromDiscardPile(AssetInfo assetInfo);

    void addToInvestigator(InvestigatorId investigatorId, AssetInfo assetInfo);

    void discard(InvestigatorId investigatorId, AssetInfo assetInfo);

    void discardFromReserve(AssetInfo assetInfo);

    void initReserve();

    void disable(AssetInfo assetInfo);

    void enable(AssetInfo assetInfo);

    void changeOwner(AssetInfo cardInfo, InvestigatorId newOwner);

    void unlockCard(AssetId unlockedCard);

    SaveDataWrite.AssetsDeckSaveDataWrite save();

    void load(AssetsDeckSaveDataRead assetsDeckSaveData);
}
