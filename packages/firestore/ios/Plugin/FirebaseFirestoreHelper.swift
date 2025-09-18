import Foundation
import FirebaseFirestore
import Capacitor

public class FirebaseFirestoreHelper {
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
