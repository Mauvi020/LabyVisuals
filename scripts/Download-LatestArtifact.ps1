<#
.SYNOPSIS
    Downloads the latest release JAR of the LabyVisuals GitHub Actions build.

.DESCRIPTION
    Resolves the most recent successful run of the build workflow on the given
    branch, picks its newest non-expired artifact and downloads the contained
    *-release.jar. No artifact ID has to be configured anywhere.

    Artifact downloads via the GitHub API require a token even for public
    repositories. Provide one via -Token or the GITHUB_TOKEN environment
    variable (a classic PAT with "repo"/"public_repo" scope is sufficient).

.PARAMETER Repo
    Repository as "owner/name". Default: Mauvi020/LabyVisuals

.PARAMETER Workflow
    Workflow file name. Default: build.yml

.PARAMETER Branch
    Branch the run must have been triggered on. Default: master

.PARAMETER ArtifactName
    Artifact name to look for. Default: Artifacts (as uploaded by the workflow).
    Use '' to accept any artifact of the run.

.PARAMETER Token
    GitHub token. Default: environment variable GITHUB_TOKEN.

.PARAMETER OutDir
    Directory the JAR is extracted to. Default: .\downloads

.PARAMETER Install
    If set, installs the JAR directly to the LabyMod addons folder.
    (default: %APPDATA%\.laby4\addons\ on Windows)

.EXAMPLE
    .\scripts\Download-LatestArtifact.ps1
    .\scripts\Download-LatestArtifact.ps1 -Token <your-token> -OutDir C:\laby\addons
    .\scripts\Download-LatestArtifact.ps1 -Install
#>
param(
    [string]$Repo = 'Mauvi020/LabyVisuals',
    [string]$Workflow = 'build.yml',
    [string]$Branch = 'master',
    [string]$ArtifactName = 'Artifacts',
    [string]$Token = $env:GITHUB_TOKEN,
    [string]$OutDir = (Join-Path (Get-Location) 'downloads'),
    [switch]$Install
)

$ErrorActionPreference = 'Stop'

# Resolve LabyMod addons folder if -Install is used
if ($Install) {
    $labyAddons = Join-Path $env:APPDATA '.laby4\addons'
    if (-not (Test-Path (Split-Path $labyAddons -Parent))) {
        Write-Warning "LabyMod directory not found at '$labyAddons'. Falling back to -OutDir."
    } else {
        $OutDir = $labyAddons
    }
}

$headers = @{
    'Accept'               = 'application/vnd.github+json'
    'X-GitHub-Api-Version' = '2022-11-28'
    'User-Agent'           = 'LabyVisuals-Artifact-Downloader'
}
if ([string]::IsNullOrWhiteSpace($Token)) {
    Write-Warning 'No token provided (env GITHUB_TOKEN / -Token). Artifact downloads usually require one, the lookup below may fail with 401/403.'
} else {
    $headers['Authorization'] = "Bearer $Token"
}

# 1) Latest successful run of the workflow on the branch (no hardcoded run ID)
$runsUri = "https://api.github.com/repos/$Repo/actions/workflows/$Workflow/runs?status=success&branch=$Branch&per_page=1"
Write-Host "Looking up latest successful run: $runsUri"
try {
    $response = Invoke-RestMethod -Uri $runsUri -Headers $headers
    $run = $response.workflow_runs[0]
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    if ($statusCode -eq 401 -or $statusCode -eq 403) {
        throw "Authentication failed ($statusCode). Provide a valid GitHub token via -Token or `$env:GITHUB_TOKEN."
    }
    throw "Failed to fetch workflow runs: $_"
}
if ($null -eq $run) {
    throw "No successful run found for workflow '$Workflow' on branch '$Branch'."
}
Write-Host ("Found run #{0} (id {1}, commit {2}, {3})" -f $run.run_number, $run.id, $run.head_sha.Substring(0, 7), $run.created_at)

