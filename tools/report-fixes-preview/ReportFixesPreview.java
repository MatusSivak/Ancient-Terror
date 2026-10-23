import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.kotcrab.vis.ui.VisUI;
import java.lang.reflect.*;
import java.lang.StringBuilder;
import java.util.*;
import java.util.List;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import sk.sivak.eldritchhorror.core.constants.*;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.question.*;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.action.ticket.TicketActionView;
import sk.sivak.eldritchhorror.core.view.components.YellowButton;
import sk.sivak.eldritchhorror.core.view.components.skill.ImproveSkillComponent;
import sk.sivak.eldritchhorror.core.view.components.sheet.mystery.MysteryCard;
import sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.question.QuestionChoiceView;
import sk.sivak.eldritchhorror.core.view.components.typewriter.TypewriterViewImpl;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.constants.asset.*;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.*;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.reserve.ReserveDragAndDropBinder;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Renders and exercises production widgets without loading saves or contacting Firestore. */
public class ReportFixesPreview extends ApplicationAdapter {
    private Stage stage;
    private static boolean latestOnly;
    private static boolean october15;
    private static boolean awakening;
    private static boolean october19;
    private static boolean crown;
    private static boolean october20;
    private static boolean nun;
    private static boolean october22;
    private static boolean october23;
    public static void main(String[] args) {
        latestOnly = Arrays.asList(args).contains("--latest");
        october15 = Arrays.asList(args).contains("--oct15");
        awakening = Arrays.asList(args).contains("--awakening");
        october19 = Arrays.asList(args).contains("--oct19");
        crown = Arrays.asList(args).contains("--crown");
        october20 = Arrays.asList(args).contains("--oct20");
        nun = Arrays.asList(args).contains("--nun");
        october22 = Arrays.asList(args).contains("--oct22");
        october23 = Arrays.asList(args).contains("--oct23");
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width=960; config.height=540; config.x=-10000; config.y=-10000; config.forceExit=false;
        new LwjglApplication(new ReportFixesPreview(), config);
    }
    @Override public void create() {
        try {
            Sound silent = proxy(Sound.class, (p,m,a) -> {
                if (m.getName().equals("hashCode")) return System.identityHashCode(p);
                if (m.getName().equals("equals")) return p == a[0];
                return m.getReturnType()==long.class ? 0L : null;
            });
            Gdx.audio = proxy(Audio.class, (p,m,a) -> silent);
            VisUI.load(); stage=InfoStage.getStageSafe();
            if (october23) { october23Reports(); return; }
            if (october22) { october22Reports(); return; }
            if (nun) { nunDiceBonus(); return; }
            if (october20) { october20Reports(); return; }
            if (crown) { crownResolution(); return; }
            if (october19) { october19Reports(); return; }
            if (awakening) { awakeningReports(); return; }
            if (october15) { october15Reports(); return; }
            if (latestOnly) { latestReports(); return; }
            skillChoices(); investigatorChoices(); ticketChoices(); mystery(); typewriter();
            System.out.println("PASS: skill restrictions/cancel, investigator cancel, ticket availability/single completion, mystery and typewriter glyphs.");
        } catch(Throwable e) { e.printStackTrace(); System.exit(1); }
        finally { Gdx.app.exit(); }
    }
    private void latestReports() throws Exception {
        hallOfFame(); collectionBack(); crashDialog(); crownClueCost();
        System.out.println("PASS: Hall of Fame clock/outcomes, collection Back, crash dialog, Crown clue cost");
    }

    private void october23Reports() throws Exception {
        new sk.sivak.eldritchhorror.core.view.game.HudButtons(proxy(GameController.class, (p,m,a) -> null)).init();
        String oldLanguage=sk.sivak.eldritchhorror.core.view.utils.UiText.getLanguage();
        for(String language:new String[]{"en","sk"}) {
            setPreviewLanguage(language);
            for(boolean priest:new boolean[]{true,false}) {
                Question<Object> notice=new Question<>();
                notice.setTitle(priest?"investigator.thePriest.boonNotice":"mystery.notEnoughClues");
                if(priest)notice.setPortraitBeforeTitle(InvestigatorId.THE_PRIEST);
                notice.setOptions(Question.Option.okOption);
                int[] done={0};
                new sk.sivak.eldritchhorror.core.view.question.QuestionView().ask(notice).subscribe(a -> done[0]++);
                tick(180);require(done[0]==0,"Notice must wait for OK");
                LabelTable prompt=find(LabelTable.class);
                for(Actor actor:all(prompt))if(actor instanceof Label) {
                    Label label=(Label)actor;label.validate();
                    for(com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun run:label.getGlyphLayout().runs)
                        require(Math.abs(run.y-label.getGlyphLayout().runs.first().y)<.01f,"Notice must fit one line: "+label.getText());
                    require(label.getPrefHeight()<=label.getHeight()+1,"Clipped notice");
                }
                draw("oct23-"+(priest?"priest":"insufficient-clues")+"-"+language);
                click(textButton("OK"));tick(180);require(done[0]==1,"Notice must complete exactly once");
            }
        }
        setPreviewLanguage(oldLanguage);
        cultistGroupMovement();
        System.out.println("PASS: one-line Priest and insufficient-Clue notices in both languages; grouped cultist movement, world wrapping and fast-forward");
    }

