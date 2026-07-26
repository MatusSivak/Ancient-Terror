package sk.sivak.eldritchhorror.core.eventlistener.spell;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.fleshward.FleshWardSpellBackListener1;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.fleshward.FleshWardSpellBackListener2;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.fleshward.FleshWardSpellBackListener3;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.fleshward.FleshWardSpellBackListener4;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueRead;

import java.util.List;

public abstract class AbstractSpellListener<T extends SpellInfo> implements SpellListener {

    private static final Logger logger = LogManager.getLogger(AbstractSpellListener.class);

    protected InvestigatorId spellOwnerId;
    protected boolean enabled = true;
    protected T spellInfo;

    protected abstract List<EventListener> getEventListeners();

    public void register(SpellInfo spellInfo, InvestigatorId investigatorId) {
        this.spellInfo = (T) spellInfo;
        this.spellOwnerId = investigatorId;
        logger.info("Registering spell '" + spellInfo.getName() + "' for " + investigatorId);
        register();
    }

    protected EventQueueRead getEventQueue() {
        return ServicePlatform.get().getEventQueue();
    }

    protected abstract void register();

    public void unregister() {
        for (EventListener eventListener : getEventListeners()) {
            getEventQueue().unregisterListener(eventListener);
        }
        logger.info("Listeners for spell '" + spellInfo.getId() + "' and investigator '" + spellOwnerId + "' unregistered");
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }

    public void changeOwner(InvestigatorId investigatorId) {
        if (this.spellOwnerId == investigatorId) {
            return;
        }
        this.spellOwnerId = investigatorId;
        logger.info("Changing owner of spell: " + spellInfo.getName() + " to " + investigatorId);
    }

    public AbstractSpellBackListener findSpellBackListener() {
        String spellBackClassName = spellInfo.getId().getSpellBackClassName(spellInfo.getSpellBack().getSpellBackId());
        String spellBackListenerClassName = spellBackClassName
                .replace(
                        "sk.sivak.eldritchhorror.core.constants.spell",
                        "sk.sivak.eldritchhorror.core.eventlistener.back.spell")
                .replace(
                        "SpellBack",
                        "SpellBackListener");
        try {
            return (AbstractSpellBackListener) Class.forName(spellBackListenerClassName)
                    .getConstructor(SpellInfo.class).newInstance(spellInfo);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }
}
