package sk.sivak.eldritchhorror.core.view.bigactors;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.VisUI;
import rx.SingleSubscriber;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.view.components.action.SelectActionComponent;
import sk.sivak.eldritchhorror.core.view.components.encounter.SelectEncounterTable;
import sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.AncientOneCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.AwakenCthulhuCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.AwakenShubNiggurathCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.AwakenYogSothothCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.AzathothCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.CthulhuCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.ShubNiggurathCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.YogSothothCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.discard.DiscardSheet;
import sk.sivak.eldritchhorror.core.view.components.sheet.investigator.*;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.MonsterCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.mystery.MysteryCard;
import sk.sivak.eldritchhorror.core.view.components.sheet.reserve.ReserveSheet;
import sk.sivak.eldritchhorror.core.view.components.sheet.rumor.RumorCard;
import sk.sivak.eldritchhorror.core.view.components.track.MaxMinComponent;
import sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterViewImpl;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;

import java.util.Collection;
import java.util.List;

import static sk.sivak.eldritchhorror.core.view.utils.ButtonUtils.addClickListener;

public class BigActorsManager {

    private static BigActorsManager instance;


    private PassportTable passportTable;
    private MysteryCard mysteryCard;
    private RumorCard rumorCard;
    private AncientOneCard ancientOneCard;
    private Boolean ancientOneAwaken;
    private ReserveSheet reserveSheet;
    private DiscardSheet discardSheet;
    private MonsterCard monsterCard;
    private SelectEncounterTable selectEncounterTable;
    private TypewriterViewImpl typewriterView;
    private SelectActionComponent selectActionComponent;
    private MaxMinComponent omenTrack;
    private MaxMinComponent doomTrack;

    private boolean lock;
    private BigActorKey lockOwner;
    private BigActorKey currentBigActorKey;

