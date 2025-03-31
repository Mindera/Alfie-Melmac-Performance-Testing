import XCTest

class XCUITestWrapper: XCTestCase {
    func testElementPresence() {
        let app = XCUIApplication(bundleIdentifier: "com.mindera.alfie.debug")
        app.launch()
        
        // Retrieve the target resource ID from the environment variable
        guard let targetResourceId = ProcessInfo.processInfo.environment["TARGET_RESOURCE_ID"] else {
            XCTFail("TARGET_RESOURCE_ID environment variable not set")
            return
        }
        
        // Check for presence of an element by resource ID
        let targetElement = app.buttons[targetResourceId]
        XCTAssertTrue(targetElement.exists, "Target element '\(targetResourceId)' is not present!")
    }
}