    private void cultistGroupMovement() throws Exception {
        sk.sivak.eldritchhorror.core.view.monster.MonsterViewImpl view=new sk.sivak.eldritchhorror.core.view.monster.MonsterViewImpl();
        Stage map=sk.sivak.eldritchhorror.core.view.game.MapStage.getStage();
        for(boolean fast:new boolean[]{false,true})for(boolean wrap:new boolean[]{false,true}) {
            if(fast)FastForwardAction.turnOn();else FastForwardAction.turnOff();
            sk.sivak.eldritchhorror.core.view.game.MapStage.clearIdActorMap();
            sk.sivak.eldritchhorror.core.constants.location.LocationId origin=wrap?
                    sk.sivak.eldritchhorror.core.constants.location.LocationId.ARKHAM:sk.sivak.eldritchhorror.core.constants.location.LocationId.ROME;
            sk.sivak.eldritchhorror.core.constants.location.LocationId destination=wrap?
                    sk.sivak.eldritchhorror.core.constants.location.LocationId.SYDNEY:sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_7;
            com.badlogic.gdx.math.Vector2 start=sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver.resolve(origin);
            com.badlogic.gdx.math.Vector2 end=sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver.resolve(destination);
            OrthographicCamera camera=sk.sivak.eldritchhorror.core.view.game.MapStage.getCamera();
            camera.position.set(start.x,start.y,0);camera.zoom=1;camera.update();
            List<sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo> group=new ArrayList<>();
            List<Actor> centers=new ArrayList<>();
            for(int index=0;index<3;index++) {
                sk.sivak.eldritchhorror.core.constants.monster.CultistMonster monster=new sk.sivak.eldritchhorror.core.constants.monster.CultistMonster();
                monster.setCurrentLocation(origin);if(index<2)group.add(monster);
                sk.sivak.eldritchhorror.core.view.map.monster.MonsterUtils.MonsterIdLayerResolver resolver=
                        new sk.sivak.eldritchhorror.core.view.map.monster.MonsterUtils.MonsterIdLayerResolver(monster);
                for(int copy=-1;copy<=1;copy++) {
                    Image actor=new Image(CustomAssetManager.getTexture("monster/Cultist.png"));actor.setSize(35,35);
                    actor.setPosition(start.x+copy*ViewProperties.MAP_WIDTH+index*40,start.y);
                    sk.sivak.eldritchhorror.core.view.game.MapStage.addToLayer(actor,resolver);
                    if(copy==0)centers.add(actor);
                }
            }
            int[] done={0};
            view.moveMonsters(group,destination).subscribe(() -> done[0]++);
            for(int frame=0;frame<1800 && done[0]==0;frame++) {
                map.act(1f/60);stage.act(1f/60);
                require(Math.abs(centers.get(1).getX()-centers.get(0).getX()-40)<.05f,"Cultists separated in transit");
                require(Math.abs(centers.get(1).getY()-centers.get(0).getY())<.05f,"Cultists moved at different speeds");
                int cameras=0;for(Actor actor:map.getActors())if(actor instanceof sk.sivak.eldritchhorror.core.view.map.CameraActor)cameras++;
                require(cameras<=1,"Group started multiple camera journeys");
            }
            require(done[0]==1,"Group movement did not finish");
            require(Math.abs(centers.get(0).getX()-end.x)<.05f && Math.abs(centers.get(0).getY()-end.y)<.05f,"Group completed before arrival");
            require(Math.abs(centers.get(2).getX()-start.x-80)<.05f && centers.get(2).getY()==start.y,"Unrelated monster moved");
            Gdx.gl.glClearColor(.08f,.12f,.1f,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);map.draw();
            Pixmap pixels=ScreenUtils.getFrameBufferPixmap(0,0,960,540);PixmapIO.PNG png=new PixmapIO.PNG();png.setFlipY(true);
            png.write(Gdx.files.local("../build/report-fixes-preview/oct23-cultists"+(wrap?"-wrap":"")+(fast?"-fast":"")+".png"),pixels);png.dispose();pixels.dispose();
        }
        sk.sivak.eldritchhorror.core.view.game.MapStage.clearIdActorMap();FastForwardAction.turnOff();
    }

    private void october22Reports() throws Exception {
        new sk.sivak.eldritchhorror.core.view.game.HudButtons(proxy(GameController.class, (p,m,a) -> null)).init();
        String oldLanguage = sk.sivak.eldritchhorror.core.view.utils.UiText.getLanguage();
        for (String language : new String[]{"en", "sk"}) {
            setPreviewLanguage(language);
            Question<Object> notice = new Question<>();
            notice.setTitle("investigator.thePriest.boonNotice");
            notice.setPortraitBeforeTitle(InvestigatorId.THE_PRIEST);
            notice.setOptions(Question.Option.okOption);
            int[] done = {0};
            new sk.sivak.eldritchhorror.core.view.question.QuestionView().ask(notice).subscribe(a -> done[0]++);
            tick(180);
            require(done[0] == 0, "Priest prompt completed without OK");
            LabelTable prompt = find(LabelTable.class);
            for (Actor actor : all(prompt)) if (actor instanceof Label) {
                Label label = (Label) actor; label.validate();
                require(label.getPrefHeight() <= label.getHeight() + 1, "Priest explanation clipped");
            }
            draw("oct22-priest-" + language);
            click(textButton("OK")); tick(180);
            require(done[0] == 1, "Priest OK did not complete once");

            for (boolean focus : new boolean[]{false, true}) for (int variant = 0; variant < 3; variant++) {
                String keyword = focus ? "Focus" : "Clue";
                String token = focus ? CustomAssetManager.FOCUS_TOKEN : CustomAssetManager.CLUE_TOKEN;
                Question<Object> question = null;
                if (variant > 0) {
                    question = new Question<>();
                    question.setTitle(variant == 1 ? "Reroll with " + keyword + " ?" : "Try again ?");
                    question.setPortraitBeforeTitle(InvestigatorId.THE_PRIEST);
                    question.addTextureName(token);
                }
                sk.sivak.eldritchhorror.core.view.test.TestViewImpl view = new sk.sivak.eldritchhorror.core.view.test.TestViewImpl();
                int[] answered = {0};
                (focus ? view.askRerollUsingFocus(question) : view.askRerollUsingClue(question)).subscribe(a -> answered[0]++);
                tick(120);
                LabelTable reroll = (LabelTable) field(view, "labelTable");
                int images = 0;
                for (Actor actor : all(reroll)) if (actor instanceof Image) images++;
                int glyphs = 0;
                for (char c : labels(reroll).toCharArray()) if (c == FontGlyphEnricher.getGlyph(keyword)) glyphs++;
                require(images + glyphs == (variant == 0 ? 1 : 2), "Duplicated/missing reroll token");
                require(labels(reroll).contains(" ?"), "Question mark touches token");
                if (variant == 0) draw("oct22-reroll-" + keyword + "-" + language);
                click(((Button[]) field(view, "buttons"))[0]); tick(120);
                require(answered[0] == 1, "Reroll answer did not complete once");
            }
        }
        setPreviewLanguage("en");
        reserveAffordability();
        preloadCard("card/condition/POISONED.jpg", null);
        CardTemplate poison = CardTemplate.buildCard(new sk.sivak.eldritchhorror.core.constants.condition.poisoned.PoisonedCondition());
        poison.setScale(.38f); poison.setPosition(250,30); stage.addActor(poison); tick(120);
        draw("oct22-poisoned"); poison.remove();
        setPreviewLanguage(oldLanguage);
        System.out.println("PASS: Priest notice/OK and single reroll tokens in both languages; reserve affordability through zoom, Debt and selection; Poisoned artwork");
    }

    private void preloadCard(String picture, Integer cost) {
        for (String texture : new String[]{picture, "card/card_description_background.png", "card/card_title_background.png",
                CustomAssetManager.NEW_CARD_TEMPLATE, "card/disabled.png", "card/card_type_asset.png",
                "card/card_type_condition.png", CustomAssetManager.PURE_WHITE_BACKGROUND}) CustomAssetManager.getTexture(texture);
        if (cost != null) CustomAssetManager.getTexture("card/asset_value_" + cost + ".png");
    }

