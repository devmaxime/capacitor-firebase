import Foundation

@objc public class GetDocumentOptions: NSObject {
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
