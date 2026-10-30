package sk.sivak.eldritchhorror.core.view.action.ticket;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.ItemActor;
import sk.sivak.eldritchhorror.core.view.components.YellowButton;
import sk.sivak.eldritchhorror.core.view.components.hud.TicketContainerBar;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import sk.sivak.eldritchhorror.core.view.utils.UiText;
import sk.sivak.eldritchhorror.core.view.utils.GameplaySounds;
import sk.sivak.eldritchhorror.core.view.action.TokenSounds;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.game.InfoStage.addSmallActorToInfoStage;

/**
 * @author msivak
 */
public class TicketActionView {

    private Skin skin;

    public TicketActionView(Skin skin) {
        this.skin = skin;
    }

    private YellowButton.TrainButton trainButton;
    private YellowButton.ShipButton shipButton;

    public Single<PathType> selectTravelTicket(boolean trainAvailable, boolean shipAvailable) {
        return selectTravelTicket(trainAvailable, shipAvailable, UiText.get("ticket.select"));
    }

    public Single<PathType> selectTravelTicket(boolean trainAvailable, boolean shipAvailable, String title) {
        return Single.create(onSub -> {
            InfoStage.displayTextForce(title);
            trainButton = trainAvailable
                    ? showTrainButton(onSub, shipAvailable ? 0.4f : 0.5f, this::hideTicketButtons) : null;
            shipButton = shipAvailable
                    ? showShipButton(onSub, trainAvailable ? 0.6f : 0.5f, this::hideTicketButtons) : null;
        });

    }

    private void hideTicketButtons() {
        for (YellowButton button : new YellowButton[]{trainButton, shipButton}) {
            if (button == null) continue;
            button.setDisabled(true);
            button.setTouchable(Touchable.disabled);
            InfoStage.hideActor(button);
        }
    }

    private YellowButton.TrainButton showTrainButton(SingleSubscriber<? super PathType> onSub, float posX, Action0 beforeSuccessAction) {
        YellowButton.TrainButton trainButton = new YellowButton.TrainButton();
        showButton(trainButton, PathType.TRAIN, onSub, posX, beforeSuccessAction);
        return trainButton;
    }

    private YellowButton.ShipButton showShipButton(SingleSubscriber<? super PathType> onSub, float posX, Action0 beforeSuccessAction) {
        YellowButton.ShipButton shipButton = new YellowButton.ShipButton();
        showButton(shipButton, PathType.SHIP, onSub, posX, beforeSuccessAction);
        return shipButton;
    }

    private void showButton(YellowButton yellowButton, PathType pathType, SingleSubscriber<? super PathType> onSub, float posX,
                            Action0 beforeSuccessAction) {
        ButtonUtils.addClickListener(yellowButton, () -> {
            if (yellowButton.isDisabled() || onSub.isUnsubscribed()) return;
            beforeSuccessAction.call();
            onSub.onSuccess(pathType);
        });
        InfoStage.showButton(yellowButton,
                VIEWPORT_WIDTH * posX - yellowButton.getWidth() / 2,
                VIEWPORT_HEIGHT * 0.1f);
    }

    public Completable gainTravelTicket(PathType ticketType) {
        return Completable.defer(() -> {
            InfoStage.getInvestigatorHud().validate();
            TicketContainerBar ticketBar = InfoStage.getInvestigatorHud().getTicketBar();
            ItemActor tokenActor = new ItemActor(CustomAssetManager.getTexture(ticketType == PathType.TRAIN
                    ? CustomAssetManager.TICKET_TRAIN_DOWN : CustomAssetManager.TICKET_SHIP_DOWN));
            tokenActor.setHolderImage(ticketBar.getEmptyTicketImage());
            addSmallActorToInfoStage(tokenActor);
            return GameplaySounds.withCue(GameplaySounds.Cue.TICKET_GAIN, tokenActor.gain())
                    .andThen(Completable.fromAction(() -> {
                        TokenSounds.playLand(TokenSounds.Cue.TICKET_GAIN, 0);
                        ticketBar.addTicket(ticketType);
                    }));
        });
    }
}
