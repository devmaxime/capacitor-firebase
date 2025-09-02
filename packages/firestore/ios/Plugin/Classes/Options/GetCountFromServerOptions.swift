import Foundation

@objc public class GetCountFromServerOptions: NSObject {
    private let reference: String
    private let databaseId: String?

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
