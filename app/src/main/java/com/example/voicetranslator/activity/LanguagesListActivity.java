package com.example.voicetranslator.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.voicetranslator.Language;
import com.example.voicetranslator.LanguageListAdapter;
import com.example.voicetranslator.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;

public class LanguagesListActivity extends AppCompatActivity {

    private FirebaseModelManager modelManager = FirebaseModelManager.getInstance();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages_list);

        recyclerView = findViewById(R.id.languages_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        LanguageListAdapter adapter = new LanguageListAdapter();
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                Language language = adapter.getLanguagesList().get(viewHolder.getAdapterPosition());

                if (language.isModelDownloaded()) {

                    FirebaseTranslateRemoteModel model =
                            new FirebaseTranslateRemoteModel.Builder(language.getId()).build();

                    modelManager.deleteDownloadedModel(model)
                            .addOnSuccessListener(v -> {

                                language.setModelDownloaded(false);
                                adapter.notifyDataSetChanged();

                            })
                            .addOnFailureListener(e -> {

                                //todo logs

                            });
                }

            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);

    }
}