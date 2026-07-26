package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropInput;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Random;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DRAW_MYTHOS_CARD;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.SHOW_RESERVE_DRAG_AND_DROP;

public class AdjustRolledDicesInAcquireAssetsAction implements Action<Object, Object> {

    private AfterRollDicesListener afterRollDicesListener;
    private BeforeShowReserveDragAndDropListner beforeShowReserveDragAndDropListner;

    public AdjustRolledDicesInAcquireAssetsAction() {

    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            afterRollDicesListener = new AfterRollDicesListener();
            beforeShowReserveDragAndDropListner = new BeforeShowReserveDragAndDropListner();
            ServicePlatform.get().getEventQueue().addAfterEventListener(afterRollDicesListener, BeforeAfterEvent.ROLL_DICES);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeShowReserveDragAndDropListner, SHOW_RESERVE_DRAG_AND_DROP);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(new BeforeDrawMythosCardListener(), DRAW_MYTHOS_CARD);
            onSub.onSuccess(null);
        });
    }

    private class BeforeDrawMythosCardListener extends EventListenerImpl<Object> {

        @Override
        public void onNotify(Object eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(afterRollDicesListener);
                ServicePlatform.get().getEventQueue().unregisterListener(beforeShowReserveDragAndDropListner);
                ServicePlatform.get().getEventQueue().unregisterListener(this);
            });

        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private class AfterRollDicesListener extends EventListenerImpl<TestData> {


        @Override
        public void onNotify(TestData eventData) {
            Random rng = new Random();
            eventData.setMinScoreToEndTest(1);
            for (int i = 0; i < eventData.getDiceRolls().size(); i++) {
                eventData.getDiceRolls().get(i).setDiceValue(rng.nextInt(4) + 1);
            }
            eventData.getDiceRolls().get(rng.nextInt(eventData.getDiceRolls().size())).setDiceValue(rng.nextInt(2)+5);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    @Override
    public void setInput(Object input) {

    }

    private class BeforeShowReserveDragAndDropListner extends EventListenerImpl<ShowReserveDragAndDropInput> {

        @Override
        public void onNotify(ShowReserveDragAndDropInput eventData) {
            eventData.setMandatoryAssetId(AssetId.CHARTER_FLIGHT);
        }

        @Override
        public Class<ShowReserveDragAndDropInput> getDataClass() {
            return ShowReserveDragAndDropInput.class;
        }
    }
}
