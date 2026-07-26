package sk.sivak.eldritchhorror.core.model;

import java8.features.function.Predicate;
import java8.features.function.Supplier;
import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.save.ArtifactsDeckSaveData;
import sk.sivak.eldritchhorror.core.model.save.ArtifactsDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.util.ArtifactsDeckHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java8.features.util.MapUtils.computeIfAbsent;
import static java8.features.util.MapUtils.getOrDefault;

/**
 * @author msivak
 */
public class ArtifactsDeck implements ArtifactsDeckWrite {

    private static final Logger logger = LogManager.getLogger(ArtifactsDeck.class);
    
    private LinkedList<ArtifactInfo> discard;
    private List<ArtifactInfo> deck;
    private Map<InvestigatorId, List<ArtifactInfo>> investigatorArtifactsMap;
    private Supplier<List<ArtifactId>> unlockedArtifactsSupplier;

    @Override
    public void createDeck() {
        discard = new LinkedList<>();
        investigatorArtifactsMap = new HashMap<>();
        deck = new ArtifactsDeckHelper().createDeck();
        removeLockedArtifacts();
    }

    @Override
    public List<ArtifactId> getLockedArtifacts() {
        LinkedList<ArtifactId> allArtifacts = new LinkedList<>(Arrays.asList(ArtifactId.values()));
        List<ArtifactId> unlockedArtifacts = unlockedArtifactsSupplier.get();
        IterableUtils.removeIf(allArtifacts, artifactId -> unlockedArtifacts.contains(artifactId));
        return allArtifacts;
    }

    private void removeLockedArtifacts() {
        List<ArtifactId> unlockedArtifacts = unlockedArtifactsSupplier.get();
        IterableUtils.removeIf(deck, artifactInfo -> !unlockedArtifacts.contains(artifactInfo.getId()));
    }

    @Override
    public ArtifactInfo removeFromDeck(ArtifactId artifactId) {
        return new ArtifactsDeckHelper().removeFromDeck(deck, artifactId);
    }

    public void setUnlockedArtifactsSupplier(Supplier<List<ArtifactId>> unlockedArtifactsSupplier) {
        this.unlockedArtifactsSupplier = unlockedArtifactsSupplier;
    }

    @Override
    public void removeTeamworkArtifacts() {
        IterableUtils.removeIf(deck, artifactInfo -> artifactInfo.getTraits().contains(AssetTrait.TEAMWORK));
    }

    @Override
    public ArtifactInfo gainRandomArtifact() {
        return deck.remove(0);
    }

    @Override
    public boolean removeFromDiscardPile(ArtifactInfo artifactInfo) {
        boolean remove = discard.remove(artifactInfo);
        if (remove) {
            logger.debug("Artifact '" + artifactInfo.getName() + "' removed from discard pile.");
        }
        return remove;
    }

    @Override
    public void addToInvestigator(InvestigatorId investigatorId, ArtifactInfo artifactInfo) {
        computeIfAbsent(investigatorArtifactsMap, investigatorId, k -> new LinkedList<>());
        investigatorArtifactsMap.get(investigatorId).add(artifactInfo);
        logger.info("Artifact '" + artifactInfo.getName() + "' added to investigator '" + investigatorId + "'");
    }

    @Override
    public void disable(ArtifactInfo artifactInfo) {
        artifactInfo.disable();
    }

    @Override
    public void enable(ArtifactInfo artifactInfo) {
        artifactInfo.enable();
    }

    @Override
    public void changeOwner(ArtifactInfo cardInfo, InvestigatorId newOwner) {
        for (Map.Entry<InvestigatorId, List<ArtifactInfo>> entry : investigatorArtifactsMap.entrySet()) {
            if (entry.getValue().remove(cardInfo)) {
                logger.info("Artifact '" + cardInfo.getName() + "' removed from investigator " + entry.getKey());
            }
        }
        addToInvestigator(newOwner, cardInfo);
    }

    @Override
    public void discard(InvestigatorId investigatorId, ArtifactInfo artifactInfo) {
        investigatorArtifactsMap.get(investigatorId).remove(artifactInfo);
        discard.add(artifactInfo);
        logger.info("Investigator '" + investigatorId + "' discarded artifact '" + artifactInfo.getName() + "'.");
    }

    @Override
    public List<ArtifactInfo> getDiscardPile() {
        return discard;
    }

