package sk.sivak.eldritchhorror.core.model;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.*;
import sk.sivak.eldritchhorror.core.constants.spell.blessingofisis.BlessingOfIsisSpell;
import sk.sivak.eldritchhorror.core.constants.spell.blessingofisis.BlessingOfIsisSpellBack1;
import sk.sivak.eldritchhorror.core.constants.spell.blessingofisis.BlessingOfIsisSpellBack2;
import sk.sivak.eldritchhorror.core.constants.spell.blessingofisis.BlessingOfIsisSpellBack3;
import sk.sivak.eldritchhorror.core.constants.spell.blessingofisis.BlessingOfIsisSpellBack4;
import sk.sivak.eldritchhorror.core.constants.spell.clairvoyance.ClairvoyanceSpell;
import sk.sivak.eldritchhorror.core.constants.spell.clairvoyance.ClairvoyanceSpellBack1;
import sk.sivak.eldritchhorror.core.constants.spell.clairvoyance.ClairvoyanceSpellBack2;
import sk.sivak.eldritchhorror.core.constants.spell.clairvoyance.ClairvoyanceSpellBack3;
import sk.sivak.eldritchhorror.core.constants.spell.clairvoyance.ClairvoyanceSpellBack4;
import sk.sivak.eldritchhorror.core.constants.spell.conjuration.ConjurationSpell;
import sk.sivak.eldritchhorror.core.constants.spell.conjuration.ConjurationSpellBack1;
import sk.sivak.eldritchhorror.core.constants.spell.conjuration.ConjurationSpellBack2;
import sk.sivak.eldritchhorror.core.constants.spell.conjuration.ConjurationSpellBack3;
import sk.sivak.eldritchhorror.core.constants.spell.conjuration.ConjurationSpellBack4;
import sk.sivak.eldritchhorror.core.constants.spell.fleshward.FleshWardSpell;
import sk.sivak.eldritchhorror.core.constants.spell.fleshward.FleshWardSpellBack1;
import sk.sivak.eldritchhorror.core.constants.spell.fleshward.FleshWardSpellBack4;
import sk.sivak.eldritchhorror.core.constants.spell.instillbravery.InstillBraverySpell;
import sk.sivak.eldritchhorror.core.constants.spell.instillbravery.InstillBraverySpellBack1;
import sk.sivak.eldritchhorror.core.constants.spell.mistsofreleh.MistsOfRelehSpell;
import sk.sivak.eldritchhorror.core.constants.spell.mistsofreleh.MistsOfRelehSpellBack1;
import sk.sivak.eldritchhorror.core.constants.spell.plumbthevoid.PlumbTheVoidSpell;
import sk.sivak.eldritchhorror.core.constants.spell.plumbthevoid.PlumbTheVoidSpellBack1;
import sk.sivak.eldritchhorror.core.constants.spell.wither.WitherSpell;
import sk.sivak.eldritchhorror.core.constants.spell.wither.WitherSpellBack2;
import sk.sivak.eldritchhorror.core.model.save.ConditionsDeckSaveData;
import sk.sivak.eldritchhorror.core.model.save.ConditionsDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SpellsDeckSaveData;
import sk.sivak.eldritchhorror.core.model.save.SpellsDeckSaveDataRead;

import java.util.*;

import static java8.features.stream.Stream.anyMatch;
import static java8.features.stream.Stream.findFirstOrException;
import static java8.features.util.MapUtils.computeIfAbsent;
import static java8.features.util.MapUtils.getOrDefault;

public class SpellsDeck implements SpellsDeckWrite {

    private static final Logger logger = LogManager.getLogger(SpellsDeck.class);

    private Map<InvestigatorId, List<SpellInfo>> investigatorSpellsMap;

    private List<SpellInfo> deck;

