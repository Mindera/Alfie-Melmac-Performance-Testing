# Domain Model

This document describes the core domain concepts used in the Melmac Performance Testing Framework.  
These concepts are shared across all layers and define the vocabulary of the system.

---

## Core Concepts

### **App**
Represents a mobile application to be tested. An app has one or more versions.

### **App Version**
A specific release or build of an App. Used to ensure test reproducibility across builds.

### **Device**
Represents a physical or virtual mobile device (iOS or Android) on which tests are executed.

### **Operative System / OS Version**
Identifies the operating system and its version running on a device (e.g., Android 14, iOS 17.1).

### **Metric**
A measurable aspect of performance (e.g., Startup Time). Each metric can have multiple outputs.

### **Metric Output**
Represents the measurable result of a Metric (e.g., time to first frame in seconds).

### **Execution Type**
Describes the mode of execution (e.g., Cold Start). Execution Types can have custom parameters.

### **Execution Type Parameter**
A named input required by a given execution type (e.g., element to wait for, timeout).

### **Test Plan**
A definition of a single test scenario. Includes app, device, metric, execution type and parameter values.

### **Test Plan Version**
A versioned snapshot of a Test Plan used for repeatable execution. Links to app version and device.

### **Test Execution**
The actual run of a Test Plan Version. Collects metric output results.

### **Test Metric Output Result**
A recorded value from a test execution for a specific Metric Output.

### **Test Suite**
A collection of Test Plans, used for grouped and repeatable execution.

### **Test Suite Version**
A versioned structure of a Test Suite, containing an ordered list of Test Plan Versions.

### **Suite Execution**
An execution of a full Test Suite Version. Triggers the execution of all contained Test Plans.

### **Test Threshold**
Defines target or acceptable values for metric outputs. Used to determine pass/fail results.

### **Threshold Type**
Specifies the type of evaluation applied to a threshold (e.g., target value, maximum allowed).

---

## Notes

- The domain model is directly implemented via the relational database structure (see [Database Documentation](./DATABASE.md)).
- Concepts are linked through foreign keys and maintained consistently using validation logic and triggers.