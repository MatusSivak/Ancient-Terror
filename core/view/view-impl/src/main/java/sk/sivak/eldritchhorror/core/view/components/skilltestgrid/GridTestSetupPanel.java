package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.function.Consumer;

final class GridTestSetupPanel extends Table {
    private final SelectBox<Integer> shifts;
    private final SelectBox<Integer> swaps;
    private final SelectBox<Integer> rerolls;
    private final SelectBox<Integer> lifts;
    private final SelectBox<Integer> superRerolls;
    private final SelectBox<Integer> gaps;
    private final SelectBox<TestMode> difficulty;
    private final SelectBox<GridSuccessTarget> target;
    private final CheckBox momentum;
    private final CheckBox blind;
    private final Label targetHint;
    private final Label.LabelStyle labelStyle;
    private final SelectBox.SelectBoxStyle selectStyle;

    GridTestSetupPanel(Skin skin, TextButton confirm, GridTestParameters parameters,
                       Consumer<GridTestParameters> onConfirm, Runnable onSettingChanged) {
        labelStyle = new Label.LabelStyle(skin.getFont("small"), Color.LIGHT_GRAY);
        selectStyle = new SelectBox.SelectBoxStyle(skin.get(SelectBox.SelectBoxStyle.class));
        selectStyle.font = skin.getFont("small");
        selectStyle.listStyle = new List.ListStyle(selectStyle.listStyle);
        selectStyle.listStyle.font = selectStyle.font;
        pad(12f);
        top().left();
        defaults().minWidth(0f).padBottom(4f);
        add(label("Prepare test", 22f)).colspan(2).left().padBottom(8f).row();
        add(label("Adjust the starting values.", 12f)).colspan(2).left().padBottom(12f).row();

        shifts = count("shifts", 1, 4);
        swaps = count("swaps", 0, 4);
        rerolls = count("rerolls", 0, 2);
        lifts = count("lifts", 0, 1);
        superRerolls = count("super-rerolls", 0, 1);
        gaps = count("gaps", 0, 3);
        difficulty = new SelectBox<>(selectStyle);
        difficulty.setName("difficulty");
        difficulty.setItems(TestMode.NORMAL, TestMode.BLESSED, TestMode.CURSED);
        target = new SelectBox<>(selectStyle);
        target.setName("success-target");
        target.setItems(GridSuccessTarget.ONE, GridSuccessTarget.TWO, GridSuccessTarget.UNLIMITED);
        row("Shifts", shifts);
        row("Swaps", swaps);
        row("Rerolls", rerolls);
        row("Lift", lifts);
        row("Super reroll", superRerolls);
        add(label("RULES", 10f)).colspan(2).left().padTop(8f).padBottom(8f).row();
        row("Difficulty", difficulty);
        row("Gaps", gaps);

        momentum = new CheckBox(" Momentum", skin);
        momentum.setName("momentum");
        blind = new CheckBox(" Blind", skin);
        blind.setName("blind");
        momentum.getLabel().setFontScale(10f / momentum.getLabel().getStyle().font.getCapHeight());
        blind.getLabel().setFontScale(10f / blind.getLabel().getStyle().font.getCapHeight());
        Table options = new Table();
        options.add(momentum).left().expandX();
        options.add(blind).left();
        add(options).colspan(2).growX().height(26f).padTop(4f).row();
        row("Min. successes", target);
        targetHint = label("", 10f);
        targetHint.setWrap(true);
        add(targetHint).colspan(2).growX().height(30f).padBottom(8f).row();
        confirm.setName("confirm-test");
        add(confirm).colspan(2).growX().height(34f).row();

        setParameters(parameters);
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateTargetHint();
                onSettingChanged.run();
            }
        });
        confirm.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!confirm.isDisabled()) {
                    onConfirm.accept(getParameters());
                }
                event.stop();
            }
        });
    }

    private Label label(String text, float height) {
        Label label = new Label(text, labelStyle);
        fitLabel(label, height, 242f);
        return label;
    }

    private static void fitLabel(Label label, float height, float width) {
        float scale = Math.min(height / Math.max(label.getStyle().font.getLineHeight(), label.getPrefHeight()),
                width / Math.max(1f, label.getPrefWidth()));
        label.setFontScale(scale);
    }

    private SelectBox<Integer> count(String name, int minimum, int maximum) {
        SelectBox<Integer> box = new SelectBox<>(selectStyle);
        box.setName(name);
        Integer[] values = new Integer[maximum - minimum + 1];
        for (int i = 0; i < values.length; i++) {
            values[i] = minimum + i;
        }
        box.setItems(values);
        return box;
    }

    private void row(String text, Actor field) {
        Label label = new Label(text, labelStyle);
        fitLabel(label, 16f, 122f);
        add(label).width(122f).left().expandX().padRight(8f);
        add(field).width(112f).height(28f).row();
    }

    void setParameters(GridTestParameters parameters) {
        shifts.setSelected(parameters.getShifts());
        swaps.setSelected(parameters.getSwaps());
        rerolls.setSelected(parameters.getRerolls());
        lifts.setSelected(parameters.getLifts());
        superRerolls.setSelected(parameters.getSuperRerolls());
        gaps.setSelected(parameters.getGaps());
        difficulty.setSelected(parameters.getDifficulty());
        target.setSelected(parameters.getSuccessTarget());
        momentum.setChecked(parameters.isMomentum());
        blind.setChecked(parameters.isBlind());
        updateTargetHint();
    }

    GridTestParameters getParameters() {
        return new GridTestParameters(shifts.getSelected(), swaps.getSelected(), rerolls.getSelected(),
                lifts.getSelected(), superRerolls.getSelected(), gaps.getSelected(),
                momentum.isChecked(), blind.isChecked(), difficulty.getSelected(), target.getSelected());
    }

    private void updateTargetHint() {
        targetHint.setText(target.getSelected().isUnlimited()
                ? "Score only. No pass/fail target."
                : "Reach the target before actions run out.");
    }
}