    private void reserveAffordability() throws Exception {
        // Same reserve and one-success budget as report c2d47867.
        AssetInfo[] assets = {new HolyCrossAsset(), new HandcuffsAsset(), new SpiritDaggerAsset(), new LuckyCigaretteCaseAsset()};
        CardTemplate[] cards = new CardTemplate[assets.length];
        for (int i = 0; i < assets.length; i++) {
            preloadCard("card/asset/" + assets[i].getId().name() + ".jpg", assets[i].getCost());
            cards[i] = CardTemplate.buildCard(assets[i]);
            cards[i].setScale(.133f);
        }
        ReserveDragAndDropBinder binder = new ReserveDragAndDropBinder(1, stage);
        binder.init(cards); stage.addActor(binder.getSourceTargetGroup()); tick(120);
        Field dndField = DragAndDropBinder.class.getDeclaredField("customDragAndDrop"); dndField.setAccessible(true);
        CustomDragAndDrop dnd = (CustomDragAndDrop) dndField.get(binder);
        require(((List<?>) field(dnd, "enabledSources")).isEmpty(), "Unaffordable cards can be dragged");
        for (CardTemplate card : cards) checkShade(card, true);

        // Reset/drag effects clear the temporary tint. Affordability must remain visible.
        for (CardTemplate card : cards) card.setForegroundColor(new Color(0,0,0,0));
        tick(120);
        for (CardTemplate card : cards) checkShade(card, true);
        for (EventListener listener : cards[3].getListeners()) if (listener instanceof CardClickListener) {
            CardClickListener zoom = (CardClickListener) listener;
            InputEvent event = new InputEvent(); event.setTarget(cards[3].getForeground());
            zoom.clicked(event, 1, 1); tick(120); require(zoom.isZoomed(), "Card did not zoom");
            zoom.scaleDownIfZoomed(); tick(120); require(!zoom.isZoomed(), "Card did not close");
        }
        for (CardTemplate card : cards) checkShade(card, true);
        draw("oct22-reserve-one-success");
        binder.takeBankLoan(); tick(120);
        require(((List<?>) field(dnd, "enabledSources")).size() == 4, "Debt did not enable affordable cards");
        for (CardTemplate card : cards) checkShade(card, false);
        draw("oct22-reserve-after-debt");
        binder.getSourceTargetGroup().getTargetActor().addActor(cards[0]);
        binder.getReserveTargetActorChangedListener().onTargetChange(Collections.singletonList(cards[0])); tick(120);
        for (int i = 1; i < cards.length; i++) checkShade(cards[i], true);
        require(((List<?>) field(dnd, "enabledSources")).size() == 1, "Remaining reserve exceeds budget");
        draw("oct22-reserve-debt-selected");
        binder.getSourceTargetGroup().getSourceDockActor().addActor(cards[0]);
        binder.getReserveTargetActorChangedListener().onTargetChange(Collections.emptyList()); tick(120);
        for (CardTemplate card : cards) checkShade(card, false);
        binder.getSourceTargetGroup().remove();
    }

    private void checkShade(CardTemplate card, boolean shaded) {
        Actor shade = card.findActor("unavailable-shade");
        require(shade != null && Math.abs(shade.getColor().a - (shaded ? .66f : 0f)) < .001f,
                "Wrong affordability shade for " + card.getAssetInfo().getName());
    }

    private void nunDiceBonus() throws Exception {
        String oldLanguage = sk.sivak.eldritchhorror.core.view.utils.UiText.getLanguage();
        for (String language : new String[]{"en", "sk"}) for (boolean fast : new boolean[]{false, true}) {
            setPreviewLanguage(language);
            if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
            InfoStage.setBottomHeight(5);
            sk.sivak.eldritchhorror.core.view.test.TestViewImpl view =
                    new sk.sivak.eldritchhorror.core.view.test.TestViewImpl();
            List<sk.sivak.eldritchhorror.core.constants.test.DiceRoll> raw = new ArrayList<>();
            List<sk.sivak.eldritchhorror.core.constants.test.DiceRoll> adjusted = new ArrayList<>();
            int[] values = {2, 4, 6};
            for (int i = 0; i < values.length; i++) {
                sk.sivak.eldritchhorror.core.constants.test.DiceRoll die =
                        new sk.sivak.eldritchhorror.core.constants.test.DiceRoll();
                die.setDiceNr(i); die.setDiceValue(values[i]);
                die.setScore(values[i] >= 5 ? sk.sivak.eldritchhorror.core.constants.test.DiceRoll.Score.GOOD
                        : sk.sivak.eldritchhorror.core.constants.test.DiceRoll.Score.BAD);
                raw.add(die);
                sk.sivak.eldritchhorror.core.constants.test.DiceRoll result =
                        new sk.sivak.eldritchhorror.core.constants.test.DiceRoll();
                result.setDiceNr(i); result.setDiceValue(Math.min(6, values[i] + 1));
                result.setScore(result.getDiceValue() >= 5 ? sk.sivak.eldritchhorror.core.constants.test.DiceRoll.Score.GOOD
                        : sk.sivak.eldritchhorror.core.constants.test.DiceRoll.Score.BAD);
                adjusted.add(result);
            }
            int[] finished = {0};
            view.showRolledTestDices(raw).subscribe(() -> finished[0]++);
            tick(1200);
            require(finished[0] == 1, "Raw dice did not settle");
            float previousBottom = InfoStage.getBottomHeight();
            view.showNunDiceBonus(adjusted).subscribe(() -> finished[0]++);
            tick(600);
            require(finished[0] == 1, "Bonus completed without OK");
            require(labels(stage.getRoot()).contains("+1"), "Nun explanation missing");
            TextButton ok = textButton("OK");
            require(ok != null && ok.getStage() == stage, "Nun OK button missing");
            for (Actor actor : InfoStage.getDiceLayer().getChildren()) {
                if (actor instanceof sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage) {
                    sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage die =
                            (sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage) actor;
                    require(die.getDiceValue() == values[die.getDiceNumber()], "Bonus applied before OK");
                }
            }
            String suffix = language + (fast ? "-fast" : "");
            draw("nun-bonus-notice-" + suffix);
            click(ok); click(ok);
            tick(300);
            require(finished[0] == 2, "Bonus animation did not complete once");
            require(!labels(stage.getRoot()).contains("+1"), "Nun explanation was not removed");
            require(ok.getStage() == null, "Nun OK button was not removed");
            require(InfoStage.getBottomHeight() == previousBottom, "Nun prompt did not restore layout");
            for (Actor actor : InfoStage.getDiceLayer().getChildren()) {
                if (actor instanceof sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage) {
                    sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage die =
                            (sk.sivak.eldritchhorror.core.view.components.diceroller.DiceImage) actor;
                    require(die.getDiceValue() == adjusted.get(die.getDiceNumber()).getDiceValue(), "Adjusted face mismatch");
                    require(die.getScore() == adjusted.get(die.getDiceNumber()).getScore(), "Adjusted highlight mismatch");
                    require(Math.abs(die.getScaleX() - 1f) < .001f, "Bonus pulse did not finish");
                }
            }
            draw("nun-bonus-adjusted-" + suffix);
            InfoStage.getDiceLayer().clearChildren();
        }
        setPreviewLanguage(oldLanguage); FastForwardAction.turnOff();
        System.out.println("PASS: Nun raw dice wait for OK, adjusted faces/highlights and single completion at both speeds/languages");
    }

