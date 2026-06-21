# Development Agent Pipeline

This file defines the required workflow for automated agents and human
contributors. The protected integration branch in this repository is `main`.
Treat `master` the same way if it is introduced as an alias.

## Non-negotiable rules

1. Never implement, commit, or push a feature or fix directly on `main` or
   `master`.
2. Preserve unrelated and pre-existing worktree changes. Do not reset, discard,
   or overwrite them.
3. Every releasable feature or fix must carry the correct version change in
   `gradle.properties`.
4. A change is complete only after the relevant tests pass and the final diff
   has been reviewed.
5. Do not commit, push, merge, tag, or publish unless the task authorizes that
   action.

## Pipeline

### 1. Inspect before editing

Run:

```text
git status --short --branch
git branch --show-current
```

Then inspect the files and tests related to the request. Identify existing
worktree changes before modifying anything.

### 2. Select the branch

If the current branch is `main` or `master`, create and switch to a focused
branch before editing:

| Change | Branch pattern |
| --- | --- |
| User-visible feature | `feat/<short-name>` |
| Bug fix | `fix/<short-name>` |
| Urgent production fix | `hotfix/<short-name>` |
| Refactor without behavior change | `refactor/<short-name>` |
| Documentation only | `docs/<short-name>` |
| Build, dependency, or maintenance work | `chore/<short-name>` |
| Release preparation | `release/<version>` |

Use lowercase kebab-case for `<short-name>`. Branch from an up-to-date `main`
unless the task explicitly targets another base. If uncommitted user changes
already exist on `main`, preserve them when creating the branch; do not clean or
stash them without permission.

### 3. Implement the smallest complete change

- Follow the existing project structure and conventions.
- Add or update tests when behavior changes and the project has a suitable test
  location.
- Keep unrelated formatting, cleanup, and refactors out of the change.
- Do not alter the mod version while the scope is still changing.

### 4. Validate the implementation

Run the narrowest useful checks during development. Before declaring the
implementation complete, run the full build:

```text
.\gradlew.bat build
```

On Unix-like systems, use `./gradlew build`.

Fix failures caused by the change. Report unrelated or environment-dependent
failures explicitly.

### 5. Apply the version change

After the implementation scope is stable and its checks pass:

1. Synchronize with the latest target branch when authorized.
2. Read the version from the target branch's `gradle.properties`.
3. Classify the completed change using the rules below.
4. Change only `mod_version` to the calculated next version.
5. Run the full build again so the exact release version is validated.

Do not bump the version at branch creation or during exploratory work. If the
target branch version changes before merge, recalculate the version from the new
base. Never resolve a version conflict by retaining a stale or duplicate
version.

### 6. Review and hand off

Before handoff:

```text
git diff --check
git status --short
git diff
```

Confirm that:

- no feature or fix was made directly on `main` or `master`;
- the version bump matches the final scope;
- generated files, secrets, IDE files, and unrelated user changes were not
  added;
- the full build passed after the version change.

Use Conventional Commit subjects when a commit is requested:

```text
feat: add localized typewriter keys
fix: release held keys when focus is lost
docs: document keyboard layout behavior
```

Open a pull request into `main`. Do not bypass the pull request and CI process.

### 7. Merge and release

- Merge only after review and required CI checks pass.
- Delete the topic branch after merge when repository policy allows it.
- Create the release tag only from the merged `main` commit.
- The tag and published artifact version must exactly match `mod_version`.
- This repository uses plain numeric tags such as `1.1.0`.

## Mod version specification

`mod_version` in `gradle.properties` is the single source of truth. Versions use
three numeric fields:

```text
MAJOR.MINOR.PATCH
```

`MINOR` is the feature number and `PATCH` is the fix number.

| Change type | Version action | Example from `1.4.7` |
| --- | --- | --- |
| Breaking or incompatible change | Increment `MAJOR`; reset the others | `2.0.0` |
| Backward-compatible feature | Increment `MINOR`; reset `PATCH` | `1.5.0` |
| Backward-compatible bug fix | Increment `PATCH` | `1.4.8` |
| Docs, tests, formatting, or internal tooling only | No mod version change | `1.4.7` |

A breaking change includes incompatible changes to saved data, configuration,
public integration behavior, network protocols, or supported APIs. A feature
adds user-visible or integration-visible capability without breaking existing
behavior. A fix corrects existing behavior without adding a new capability.

For a pull request containing several change types, use the highest required
bump: `MAJOR` over `MINOR`, and `MINOR` over `PATCH`. Apply one version bump per
pull request, not one bump per commit.

Examples:

- `1.0.2` plus a new keyboard-layout feature becomes `1.1.0`.
- `1.1.0` plus a key-release bug fix becomes `1.1.1`.
- `1.1.1` plus an incompatible channel protocol becomes `2.0.0`.

## Git flow

```text
main
  └── feat/*, fix/*, hotfix/*, refactor/*, docs/*, chore/*
        └── implementation -> tests -> version decision -> final build -> PR
              └── review + CI -> merge to main -> tag/release
```

`main` must remain buildable and releasable. Repository administrators should
enable branch protection for `main` with pull requests and the `build` CI job
required, because written agent rules cannot technically prevent a direct push.
