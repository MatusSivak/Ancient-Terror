package sk.sivak.eldritchhorror.core.model.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class ArtifactsDeckHelper {

    private static final Logger logger = LogManager.getLogger(ArtifactsDeckHelper.class);

    public List<ArtifactInfo> createDeck() {
        List<ArtifactInfo> deck = new LinkedList<>();
        for (ArtifactId artifactId : ArtifactId.values()) {
            try {
                Class.forName(artifactId.getArtifactListenerClassName()); // only those that have listener
            } catch (ClassNotFoundException e) {
                logger.warn("No listener found for artifact '" + artifactId + "'.");
                continue;
            }
            try {
                deck.add((ArtifactInfo) Class.forName(artifactId.getArtifactClassName()).newInstance());
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        Collections.shuffle(deck);
        logger.info("Artifacts deck created with " + deck.size() + " artifacts.");
        return deck;
    }


    public ArtifactInfo removeFromDeck(List<ArtifactInfo> deck, ArtifactId artifactId) {
        Iterator<ArtifactInfo> iterator = deck.iterator();
        while (iterator.hasNext()) {
            ArtifactInfo next = iterator.next();
            if (next.getId() == artifactId) {
                iterator.remove();
                logger.info("Removing '" + artifactId + "' from artifact deck");
                return next;
            }
        }
        logger.warn("Artifact '" + artifactId + "' not found in deck");
        return null;
    }
}