    private void october20Reports() throws Exception {
        new sk.sivak.eldritchhorror.core.view.game.HudButtons(
                proxy(GameController.class, (p,m,a) -> null)).init();
        String oldLanguage = sk.sivak.eldritchhorror.core.view.utils.UiText.getLanguage();
        for (String language : new String[]{"en", "sk"}) for (boolean fast : new boolean[]{false, true}) {
            setPreviewLanguage(language);
            if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
            MysteryCardInfo info = sk.sivak.eldritchhorror.core.model.util.YigMysteryDeck.create(2).stream()
                    .filter(c -> c.getMysteryCardId() == MysteryCardId.Yig.CROWN_OF_THE_SERPENT).findFirst().get();
            info.setProgressSupplier(info::getMysteryComplexity);
            new sk.sivak.eldritchhorror.core.view.game.GameViewImpl()
                    .showCurrentMysteryCard(info, false, 1, 3).subscribe();
            Question<Object> question = new Question<>();
            question.setTitle("Mystery was solved!");
            question.setOptions(Question.Option.okOption);
            question.displayCurrentMysteryCard();
            int[] completed = {0};
            new sk.sivak.eldritchhorror.core.view.question.QuestionView()
                    .ask(question).subscribe(answer -> completed[0]++);
            tick(180);
            MysteryCard card = sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager.getMysteryCard();
            sk.sivak.eldritchhorror.core.view.components.table.LabelTable prompt = null;
            Button ok = null;
            for (Actor actor : all(stage.getRoot())) {
                if (actor instanceof sk.sivak.eldritchhorror.core.view.components.table.LabelTable)
                    prompt = (sk.sivak.eldritchhorror.core.view.components.table.LabelTable) actor;
                if (actor instanceof TextButton && actor.getParent() == card.getParent()) ok = (Button) actor;
            }
            require(prompt != null && ok != null, "Solved-mystery controls missing");
            require(prompt.getColor().a > .99f && prompt.getParent().getColor().a > .99f, "Prompt hidden");
            require(card.getY() >= prompt.getTop() + 9f, "Mystery overlaps solved prompt");
            require(card.getTop() <= 540, "Mystery clipped above viewport");
            require(prompt.getY() >= ok.getTop(), "Solved prompt overlaps OK");
            draw("oct20-mystery-solved-" + language + (fast ? "-fast" : ""));
            click(ok); tick(180);
            require(completed[0] == 1, "Solved mystery must acknowledge exactly once");
            require(card.getParent() == null, "Acknowledged mystery remains open");

            String rules = language.equals("en")
                    ? "Spend 2 Clues and test Will. Gain 1 Train Ticket or 1 Ship Ticket."
                    : "Získaj 2 vlakové lístky alebo 1 lodný lístok.";
            sk.sivak.eldritchhorror.core.view.components.table.LabelTable glyphPrompt =
                    sk.sivak.eldritchhorror.core.view.components.table.LabelTable.createAndShowTable(0, rules);
            tick(60);
            String text = labels(glyphPrompt);
            require(text.indexOf(FontGlyphEnricher.getGlyph("Train Ticket")) >= 0
                    && text.indexOf(FontGlyphEnricher.getGlyph("Ship Ticket")) >= 0, "Prompt ticket glyphs missing");
            if (language.equals("en")) require(text.indexOf(FontGlyphEnricher.getGlyph("Will")) >= 0,
                    "Prompt skill glyph missing");
            for (Actor actor : all(glyphPrompt)) if (actor instanceof Label) {
                Label label = (Label) actor; label.validate();
                require(label.getPrefHeight() <= label.getHeight() + 1, "Prompt glyph text clipped");
                require(label.getStyle().fontColor.equals(Color.WHITE), "Prompt style tints icon artwork");
            }
            draw("oct20-prompt-glyphs-" + language + (fast ? "-fast" : ""));
            glyphPrompt.remove(); InfoStage.setBottomHeight(5);
        }
        setPreviewLanguage(oldLanguage); FastForwardAction.turnOff();
        System.out.println("PASS: solved-mystery prompt bounds/acknowledgement and gold prompt glyphs, both languages/speeds");
    }

    private void crownResolution() throws Exception {
        sk.sivak.eldritchhorror.core.constants.location.LocationId location =
                sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_19;
        InvestigatorBasics basics = proxy(InvestigatorBasics.class, (p,m,a) -> {
            switch (m.getName()) {
                case "getInvestigatorId": return InvestigatorId.THE_SPY;
                case "getLocationId": return location;
                case "getClues": return 2;
                case "getFocusTokens": return 1;
                case "getCurrentHealth": case "getMaxHealth": return 7;
                case "getCurrentSanity": case "getMaxSanity": return 5;
                case "getTickets": return Arrays.asList(PathType.TRAIN, PathType.SHIP);
                default: throw new AssertionError(m.getName());
            }
        });
        GameController controller = proxy(GameController.class, (p,m,a) -> {
            if (m.getName().equals("getInvestigatorBasics")) return basics;
            throw new AssertionError(m.getName());
        });
        new sk.sivak.eldritchhorror.core.view.game.HudButtons(controller).init();
        sk.sivak.eldritchhorror.core.view.investigator.InvestigatorView investigators =
                new sk.sivak.eldritchhorror.core.view.investigator.InvestigatorView(controller);
        sk.sivak.eldritchhorror.core.view.action.focus.TokenView tokens =
                new sk.sivak.eldritchhorror.core.view.action.focus.TokenView();
        String oldLanguage = sk.sivak.eldritchhorror.core.view.utils.UiText.getLanguage();
        for (String language : new String[]{"en", "sk"}) for (boolean fast : new boolean[]{false, true}) {
            setPreviewLanguage(language);
            if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
            InfoStage.getInvestigatorHud().getClueBar().setCurrentValue(0);
            InfoStage.getInvestigatorHud().getFocusBar().setCurrentValue(0);
            MysteryCardInfo mystery = sk.sivak.eldritchhorror.core.model.util.YigMysteryDeck.create(2).stream()
                    .filter(c -> c.getMysteryCardId() == MysteryCardId.Yig.CROWN_OF_THE_SERPENT).findFirst().get();
            int[] completed = {0};
            new sk.sivak.eldritchhorror.core.view.game.GameViewImpl()
                    .showCurrentMysteryCard(mystery, false, 1, 3).subscribe(() -> completed[0]++);
            tick(180);
            require(completed[0] == 0, "Mystery must wait for acknowledgement");
            MysteryCard card = sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager.getMysteryCard();
            require(card.getY() >= 0 && card.getTop() <= 540, "Crown card clipped");
            draw("oct19-crown-prompt-" + language + (fast ? "-fast" : ""));
            click(card); tick(180);
            require(completed[0] == 1, "Mystery dismissal must complete once");
            investigators.showActiveInvestigator(false, false).subscribe(() -> completed[0]++);
            for (int i=0; i<1800; i++) {
                stage.act(1f/60);
                sk.sivak.eldritchhorror.core.view.game.MapStage.getStage().act(1f/60);
            }
            require(completed[0] == 2, "Holder camera/HUD did not finish");
            require(InfoStage.getInvestigatorHud().getClueBar().getCurrentValue() == 2, "Holder Clues missing");
            require(InfoStage.getInvestigatorHud().getFocusBar().getCurrentValue() == 1, "Holder Focus missing");
            com.badlogic.gdx.math.Vector2 target = sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver.resolve(location);
            OrthographicCamera camera = sk.sivak.eldritchhorror.core.view.game.MapStage.getCamera();
            require(Math.abs(camera.position.x-target.x)<.01f && Math.abs(camera.position.y-target.y)<.01f,
                    "Camera did not focus the holder");
            draw("oct19-crown-holder-hud-" + language + (fast ? "-fast" : ""));
            for (int i=0; i<2; i++) { tokens.loseClue().subscribe(() -> completed[0]++); tick(600); }
            tokens.loseFocus().subscribe(() -> completed[0]++); tick(600);
            require(completed[0] == 5, "Token animations did not complete exactly once");
            require(InfoStage.getInvestigatorHud().getClueBar().getCurrentValue() == 0, "Clue payment mismatch");
            require(InfoStage.getInvestigatorHud().getFocusBar().getCurrentValue() == 0, "Focus payment mismatch");
        }
        setPreviewLanguage(oldLanguage); FastForwardAction.turnOff();
        System.out.println("PASS: Crown acknowledgement, holder camera/HUD and Clue/Focus animations in both languages and speeds");
    }