# 2) Newest non-expired artifact of that run
$artifactsUri = "https://api.github.com/repos/$Repo/actions/runs/$($run.id)/artifacts?per_page=100"
try {
    $artifactsResponse = Invoke-RestMethod -Uri $artifactsUri -Headers $headers
    $artifacts = $artifactsResponse.artifacts |
        Where-Object { -not $_.expired } |
        Sort-Object -Property created_at -Descending
} catch {
    throw "Failed to fetch artifacts for run $($run.id): $_"
}
if ($null -eq $artifacts -or @($artifacts).Count -eq 0) {
    throw "Run $($run.id) has no downloadable artifacts."
}
$artifact = @($artifacts | Where-Object { [string]::IsNullOrEmpty($ArtifactName) -or $_.name -eq $ArtifactName })[0]
if ($null -eq $artifact) {
    $available = (@($artifacts) | ForEach-Object { $_.name }) -join ', '
    throw "No artifact named '$ArtifactName' found. Available: $available"
}
Write-Host ("Using artifact '{0}' (id {1}, {2:N0} bytes, created {3})" -f $artifact.name, $artifact.id, $artifact.size_in_bytes, $artifact.created_at)

# 3) Download the zip (requires authentication)
$zipPath = Join-Path ([System.IO.Path]::GetTempPath()) ("labyvisuals-artifact-{0}.zip" -f $artifact.id)
Write-Host "Downloading $($artifact.archive_download_url)"
try {
    Invoke-WebRequest -Uri $artifact.archive_download_url -Headers $headers -OutFile $zipPath -UseBasicParsing
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    if ($statusCode -eq 401 -or $statusCode -eq 403) {
        throw "Download failed ($statusCode Unauthorized). Artifact downloads require a valid GitHub token even for public repos."
    }
    throw "Download failed: $_"
}

# 4) Validate the zip file
if (-not (Test-Path $zipPath) -or (Get-Item $zipPath).Length -eq 0) {
    throw "Downloaded file is empty or missing."
}
try {
    $zip = [System.IO.Compression.ZipFile]::OpenRead($zipPath)
    $zip.Dispose()
} catch {
    throw "Downloaded file is not a valid zip archive: $_"
}

# 5) Extract the *-release.jar
if (Test-Path $OutDir) {
    Remove-Item $OutDir -Recurse -Force -ErrorAction SilentlyContinue
}
$extractDir = Join-Path ([System.IO.Path]::GetTempPath()) ("labyvisuals-artifact-{0}" -f $artifact.id)
if (Test-Path $extractDir) {
    Remove-Item $extractDir -Recurse -Force
}
Expand-Archive -Path $zipPath -DestinationPath $extractDir -Force
$jar = Get-ChildItem $extractDir -Recurse -Filter '*-release.jar' | Select-Object -First 1
if ($null -eq $jar) {
    throw "No *-release.jar found inside the artifact zip."
}

# 6) Validate JAR is a valid zip (JARs are zip files)
try {
    $jarZip = [System.IO.Compression.ZipFile]::OpenRead($jar.FullName)
    $jarZip.Dispose()
} catch {
    throw "Downloaded JAR appears to be corrupted: $_"
}

New-Item -ItemType Directory -Path $OutDir -Force | Out-Null
$target = Join-Path $OutDir $jar.Name
Copy-Item $jar.FullName $target -Force
Remove-Item $zipPath -Force
Remove-Item $extractDir -Recurse -Force

Write-Host ''
Write-Host "Done: $target" -ForegroundColor Green
Write-Host ("Artifact: {0} (run {1}, commit {2})" -f $artifact.id, $run.id, $run.head_sha)

if ($Install) {
    Write-Host ''
    Write-Host "Installed to LabyMod addons folder." -ForegroundColor Cyan
    Write-Host "Restart LabyMod to load the addon." -ForegroundColor Cyan
}