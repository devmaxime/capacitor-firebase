import Foundation
import Capacitor

@objc public class WriteBatchOptions: NSObject {
    private var operations: [WriteBatchOperation]
    private var databaseId: String?

    init(operations: [JSObject], databaseId: String? = nil) {
        self.operations = WriteBatchOptions.createWriteBatchOperationArrayFromJSArray(operations)
        self.databaseId = databaseId
    }

    public func getOperations() -> [WriteBatchOperation] {
        return self.operations
    }

    public func getDatabaseId() -> String? {
        return self.databaseId
    }

    private static func createWriteBatchOperationArrayFromJSArray(_ data: [JSObject]) -> [WriteBatchOperation] {
        var operations: [WriteBatchOperation] = []
        for item in data {
            operations.append(WriteBatchOperation(item))
        }
        return operations
    }
}
