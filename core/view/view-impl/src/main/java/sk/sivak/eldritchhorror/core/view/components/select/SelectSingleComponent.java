package sk.sivak.eldritchhorror.core.view.components.select;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;
import java8.features.function.Function;
import rx.SingleSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.view.components.button.HideOkButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class SelectSingleComponent<Key> {
    private List<Key> availableKeys;
    private SelectComponentsComplexTable<Key> selectComponentsComplexTable;
    private SingleSubscriber<? super Key> subscriber;
    private HideOkButtons hideOkButtons;
    private String hideText;
    private String cancelText;
    private TextButton cancelButton;
    private boolean completed;

    public void setCancelText(String cancelText) { this.cancelText = cancelText; }

    public SelectSingleComponent(List<Key> availableKeys,
                                 SingleSubscriber<? super Key> subscriber) {
        this.availableKeys = availableKeys;
        this.subscriber = subscriber;
        hideOkButtons = new HideOkButtons();
        selectComponentsComplexTable = new SelectComponentsComplexTable<Key>() {
            @Override
            protected void hideOkButton() {
                hideOkButtons.disableOkButton();
            }

            @Override
            protected void showOkButton() {
                hideOkButtons.enableOkButton();
            }
        };
    }

    public void init(String hideText, String title, Function<Float, Color> backgroundColorFn, String backgroundTexture) {
        this.hideText = hideText;
        this.selectComponentsComplexTable.setTitle(title);
        this.selectComponentsComplexTable.setBackgroundColorFn(backgroundColorFn);
        this.selectComponentsComplexTable.setBackgroundTexture(backgroundTexture);
    }

    public void show() {

        selectComponentsComplexTable.init(availableKeys);
        selectComponentsComplexTable.setPosition(
                (VIEWPORT_WIDTH-75) / 2 - selectComponentsComplexTable.getWidth() / 2 + 75,
                160);
        InfoStage.showActor(selectComponentsComplexTable);

        hideOkButtons.init(hideText, getOnConfirmAction(), selectComponentsComplexTable);
        hideOkButtons.showButtons();
        hideOkButtons.disableOkButton();
        if (cancelText != null) {
            cancelButton = ButtonBuilder.buildButton(cancelText);
            ButtonUtils.addClickListener(cancelButton, () -> {
                if (completed) return;
                hideOkButtons.cancel();
                onSelect(null);
            });
            InfoStage.showButton(cancelButton, VIEWPORT_WIDTH / 2 + 170, 5);
        }
    }

    private Action0 getOnConfirmAction() {
        return () -> onSelect(selectComponentsComplexTable.getSelectedKey());
    }


    private void onSelect(Key key) {
        if (completed) return;
        completed = true;
        if (key != null) selectComponentsComplexTable.disable(key);
        if (cancelButton != null) InfoStage.hideActor(cancelButton);
        InfoStage.hideActor(selectComponentsComplexTable, () -> subscriber.onSuccess(key), 0.25f);
    }
}
