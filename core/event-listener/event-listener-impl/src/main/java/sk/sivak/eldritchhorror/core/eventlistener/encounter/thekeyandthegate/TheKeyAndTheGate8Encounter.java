package sk.sivak.eldritchhorror.core.eventlistener.encounter.thekeyandthegate;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.darkpact.DarkPactCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TheKeyAndTheGateEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class TheKeyAndTheGate8Encounter extends AbstractTheKeyAndTheGateEncounter {

    public TheKeyAndTheGate8Encounter() {
        super(8);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = () -> {

            BeforeConfirmTestListener beforeConfirmTestListener = new BeforeConfirmTestListener();
            ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeConfirmTestListener, BeforeAfterEvent.CONFIRM_TEST);

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeInfo("Minimum score to pass is three.\n" +
                    "You may gain a Dark Pact Condition\nto roll three additional dice.");

            new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0, () -> {
                ServicePlatform.get().getGameService().advanceActiveMystery();
            }, () -> {
                ServicePlatform.get().getGameService().gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
            }).execute();

            ServicePlatform.get().getService().release();
        };

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo(1).build(),
                    getTextBuilder().withInfo(2).build(),
                    getTextBuilder().withInfo(3).build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                discardAllClues();
                discardAllSpells();
                discardImprovementTokens();
                ServicePlatform.get().getService().release();
            });
        };

        new TheKeyAndTheGateEncounterTemplate(getTextBuilder(), Stat.INFLUENCE, -1, passTemplate, failTemplate).execute();
    }

    private void discardAllClues() {
        int clueCount = ServicePlatform.get().getCluePool().getClueCount(getActiveInvestigatorId());
        if (clueCount == 0) {
            return;
        }
        for (int i = 0; i < clueCount; i++) {
            ServicePlatform.get().getTokenService().loseClue();
        }
    }

    private void discardAllSpells() {
        List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId());
        for (SpellInfo spell : spells) {
            EncounterUtils.onSelectCardToDiscard(spell);
        }
    }

    private void discardImprovementTokens() {
        List<Stat> improvedSkills = new LinkedList<>();
        for (Stat stat : Stat.values()) {
            if (ServicePlatform.get().getInvestigators().getActiveInvestigator().getStatBonus(stat) == 0) {
                continue;
            }
            improvedSkills.add(stat);
        }

        if (!improvedSkills.isEmpty()) {
            for (Stat improvedSkill : improvedSkills) {
                ServicePlatform.get().getInvestigatorService().loseImprovement(new LoseImprovementData(improvedSkill, 2));
            }
        }
    }

    private class BeforeConfirmTestListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(BeforeConfirmTestListener.this);
            });
            eventData.setMinScoreToBeSuccessful(3);
            eventData.setMinScoreToEndTest(3);

            if (!ServicePlatform.get().getConditionsDeck().canGetConditionId(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
                return;
            }

            ShowCardRequest showCardRequest = new ShowCardRequestBuilder(new DarkPactCondition())
                    .withDisplayAssetResponseType(DisplayAssetResponseType.YES_NO)
                    .withTitle("Gain a Dark Pact Condition?")
                    .withAssetHideType(HideType.INVENTORY_ON_YES)
                    .withAssetOriginType(AssetOriginType.CENTER).build();

            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(answer -> {
                if (answer == ShowCardResponse.YES) {
                    ServicePlatform.get().getService().hold();
                    eventData.setAdditionalDicesCount(3);
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT, false);
                    ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                    ServicePlatform.get().getService().release();
                } else {
                    ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                }

            });
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
