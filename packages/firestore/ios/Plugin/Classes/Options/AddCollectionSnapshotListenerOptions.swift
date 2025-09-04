import Foundation
import Capacitor

@objc public class AddCollectionSnapshotListenerOptions: NSObject {
    private var reference: String
    private var compositeFilter: QueryCompositeFilterConstraint?
    private var queryConstraints: [QueryNonFilterConstraint]
    private var originalQueryConstraints: [JSObject]?
    private var includeMetadataChanges: Bool
    private var callbackId: String
    private var databaseId: String?

    init(reference: String, compositeFilter: JSObject?, queryConstraints: [JSObject]?, includeMetadataChanges: Bool, callbackId: String, databaseId: String? = nil) {
        self.reference = reference
        self.compositeFilter = FirebaseFirestoreHelper.createQueryCompositeFilterConstraintFromJSObject(compositeFilter)
        self.queryConstraints = FirebaseFirestoreHelper.createQueryNonFilterConstraintArrayFromJSArray(queryConstraints)
        self.originalQueryConstraints = queryConstraints
        self.includeMetadataChanges = includeMetadataChanges
        self.callbackId = callbackId
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

    func getIncludeMetadataChanges() -> Bool {
        return includeMetadataChanges
    }

    func getCallbackId() -> String {
        return callbackId
    }

    func getDatabaseId() -> String? {
        return databaseId
    }

    func getOriginalQueryConstraints() -> [JSObject]? {
        return originalQueryConstraints
    }
}
