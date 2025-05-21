import XCTest
import Swifter

final class DriverRunnerUITests: XCTestCase {
    private var app: XCUIApplication!
    private var server: HttpServer?
    private var endExpectation: XCTestExpectation?

    // MARK: - XCTest Lifecycle

    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        print("🧪 setUp() called")

        let bundleID = ProcessInfo.processInfo.environment["COMMAND_BUNDLE_ID"] ?? "com.mindera.alfie.debug"
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
            .ok(.text("OK"))
        }

        server?["/test-launch"] = { request in
            defer {
                self.endExpectation?.fulfill()
            }

            guard let elementID = request.queryParams.first(where: { $0.0 == "element" })?.1 else {
                XCTFail("❌ Missing 'element' query parameter")
                return .badRequest(.text("Missing 'element' query parameter"))
            }

            var results: [TestStepResult] = []
            DispatchQueue.main.sync {
                results = self.runAppLaunchTest(waitForElement: elementID)
            }

            return .ok(.text(TestResultLogger.toJSON(from: results)))
        }


        server?["/hierarchy"] = { _ in
            .ok(.text(self.app.debugDescription))
        }

        server?["/finish"] = { _ in
            DispatchQueue.main.sync {
                self.endExpectation?.fulfill()
            }
            return .ok(.text("Test manually completed"))
        }

        do {
            try server?.start(4000)
            print("🚀 Server started on port 4000")
        } catch {
            XCTFail("❌ Failed to start server: \(error.localizedDescription)")
        }
    }

    // MARK: - App Test Logic

    private func runAppLaunchTest(waitForElement identifier: String) -> [TestStepResult] {
        TestResultLogger.shared.reset()

        let start = Date()
        app.launch()

        let element = app.descendants(matching: .any)[identifier]
        let appeared = element.waitForExistence(timeout: 10)

        let duration = Date().timeIntervalSince(start)
        let success = appeared
        let error = appeared ? nil : "Element \(identifier) did not appear"

        if !appeared {
            XCTFail("❌ Element '\(identifier)' did not appear within timeout.")
        }

        TestResultLogger.shared.log(
            step: TestStep(
                action: "measureStartup",
                target: identifier,
                value: String(format: "%.3f", duration),
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
        steps.append(TestStepResult(
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
              let json = String(data: data, encoding: .utf8) else {
            return "[]"
        }
        return json
    }
}
