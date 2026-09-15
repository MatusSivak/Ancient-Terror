"""Generate a small code-defined radial alpha mask for batched contact shadows."""
import math
from pathlib import Path
import struct
import zlib

size = 96
rows = bytearray()
for y in range(size):
    rows.append(0)
    for x in range(size):
        radius = math.hypot((x + 0.5 - size/2) / (size/2), (y + 0.5 - size/2) / (size/2))
        alpha = round(255 * max(0, 1 - radius * radius) ** 2)
        rows.extend((255, 255, 255, alpha))

def chunk(name, data):
    return struct.pack('>I', len(data)) + name + data + struct.pack('>I', zlib.crc32(name + data))

png = b'\x89PNG\r\n\x1a\n'
png += chunk(b'IHDR', struct.pack('>IIBBBBB', size, size, 8, 6, 0, 0, 0))
png += chunk(b'IDAT', zlib.compress(rows, 9)) + chunk(b'IEND', b'')
(Path(__file__).resolve().parents[1] / 'assets/dice_shadow.png').write_bytes(png)
