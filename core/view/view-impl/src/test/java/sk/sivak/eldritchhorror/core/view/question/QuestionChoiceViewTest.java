package sk.sivak.eldritchhorror.core.view.question;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import static org.junit.Assert.*;

public class QuestionChoiceViewTest {
    @Test public void skillAndInvestigatorChoicesUseSelectorsIncludingOptionalChoices() {
        assertEquals(Stat.class, QuestionChoiceView.choiceType(question(Stat.LORE, Stat.WILL)));
        assertEquals(Stat.class, QuestionChoiceView.choiceType(question(Stat.OBSERVATION, null)));
        assertEquals(InvestigatorId.class, QuestionChoiceView.choiceType(question(InvestigatorId.THE_NUN, InvestigatorId.THE_PRIEST, null)));
    }
    @Test public void ordinaryAndMixedQuestionsKeepTheirOriginalPresentation() {
        assertNull(QuestionChoiceView.choiceType(question(true, false)));
        assertNull(QuestionChoiceView.choiceType(question((Object) null)));
        assertNull(QuestionChoiceView.choiceType(question(Stat.LORE, "Done")));
        assertNull(QuestionChoiceView.choiceType(question(Stat.LORE, InvestigatorId.THE_NUN)));
        assertNull(QuestionChoiceView.choiceType(question(Stat.LORE, null, null)));
    }
    private Question<Object> question(Object... values) {
        Question<Object> result = new Question<>();
        for (Object value : values) result.addOption(new Question.Option<>(String.valueOf(value), value));
        return result;
    }
}
