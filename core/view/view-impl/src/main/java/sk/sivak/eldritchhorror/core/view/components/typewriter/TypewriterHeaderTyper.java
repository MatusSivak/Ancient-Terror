package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.graphics.Color;
import rx.Completable;

public class TypewriterHeaderTyper {

    private final TypewriterViewImpl typewriterView;

    public TypewriterHeaderTyper(TypewriterViewImpl typewriterView) {
        this.typewriterView = typewriterView;
    }

    public Completable typeHeader(String header) {
        return Completable.create(onSub -> {
            typewriterView.setFontColor(Color.BLACK);
            int length = header.length();
            StringBuilder dashes = new StringBuilder();
            for (int i = 0; i < length; i++) {
                dashes.append("_");
            }
            String newHeader = header + "\n" + dashes.toString() + "\n \n";
            typewriterView.prepareAndRepeatRuns(newHeader, onSub);
        });
    }
}
