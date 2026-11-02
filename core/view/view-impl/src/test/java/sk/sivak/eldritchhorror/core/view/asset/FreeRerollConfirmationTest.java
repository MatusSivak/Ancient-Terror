package sk.sivak.eldritchhorror.core.view.asset;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import static org.junit.Assert.*;

public class FreeRerollConfirmationTest {
    @Test public void onlyExplicitFreeYesNoReturningCardsMayAutoAccept() {
        ShowCardRequest request = new ShowCardRequest();
        request.setTitle("Reroll 1 die?");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
        assertFalse(request.isAutoAcceptFreeReroll());
        request.setAutoAcceptFreeReroll(true);
        assertTrue(request.isAutoAcceptFreeReroll());
        for (HideType hide : HideType.values()) {
            request.setHideType(hide);
            assertEquals(hide.toString(), hide == HideType.RETURN, request.isAutoAcceptFreeReroll());
        }
        request.setHideType(HideType.RETURN);
        request.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        assertFalse(request.isAutoAcceptFreeReroll());
    }

    @Test public void onlyExplicitAffirmativeQuestionOptionsMayAutoAccept() {
        assertFalse(new Question.Option<>("Yes", true).isAutoAcceptFreeReroll());
        assertTrue(new Question.Option<>("Localized yes", true).autoAcceptFreeReroll(true).isAutoAcceptFreeReroll());
        assertFalse(new Question.Option<>("No", false).autoAcceptFreeReroll(true).isAutoAcceptFreeReroll());
        assertFalse(new Question.Option<>("One", 1).autoAcceptFreeReroll(true).isAutoAcceptFreeReroll());
    }

    @Test public void manualAndAutomaticAnswersShareOneDecision() {
        boolean[] answered = {false};
        int[] calls = {0, 0};
        Runnable yes = AssetView.answerOnce(answered, () -> calls[0]++);
        Runnable no = AssetView.answerOnce(answered, () -> calls[1]++);
        no.run();
        yes.run();
        no.run();
        assertArrayEquals(new int[]{0, 1}, calls);
        answered = new boolean[]{false};
        yes = AssetView.answerOnce(answered, () -> calls[0]++);
        no = AssetView.answerOnce(answered, () -> calls[1]++);
        yes.run();
        yes.run();
        no.run();
        assertArrayEquals(new int[]{1, 1}, calls);
    }
}