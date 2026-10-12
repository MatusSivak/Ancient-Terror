package sk.sivak.eldritchhorror.core.view.question;

import org.junit.Test;
import rx.Single;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.view.action.ticket.TicketActionView;
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
        assertNull(QuestionChoiceView.choiceType(question(PathType.TRAIN, null)));
        assertNull(QuestionChoiceView.choiceType(question(PathType.TRAIN, Stat.LORE)));
        assertNull(QuestionChoiceView.choiceType(question(PathType.WALK)));
    }

    @Test public void butlerTicketQuestionUsesPickerAndWaitsForSelectedTicket() {
        for (PathType selected : new PathType[]{PathType.SHIP, PathType.TRAIN}) {
            Question<Object> question = question(PathType.SHIP, PathType.TRAIN);
            question.setTitle("Carson: gain one additional travel ticket");
            question.setPortraitBeforeTitle(InvestigatorId.THE_BUTLER);
            assertEquals(PathType.class, QuestionChoiceView.choiceType(question));
            CapturingTicketPicker picker = new CapturingTicketPicker();
            TestSubscriber<Answer<Object, Object>> subscriber = new TestSubscriber<>();
            new QuestionChoiceView(null, picker).ask(question).subscribe(subscriber);
            assertTrue(picker.trainAvailable);
            assertTrue(picker.shipAvailable);
            assertEquals(question.getTitle(), picker.title);
            subscriber.assertNoValues();
            subscriber.assertNotCompleted();
            picker.choice.onNext(selected);
            picker.choice.onCompleted();
            subscriber.assertNoErrors();
            subscriber.assertCompleted();
            assertEquals(1, subscriber.getOnNextEvents().size());
            assertEquals(selected, subscriber.getOnNextEvents().get(0).getResponseData());
        }
    }

    @Test public void ticketPickerReceivesOnlyAvailableOptions() {
        for (PathType available : new PathType[]{PathType.SHIP, PathType.TRAIN}) {
            Question<Object> question = question(available);
            assertTrue(QuestionChoiceView.supports(question));
            CapturingTicketPicker picker = new CapturingTicketPicker();
            new QuestionChoiceView(null, picker).ask(question).subscribe();
            assertEquals(available == PathType.TRAIN, picker.trainAvailable);
            assertEquals(available == PathType.SHIP, picker.shipAvailable);
        }
    }

    private static class CapturingTicketPicker extends TicketActionView {
        final PublishSubject<PathType> choice = PublishSubject.create();
        boolean trainAvailable, shipAvailable;
        String title;
        CapturingTicketPicker() { super(null); }
        @Override public Single<PathType> selectTravelTicket(boolean train, boolean ship, String title) {
            trainAvailable = train;
            shipAvailable = ship;
            this.title = title;
            return choice.toSingle();
        }
    }
    private Question<Object> question(Object... values) {
        Question<Object> result = new Question<>();
        for (Object value : values) result.addOption(new Question.Option<>(String.valueOf(value), value));
        return result;
    }
}
