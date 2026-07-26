package sk.sivak.eldritchhorror.core.model;

import java8.features.function.Predicate;
import java8.features.function.Supplier;
import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AbstractAssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.asset.AxeAsset;
import sk.sivak.eldritchhorror.core.constants.asset.CarbineRifleAsset;
import sk.sivak.eldritchhorror.core.constants.asset.Dot45AutomaticAsset;
import sk.sivak.eldritchhorror.core.constants.asset.DoubleBarreledShotgunAsset;
import sk.sivak.eldritchhorror.core.constants.asset.LuckyRabbitsFootAsset;
import sk.sivak.eldritchhorror.core.constants.asset.PrivateCareAsset;
import sk.sivak.eldritchhorror.core.constants.asset.ProtectiveAmuletAsset;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.save.ArtifactsDeckSaveData;
import sk.sivak.eldritchhorror.core.model.save.AssetsDeckSaveData;
import sk.sivak.eldritchhorror.core.model.save.AssetsDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.util.AssetsDeckHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java8.features.stream.Stream.anyMatch;
import static java8.features.util.MapUtils.computeIfAbsent;
import static java8.features.util.MapUtils.getOrDefault;

/**
 * @author msivak
 */
public class AssetsDeck implements AssetsDeckWrite {

    private static final Logger logger = LogManager.getLogger(AssetsDeck.class);

    private ArrayList<AssetInfo> reserve;
    private LinkedList<AssetInfo> discard;
    private List<AssetInfo> deck;
    private Map<InvestigatorId, List<AssetInfo>> investigatorAssetsMap;

    private Supplier<List<AssetId>> unlockedAssetsSupplier;

    public AssetsDeck() {
    }

    @Override
    public void createDeck() {
        reserve = new ArrayList<>(4);
        discard = new LinkedList<>();
        deck = new AssetsDeckHelper().createDeck();
        investigatorAssetsMap = new HashMap<>();
        removeLockedAssets();
    }

    @Override
    public List<AssetId> getLockedAssets() {
        LinkedList<AssetId> allAssets = new LinkedList<>(Arrays.asList(AssetId.values()));
        List<AssetId> unlockedAssets = unlockedAssetsSupplier.get();
        IterableUtils.removeIf(allAssets, assetId -> unlockedAssets.contains(assetId));
        return allAssets;
    }

    public void setUnlockedAssetsSupplier(Supplier<List<AssetId>> unlockedAssetsSupplier) {
        this.unlockedAssetsSupplier = unlockedAssetsSupplier;
    }

    @Override
    public void removeTeamworkAssets() {
        IterableUtils.removeIf(deck, assetInfo -> assetInfo.getTraits().contains(AssetTrait.TEAMWORK));
    }



    private void removeLockedAssets() {
        List<AssetId> unlockedAssets = unlockedAssetsSupplier.get();
        IterableUtils.removeIf(deck, assetInfo -> !unlockedAssets.contains(assetInfo.getId()));
    }

    @Override
    public AssetInfo removeFromDeck(AssetId assetId) {
        return new AssetsDeckHelper().removeFromDeck(deck, assetId);
    }

    @Override
    public boolean removeFromReserve(AssetInfo assetInfo) {
        boolean remove = reserve.remove(assetInfo);
        if (remove) {
            logger.debug("Asset '" + assetInfo.getName() + "' removed from reserve.");
        }
        return remove;
    }

    @Override
    public boolean removeFromDiscardPile(AssetInfo assetInfo) {
        boolean remove = discard.remove(assetInfo);
        if (remove) {
            logger.debug("Asset '" + assetInfo.getName() + "' removed from discard pile.");
        }
        return remove;
    }

    @Override
    public void addToInvestigator(InvestigatorId investigatorId, AssetInfo assetInfo) {
        computeIfAbsent(investigatorAssetsMap, investigatorId, k -> new LinkedList<>());
        investigatorAssetsMap.get(investigatorId).add(assetInfo);
        logger.info("Asset '" + assetInfo.getName() + "' added to investigator '" + investigatorId + "'");
    }

