import Swifter
import XCTest

final class DriverRunnerUITests: XCTestCase {
    private var app: XCUIApplication!
    private var server: HttpServer?
    private var endExpectation: XCTestExpectation?

    // MARK: - XCTest Lifecycle

    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        print("🧪 setUp() called")

        let bundleID =
            ProcessInfo.processInfo.environment["COMMAND_BUNDLE_ID"] ?? "com.mindera.alfie.debug"

        app = XCUIApplication(bundleIdentifier: bundleID)

        endExpectation = XCTestExpectation(description: "Waiting for test request")
        startServer()
    }

    override func tearDown() {
        server?.stop()
        print("🛑 Server stopped")
        super.tearDown()
    }

    // MARK: - XCTest Entry Point

    func testWaitForTestRequests() {
        print("🕹️ Starting testWaitForTestRequests")
        print("✅ App: \(app.debugDescription)")
        wait(for: [endExpectation!], timeout: 300)
    }

    // MARK: - HTTP Server Setup

    private func startServer() {
        server = HttpServer()

        server?["/health"] = { _ in
            HttpResponse.ok(.text("OK"))
        }

        server?["/test-launch"] = { request in
            guard let elementID = request.queryParams.first(where: { $0.0 == "element" })?.1 else {
                XCTFail("❌ Missing 'element' query parameter")
                return HttpResponse.badRequest(.text("Missing 'element' query parameter"))
            }

            guard let timeoutString = request.queryParams.first(where: { $0.0 == "timeout" })?.1,
                let timeout = Double(timeoutString)
            else {
                XCTFail("❌ Missing or invalid 'timeout' query parameter")
                return HttpResponse.badRequest(
                    .text("Missing or invalid 'timeout' query parameter"))
            }

            // Threshold support
            let thresholdType = request.queryParams.first(where: { $0.0 == "thresholdType" })?.1
            let thresholdValueString = request.queryParams.first(where: { $0.0 == "thresholdValue" }
            )?.1
            let thresholdValue = thresholdValueString.flatMap { Double($0) }

            var results: [TestStepResult] = []
            var launchFailed = false
            var failureReason: String? = nil

            DispatchQueue.main.sync {
                results = self.runAppLaunchTest(
                    waitForElement: elementID,
                    timeout: timeout,
                    thresholdType: thresholdType,
                    thresholdValue: thresholdValue
                )

                // Check for any failed step
                if let failed = results.first(where: { !$0.success }) {
                    launchFailed = true
                    failureReason = failed.error
                }
            }

            let responseText = TestResultLogger.toJSON(from: results)

            // Send HTTP response **before** failing
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                if launchFailed {
                    XCTFail(failureReason ?? "Unknown failure")
                }
                self.endExpectation?.fulfill()
            }

            return HttpResponse.ok(.text(responseText))

        }

        server?["/hierarchy"] = { _ in
            HttpResponse.ok(.text(self.app.debugDescription))
        }

        server?["/finish"] = { _ in
            DispatchQueue.main.sync {
                self.endExpectation?.fulfill()
            }
            return HttpResponse.ok(.text("Test manually completed"))
        }

        do {
            try server?.start(4000)
            print("🚀 Server started on port 4000")
        } catch {
            XCTFail("❌ Failed to start server: \(error.localizedDescription)")
        }
    }

    // MARK: - App Test Logic

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

        // Threshold evaluation (if both are present and element appeared)
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
                // Unknown type, do not fail
                break
            }
        }

        TestResultLogger.shared.log(
            step: TestStep(
                action: "measureStartup",
                target: identifier,
                value: "\(duration)",
                metric: "launchDuration"
            ),
            success: success,
            error: error
        )

        return TestResultLogger.shared.steps
    }
}

// MARK: - Data Models

struct TestStep: Decodable {
    let action: String
    let target: String?
    let value: String?
    let metric: String?
}

struct TestStepResult: Codable {
    let action: String
    let target: String?
    let value: String?
    let metric: String?
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
