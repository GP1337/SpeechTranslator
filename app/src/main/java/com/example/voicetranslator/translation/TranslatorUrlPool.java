package com.example.voicetranslator.translation;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TranslatorUrlPool {

    private static TranslatorUrlPool instance;

    public static void init(OnInitListener onInitListener){

        if (instance == null){
            instance = new TranslatorUrlPool(onInitListener);
        }

    }

    public static TranslatorUrlPool getInstance(){

        return instance;
    }

    private List<String>  urlList;

    private TranslatorUrlPool(OnInitListener onInitListener) {

        urlList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> tempList = new ArrayList<>();

                snapshot.getChildren().forEach(dataSnapshot1 -> tempList.add(dataSnapshot1.getValue(String.class)));

                urlList = tempList;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseCrashlytics.getInstance().recordException(error.toException());
            }
        });

        Task<DataSnapshot> dataSnapshotTask = myRef.get();

        dataSnapshotTask.addOnSuccessListener(
                dataSnapshot -> {dataSnapshot.getChildren().forEach(dataSnapshot1 -> {
                    urlList.add(dataSnapshot1.getValue(String.class));});
                onInitListener.onInit(urlList);
                }).addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));

    }

    public List<String> getPool(){
        return urlList;
    }

    @FunctionalInterface
    public interface OnInitListener{

        void onInit(List<String> urlList);

    }

}
