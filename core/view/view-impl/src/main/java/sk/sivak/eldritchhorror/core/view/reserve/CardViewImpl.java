package sk.sivak.eldritchhorror.core.view.reserve;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.BankLoanAsset;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;
import sk.sivak.eldritchhorror.core.controller.CardController;
import sk.sivak.eldritchhorror.core.view.CardView;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.components.select.SelectSingleComponent;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.SourceTargetGroup;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.reserve.ReserveDragAndDropBinder;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static java8.features.stream.Stream.collectToList;
import static java8.features.stream.Stream.map;
import static java8.features.util.IterableUtils.forEach;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class CardViewImpl implements CardView {

    private CardController controller;
    private ReserveDragAndDropBinder dragAndDropBinder;
    private TextButton okButton;
    private LabelTable displayReserveLabel;
    private ShowReserveDragAndDropOutput output;
    private TextButton hideButton;

    public void setController(CardController controller) {
        this.controller = controller;
    }

    @Override
    public Single<ShowReserveDragAndDropOutput> showReserveDragAndDrop(
            int testScore, boolean bankLoanAvailable, List<AssetInfo> reserve, AssetId mandatoryAssetId) {
        output = new ShowReserveDragAndDropOutput();
        return Single.create(onSub -> {
            showDragAndDrop(testScore, reserve, bankLoanAvailable);
            showButtons(onSub);

            if (mandatoryAssetId != null) {
                handleMandatoryAssetId(mandatoryAssetId);
            }
        });
    }

    private void handleMandatoryAssetId(AssetId mandatoryAssetId) {
        okButton.setDisabled(true);
        dragAndDropBinder.getReserveTargetActorChangedListener().addChildListener(x -> {
            okButton.setDisabled(true);

            for (CardTemplate cardTemplate : x) {
                if (cardTemplate.getAssetInfo().getId() == mandatoryAssetId) {
                    okButton.setDisabled(false);
                    break;
                }
            }
        });
    }

    @Override
    public Single<CardInfo> selectSingleCard(List<? extends CardInfo> availableCards, String titleText, String hideText) {
        return Single.create(onSub -> {
            SelectSingleComponent<? extends CardInfo> selectSingleComponent = new SelectSingleComponent<>(availableCards, onSub);
            selectSingleComponent.init(hideText, titleText, alpha -> new Color(1f, 1f, 1f, alpha), GRAY_BACKGROUND);
            selectSingleComponent.show();
        });
    }

    @Override
    public void darkenWorld(float alpha) {
        MapStage.darkenWorld(alpha);
    }

    @Override
    public void brightenWorld() {
        MapStage.brightenWorld();
    }

    private void showButtons(SingleSubscriber<? super ShowReserveDragAndDropOutput> onSub) {
        hideButton = ButtonBuilder.buildButton(get("dialog.hide"));
        okButton = ButtonBuilder.buildButton(get("dialog.ok"));

        addClickListener(hideButton, () -> {
            prepareShowReserveButton(hideButton).run();
        });
        addClickListener(okButton, () -> {
            if (okButton.isDisabled()) {
                return;
            }
            onConfirm();
            onSub.onSuccess(output);
        });
        okButton.getStyle().fontColor = new Color(0x008000ff);

        float bottomHeight = InfoStage.getBottomHeight();
        InfoStage.setBottomHeight(bottomHeight + okButton.getHeight() + 5);

        InfoStage.addActionToInfoStage(Actions.run(() -> {
            InfoStage.showButton(hideButton, VIEWPORT_WIDTH / 2 - okButton.getWidth(), bottomHeight, false);
            InfoStage.showButton(okButton, VIEWPORT_WIDTH / 2, bottomHeight);
        }));
    }

    private void onConfirm() {
        output.setSelectedAssets(dragAndDropBinder.getSelectedAssets());
        InfoStage.hideActor(dragAndDropBinder.getSourceTargetGroup());
        InfoStage.hideActor(okButton);
        InfoStage.hideActor(hideButton);
        InfoStage.setBottomHeight(5);
    }

    private Runnable prepareShowReserveButton(TextButton button) {
        return () -> {
            if (button.isDisabled()) {
                return;
            }
            dragAndDropBinder.getSourceTargetGroup().setTouchable(Touchable.disabled);
            displayReserveLabel = LabelTable.createAndShowTable(0, get("reserve.displayPrompt"));
            button.setText(get("dialog.yes"));
            button.setTouchable(Touchable.disabled);
            okButton.setTouchable(Touchable.disabled);
            SourceTargetGroup sourceTargetGroup = dragAndDropBinder.getSourceTargetGroup();
            sourceTargetGroup.addAction(Actions.alpha(0, FAST_ACTION_DURATION));
            okButton.addAction(Actions.alpha(0, FAST_ACTION_DURATION));
            button.addAction(sequence(
                    moveTo(VIEWPORT_WIDTH / 2 - button.getWidth() / 2, button.getY(), FAST_ACTION_DURATION),
                    Actions.run(() -> {
                        button.setTouchable(Touchable.enabled);
                        button.clearListeners();
                        addClickListener(button, prepareHideReserveButton(button));
                    })
            ));
        };
    }

    private Runnable prepareHideReserveButton(TextButton button) {
        return () -> {
            if (button.isDisabled()) {
                return;
            }
            dragAndDropBinder.getSourceTargetGroup().setTouchable(Touchable.enabled);
            InfoStage.setBottomHeight(InfoStage.getBottomHeight() - displayReserveLabel.getHeight() - 5);
            displayReserveLabel.addAction(sequence(
                    Actions.alpha(0, FAST_ACTION_DURATION),
                    Actions.run(() -> displayReserveLabel.remove())
            ));
            button.setText(get("dialog.hide"));
            button.setTouchable(Touchable.disabled);
            SourceTargetGroup sourceTargetGroup = dragAndDropBinder.getSourceTargetGroup();
            sourceTargetGroup.addAction(Actions.alpha(1, FAST_ACTION_DURATION));
            okButton.addAction(Actions.alpha(1, FAST_ACTION_DURATION));
            button.addAction(sequence(
                    moveTo(VIEWPORT_WIDTH / 2 - okButton.getWidth(), button.getY(), FAST_ACTION_DURATION),
                    Actions.run(() -> {
                        button.setTouchable(Touchable.enabled);
                        okButton.setTouchable(Touchable.enabled);
                        button.clearListeners();
                        addClickListener(button, prepareShowReserveButton(button));
                    })
            ));
        };
    }

    private void showDragAndDrop(int testScore, List<AssetInfo> reserve, boolean bankLoanAvailable) {
        List<CardTemplate> cardTemplates = toCardTemplates(reserve);
        if (bankLoanAvailable) {
            cardTemplates.add(0, CardTemplate.buildCard(new BankLoanAsset()));
        }
        forEach(cardTemplates, cardTemplate -> cardTemplate.setScale(0.16f));
        dragAndDropBinder = new ReserveDragAndDropBinder(testScore, InfoStage.getStageSafe());
        dragAndDropBinder.init(cardTemplates.toArray(new CardTemplate[cardTemplates.size()]));
        dragAndDropBinder.getSourceTargetGroup().setY(InfoStage.getInvestigatorHud().getPrefHeight() + VIEWPORT_HEIGHT * 0.02f);
        InfoStage.addBigActor(OnScreenActors.ActorKey.DRAG_AND_DROP, dragAndDropBinder.getSourceTargetGroup(),
                () -> dragAndDropBinder.getSourceTargetGroup().addAction(Actions.alpha(0f, 1f)),
                () -> dragAndDropBinder.getSourceTargetGroup().addAction(Actions.alpha(1f, 1f)));
    }

    private List<CardTemplate> toCardTemplates(List<AssetInfo> reserve) {
        return collectToList(map(reserve, CardTemplate::buildCard));
    }
}
