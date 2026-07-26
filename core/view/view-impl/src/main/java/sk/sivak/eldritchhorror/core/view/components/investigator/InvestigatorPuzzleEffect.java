package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureAsync;
import static sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage.BACKGROUND_SCALE;

public class InvestigatorPuzzleEffect {

    public static Single<List<TextureRegion>> prepareTextureRegions(InvestigatorImage investigatorImage) {
        return Single.create(onSub -> {
            List<TextureRegion> textureRegions = new ArrayList<>(49);
            SpriteBatch spriteBatch = new SpriteBatch();
            OrthographicCamera camera = new OrthographicCamera(investigatorImage.getWidth() * BACKGROUND_SCALE, investigatorImage.getHeight() * BACKGROUND_SCALE);

            FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) (762 * BACKGROUND_SCALE), (int) (853 * BACKGROUND_SCALE), false);

            Observable.range(0, 49)
                    .concatMap(i -> Observable.create(x -> {
                        Gdx.app.postRunnable(() -> {
                            buildTextureRegion(spriteBatch, camera, frameBuffer, investigatorImage, i).subscribe(tr -> {
                                textureRegions.add(tr);
                                x.onCompleted();
                            });
                        });
                    }))
                    .doOnCompleted(() -> {
                        Gdx.app.postRunnable(() -> {
                            frameBuffer.dispose();
                            spriteBatch.dispose();
                            onSub.onSuccess(textureRegions);
                        });
                    }).subscribe();
        });
    }

    public static Completable devourInvestigator(InvestigatorImage investigatorImage, List<TextureRegion> textureRegions, Group group) {
        PublishSubject<Object> publishSubject = PublishSubject.create();
        List<Image> puzzleImages = createPuzzleImages(investigatorImage,textureRegions, group);
        return destroyPuzzleImages(puzzleImages, publishSubject);
    }

    public static Completable destroyPuzzleImages(List<Image> puzzleImages, PublishSubject publishSubject) {
        for (int i = 0; i < puzzleImages.size(); i++) {
            puzzleImages.get(i).addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.alpha(1,i * 0.075f),
                    Actions.parallel(
                            Actions.scaleTo(0,0,1f, Interpolation.sineIn),
                            Actions.alpha(0,1f, Interpolation.sineIn)
                    ),
                    Actions.removeActor()
            )));
            if (i == puzzleImages.size()-1) {
                puzzleImages.get(i).addAction(new FastForwardAction<>(Actions.delay( + i * 0.075f + 1f, Actions.run(() -> {
                    /*
                    for (Image piece : puzzleImages) {
                        ((TextureRegionDrawable) piece.getDrawable()).getRegion().getTexture().dispose();
                    }
                    */
                    publishSubject.onCompleted();
                }))));
            }
        }
        return publishSubject.toCompletable();
    }

    public static Observable<TextureRegion> prepareTextureRegionsForDefeated() {
        List<Integer> integers = new LinkedList<>();
        for (int i = 0; i <= 48; i++) {
            integers.add(i);
        }
        Collections.shuffle(integers);

        return Observable.from(integers).concatMap(value -> Observable.create(onSub -> Gdx.app.postRunnable(() -> {
            buildTextureRegionSimple(value).subscribe(tr -> {
                onSub.onNext(tr);
                onSub.onCompleted();
            });
        })));
    }

    public static Completable defeatInvestigator(Observable<TextureRegion> textureRegionObservable,
                                  InvestigatorImage investigatorImage,
                                  boolean health) {
        PublishSubject<Object> publishSubject = PublishSubject.create();
        List<Image> puzzleImages = new LinkedList<>();
        textureRegionObservable.map(textureRegion -> createPuzzleImage(investigatorImage, textureRegion))
                .subscribe(puzzleImage -> {
                    puzzleImage.setTouchable(Touchable.disabled);
                    puzzleImages.add(puzzleImage);
                    investigatorImage.getParent().addActorAfter(investigatorImage, puzzleImage);
                    puzzleImage.setColor(health ?
                            new Color(1f, 0.25f,0.25f,0f) :
                            new Color(0.25f, 0.25f,1f,0f)
                    );
                    puzzleImage.addAction(Actions.sequence(
                            Actions.alpha(0.75f,1.5f),
                            Actions.alpha(0f,1.5f)
                    ));
                }, err -> {

                }, () -> {
                    float delay = 0.0f;
                    for (Image puzzleImage : puzzleImages) {
                        delay += 3/49f;
                        puzzleImage.addAction(Actions.after(Actions.delay(delay, Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                                Actions.alpha(0.5f,1.5f),
                                Actions.alpha(0f,1.5f)
                        )))));
                    }
                    investigatorImage.setPuzzleImages(puzzleImages);
                    investigatorImage.setDefeatedByHealth(health);
                    publishSubject.onCompleted();
                });
        return publishSubject.toCompletable();
    }

    private static List<Image> createPuzzleImages(InvestigatorImage investigatorImage, List<TextureRegion> textureRegions, Group group) {
        List<Image> puzzleImages = new LinkedList<>();
        for (TextureRegion textureRegion : textureRegions) {
            Image puzzlePiece = createPuzzleImage(investigatorImage, textureRegion);
            puzzleImages.add(puzzlePiece);
            group.addActor(puzzlePiece);
        }
        Collections.shuffle(puzzleImages);
        return puzzleImages;
    }

    private static Image createPuzzleImage(InvestigatorImage investigatorImage, TextureRegion textureRegion) {
        Image puzzlePiece = new Image(textureRegion);
        puzzlePiece.setSize(investigatorImage.getWidth() * BACKGROUND_SCALE, investigatorImage.getHeight() * BACKGROUND_SCALE);
        puzzlePiece.setOrigin(Align.center);
        puzzlePiece.setPosition(
                investigatorImage.getX() - (BACKGROUND_SCALE-1)*0.5f*investigatorImage.getWidth(),
                investigatorImage.getY() - (BACKGROUND_SCALE-1)*0.5f*investigatorImage.getHeight());
        return puzzlePiece;
    }


    private static Single<TextureRegion> buildTextureRegionSimple(int i) {
        return Single.create(onSub -> {
            getTextureAsync("investigator/puzzle/" + String.format(Locale.ENGLISH, "%03d", i) + ".png").subscribe(texture -> {
                onSub.onSuccess(new TextureRegion(texture));
            });
        });


    }

    private static Single<TextureRegion> buildTextureRegion(SpriteBatch spriteBatch,
                                                                  OrthographicCamera camera,
                                                                  FrameBuffer frameBuffer,
                                                                  InvestigatorImage investigatorImage, int i) {

        float investigatorImageWidth = investigatorImage.getWidth();
        float investigatorImageHeight = investigatorImage.getHeight();
        camera.position.x = investigatorImageWidth *BACKGROUND_SCALE / 2f;
        camera.position.y = investigatorImageHeight *BACKGROUND_SCALE / 2f;
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        Vector2 investigatorImagePosition = new Vector2(investigatorImage.getX(), investigatorImage.getY());

        return Single.create(onSub -> {
            getTextureAsync("investigator/puzzle/" + String.format(Locale.ENGLISH, "%03d", i) + ".png").subscribe(texture -> {

                frameBuffer.begin();
                Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


                spriteBatch.begin();
                investigatorImage.setPosition(
                        0.5f*investigatorImageWidth * (BACKGROUND_SCALE - 1),
                        0.5f*investigatorImageHeight * (BACKGROUND_SCALE - 1));
                investigatorImage.act(0);
                investigatorImage.draw(spriteBatch, 1f);

                spriteBatch.setColor(Color.WHITE);
                spriteBatch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);

                spriteBatch.draw(texture, 0, 0,
                        investigatorImageWidth * BACKGROUND_SCALE,
                        investigatorImageHeight * BACKGROUND_SCALE);
                spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                spriteBatch.end();


                TextureRegion textureRegion = new TextureRegion(ScreenUtils.getFrameBufferTexture(0,0, (int) (762 * BACKGROUND_SCALE), (int) (853* BACKGROUND_SCALE)));
                textureRegion.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                frameBuffer.end();

                investigatorImage.setPosition(investigatorImagePosition.x, investigatorImagePosition.y);
                onSub.onSuccess(textureRegion);
            });
        });


    }

}
