package io.capawesome.capacitorjs.plugins.firebase.firestore.classes.options;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GetCountFromServerOptions {

    @NonNull
    private final String reference;

    @Nullable
    private final String databaseId;

    public GetCountFromServerOptions(@NonNull String reference, @Nullable String databaseId) {
        this.reference = reference;
        this.databaseId = databaseId;
    }

    @NonNull
    public String getReference() {
        return reference;
    }

    @Nullable
    public String getDatabaseId() {
        return databaseId;
    }
}
