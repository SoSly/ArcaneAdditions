# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased](https://github.com/SoSly/ArcaneAdditions/tree/main)

### Added
- counterspell component for council wizards

### Changed
- update to mna-3.0.0.9

### Fixed
- fix the component name for astral projection

## [1.20.1-forge-1.7.2] - 2024-02-15
### Added
- interop support for woodwalker's-based polymorph

### Changed
- upgrade to minecraft 1.20

### Removed
- interop support for identity

### Fixed
- rendering for ice block and soulsearcher's lens

## [1.18.2-forge-1.6.0] - 2023-10-07

### Added
- interop support for identity-based polymorph

### Changed
- create a proper interop and abstraction layer for polymorph

### Removed
- doom component

### Fixed
- make the soulsearcher's lens show up in creative menus and jei
- correctly assign the maximum fill for a phylactery when using the soulsearcher's lens in creative
- soulsearcher's lens will always cost xp even for smaller creatures

## [1.18.2-forge-1.5.3] - 2023-08-12
### Added

### Changed
- updated to mna-2.0

### Deprecated
- doom component

### Fixed
- refactored polymorph with new API features
- removed non-API calls from phylacteries

## [1.18.2-forge-1.5.2] - 2023-06-21
### Added
- allow players to depolymorph with a book of rote

### Fixed
- stopped configuration rewriting

## [1.18.2-forge-1.5.1] - 2023-06-18
### Changed
- update to mna 1.7.2.10
- mention animus dust in the polymorph entry

## [1.18.2-forge-1.4.2] - 2022-07-01
### Added
- soulsearcher's lens now emits a beam particle while in use

### Changed
- reduced doom complexity
- updated recipes for several spell components