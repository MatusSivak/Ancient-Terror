package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.rumor.InitRumorCardData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DEFEAT_MONSTER;

public class BlueMythosCard1 implements MythosCardEventListener{

    private static final String RUMOR_CARD_ID = "TheWindWalker";
    private BlueMythosCard1ReckoningListener reckoningListener;
    private BeforeDefeatMonsterListener beforeDefeatMonsterListener;

    @Override
    public void execute() {
        reckoningListener = new BlueMythosCard1ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_RUMORS);

        beforeDefeatMonsterListener = new BeforeDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDefeatMonsterListener, DEFEAT_MONSTER);

        SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
        spawnMonsterData.setLocationId(LocationId.SPACE_4);
        spawnMonsterData.setMonsterId(EpicMonsterId.WIND_WALKER);
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);

        InitRumorCardData initRumorCardData = new InitRumorCardData();
        initRumorCardData.setRumorId(RUMOR_CARD_ID);
        ServicePlatform.get().getDoomOmenService().initRumorCard(initRumorCardData);

        ServicePlatform.get().getDoomOmenService().showRumorCard(RUMOR_CARD_ID);
        ServicePlatform.get().getService().release();
    }

    public void justLoad() {
        reckoningListener = new BlueMythosCard1ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_RUMORS);

        beforeDefeatMonsterListener = new BeforeDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDefeatMonsterListener, DEFEAT_MONSTER);
    }

    private class BlueMythosCard1ReckoningListener extends EventListenerImpl<ReckoningFireType> {

        @Override
        public void onNotify(ReckoningFireType eventData) {
            if (eventData == ReckoningFireType.CHARGE) {
                return;
            }
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
                ServicePlatform.get().getDoomOmenService().countdownRumorCard(RUMOR_CARD_ID, 1);
                if (ServicePlatform.get().getRumors().getActiveRumor(RUMOR_CARD_ID).getTimeRemaining() == 1) {
                    ServicePlatform.get().getDoomOmenService().highlightRumorFailure(RUMOR_CARD_ID);
                    triggerFailureEffect();
                    unregister();
                    ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
                }
                ServicePlatform.get().getService().convertTo(Object.class, () -> in);
                ServicePlatform.get().getService().release();
            });
        }

        private void triggerFailureEffect() {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getService().hold();
                for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
                    InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                    ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(investigatorId, true));
                    ServicePlatform.get().getTokenService().loseHealth(6);
                }
                ServicePlatform.get().getService().convertTo(Object.class, ()-> in);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<ReckoningFireType> getDataClass() {
            return ReckoningFireType.class;
        }
    }

    private class BeforeDefeatMonsterListener extends EventListenerImpl<DefeatMonsterData> {

        @Override
        public void onNotify(DefeatMonsterData eventData) {
            if (eventData.getMonsterInfo().getMonsterId() != EpicMonsterId.WIND_WALKER) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getDoomOmenService().highlightRumorObjective(RUMOR_CARD_ID);
            unregister();
            ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }
    }

    private void unregister() {
        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
            ServicePlatform.get().getEventQueue().unregisterListener(beforeDefeatMonsterListener);
        });
    }

}
