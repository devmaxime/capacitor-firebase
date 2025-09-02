package io.capawesome.capacitorjs.plugins.firebase.firestore.classes.options;

import androidx.annotation.Nullable;

public class DeleteDocumentOptions {

    private String reference;
    @Nullable
    private String databaseId;

    public DeleteDocumentOptions(String reference, @Nullable String databaseId) {
        this.reference = reference;
        this.databaseId = databaseId;
    }

    public String getReference() {
        return reference;
    }

    @Nullable
    public String getDatabaseId() {
        return databaseId;
    }
}
