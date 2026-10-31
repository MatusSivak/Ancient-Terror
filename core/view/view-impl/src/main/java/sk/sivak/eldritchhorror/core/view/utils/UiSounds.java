package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager.BigActorKey;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.components.hud.HudButton;

import java.util.Set;

/** UI gestures stay audible during fast-forward; automatic panel animations do not. */
public final class UiSounds {
    public enum Cue {
        BUTTON_CLICK("button_click", .75f), HUD_CLICK("hud_click", .80f),
        ACTION_CLICK("action_click", .85f),
        CARD_OPEN("card_open", .80f), CARD_CLOSE("card_close", .72f),
        CARD_OPEN_SPELL("card_reveal_spell", .90f), CARD_OPEN_ARTIFACT("card_reveal_artifact", .90f),
        CARD_OPEN_SPELL_INCANTATION("card_reveal_spell_incantation", .85f),
        CARD_OPEN_SPELL_RITUAL("card_reveal_spell_ritual", .85f),
        CARD_OPEN_CONDITION("card_reveal_condition", .90f), CARD_OPEN_ASSET("card_reveal_asset", .90f),
        CARD_OPEN_ASSET_WEAPON("card_reveal_asset_weapon", .90f),
        CARD_OPEN_ASSET_TOME("card_reveal_asset_tome", .90f),
        CARD_OPEN_ASSET_ALLY("card_reveal_asset_ally", .90f),
        CARD_OPEN_ASSET_SERVICE("card_reveal_asset_service", .85f),
        CARD_OPEN_ASSET_TRINKET("card_reveal_asset_trinket", .85f),
        CARD_OPEN_CONDITION_BOON("card_reveal_condition_boon", .85f),
        CARD_OPEN_CONDITION_MADNESS("card_reveal_condition_madness", .85f),
        CARD_OPEN_CONDITION_INJURY("card_reveal_condition_injury", .90f),
        CARD_OPEN_CONDITION_ILLNESS("card_reveal_condition_illness", .85f),
        CARD_OPEN_CONDITION_DEAL("card_reveal_condition_deal", .85f),
        CARD_OPEN_CONDITION_BANE("card_reveal_condition_bane", .90f),
        SHEET_OPEN("sheet_open", .80f), SHEET_CLOSE("sheet_close", .72f),
        ENCOUNTER_OPEN("encounter_open", .80f), ENCOUNTER_CLOSE("encounter_close", .72f),
        MYSTERY_OPEN("mystery_open", .82f), MYSTERY_CLOSE("mystery_close", .72f),
        ANCIENT_OPEN("ancient_open", .85f), ANCIENT_CLOSE("ancient_close", .75f),
        POPUP_OPEN("popup_open", .70f), POPUP_CLOSE("popup_close", .65f);

        public final String path;
        public final float volume;

        Cue(String name, float volume) {
            path = "sounds/ui_" + name + ".wav";
            this.volume = volume;
        }
    }

    private static int inputDepth;

    private UiSounds() { }

    public static void play(Cue cue) {
        SoundPlayer.play(CustomAssetManager.getLoadedSound(cue.path), cue.volume, 1f, 0f);
    }

    public static void transition(Cue cue) {
        if (!FastForwardAction.isOn() || inputDepth > 0) play(cue);
    }

    public static void userAction(Runnable action) {
        inputDepth++;
        try {
            action.run();
        } finally {
            inputDepth--;
        }
    }

    /** Artifacts have their own reveal; spells, assets and conditions sound by category; else CARD_OPEN. */
    public static Cue cardOpenCue(Object cardInfo) {
        if (cardInfo instanceof SpellInfo) return spellCue(((SpellInfo) cardInfo).getTraits());
        if (cardInfo instanceof ArtifactInfo) return Cue.CARD_OPEN_ARTIFACT;
        if (cardInfo instanceof ConditionInfo) return conditionCue(((ConditionInfo) cardInfo).getTraits());
        if (cardInfo instanceof AssetInfo) return assetCue(((AssetInfo) cardInfo).getTraits());
        return Cue.CARD_OPEN;
    }

    public static Cue cardOpenCue(CardTemplate template) {
        if (template.getSpellInfo() != null) return cardOpenCue(template.getSpellInfo());
        if (template.getArtifactInfo() != null) return Cue.CARD_OPEN_ARTIFACT;
        if (template.getConditionInfo() != null) return cardOpenCue(template.getConditionInfo());
        if (template.getAssetInfo() != null) return cardOpenCue(template.getAssetInfo());
        return Cue.CARD_OPEN;
    }

    /** Glamour, teamwork-only and untraited spells keep the original spell reveal. */
    static Cue spellCue(Set<SpellTrait> traits) {
        if (traits == null) return Cue.CARD_OPEN_SPELL;
        if (traits.contains(SpellTrait.INCANTATION)) return Cue.CARD_OPEN_SPELL_INCANTATION;
        if (traits.contains(SpellTrait.RITUAL)) return Cue.CARD_OPEN_SPELL_RITUAL;
        return Cue.CARD_OPEN_SPELL;
    }

