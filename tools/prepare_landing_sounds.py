"""Prepare downloaded Kenney CC0 impact recordings using our existing foley pipeline.

Requires numpy and soundfile. See assets/sounds/LANDING_AUDIO.md.
"""
from zipfile import ZipFile
import numpy as np
import soundfile as sf
import prepare_tabletop_sounds as foley

foley.SOURCE = foley.ROOT / "source_assets/sounds/landing"
foley.SOURCES = {
    "map_token_impact": "impactWood_light_001.ogg",
    "map_investigator_impact": "impactSoft_heavy_001.ogg",
    "map_gate_impact": "impactWood_heavy_000.ogg",
}

if __name__ == "__main__":
    with ZipFile(foley.SOURCE / "kenney_impact-sounds.zip") as archive:
        license_text = archive.read("License.txt").decode("utf-8")
        (foley.SOURCE / "License.txt").write_text(
            "\n".join(line.rstrip() for line in license_text.splitlines()).strip() + "\n",
            encoding="utf-8", newline="\n")
    foley.main()
    # Give the soft investigator footfall more presence without clipping the transient.
    path = foley.OUTPUT / "map_investigator_impact.wav"
    data, rate = sf.read(path)
    data = .92 * np.tanh(data * 2.6 / .92)
    sf.write(path, data, rate, subtype="PCM_16")
