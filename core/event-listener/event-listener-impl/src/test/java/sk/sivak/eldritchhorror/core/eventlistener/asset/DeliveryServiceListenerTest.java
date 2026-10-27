package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.asset.HolyCrossAsset;
import sk.sivak.eldritchhorror.core.constants.asset.UrbanGuideAsset;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import static org.junit.Assert.*;

public class DeliveryServiceListenerTest {
    @Test public void onlySenderItemsCanBeDeliveredAndNeitherSideOffersTokens() {
        TradeData trade = new TradeData();
        CardInfo item = new HolyCrossAsset();
        trade.setSourceCards(new ArrayList<>(Arrays.asList(item, new UrbanGuideAsset())));
        trade.setTargetCards(new ArrayList<>(Arrays.asList(new HolyCrossAsset())));
        trade.setSourceTokenTrading(tokens(3, 1, 1));
        trade.setTargetTokenTrading(tokens(2, 0, 2));
        TradeData.Tokens originalSource = trade.getSourceTokenTrading().getBeforeTrade();
        TradeData.Tokens originalTarget = trade.getTargetTokenTrading().getBeforeTrade();

        DeliveryServiceListener.restrictDelivery(trade);

        assertEquals(Arrays.asList(item), trade.getSourceCards());
        assertTrue(trade.getTargetCards().isEmpty());
        for (TradeData.TokenTrading side : Arrays.asList(trade.getSourceTokenTrading(), trade.getTargetTokenTrading())) {
            for (TradeData.Tokens tokens : Arrays.asList(side.getBeforeTrade(), side.getAfterTrade())) {
                assertEquals(0, tokens.getClues());
                assertEquals(0, tokens.getTrainTickets());
                assertEquals(0, tokens.getShipTickets());
            }
        }
        // Restricting offers must not remove actual possessions.
        assertEquals(3, originalSource.getClues());
        assertEquals(1, originalSource.getTrainTickets());
        assertEquals(2, originalTarget.getClues());
        assertEquals(2, originalTarget.getShipTickets());
    }

    private TradeData.TokenTrading tokens(int clues, int train, int ship) {
        TradeData.TokenTrading result = new TradeData.TokenTrading();
        TradeData.Tokens before = new TradeData.Tokens();
        before.setClues(clues); before.setTrainTickets(train); before.setShipTickets(ship);
        result.setBeforeTrade(before);
        return result;
    }
}