    @Override
    public List<ArtifactInfo> getArtifacts(InvestigatorId investigatorId) {
        return getOrDefault(investigatorArtifactsMap, investigatorId, new LinkedList<>());
    }

    @Override
    public boolean hasArtifact(InvestigatorId investigatorId, ArtifactId artifactId) {
        List<ArtifactInfo> artifacts = getOrDefault(investigatorArtifactsMap, investigatorId, new LinkedList<>());
        return Stream.anyMatch(artifacts, artifact -> artifact.getId() == artifactId);
    }

    @Override
    public ArtifactInfo findFirst(AssetTrait trait) {
        return findFirst(artifactInfo -> artifactInfo.getTraits().contains(trait));
    }

    @Override
    public ArtifactInfo findFirst(ArtifactId artifactId) {
        return findFirst(artifactInfo -> artifactInfo.getId().equals(artifactId));
    }

    private ArtifactInfo findFirst(Predicate<ArtifactInfo> predicate) {
        if (Stream.anyMatch(deck, predicate)) {
            return Stream.findFirstOrException(deck, predicate);
        } else {
            return Stream.findFirstOrException(discard, predicate);
        }
    }

    @Override
    public boolean isInDeckOrDiscardPile(ArtifactId artifactId) {
        Predicate<ArtifactInfo> predicate = artifactInfo -> artifactInfo.getId().equals(artifactId);
        return Stream.anyMatch(deck, predicate) || Stream.anyMatch(discard, predicate);
    }

    @Override
    public void unlockCard(ArtifactId unlockedCard) {
        List<ArtifactId> unlockedCards = new LinkedList<>(unlockedArtifactsSupplier.get());
        unlockedCards.add(unlockedCard);
        GoogleServicesHolder.overwriteUnlockedArtifacts(unlockedCards);
    }

    @Override
    public ArtifactsDeckSaveData save() {
        ArtifactsDeckSaveData artifactsDeckSaveData = new ArtifactsDeckSaveData();
        artifactsDeckSaveData.setDiscarded(Stream.collectToList(Stream.map(discard, ArtifactInfo::getId)));
        HashMap<String, List<? extends ArtifactsDeckSaveDataRead.OwnedArtifactSaveDataRead>> saveDataMap = new HashMap<>();
        for (Map.Entry<InvestigatorId, List<ArtifactInfo>> entry : investigatorArtifactsMap.entrySet()) {
            InvestigatorId key = entry.getKey();
            List<ArtifactsDeckSaveData.OwnedArtifactSaveData> ownedArtifacts = Stream.collectToList(Stream.map(entry.getValue(), artifactInfo -> {
                ArtifactsDeckSaveData.OwnedArtifactSaveData ownedArtifactSaveData = new ArtifactsDeckSaveData.OwnedArtifactSaveData();
                ownedArtifactSaveData.setArtifactId(artifactInfo.getId());
                ownedArtifactSaveData.setDisabled(artifactInfo.isDisabled());
                return ownedArtifactSaveData;
            }));

            saveDataMap.put(key.name(), ownedArtifacts);
        }
        artifactsDeckSaveData.setInvestigatorArtifactsMap(saveDataMap);
        return artifactsDeckSaveData;
    }

    @Override
    public void load(ArtifactsDeckSaveDataRead saveData) {
        createDeck();
        for (ArtifactId artifactId : saveData.getDiscarded()) {
            ArtifactInfo artifactInfo = Stream.findFirstOrException(deck, ai -> ai.getId().equals(artifactId));
            discard.add(artifactInfo);
            deck.remove(artifactInfo);
        }
        for (Map.Entry<String, List<? extends ArtifactsDeckSaveDataRead.OwnedArtifactSaveDataRead>> entry : saveData.getInvestigatorArtifactsMap().entrySet()) {
            for (ArtifactsDeckSaveDataRead.OwnedArtifactSaveDataRead ownedArtifact : entry.getValue()) {
                ArtifactInfo artifactInfo = Stream.findFirstOrException(deck, ai -> ai.getId().equals(ownedArtifact.getArtifactId()));
                addToInvestigator(InvestigatorId.valueOf(entry.getKey()), artifactInfo);
                if (ownedArtifact.isDisabled()) {
                    disable(artifactInfo);
                }
                deck.remove(artifactInfo);
            }
        }
    }
}
