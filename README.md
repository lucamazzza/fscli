<p align='center'>

<img src="https://capsule-render.vercel.app/api?type=venom&color=a86b23&height=200&section=header&text=fscli&fontSize=90&fontColor=ffffff&animation=fadeIn&fontAlignY=35&desc=FileSystem%20Command%20Line%20Interface%20Simulator&descAlignY=61&descAlign=50"/>


</p>

<p align='center'>

<img src="https://img.shields.io/badge/17-%23a86b23?style=for-the-badge&logo=java&logoColor=%23a86b23&labelColor=%23ffffff&color=%23a86b23"/>
<img src="https://img.shields.io/github/v/release/lucamazzza/fscli?include_prereleases&sort=date&display_name=release&style=for-the-badge&label=%20"/>

</p>

What is it?
-----------

FSCLI is a Java-based file system command line interface that provides core
functionalities for file system operations through a modern graphical user
interface and a robust backend architecture. It includes a command execution
engine, an in-memory file system implementation, and a JavaFX-based client
application that demonstrates the engine capabilities.

The project is structured as a backend library (core engine) and a frontend
application, both implemented in Java. The backend handles all file system
operations, commands, and business logic, while the frontend provides a
user-friendly interface for interacting with the file system.

The backend is designed with extensibility in mind, allowing developers to
add new commands and implement alternative file system strategies without
modifying existing code. The frontend uses an event-driven architecture for
loose coupling between components.

On what hardware does it run?
-----------------------------

The application has been developed and tested on macOS, Windows and Linux 
platforms, ensuring broad compatibility across different operating systems.
The software requires very few prerequisites:

- Java 17 or later (JDK)
- Maven 3.6 or later (for building)

The application is platform-independent and will run on any system with a
compatible Java Virtual Machine. No additional native dependencies are
required.

Addition of Features
--------------------

The project is designed to be easily extended with new functionality. There
are two primary ways to contribute:

### Adding New Commands

Commands are the primary way users interact with the file system. New
commands can be added without modifying existing code.

Steps:
1. Create a new class extending AbstractCommand in the core/command/
   package
2. Register the command in di/CommandModule.java using the Multibinder
3. Add localized messages in resources/messages_backend.properties

For detailed implementation guide, see ARCHITECTURE.md section
"Adding New Commands to the Codebase"

### Adding New FileSystem Strategies

Different storage strategies can be implemented by creating a new class
implementing the FileSystem interface. This allows plugging in different
backends without changing any commands.

Strategies can include:
- Database-backed filesystems
- Cloud storage integration
- Compressed file access
- Network-based filesystems
- Custom storage solutions

Steps:
1. Create a new class implementing FileSystem interface in core/
2. Implement all required methods for file operations
3. Optionally register in di/BackendModule.java
4. Test using existing commands - they work automatically

### Design Principles

All extensions follow these principles:
- Dependency Injection (Guice) for loose coupling
- Strategy Pattern for pluggable implementations
- Observer Pattern for event-driven communication
- Interface-based design for extensibility
- Minimal impact on existing code


Installation
------------

To compile the application, you need to have Maven and Java 17 properly 
installed on your system.

### Prerequisites

- Java 17 or later (JDK)
- Maven 3.6 or later
- Git (for cloning the repository)

### Building the Project

Building is straightforward using Maven:

```bash
# Builds both the backend library and frontend application,
# runs all tests, and creates deployable artifacts.
mvn clean install

# Builds without running tests (faster for development).
mvn clean install -DskipTests

# Removes all build artifacts and compiled classes.
mvn clean

# Executes unit tests for both backend and frontend modules.
mvn test
```

If something goes wrong
-----------------------

### Build Issues

Check that:

- Java 17 or later is properly installed and in your PATH
- Maven 3.6 or later is properly installed and in your PATH
- All Maven plugins are up-to-date by running: mvn clean install
- Your system has sufficient disk space for Maven cache
- Internet connection is available for downloading dependencies

### Compilation Errors

Ensure that:

- The backend module is built before the frontend (frontend depends
  on backend)
- All dependencies can be downloaded from Maven Central Repository
- Your project uses Java source encoding UTF-8 (already configured
  in pom.xml)
- No conflicting Java versions are active

### Runtime Errors

If the application fails to run:

- Verify Java version: java -version
- Check that frontend JAR was created in target/
- Ensure JavaFX libraries are available (included in dependencies)
- Review console output for specific error messages

### Test Failures

If tests fail:

- Run tests individually: mvn test -Dtest=TestClassName
- Check test output in target/surefire-reports/
- Ensure no other instances of the application are running
- Try: mvn clean test to start fresh

### FileSystem Operations Errors

When implementing new FileSystem strategies:

- All methods must throw FSException on error
- Use specific exception types (NotFoundException, AlreadyExistsException)
- Test with built-in commands to verify interface compliance
- Check path resolution logic carefully
- Verify node creation and deletion logic

### Common Issues and Solutions

"Command not found"

- Verify command is registered in CommandModule.java
- Check command name matches in execute() and getName()
- Ensure command messages are in messages_backend.properties

"Path not found"

- Verify path resolution logic in your FileSystem implementation
- Check that parent directories exist
- Ensure PathResolver is used correctly

"ClassNotFoundException"

- Run: mvn clean install to rebuild
- Verify all dependencies are in pom.xml
- Check that classes are in correct packages

Build hangs

- Maven might be waiting for input
- Try: mvn clean install --batch-mode
- Check internet connection for downloading dependencies

Copyright
---------

Authors:

- Luca Mazza             (C) SUPSI [luca.mazza@supsi.ch]
- Vasco Silva Pereira    (C) SUPSI [vasco.silvapereira@supsi.ch]
- Roeld Hoxha            (C) SUPSI [roeld.hoxha@supsi.ch]

This is an educational project developed at SUPSI (Scuola Universitaria
Professionale della Svizzera Italiana).