    @Override
    public void initReserve() {
        if (!GoogleServicesHolder.isTutorialPassed() && reserve.isEmpty()) {
            AssetInfo whiskey = find(AssetId.WHISKEY);
            if (whiskey == null) {
                throw new IllegalStateException("Whiskey was not found... strange. Cards in deck: " + Stream.map(deck, AssetInfo::getId));
            }
            reserve.add(whiskey);
            reserve.add(find(AssetId.CHARTER_FLIGHT));
            reserve.add(find(AssetId.HIRED_MUSCLE));
            reserve.add(find(AssetId.ARCANE_MANUSCRIPTS));
            deck.remove(find(AssetId.WHISKEY));
            deck.remove(find(AssetId.CHARTER_FLIGHT));
            deck.remove(find(AssetId.HIRED_MUSCLE));
            deck.remove(find(AssetId.ARCANE_MANUSCRIPTS));
        }
        int amount = 4 - reserve.size();
        for (int i = 0; i < amount; i++) {
            if (deck.size() == 0) {
                return;
            }
            AssetInfo asset = deck.remove(0);
            logger.info("Asset '" + asset.getName() + "' added to reserve.");
            reserve.add(asset);
        }
    }

    @Override
    public void disable(AssetInfo assetInfo) {
        ((AbstractAssetInfo) assetInfo).disable();
    }

    @Override
    public void enable(AssetInfo assetInfo) {
        ((AbstractAssetInfo) assetInfo).enable();
    }

    @Override
    public void changeOwner(AssetInfo cardInfo, InvestigatorId newOwner) {
        for (Map.Entry<InvestigatorId, List<AssetInfo>> entry : investigatorAssetsMap.entrySet()) {
            if (entry.getValue().remove(cardInfo)) {
                logger.info("Asset '" + cardInfo.getName() + "' removed from investigator " + entry.getKey());
            }
        }
        addToInvestigator(newOwner, cardInfo);
    }

    @Override
    public void discard(InvestigatorId investigatorId, AssetInfo assetInfo) {
        investigatorAssetsMap.get(investigatorId).remove(assetInfo);
        discard.add(assetInfo);
        logger.info("Investigator '" + investigatorId + "' discarded asset '" + assetInfo.getName() + "'.");
    }

    @Override
    public void discardFromReserve(AssetInfo assetInfo) {
        if (!reserve.remove(assetInfo)) {
            throw new IllegalArgumentException("Asset '" + assetInfo.getName() + "' not found in reserve!");
        }
        discard.add(assetInfo);
        logger.info("Discarding '" + assetInfo.getName() + "' from reserve.");
    }

    @Override
    public List<AssetInfo> getReserve() {
        return reserve;
    }

    @Override
    public List<AssetInfo> getDiscardPile() {
        return discard;
    }

    @Override
    public List<AssetInfo> getAssets(InvestigatorId investigatorId) {
        return getOrDefault(investigatorAssetsMap, investigatorId, new LinkedList<>());
    }

    @Override
    public boolean hasAsset(InvestigatorId investigatorId, AssetId assetId) {
        List<AssetInfo> assets = getOrDefault(investigatorAssetsMap, investigatorId, new LinkedList<>());
        return Stream.anyMatch(assets, assetInfo -> assetInfo.getId() == assetId);
    }

    @Override
    public boolean hasAsset(InvestigatorId investigatorId, AssetTrait assetTrait) {
        List<AssetInfo> assets = getOrDefault(investigatorAssetsMap, investigatorId, new LinkedList<>());
        return Stream.anyMatch(assets, assetInfo -> assetInfo.getTraits().contains(assetTrait));
    }

    @Override
    public AssetInfo findFirst(AssetTrait trait) {
        if (Stream.anyMatch(deck, assetInfo -> assetInfo.getTraits().contains(trait))) {
            return Stream.findFirstOrException(deck, assetInfo -> assetInfo.getTraits().contains(trait));
        } else if (Stream.anyMatch(discard, assetInfo -> assetInfo.getTraits().contains(trait)) ){
            return Stream.findFirstOrException(discard, assetInfo -> assetInfo.getTraits().contains(trait));
        } else {
            return null;
        }
    }

