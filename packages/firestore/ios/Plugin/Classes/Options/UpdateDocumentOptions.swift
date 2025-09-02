import Foundation
import Capacitor

@objc public class UpdateDocumentOptions: NSObject {
    private var reference: String
    private var data: [String: Any]
    private var databaseId: String?

    init(reference: String, data: JSObject, databaseId: String? = nil) {
        self.reference = reference
        self.data = FirebaseFirestoreHelper.createHashMapFromJSObject(data)
        self.databaseId = databaseId
    }

    func getReference() -> String {
        return reference
    }

    func getData() -> [String: Any] {
        return data
    }

    func getDatabaseId() -> String? {
        return databaseId
    }
}
