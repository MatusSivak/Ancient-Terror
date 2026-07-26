package sk.sivak.eldritchhorror.core.eventlistener.back.spell;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.spell.ResolvedSpellData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractSpellBackListener {

    private SpellInfo spellInfo;
    private TestData testData;
    private Map<String, Object> spellDataMap = new HashMap<>();

    public AbstractSpellBackListener(SpellInfo spellInfo) {
        this.spellInfo = spellInfo;
    }

    protected SpellInfo getSpellInfo() {
        return spellInfo;
    }

    private List<ConcreteSpellBackEffect> spellBackEffectList = new LinkedList<>();

    private Runnable mainSpellAction = () -> {};

    public void setMainSpellAction(Runnable mainSpellAction) {
        this.mainSpellAction = mainSpellAction;
    }

    protected void runMainSpellAction() {
        mainSpellAction.run();
    }

    public void executeWhole() {
        fillSpellBackEffectList(spellBackEffectList);
        ServicePlatform.get().getService().hold();
        if (spellBackEffectList.get(getSpellEffectId()).beforeActionIsExecutedAction != null) {
            spellBackEffectList.get(getSpellEffectId()).beforeActionIsExecutedAction.run();
        }
        if (!spellBackEffectList.get(getSpellEffectId()).isSupressMainSpell() && testData.isSuccessful()) {
            runMainSpellAction();
        }
        if (spellBackEffectList.get(getSpellEffectId()).getBeforePaperIsDisplayedAction() != null) {
            spellBackEffectList.get(getSpellEffectId()).getBeforePaperIsDisplayedAction().run();
        }
        ServicePlatform.get().getTestService().setTestFlavor(new TestFlavorRequest(TestFlavorType.SPELL, spellInfo));
        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader(spellInfo.getName());
        ServicePlatform.get().getEncounterService().typeFlavor(
                spellInfo.getSpellBack().getSpellEffect(testData.getScore()).getFlavorText());
        spellBackEffectList.get(getSpellEffectId()).getAction().run();
        ServicePlatform.get().getTestService().unsetTestFlavor();
        ServicePlatform.get().getService().fireSpellResolvedEvent(new ResolvedSpellData(getSpellInfo(), testData));
        ServicePlatform.get().getService().convertToNull();
        ServicePlatform.get().getService().release();
    }

    protected abstract void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList);

    protected InvestigatorId getActiveInvestigatorId() {
        return ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
    }

    private int getSpellEffectId() {
        return getSpellInfo().getSpellBack().getSpellEffect(testData.getScore()).getSpellEffectId();
    }

    public void setData(String key, Object value) {
        spellDataMap.put(key, value);
    }

    public <T> T getData(String key) {
        return (T) spellDataMap.get(key);
    }

    public void setTestData(TestData testData) {
        this.testData = testData;
    }

    protected TestData getTestData() {
        return testData;
    }

    protected static class ConcreteSpellBackEffect {
        private Runnable action;
        private Runnable beforePaperIsDisplayedAction = null;
        private Runnable beforeActionIsExecutedAction = null;
        private boolean supressMainSpell = false;

        public ConcreteSpellBackEffect(Runnable action) {
            this.action = action;
        }

        public ConcreteSpellBackEffect(Runnable action, boolean supressMainSpell) {
            this.action = action;
            this.supressMainSpell = supressMainSpell;
        }

        public ConcreteSpellBackEffect(Runnable action, Runnable beforePaperIsDisplayedAction) {
            this.action = action;
            this.beforePaperIsDisplayedAction = beforePaperIsDisplayedAction;
        }

        public ConcreteSpellBackEffect(Runnable action, Runnable beforePaperIsDisplayedAction, boolean supressMainSpell) {
            this.action = action;
            this.beforePaperIsDisplayedAction = beforePaperIsDisplayedAction;
            this.supressMainSpell = supressMainSpell;
        }

        private Runnable getAction() {
            return action;
        }

        private boolean isSupressMainSpell() {
            return supressMainSpell;
        }

        public Runnable getBeforePaperIsDisplayedAction() {
            return beforePaperIsDisplayedAction;
        }

        private Runnable getBeforeActionIsExecutedAction() {
            return beforeActionIsExecutedAction;
        }

        public ConcreteSpellBackEffect withBeforeActionIsExecutedAction(Runnable beforeActionIsExecutedAction) {
            this.beforeActionIsExecutedAction = beforeActionIsExecutedAction;
            return this;
        }
    }
}
