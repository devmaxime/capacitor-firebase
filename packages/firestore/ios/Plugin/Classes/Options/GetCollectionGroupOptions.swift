import Foundation
import Capacitor

@objc public class GetCollectionGroupOptions: NSObject {
    private var reference: String
    private var compositeFilter: QueryCompositeFilterConstraint?
    private var queryConstraints: [QueryNonFilterConstraint]
    private var originalQueryConstraints: [JSObject]?
    private var databaseId: String?

    init(reference: String, compositeFilter: JSObject?, queryConstraints: [JSObject]?, databaseId: String? = nil) {
        self.reference = reference
        self.compositeFilter = FirebaseFirestoreHelper.createQueryCompositeFilterConstraintFromJSObject(compositeFilter)
        self.queryConstraints = FirebaseFirestoreHelper.createQueryNonFilterConstraintArrayFromJSArray(queryConstraints)
        self.originalQueryConstraints = queryConstraints
        self.databaseId = databaseId
    }

    func getReference() -> String {
        return reference
    }

    func getCompositeFilter() -> QueryCompositeFilterConstraint? {
        return compositeFilter
    }

    func getQueryConstraints() -> [QueryNonFilterConstraint] {
        return queryConstraints
    }

    func getDatabaseId() -> String? {
        return databaseId
    }
    
    func getOriginalQueryConstraints() -> [JSObject]? {
        return originalQueryConstraints
    }
}
