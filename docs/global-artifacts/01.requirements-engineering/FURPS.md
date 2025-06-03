## Supplementary Specification (FURPS+)

## Functionality

_Specifies functionalities that:_

- Execute **mobile performance tests** using **ADB (Android)** & **XCUI (iOS)**.
- Process test results and generate reports using **Kotlin**.
- Support **logging, reporting, and automation**.

### **Startup Time Testing**
- Executes startup time measurements via:
  - **ADB shell commands** (Android)
  - **Xcode tools including XCTest/XCUI Test commands written in Swift** (iOS)
- Measures:
  - **Cold start** – launch from terminated state.
- Captures:
  - **Time until a specified UI element is detected (used as readiness indicator)**.
- Logs raw execution data for processing.

### **Data Processing & Report Generation**
- Kotlin processes raw test data.
- Generates **structured reports** with:
  - **Test execution details**
  - **Performance metrics**
  - **Test results**
- Supports multiple output formats:
  - **CLI output**
  - **Summary reports in .MD (Markdown) format**

### **Database Model for Performance Tracking**
- A **database model** will be designed and implemented to track:
  - **Test Suites and Test Cases Configuration**
  - **Test execution history**
  - **Performance trends over time**
  - **Detailed logs of each test iteration**
  - **Aggregated analytics for deeper insights**
- The database will support **queries and filtering** for historical analysis.

### **Developer Alerts**
- Developers **must be notified** of performance test results.
- Notification methods include:
  - **Email alerts**
  - **PR comments**
  - **Summary reports in CI/CD pipelines**

### **Automation & CI/CD Integration**
- CLI-based execution.
- Supports running in **automated test pipelines**.

---

## **Usability**
- Command-line interface for test execution.
- Reports are **formatted for easy analysis**.

---

## **Reliability**
- Runs multiple iterations for consistent results.
- Handles **edge cases** like test failures and data corruption.

---

## **Performance**
- Focused on **startup time**.
- Kotlin ensures **efficient data processing**.

---

## **Supportability**
- **Kotlin** processes data.
- Uses **ADB (Android)** and **XCode Tools (iOS)** for test execution.
- **Swift** is used for writing XCUI test commands for iOS device interaction.
- Designed for **modularity** to support future features.

---

## **+ (Additional Constraints)**

### **Design Constraints**
| Subcategory          | Category | Description                                   |
| -------------------- | -------- | --------------------------------------------- |
| Programming Languages | Kotlin   | Used for data processing and reporting.       |
| Programming Languages | Swift    | Used for XCUI test commands for iOS devices.  |
| Tools                | ADB | Primary tool for executing mobile performance tests on Android devices. |
| Tools                | XCode Tools | Primary tools for executing mobile performance tests on iOS devices. |

### **Implementation Constraints**
| Subcategory | Category | Description                                   |
| ----------- | -------- | --------------------------------------------- |
| Tools       | ADB      | Executes Android performance tests.           |
| Tools       | XCode Tools | Executes iOS performance tests.               |
| Storage     | SQL Server | Tracks and logs test results for historical analysis. |

### **Interface Constraints**
- CLI and HTTP endpoints for launching and managing tests.
- Compatible with CI environments for automated execution.
- Database allows querying and analytics.

### **Physical Constraints**
- Must run on macOS (Android and iOS) and Linux/Windows/macOS (Android only) environments.
- Requires device access (USB or simulator/emulator).
- Database must handle **large volumes of test data efficiently**.