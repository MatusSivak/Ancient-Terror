package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.util.*;
import sk.sivak.eldritchhorror.core.constants.*;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.*;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.*;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.*;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import static sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig.YigEffects.*;
import static sk.sivak.eldritchhorror.core.constants.MysteryCardId.Yig.*;

/** Setup is separate from listener restoration, so loading never repeats spawns or costs. */
public final class YigMysteryListener extends AbstractMysteryListener {
    private final MysteryCardId.Yig id;
    private final List<EventListener> listeners = new ArrayList<>();
    private int progress;
    public YigMysteryListener(MysteryCardInfo info) { super(info); id=(MysteryCardId.Yig)info.getMysteryCardId(); }
    private EpicMonsterId epic() {
        switch(id) {
            case DESCENDANTS_OF_YIG:return EpicMonsterId.CHILDREN_OF_YIG;
            case THE_WINGED_SERPENT:return EpicMonsterId.WINGED_SERPENT;
            case SERPENTS_NEST:return EpicMonsterId.YIG;
            default:return null;
        }
    }
    private MonsterInfo monster() {
        if(epic()==null)return null;
        for(MonsterInfo m:p().getMonsterCup().getMonsters()) if(m.getMonsterId()==epic())return m;
        return null;
    }
    @Override protected int getProgress() {
        MonsterInfo m=monster();
        return m==null?progress:mysteryCardInfo.getMysteryComplexity()-m.getCurrentHealth();
    }
    @Override protected List<LocationId> getPinLocations() {
        if(id==MIGRATION_OF_SERPENTS)return new ArrayList<>(p().getCluePool().getClueLocations());
        return Collections.emptyList();
    }
    private boolean hasEncounterPins() {
        // Monsters and Clues already mark their locations; Pins identify special encounters.
        return id==KN_YAN_UNEARTHED || id==RISE_OF_THE_SERPENT_PEOPLE;
    }
    @Override public void register() {
        sequence(() -> {
            if(hasEncounterPins()) super.register();
            else p().getGameService().showCurrentMysteryCard(true, false);
            attach();
            if(epic()!=null) {
                if(id==SERPENTS_NEST) for(MonsterInfo m:p().getMonsterCup().getMonsters())
                    if(m.getMonsterId()==NonEpicMonsterId.CULTIST)p().getMonsterService().moveMonster(m,LocationId.SPACE_7);
                spawn(epic(),mysteryCardInfo.getPinLocations().get(0));
            }
            if(id==MIGRATION_OF_SERPENTS) for(ClueInfo clue:new ArrayList<>(p().getCluePool().getSpawnedClues()))
                moveClueToExpedition(clue.getSpawnLocationId(),clue.getCurrentLocationId());
        });
    }
    @Override public void justRegisterListeners(int progress) { this.progress=progress; attach(); }
    @Override public void justAddRedPins() {
        if(hasEncounterPins()) p().getGameService().justAddRedPins();
    }
    @Override public void unregister() {
        for(EventListener listener:listeners)p().getEventQueue().unregisterListener(listener);
        if(hasEncounterPins()) p().getGameService().clearRedPins();
    }
    private void attach() {
        if(epic()!=null) {
            EventListenerImpl<DefeatMonsterData> defeated=new EventListenerImpl<DefeatMonsterData>() {
                public Class<DefeatMonsterData> getDataClass(){return DefeatMonsterData.class;}
                public void onNotify(DefeatMonsterData data) {
                    if(data.getMonsterInfo().getMonsterId()!=epic())return;
                    progress=mysteryCardInfo.getMysteryComplexity();
                    if(id==SERPENTS_NEST)p().getDoomOmenService().resolveCurrentMystery();
                }
            };
            listeners.add(defeated); p().getEventQueue().addAfterEventListener(defeated,BeforeAfterEvent.DEFEAT_MONSTER);
        }
        if(id==KN_YAN_UNEARTHED || id==RISE_OF_THE_SERPENT_PEOPLE) {
            EventListenerImpl<AvailableEncounters> collect=new EventListenerImpl<AvailableEncounters>() {
                public Class<AvailableEncounters> getDataClass(){return AvailableEncounters.class;}
                public void onNotify(AvailableEncounters data) {
                    if(getProgress()<mysteryCardInfo.getMysteryComplexity() && mysteryCardInfo.getPinLocations().contains(p().getInvestigators().getActiveInvestigator().getCurrentLocationId()))
                        data.addEncounter(new MysteryEncounter(mysteryCardInfo.getName()));
                }
            };
            EventListenerImpl<MysteryEncounter> encounter=new EventListenerImpl<MysteryEncounter>() {
                public Class<MysteryEncounter> getDataClass(){return MysteryEncounter.class;}
                public void onNotify(MysteryEncounter data) { sequence(() -> {
                    if(id==KN_YAN_UNEARTHED)new YigSpecialEncounter(mysteryCardInfo,() -> addProgress()).execute();
                    else test(Stat.OBSERVATION,-1,() -> ask("Spend 2 Clues to advance the Active Mystery?",
                            () -> spend(2,0,0,() -> removeToken(p().getInvestigators().getActiveInvestigator().getCurrentLocationId()),() -> {}),() -> {}),
                            () -> p().getMonsterService().ambush(NonEpicMonsterId.CULTIST).subscribe());
                    p().getService().convertTo(MysteryEncounter.class,() -> data);
                }); }
            };
            listeners.add(collect);listeners.add(encounter);
            p().getEventQueue().addDirectEventListener(collect,DirectEvent.COLLECT_COMMON_ENCOUNTERS);
            p().getEventQueue().addDirectEventListener(encounter,DirectEvent.ENCOUNTER_ACTIVE_MYSTERY);
        }
        if(id==CROWN_OF_THE_SERPENT) attachCrown();
    }
    private void removeToken(LocationId location) {
        if(!mysteryCardInfo.getPinLocations().remove(location))return;
        addProgress(); p().getGameService().clearRedPins(); p().getGameService().justAddRedPins();
    }
    private void addProgress() {
        if(progress>=mysteryCardInfo.getMysteryComplexity())return;
        progress++;p().getGameService().advanceCurrentMysteryCard(1);
    }
    @Override public void advanceActiveMystery() {
        sequence(() -> {
            if(getProgress()>=mysteryCardInfo.getMysteryComplexity())return;
            MonsterInfo m=monster();
            if(m!=null) { p().getMonsterService().dealDamageToMonster(m,2);return; }
            if(id==CROWN_OF_THE_SERPENT) {
                mysteryCardInfo.setClueCredit(mysteryCardInfo.getClueCredit()+1);
                p().getGameService().displayText("Advance the Active Mystery: reduce the Clue cost to solve this Mystery by 1.");
            }
            else addProgress();
        });
    }
    private void attachCrown() {
        EventListenerImpl<GainCardData> substitute=new EventListenerImpl<GainCardData>() {
            public Class<GainCardData> getDataClass(){return GainCardData.class;}
            public void onNotify(GainCardData data) {
                if(!data.isArtifact() || !p().getArtifactsDeck().isInDeckOrDiscardPile(ArtifactId.SERPENT_CROWN))return;
                ask("Gain the Serpent Crown Artifact instead?",
                    () -> p().getService().convertTo(GainCardData.class,() -> new GainCardData(ArtifactId.SERPENT_CROWN)),
                    () -> p().getService().convertTo(GainCardData.class,() -> data));
            }
        };
        EventListenerImpl<Object> resolve=new EventListenerImpl<Object>() {
            public Class<Object> getDataClass(){return Object.class;}
            public void onNotify(Object ignored) {
                if(progress>=mysteryCardInfo.getMysteryComplexity())return;
                InvestigatorId previous=investigator();
                for(InvestigatorRead inv:p().getInvestigators().getOnBoardInvestigators()) {
                    InvestigatorId owner=inv.getInfo().getInvestigatorId();
                    if(!p().getArtifactsDeck().hasArtifact(owner,ArtifactId.SERPENT_CROWN))continue;
                    sequence(() -> {
                        p().getInvestigatorService().changeActiveInvestigator(owner);
                        later(() -> ask("Attempt a Will (-1) test to break the Serpent Crown's hold?",() -> test(Stat.WILL,-1,() -> {
                            int allies=0;for(CardInfo card:EncounterUtils.getPossession(owner,AssetTrait.ALLY))if(card instanceof AssetInfo)allies++;
                            if(allies<2)return;
                            int clues = Math.max(0,p().getInvestigators().getPlayers()-mysteryCardInfo.getClueCredit());
                            ask("Spend " + clues + " Clues and discard 2 Ally Assets to solve this Mystery?",
                                () -> spend(clues,0,0,() -> discardPossessions(AssetTrait.ALLY,2,true,() -> {
                                    mysteryCardInfo.setClueCredit(0);
                                    addProgress();
                                }),() -> {}),() -> {});
                        },() -> {}),() -> {}));
                        p().getInvestigatorService().changeActiveInvestigator(previous);
                    });
                    break;
                }
            }
        };
        listeners.add(substitute);listeners.add(resolve);
        p().getEventQueue().addBeforeEventListener(substitute,BeforeAfterEvent.SELECT_CARD_TO_GAIN);
        p().getEventQueue().addBeforeEventListener(resolve,BeforeAfterEvent.RESOLVE_CURRENT_MYSTERY);
    }
}
