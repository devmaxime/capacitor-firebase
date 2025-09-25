import Foundation
import FirebaseFirestore
import Capacitor

public class FirebaseFirestoreHelper {
    
    /**
     * Converts a JavaScript timestamp value to a Firestore Timestamp object if it appears to be a timestamp.
     * Handles ISO 8601 strings and Unix timestamps in milliseconds.
     */
    public static func convertTimestampValue(_ value: Any?) -> Any? {
        guard let value = value else {
            return nil
        }
        
        // Handle ISO 8601 timestamp strings (e.g., "2025-09-23T17:18:38.749Z")
        if let stringValue = value as? String {
            if isISO8601Timestamp(stringValue) {
                let dateFormatter = ISO8601DateFormatter()
                dateFormatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
                
                if let date = dateFormatter.date(from: stringValue) {
                    return Timestamp(date: date)
                } else {
                    // Try without fractional seconds
                    dateFormatter.formatOptions = [.withInternetDateTime]
                    if let date = dateFormatter.date(from: stringValue) {
                        return Timestamp(date: date)
                    }
                }
            }
            
            // Handle Unix timestamp that comes as string (potential iOS-specific issue)
            if let longValue = Int64(stringValue) {
                // Check if this looks like a Unix timestamp in milliseconds
                // Reasonable range: between year 2000 (946684800000) and year 2100 (4102444800000)
                if longValue >= 946684800000 && longValue <= 4102444800000 {
                    let date = Date(timeIntervalSince1970: Double(longValue) / 1000.0)
                    return Timestamp(date: date)
                }
            }
        }
        
        // Handle Unix timestamp in milliseconds (e.g., 1758388718749)
        if let numberValue = value as? NSNumber {
            let longValue = numberValue.int64Value
            // Check if this looks like a Unix timestamp in milliseconds
            // Reasonable range: between year 2000 (946684800000) and year 2100 (4102444800000)
            if longValue >= 946684800000 && longValue <= 4102444800000 {
                let date = Date(timeIntervalSince1970: Double(longValue) / 1000.0)
                return Timestamp(date: date)
            }
        }
        
        return value // Return original value if not a timestamp
    }
    
