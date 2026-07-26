package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import sk.sivak.eldritchhorror.core.view.components.lightning.CompositeBolt;
import sk.sivak.eldritchhorror.core.view.components.lightning.CompositeBoltAnimationActor;
import sk.sivak.eldritchhorror.core.view.components.lightning.CompositeBoltAnimationBuilder;
import sk.sivak.eldritchhorror.core.view.components.lightning.CompositeBoltBuilder;

import java.util.LinkedList;
import java.util.List;

public class CombatEffects {

    public static List<CompositeBoltAnimationActor> createBoltAnimations(List<Vector2> sources, List<Vector2> destinations, Color color) {
        List<CompositeBoltAnimationActor> result = new LinkedList<>();
        for (int n = 0; n < sources.size(); n++) {
            LinkedList<CompositeBolt> compositeBolts = new LinkedList<>();
            for (int i = 0; i < 10; i++) {
                CompositeBolt compositeBolt = CompositeBoltBuilder.build(destinations.get(n),sources.get(n), 15, color, 3);
                compositeBolts.add(compositeBolt);
            }
            CompositeBoltAnimationActor compositeBoltAnimationActor = new CompositeBoltAnimationActor(CompositeBoltAnimationBuilder.build(compositeBolts));
            compositeBoltAnimationActor.setExplosionColor(color);
            result.add(compositeBoltAnimationActor);
        }
        return result;
    }
}