    @Override
    public boolean isAvailable(AssetTrait trait) {
        if (Stream.anyMatch(deck, assetInfo -> assetInfo.getTraits().contains(trait))) {
            return true;
        } else if (Stream.anyMatch(discard, assetInfo -> assetInfo.getTraits().contains(trait))){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public AssetInfo findFirst() {
        return deck.get(0);
    }

    @Override
    public AssetInfo find(AssetId assetId) {
        Predicate<AssetInfo> hasSameId = assetInfo -> assetInfo.getId().equals(assetId);
        if (Stream.anyMatch(deck, hasSameId)) {
            return Stream.findFirstOrException(deck, hasSameId);
        } else if (Stream.anyMatch(discard, hasSameId)) {
            return Stream.findFirstOrException(discard, hasSameId);
        } else if (Stream.anyMatch(reserve, hasSameId)) {
            return Stream.findFirstOrException(reserve, hasSameId);
        } else {
            return null;
        }
    }

    @Override
    public void unlockCard(AssetId unlockedCard) {
        List<AssetId> unlockedCards = new LinkedList<>(unlockedAssetsSupplier.get());
        unlockedCards.add(unlockedCard);
        GoogleServicesHolder.overwriteUnlockedAssets(unlockedCards);
    }

    @Override
    public AssetsDeckSaveData save() {
        AssetsDeckSaveData assetsDeckSaveData = new AssetsDeckSaveData();
        assetsDeckSaveData.setDiscarded(Stream.collectToList(Stream.map(discard, AssetInfo::getId)));
        assetsDeckSaveData.setReserve(Stream.collectToList(Stream.map(reserve, AssetInfo::getId)));
        HashMap<String, List<? extends AssetsDeckSaveDataRead.OwnedAssetSaveDataRead>> saveDataMap = new HashMap<>();
        for (Map.Entry<InvestigatorId, List<AssetInfo>> entry : investigatorAssetsMap.entrySet()) {
            InvestigatorId key = entry.getKey();
            List<AssetsDeckSaveData.OwnedAssetSaveData> ownedAssets = Stream.collectToList(Stream.map(entry.getValue(), assetInfo -> {
                AssetsDeckSaveData.OwnedAssetSaveData ownedAssetSaveData = new AssetsDeckSaveData.OwnedAssetSaveData();
                ownedAssetSaveData.setAssetId(assetInfo.getId());
                ownedAssetSaveData.setDisabled(assetInfo.isDisabled());
                return ownedAssetSaveData;
            }));

            saveDataMap.put(key.name(), ownedAssets);
        }
        assetsDeckSaveData.setInvestigatorAssetsMap(saveDataMap);
        return assetsDeckSaveData;
    }

    @Override
    public void load(AssetsDeckSaveDataRead saveData) {
        createDeck();
        for (AssetId assetId : new HashSet<>(saveData.getReserve())) {
            AssetInfo assetInfo = Stream.findFirstOrException(deck, ai -> ai.getId().equals(assetId));
            reserve.add(assetInfo);
            deck.remove(assetInfo);
        }
        for (AssetId assetId : new HashSet<>(saveData.getDiscarded())) {
            AssetInfo assetInfo = Stream.findFirstOrException(deck, ai -> ai.getId().equals(assetId));
            discard.add(assetInfo);
            deck.remove(assetInfo);
        }
        for (Map.Entry<String, List<? extends AssetsDeckSaveDataRead.OwnedAssetSaveDataRead>> entry : saveData.getInvestigatorAssetsMap().entrySet()) {
            for (AssetsDeckSaveDataRead.OwnedAssetSaveDataRead ownedAsset : entry.getValue()) {
                AssetInfo assetInfo = Stream.findFirstOrException(deck, ai -> ai.getId().equals(ownedAsset.getAssetId()));
                addToInvestigator(InvestigatorId.valueOf(entry.getKey()), assetInfo);
                if (ownedAsset.isDisabled()) {
                    disable(assetInfo);
                }
                deck.remove(assetInfo);
            }
        }
    }
}
