# Contributing to JavaCalTools

Thank you for your interest in contributing to JavaCalTools! This document provides guidelines and information for contributors.

## 📋 Table of Contents

- [How to Contribute](#how-to-contribute)
- [Development Setup](#development-setup)
- [Code Style](#code-style)
- [Testing](#testing)
- [Submitting Changes](#submitting-changes)
- [Reporting Bugs](#reporting-bugs)
- [Documentation](#documentation)
- [License](#license)

## 🚀 How to Contribute

We welcome contributions in various forms:

- **Bug fixes** - Fix issues in the issue tracker
- **Feature requests** - Implement new RFC features or enhancements
- **Documentation** - Improve docs, add examples, or fix typos
- **Tests** - Add test cases or improve test coverage
- **Code review** - Review pull requests from other contributors

### First Time Contributors

If you're new to open source or this project:

1. Look for issues labeled `good first issue` or `help wanted`
2. Comment on the issue to indicate you're working on it
3. Ask questions in the issue comments or GitHub Discussions
4. Submit your pull request when ready

## 🛠️ Development Setup

### Prerequisites

- **Java 17 or higher** (for compilation and runtime)
- **Maven 3.8+** (for building and dependency management)
- **Git** (for version control)
- **IDE** (IntelliJ IDEA, Eclipse, VS Code, or similar with Java support)

### Clone and Setup

1. **Fork the repository** on GitHub
2. **Clone your fork**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/javacaltools.git
   cd javacaltools
   ```

3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/craigk5n/javacaltools.git
   ```

4. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

5. **Build the project**:
   ```bash
   mvn clean compile
   ```

6. **Run tests**:
   ```bash
   mvn test
   ```

### Development Workflow

1. **Keep your branch updated**:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Make your changes** following the code style guidelines

3. **Add tests** for new functionality

4. **Run the full test suite**:
   ```bash
   mvn clean test
   ```

5. **Check code coverage** (optional):
   ```bash
   mvn test jacoco:report
   ```

## 🎨 Code Style

JavaCalTools follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with some project-specific adaptations:

### Java Style Rules

- **Indentation**: 2 spaces (no tabs)
- **Line length**: 100 characters maximum
- **Braces**: Always use braces for control structures
- **Naming**: camelCase for methods/variables, PascalCase for classes
- **Imports**: Organize imports alphabetically, separate java.* from others
- **Comments**: Use JavaDoc for public APIs, regular comments for implementation details

### Example Code Style

```java
/**
 * Represents an iCalendar property with validation.
 *
 * @author Your Name
 */
public class Property {
  private final String name;
  private String value;

  /**
   * Creates a new property with the given name and value.
   *
   * @param name the property name (required)
   * @param value the property value
   * @throws IllegalArgumentException if name is null or empty
   */
  public Property(String name, String value) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Property name cannot be null or empty");
    }
    this.name = name.trim();
    this.value = value != null ? value.trim() : "";
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value != null ? value.trim() : "";
  }
}
```

## 🧪 Testing

### Test Structure

- **Unit tests**: `src/test/java/us/k5n/ical/` - Test individual classes and methods

### Writing Tests

1. **Use JUnit 5** for all test cases
2. **Follow naming convention**: `ClassNameTest.java`
3. **Test method naming**: `testFeatureDescription()`
4. **Use descriptive assertions** with clear failure messages
5. **Test edge cases** and error conditions
6. **Include RFC references** in test comments

### Test Coverage

- **Maintain high coverage**: Aim for 80%+ line coverage
- **Test public APIs** thoroughly
- **Test error conditions** and exception handling
- **Test RFC compliance** with sample data

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PropertyTest

# Run specific test method
mvn test -Dtest=PropertyTest#testValidProperty

# Run with coverage report
mvn test jacoco:report
```

## 📝 Submitting Changes

### Pull Request Process

1. **Ensure your branch is updated** with the latest upstream changes
2. **Run the full test suite** and ensure all tests pass
3. **Update documentation** if needed (README, JavaDoc, etc.)
4. **Write a clear commit message**:
   ```
   feat: add PARTICIPANT component support

   - Implement PARTICIPANT component parsing
   - Add PARTICIPANT-TYPE property validation
   - Add comprehensive test coverage

   Closes #123
   ```

5. **Create a pull request**:
   - Use a descriptive title
   - Reference related issues
   - Provide context and testing details
   - Request review from maintainers

### Commit Guidelines

- **Atomic commits**: Each commit should do one thing
- **Clear messages**: Use imperative mood ("Add feature" not "Added feature")
- **Reference issues**: Use "Closes #123" or "Fixes #456"

## 🐛 Reporting Bugs

### Bug Report Guidelines

When reporting bugs, please include:

1. **Clear title** describing the issue
2. **Detailed description** of the problem
3. **Steps to reproduce**:
   - Sample iCalendar data
   - Code snippets
   - Expected vs actual behavior
4. **Environment details**:
   - Java version
   - Maven version
   - OS and version
5. **Error logs** or stack traces
6. **RFC references** if applicable


## 📚 Documentation

### JavaDoc Requirements

- **All public classes/methods** must have JavaDoc
- **Parameter descriptions** for all parameters
- **Return value descriptions** for non-void methods
- **Exception documentation** with `@throws`
- **RFC references** where applicable

### Documentation Updates

When making changes:

1. **Update JavaDoc** for modified APIs
2. **Update README.md** for new features
3. **Add examples** in the `/examples` directory
4. **Update RFC compliance** information

### Building Documentation

```bash
# Generate JavaDoc
mvn javadoc:javadoc

# View documentation
open target/site/apidocs/index.html
```

## 📄 License

By contributing to JavaCalTools, you agree that your contributions will be licensed under the same license as the project: **GNU Lesser General Public License v2.1**.

## 📞 Getting Help

- **GitHub Issues**: For bugs and feature requests
- **GitHub Discussions**: For questions and general discussion

