# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased](https://github.com/SoSly/ArcaneAdditions/tree/1.20.1)

### Added
- Added familiar AI behavior settings to the server config

### Changed
- Improved familiar spellcasting reliability; familiars will now more consistently cast offensive and utility spells when appropriate
- Familiars now better respect their spell cooldowns when deciding whether to cast

### Fixed
- Added a loot table to the scribe's bench so that it properly drops when broken
- Fixed a typo in the Soul Searcher's Lens guidebook description
- Fixed a bug preventing familiars from changing casting resource types
- Improved familiar tracking performance when crossing dimensions or exploring distant areas
- Fixed familiars attempting to cast spells they couldn't afford or were still on cooldown
- Fixed familiars continuing to try casting spells after their target was defeated
- Fixed entity-capability synchronization issues causing familiars to duplicate or become unresponsive during dimension travel
- Fixed loot chest content voiding caused by familiar shapes being incorrectly included in random spell generation

## [1.20.1-forge-1.9.6](https://github.com/SoSly/ArcaneAdditions/releases/tag/1.20.1-forge-1.9.6)
### Changed
- updated to MnA 3.1.0.1

## [1.20.1-forge-1.9.5](https://github.com/SoSly/ArcaneAdditions/releases/tag/1.20.1-forge-1.9.5)
### Changed
- familiars now wander a little more predictably

### Fixed
- the game no longer crashes with MnA 3.0.0.17

## [1.20.1-forge-1.9.4](https://github.com/SoSly/ArcaneAdditions/releases/tag/1.20.1-forge-1.9.4)
### Added
- chinese localization (thanks to @xiaoknya).

### Changed
- allay can now be taken as a familiar by default

### Fixed
- fixed the ritual of inculcation's name in tooltips
- strip now progresses your magic level and as a rote
- random polymorph staves no longer cause the game to crash

## [1.20.1-forge-1.9.3](https://github.com/SoSly/ArcaneAdditions/releases/tag/1.20.1-forge-1.9.3)
### Fixed
- fixed changes to the familiar list not being saved
- this also might have fixed the config file rotating on every server start?

## [1.20.1-forge-1.9.2](https://github.com/SoSly/ArcaneAdditions/releases/tag/1.20.1-forge-1.9.2)
### Fixed
- we no longer attempt to render the scribe's bench on the server (which was causing a crash)

## [1.20.1-forge-1.9.1](https://github.com/SoSly/ArcaneAdditions/releases/tag/1.20.1-forge-1.9.1)
### Fixed
- you no longer require a familiar to engage with other mobs

## [1.20.1-forge-1.9.0](https://github.com/SoSly/ArcaneAdditions/releases/tag/1.20.1-forge-1.9.0)
### Added
- a new scribe's table was added for copying written spells onto blank vellum
- you will now get an advancement when you summon your first familiar
- familiars now have their own mana pool based on their caster's level
- familiars now regenerate health over time if they have enough mana to do so
- familiars now have damage resistance based on their caster's level
- you can now bap your familiar with a wand to temporarily unsummon them
- there is a cantrip to re-summon your familiar after you have bapped them
- familiars can now learn spells with the ritual of inculcation

### Changed
- the treestride gui was reworked and now better matches those included in mana & artifice
- the recipe for a soulsearcher's lens was changed to require transmuted silver instead of a purified vinteum ingot.
- improved familiar AI a little bit, but it still needs a lot of work.
- familiars now disconnect when their caster leaves the server. Don't worry, they'll be back when you log in again.
- familiars may now wander a little farther from you

### Fixed
- added the missing counterspell recipe
- added modifier descriptions for all spell components
- updated the treestride gui so that the delete button does not overlap the teleport text 

## [1.20.1-forge-1.8.1](https://github.com/SoSly/ArcaneAdditions/releases/tag/1.20.1-forge-1.8.1)

### Fixed
- ensure client events only run on the client

## [1.20.1-forge-1.8.0](https://github.com/SoSly/ArcaneAdditions/releases/tag/1.18.2-1.8.0)

### Added
- counterspell component for council wizards
- a ritual to summon your familiar
- familiar shape for affecting familiars with spells
- shared shape for affecting your familiar and yourself at the same time

### Changed
- update to mna-3.0.0.10
- update to neoforge-47.1.100

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
