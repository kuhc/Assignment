package com.utar.assignment.Util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utar.assignment.Model.User;

public class FirestoreHelper {

    private static FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    // Set User
    public static void setUser(User user, FirebaseCallback callback) {
        fStore.collection("Users").document(user.getUid()).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callback.onResponse();
                    }
                });
    }

    // Get User
    public static void getUser(String Id, FirebaseCallback callback) {
        fStore.collection("Users").document(Id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        callback.onResponse(user);
                    }
                });
    }
}
