# JavaCalTools - iCalendar Library for Java

[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Maven Central](https://img.shields.io/maven-central/v/us.k5n/javacaltools)](https://search.maven.org/artifact/us.k5n/javacaltools)
[![Build Status](https://img.shields.io/github/actions/workflow/status/k5n/javacaltools/unit-tests.yml)](https://github.com/k5n/javacaltools/actions)
[![codecov](https://codecov.io/gh/k5n/javacaltools/branch/master/graph/badge.svg)](https://codecov.io/gh/k5n/javacaltools)
[![Test Coverage](https://img.shields.io/badge/tests-376%20passing-brightgreen)](#testing)
[![RFC Compliance](https://img.shields.io/badge/RFC%20Compliance-5545%2C%207986%2C%209073%2C%209074%2C%205546-blue)](#compliance)

A comprehensive, RFC-compliant Java library for parsing, generating, and manipulating iCalendar (RFC 5545) data. Perfect for calendar applications, scheduling systems, and any Java project that needs to work with calendar data.

## 📋 Table of Contents

- [Features](#-features)
- [Quick Start](#-quick-start)
- [Installation](#-installation)
- [Usage](#-usage)
- [API Documentation](#-api-documentation)
- [RFC Compliance](#-rfc-compliance)
- [Testing](#-testing)
- [Contributing](#-contributing)
- [Building](#-building)
- [License](#-license)
- [Credits](#-credits)

## ✨ Features

- **Complete RFC 5545 Compliance**: Full support for iCalendar core specification
- **Modern Extensions**: RFC 7986, 9073, 9074, and RFC 5546 iTIP METHOD property support
- **Component Support**: VEVENT, VTODO, VJOURNAL, VFREEBUSY, VTIMEZONE, VALARM
- **Property Support**: All standard iCalendar properties with validation
- **Recurrence Rules**: Full RRULE support with complex patterns and exceptions
- **Timezone Handling**: VTIMEZONE component support with daylight saving
- **UTF-8 & Unicode**: Complete internationalization support
- **Streaming Parser**: Memory-efficient parsing of large calendar files
- **Validation**: Comprehensive validation with detailed error reporting
- **Java 17+**: Modern Java with minimal, secure dependencies
- **Thread-Safe**: Designed for concurrent applications

## 🚀 Quick Start

```java
import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;
import us.k5n.ical.Event;

// Parse iCalendar data
String icalData = """
    BEGIN:VCALENDAR
    VERSION:2.0
    PRODID:-//Example Corp.//Calendar 1.0//EN
    BEGIN:VEVENT
    UID:1234567890@example.com
    DTSTAMP:20230101T100000Z
    DTSTART:20230101T100000Z
    DTEND:20230101T110000Z
    SUMMARY:Team Meeting
    DESCRIPTION:Discuss project progress
    END:VEVENT
    END:VCALENDAR
    """;

ICalendarParser parser = new ICalendarParser();
parser.parse(icalData);

DefaultDataStore dataStore = (DefaultDataStore) parser.getDataStoreAt(0);
Event event = dataStore.getAllEvents().get(0);

System.out.println("Event: " + event.getSummary().getValue());
```

## 📦 Installation

### Maven
```xml
<dependency>
    <groupId>us.k5n</groupId>
    <artifactId>javacaltools</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Gradle
```gradle
implementation 'us.k5n:javacaltools:2.0.0'
```

### Manual Download
Download the latest JAR from [Maven Central](https://search.maven.org/artifact/us.k5n/javacaltools) or [GitHub Releases](https://github.com/k5n/javacaltools/releases).

## 💡 Usage

### Parsing iCalendar Data

```java
import us.k5n.ical.ICalendarParser;
import us.k5n.ical.DefaultDataStore;

ICalendarParser parser = new ICalendarParser();
parser.parse(icalString); // or parser.parse(reader)

DefaultDataStore dataStore = (DefaultDataStore) parser.getDataStoreAt(0);

// Access parsed components
List<Event> events = dataStore.getAllEvents();
List<Todo> todos = dataStore.getAllTodos();
List<Timezone> timezones = dataStore.getAllTimezones();
```

### Creating Calendar Components

```java
import us.k5n.ical.Event;
import us.k5n.ical.Date;
import us.k5n.ical.Summary;
import us.k5n.ical.Description;

Event event = new Event();
event.setUid("unique-id@example.com");
event.setDtstamp(new Date("20230101T100000Z"));
event.setStartDate(new Date("20230101T100000Z"));
event.setEndDate(new Date("20230101T110000Z"));
event.setSummary(new Summary("Team Meeting"));
event.setDescription(new Description("Discuss project progress"));

// Generate iCalendar format
String icalOutput = event.toICalendar();
```

### Working with Recurrence Rules

```java
import us.k5n.ical.Rrule;

// Parse RRULE
Rrule rrule = new Rrule("FREQ=WEEKLY;COUNT=10;BYDAY=MO,WE,FR");

// Generate occurrence dates
List<Date> occurrences = rrule.generateRecurrances(startDate, endDate);
```

### Timezone Support

```java
import us.k5n.ical.Timezone;
import us.k5n.ical.Event;

// Create timezone-aware event
Event event = new Event();
event.setStartDate(new Date("20230101T100000", "America/New_York"));
event.setEndDate(new Date("20230101T110000", "America/New_York"));
```

## 📚 API Documentation

- **Javadoc**: [Online API Documentation](https://javadoc.io/doc/us.k5n/javacaltools/latest/index.html)
- **Examples**: See the `src/test/java` directory for comprehensive usage examples
- **RFC References**: All classes and methods include RFC section references

### Core Classes

| Class | Description |
|-------|-------------|
| `ICalendarParser` | Main parser for iCalendar data |
| `DefaultDataStore` | Storage for parsed calendar components |
| `Event` | VEVENT component representation |
| `Todo` | VTODO component representation |
| `Journal` | VJOURNAL component representation |
| `Freebusy` | VFREEBUSY component representation |
| `Timezone` | VTIMEZONE component representation |
| `Valarm` | VALARM component representation |
| `Rrule` | Recurrence rule handling |
| `Property` | iCalendar property representation |

## 📋 RFC Compliance

JavaCalTools provides comprehensive support for iCalendar standards:

- ✅ **RFC 5545** - Internet Calendaring and Scheduling Core Object Specification (iCalendar)
- ✅ **RFC 7986** - New Properties for iCalendar (COLOR, IMAGE, CONFERENCE)
- ✅ **RFC 9073** - Event Publishing Extensions to iCalendar
- ✅ **RFC 9074** - VALARM Extensions for iCalendar
- ✅ **RFC 5546** - iTIP METHOD property support with validation (PUBLISH, REQUEST, REPLY, ADD, CANCEL, REFRESH, COUNTER, DECLINECOUNTER)

### Compliance Validation

The library includes 376 comprehensive tests covering:
- All RFC 5545 sections (3.1-4.0)
- Component validation and property handling
- Data type parsing and formatting
- Internationalization and Unicode support
- Edge cases and error conditions

## 🧪 Testing

Run the comprehensive test suite:

```bash
mvn test
```

### Test Coverage

- **376 total tests** with 100% pass rate
- **RFC compliance validation** across all supported specifications
- **Internationalization testing** with UTF-8 and Unicode support
- **Performance testing** for large calendar files
- **Edge case testing** for malformed data handling

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

1. **Prerequisites**: Java 11+, Maven 3.6+
2. **Clone the repository**:
   ```bash
   git clone https://github.com/craigk5n/javacaltools.git
   cd javacaltools
   ```
3. **Build the project**:
   ```bash
   mvn clean compile
   ```
4. **Run tests**:
   ```bash
   mvn test
   ```

### Code Style

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Include comprehensive JavaDoc comments
- Add unit tests for all new functionality

### Pull Request Process

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 🔨 Building

### Requirements

- Java 17 or higher
- Maven 3.8+ (for building)

### Build Commands

```bash
# Clean and build
mvn clean package

# Run tests
mvn test

# Generate documentation
mvn javadoc:javadoc

# Create source JAR
mvn source:jar
```

### Build Artifacts

- `target/javacaltools-2.0.0.jar` - Main JAR file
- `target/javacaltools-2.0.0-sources.jar` - Source JAR
- `target/javacaltools-2.0.0-javadoc.jar` - Javadoc JAR

### Security Scanning

```bash
# Run OWASP dependency vulnerability check
mvn org.owasp:dependency-check-maven:check
```

### Code Coverage

```bash
# Generate JaCoCo coverage report
mvn test jacoco:report
```

### Publishing to Maven Central

This project is published to Maven Central. To deploy a new version:

1. Create a release on GitHub
2. The CI/CD pipeline will automatically deploy to OSSRH/Maven Central
3. Requires GPG signing and OSSRH credentials in repository secrets

## 📄 License

This project is licensed under the **GNU Lesser General Public License v2.1** - see the [LICENSE](LICENSE) file for details.

The project bundles Google RFC 2445 Rrule code (now retired) - see [LICENSE-google-rfc-2445](LICENSE-google-rfc-2445) for details.

## 🙏 Credits

- **Craig Knudsen** - Original author and maintainer
- **Google RFC 2445 Project** - RRULE implementation (bundled)
- **Open Source Community** - Bug reports, feature requests, and contributions

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/craigk5n/javacaltools/issues)
- **Discussions**: [GitHub Discussions](https://github.com/craigk5n/javacaltools/discussions)
- **Email**: craig@k5n.us

## 🎯 Roadmap

See [ROADMAP.md](ROADMAP.md) for planned features and improvements.

### Recent Updates

- **v1.0.2**: Enhanced RFC compliance testing, internationalization support, bug fixes
- **Complete RFC Coverage**: 100% RFC 5545 compliance with extension support
- **Performance Improvements**: Streaming parser for large files
- **Unicode Support**: Full UTF-8 and internationalization

---

<p align="center">
  <b>JavaCalTools</b> - Making calendar data handling in Java simple and reliable
</p>

<p align="center">
  <a href="https://github.com/craigk5n/javacaltools">GitHub</a> •
  <a href="https://javadoc.io/doc/us.k5n/javacaltools">Documentation</a> •
  <a href="https://search.maven.org/artifact/us.k5n/javacaltools">Maven Central</a>
</p>