    private void october19Reports() throws Exception {
        cameraQueue();
        typewriter();
        String oldLanguage = sk.sivak.eldritchhorror.core.view.utils.UiText.getLanguage();
        for (String language : new String[]{"en", "sk"}) {
            setPreviewLanguage(language);
            String rules = language.equals("en") ? "Gain 2 Train Tickets or 1 Ship Ticket."
                    : "Získaj 2 vlakové lístky alebo 1 lodný lístok.";
            Table panel = new Table(); panel.setBounds(100, 170, 760, 200);
            Label.LabelStyle style = new Label.LabelStyle(
                    sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getBitmapFontNew(
                            sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4, 40), Color.WHITE);
            style.font.getData().markupEnabled = true;
            Label cardRules = new Label(sk.sivak.eldritchhorror.core.view.utils.MarkupText.markupWithKeywords(rules, "FFFFFF"), style);
            cardRules.setFontScale(.55f); panel.add(cardRules).growX(); panel.row();
            sk.sivak.eldritchhorror.core.view.components.sheet.investigator.SpecialTable abilities =
                    new sk.sivak.eldritchhorror.core.view.components.sheet.investigator.SpecialTable();
            abilities.init(new sk.sivak.eldritchhorror.core.view.components.sheet.investigator.SpecialTableData.Builder()
                    .action(rules).ability(rules).build());
            panel.add(abilities).growX(); stage.addActor(panel); tick(3);
            draw("oct19-ticket-rules-" + language);
            for (Actor actor : all(panel)) if (actor instanceof Label) {
                Label label = (Label) actor; label.validate();
                require(label.getPrefHeight() <= label.getHeight()+1, "Ticket text clipped: " + label.getText()
                        + " preferred=" + label.getPrefHeight() + " actual=" + label.getHeight());
            }
            String text = labels(panel);
            require(text.indexOf(FontGlyphEnricher.getGlyph("Train Ticket")) >= 0
                    && text.indexOf(FontGlyphEnricher.getGlyph("Ship Ticket")) >= 0, "Ticket glyphs missing");
            panel.remove();
        }
        setPreviewLanguage(oldLanguage);
        System.out.println("PASS: queued camera transitions, ticket rules/cards/abilities in English and Slovak");
    }

    private void cameraQueue() {
        Stage map = sk.sivak.eldritchhorror.core.view.game.MapStage.getStage();
        OrthographicCamera camera = sk.sivak.eldritchhorror.core.view.game.MapStage.getCamera();
        for (boolean fast : new boolean[]{false, true}) {
            if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
            camera.position.set(500, 300, 0); camera.zoom = 4;
            List<Integer> completed = new ArrayList<>();
            moveCamera(550, 310, 1, .8f, () -> {
                require(Math.abs(camera.position.x - 550) < .01f && Math.abs(camera.zoom - 1) < .01f,
                        "First callback sees stale camera"); completed.add(1);
            });
            map.act(.01f);
            float before = camera.zoom;
            com.badlogic.gdx.math.Vector2 target = new com.badlogic.gdx.math.Vector2(650, 330);
            rx.Completable.create(s -> sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper
                    .moveCameraToPosition(target, s, 2, .6f)).subscribe(() -> completed.add(2));
            target.set(900, 900); // Queued destinations must be copied.
            moveCamera(700, 350, 1.5f, .3f, () -> completed.add(3));
            require(camera.zoom == before, "Queueing a move changed zoom immediately");
            for (int i=0;i<300;i++) {
                int writers = 0;
                for (Actor actor : map.getActors()) if (actor instanceof sk.sivak.eldritchhorror.core.view.map.CameraActor) writers++;
                require(writers <= 1, "Overlapping camera writers");
                map.act(1f/60);
            }
            require(completed.equals(Arrays.asList(1,2,3)), "Camera completions missing, repeated or out of order: " + completed);
            require(Math.abs(camera.position.x - 700) < .01f && Math.abs(camera.position.y - 350) < .01f
                    && Math.abs(camera.zoom - 1.5f) < .01f, "Wrong final camera pose");
            // Exercise the natural two-phase zoom and a reentrant completion.
            completed.clear();
            moveCamera(ViewProperties.MAP_WIDTH - 10, 300, 1.5f, null, () -> {
                completed.add(1); moveCamera(20, 300, 1, 0f, () -> completed.add(2));
            });
            for (int i=0;i<1800;i++) map.act(1f/60);
            require(completed.equals(Arrays.asList(1,2)), "Reentrant camera move did not complete");
            require(Math.abs(camera.position.x-20)<.01f && Math.abs(camera.zoom-1)<.01f, "Wrapped/zero-duration camera failed");
        }
        FastForwardAction.turnOff();
    }

    private void moveCamera(float x, float y, float zoom, Float duration, Runnable completed) {
        rx.Completable.create(s -> sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper
                .moveCameraToPosition(new com.badlogic.gdx.math.Vector2(x,y), s, zoom, duration)).subscribe(completed::run);
    }

