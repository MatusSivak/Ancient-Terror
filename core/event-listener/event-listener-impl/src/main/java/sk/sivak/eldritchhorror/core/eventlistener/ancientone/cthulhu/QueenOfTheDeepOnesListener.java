package sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth.VoiceOfAzathothListener;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;

import java.util.Arrays;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.SELECT_CARD_TO_GAIN;

public class QueenOfTheDeepOnesListener extends AbstractMysteryListener {

    private QueenOfTheDeepOnesListener.BeforeSelectCardToGainListener beforeSelectCardToGainListener;

    public QueenOfTheDeepOnesListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    @Override
    protected int getProgress() {
        MonsterInfo epicMonster = findHydra();
        if (epicMonster != null) {
            return epicMonster.getToughness() - epicMonster.getCurrentHealth();
        } else {
            // not present on the board means it is dead
            return ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 2;
        }
    }

    @Override
    public void register() {
        ServicePlatform.get().getGameService().hold();
        SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
        spawnMonsterData.setLocationId(mysteryCardInfo.getPinLocations().get(0));
        spawnMonsterData.setMonsterId(EpicMonsterId.HYDRA);
        ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
        ServicePlatform.get().getGameService().showCurrentMysteryCard(true, false);
        ServicePlatform.get().getGameService().release();

        beforeSelectCardToGainListener = new BeforeSelectCardToGainListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeSelectCardToGainListener, SELECT_CARD_TO_GAIN);
    }

    @Override
    public void justAddRedPins() {

    }

    @Override
    public void justRegisterListeners(int progress) {
        beforeSelectCardToGainListener = new BeforeSelectCardToGainListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeSelectCardToGainListener, SELECT_CARD_TO_GAIN);
    }

    @Override
    public void unregister() {

    }

    @Override
    public void advanceActiveMystery() {
        ServicePlatform.get().getService().hold();
        MonsterInfo hydra = findHydra();
        if (hydra != null) {
            ServicePlatform.get().getMonsterService().dealDamageToMonster(hydra, 2);
            ServicePlatform.get().getGameService().advanceCurrentMysteryCard(Math.min(hydra.getCurrentHealth(), 2));
        } else {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Can't advance active mystery.");
            ServicePlatform.get().getGameService().ask(question).subscribe();
        }
        ServicePlatform.get().getService().release();
    }

    private class BeforeSelectCardToGainListener extends EventListenerImpl<GainCardData> {

        @Override
        public void onNotify(GainCardData eventData) {
            if (!eventData.isArtifact()) {
                return;
            }

            if (!ServicePlatform.get().getArtifactsDeck().isInDeckOrDiscardPile(ArtifactId.SWORD_OF_YHA_TALLA)) {
                // someone has it
                return;
            }
            Question<Boolean> question = new Question<>();
            question.setOptions(Arrays.asList(
                    new Question.Option<>("No", false, 0x800000ff),
                    new Question.Option<>("Yes", true, 0x008000ff)
            ));
            question.setTitle("Gain 'Sword of Y'ha-Talla' Artifact instead?");
            question.displayCurrentMysteryCard();
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> onAnswer(answer, eventData));
        }

        private void onAnswer(Answer<Boolean, Object> answer, GainCardData eventData) {
            if (!answer.getResponseData()) {
                ServicePlatform.get().getService().convertTo(getDataClass(), () -> eventData);
                return;
            }
            ServicePlatform.get().getService().convertTo(getDataClass(), () -> new GainCardData(ArtifactId.SWORD_OF_YHA_TALLA));

        }

        @Override
        public Class<GainCardData> getDataClass() {
            return GainCardData.class;
        }
    }

    private MonsterInfo findHydra() {
        for (MonsterInfo monsterInfo : ServicePlatform.get().getMonsterCup().getMonsters()) {
            if (monsterInfo.getMonsterId() != EpicMonsterId.HYDRA) {
                continue;
            }
            return monsterInfo;
        }
        return null;
    }
}
