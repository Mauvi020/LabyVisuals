# LabyVisuals

LabyMod 4 addon with combat-focused HUD visuals: Target HUD, Last Hit damage,
Inventory HUD, Hit Marker, Combo Counter, Damage Stats (DPS) and a Low Health
vignette.

## Download

### Option 1 - stable URL (recommended, no login required)

Every successful build on `master` replaces the JAR of the rolling
`latest` release:

```
https://github.com/Mauvi020/LabyVisuals/releases/download/latest/labyvisuals-release.jar
```

Alias (serves the most recently *published* release):

```
https://github.com/Mauvi020/LabyVisuals/releases/latest/download/labyvisuals-release.jar
```

### Option 2 - download script (always the newest Actions artifact)

Resolves the latest successful workflow run automatically - no artifact ID
needed. Requires a GitHub token with `repo`/`public_repo` scope in
`$env:GITHUB_TOKEN` (artifact downloads via the API need a token even for
public repositories):

```powershell
.\scripts\Download-LatestArtifact.ps1
# optional: .\scripts\Download-LatestArtifact.ps1 -Token <your-token> -OutDir "$env:APPDATA\.laby4\addons"
```

### Option 3 - gh CLI (dynamic, no artifact ID needed)

`gh` needs authentication for artifact downloads (`gh auth login` once, or a
`GH_TOKEN`/`GITHUB_TOKEN` environment variable - never commit a token):

```powershell
$runId = gh run list --repo Mauvi020/LabyVisuals --workflow build.yml --status success --limit 1 --json databaseId --jq '.[0].databaseId'
gh run download $runId --repo Mauvi020/LabyVisuals --name Artifacts
```

By artifact ID (only if you already know it - always authenticated via `gh`):

```powershell
gh api repos/Mauvi020/LabyVisuals/actions/artifacts/<artifact-id>/zip > artifact.zip
```

> Note: plain browser clicks on `https://api.github.com/.../artifacts/<id>/zip`
> fail with `401 Unauthorized` - this endpoint always requires an
> `Authorization: Bearer <token>` header. Use Option 1 for a direct link.

## Build locally

```powershell
.\gradlew.bat build
.\gradlew.bat createReleaseJar
# result: build\libs\labyvisuals-release.jar
```

Run `build` before `createReleaseJar` - the release task alone fails because
it needs the project metadata produced by the first task.

## Installing

Copy `labyvisuals-release.jar` into `%appdata%\.laby4\addons\` and restart
LabyMod. Configure features under **LabyMod Settings -> LabyVisuals**; the
HUD widgets are added via the LabyMod HUD editor (category "LabyVisuals").