    private void awakeningReports() throws Exception {
        new sk.sivak.eldritchhorror.core.view.game.HudButtons(null);
        String language = sk.sivak.eldritchhorror.core.view.utils.UiText.getLanguage();
        for (String lang : new String[]{"en", "sk"}) {
            setPreviewLanguage(lang);
            for (String reason : new String[]{"mysteries", "midnight"}) {
                Question<Object> question = new Question<>();
                question.setTitle("ancientOne.yig.awakening");
                question.setPortraitBeforeTitle(sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId.YIG);
                question.setOptions(Question.Option.okOption);
                int[] done = {0};
                new sk.sivak.eldritchhorror.core.view.question.QuestionView().ask(question).subscribe(a -> done[0]++);
                tick(120);
                require(done[0] == 0, "Awakening must wait for OK");
                String notice = lang.equals("en") ? "Yig awakens!" : "Yig sa prebúdza!";
                require(labels(stage.getRoot()).contains(notice) && !labels(stage.getRoot()).contains("23:20"), "Awakening notice should contain only the announcement");
                for (Actor actor : all(stage.getRoot())) if (actor instanceof Label
                        && ((Label)actor).getText().toString().equals(notice)) {
                    com.badlogic.gdx.math.Vector2 bottom = actor.localToStageCoordinates(new com.badlogic.gdx.math.Vector2());
                    require(bottom.y > textButton("OK").getTop(), "Awakening message overlaps OK");
                }
                draw("awakening-confirm-" + reason + "-" + lang);
                click(textButton("OK")); tick(120);
                require(done[0] == 1, "Awakening acknowledgement failed");
            }
            sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo[] infos = {
                sk.sivak.eldritchhorror.core.model.util.AncientOneHelper.createAwakenYig(),
                sk.sivak.eldritchhorror.core.model.util.AncientOneHelper.createAwakenCthulhu(),
                sk.sivak.eldritchhorror.core.model.util.AncientOneHelper.createAwakenShubNiggurath(),
                sk.sivak.eldritchhorror.core.model.util.AncientOneHelper.createAwakenYogSothoth()
            };
            sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.AncientOneCard[] cards = {
                new sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.YigCard(),
                new sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.AwakenCthulhuCard(),
                new sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.AwakenShubNiggurathCard(),
                new sk.sivak.eldritchhorror.core.view.components.sheet.ancientone.AwakenYogSothothCard()
            };
            for(int i=0;i<cards.length;i++) {
                cards[i].init(infos[i]); Actor card=cards[i].getActor();
                card.setPosition(480-card.getWidth()/2,270-card.getHeight()/2); stage.addActor(card); tick(5);
                require(labels(card).contains(sk.sivak.eldritchhorror.core.view.utils.UiText.get("ancientOne.awakened")), "Missing awakened status");
                require(card.getY() >= 0 && card.getTop() <= 540, "Awakened sheet clipped");
                for (Actor actor : all(card)) if (actor instanceof Label) {
                    Label label = (Label) actor; label.validate();
                    require(label.getPrefHeight() <= label.getHeight()+1, "Awakened sheet text clipped: " + label.getText());
                }
                draw("awakened-"+infos[i].getAncientOneId()+"-"+lang); card.remove();
            }
            cards[0].init(sk.sivak.eldritchhorror.core.model.util.AncientOneHelper.createYig());
            require(!labels(cards[0].getActor()).contains(sk.sivak.eldritchhorror.core.view.utils.UiText.get("ancientOne.awakened")), "Sleeping Yig labeled awakened");
        }
        setPreviewLanguage(language);
        for (boolean fast : new boolean[]{false, true}) {
            if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
            for (int initial : new int[]{0, 1, 5, 8, 10}) {
                sk.sivak.eldritchhorror.core.view.components.track.DoomTrackWidget clock =
                        new sk.sivak.eldritchhorror.core.view.components.track.DoomTrackWidget();
                clock.setScale(.5f); clock.setPosition(330, 120); stage.addActor(clock);
                clock.updateDoom(initial); tick(1500); clock.updateDoom(8); tick(1500);
                require(clock.getCurrentDoom() == 8, "Awakening did not reset to 23:20");
                require(Math.abs(((Actor)field(clock,"minuteHand")).getRotation()-240) < .01f
                        && Math.abs(((Actor)field(clock,"hourHand")).getRotation()-20) < .01f,
                        "Clock hands do not show 23:20 after awakening");
                if(initial == 5) draw(fast ? "awakening-clock-fast" : "awakening-clock");
                clock.remove();
            }
        }
        FastForwardAction.turnOff();
        System.out.println("PASS: awakening confirmation waits for OK, translated notices, all four awakened sheets, and 23:20 clock at both speeds");
    }

    private void october15Reports() throws Exception {
        String language = sk.sivak.eldritchhorror.core.view.utils.UiText.getLanguage();
        for (String lang : new String[]{"en", "sk"}) {
            setPreviewLanguage(lang);
            sk.sivak.eldritchhorror.core.model.rumor.AbstractRumorCardInfo[] rumors = {
                new sk.sivak.eldritchhorror.core.model.rumor.GrowingMadnessRumorCard(),
                new sk.sivak.eldritchhorror.core.model.rumor.FracturedRealityRumorCard(),
                new sk.sivak.eldritchhorror.core.model.rumor.LostKnowledgeRumorCard(),
                new sk.sivak.eldritchhorror.core.model.rumor.StarsAlignedRumorCard(),
                new sk.sivak.eldritchhorror.core.model.rumor.TheWindWalkerRumorCard()
            };
            for (sk.sivak.eldritchhorror.core.model.rumor.AbstractRumorCardInfo info : rumors) {
                info.setCluesRequired(2);
                sk.sivak.eldritchhorror.core.view.components.sheet.rumor.RumorCard card =
                        new sk.sivak.eldritchhorror.core.view.components.sheet.rumor.RumorCard();
                card.init(info);
                card.setPosition(480-card.getWidth()/2,270-card.getHeight()/2);
                stage.addActor(card); tick(3);
                require(card.getY() >= 4 && card.getTop() <= 536, "Rumor clipped: " + info.getId() + " " + card.getHeight());
                for (Actor actor : all(card)) if (actor instanceof Label) {
                    Label label = (Label) actor; label.validate();
                    require(label.getPrefHeight() <= label.getHeight()+1, "Rumor text clipped: " + label.getText());
                    require(label.getGlyphLayout().width <= label.getWidth()+1, "Rumor text exceeds its column: " + label.getText());
                }
                draw("oct15-rumor-" + info.getId() + "-" + lang);
                if (info.getId().equals("GrowingMadness")) {
                    for (String method : new String[]{"highlightRumorObjective", "highlightRumorFailure", "highlightRumorReckoning"}) {
                        int[] done = {0};
                        ((rx.Completable)card.getClass().getMethod(method).invoke(card)).subscribe(() -> done[0]++);
                        tick(40); draw("oct15-rumor-" + method + "-" + lang);
                        clickOverlay(card); tick(60);
                        require(done[0] == 1, "Rumor highlight did not finish");
                    }
                    int[] done = {0};
                    card.countdown(1).subscribe(() -> done[0]++);
                    tick(40); clickOverlay(card); tick(120);
                    require(done[0] == 1, "Rumor countdown did not finish");
                    draw("oct15-rumor-countdown-" + lang);
                }
                card.remove();
            }
        }
        setPreviewLanguage(language);
        Table choices = new Table();
        for (InvestigatorId id : new InvestigatorId[]{InvestigatorId.THE_NUN, InvestigatorId.THE_PRIEST, InvestigatorId.THE_SOLDIER}) {
            sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData data =
                    new sk.sivak.eldritchhorror.core.eventtype.data.encounter.DefeatedInvestigatorEncounter(id, true).buildButtonData();
            choices.add(new sk.sivak.eldritchhorror.core.view.components.encounter.EncounterButton(data)).pad(8).row();
        }
        choices.pack(); choices.setPosition(480-choices.getWidth()/2,270-choices.getHeight()/2);
        stage.addActor(choices); tick(3); draw("oct15-defeated-portraits"); choices.remove();
        typewriter();
        System.out.println("PASS: rumor cards in EN/SK, highlights/countdown, defeated portraits and Reckoning glyphs at both speeds");
    }

    private void clickOverlay(Actor card) {
        for (Actor actor : stage.getActors()) if (actor instanceof Image && actor != card && actor.getListeners().size > 0) {
            click(actor); return;
        }
        throw new AssertionError("Missing rumor acknowledgement overlay");
    }

