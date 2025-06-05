<img width=100% src="https://capsule-render.vercel.app/api?type=waving&height=120&color=3A4A51&reversal=true&textBg=false&section=header"/>

# Melmac Performance Testing

## 1. Description of the Project

As part of [Mindera's](https://mindera.com) internship program, we are developing a framework for testing the performance of mobile applications. The goal is to create a robust and industry-aligned solution that will help evaluate and optimize the performance of mobile apps. Over the course of this internship, we will focus on identifying key performance metrics, developing a testing framework, and analyzing the results to propose performance enhancements. Our work will contribute to Mindera’s ongoing efforts to ensure that deliverables meet high industry standards and will provide valuable insights for future performance testing initiatives. This repository serves as the central hub for our internship project, containing initial source code, project management tools, and documentation to guide us through the process of building and refining the performance testing framework.

## 2. Planning and Technical Documentation

[_`Planning and Technical Documentation`_](docs/readme.md)

## 3. Necessary Tools

To set up and work on this project, ensure you have the following tools and dependencies installed:

### 3.1 Java Development Kit (JDK)
- **Requirement:** JDK 17 or higher (the backend is developed in Kotlin).
- **Download:** [Adoptium](https://adoptium.net/)

### 3.2 Gradle
- **Purpose:** Build automation for compiling, running, and testing the project.
- **Installation:**  
  - Install manually ([Guide](https://gradle.org/install/)), or use the project’s Gradle wrapper (`./gradlew`).

### 3.3 Android SDK & ADB (Android Debug Bridge)
- **Purpose:** Required for running performance tests on Android devices or emulators.
- **Notes:**  
  - Ensure `adb` is available in your system `PATH`.
  - Install via [Android Studio](https://developer.android.com/studio)
  - Install platform tools, system images, and emulator support.

### 3.4 Xcode & Command Line Tools (macOS only)
- **Purpose:** Required for iOS performance testing.
- **Notes:**  
  - Uses `xcodebuild`, `simctl`, and other Xcode utilities.
  - Install Command Line Tools: `xcode-select --install`
  - Download Xcode from the [Mac App Store](https://apps.apple.com/us/app/xcode/id497799835?mt=12)
  - XCUI tests are written in Swift and executed via `xcodebuild test-without-building`.

### 3.5 Mobile Device or Emulator/Simulator
- **Android:** Real device or emulator.
- **iOS:** Real device or simulator (macOS required).

### 3.6 API Testing Tools (Optional)
- **Purpose:** For testing and invoking API endpoints during development.
- **Examples:** [curl](https://curl.se/) or [Postman](https://www.postman.com/)

## 3. How to Generate the Gradle Wrapper

To generate the Gradle wrapper, you can run the following command in the root directory of your project:

```bash
./gradlew wrapper --gradle-version=7.6.1
```

This command generates the Gradle wrapper files, allowing you to run Gradle tasks without installing Gradle globally. The `--gradle-version` flag is optional - if omitted, the wrapper will use the version specified in your project's `gradle/wrapper/gradle-wrapper.properties` file or the default version for your current Gradle installation.

## 4. How to Build

To build the project, you can use the Gradle wrapper. Run the following command in the root directory of your project:

```bash
./gradlew build
```

This command compiles the source code, runs tests, and packages the application into a JAR file. If you want to skip tests during the build process, you can use:

```bash
./gradlew build -x test
```

## 5. How to Execute Tests

To execute the tests, you can use the Gradle wrapper with the `test` task. Run the following command in the root directory of your project:

```bash
./gradlew test
```

This command runs all the tests defined in your project. If you want to run a specific test class or method, you can use the `--tests` option:

```bash
./gradlew test --tests "com.example.YourTestClass"
```

## 6. How to Run 
To run the application, you can use the Gradle wrapper with the `run` task. Run the following command in the root directory of your project:

```bash
./gradlew run
```
This command starts the application, allowing you to interact with it. 

## 7. How to Call Endpoints

To call the endpoints of your application, you can use tools like `curl`, Postman, or any HTTP client library in your programming language. The endpoints are defined in your application code, and you can access them using the appropriate HTTP methods (GET, POST, PUT, DELETE, etc.) along with the correct URL paths.

**Example using `curl`:**

```bash
curl -X GET http://localhost:8080/api/endpoint
```

Replace `GET` with the desired HTTP method and update the URL to match your application's endpoint.

<img width=100% src="https://capsule-render.vercel.app/api?type=waving&height=120&color=3A4A51&reversal=true&textBg=false&section=footer"/>