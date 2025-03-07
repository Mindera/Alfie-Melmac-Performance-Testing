<img width=100% src="https://capsule-render.vercel.app/api?type=waving&height=120&color=4E1764"/>

# Supplementary Specification (FURPS+)

## Functionality

_Specifies functionalities that:_

- Execute **mobile performance tests** using **ADB (Android) & XCUI (iOS)**.
- Process test results and generate reports using **Kotlin**.
- Support **logging, reporting, and automation**.

### **Startup Time Testing (Current Focus)**
- Executes startup time measurements via:
  - **ADB shell commands** (Android)
  - **XCUI test commands** (iOS)
- Measures:
  - **Cold start** – launch from terminated state.
  - **Warm start** – launch from background (app still in memory).
  - **Hot start** – launch when the app is still in foreground but needs to reload UI.
- Captures timestamps for:
  - **Process start**
  - **First frame rendered**
  - **App ready for interaction**
- Logs raw execution data for processing.

### **Data Processing & Report Generation**
- Kotlin processes raw test data.
- Generates **structured reports** with:
  - **Average startup time**
  - **Min/max startup times**
  - **Standard deviation**
- Supports **exporting reports in a suitable format** (e.g., JSON, CSV, or HTML – to be decided).

### **Developer Alerts (To Be Defined)**
- Developers **must be notified** of performance test results.
- The method of notification is **yet to be defined**, but may include:
  - **Email alerts**
  - **PR comments**
  - **Other automated messaging systems**
- The final notification method will be decided in future iterations.

### **Database & Backend for Test Logging (Under Discussion)**
- There is an **ongoing discussion** about introducing a **database and backend** to:
  - Store and track **historical performance test data**.
  - Enable **advanced data analysis and comparisons**.
  - Provide a **centralized dashboard for test results**.
- No final decision has been made yet.

### **Potential Future Performance Metrics**
- The framework **may** be expanded beyond startup time testing to include:
  - **Memory usage tracking**
  - **CPU load monitoring**
  - **Network performance analysis**
  - **UI responsiveness tests**
- These features are **not confirmed** and will depend on project needs and feasibility.

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
- Focused on **startup time now**, but expandable.
- Kotlin ensures **efficient data processing**.

---

## **Supportability**
- **Kotlin** processes data and generates reports.
- Uses **ADB (Android)** and **XCUI (iOS)** for test execution.
- Designed for **modularity** to support future features.

---

## **+ (Additional Constraints)**

### **Design Constraints**
| Subcategory | Category | Description |
| --- | --- | --- |
| Programming Languages | Kotlin | Used for data processing and reporting. |
| Tools | ADB & XCUI | Primary tools for executing mobile performance tests. |

### **Implementation Constraints**
| Subcategory | Category | Description |
| --- | --- | --- |
| Tools | ADB | Executes Android performance tests. |
| Tools | XCUI | Executes iOS performance tests. |

### **Interface Constraints**
- Provides CLI commands for test execution.
- Exports reports in **JSON, CSV, or HTML** (format TBD).

### **Physical Constraints**
- Runs on developer machines and CI/CD environments.

---

<img width=100% src="https://capsule-render.vercel.app/api?type=waving&height=120&color=4E1764&section=footer"/>