    private void hallOfFame() throws Exception {
        // Use the real screen and local records; no network calls or submissions.
        Net previousNet = Gdx.net;
        Gdx.net = proxy(Net.class, (p,m,a) -> null);
        sk.sivak.eldritchhorror.core.view.components.halloffame.HallOfFameScreen screen =
                new sk.sivak.eldritchhorror.core.view.components.halloffame.HallOfFameScreen();
        try { screen.show(); } finally { Gdx.net = previousNet; }
        stage = (Stage) field(screen, "stage");
        sk.sivak.eldritchhorror.core.constants.firebase.HallOfFameData record =
                new sk.sivak.eldritchhorror.core.constants.firebase.HallOfFameData();
        record.setName("Yep, Yig is coming"); record.setDoom(-4); record.setRounds(14);
        record.setAncientOneId(sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId.YIG);
        record.setInvestigatorsCount(2); record.setTimestamp(1791797274700L);
        record.setInvestigators(Arrays.asList("Nun", "Priest", "Butler", "Explorer", "Bounty Hunter", "Musician", "Ex-Convict", "Rookie Cop"));
        record.setSolvedMysteries(Arrays.asList("mystery.yig.KN_YAN_UNEARTHED.name", "mystery.yig.MIGRATION_OF_SERPENTS.name",
                "mystery.yig.SERPENTS_NEST.name", "mystery.yig.DESCENDANTS_OF_YIG.name"));
        Method populate = screen.getClass().getDeclaredMethod("populateRecords", List.class);
        populate.setAccessible(true); populate.invoke(screen, Collections.singletonList(record));
        tick(2); draw("hall-of-fame-legacy-yig");
        require(labels(stage.getRoot()).contains("23:40"), "Reported Yig clock is incorrect");
        require(labels(stage.getRoot()).contains("Survival details unavailable"), "Old record invented survival information");
        record.setName("Survival preview");
        record.setInvestigators(Arrays.asList("Nun", "Priest", "Butler", "Explorer"));
        record.setDefeatedInvestigators(Arrays.asList("Priest", "Butler"));
        populate.invoke(screen, Collections.singletonList(record));
        tick(2); draw("hall-of-fame-skulls");
        int skulls = 0;
        for (Actor actor : all(stage.getRoot())) if (actor.getName() != null && actor.getName().startsWith("defeated-")) skulls++;
        require(skulls == 2, "Must mark only the two recorded casualties");
        for (Actor actor : all(stage.getRoot())) if (actor instanceof Label && ((Label) actor).getText().toString().equals("Priest")) {
            Label name = (Label) actor; name.validate();
            require(name.getGlyphLayout().runs.size == 1, "Investigator name wrapped beside the skull");
        }
        screen.dispose();
    }

    private void collectionBack() throws Exception {
        sk.sivak.eldritchhorror.core.view.components.combat.CombatScreen screen =
                new sk.sivak.eldritchhorror.core.view.components.combat.CombatScreen();
        int[] exits = {0};
        screen.setChangeScreenHandler(proxy(sk.sivak.eldritchhorror.core.view.handler.ChangeScreenHandler.class,
                (p,m,a) -> { if (m.getName().equals("changeScreen")) exits[0]++; return null; }));
        screen.show(); screen.resize(960, 540);
        stage = (Stage) field(screen, "stage");
        TextButton back = textButton(sk.sivak.eldritchhorror.core.view.utils.UiText.get("combat.back"));
        click(back); screen.show(); click(back); screen.show();
        require(exits[0] == 2 && !back.isChecked(), "Back stayed checked or failed navigation on re-entry");
        require(textButton(sk.sivak.eldritchhorror.core.view.utils.UiText.get("combat.assets")).isChecked(),
                "The selected collection tab should remain checked");
        tick(2); draw("collection-back"); stage.dispose();
    }

