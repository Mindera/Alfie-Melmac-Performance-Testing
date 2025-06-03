import XCTest

// MARK: - DriverRunnerUITests

final class DriverRunnerUITests: XCTestCase {
    private var app: XCUIApplication!

    // MARK: setUp
    // Sets up the test environment and initializes the XCUIApplication with the bundle ID from environment variables.
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        print("🧪 setUp() called")

        let bundleID = ProcessInfo.processInfo.environment["COMMAND_BUNDLE_ID"] ?? "com.mindera.alfie.debug"
        app = XCUIApplication(bundleIdentifier: bundleID)
    }

    // MARK: testAppStartup
    // Main test entry point. Reads environment variables, runs the launch test, prints results, and fails if any step fails.
    func testAppStartup() {
        print("📦 All environment variables:")
        ProcessInfo.processInfo.environment.forEach { print("\($0.key): \($0.value)") }
        guard let elementID = ProcessInfo.processInfo.environment["TEST_ELEMENT"],
              let timeoutString = ProcessInfo.processInfo.environment["TEST_TIMEOUT"],
              let timeout = Double(timeoutString) else {
            XCTFail("Missing required TEST_ELEMENT or TEST_TIMEOUT")
            return
        }

        let thresholdType = ProcessInfo.processInfo.environment["TEST_THRESHOLD_TYPE"]
        let thresholdValue = ProcessInfo.processInfo.environment["TEST_THRESHOLD_VALUE"]?.toDouble()

        print("📦 Running with:")
        print("- elementID: \(elementID)")
        print("- timeout: \(timeout)")
        print("- thresholdType: \(thresholdType ?? "nil")")
        print("- thresholdValue: \(thresholdValue?.description ?? "nil")")

        let results = runAppLaunchTest(
            waitForElement: elementID,
            timeout: timeout,
            thresholdType: thresholdType,
            thresholdValue: thresholdValue
        )

        print("✅ Result JSON:")
        print(TestResultLogger.toJSON(from: results))

        if let failed = results.first(where: { !$0.success }) {
            XCTFail(failed.error ?? "Test failed")
        }
    }

    // MARK: runAppLaunchTest
    // Launches the app, waits for a UI element, measures launch duration, checks thresholds, and logs the result.
    private func runAppLaunchTest(
        waitForElement identifier: String,
        timeout: Double,
        thresholdType: String?,
        thresholdValue: Double?
    ) -> [TestStepResult] {
        TestResultLogger.shared.reset()

        let start = Date()
        app.launch()

        let element = app.descendants(matching: .any)[identifier]
        let appeared = element.waitForExistence(timeout: timeout)

        let duration = Int((Date().timeIntervalSince(start)) * 1000)
        var success = appeared
        var error: String? = appeared ? nil : "Element \(identifier) did not appear"

        if appeared, let type = thresholdType, let value = thresholdValue {
            switch type.uppercased() {
            case "MAX":
                if Double(duration) > value {
                    success = false
                    error = "Launch time \(duration)ms exceeded MAX threshold \(value)ms"
                }
            case "MIN":
                if Double(duration) < value {
                    success = false
                    error = "Launch time \(duration)ms below MIN threshold \(value)ms"
                }
            case "TARGET":
                if Double(duration) != value {
                    success = false
                    error = "Launch time \(duration)ms not equal to threshold \(value)ms"
                }
            default:
                break
            }
        }

        TestResultLogger.shared.log(
            step: TestStep(
                action: "measureStartup",
                target: identifier,
                value: "\(duration)",
                metric: "launchDuration",
                elementFound: appeared
            ),
            success: success,
            error: error
        )

        return TestResultLogger.shared.steps
    }
}

// MARK: - String Extension

extension String {
    // MARK: toDouble
    // Converts the string to a Double, if possible.
    func toDouble() -> Double? {
        return Double(self)
    }
}

// MARK: - Data Models

// MARK: TestStep
// Represents a single test step with action, target, value, metric, and elementFound flag.
struct TestStep: Decodable {
    let action: String
    let target: String?
    let value: String?
    let metric: String?
    let elementFound: Bool
}

// MARK: TestStepResult
// Represents the result of a test step, including success, error, and timestamp.
struct TestStepResult: Codable {
    let action: String
    let target: String?
    let value: String?
    let metric: String?
    let elementFound: Bool
    let success: Bool
    let error: String?
    let timestamp: String
}

// MARK: - Logger

// MARK: TestResultLogger
// Singleton logger for collecting and serializing test step results.
final class TestResultLogger {
    static let shared = TestResultLogger()
    private(set) var steps: [TestStepResult] = []

    // MARK: log
    // Logs a test step result with success, error, and timestamp.
    func log(step: TestStep, success: Bool, error: String?) {
        let formatter = ISO8601DateFormatter()
        steps.append(
            TestStepResult(
                action: step.action,
                target: step.target,
                value: step.value,
                metric: step.metric,
                elementFound: step.elementFound,
                success: success,
                error: error,
                timestamp: formatter.string(from: Date())
            ))
    }

    // MARK: reset
    // Clears all logged test step results.
    func reset() {
        steps.removeAll()
    }

    // MARK: toJSON
    // Serializes an array of TestStepResult to a pretty-printed JSON string.
    static func toJSON(from results: [TestStepResult]) -> String {
        let encoder = JSONEncoder()
        encoder.outputFormatting = .prettyPrinted
        guard let data = try? encoder.encode(results),
              let json = String(data: data, encoding: .utf8)
        else {
            return "[]"
        }
        return json
    }
}