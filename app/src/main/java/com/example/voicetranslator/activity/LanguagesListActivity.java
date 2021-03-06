package com.example.voicetranslator.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.voicetranslator.model.Language;
import com.example.voicetranslator.adapter.LanguageListAdapter;
import com.example.voicetranslator.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateRemoteModel;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class LanguagesListActivity extends AppCompatActivity {

    private RemoteModelManager modelManager = RemoteModelManager.getInstance();
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

        ItemTouchHelper.Callback callback = getCallback(adapter);

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ItemTouchHelper.Callback getCallback(LanguageListAdapter adapter) {
        return new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                Language language = adapter.getLanguagesList().get(viewHolder.getAdapterPosition());

                int dragFlags = 0;
                int swipeFlags = 0;

                if (language.isModelDownloaded() && language.isDeletable()){
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

                    TranslateRemoteModel model =
                            new TranslateRemoteModel.Builder(language.getId()).build();

                    modelManager.deleteDownloadedModel(model)
                            .addOnSuccessListener(v -> {

                                language.setModelDownloaded(false);
                                adapter.notifyDataSetChanged();

                            })
                            .addOnFailureListener(e -> {

                                FirebaseCrashlytics.getInstance().recordException(e);

                            });
                }

            }

            public void onChildDraw (@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(LanguagesListActivity.this, R.color.bar))
                        .addActionIcon(R.drawable.ic_baseline_delete_sweep_64)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
    }

}