    private void crashDialog() throws Exception {
        stage = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(960, 540));
        String language = sk.sivak.eldritchhorror.core.view.utils.UiText.getLanguage();
        for (String lang : new String[]{"en", "sk"}) {
            setPreviewLanguage(lang);
            int[] closed = {0};
            sk.sivak.eldritchhorror.core.view.game.CrashReportDialog dialog =
                    new sk.sivak.eldritchhorror.core.view.game.CrashReportDialog(() -> closed[0]++);
            dialog.show(stage); tick(30);
            require(dialog.getX() >= 0 && dialog.getY() >= 0 && dialog.getRight() <= 960 && dialog.getTop() <= 540,
                    "Crash dialog is clipped");
            draw("crash-dialog-" + lang);
            TextButton close = textButton(sk.sivak.eldritchhorror.core.view.utils.UiText.get("crash.close"));
            click(close); click(close); tick(30);
            require(closed[0] == 1 && !close.isChecked(), "Crash handler must be called exactly once");
            require(dialog.getStage() == null, "Crash dialog did not close");
        }
        setPreviewLanguage(language);
    }

    private void crownClueCost() throws Exception {
        for (int credit : new int[]{0, 1, 2}) {
            MysteryCardInfo info = null;
            for (MysteryCardInfo card : sk.sivak.eldritchhorror.core.model.util.YigMysteryDeck.create(2)) {
                if (card.getMysteryCardId() == MysteryCardId.Yig.CROWN_OF_THE_SERPENT) info = card;
            }
            info.setClueCredit(credit);
            MysteryCard card = new MysteryCard(); card.init(info, 2, 3);
            card.setPosition(480-card.getWidth()/2, 270-card.getHeight()/2); stage.addActor(card);
            tick(2);
            String text = labels(card);
            require(text.contains("spend " + (2-credit)) && !text.contains("per investigator") && !text.contains("CLUE_COST"),
                    "Crown must show its actual current clue cost");
            require(card.getY() >= 0 && card.getTop() <= 540, "Crown card clipped");
            draw("crown-cost-" + (2-credit)); card.remove();
        }
        stage.dispose();
    }

    private String labels(Actor actor) {
        StringBuilder text = new StringBuilder();
        for (Actor child : all(actor)) if (child instanceof Label) text.append(((Label) child).getText()).append('\n');
        return text.toString();
    }

    private void setPreviewLanguage(String language) {
        Application app = Gdx.app;
        try { Gdx.app = null; sk.sivak.eldritchhorror.core.view.utils.UiText.setLanguage(language); }
        finally { Gdx.app = app; }
    }
    private void skillChoices() throws Exception {
        InvestigatorBasics basics = proxy(InvestigatorBasics.class, (p,m,a) -> m.getName().equals("getBonusStat") ? 2 : 3);
        GameController controller = proxy(GameController.class, (p,m,a) -> basics);
        Question<Stat> q=new Question<>(); q.setTitle("Spend an improvement token to let an investigator discard Cursed?");
        q.addOption(new Question.Option<>("Lore",Stat.LORE));
        q.addOption(new Question.Option<>("Will",Stat.WILL));
        q.addOption(new Question.Option<>("Do not spend an improvement; gain Detained",null));
        int[] calls={0}; Stat[] selected={null};
        new QuestionChoiceView(controller).ask(q).subscribe(a -> { calls[0]++; selected[0]=a.getResponseData(); });
        tick(100);
        ImproveSkillComponent component=find(ImproveSkillComponent.class);
        List<ImageButton> buttons=(List<ImageButton>)field(component,"imageButtons");
        require(buttons.size()==2 && buttons.get(0).isVisible(),"Only eligible skills, including +2 skills when spending, must be selectable");
        draw("skill-spend-choice"); click(buttons.get(0)); tick(100);
        require(calls[0]==1 && selected[0]==Stat.LORE,"Selected skill was not delivered exactly once");
        q.setTitle("Choose a skill to improve"); q.setOptions(new ArrayList<>(Arrays.asList(new Question.Option<>("Will",Stat.WILL))));
        new QuestionChoiceView(controller).ask(q).subscribe(a -> calls[0]++); tick(100);
        component=find(ImproveSkillComponent.class); buttons=(List<ImageButton>)field(component,"imageButtons");
        require(buttons.size()==1,"Excluded skills must not be offered"); draw("skill-improve-choice");
        click(buttons.get(0)); tick(100);
        q.addOption(new Question.Option<>("Done",null));
        new QuestionChoiceView(controller).ask(q).subscribe(a -> { require(a.getResponseData()==null,"Cancel changed response"); calls[0]++; });
        tick(100); click(textButton("Done")); tick(100); require(calls[0]==3,"Skill cancellation did not complete");
    }
    private void investigatorChoices() throws Exception {
        Question<InvestigatorId> q=new Question<>(); q.setTitle("Discard a Madness Condition (2 remaining)");
        q.addOption(new Question.Option<>("Sister Mary",InvestigatorId.THE_NUN));
        q.addOption(new Question.Option<>("Father Mateo",InvestigatorId.THE_PRIEST));
        q.addOption(new Question.Option<>("Done",null));
        int[] calls={0};
        new QuestionChoiceView(null).ask(q).subscribe(a -> { require(a.getResponseData()==null,"Done must preserve null response"); calls[0]++; });
        tick(100); draw("investigator-choice"); click(textButton("Done")); tick(100);
        require(calls[0]==1,"Investigator cancellation did not complete");
    }
    private void ticketChoices() throws Exception {
        for(boolean train:new boolean[]{false,true}) {
            TicketActionView view=new TicketActionView(null); int[] calls={0};
            view.selectTravelTicket(train,true).subscribe(ticket -> { require(ticket==PathType.SHIP,"Wrong ticket"); calls[0]++; });
            tick(100);
            YellowButton trainButton=(YellowButton)field(view,"trainButton"), shipButton=(YellowButton)field(view,"shipButton");
            require((train ? trainButton != null && !trainButton.isDisabled() : trainButton == null)
                    && !shipButton.isDisabled(),"Invalid ticket availability");
            require(calls[0]==0,"Ticket was selected automatically");
            draw(train?"tickets-both":"tickets-ship-only");
            click(shipButton); click(shipButton); tick(100); require(calls[0]==1,"Ticket selection completed more than once");
        }
    }
    private void mystery() throws Exception {
        MysteryCardInfo info=proxy(MysteryCardInfo.class,(p,m,a)->{
            switch(m.getName()) {
                case "getName": return "mystery.yig.RISE_OF_THE_SERPENT_PEOPLE.name";
                case "getFlavorText": return "mystery.yig.RISE_OF_THE_SERPENT_PEOPLE.flavor";
                case "getMysteryText": return "mystery.yig.RISE_OF_THE_SERPENT_PEOPLE.text";
                case "getMysteryComplexity": return 2;
                case "getProgress": case "getClueCredit": return 0;
                default: return null;
            }
        });
        MysteryCard card=new MysteryCard(); card.init(info,1,3); card.setPosition(480-card.getWidth()/2,270-card.getHeight()/2); stage.addActor(card);
        tick(5); boolean found=false;
        for(Actor actor:all(card)) if(actor instanceof Label && ((Label)actor).getText().toString().contains(String.valueOf(FontGlyphEnricher.getGlyph("Observation")))) found=true;
        require(found,"Mystery is missing the Observation glyph"); draw("mystery-glyphs"); card.remove();
    }
    private <T extends Actor> T find(Class<T> type) { for(Actor a:all(stage.getRoot())) if(type.isInstance(a)) return type.cast(a); throw new AssertionError(type); }
    private void typewriter() throws Exception {
        for (boolean fast : new boolean[]{false, true}) {
            if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
            TypewriterViewImpl view = new TypewriterViewImpl();
            // Mount the production paper without the surrounding game HUD/board.
            Method createTable = TypewriterViewImpl.class.getDeclaredMethod("createTable");
            createTable.setAccessible(true); createTable.invoke(view);
            Table paper = view.getTable();
            paper.setPosition(960 - paper.getWidth() - 40, 270 - paper.getHeight());
            stage.addActor(paper);
            int[] completed = {0};
            view.typeInfo("Each investigator tests Observation-1 or Will+2. [#BAD]Lose 1 Health and 2 Sanity[].")
                    .andThen(view.typeInfo("Test Lore, Influence or Strength. Roll 1 die. On a 1 or 2, gain 1 Clue."))
                    .andThen(view.typeInfo("[#BAD]Reckoning![] Resolve reckoning effects."))
                    .andThen(view.typeInfo("[#GOOD]Gain one Ship Ticket[] or 2 train tickets."))
                    .subscribe(() -> completed[0]++);
            tick(1800);
            require(completed[0] == 1, "Typewriter rules did not finish exactly once");
            StringBuilder rendered = new StringBuilder();
            for (Actor actor : all(view.getTable())) if (actor instanceof Label) {
                Label label = (Label) actor;
                rendered.append(label.getText());
                label.validate();
                for (com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun run : label.getGlyphLayout().runs)
                    require(run.y == 0, "Typewriter line wrapped into the next row");
            }
            for (String word : new String[]{"Observation", "Will", "Lore", "Influence", "Strength", "Health", "Sanity", "Clue", "RECKONING", "Ship Ticket", "Train Ticket"})
                require(rendered.indexOf(String.valueOf(FontGlyphEnricher.getGlyph(word))) >= 0, "Missing typewriter glyph: " + word);
            require(rendered.indexOf(String.valueOf(FontGlyphEnricher.getDiceGlyph(1))) >= 0, "Missing die-result glyph");
            draw(fast ? "typewriter-glyphs-fast" : "typewriter-glyphs");
            paper.remove();
        }
        FastForwardAction.turnOff();
    }
    private TextButton textButton(String text) { for(Actor a:all(stage.getRoot())) if(a instanceof TextButton && ((TextButton)a).getText().toString().equals(text)) return (TextButton)a; throw new AssertionError(text); }
    private List<Actor> all(Actor root) { List<Actor> result=new ArrayList<>(); result.add(root); if(root instanceof Group) for(Actor child:((Group)root).getChildren()) result.addAll(all(child)); return result; }
    private void click(Actor actor) { for(EventListener l:actor.getListeners()) if(l instanceof ClickListener) ((ClickListener)l).clicked(new InputEvent(),1,1); }
    private Object field(Object target,String name)throws Exception { Field f=target.getClass().getDeclaredField(name); f.setAccessible(true); return f.get(target); }
    private void tick(int frames) { for(int i=0;i<frames;i++) stage.act(1f/60); }
    private static void require(boolean ok,String message) { if(!ok)throw new AssertionError(message); }
    private static <T>T proxy(Class<T> type,InvocationHandler handler) { return type.cast(Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},handler)); }
    private void draw(String name)throws Exception {
        Gdx.gl.glClearColor(.08f,.12f,.1f,1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels=ScreenUtils.getFrameBufferPixmap(0,0,960,540); PixmapIO.PNG png=new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/report-fixes-preview/"+name+".png"),pixels); png.dispose(); pixels.dispose();
    }
}
