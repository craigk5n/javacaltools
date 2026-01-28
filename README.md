# JavaCalTools

A Java library for parsing, generating, and manipulating iCalendar (RFC 5545) data.

[![Java](https://img.shields.io/badge/Java-17%2B-blue)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-LGPL%202.1-green.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/us.k5n/javacaltools)](https://search.maven.org/artifact/us.k5n/javacaltools)
[![Tests](https://img.shields.io/github/actions/workflow/status/craigk5n/javacaltools/test.yml?branch=master)](https://github.com/craigk5n/javacaltools/actions/workflows/test.yml)

## Features

- **RFC 5545 Compliant**: Core iCalendar specification support (~95%)
- **Modern RFC Extensions**: RFC 7986, RFC 9073, RFC 9074, RFC 5546 support
- **Component Support**: VEVENT, VTODO, VJOURNAL, VFREEBUSY, VTIMEZONE, VALARM, VLOCATION, VRESOURCE, PARTICIPANT, VAVAILABILITY
- **Recurrence Rules**: Full RRULE support with EXDATE and RDATE exceptions
- **Timezone Handling**: VTIMEZONE component with daylight saving support
- **UTF-8 Support**: Full internationalization and Unicode handling
- **Streaming Parser**: Memory-efficient parsing for large calendar files
- **Java 17+**: Modern Java with minimal dependencies

## Installation

### Maven

```xml
<dependency>
    <groupId>us.k5n</groupId>
    <artifactId>javacaltools</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'us.k5n:javacaltools:2.0.0'
```

## Quick Start

### Parsing iCalendar Data

```java
import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;
import us.k5n.ical.Event;

String icalData = """
    BEGIN:VCALENDAR
    VERSION:2.0
    PRODID:-//Example//Calendar//EN
    BEGIN:VEVENT
    UID:12345@example.com
    DTSTAMP:20240101T100000Z
    DTSTART:20240115T100000Z
    DTEND:20240115T110000Z
    SUMMARY:Team Meeting
    END:VEVENT
    END:VCALENDAR
    """;

ICalendarParser parser = new ICalendarParser();
parser.parse(icalData);

DefaultDataStore dataStore = (DefaultDataStore) parser.getDataStoreAt(0);
Event event = dataStore.getAllEvents().get(0);
System.out.println("Event: " + event.getSummary().getValue());
```

### Creating Events

```java
import us.k5n.ical.Event;
import us.k5n.ical.Date;
import us.k5n.ical.Summary;

Event event = new Event();
event.setUid("unique-id@example.com");
event.setDtstamp(new Date("DTSTAMP", "20240101T100000Z"));
event.setStartDate(new Date("DTSTART", "20240115T100000Z"));
event.setEndDate(new Date("DTEND", "20240115T110000Z"));
event.setSummary(new Summary("SUMMARY:Team Meeting"));

String icalOutput = event.toICalendar();
```

### Working with Recurrence Rules

```java
import us.k5n.ical.Rrule;

Rrule rrule = new Rrule("RRULE:FREQ=WEEKLY;COUNT=10;BYDAY=MO,WE,FR");
List<Date> occurrences = rrule.generateRecurrances(startDate, endDate);
```

## RFC Compliance

| RFC | Description | Status | Coverage |
|-----|-------------|--------|----------|
| [RFC 5545](https://datatracker.ietf.org/doc/html/rfc5545) | iCalendar Core Specification | Implemented | ~95% |
| [RFC 7986](https://datatracker.ietf.org/doc/html/rfc7986) | New Properties for iCalendar | Implemented | ~85% |
| [RFC 9073](https://datatracker.ietf.org/doc/html/rfc9073) | Event Publishing Extensions | Implemented | ~80% |
| [RFC 9074](https://datatracker.ietf.org/doc/html/rfc9074) | VALARM Extensions | Implemented | ~90% |
| [RFC 5546](https://datatracker.ietf.org/doc/html/rfc5546) | iTIP Protocol | Partial | ~70% |
| [RFC 2445](https://datatracker.ietf.org/doc/html/rfc2445) | Original iCalendar (Legacy) | Supported | Backward Compatible |

### RFC 5545 - Core iCalendar

**Components**: VEVENT, VTODO, VJOURNAL, VFREEBUSY, VTIMEZONE, VALARM

**Key Properties**:
- Date/Time: DTSTART, DTEND, DURATION, DTSTAMP
- Recurrence: RRULE, EXDATE, RDATE
- Descriptive: SUMMARY, DESCRIPTION, LOCATION, CATEGORIES
- Relationship: ATTENDEE, ORGANIZER, RELATED-TO
- Status: STATUS, PRIORITY, SEQUENCE, TRANSP

### RFC 7986 - Calendar Extensions

**VCALENDAR Properties**: NAME, DESCRIPTION, UID, URL, LAST-MODIFIED

**Component Properties**: COLOR, IMAGE, CONFERENCE

### RFC 9073 - Event Publishing

**Components**: PARTICIPANT, VLOCATION, VRESOURCE

**Properties**: STYLED-DESCRIPTION, CALENDAR-ADDRESS, LOCATION-TYPE, RESOURCE-TYPE, PARTICIPANT-TYPE

### RFC 9074 - VALARM Extensions

**Properties**: PROXIMITY (location-based triggers), ACKNOWLEDGED, STRUCTURED-DATA

### Known Limitations

- REQUEST-STATUS property not implemented for iTIP
- COUNTER/DECLINECOUNTER workflows are basic
- Some RFC 7986 properties (REFRESH-INTERVAL, SOURCE) defined but not fully integrated

## API Documentation

### Core Classes

| Class | Description |
|-------|-------------|
| `ICalendarParser` | Main parser for iCalendar data |
| `DefaultDataStore` | Storage for parsed calendar components |
| `Event` | VEVENT component |
| `Todo` | VTODO component |
| `Journal` | VJOURNAL component |
| `Freebusy` | VFREEBUSY component |
| `Timezone` | VTIMEZONE component |
| `Valarm` | VALARM component |
| `Rrule` | Recurrence rule handling |
| `Participant` | PARTICIPANT component (RFC 9073) |
| `VLocation` | VLOCATION component (RFC 9073) |
| `VResource` | VRESOURCE component (RFC 9073) |

## Testing

```bash
mvn test
```

**Test Coverage**: 716 tests across 80+ test classes covering:
- Component parsing and serialization
- Property validation
- Recurrence rule expansion
- RFC compliance validation
- Round-trip data preservation
- Internationalization (UTF-8)
- Error handling

## Building

### Requirements

- Java 17 or higher
- Maven 3.8+

### Commands

```bash
# Build
mvn clean package

# Run tests
mvn test

# Generate Javadoc
mvn javadoc:javadoc

# Code coverage report
mvn test jacoco:report

# Security scan
mvn org.owasp:dependency-check-maven:check
```

### Artifacts

- `target/javacaltools-2.0.0.jar` - Main library
- `target/javacaltools-2.0.0-sources.jar` - Sources
- `target/javacaltools-2.0.0-javadoc.jar` - Documentation

## Contributing

Contributions are welcome. Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/name`)
3. Write tests for new functionality
4. Ensure all tests pass (`mvn test`)
5. Submit a pull request

### Development Guidelines

- Follow existing code style
- Add Javadoc for public methods
- Include RFC references where applicable
- Maintain backward compatibility

## License

[GNU Lesser General Public License v2.1](LICENSE)

This library includes bundled code from the Google RFC 2445 project. See [LICENSE-google-rfc-2445](LICENSE-google-rfc-2445) for details.

## Credits

- **Craig Knudsen** - Author and maintainer
- **Google RFC 2445 Project** - RRULE implementation

## Support

- **Issues**: [GitHub Issues](https://github.com/craigk5n/javacaltools/issues)
- **Email**: craig@k5n.us

## Changelog

### Version 2.0.0

- Java 17+ requirement
- RFC 9073 support (PARTICIPANT, VLOCATION, VRESOURCE, STYLED-DESCRIPTION)
- RFC 9074 support (PROXIMITY, ACKNOWLEDGED in VALARM)
- RFC 7986 VCALENDAR properties (NAME, DESCRIPTION, UID, URL, LAST-MODIFIED)
- RFC 5546 iTIP support with METHOD validation
- Improved text escaping per RFC 5545
- 716 tests with comprehensive coverage
- Performance improvements for large files
