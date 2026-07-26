package sk.sivak.eldritchhorror.core.eventlistener.provider;

import java8.features.util.MapUtils;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.spell.AbstractSpellListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.ArcaneInsightListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.BanishmentListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.BlessingOfIsisListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.ClairvoyanceListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.ConjurationListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.FeedTheMindListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.FleshWardListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.HealingWordsListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.InstillBraveryListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.InterveneListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.MistsOfRelehListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.PlumbTheVoidListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.PoisonMistListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.ShrivelingListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.StormOfSpiritsListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.VoiceOfRaListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.WitherListener;

import java.util.HashMap;
import java.util.Map;

public class SpellListenerProviderImpl implements SpellListenerProvider {

    private Map<SpellInfo, AbstractSpellListener<? extends SpellInfo>> registeredListenersMap;

    @Override
    public void init() {
        registeredListenersMap = new HashMap<>();
    }

    @Override
    public AbstractSpellListener<? extends SpellInfo> getSpellListener(SpellInfo spellInfo) {
        AbstractSpellListener<? extends SpellInfo> registeredListener = MapUtils.getOrDefault(registeredListenersMap, spellInfo, null);
        if (registeredListener != null) {
            return registeredListener;
        }
        switch (spellInfo.getId()) {
            case VOICE_OF_RA:
                registeredListenersMap.put(spellInfo, new VoiceOfRaListener());
                break;
            case SHRIVELING:
                registeredListenersMap.put(spellInfo, new ShrivelingListener());
                break;
            case POISON_MIST:
                registeredListenersMap.put(spellInfo, new PoisonMistListener());
                break;
            case FEED_THE_MIND:
                registeredListenersMap.put(spellInfo, new FeedTheMindListener());
                break;
            case BANISHMENT:
                registeredListenersMap.put(spellInfo, new BanishmentListener());
                break;
            case ARCANE_INSIGHT:
                registeredListenersMap.put(spellInfo, new ArcaneInsightListener());
                break;
            case PLUMB_THE_VOID:
                registeredListenersMap.put(spellInfo, new PlumbTheVoidListener());
                break;
            case WITHER:
                registeredListenersMap.put(spellInfo, new WitherListener());
                break;
            case INTERVENE:
                registeredListenersMap.put(spellInfo, new InterveneListener());
                break;
            case HEALING_WORDS:
                registeredListenersMap.put(spellInfo, new HealingWordsListener());
                break;
            case FLESH_WARD:
                registeredListenersMap.put(spellInfo, new FleshWardListener());
                break;
            case INSTILL_BRAVERY:
                registeredListenersMap.put(spellInfo, new InstillBraveryListener());
                break;
            case STORM_OF_SPIRITS:
                registeredListenersMap.put(spellInfo, new StormOfSpiritsListener());
                break;
            case MISTS_OF_RELEH:
                registeredListenersMap.put(spellInfo, new MistsOfRelehListener());
                break;
            case CLAIRVOYANCE:
                registeredListenersMap.put(spellInfo, new ClairvoyanceListener());
                break;
            case BLESSING_OF_ISIS:
                registeredListenersMap.put(spellInfo, new BlessingOfIsisListener());
                break;
            case CONJURATION:
                registeredListenersMap.put(spellInfo, new ConjurationListener());
                break;
        }
        if (!registeredListenersMap.containsKey(spellInfo)) {
            throw new IllegalArgumentException("No key found for " + spellInfo.getId());
        }
        return registeredListenersMap.get(spellInfo);
    }

    @Override
    public void unregister(SpellInfo spellInfo) {
        registeredListenersMap.get(spellInfo).unregister();
    }
}
