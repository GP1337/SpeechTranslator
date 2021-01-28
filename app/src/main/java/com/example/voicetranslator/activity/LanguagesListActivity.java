package com.example.voicetranslator.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.voicetranslator.Language;
import com.example.voicetranslator.LanguageListAdapter;
import com.example.voicetranslator.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;

import dagger.Module;
import dagger.Provides;

public class LanguagesListActivity extends AppCompatActivity {

    private FirebaseModelManager modelManager = FirebaseModelManager.getInstance();
    private RecyclerView recyclerView;
    private int mode;

    public static final String MODE_NAME = "mode";
    public static final String LANGUAGE_EXTRA_NAME = "language";
    public static final int MODE_SETTINGS = 1;
    public static final int MODE_SELECT = 2;

    public static Intent getSelectIntent(Context context){

        Intent intent = new Intent(context, LanguagesListActivity.class);

        intent.putExtra(MODE_NAME, MODE_SELECT);

        return intent;

    }

    public static Intent getSettingsIntent(Context context){

        Intent intent = new Intent(context, LanguagesListActivity.class);

        intent.putExtra(MODE_NAME, MODE_SETTINGS);

        return intent;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages_list);

        Bundle extras = getIntent().getExtras();

        mode = extras.getInt(MODE_NAME, MODE_SELECT);

        recyclerView = findViewById(R.id.languages_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        LanguageListAdapter adapter = new LanguageListAdapter(mode);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                Language language = adapter.getLanguagesList().get(viewHolder.getAdapterPosition());

                int dragFlags = 0;
                int swipeFlags = 0;

                if (language.isModelDownloaded()){
                   swipeFlags = ItemTouchHelper.START;
                }

                return makeMovementFlags(dragFlags, swipeFlags);

            }

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