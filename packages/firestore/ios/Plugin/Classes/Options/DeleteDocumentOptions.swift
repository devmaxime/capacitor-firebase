import Foundation

@objc public class DeleteDocumentOptions: NSObject {
    private var reference: String
    private var databaseId: String?

    init(reference: String, databaseId: String? = nil) {
        self.reference = reference
        self.databaseId = databaseId
    }

    func getReference() -> String {
        return reference
    }

    func getDatabaseId() -> String? {
        return databaseId
    }
}
