# Ancient Terror dialog

An empty bronze-framed dialog with a dark jade header and charcoal content area.

- `dialog.9.png`: 514 x 386 PNG including the one-pixel Android/libGDX TexturePacker marker border. Artwork is 512 x 384.
- `dialog.png` and `dialog.atlas`: ready-to-load libGDX equivalent, with the markers removed and split/padding metadata stored in the atlas.
- Split insets (left, right, top, bottom): `80, 80, 100, 104`. Header and corner ornaments remain fixed while the center and straight rails stretch.
- Content padding (left, right, top, bottom): `80, 80, 108, 40`. Header text belongs in the title band above the content.

For an existing libGDX skin:

```java
TextureAtlas dialogAtlas = new TextureAtlas(
        Gdx.files.internal("skin/ancient-terror/dialog.atlas"));
NinePatch patch = dialogAtlas.createPatch("ancient-terror-dialog");
// Optional for the game's smaller UI: patch.scale(0.5f, 0.5f);
Window.WindowStyle style = new Window.WindowStyle(
        skin.get(Window.WindowStyle.class));
style.background = new NinePatchDrawable(patch);
Dialog dialog = new Dialog("Ancient Terror", style);
```

Keep the atlas alive while dialogs use it, then dispose it with the owning screen or asset manager. Load `dialog.atlas` for this example; do not load the marked `.9.png` directly as an ordinary texture. The in-game burger menu uses this background at half scale through `CustomAssetManager`; other dialogs retain their existing skin.

Artwork generated with built-in imagegen. Technical nine-patch borders, resizing and stretch previews produced with System.Drawing. Prompt is in `prompt.txt`.
