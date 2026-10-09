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

/** Renders and exercises production widgets without loading saves or contacting Firestore. */
public class ReportFixesPreview extends ApplicationAdapter {
    private Stage stage;
    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width=960; config.height=540; config.x=-10000; config.y=-10000; config.forceExit=false;
        new LwjglApplication(new ReportFixesPreview(), config);
    }
    @Override public void create() {
        try {
            Sound silent = proxy(Sound.class, (p,m,a) -> m.getReturnType()==long.class ? 0L : null);
            Gdx.audio = proxy(Audio.class, (p,m,a) -> silent);
            VisUI.load(); stage=InfoStage.getStageSafe();
            skillChoices(); investigatorChoices(); ticketChoices(); mystery();
            System.out.println("PASS: skill restrictions/cancel, investigator cancel, ticket availability/single completion, mystery glyphs.");
        } catch(Throwable e) { e.printStackTrace(); System.exit(1); }
        finally { Gdx.app.exit(); }
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
            require(trainButton.isDisabled()!=train && !shipButton.isDisabled(),"Invalid ticket availability");
            require(calls[0]==0,"Ticket was selected automatically");
            draw(train?"tickets-both":"tickets-ship-only");
            if(!train) { click(trainButton); require(calls[0]==0,"Disabled ticket was accepted"); }
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
