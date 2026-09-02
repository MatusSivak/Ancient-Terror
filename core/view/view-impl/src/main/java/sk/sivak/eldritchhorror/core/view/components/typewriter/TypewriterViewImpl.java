package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.rafaskoberg.gdx.typinglabel.TypingAdapter;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import java8.features.util.MapUtils;
import rx.Completable;
import rx.CompletableSubscriber;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.firebase.HallOfFameData;
import sk.sivak.eldritchhorror.core.view.TypewriterView;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.tutorial.SafeTypingLabel;
import sk.sivak.eldritchhorror.core.view.firebase.FirebaseHallOfFame;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GRAY_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SPECIAL_ELITE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getSkin;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.BACKGROUND_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.BACKGROUND_WIDTH;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.BAD_PLACEHOLDER;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.FONT_SCALE;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.GOOD_PLACEHOLDER;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.HEX_BAD;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.HEX_GOOD;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.NEW_LINE_SPEED;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.TABLE_SCALE;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.TEXT_AREA_PAD_TOP;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.TEXT_AREA_WIDTH;
import static sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterConstants.TEXT_LINE_HEIGHT;

public class TypewriterViewImpl implements TypewriterView  {
    private TypewriterTableStack typewriterTableStack;
    private final TypewriterHeaderTyper typewriterHeaderTyper;
    private final TypewriterQuestionTyper typewriterQuestionTyper;
    private final TypewriterEffect typewriterEffect;

    private Color fontColor = Color.BLACK;

    public TypewriterViewImpl() {
        typewriterHeaderTyper = new TypewriterHeaderTyper(this);
        typewriterQuestionTyper = new TypewriterQuestionTyper(this);
        typewriterEffect = new TypewriterEffect(this);
        typewriterTableStack = new TypewriterTableStack();
    }

    private void createTable() {
        Table table = new Table() {
            protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
                Color color = getColor();
                batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
                ((TextureRegionDrawable) getBackground()).draw(batch, x + getWidth(), y, 0, 0, getHeight(), getWidth(), 1f, 1f, 90);
            }
        };
        table.setBackground(CustomAssetManager.getTextureRegionDrawable(CustomAssetManager.MYSTERY_BACKGROUND));
        table.setWidth(BACKGROUND_WIDTH * TABLE_SCALE);
        table.setHeight(BACKGROUND_HEIGHT * TABLE_SCALE);

        table.align(Align.top);
        table.padTop(TEXT_AREA_PAD_TOP * TABLE_SCALE);

        table.setTransform(true);

