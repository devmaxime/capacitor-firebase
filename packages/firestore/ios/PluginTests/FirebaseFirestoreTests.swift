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
    
    func testTimestampConversion() {
        // Test ISO 8601 string conversion
        let isoString = "2025-09-23T17:18:38.749Z"
        let convertedISO = FirebaseFirestoreHelper.convertTimestampValue(isoString)
        XCTAssertTrue(convertedISO is Timestamp, "ISO string should be converted to Timestamp")
        
        // Test Unix timestamp in milliseconds conversion
        let unixTimestamp: Int64 = 1758388718749
        let convertedUnix = FirebaseFirestoreHelper.convertTimestampValue(NSNumber(value: unixTimestamp))
        XCTAssertTrue(convertedUnix is Timestamp, "Unix timestamp should be converted to Timestamp")
        
        // Test regular string (should not be converted)
        let regularString = "not_a_timestamp"
        let notConverted = FirebaseFirestoreHelper.convertTimestampValue(regularString)
        XCTAssertTrue(notConverted is String, "Regular string should not be converted")
        XCTAssertEqual(notConverted as? String, regularString)
        
        // Test regular number (should not be converted)
        let regularNumber = 42
        let notConvertedNumber = FirebaseFirestoreHelper.convertTimestampValue(regularNumber)
        XCTAssertEqual(notConvertedNumber as? Int, regularNumber, "Regular number should not be converted")
    }
}
