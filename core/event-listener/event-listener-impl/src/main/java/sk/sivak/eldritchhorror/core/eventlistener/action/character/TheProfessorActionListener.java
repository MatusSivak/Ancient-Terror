package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import java.util.ArrayList;
import java.util.List;

public class TheProfessorActionListener extends AbstractActionPhaseListener<TheProfessorActionListener.CharacterAction> {
    public TheProfessorActionListener() { name = "Tutor"; }
    @Override protected String getAdditionalInfo() { return null; }
    @Override protected String getGeneralDescription() { return getActiveInvestigator().getInfo().getActionText(); }
    @Override protected CharacterAction createAction() { return new CharacterAction(); }
    @Override protected boolean isVisible() { return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_PROFESSOR; }
    @Override protected boolean isDisabled() {
        disabledReason = "No investigator on your space can learn from you.";
        return students().isEmpty();
    }
    @Override protected boolean isNotRecommended() { return false; }
    @Override protected ActionButtonData.ActionButtonId getActionButtonId() { return ActionButtonData.ActionButtonId.INVESTIGATOR; }
    @Override protected String getTexturePath() { return "investigator/THE_PROFESSOR.png"; }

    private static int value(InvestigatorRead investigator, Stat stat) {
        return investigator.getInfo().getBaseStat(stat) + investigator.getStatBonus(stat);
    }

    private List<Stat> teachableSkills(InvestigatorRead student) {
        InvestigatorRead professor = getInvestigators().getInvestigator(InvestigatorId.THE_PROFESSOR);
        List<Stat> result = new ArrayList<>();
        for (Stat stat : Stat.values()) {
            if (student.getStatBonus(stat) < 2 && value(professor, stat) >= value(student, stat)) result.add(stat);
        }
        return result;
    }

    private List<InvestigatorId> students() {
        InvestigatorRead professor = getInvestigators().getInvestigator(InvestigatorId.THE_PROFESSOR);
        List<InvestigatorId> result = new ArrayList<>();
        for (InvestigatorRead investigator : getInvestigators().getOnBoardInvestigators()) {
            InvestigatorId id = investigator.getInfo().getInvestigatorId();
            if (id == InvestigatorId.THE_PROFESSOR || investigator.getCurrentLocationId() != professor.getCurrentLocationId()) continue;
            if (!teachableSkills(investigator).isEmpty()) result.add(id);
        }
        return result;
    }

    private static String statName(Stat stat) {
        String name = stat.name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    protected class CharacterAction extends AbstractActionPhaseAction {
        @Override public void execute() {
            InvestigatorRestriction restriction = new InvestigatorRestriction(true);
            restriction.addAllowedInvestigators(students());
            restriction.addDisabledInvestigator(InvestigatorId.THE_PROFESSOR);
            ServicePlatform.get().getInvestigatorService().selectInvestigator(restriction).subscribe(this::teach);
        }

        private void teach(InvestigatorId studentId) {
            InvestigatorRead student = getInvestigators().getInvestigator(studentId);
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(studentId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            Question<Stat> question = new Question<>();
            question.setTitle(student.getInfo().getInvestigatorName() + ": choose a skill to improve");
            question.setPortraitBeforeTitle(InvestigatorId.THE_PROFESSOR);
            List<Question.Option<Stat>> options = new ArrayList<>();
            for (Stat stat : teachableSkills(student)) options.add(new Question.Option<>(statName(stat), stat));
            question.setOptions(options);
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().improveSkill(answer.getResponseData());
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(InvestigatorId.THE_PROFESSOR);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        }
    }
}
