package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFont;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.BUTTON_BORDER;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.BUTTON_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.FONT_SCALE;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.NEW_LINE_SPEED;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.TABLE_SCALE;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.TEXT_AREA_WIDTH;
import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

public class TypewriterQuestionTyper {
    private static final float BUTTON_FONT_SCALE_MULTIPLIER = 1.24f;

    private final TypewriterViewImpl typewriterView;

    public TypewriterQuestionTyper(TypewriterViewImpl typewriterView) {
        this.typewriterView = typewriterView;
    }


    public Single<Integer> typeQuestion(String[] buttonTexts) {
        return Single.create(onSub -> {

            boolean onNewLine = false;
            for (String buttonText : buttonTexts) {
                if (buttonText.length() > 35) {
                    onNewLine = true;
                    break;
                }
            }

            List<String> answersModified1 = replacePlaceholdersInAnswers(buttonTexts);
            if (onNewLine) {
                List<ButtonData> buttonDatas = createButtonDatas(answersModified1, 350/ FONT_SCALE);
                List<TextButtonWithValue> buttons = createButtons(buttonDatas);
                addButtonsToTableOnNewLine(buttons);
                addClickListenerOnButtons(buttons, onSub);
            } else {

                BitmapFont bitmapFont = getBitmapFont(FONT_ADLER);
                float buttonWidth = findButtonWidth(answersModified1, bitmapFont);
                List<ButtonData> buttonDatas = createButtonDatas(answersModified1, buttonWidth);
                List<TextButtonWithValue> buttons = createButtons(buttonDatas);

                addButtonsToTable(buttonWidth * FONT_SCALE, buttons);
                addClickListenerOnButtons(buttons, onSub);
            }
        });
    }

    private void addButtonsToTableOnNewLine(List<TextButtonWithValue> buttons) {
        Table miniTable = new Table();
        for (TextButtonWithValue button : buttons) {
            miniTable.add(button.textButton).width(350).height(BUTTON_HEIGHT).padBottom(5).row();
        }
        typewriterView.getTable().add(miniTable).padTop(0).padBottom(10).row();
        typewriterView.getTable().addAction(Actions.moveBy(0,
                5 + (BUTTON_HEIGHT / 2f) * buttons.size(), NEW_LINE_SPEED));
        typewriterView.increaseOffset(10 + (BUTTON_HEIGHT / 2f) * buttons.size());
        BigActorsManager.unlock(BigActorsManager.BigActorKey.PAPER);
    }

    private void addButtonsToTable(float maxWidth, List<TextButtonWithValue> buttons) {
        Table miniTable = new Table();
        boolean first = true;
        for (TextButtonWithValue button : buttons) {
            if (!first) {
                miniTable.add().width((TEXT_AREA_WIDTH * TABLE_SCALE - maxWidth * buttons.size()) / buttons.size() + 1).height(BUTTON_HEIGHT);
            }
            miniTable.add(button.textButton).width(maxWidth).height(BUTTON_HEIGHT);
            first = false;
        }
        typewriterView.getTable().add(miniTable).padTop(0).padBottom(10).row();
        typewriterView.getTable().addAction(Actions.moveBy(0,
                5 + BUTTON_HEIGHT / 2f, NEW_LINE_SPEED));
        typewriterView.increaseOffset(10 + BUTTON_HEIGHT / 2f);
        BigActorsManager.unlock(BigActorsManager.BigActorKey.PAPER);
    }

    private LinkedList<TextButtonWithValue> createButtons(List<ButtonData> buttonDatas) {
        LinkedList<TextButtonWithValue> buttons = new LinkedList<>();
        for (int i = 0; i < buttonDatas.size(); i++) {
            TextButton button = typewriterView.createTextButton(buttonDatas.get(i).buttonText);
            button.getLabel().setColor(Color.WHITE);
            button.getLabel().setFontScale(FONT_SCALE * BUTTON_FONT_SCALE_MULTIPLIER);

            TextButtonWithValue textButtonWithValue = new TextButtonWithValue(button);
            textButtonWithValue.setButtonId(i);
            buttons.add(textButtonWithValue);
        }
        return buttons;
    }

