package io.capawesome.capacitorjs.plugins.firebase.firestore.classes.options;

import androidx.annotation.Nullable;

public class GetDocumentOptions {

    private String reference;
    @Nullable
    private String databaseId;

    public GetDocumentOptions(String reference, @Nullable String databaseId) {
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