        typewriterTableStack.push(new TypewriterTableStack.TableData(table));
    }

    public TypewriterEffect getTypewriterEffect() {
        return typewriterEffect;
    }

    void increaseOffset(float amount) {
        typewriterTableStack.peek().increaseOffsetY(amount);
    }

    void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public Table getTable() {
        return typewriterTableStack.peek().getTable();
    }

    public Vector2 getTablePosition() {
        return typewriterTableStack.peek().getTablePosition();
    }

    public void updateTablePosition(Vector2 vector2) {
        typewriterTableStack.peek().setTablePosition(vector2);
    }


    @Override
    public Completable typeHeader(String header) {
        return typewriterHeaderTyper.typeHeader(header);
    }

    public Completable typeFlavor(String flavor) {
        return typeTextInternal(flavor + "\n ", Color.BLACK);
    }

    public Completable typeInfo(String regular) {
        return typeTextInternal(regular, Color.BLACK);
    }

    public Completable newLine() {
        return typeTextInternal("\n ", Color.BLACK);
    }

    public Single<Integer> displayButtons(String... buttonTexts) {
        return typewriterQuestionTyper.typeQuestion(buttonTexts);
    }

    private Completable typeTextInternal(String flavor, Color fontColor) {
        return Completable.create(onSub -> {
            this.fontColor = fontColor;
            prepareAndRepeatRuns(flavor, onSub);
        });
    }

    void prepareAndRepeatRuns(String text, CompletableSubscriber onSub) {
        repeatRuns(prepareRuns(text), onSub);
    }

    private List<String> prepareRuns(String text) {
        typewriterTableStack.peek().getTable().addAction(new FastForwardAction<>(Actions.sequence(
                Actions.moveBy(0, TEXT_LINE_HEIGHT, NEW_LINE_SPEED),
                Actions.run(() -> typewriterTableStack.peek().increaseOffsetY(TEXT_LINE_HEIGHT))
        )));
        text = replacePlaceholders(text);
        BitmapFont bitmapFont = getBitmapFontNew(NEW_FONT_SPECIAL_ELITE);
        bitmapFont.getData().markupEnabled = true;
        GlyphLayout glyphLayout = new GlyphLayout(bitmapFont, text, fontColor, TEXT_AREA_WIDTH * TABLE_SCALE / FONT_SCALE, Align.left, true);

        Map<Float, String> linesMap = new LinkedHashMap<>();

        for (GlyphLayout.GlyphRun run : glyphLayout.runs) {
            StringBuilder sentenceBuilder = new StringBuilder();
            sentenceBuilder.append("[#").append(run.color.toString()).append("]");
            for (BitmapFont.Glyph glyph : run.glyphs) {
                sentenceBuilder.append((char) glyph.id);
            }
            sentenceBuilder.append("[]");

            MapUtils.computeIfAbsent(linesMap, run.y, key -> "");
            String existing = linesMap.get(run.y);
            linesMap.put(run.y, existing + sentenceBuilder.toString());
        }
        return new LinkedList<>(linesMap.values());
    }

    String replacePlaceholders(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text
                .replaceAll(BAD_PLACEHOLDER, "[#" + HEX_BAD + "]")
                .replaceAll(GOOD_PLACEHOLDER, "[#" + HEX_GOOD + "]");
    }

    private void repeatRuns(List<String> runs, CompletableSubscriber onSub) {
        TypingLabel typingLabel = createTypingLabel(runs.remove(0));
        typewriterTableStack.peek().getTable().add(typingLabel).width(TEXT_AREA_WIDTH * TABLE_SCALE).height(TEXT_LINE_HEIGHT).center().row();

        typingLabel.setTypingListener(new TypingAdapter() {

            @Override
            public void onChar(Character ch) {
                super.onChar(ch);
                InfoStage.getFastForwardButton().setTypewriterTyping(true);
                if (FastForwardAction.isOn()) {
                    typingLabel.skipToTheEnd();
                }
            }

            @Override
            public void end() {
                if (runs.isEmpty()) {
                    InfoStage.getFastForwardButton().setTypewriterTyping(false);
                    typewriterTableStack.peek().getTable().addAction(Actions.run(onSub::onCompleted));
                } else {
                    typewriterTableStack.peek().getTable().addAction(new FastForwardAction(Actions.sequence(
                            Actions.moveBy(0, TEXT_LINE_HEIGHT, NEW_LINE_SPEED),
                            Actions.run(() -> {
                                typewriterTableStack.peek().increaseOffsetY(TEXT_LINE_HEIGHT);
                                repeatRuns(runs, onSub);
                            })
                    )));
                }

            }
        });
    }

    private TypingLabel createTypingLabel(String text) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SPECIAL_ELITE), Color.WHITE);
        TypingLabel typingLabel = new SafeTypingLabel(text, labelStyle);
        typingLabel.setWrap(true);
        typingLabel.setAlignment(Align.left, Align.center);
        typingLabel.setFontScale(FONT_SCALE);
        return typingLabel;
    }

    TextButton createTextButton(String text) {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = new TextureRegionDrawable(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND)) {

            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                float r = batch.getColor().r;
                float g = batch.getColor().g;
                float b = batch.getColor().b;
                float a = batch.getColor().a;
                batch.setColor(r, g, b, a * 0.92f);
                super.draw(batch, x, y, width, height);
                batch.setColor(r, g, b, a);
            }
        };

        textButtonStyle.down = new TextureRegionDrawable(CustomAssetManager.getTextureRegionDrawable(GRAY_BACKGROUND)) {

            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                float r = batch.getColor().r;
                float g = batch.getColor().g;
                float b = batch.getColor().b;
                float a = batch.getColor().a;
                batch.setColor(r, g, b, a);
                super.draw(batch, x, y, width, height);
                batch.setColor(r, g, b, a);
            }
        };

        textButtonStyle.checked = textButtonStyle.down;
        textButtonStyle.font = getBitmapFontNew(NEW_FONT_SPECIAL_ELITE);
        TextButton textButton = new TextButton(text, textButtonStyle);
        textButton.getLabel().setColor(Color.WHITE);
        textButton.getLabel().setFontScale(FONT_SCALE);
        return textButton;
    }

    @Override
    public Completable showPaper(boolean newPaper) {
        BigActorsManager.setTypewriterView(this);
        if (newPaper) {
            createTable();
        }
        return getTypewriterEffect().showPaper();
    }

    @Override
    public Completable finishPaper() {
        return getTypewriterEffect().finishPaper().andThen(Completable.create(onSub -> {
            typewriterTableStack.pop();
            onSub.onCompleted();
        }));
    }

    @Override
    public Completable hidePaper() {
        return getTypewriterEffect().hidePaper();
    }

    public void displayOrHide() {
    }

    @Override
    public Single<String> readInput(String label) {
        return Single.create(onSub -> {
            TypingLabel typingLabel = createTypingLabel(label);
            typingLabel.setAlignment(Align.left, Align.center);
            typingLabel.setColor(Color.BLACK);

            TextField nameTextField = new TextField("", getSkin());
            nameTextField.setMaxLength(24);

            Table miniTable = new Table();
            miniTable.add(typingLabel).align(Align.right).width(125);
            miniTable.add(nameTextField).align(Align.left).width(200);
            miniTable.padBottom(5);

            typewriterTableStack.peek().getTable().add(miniTable);
            typewriterTableStack.peek().getTable().row();


            typewriterTableStack.peek().getTable().addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.moveBy(0, TEXT_LINE_HEIGHT * 2, NEW_LINE_SPEED),
                    Actions.run(() -> typewriterTableStack.peek().increaseOffsetY(TEXT_LINE_HEIGHT))
            )));

            nameTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (nameTextField.getText().length() >= 3) {
                        typewriterQuestionTyper.typeQuestion(new String[]{"Submit"}).subscribe(ok -> {
                            onSub.onSuccess(nameTextField.getText());
                        });
                        nameTextField.removeListener(this);
                    }
                }
            });

        });
    }

    @Override
    public void recordHallOfFame(HallOfFameData hallOfFameData) {
        new FirebaseHallOfFame().recordHallOfFame(hallOfFameData);
    }
}