    private List<ButtonData> createButtonDatas(List<String> answersModified1, float maxWidth) {
        List<ButtonData> buttonDatas = new LinkedList<>();
        GlyphLayout glyphLayout = new GlyphLayout();
        for (String answerModified : answersModified1) {
            ButtonData buttonData = new ButtonData();

            glyphLayout.reset();
            glyphLayout.setText(getBitmapFont(FONT_ADLER), answerModified, Color.WHITE, maxWidth, Align.center, true);

            StringBuilder stringBuilder = new StringBuilder();
            buttonData.buttonColor = new Color(glyphLayout.runs.get(0).color);
            for (GlyphLayout.GlyphRun run : glyphLayout.runs) {
                for (BitmapFont.Glyph glyph : run.glyphs) {
                    stringBuilder.append((char) glyph.id);
                }
                if (stringBuilder.charAt(stringBuilder.length() - 1) == ' ') {
                    stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
                }
                stringBuilder.append("\n");
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            buttonData.buttonText = stringBuilder.toString();
            buttonDatas.add(buttonData);
        }

        return buttonDatas;
    }

    private float findButtonWidth(List<String> answersModified1, BitmapFont bitmapFont) {
        float buttonWidth = 0;
        GlyphLayout glyphLayout = new GlyphLayout();
        for (String answerModified : answersModified1) {
            glyphLayout.reset();
            glyphLayout.setText(bitmapFont, answerModified);
            float width = glyphLayout.width;
            glyphLayout.reset();
            for (float i = 0.5f; i < 1; i += 0.01) {
                glyphLayout.reset();
                glyphLayout.setText(bitmapFont, answerModified, Color.WHITE, width * i, Align.center, true);
                if (glyphLayout.runs.size == 2 && (width * i) > buttonWidth) {
                    buttonWidth = width * i;
                    break;
                }
            }
        }
        return Math.max(buttonWidth + BUTTON_BORDER, TypewriterConstants.MIN_BUTTON_WIDTH / FONT_SCALE);
    }

    private List<String> replacePlaceholdersInAnswers(String[] answers) {
        List<String> answersModified1 = new LinkedList<>();
        for (String answer : answers) {
            answersModified1.add(typewriterView.replacePlaceholders(answer));
        }
        return answersModified1;
    }

    private void addClickListenerOnButtons(List<TextButtonWithValue> buttons, SingleSubscriber<? super Integer> onSub) {
        for (TextButtonWithValue button : buttons) {
            addClickListener(button.getTextButton(), () -> {
                BigActorsManager.lock(BigActorsManager.BigActorKey.PAPER);
                Array<Cell> cells = typewriterView.getTable().getCells();
                cells.get(cells.size - 1).setActor(button.getTextButton()).height(BUTTON_HEIGHT).width(button.getTextButton().getWidth());
                for (TextButtonWithValue textButtonWithValue : buttons) {
                    textButtonWithValue.getTextButton().clearListeners();
                }

                typewriterView.getTable().addAction(
                        Actions.sequence(
                                Actions.moveBy(0,5 + BUTTON_HEIGHT / 2f, NEW_LINE_SPEED),
                                Actions.run( () -> {
                                    typewriterView.increaseOffset(5 + BUTTON_HEIGHT / 2f);
                                    onSub.onSuccess(button.getButtonId());
                                })
                        ));
            });
        }
    }

    private class ButtonData {
        private Color buttonColor;
        private String buttonText;
    }

    private class TextButtonWithValue{

        private TextButton textButton;
        private int buttonId;

        TextButtonWithValue(TextButton textButton) {
            this.textButton = textButton;
        }

        TextButton getTextButton() {
            return textButton;
        }

        int getButtonId() {
            return buttonId;
        }

        void setButtonId(int buttonId) {
            this.buttonId = buttonId;
        }
    }
}
