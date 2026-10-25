# Builds the implementation jars into the artifacts folder.
# Requires a JDK (javac + jar) on the PATH.

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot

$out = Join-Path $root "out"
$artifacts = Join-Path $root "artifacts"
$lib = Join-Path $root "lib"

if (Test-Path $out) { Remove-Item $out -Recurse -Force }
New-Item -ItemType Directory -Path "$out\model-impl" -Force | Out-Null
New-Item -ItemType Directory -Path "$out\logic-impl" -Force | Out-Null
New-Item -ItemType Directory -Path $artifacts -Force | Out-Null

$apiCp = "$lib\model-1.0.jar;$lib\logic-1.0.jar"

# Compile the model module
$modelSources = Get-ChildItem "$root\model-impl\src" -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -cp $apiCp -d "$out\model-impl" $modelSources
if ($LASTEXITCODE -ne 0) { throw "model-impl compilation failed" }

# Compile the logic module (depends on the model module)
$logicSources = Get-ChildItem "$root\logic-impl\src" -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -cp "$apiCp;$out\model-impl" -d "$out\logic-impl" $logicSources
if ($LASTEXITCODE -ne 0) { throw "logic-impl compilation failed" }

# Package the jars
jar cf "$artifacts\model-impl-1.0.jar" -C "$out\model-impl" .
if ($LASTEXITCODE -ne 0) { throw "model-impl jar failed" }
jar cf "$artifacts\logic-impl-1.0.jar" -C "$out\logic-impl" .
if ($LASTEXITCODE -ne 0) { throw "logic-impl jar failed" }

Write-Host "Build OK:"
Get-ChildItem $artifacts | ForEach-Object { Write-Host "  $($_.Name)" }
