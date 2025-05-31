import XCTest

final class DriverRunnerUITests: XCTestCase {
    private var app: XCUIApplication!

    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        print("🧪 setUp() called")

        let bundleID = ProcessInfo.processInfo.environment["COMMAND_BUNDLE_ID"] ?? "com.mindera.alfie.debug"
        app = XCUIApplication(bundleIdentifier: bundleID)
    }

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

extension String {
    func toDouble() -> Double? {
        return Double(self)
    }
}

// MARK: - Data Models

struct TestStep: Decodable {
    let action: String
    let target: String?
    let value: String?
    let metric: String?
    let elementFound: Bool
}

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

final class TestResultLogger {
    static let shared = TestResultLogger()
    private(set) var steps: [TestStepResult] = []

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

    func reset() {
        steps.removeAll()
    }

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
