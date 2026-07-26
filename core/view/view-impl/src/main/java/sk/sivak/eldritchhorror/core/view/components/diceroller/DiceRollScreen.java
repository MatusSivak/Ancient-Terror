package sk.sivak.eldritchhorror.core.view.components.diceroller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.view.components.lightning.Bolt;
import sk.sivak.eldritchhorror.core.view.components.lightning.BoltBuilder;
import sk.sivak.eldritchhorror.core.view.components.lightning.CompositeBolt;
import sk.sivak.eldritchhorror.core.view.components.lightning.CompositeBoltAnimation;
import sk.sivak.eldritchhorror.core.view.components.lightning.CompositeBoltAnimationActor;
import sk.sivak.eldritchhorror.core.view.components.lightning.CompositeBoltAnimationBuilder;
import sk.sivak.eldritchhorror.core.view.components.lightning.CompositeBoltBuilder;
import sk.sivak.eldritchhorror.core.view.components.lightning.Line;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;

import java.util.LinkedList;
import java.util.List;

public class DiceRollScreen implements Screen {

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private CompositeBoltAnimationActor compositeBoltAnimationActor;

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        LinkedList<CompositeBolt> compositeBolts = new LinkedList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            CompositeBolt compositeBolt = CompositeBoltBuilder.build(new Vector2(100, 100), new Vector2(560, 340), 15, Color.RED, 3);
            compositeBolts.add(compositeBolt);
        }
        compositeBoltAnimationActor = new CompositeBoltAnimationActor(CompositeBoltAnimationBuilder.build(compositeBolts));
        compositeBoltAnimationActor.setExplosionColor(Color.BROWN);
        System.out.println("Duration: " + (System.currentTimeMillis() - start));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f,0.2f,0.3f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        spriteBatch.begin();
        compositeBoltAnimationActor.act(delta);
        compositeBoltAnimationActor.draw(spriteBatch, 1f);
        spriteBatch.end();
        shapeRenderer.end();


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
