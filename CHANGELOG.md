# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.1] - 2026-02-03

### Changed

- Reorganized test suite into structured packages by RFC and component
  (`core/components`, `core/properties`, `core/rfc5545`, `extensions/rfc7986`,
  `extensions/rfc9073`, `extensions/rfc9074`, `infrastructure`, `integration`,
  `performance`, `compatibility`)
- Consolidated duplicate tests (716 → 694 tests across 67 classes)
- Source code cleanups: removed unused imports and dead code

### Dependencies

- Updated maven-enforcer-plugin to 3.6.2
- Updated versions-maven-plugin to 2.21.0

## [2.0.0] - 2026-01-27

### Added

- **RFC 9073 - Event Publishing Extensions**
  - PARTICIPANT component with UID, PARTICIPANT-TYPE, CALENDAR-ADDRESS,
    STRUCTURED-DATA, NAME, and DESCRIPTION properties
  - VLOCATION component with UID, NAME, DESCRIPTION, GEO, LOCATION-TYPE,
    CATEGORIES, and URL properties
  - VRESOURCE component with UID, NAME, DESCRIPTION, GEO, RESOURCE-TYPE,
    and STRUCTURED-DATA properties
  - STYLED-DESCRIPTION property for rich-text descriptions (HTML, Markdown)
  - CALENDAR-ADDRESS, LOCATION-TYPE, PARTICIPANT-TYPE, and RESOURCE-TYPE
    property classes
- **RFC 9074 - VALARM Extensions**
  - PROXIMITY property for location-based alarm triggers
    (ARRIVE, DEPART, CONNECT, DISCONNECT)
  - STRUCTURED-DATA property for VALARM components
- **RFC 7986 - Calendar Extensions**
  - COLOR, IMAGE, and CONFERENCE properties for VEVENT/VTODO/VJOURNAL
  - NAME and CALENDAR-ADDRESS properties for VCALENDAR
  - RefreshInterval and Source property classes
- **RFC 5546 - iTIP Protocol**
  - METHOD property parsing, storage, and serialization
  - METHOD value validation against RFC 5546 constants in strict mode
  - iTIP flow support for PUBLISH, REQUEST, REPLY, and CANCEL methods
- VALARM component with ACTION, TRIGGER, SUMMARY, DESCRIPTION, DURATION,
  REPEAT properties and multiple ATTENDEE/ATTACH support
- VALARM subcomponent parsing across VEVENT, VTODO, and VJOURNAL
- VAVAILABILITY component with BUSYTYPE support
  (BUSY, FREE, BUSY-UNAVAILABLE, BUSY-TENTATIVE)
- Organizer and Contact property classes
- VEVENT enhancements: ORGANIZER, CONTACT, PRIORITY, REQUEST-STATUS, GEO
- Comprehensive validation with detailed error reporting for Event, Todo,
  Journal, Timezone, and RRULE components
- Advanced recurrence support with `getAllOccurrences` for complex RRULE
  patterns with RDATE/EXDATE exceptions
- Performance optimizations: streaming parsing mode, configurable buffer
  sizes, performance monitoring metrics
- RFC 5545 line folding in all `toICalendar()` methods
- `Utils.exportToFile()` and `Utils.importFromFile()` utilities
- Comprehensive RFC 5545 compliance test framework (sections 3.1–4.0)
- Dependabot for automated dependency updates
- GitHub Actions CI workflow

### Changed

- Java 17+ now required (up from Java 11)
- Enhanced VTODO status handling with strict validation (COMPLETED status
  requires COMPLETED date and PERCENT-COMPLETE=100)
- Improved RRULE validation: FREQ values, INTERVAL ranges, COUNT/UNTIL
  mutual exclusivity, BYxxx rule compatibility checks
- Enhanced timezone validation: TZID format, TZURL, offset format checking
- Improved text escaping per RFC 5545

### Fixed

- TRANSP property parsing (was incorrectly using `parseStatus` instead of
  direct value parsing)
- Todo.java compilation errors (duplicate methods, missing constructors)
- Organizer.java inheritance issues (Property constructor compatibility)

### Removed

- Ant build file (Maven only going forward)

## [1.0.2] - 2024-09-02

### Changed

- Bundled Google RFC 2445 RRule library source directly, since no reliable
  Maven artifact is available (source unmodified except for a Java 9+
  compatibility fix)
- Switched back to Java 11 (Java 17+ features were not yet in use)

### Removed

- Journal UI code (moved to its own repository at craigk5n/k5njournal)

## [1.0.1] - 2024-08-28

### Added

- New unit tests

### Changed

- Updated to Java 17

### Fixed

- Fixes for many existing tests

## [1.0.0] - 2021-12-14

### Changed

