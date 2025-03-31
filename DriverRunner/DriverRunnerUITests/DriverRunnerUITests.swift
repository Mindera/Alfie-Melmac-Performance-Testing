import XCTest

class DriverRunnerUITests: XCTestCase {
    let app = XCUIApplication()

    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        app.launch()
    }

    func testDriver() {
        guard let command = ProcessInfo.processInfo.environment["COMMAND"] else {
            XCTFail("COMMAND environment variable not set")
            return
        }

        executeCommand(command)
    }

    func executeCommand(_ command: String) {
        switch command {
        case "tapButton":
            let button = app.buttons["ButtonIdentifier"]
            XCTAssertTrue(button.waitForExistence(timeout: 5), "Button did not appear in time")
            button.tap()
        default:
            XCTFail("Unknown command: \(command)")
        }
    }
}
