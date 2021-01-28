package com.example.voicetranslator;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voicetranslator.activity.LanguagesListActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;

import java.util.List;

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.LanguageViewHolder> {

    private FirebaseModelManager modelManager = FirebaseModelManager.getInstance();
    private List<Language> languagesList = Language.getAllLanguages();
    private int mode;

    public List<Language> getLanguagesList() {
        return languagesList;
    }

    public LanguageListAdapter(int mode) {

        this.mode = mode;

        for (Language language : languagesList) {

            FirebaseTranslateRemoteModel remoteModel = new FirebaseTranslateRemoteModel.Builder(language.getId()).build();

            Task<Boolean> booleanTask = modelManager.isModelDownloaded(remoteModel);

            booleanTask.addOnSuccessListener(aBoolean -> {
                language.setModelDownloaded(aBoolean);
                notifyDataSetChanged();
            });
        }

    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LanguageViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.languages_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {

        LanguageViewHolder languageViewHolder = (LanguageViewHolder) holder;

        Language language = languagesList.get(position);

        languageViewHolder.flag.setImageResource(language.getFlagId());

        languageViewHolder.progressBar.setVisibility(View.INVISIBLE);

        if (mode == LanguagesListActivity.MODE_SELECT){

            languageViewHolder.download.setVisibility(View.INVISIBLE);
            languageViewHolder.progressBar.setVisibility(View.INVISIBLE);

            languageViewHolder.root.setOnClickListener(view -> rootOnClickListener(view, position));

        }
        else if (mode == LanguagesListActivity.MODE_SETTINGS){

            if (language.isModelDownloaded()) {

                languageViewHolder.download.setImageResource(0);
                languageViewHolder.download.setOnClickListener(null);

            } else {

                languageViewHolder.download.setImageResource(R.drawable.download);
                languageViewHolder.download.setOnClickListener(view -> {

                    languageViewHolder.setVisibility(true);

                    FirebaseModelManager modelManager = FirebaseModelManager.getInstance();

                    FirebaseTranslateRemoteModel remoteModel =
                            new FirebaseTranslateRemoteModel.Builder(language.getId()).build();

                    FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                            .requireWifi()
                            .build();

                    modelManager.download(remoteModel, conditions)
                            .addOnSuccessListener(v -> {

                                language.setModelDownloaded(true);

                                notifyDataSetChanged();

                                languageViewHolder.setVisibility(false);
                                languageViewHolder.download.setOnClickListener(null);
                            })
                            .addOnFailureListener(e -> languageViewHolder.setVisibility(false));

                });
            }
        }
        languageViewHolder.name.setText(language.getName());

    }

    @Override
    public int getItemCount() {
        return languagesList.size();
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder{

        ImageView flag;
        ImageView download;
        TextView name;
        ProgressBar progressBar;
        RelativeLayout root;

        public LanguageViewHolder(@NonNull View itemView) {

            super(itemView);

            flag = itemView.findViewById(R.id.item_list_flag);
            download = itemView.findViewById(R.id.item_list_download);
            name = itemView.findViewById(R.id.item_list_name);
            progressBar = itemView.findViewById(R.id.progress_bar);
            root = itemView.findViewById(R.id.item_list_root);

        }

        public void setVisibility(boolean downloading){

            if (downloading) {
                progressBar.setVisibility(View.VISIBLE);
                download.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                download.setVisibility(View.VISIBLE);
            }
        }

    }

    private void rootOnClickListener(View view, int position){

        LanguagesListActivity context =  (LanguagesListActivity) view.getContext();
        Intent intent = new Intent();
        intent.putExtra(LanguagesListActivity.LANGUAGE_EXTRA_NAME, languagesList.get(position));

        context.setResult(1, intent);
        context.finish();

    }

}