    private BigActorsManager() {
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }
        lock = false;
        lockOwner = null;
        currentBigActorKey = null;
        passportTable = new PassportTable();
        mysteryCard = new MysteryCard();
        rumorCard = new RumorCard();
        reserveSheet = new ReserveSheet();
        discardSheet = new DiscardSheet();
        monsterCard = new MonsterCard();
        selectEncounterTable = new SelectEncounterTable();
        selectActionComponent = new SelectActionComponent();
        omenTrack = new MaxMinComponent(HudButtons.getOmenTrack(), BigActorKey.OMEN_TRACK);
        doomTrack = new MaxMinComponent(HudButtons.getDoomTrack(), BigActorKey.DOOM_TRACK);
    }

    public static void reset() {
        instance = new BigActorsManager();
    }
    public static void setTypewriterView(TypewriterViewImpl typewriterView) {
        getInstance().typewriterView = typewriterView;
    }

    private static BigActorsManager getInstance() {
        if (instance == null) {
            instance = new BigActorsManager();
        }
        return instance;
    }

    public static void nullifyInstance() {
        instance = null;
    }

    public static boolean isLocked() {
        return getInstance().lock;
    }

    public static void lock(BigActorKey lockOwner) {
        getInstance().lock = true;
        getInstance().lockOwner = lockOwner;
        HudButtons.getInstance().disableEnabledButtons();
    }

    public static void unlock(BigActorKey caller) {
        if (caller == null) {
            return;
        }
        if (!caller.equals(getInstance().lockOwner)) {
            return;
        }
        getInstance().lockOwner = null;
        getInstance().lock = false;
        HudButtons.getInstance().enableEnabledButtons();
    }

    private static boolean checkLockFlipAndLock(BigActorKey bigActorKey) {
        if (isLocked()) {
            return true;
        }
        flip(bigActorKey);
        lock(bigActorKey);
        return false;
    }

    public static void initPassport(InvestigatorBasics investigatorBasics, List<CardInfo> cards) {
        if (isLocked()) return;
        getInstance().passportTable.init(
                new BioTableData.Builder().fromInvestigatorBasics(investigatorBasics).build(),
                new StatsTableData.Builder().fromInvestigatorBasics(investigatorBasics).build(),
                new TokensTableData.Builder().fromInvestigatorBasics(investigatorBasics).build(),
                new SpecialTableData.Builder().fromInvestigatorBasics(investigatorBasics).build(), cards);
    }

    public static void displayOrHidePassport() {
        displayOrHide(BigActorKey.PASSPORT);
    }

    public static void initMonsterCard(MonsterInfo monsterInfo, Action0 onClickAction, Action0 onDisplayHideAction) {
        if (isLocked()) return;
        getInstance().monsterCard.init(monsterInfo, onDisplayHideAction);
        if (onClickAction != null) {
            addClickListener(getInstance().monsterCard, onClickAction::call);
        }
    }

    public static void displayOrHideMonsterCard() {
        displayOrHide(BigActorKey.MONSTER_CARD);
    }

    public static void advanceCurrentMystery(MysteryCardInfo currentMysteryCard, Action0 onClickAction, Action0 afterHideAction, int amount) {
        if (isLocked()) return;
        getInstance().mysteryCard.advanceActiveMystery(amount);
        getInstance().mysteryCard.init(currentMysteryCard);
        addClickListener(getInstance().mysteryCard, onClickAction::call);
        getInstance().mysteryCard.setAfterHideAction(afterHideAction);
    }

    public static void displayOrHideCurrentMystery() {
        displayOrHide(BigActorKey.MYSTERY_CARD);
    }

    public static void displayOrHideRumor() {
        displayOrHide(BigActorKey.RUMOR_CARD);
    }

    public static void initAncientOne(AncientOneInfo ancientOneInfo, Action0 onClickAction, Action0 afterHideAction) {
        if (isLocked()) return;
        createAncientOneCard(ancientOneInfo.getAncientOneId(), ancientOneInfo.isAwaken());
        getInstance().ancientOneCard.init(ancientOneInfo);
        addClickListener(getInstance().ancientOneCard.getActor(), onClickAction::call);
        getInstance().ancientOneCard.setAfterHideAction(afterHideAction);
    }

    public static void createAncientOneCard(AncientOneId ancientOneId, boolean awaken) {
        if (getInstance().ancientOneAwaken != null && getInstance().ancientOneAwaken.equals(awaken)) {
            return;
        }
        getInstance().ancientOneAwaken = awaken;
        switch (ancientOneId) {
            case AZATHOTH:
                getInstance().ancientOneCard = new AzathothCard();
                break;
            case CTHULHU:
                if (!awaken) {
                    getInstance().ancientOneCard = new CthulhuCard();
                } else {
                    getInstance().ancientOneCard = new AwakenCthulhuCard();
                }
                break;
            case SHUB_NIGGURATH:
                if (!awaken) {
                    getInstance().ancientOneCard = new ShubNiggurathCard();
                } else {
                    getInstance().ancientOneCard = new AwakenShubNiggurathCard();
                }
                break;
            case YOG_SOTHOTH:
                if (!awaken) {
                    getInstance().ancientOneCard = new YogSothothCard();
                } else {
                    getInstance().ancientOneCard = new AwakenYogSothothCard();
                }
                break;
        }
    }

    public static void displayOrHideAncientOne() {
        displayOrHide(BigActorKey.ANCIENT_ONE);
    }

    public static void initReserveSheet(List<AssetInfo> assets, Action0 onClickAction) {
        if (isLocked()) return;
        getInstance().reserveSheet.init(assets, onClickAction);
    }

    public static void displayOrHideReserve() {
        displayOrHide(BigActorKey.RESERVE);
    }

    public static void initDiscardSheet(List<CardInfo> discardedCards, Action0 onClickAction) {
        if (isLocked()) return;
        getInstance().discardSheet.init(discardedCards, onClickAction);
    }

    public static void displayOrHideDiscard() {
        displayOrHide(BigActorKey.DISCARD);
    }

    public static void initEncounterTable(SingleSubscriber<? super String> onSub, Collection<EncounterButtonData> encounterButtonDataList) {
        if (isLocked()) {
            Gdx.app.postRunnable(() -> {
                onSub.onError(new IllegalStateException("It's locked"));
            });
            return;
        }
        getInstance().selectEncounterTable.setOnSub(onSub);
        getInstance().selectEncounterTable.init(encounterButtonDataList);
    }

    public static void displayOrHideEncounterTable() {
        displayOrHide(BigActorKey.ENCOUNTER_TABLE);
    }

    public static void displayOrHidePaper() {
        displayOrHide(BigActorKey.PAPER);
    }

    public static void displayOrHideOmenTrack() {
        displayOrHide(BigActorKey.OMEN_TRACK);
    }

    public static void displayOrHideDoomTrack() {
        displayOrHide(BigActorKey.DOOM_TRACK);
    }

    public static void initActionsTable(List<ActionPhaseAction> actionPhaseActions, SingleSubscriber<? super ActionPhaseAction> sub) {
        if (isLocked()) {
            sub.onError(new IllegalArgumentException("something is blocking action table"));
            return;
        }
        getInstance().selectActionComponent.setSubscriber(sub);
        getInstance().selectActionComponent.init(actionPhaseActions);
    }

    public static void displayOrHideActionsTable() {
        displayOrHide(BigActorKey.ACTION_TABLE);
    }

    private static void displayOrHide(BigActorKey bigActorKey) {
        if (checkLockFlipAndLock(bigActorKey)) return;
        bigActorKey.displayHideAction.call();
    }

    private static void flip(BigActorKey bigActorKey) {
        if (getInstance().currentBigActorKey == null) {
            getInstance().currentBigActorKey = bigActorKey;
            return;
        }
        if (getInstance().currentBigActorKey == bigActorKey) {
            getInstance().currentBigActorKey = null;
            onEmptyScren();
        } else {
            getInstance().currentBigActorKey.displayHideAction.call();
            getInstance().currentBigActorKey = bigActorKey;
        }
    }

    private static void onEmptyScren() {
        HudButtons.getInstance().uncheckAll();
        if (getInstance().selectActionComponent.isOnStage()) {
            getInstance().selectActionComponent.flipAlpha();
            getInstance().currentBigActorKey = BigActorKey.ACTION_TABLE;
        }
    }


    public static MonsterCard getMonsterCard() {
        return getInstance().monsterCard;
    }

    public static MysteryCard getMysteryCard() {
        return getInstance().mysteryCard;
    }

    public static PassportTable getPassportTable() {
        return getInstance().passportTable;
    }

    public static AncientOneCard getAncientOneCard() {
        return getInstance().ancientOneCard;
    }

    public static ReserveSheet getReserveSheet() {
        return getInstance().reserveSheet;
    }

    public static boolean isActionTableOnStage() {
        return getInstance().selectActionComponent.getParent() != null;
    }

    public static RumorCard getRumorCard() {
        return getInstance().rumorCard;
    }


    public enum BigActorKey {
        OMEN_TRACK(() -> getInstance().omenTrack.maximizeOrMinimize()),
        DOOM_TRACK(() -> getInstance().doomTrack.maximizeOrMinimize()),
        ACTION_TABLE(() -> getInstance().selectActionComponent.displayOrHide()),
        MYSTERY_CARD(() -> getInstance().mysteryCard.displayOrHide()),
        RUMOR_CARD(() -> getInstance().rumorCard.displayOrHide()),
        ANCIENT_ONE(() -> getInstance().ancientOneCard.displayOrHide()),
        RESERVE(() -> getInstance().reserveSheet.displayOrHide(null)),
        DISCARD(() -> getInstance().discardSheet.displayOrHide(null)),
        PASSPORT(() -> getInstance().passportTable.displayOrHide()),
        PAPER(() -> getInstance().typewriterView.displayOrHide()),
        MONSTER_CARD(() -> getInstance().monsterCard.displayOrHide()),
        ENCOUNTER_TABLE(() -> getInstance().selectEncounterTable.displayOrHide()),
        BLOCKER_QUESTION(() -> {});

        private Action0 displayHideAction;

        BigActorKey(Action0 displayHideAction) {
            this.displayHideAction = displayHideAction;
        }

    }

    public static boolean isPaperDisplayed() {
        return getInstance().currentBigActorKey == BigActorKey.PAPER;
    }

    public static boolean isMonsterCardDisplayed() {
        return getInstance().currentBigActorKey == BigActorKey.MONSTER_CARD;
    }

    public static void setCurrentBigActorKey(BigActorKey bigActorKey) {
        getInstance().currentBigActorKey = bigActorKey;
    }
}
