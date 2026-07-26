package sk.sivak.eldritchhorror.core.model.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class AssetsDeckHelper {

    private static final Logger logger = LogManager.getLogger(AssetsDeckHelper.class);

    public List<AssetInfo> createDeck() {
        List<AssetInfo> deck = new LinkedList<>();
        for (AssetId assetId : AssetId.values()) {
            if (AssetId.BANK_LOAN == assetId) {
                continue;
            }
            try {
                Class.forName(assetId.getAssetListenerClassName()); // only those that have listener

            } catch (ClassNotFoundException e) {
                logger.warn("No listener found for asset '" + assetId + "'.");
                continue;
            }
            try {
                deck.add((AssetInfo) Class.forName(assetId.getAssetClassName()).newInstance());
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        Collections.shuffle(deck);
        logger.info("Assets deck created with " + deck.size() + " assets.");
        return deck;
    }


    public AssetInfo removeFromDeck(List<AssetInfo> deck, AssetId assetId) {
        Iterator<AssetInfo> iterator = deck.iterator();
        while (iterator.hasNext()) {
            AssetInfo next = iterator.next();
            if (next.getId() == assetId) {
                iterator.remove();
                logger.info("Removing '" + assetId + "' from asset deck");
                return next;
            }
        }
        logger.warn("Asset '" + assetId + "' not found in deck");
        return null;
    }
}
