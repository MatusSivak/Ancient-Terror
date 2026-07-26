package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.GamblersDiceAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author msivak
 */
public class GamblersDiceListener extends AbstractAssetListener<GamblersDiceAsset> {

    private static final Logger logger = LogManager.getLogger(GamblersDiceListener.class);
    private CalculateDicePoolListener calculateDicePoolListener;
    private RerollUsingAssetListener rerollUsingAssetListener;
    private EnableCardListener enableCardListener;

    @Override
    protected void register() {
        calculateDicePoolListener = new CalculateDicePoolListener();
        getEventQueue().addAfterEventListener(calculateDicePoolListener, BeforeAfterEvent.CALCULATE_DICE_POOL);
        rerollUsingAssetListener = new RerollUsingAssetListener();
        getEventQueue().addDirectEventListener(rerollUsingAssetListener, DirectEvent.REROLL_USING_ASSETS_2);
        enableCardListener = new EnableCardListener(getAssetInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(calculateDicePoolListener, rerollUsingAssetListener, enableCardListener);
    }

    private class CalculateDicePoolListener extends AbstractAssetEventListener<TestData> {

        public CalculateDicePoolListener() {
            super(GamblersDiceListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getCalculatedDicePool() > 1) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                showCardRequest.setHideType(HideType.RETURN);
                showCardRequest.setAssetOriginType(AssetOriginType.INVENTORY);
                showCardRequest.setTitle("You roll a minimum of two dice.");
                showCardRequest.setAssetInfo(getAssetInfo());
                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> {
                    input.setMinimumRolledDice(2);
                    input.setCalculatedDicePool(input.calculateDicePool());
                    ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
                });
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class RerollUsingAssetListener extends AbstractAssetEventListener<TestData> {

        public RerollUsingAssetListener() {
            super(GamblersDiceListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (!enabled) {
                    return;
                }
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (input.getCalculatedDicePool() <= input.getScore()) {
                    return;
                }

                TreeMap<Integer, List<Integer>> diceValueDiceNrsMap = new TreeMap<>();

                for (DiceRoll diceRoll : input.getDiceRolls()) {
                    if (diceRoll.getScore() != DiceRoll.Score.BAD) {
                        continue;
                    }
                    if (!diceValueDiceNrsMap.containsKey(diceRoll.getDiceValue())) {
                        diceValueDiceNrsMap.put(diceRoll.getDiceValue(), new LinkedList<>());
                    }
                    diceValueDiceNrsMap.get(diceRoll.getDiceValue()).add(diceRoll.getDiceNr());
                }
                for (Map.Entry<Integer, List<Integer>> entry : diceValueDiceNrsMap.entrySet()) {
                    if (entry.getValue().size() >= 2) {
                        reroll(entry.getValue().get(0), entry.getValue().get(1), input);
                        break;
                    }
                }



            };
        }

        private void reroll(Integer diceOne, int diceTwo, TestData input) {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setAssetInfo(getAssetInfo());
            showCardRequest.setHideType(HideType.DISABLE_ON_YES);
            showCardRequest.setTitle("Reroll two dice with matching results?");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

            ServicePlatform.get().getGameService().showCard(showCardRequest)
                    .subscribe(showCardResponse -> onAnswer(diceOne, diceTwo, showCardResponse, input));
        }

        private void onAnswer(Integer diceOne, int diceTwo, ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getAssetInfo());
                List<DiceRoll> dicesToReroll = new LinkedList<>();
                for (DiceRoll diceRoll : input.getDiceRolls()) {
                    if (diceRoll.getDiceNr() == diceOne || diceRoll.getDiceNr() == diceTwo) {
                        dicesToReroll.add(diceRoll);
                    }
                }

                CountRerollDiceData countRerollDiceData = new CountRerollDiceData();
                countRerollDiceData.setCount(2);
                countRerollDiceData.setCountRerollDiceType(CountRerollDiceData.CountRerollDiceType.OTHER);
                countRerollDiceData.setTestData(input);

                ServicePlatform.get().getService().convertTo(CountRerollDiceData.class, () -> countRerollDiceData);
                ServicePlatform.get().getTestService().rerollDice(dicesToReroll);
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().convertFromTo(ShowCardResponse.class, TestData.class, response -> input);
            }

        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
