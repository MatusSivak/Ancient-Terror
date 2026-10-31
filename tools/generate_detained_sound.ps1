# Original procedural iron-bar rattle; no sampled audio. Run from the repository root.
$rate = 44100
$samples = [int]($rate * 0.8)
$target = Join-Path $PSScriptRoot '../assets/sounds/investigator_detained.wav'
$writer = [IO.BinaryWriter]::new([IO.File]::Create($target))
try {
    $writer.Write([Text.Encoding]::ASCII.GetBytes('RIFF'))
    $writer.Write([int](36 + $samples * 2))
    $writer.Write([Text.Encoding]::ASCII.GetBytes('WAVEfmt '))
    $writer.Write([int]16); $writer.Write([int16]1); $writer.Write([int16]1)
    $writer.Write([int]$rate); $writer.Write([int]($rate * 2))
    $writer.Write([int16]2); $writer.Write([int16]16)
    $writer.Write([Text.Encoding]::ASCII.GetBytes('data')); $writer.Write([int]($samples * 2))
    $random = [Random]::new(31)
    for ($i = 0; $i -lt $samples; $i++) {
        $t = $i / [double]$rate
        $value = 0.0
        foreach ($hit in @(0.02, 0.10, 0.19, 0.31, 0.45)) {
            $age = $t - $hit
            if ($age -ge 0) {
                $envelope = [Math]::Exp(-$age * 23) * [Math]::Min(1, $age * 1600)
                $ring = [Math]::Sin(2 * [Math]::PI * 830 * $age) + 0.55 * [Math]::Sin(2 * [Math]::PI * 1371 * $age)
                $value += 0.19 * $envelope * ($ring + 0.3 * ($random.NextDouble() * 2 - 1))
            }
        }
        $writer.Write([int16]([Math]::Max(-1, [Math]::Min(1, $value)) * 25000))
    }
} finally { $writer.Dispose() }
