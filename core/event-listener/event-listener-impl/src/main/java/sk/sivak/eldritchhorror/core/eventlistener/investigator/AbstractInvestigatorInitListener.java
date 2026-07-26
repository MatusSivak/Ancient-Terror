package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ConditionEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.DefeatedInvestigatorEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.service.Service;

/**
 * @author msivak
 */
public abstract class AbstractInvestigatorInitListener extends EventListenerImpl<InvestigatorId> {

    private static final Logger logger = LogManager.getLogger(AbstractInvestigatorInitListener.class);

    protected InvestigatorId eventData;
    private AddEncounterListener defeatedListener;

    @Override
    public Class<InvestigatorId> getDataClass() {
        return InvestigatorId.class;
    }

    protected abstract InvestigatorId getInvestigatorId();

    @Override
    public void onNotify(InvestigatorId eventData) {
        if (eventData != getInvestigatorId()) {
            return;
        }
        this.eventData = eventData;
        logger.info("Registering new investigator: " + eventData);
        initInvestigator();
    }

    public abstract void justRegisterListeners();

    protected abstract void initInvestigator();

    protected Service getService() {
        return ServicePlatform.get().getService();
    }

    public abstract void unregisterInvestigator();

    public void registerDefeatedListener(boolean health) {
        defeatedListener = new AddEncounterListener(health);
        ServicePlatform.get().getEventQueue().addDirectEventListener(defeatedListener, DirectEvent.COLLECT_COMMON_ENCOUNTERS);
    }

    public void unregisterDefeatedListener() {
        ServicePlatform.get().getEventQueue().unregisterListener(defeatedListener);
    }

    private class AddEncounterListener extends EventListenerImpl<AvailableEncounters> {

        private final boolean health;

        public AddEncounterListener(boolean health) {
            this.health = health;
        }

        @Override
        public void onNotify(AvailableEncounters eventData) {
            InvestigatorRead investigator = Stream.findFirstOrException(ServicePlatform.get().getInvestigators().getDefeatedInvestigators(),
                    in -> in.getInfo().getInvestigatorId() == getInvestigatorId());

            if (ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId() != investigator.getCurrentLocationId()) {
                return;
            }
            eventData.addEncounter(new DefeatedInvestigatorEncounter(getInvestigatorId(), health));
        }

        @Override
        public Class<AvailableEncounters> getDataClass() {
            return AvailableEncounters.class;
        }
    }
}