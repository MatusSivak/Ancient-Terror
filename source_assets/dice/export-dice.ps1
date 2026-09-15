# Convert the generated chroma-key source into the game's transparent 16 x 9 atlas.
param([string]$OutputPath = (Join-Path $PSScriptRoot '../../assets/dice_sheet.png'))
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$drawingReferences = @([Drawing.Bitmap].Assembly.Location, [Drawing.Color].Assembly.Location)
foreach ($drawingDependency in @('System.Private.Windows.GdiPlus.dll', 'System.Private.Windows.Core.dll')) {
    $drawingDependencyPath = Join-Path $PSHOME $drawingDependency
    if (Test-Path $drawingDependencyPath) { $drawingReferences += $drawingDependencyPath }
}
Add-Type -ReferencedAssemblies $drawingReferences -TypeDefinition @'
using System;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;

public static class DiceAtlasExport {
    public static void Run(string sourcePath, string outputPath) {
        using (var source = new Bitmap(sourcePath))
        using (var transparent = new Bitmap(source.Width, source.Height, PixelFormat.Format32bppArgb))
        using (var output = new Bitmap(1472, 828, PixelFormat.Format32bppArgb)) {
            for (int y = 0; y < source.Height; y++) {
                for (int x = 0; x < source.Width; x++) {
                    Color pixel = source.GetPixel(x, y);
                    // The generated key has small color variations and dark edge spill.
                    // Reject chromatic magenta before recovering the remaining edge pixels.
                    if (Math.Min(pixel.R, pixel.B) - pixel.G > 35) continue;
                    // Ivory and charcoal contain no magenta. Recover edge coverage and
                    // unmix the key color to avoid pink fringes on translucent pixels.
                    double alpha = Math.Min(1.0, 1.0 - (Math.Min(pixel.R, pixel.B) - pixel.G) / 255.0);
                    if (alpha < 0.06) continue;
                    int red = Clamp((pixel.R - 255 * (1 - alpha)) / alpha);
                    int green = Clamp(pixel.G / alpha);
                    int blue = Clamp((pixel.B - 255 * (1 - alpha)) / alpha);
                    transparent.SetPixel(x, y, Color.FromArgb(Clamp(alpha * 255), red, green, blue));
                }
            }
            using (Graphics graphics = Graphics.FromImage(output)) {
                graphics.CompositingMode = CompositingMode.SourceCopy;
                graphics.InterpolationMode = InterpolationMode.HighQualityBicubic;
                graphics.PixelOffsetMode = PixelOffsetMode.HighQuality;
                for (int row = 0; row < 9; row++) {
                    for (int column = 0; column < 16; column++) {
                        if ((row == 0 || row == 8) && column != 0) continue;
                        // Keep padding inside every cell for linear texture filtering.
                        graphics.DrawImage(transparent,
                            new RectangleF(column * 92 + 2, row * 92 + 2, 88, 88),
                            new RectangleF(column * source.Width / 16f, row * source.Height / 9f,
                                source.Width / 16f, source.Height / 9f), GraphicsUnit.Pixel);
                    }
                }
            }
            output.Save(outputPath, ImageFormat.Png);
        }
    }

    private static int Clamp(double value) {
        return (int)Math.Max(0, Math.Min(255, Math.Round(value)));
    }
}
'@
[DiceAtlasExport]::Run((Join-Path $PSScriptRoot 'dice_sheet_ancient_ivory_keyed.png'), [IO.Path]::GetFullPath($OutputPath))
