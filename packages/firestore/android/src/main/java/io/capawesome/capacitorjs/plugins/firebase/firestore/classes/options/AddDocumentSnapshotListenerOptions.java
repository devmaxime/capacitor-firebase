package io.capawesome.capacitorjs.plugins.firebase.firestore.classes.options;

import androidx.annotation.Nullable;

public class AddDocumentSnapshotListenerOptions {

    private String reference;
    private final boolean includeMetadataChanges;
    private String callbackId;
    
    @Nullable
    private String databaseId;

    public AddDocumentSnapshotListenerOptions(String reference, @Nullable Boolean includeMetadataChanges, String callbackId, @Nullable String databaseId) {
        this.reference = reference;
        this.includeMetadataChanges = includeMetadataChanges == null ? false : includeMetadataChanges;
        this.callbackId = callbackId;
        this.databaseId = databaseId;
    }

    public String getReference() {
        return reference;
    }

    public boolean isIncludeMetadataChanges() {
        return includeMetadataChanges;
    }

    public String getCallbackId() {
        return callbackId;
    }
    
    @Nullable
    public String getDatabaseId() {
        return databaseId;
    }
}