    @Override
    public void createDeck() {
        investigatorSpellsMap = new HashMap<>();
        deck = new LinkedList<>();

        for (SpellId spellId : SpellId.values()) {
            int suffix = 1;
            while (true) {
                try {
                    Class<?> backSpellClassName = Class.forName(spellId.getSpellBackClassName(suffix++));
                    AbstractSpellInfo abstractSpellInfo = (AbstractSpellInfo) Class.forName(spellId.getSpellClassName()).newInstance();
                    abstractSpellInfo.setSpellBack((SpellBack) backSpellClassName.newInstance());
                    deck.add(abstractSpellInfo);
                } catch (ClassNotFoundException e) {
                    if (suffix > 4) {
                        break;
                    }
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        Collections.shuffle(deck);

//        FleshWardSpell spell = new FleshWardSpell();
//        spell.setSpellBack(new FleshWardSpellBack1());
//        deck.add(0,spell);
    }

    @Override
    public boolean hasSpell(InvestigatorId investigatorId, SpellId spellId) {
        List<SpellInfo> spells = getOrDefault(investigatorSpellsMap, investigatorId, new LinkedList<>());
        return anyMatch(spells, spell -> spellId == spell.getId());
    }

    @Override
    public boolean hasTrait(InvestigatorId investigatorId, SpellTrait trait) {
        List<SpellInfo> spells = getOrDefault(investigatorSpellsMap, investigatorId, new LinkedList<>());
        return anyMatch(spells, spell -> spell.getTraits().contains(trait));
    }

    @Override
    public List<SpellInfo> getSpells(InvestigatorId investigatorId) {
        return getOrDefault(investigatorSpellsMap, investigatorId, new LinkedList<>());
    }

    @Override
    public SpellInfo gainSpell(SpellId spellId, InvestigatorId investigatorId) {
        return gainSpell(ci -> spellId == ci.getId(), investigatorId);
    }

    @Override
    public SpellInfo gainSpell(SpellTrait spellTrait, InvestigatorId investigatorId) {
        return gainSpell(ci -> ci.getTraits().contains(spellTrait), investigatorId);
    }

    @Override
    public SpellInfo gainSpell(InvestigatorId investigatorId) {
        return gainSpell(ci -> true, investigatorId);
    }

    @Override
    public InvestigatorId getSpellOwner(SpellInfo spellInfo) {
        for (Map.Entry<InvestigatorId, List<SpellInfo>> entry: investigatorSpellsMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            for (SpellInfo value : entry.getValue()) {
                if (value == spellInfo) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    @Override
    public boolean canGetTrait(InvestigatorId investigatorId, SpellTrait trait) {
        return canGainSpell(ci -> ci.getTraits().contains(trait), investigatorId);
    }

    @Override
    public boolean canGetRandomSpell(InvestigatorId activeInvestigatorId) {
        return canGainSpell(ci -> true, activeInvestigatorId);
    }

    private SpellInfo gainSpell(Predicate<SpellInfo> predicate, InvestigatorId investigatorId) {
        List<SpellInfo> spellsList = getOrDefault(investigatorSpellsMap, investigatorId, new LinkedList<>());
        Predicate<SpellInfo> notOwningPredicate = spellInfo -> !Stream.map(spellsList, SpellInfo::getId).contains(spellInfo.getId());
        Predicate<SpellInfo> compositePredicate = spellInfo -> notOwningPredicate.test(spellInfo) && predicate.test(spellInfo);
        SpellInfo spellInfo = findFirstOrException(deck, compositePredicate);
        deck.remove(spellInfo);

        spellsList.add(spellInfo);
        investigatorSpellsMap.put(investigatorId, spellsList);
        logger.info("Investigator '" + investigatorId + "' gained spell '" + spellInfo.getId() + "'");
        return spellInfo;
    }

    private boolean canGainSpell(Predicate<SpellInfo> predicate, InvestigatorId investigatorId) {
        List<SpellInfo> spellsList = getOrDefault(investigatorSpellsMap, investigatorId, new LinkedList<>());
        Predicate<SpellInfo> notOwningPredicate = spellInfo -> !Stream.map(spellsList, SpellInfo::getId).contains(spellInfo.getId());
        Predicate<SpellInfo> compositePredicate = spellInfo -> notOwningPredicate.test(spellInfo) && predicate.test(spellInfo);
        return anyMatch(deck, compositePredicate);
    }

    @Override
    public void discard(InvestigatorId investigatorId, SpellInfo spellInfo) {
        investigatorSpellsMap.get(investigatorId).remove(spellInfo);
        deck.add(spellInfo);
        Collections.shuffle(deck);
        logger.info("Investigator '" + investigatorId + "' discarded spell '" + spellInfo.getName() + "'.");
    }

    @Override
    public void disable(SpellInfo spellInfo) {
        ((AbstractSpellInfo) spellInfo).disable();
    }

    @Override
    public void enable(SpellInfo spellInfo) {
        ((AbstractSpellInfo) spellInfo).enable();
    }

    @Override
    public void changeOwner(SpellInfo cardInfo, InvestigatorId newOwner) {
        for (Map.Entry<InvestigatorId, List<SpellInfo>> entry : investigatorSpellsMap.entrySet()) {
            if (entry.getValue().remove(cardInfo)) {
                logger.info("Spell '" + cardInfo.getName() + "' removed from investigator " + entry.getKey());
            }
        }
        addToInvestigator(newOwner, cardInfo);
    }

    private void addToInvestigator(InvestigatorId investigatorId, SpellInfo spellInfo) {
        computeIfAbsent(investigatorSpellsMap, investigatorId, k -> new LinkedList<>());
        investigatorSpellsMap.get(investigatorId).add(spellInfo);
        logger.info("Spell '" + spellInfo.getName() + "' added to investigator '" + investigatorId + "'");
    }

    @Override
    public SpellsDeckSaveData save() {
        SpellsDeckSaveData spellsDeckSaveData = new SpellsDeckSaveData();
        HashMap<String, List<? extends SpellsDeckSaveDataRead.SpellInfoSaveDataRead>> saveDataMap = new HashMap<>();
        for (Map.Entry<InvestigatorId, List<SpellInfo>> entry : investigatorSpellsMap.entrySet()) {
            List<SpellsDeckSaveData.SpellInfoSaveData> spellInfoSaveDataList = Stream.collectToList(Stream.map(entry.getValue(),
                    spellInfo -> {
                        SpellsDeckSaveData.SpellInfoSaveData spellInfoSaveData = new SpellsDeckSaveData.SpellInfoSaveData();
                        spellInfoSaveData.setSpellId(spellInfo.getId());
                        spellInfoSaveData.setSpellBackId(spellInfo.getSpellBack().getSpellBackId());
                        spellInfoSaveData.setDisabled(spellInfo.isDisabled());
                        return spellInfoSaveData;
                    }));
            saveDataMap.put(entry.getKey().name(), spellInfoSaveDataList);
        }
        spellsDeckSaveData.setInvestigatorSpellInfoMap(saveDataMap);
        return spellsDeckSaveData;
    }

    @Override
    public void load(SpellsDeckSaveDataRead saveData) {
        createDeck();

        for (Map.Entry<String, List<? extends SpellsDeckSaveDataRead.SpellInfoSaveDataRead>> entry : saveData.getInvestigatorSpellInfoMap().entrySet()) {
            for (SpellsDeckSaveDataRead.SpellInfoSaveDataRead ownedSpell : entry.getValue()) {
                SpellInfo spellInfo = Stream.findFirstOrException(deck,
                        ai -> ai.getId().equals(ownedSpell.getSpellId()) && ai.getSpellBack().getSpellBackId() == ownedSpell.getSpellBackId());
                InvestigatorId investigatorId = InvestigatorId.valueOf(entry.getKey());
                List<SpellInfo> existingSpells = getOrDefault(investigatorSpellsMap, investigatorId, new LinkedList<>());
                existingSpells.add(spellInfo);
                if (ownedSpell.isDisabled()) {
                    disable(spellInfo);
                }
                deck.remove(spellInfo);
                investigatorSpellsMap.put(investigatorId, existingSpells);
            }
        }
    }
}
