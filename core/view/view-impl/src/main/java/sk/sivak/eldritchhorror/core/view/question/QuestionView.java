package sk.sivak.eldritchhorror.core.view.question;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.view.TypewriterView;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.select.SelectComponentsTable;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

public class QuestionView {

    private float originalBottomHeight;
    private LinkedList<Button> buttons;
    private LabelTable labelTable;
    private SelectComponentsTable selectComponentsTable;
    private Answer answer;
    private Runnable beforeOnSubAction;

    public <RD, AD> Single<Answer<RD, AD>> ask(Question<RD> question) {
        return Single.create(subscriber -> {
            beforeOnSubAction = () -> {};
            answer = new Answer<RD, AD>();
            originalBottomHeight = InfoStage.getBottomHeight();
            displayButtons(question.getOptions(), subscriber);
            displayTable(question.getTitle(), question.getPortraitTextureName());
            displaySelectComponentsTable(question.getSelectComponentsTableData());

            if (question.isDisplayCurrentMysteryCard()) {
                labelTable.remove();
                InfoStage.addBigActor(null, labelTable, null, null);
                beforeOnSubAction = () -> {
                    BigActorsManager.unlock(BigActorsManager.BigActorKey.BLOCKER_QUESTION);
                    BigActorsManager.displayOrHideCurrentMystery();
                };
                for (Button button : buttons) {
                    button.remove();
                    InfoStage.addBigActor(null, button, null, null);
                    BigActorsManager.lock(BigActorsManager.BigActorKey.BLOCKER_QUESTION);
                }
            } else if (question.getDisplayOngoingRumorCard() != null) {
                labelTable.remove();
                InfoStage.addBigActor(null, labelTable, null, null);
                beforeOnSubAction = () -> {
                    BigActorsManager.unlock(BigActorsManager.BigActorKey.BLOCKER_QUESTION);
                };
                for (Button button : buttons) {
                    button.remove();
                    InfoStage.addBigActor(null, button, null, null);
                    BigActorsManager.lock(BigActorsManager.BigActorKey.BLOCKER_QUESTION);
                }
            }else if (BigActorsManager.isPaperDisplayed()) {
                labelTable.remove();
                InfoStage.addBigActor(null, labelTable, null, null);
                for (Button button : buttons) {
                    button.remove();
                    InfoStage.addBigActor(null, button, null, null);;
                }
            }
        });
    }

    private void displaySelectComponentsTable(Question.SelectComponentsTableData selectComponentsTableData) {
        if (selectComponentsTableData == null) {
            return;
        }
        switch (selectComponentsTableData.getType()) {
            case INVESTIGATORS:
                displaySelectInvestigatorTable(((List<InvestigatorId>) selectComponentsTableData.getAvailableKeys()));
                break;
            case CARDS:
                displaySelectCardsTable(((List<CardInfo>) selectComponentsTableData.getAvailableKeys()));
                break;
            case GATES:
                displaySelectGateTable(((List<GateInfo>) selectComponentsTableData.getAvailableKeys()));
                break;
        }

    }

    private void displaySelectCardsTable(List<CardInfo> availableKeys) {
        SelectComponentsTable<CardInfo> table = new SelectComponentsTable<>();
        table.addObserver(cardInfo -> getAnswer().setAdditionalData(cardInfo));
        table.setBackgroundColorFn(alpha -> new Color(1f, 1f, 1f, alpha));
        table.setBackgroundTexture(GRAY_BACKGROUND);
        table.init(availableKeys);
        table.setPosition(
                (VIEWPORT_WIDTH-75) / 2 - table.getWidth() / 2 + 75,
                VIEWPORT_HEIGHT / 2 - table.getHeight() / 2 - 20);
        InfoStage.showActor(table);
        setSelectComponenentsTable(table);
    }

    private void displaySelectGateTable(List<GateInfo> availableKeys) {
        SelectComponentsTable<GateInfo> table = new SelectComponentsTable<>();
        table.addObserver(cardInfo -> getAnswer().setAdditionalData(cardInfo));
        table.setBackgroundColorFn(alpha -> new Color(0.0f, 0.0f, 0.0f, 0.5f * alpha));
        table.setBackgroundTexture(PURE_WHITE_BACKGROUND);
        table.init(availableKeys);
        table.setPosition(
                (VIEWPORT_WIDTH-75) / 2 - table.getWidth() / 2 + 75,
                VIEWPORT_HEIGHT / 2 - table.getHeight() / 2 - 20);
        InfoStage.showActor(table);
        setSelectComponenentsTable(table);
    }

    private void setSelectComponenentsTable(SelectComponentsTable selectComponentsTable) {
        this.selectComponentsTable = selectComponentsTable;
    }

    private void displaySelectInvestigatorTable(List<InvestigatorId> availableKeys) {
        SelectComponentsTable<InvestigatorId> table = new SelectComponentsTable<>();
        table.addObserver(investigatorId -> getAnswer().setAdditionalData(investigatorId));
        table.setBackgroundColorFn(alpha -> new Color(1f, 1f, 1f, alpha));
        table.setBackgroundTexture(GRAY_BACKGROUND);
        table.init(availableKeys);
        table.setPosition(
                (VIEWPORT_WIDTH-75) / 2 - table.getWidth() / 2 + 75,
                VIEWPORT_HEIGHT / 2 - table.getHeight() / 2 + 10);
        InfoStage.showActor(table);
        setSelectComponenentsTable(table);
    }

    private void displayTable(String title, String portraitTextureName) {
        if (portraitTextureName != null) {
            labelTable = LabelTable.createAndShowTable(0, title, portraitTextureName, Collections.emptyList(),  0);
        } else {
            labelTable = LabelTable.createAndShowTable(0, title);
        }
    }

    private <RD, AD> void displayButtons(List<Question.Option<RD>> options, SingleSubscriber<? super Answer<RD, AD>> subscriber) {
        float bottomHeight = InfoStage.getBottomHeight();
        buttons = new LinkedList<>();
        int index = 0;
        float buttonWidth;
        float buttonHeight;

        for (Question.Option<RD> option : options) {
            TextButton textButton = ButtonBuilder.buildButton(option.getName());
            textButton.getStyle().fontColor = new Color(option.getColorCode());
            buttonWidth = textButton.getWidth();
            buttonHeight = textButton.getHeight();
            addClickListener(textButton, createOnClickAction(subscriber, option));
            InfoStage.showButton(textButton,
                    VIEWPORT_WIDTH / 2 + index * buttonWidth - options.size() * buttonWidth / 2,
                    bottomHeight);
            index++;
            buttons.add(textButton);
            InfoStage.setBottomHeight(bottomHeight + buttonHeight + 5);
        }

    }


    private <RD, AD> Runnable createOnClickAction(SingleSubscriber<? super Answer<RD, AD>> subscriber, Question.Option<RD> option) {
        return () -> {
            for (Button button : buttons) {
                InfoStage.hideActor(button);
            }
            InfoStage.hideActor(labelTable);
            if (selectComponentsTable != null) {
                InfoStage.hideActor(selectComponentsTable);
            }
            InfoStage.setBottomHeight(originalBottomHeight);
            Answer<RD, AD> answer = getAnswer();
            answer.setResponseData(option.getValue());
            beforeOnSubAction.run();
            subscriber.onSuccess(answer);
        };
    }

    public <RD, AD> Answer<RD, AD> getAnswer() {
        return answer;
    }
}
