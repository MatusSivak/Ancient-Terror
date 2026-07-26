package sk.sivak.eldritchhorror.core.view.action.ticket;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.TokenActor;
import sk.sivak.eldritchhorror.core.view.components.YellowButton;
import sk.sivak.eldritchhorror.core.view.components.hud.TicketContainerBar;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

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

    public Single<PathType> selectTravelTicket() {
        return Single.create(onSub -> {
            InfoStage.displayTextForce("Select ticket");
            trainButton = showTrainButton(onSub, 0.4f, () -> InfoStage.hideActor(shipButton));
            shipButton = showShipButton(onSub, 0.6f, () -> InfoStage.hideActor(trainButton));
            hideOtherButton(trainButton, shipButton);
            hideOtherButton(shipButton, trainButton);
        });

    }

    private void hideOtherButton(YellowButton mainButton, YellowButton buttonToHide) {
        mainButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });
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
            beforeSuccessAction.call();
            onSub.onSuccess(pathType);
        });
        InfoStage.showButton(yellowButton,
                VIEWPORT_WIDTH * posX - yellowButton.getWidth() / 2,
                VIEWPORT_HEIGHT * 0.1f);
    }

    public Completable gainTravelTicket(PathType ticketType) {
        TokenActor tokenActor;
        if (ticketType == PathType.TRAIN) {
            tokenActor = new TokenActor(
                    CustomAssetManager.getTexture(CustomAssetManager.TICKET_TRAIN_LEFT),
                    CustomAssetManager.getTexture(CustomAssetManager.TICKET_TRAIN_RIGHT)
            );
        } else {
            tokenActor = new TokenActor(
                    CustomAssetManager.getTexture(CustomAssetManager.TICKET_SHIP_LEFT),
                    CustomAssetManager.getTexture(CustomAssetManager.TICKET_SHIP_RIGHT)
            );
        }

        addSmallActorToInfoStage(tokenActor);
        TicketContainerBar ticketBar = InfoStage.getInvestigatorHud().getTicketBar();
        tokenActor.setHolderLocation(ticketBar.getEmptyContainerPosition());
        Completable gain = tokenActor.gain();
        return gain.andThen(Completable.create(onSub -> {
            ticketBar.addTicket(ticketType);
            onSub.onCompleted();
        }));
    }
}
