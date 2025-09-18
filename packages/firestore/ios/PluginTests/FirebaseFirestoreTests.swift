import XCTest
import FirebaseFirestore
@testable import Plugin

class FirebaseFirestoreTests: XCTestCase {

    func testEcho() {
        // This is an example of a functional test case for a plugin.
        // Use XCTAssert and related functions to verify your tests produce the correct results.

        let implementation = FirebaseFirestore()
        let value = "Hello, World!"
        let result = implementation.echo(value)

        XCTAssertEqual(value, result)
    }
    
    func testTimestampHandling() {
        // Test that Firestore Timestamps are properly converted to string format
        let testData: [String: Any] = [
            "timestamp_field": Timestamp(seconds: 1757689490, nanoseconds: 446000000),
            "string_field": "test_string",
            "number_field": 42
        ]
        
        let result = FirebaseFirestoreHelper.createJSObjectFromHashMap(testData)
        
        XCTAssertNotNil(result)
        XCTAssertEqual(result?["timestamp_field"] as? String, "Timestamp(seconds=1757689490, nanoseconds=446000000)")
        XCTAssertEqual(result?["string_field"] as? String, "test_string")
        XCTAssertEqual(result?["number_field"] as? Int, 42)
    }
}
