# Rebuild the runtime artwork from the full-resolution generated sources (Windows).
# Run from any directory: ./tools/prepare-nov17-artwork.ps1
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$projectRoot = Split-Path $PSScriptRoot -Parent
$manifest = Get-Content (Join-Path $projectRoot 'docs/new-investigator-artwork-2026-11-17.json') -Raw | ConvertFrom-Json
$mask = [System.Drawing.Bitmap]::new((Join-Path $projectRoot 'assets/investigator/THE_ACTRESS.png'))

function Read-Pixels([System.Drawing.Bitmap] $bitmap) {
    $rect = [System.Drawing.Rectangle]::new(0, 0, $bitmap.Width, $bitmap.Height)
    $data = $bitmap.LockBits($rect, [System.Drawing.Imaging.ImageLockMode]::ReadOnly, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
        $bytes = [byte[]]::new($data.Stride * $bitmap.Height)
        [System.Runtime.InteropServices.Marshal]::Copy($data.Scan0, $bytes, 0, $bytes.Length)
        return ,$bytes
    } finally { $bitmap.UnlockBits($data) }
}

try {
    $maskPixels = Read-Pixels $mask
    foreach ($job in $manifest.artwork) {
        $isPortrait = $job.type -eq 'portrait'
        $width = if ($isPortrait) { $mask.Width } else { 700 }
        $height = if ($isPortrait) { $mask.Height } else { 340 }
        $source = [System.Drawing.Bitmap]::new((Join-Path $projectRoot $job.source))
        $result = [System.Drawing.Bitmap]::new($width, $height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        try {
            $graphics = [System.Drawing.Graphics]::FromImage($result)
            $attributes = [System.Drawing.Imaging.ImageAttributes]::new()
            try {
                $graphics.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceCopy
                $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
                $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
                $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
                $attributes.SetWrapMode([System.Drawing.Drawing2D.WrapMode]::TileFlipXY)
                # Center crop to the runtime aspect ratio without stretching.
                $scale = [Math]::Max($width / $source.Width, $height / $source.Height)
                $cropWidth = $width / $scale
                $cropHeight = $height / $scale
                $destination = [System.Drawing.Rectangle]::new(0, 0, $width, $height)
                $graphics.DrawImage($source, $destination, [single](($source.Width - $cropWidth) / 2), [single](($source.Height - $cropHeight) / 2), [single]$cropWidth, [single]$cropHeight, [System.Drawing.GraphicsUnit]::Pixel, $attributes)
            } finally { $attributes.Dispose(); $graphics.Dispose() }
            if ($isPortrait) {
                $rect = [System.Drawing.Rectangle]::new(0, 0, $width, $height)
                $data = $result.LockBits($rect, [System.Drawing.Imaging.ImageLockMode]::ReadWrite, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
                try {
                    $pixels = [byte[]]::new($data.Stride * $height)
                    [System.Runtime.InteropServices.Marshal]::Copy($data.Scan0, $pixels, 0, $pixels.Length)
                    for ($i = 3; $i -lt $pixels.Length; $i += 4) { $pixels[$i] = $maskPixels[$i] }
                    [System.Runtime.InteropServices.Marshal]::Copy($pixels, 0, $data.Scan0, $pixels.Length)
                } finally { $result.UnlockBits($data) }
                $result.Save((Join-Path $projectRoot $job.runtime), [System.Drawing.Imaging.ImageFormat]::Png)
            } else {
                $codec = [System.Drawing.Imaging.ImageCodecInfo]::GetImageEncoders() | Where-Object MimeType -eq 'image/jpeg'
                $parameters = [System.Drawing.Imaging.EncoderParameters]::new(1)
                try {
                    $parameters.Param[0] = [System.Drawing.Imaging.EncoderParameter]::new([System.Drawing.Imaging.Encoder]::Quality, [long]95)
                    $result.Save((Join-Path $projectRoot $job.runtime), $codec, $parameters)
                } finally { $parameters.Dispose() }
            }
        } finally { $source.Dispose(); $result.Dispose() }
        # Decode the actual saved file and verify its runtime dimensions and alpha.
        $saved = [System.Drawing.Bitmap]::new((Join-Path $projectRoot $job.runtime))
        try {
            if ($saved.Width -ne $width -or $saved.Height -ne $height) { throw "Incorrect dimensions: $($job.id)" }
            if ($isPortrait) {
                $savedPixels = Read-Pixels $saved
                for ($i = 3; $i -lt $savedPixels.Length; $i += 4) {
                    if ($savedPixels[$i] -ne $maskPixels[$i]) { throw "Portrait mask mismatch: $($job.id)" }
                }
            }
        } finally { $saved.Dispose() }
        Write-Output "$($job.runtime): verified ${width}x${height}$(if ($isPortrait) { ', exact shared alpha' })"
    }
} finally { $mask.Dispose() }