- Converted build system from Ant to Maven
- Restructured directories for Maven conventions
- Upgraded from JUnit 3 to JUnit 5
- Reformatted code to standard Java formatting
- Refactored collections: replaced `Vector` with `List`/`ArrayList`,
  simplified loops

### Fixed

- RRULE code that threw exceptions when no timezone info was provided

## [0.4.8]

### Fixed

- Creating Event would lose start date setting
- Bug in CSVParser class in parser method (patch 2859626)

### Added

- ISO 8601 date parsing in CSVParser (patch 2860537)
- yDoc build documentation (patch 2864816)

## [0.4.7] - 2008-01-31

### Added

- CalendarPanelApplet class for displaying remote iCalendar files in a
  Java applet

## [0.4.6] - 2008-01-14

### Added

- EXDATE and RDATE support in VEVENT
- ATTENDEE read/write support
- CREATED field support
- TRANSP field support in VEVENT

### Fixed

- Empty UID on new events
- Floating date-time handling (added `Date.setFloating`/`Date.isFloating`)
- CalendarPanel header area not resizing on font changes

## [0.4.5] - 2008-01-03

### Added

- Multi-column day layout in CalendarPanel for days with many events
- Custom mouse-over popup displays replacing Swing ToolTip
- `setFont` support in CalendarPanel

### Fixed

- Weekly repeating events not saved correctly
- CalendarPanel scrollbar values inaccurate after pressing "Today" button

## [0.4.4] - 2007-12-12

### Fixed

- Generated iCalendar output for RRULEs

### Added

- Simple constructor for Rrule class

## [0.4.3] - 2007-09-20

### Added

- ATTACH property support with Apache Commons Codec for base64
  encoding/decoding
- `getNamedAttribute` method on all Property classes
- Performance improvements for parsing long folded lines (inline
  attachments)

## [0.4.2] - 2007-08-01

### Added

- STATUS field support in Event and Journal classes
- CalendarPanel: different background for today's date, "Today" button,
  background color override support

## [0.4.1] - 2007-06-12

### Added

- CSVParser for parsing CSV calendar data from MS Outlook

### Changed

- CalendarPanel modified to work with Java 1.2 (removed generics)

## [0.4.0] - 2007-06-11

### Added

- CalendarPanel Swing GUI component
- `source-zip` and `javadoc` Ant targets

## [0.3.0] - 2007-05-30

### Added

- Google RRULE Java implementation for generating recurrence dates
- `getDayOfWeek`, `getFirstDayOfWeekForYear`, `getLastDayOfWeekForYear`
  in Utils
- `getWeekOfYear`, `getDayOfYear`, `getDayOfWeek`, `isBefore`, `isAfter`
  methods on Date
- `setUserData`/`getUserData` methods on Event
- Timezone support via Joda Time
- Comment and Location support for events

### Fixed

- Numerous RRULE bugs
- Duration in Event

## [0.2.6] - 2007-04-24

### Changed

- Replaced "Clear" button for text search with graphic icon (k5njournal)

## [0.2.5] - 2007-04-24

### Added

- Text search filter (k5njournal)
- Export to ICS in File menu (k5njournal)

### Fixed

- Clicking journal list column headers to sort caused wrong entry display

## [0.2.4] - 2007-04-18

### Changed

- Wrap text at word boundaries in entry descriptions (k5njournal)
- Default to Windows look and feel (k5njournal)

### Fixed

- NullPointerException when data file is missing DTSTART values

## [0.2.3] - 2007-04-18

### Fixed

- JTree not refreshing to show newly added dates (k5njournal)

## [0.2.2] - 2007-04-17

### Fixed

- NullPointerException if no journal entries yet (k5njournal)

## [0.2.1] - 2007-04-16

### Fixed

- k5njournal compatibility with Java 1.4

## [0.2.0] - 2007-04-16

### Added

- k5njournal application (`java -jar k5njournal-0.2.0.jar`)
- Journal object (VJOURNAL) implementation
- CLASS and URL support in Event
- Unit tests based on JUnit

### Changed

- Made most Event members protected with get/set methods
- Renamed classes with "Ical" to "ICalendar"
- Changed `Date.hasTime` to `Date.isDateOnly`
- Added getter/setter methods to Date
- Added `Utils.DateToYYYYMMDD` method

### Fixed

- LANGUAGE attribute handling in Description and Summary
- VALUE=DATE attribute handling for dates
- Day-of-month error checking including leap year
- Date parsing for UTC dates (patch 1464212)

## [0.1.0] - 2006-02-02

### Added

- Initial public release

[2.0.1]: https://github.com/craigk5n/javacaltools/compare/v2.0.0...HEAD
[2.0.0]: https://github.com/craigk5n/javacaltools/releases/tag/v2.0.0
[1.0.2]: https://github.com/craigk5n/javacaltools/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/craigk5n/javacaltools/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/craigk5n/javacaltools/releases/tag/v1.0.0
