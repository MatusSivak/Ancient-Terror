package sk.sivak.eldritchhorror.core.view.question;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.components.select.SelectSingleComponent;
import sk.sivak.eldritchhorror.core.view.components.sheet.investigator.StatsTableData;
import sk.sivak.eldritchhorror.core.view.components.skill.ImproveSkillComponent;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.PURE_WHITE_BACKGROUND;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/** Typed questions use the same selectors as the normal investigator/skill flows. */
public final class QuestionChoiceView {
    private final GameController controller;
    public QuestionChoiceView(GameController controller) { this.controller = controller; }

    static Class<?> choiceType(Question<?> question) {
        if (question.getOptions() == null || question.getSelectComponentsTableData() != null
                || question.isDisplayCurrentMysteryCard() || question.getDisplayOngoingRumorCard() != null) return null;
        Class<?> type = null;
        int cancellations = 0;
        for (Question.Option<?> option : question.getOptions()) {
            Object value = option.getValue();
            if (value == null) { if (++cancellations > 1) return null; continue; }
            Class<?> current = value instanceof InvestigatorId ? InvestigatorId.class : value instanceof Stat ? Stat.class : null;
            if (current == null || (type != null && type != current)) return null;
            type = current;
        }
        return type;
    }

    public static boolean supports(Question<?> question) { return choiceType(question) != null; }

    @SuppressWarnings("unchecked")
    public <RD, AD> Single<Answer<RD, AD>> ask(Question<RD> question) {
        List<InvestigatorId> investigators = new ArrayList<>();
        Set<Stat> skills = EnumSet.noneOf(Stat.class);
        String cancel = null;
        for (Question.Option<RD> option : question.getOptions()) {
            if (option.getValue() instanceof InvestigatorId) investigators.add((InvestigatorId) option.getValue());
            else if (option.getValue() instanceof Stat) skills.add((Stat) option.getValue());
            else if (option.getValue() == null) cancel = option.getName();
        }
        final String cancelText = cancel;
        Single<?> selection;
        if (!investigators.isEmpty()) {
            selection = Single.<InvestigatorId>create(sub -> {
                SelectSingleComponent<InvestigatorId> picker = new SelectSingleComponent<>(investigators, sub);
                picker.init(get("investigator.displayInvestigators"), question.getTitle(),
                        alpha -> new Color(0, 0, 0, 0.5f * alpha), PURE_WHITE_BACKGROUND);
                picker.setCancelText(cancelText);
                picker.show();
            });
        } else {
            selection = Single.<Stat>create(sub -> {
                ImproveSkillComponent picker = new ImproveSkillComponent();
                picker.setOnSub(sub);
                picker.initChoice(new StatsTableData.Builder().fromInvestigatorBasics(controller.getInvestigatorBasics()).build(),
                        skills, question.getTitle(), cancelText);
            });
        }
        return selection.map(value -> {
            Answer<RD, AD> answer = new Answer<>();
            answer.setResponseData((RD) value);
            return answer;
        });
    }
}
