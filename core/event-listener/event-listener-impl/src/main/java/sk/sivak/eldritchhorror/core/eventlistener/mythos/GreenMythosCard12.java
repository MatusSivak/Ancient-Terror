package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.ArrayList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.GameProperties.MAX_FOCUS_TOKENS;

/**
 * Glimmer of Hope: each investigator may gain 1 Focus, spend 1 Focus to gain 1 Clue,
 * or spend 1 Clue to gain 1 Glamour Spell.
 */
public class GreenMythosCard12 implements MythosCardEventListener {

    private static final int GAIN_FOCUS = 1;
    private static final int FOCUS_FOR_CLUE = 2;
    private static final int CLUE_FOR_SPELL = 3;

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            List<Question.Option<Integer>> options = new ArrayList<>();
            if (investigator.getFocusTokens() < MAX_FOCUS_TOKENS) {
                options.add(new Question.Option<>("Gain 1 Focus", GAIN_FOCUS));
            }
            if (investigator.getFocusTokens() > 0) {
                options.add(new Question.Option<>("1 Focus → 1 Clue", FOCUS_FOR_CLUE));
            }
            if (ServicePlatform.get().getCluePool().getClueCount(investigatorId) > 0
                    && ServicePlatform.get().getSpellsDeck().canGetTrait(investigatorId, SpellTrait.GLAMOUR)) {
                options.add(new Question.Option<>("1 Clue → Glamour", CLUE_FOR_SPELL));
            }
            if (options.isEmpty()) {
                continue;
            }
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            Question<Integer> question = new Question<>();
            question.setTitle("Glimmer of Hope");
            question.setOptions(options);
            ServicePlatform.get().getGameService().ask(question).map(Answer::getResponseData).subscribe(option -> {
                ServicePlatform.get().getService().hold();
                switch (option) {
                    case GAIN_FOCUS:
                        ServicePlatform.get().getTokenService().gainFocus();
                        break;
                    case FOCUS_FOR_CLUE:
                        ServicePlatform.get().getTokenService().loseFocus();
                        ServicePlatform.get().getTokenService().gainClueFromPool();
                        break;
                    case CLUE_FOR_SPELL:
                        ServicePlatform.get().getTokenService().loseClue();
                        ServicePlatform.get().getGameService().gainSpell(SpellTrait.GLAMOUR);
                        break;
                    default:
                        break;
                }
                ServicePlatform.get().getService().release();
            });
        }
        ServicePlatform.get().getService().release();
    }
}