    /**
     * Checks if a string matches the ISO 8601 timestamp format.
     */
    private static func isISO8601Timestamp(_ value: String) -> Bool {
        // Basic check for ISO 8601 format: YYYY-MM-DDTHH:mm:ss[.s+]Z
        // Allow 1-6 millisecond digits instead of exactly 3, to be more flexible
        let pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{1,6})?Z$"
        let regex = try? NSRegularExpression(pattern: pattern)
        let range = NSRange(location: 0, length: value.utf16.count)
        return regex?.firstMatch(in: value, options: [], range: range) != nil
    }

    public static func createHashMapFromJSObject(_ object: JSObject) -> [String: Any] {
        var map: [String: Any] = [:]
        for key in object.keys {
            if let value = object[key] {
                map[key] = value
            }
        }
        return map
    }

    public static func createJSObjectFromHashMap(_ map: [String: Any]?) -> JSObject? {
        guard let map = map else {
            return nil
        }
        var object: JSObject = [:]
        for key in map.keys {
            object[key] = self.createJSValue(value: map[key])
        }
        return object
    }

    public static func createQueryCompositeFilterConstraintFromJSObject(_ compositeFilter: JSObject?) -> QueryCompositeFilterConstraint? {
        if let compositeFilter = compositeFilter {
            return QueryCompositeFilterConstraint(compositeFilter)
        } else {
            return nil
        }
    }

    public static func createQueryNonFilterConstraintArrayFromJSArray(_ queryConstraints: [JSObject]?) -> [QueryNonFilterConstraint] {
        if let queryConstraints = queryConstraints {
            var queryNonFilterConstraint: [QueryNonFilterConstraint] = []
            for queryConstraint in queryConstraints {
                let queryConstraintType = queryConstraint["type"] as? String
                switch queryConstraintType {
                case "orderBy":
                    let queryOrderByConstraint = QueryOrderByConstraint(queryConstraint)
                    queryNonFilterConstraint.append(queryOrderByConstraint)
                case "limit", "limitToLast":
                    let queryLimitConstraint = QueryLimitConstraint(queryConstraint)
                    queryNonFilterConstraint.append(queryLimitConstraint)
                case "startAt", "startAfter":
                    let queryStartAtConstraint = QueryStartAtConstraint(queryConstraint)
                    queryNonFilterConstraint.append(queryStartAtConstraint)
                case "endAt", "endBefore":
                    let queryEndAtConstraint = QueryEndAtConstraint(queryConstraint)
                    queryNonFilterConstraint.append(queryEndAtConstraint)
                case "where":
                    // Skip where constraints - they should be handled as filter constraints
                    // This prevents null entries and allows them to be processed separately
                    break
                default:
                    // Skip unsupported constraint types instead of creating null entries
                    break
                }
            }
            return queryNonFilterConstraint
        } else {
            return []
        }
    }

    public static func createFilterFromWhereConstraints(_ queryConstraints: [JSObject]?) -> Filter? {
        guard let queryConstraints = queryConstraints else {
            return nil
        }
        
        var filters: [Filter] = []
        for queryConstraint in queryConstraints {
            let queryConstraintType = queryConstraint["type"] as? String
            if queryConstraintType == "where" {
                let whereConstraint = QueryFieldFilterConstraint(queryConstraint)
                if let filter = whereConstraint.toFilter() {
                    filters.append(filter)
                }
            }
        }
        
        if filters.isEmpty {
            return nil
        } else if filters.count == 1 {
            return filters[0]
        } else {
            return Filter.andFilter(filters)
        }
    }

    private static func createJSValue(value: Any?) -> JSValue? {
        guard let value = value else {
            return nil
        }
        
        // Handle Firestore Timestamp objects specifically
        if let timestamp = value as? Timestamp {
            // Convert to string representation like Android: "Timestamp(seconds=..., nanoseconds=...)"
            return "Timestamp(seconds=\(timestamp.seconds), nanoseconds=\(timestamp.nanoseconds))"
        }
        
        guard let value = JSTypes.coerceDictionaryToJSObject(["key": value]) as JSObject? else {
            return nil
        }
        return value["key"]
    }

    public static func createErrorCode(error: Error?) -> String? {
        if let error = error as NSError? {
            if let errorCode = convertErrorCodeToString(errorCode: error.code) {
                let prefixedErrorCode = "firestore/" + errorCode
                return prefixedErrorCode
            } else {
                return nil
            }
        }
        return nil
    }

    private static func convertErrorCodeToString(errorCode: Int) -> String? {
        let errorCodes: [Int: String] = [
            FirestoreErrorCode.aborted.rawValue: "aborted",
            FirestoreErrorCode.alreadyExists.rawValue: "already-exists",
            FirestoreErrorCode.cancelled.rawValue: "cancelled",
            FirestoreErrorCode.dataLoss.rawValue: "data-loss",
            FirestoreErrorCode.deadlineExceeded.rawValue: "deadline-exceeded",
            FirestoreErrorCode.failedPrecondition.rawValue: "failed-precondition",
            FirestoreErrorCode.internal.rawValue: "internal",
            FirestoreErrorCode.invalidArgument.rawValue: "invalid-argument",
            FirestoreErrorCode.notFound.rawValue: "not-found",
            FirestoreErrorCode.outOfRange.rawValue: "out-of-range",
            FirestoreErrorCode.permissionDenied.rawValue: "permission-denied",
            FirestoreErrorCode.resourceExhausted.rawValue: "resource-exhausted",
            FirestoreErrorCode.unauthenticated.rawValue: "unauthenticated",
            FirestoreErrorCode.unavailable.rawValue: "unavailable",
            FirestoreErrorCode.unimplemented.rawValue: "unimplemented",
            FirestoreErrorCode.unknown.rawValue: "unknown"
        ]

        return errorCodes[errorCode]
    }
}