    /** First matching category wins, e.g. a magical weapon sounds like a weapon. */
    static Cue assetCue(Set<AssetTrait> traits) {
        if (traits == null) return Cue.CARD_OPEN_ASSET;
        if (traits.contains(AssetTrait.WEAPON)) return Cue.CARD_OPEN_ASSET_WEAPON;
        if (traits.contains(AssetTrait.TOME)) return Cue.CARD_OPEN_ASSET_TOME;
        if (traits.contains(AssetTrait.ALLY)) return Cue.CARD_OPEN_ASSET_ALLY;
        if (traits.contains(AssetTrait.SERVICE) || traits.contains(AssetTrait.TASK)) return Cue.CARD_OPEN_ASSET_SERVICE;
        if (traits.contains(AssetTrait.TRINKET) || traits.contains(AssetTrait.MAGICAL) || traits.contains(AssetTrait.RELIC))
            return Cue.CARD_OPEN_ASSET_TRINKET;
        return Cue.CARD_OPEN_ASSET;
    }

    /** Restrictions and anything uncategorized keep the chain reveal. */
    static Cue conditionCue(Set<ConditionTrait> traits) {
        if (traits == null) return Cue.CARD_OPEN_CONDITION;
        if (traits.contains(ConditionTrait.BOON)) return Cue.CARD_OPEN_CONDITION_BOON;
        if (traits.contains(ConditionTrait.MADNESS)) return Cue.CARD_OPEN_CONDITION_MADNESS;
        if (traits.contains(ConditionTrait.INJURY)) return Cue.CARD_OPEN_CONDITION_INJURY;
        if (traits.contains(ConditionTrait.ILLNESS)) return Cue.CARD_OPEN_CONDITION_ILLNESS;
        if (traits.contains(ConditionTrait.DEAL)) return Cue.CARD_OPEN_CONDITION_DEAL;
        if (traits.contains(ConditionTrait.BANE)) return Cue.CARD_OPEN_CONDITION_BANE;
        return Cue.CARD_OPEN_CONDITION;
    }

    public static Cue sheetCue(BigActorKey key, boolean opening) {
        if (key == BigActorKey.ANCIENT_ONE) return opening ? Cue.ANCIENT_OPEN : Cue.ANCIENT_CLOSE;
        if (key == BigActorKey.MYSTERY_CARD) return opening ? Cue.MYSTERY_OPEN : Cue.MYSTERY_CLOSE;
        if (key == BigActorKey.MONSTER_CARD || key == BigActorKey.RUMOR_CARD)
            return opening ? Cue.CARD_OPEN : Cue.CARD_CLOSE;
        if (key == BigActorKey.ENCOUNTER_TABLE) return opening ? Cue.ENCOUNTER_OPEN : Cue.ENCOUNTER_CLOSE;
        return opening ? Cue.SHEET_OPEN : Cue.SHEET_CLOSE;
    }

    /** Safe to call from both a button factory and its action binding. No ChangeEvent sounds. */
    public static void attachButton(Button button) {
        // The action tile includes its caption and plays one cue from the wrapper's click handler.
        if (button instanceof sk.sivak.eldritchhorror.core.view.components.action.ActionButton) return;
        if (clickListener(button) == null) button.addCaptureListener(new ButtonSoundListener(button));
    }

    public static void attachButtons(Actor actor) {
        if (actor instanceof Button) attachButton((Button) actor);
        if (actor instanceof Group) {
            for (Actor child : ((Group) actor).getChildren()) attachButtons(child);
        }
    }

    private static ButtonSoundListener clickListener(Actor actor) {
        for (EventListener listener : actor.getCaptureListeners()) {
            if (listener instanceof ButtonSoundListener) return (ButtonSoundListener) listener;
        }
        return null;
    }

    private static final class ButtonSoundListener extends ClickListener {
        private final Button owner;

        ButtonSoundListener(Button owner) { this.owner = owner; }

        @Override public void clicked(InputEvent event, float x, float y) {
            if (owner.isDisabled() || (owner instanceof HudButton && ((HudButton) owner).looksDisabled())) return;
            for (Actor actor = owner; actor != null; actor = actor.getParent()) {
                if (!actor.isVisible() || actor.getTouchable() == Touchable.disabled) return;
            }
            // Nested buttons bubble through their parents; only the closest control clicks.
            for (Actor actor = event.getTarget(); actor != null && actor != owner; actor = actor.getParent()) {
                if (clickListener(actor) != null) return;
            }
            play(owner instanceof HudButton ? Cue.HUD_CLICK : Cue.BUTTON_CLICK);
        }
    }
}
