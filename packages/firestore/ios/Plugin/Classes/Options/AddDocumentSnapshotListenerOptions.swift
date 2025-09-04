import Foundation

@objc public class AddDocumentSnapshotListenerOptions: NSObject {
    private var reference: String
    private var includeMetadataChanges: Bool
    private var callbackId: String
    private var databaseId: String?

    init(reference: String, includeMetadataChanges: Bool, callbackId: String, databaseId: String? = nil) {
        self.reference = reference
        self.includeMetadataChanges = includeMetadataChanges
        self.callbackId = callbackId
        self.databaseId = databaseId
    }

    func getReference() -> String {
        return reference
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
}
