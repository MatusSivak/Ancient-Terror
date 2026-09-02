package sk.sivak.eldritchhorror.core.view.components.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import java8.features.function.Predicate;
import java8.features.stream.Stream;
import rx.SingleSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.button.HideOkButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class SelectActionComponent extends VisTable implements ActionButtonClickedListener {
    private ActionInfoTable actionInfoTable;
    private ActionButtonsTable actionButtonsTable;
    private List<ActionPhaseAction> actionPhaseActions;
    private SingleSubscriber<? super ActionPhaseAction> subscriber;

    private ActionButtonData selectedActionButtonData;
    private HideOkButtons hideOkButtons;
    private Image hitImage;
    private Image noHitImage;
    private boolean onStage;
    private boolean visible;

    public SelectActionComponent() {
        /*
        actionButtonsTable = new ActionButtonsTable();
        actionButtonsTable.setActionButtonClickedListener(this);
        actionInfoTable = new ActionInfoTable();
        */
    }

    public void init(List<ActionPhaseAction> actionPhaseActions) {
        onStage = true;
        clear();
        setPosition(0, 0);
        setColor(Color.WHITE);
        setTouchable(Touchable.enabled);
        actionButtonsTable = new ActionButtonsTable();
        actionInfoTable = new ActionInfoTable();

        this.actionPhaseActions = actionPhaseActions;
        actionButtonsTable.init(Stream.map(actionPhaseActions, ActionPhaseAction::getActionButtonData));
        actionButtonsTable.setActionButtonClickedListener(this);

        addHitImage();
        addNoHitImage();
        ScrollPane actionButtonsTableWrapper = new ScrollPane(actionButtonsTable);
        actionButtonsTableWrapper.setScrollingDisabled(false, true);
        if (actionButtonsTable.getWidth() == 550) {
            add(actionButtonsTableWrapper).maxWidth(550);
        } else {
            add(actionButtonsTableWrapper).maxWidth(500);
        }

        add(actionInfoTable).pad(5).growY().width(250);

        pack();

        setPosition(
                VIEWPORT_WIDTH / 2 - getWidth() / 2 + 5,
                VIEWPORT_HEIGHT / 2 - getHeight() / 2);


        hideOkButtons = new HideOkButtons();

        hideOkButtons.init(get("action.displayActions"), getOnConfirmAction(), this);
    }

    private void addHitImage() {
        hitImage = new Image(CustomAssetManager.getTextureRegion(CustomAssetManager.PURE_WHITE_BACKGROUND));
        addActor(hitImage);
        hitImage.getColor().a = 0.0f;
        addClickListener(hitImage, () -> {
//            hideOkButtons.hide();
        });
    }

    private void addNoHitImage() {
        noHitImage = new Image(CustomAssetManager.getTextureRegion(CustomAssetManager.PURE_WHITE_BACKGROUND));
        addActor(noHitImage);
        noHitImage.getColor().a = 0.0f;
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        noHitImage.setBounds(0, 0, getPrefWidth(), getPrefHeight());
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        noHitImage.setBounds(0, 0, getPrefWidth(), getPrefHeight());
        hitImage.setBounds(-getX(), -getY(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    public void displayOrHide() {
        if (getParent() == null) {
            InfoStage.displayTextDontHide(get("action.select"));
            visible = true;
            BigActorsManager.unlock(BigActorsManager.BigActorKey.ACTION_TABLE);
            InfoStage.addBigActor(null, this, null, null);
            hideOkButtons.hideInstantly();
            hideOkButtons.disableOkButton();
        } else if (isOnStage()) {
            flipAlpha();
        }
    }

    public void flipAlpha() {
        if (hideOkButtons == null) {
            return;
        }
        if (!visible) {
            InfoStage.displayTextDontHide(get("action.select"));
            hideOkButtons.alphaToValue(1f);
            visible = true;
        } else {
            InfoStage.hideLabel(get("action.select"));
            hideOkButtons.alphaToValue(0f);
            visible = false;
        }
    }

    public boolean isOnStage() {
        return onStage;
    }

    private Action0 getOnConfirmAction() {
        return () -> {
            if (selectedActionButtonData == null) {
                return;
            }
            onStage = false;
            ActionPhaseAction selectedActionPhaseAction = findSelectedActionPhaseAction(selectedActionButtonData);
            BigActorsManager.displayOrHideActionsTable();
            BigActorsManager.unlock(BigActorsManager.BigActorKey.ACTION_TABLE);
            InfoStage.hideLabel(get("action.select"));
            subscriber.onSuccess(selectedActionPhaseAction);
        };
    }

    private ActionPhaseAction findSelectedActionPhaseAction(ActionButtonData actionButtonData) {
        Predicate<ActionPhaseAction> condition = actionPhaseAction -> actionPhaseAction.getActionButtonData() == actionButtonData;
        return Stream.findFirstOrException(actionPhaseActions, condition);
    }

    public void setSubscriber(SingleSubscriber<? super ActionPhaseAction> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onEnabledButtonClickedEvent(ActionButtonData actionButtonData) {
        hideOkButtons.enableOkButton();
        selectedActionButtonData = actionButtonData;
        actionInfoTable.init(findSelectedActionPhaseAction(actionButtonData));
    }

    @Override
    public void onDisabledButtonClickedEvent(ActionButtonData actionButtonData) {
        hideOkButtons.disableOkButton();
        selectedActionButtonData = null;
        actionInfoTable.init(findSelectedActionPhaseAction(actionButtonData));
    }
}
