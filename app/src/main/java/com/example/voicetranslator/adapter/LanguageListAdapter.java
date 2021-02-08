package com.example.voicetranslator.adapter;

import android.app.Activity;
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

import com.example.voicetranslator.model.Language;
import com.example.voicetranslator.R;
import com.example.voicetranslator.activity.LanguagesListActivity;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateRemoteModel;

import java.util.List;

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.LanguageViewHolder> {

    private RemoteModelManager modelManager = RemoteModelManager.getInstance();
    private List<Language> languagesList = Language.getAllLanguages();
    private int mode;

    public List<Language> getLanguagesList() {
        return languagesList;
    }

    public LanguageListAdapter(int mode) {

        this.mode = mode;

    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LanguageViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.languages_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {

        Language language = languagesList.get(position);

        holder.flag.setImageResource(language.getFlagId());

        holder.progressBar.setVisibility(View.INVISIBLE);

        if (mode == LanguagesListActivity.MODE_SELECT){

            holder.download.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.INVISIBLE);

            holder.root.setOnClickListener(view -> rootOnClickListener(view, position));

        }
        else if (mode == LanguagesListActivity.MODE_SETTINGS){

            holder.setVisibility(language);

            if (language.isModelDownloaded()) {

                holder.download.setImageResource(0);
                holder.download.setOnClickListener(null);

            } else {

                holder.download.setImageResource(R.drawable.ic_baseline_cloud_download_64);
                holder.download.setOnClickListener(view -> downloadOnClickListener(view, holder, language));
            }
        }
        holder.name.setText(language.getName());

    }

    @Override
    public int getItemCount() {
        return languagesList.size();
    }

    private void rootOnClickListener(View view, int position){

        LanguagesListActivity context =  (LanguagesListActivity) view.getContext();
        Intent intent = new Intent();
        intent.putExtra(LanguagesListActivity.LANGUAGE_EXTRA_NAME, languagesList.get(position));

        context.setResult(Activity.RESULT_OK, intent);
        context.finish();

    }

    private void downloadOnClickListener(View view, LanguageViewHolder languageViewHolder, Language language){

        RemoteModelManager modelManager = RemoteModelManager.getInstance();

        TranslateRemoteModel remoteModel =
                new TranslateRemoteModel.Builder(language.getId()).build();

        DownloadConditions conditions = new DownloadConditions .Builder()
                .build();

        modelManager.download(remoteModel, conditions)
                .addOnSuccessListener(v -> {

                    language.setModelDownloaded(true);
                    language.setModelDownloading(false);

                    notifyDataSetChanged();

                    languageViewHolder.setVisibility(language);
                    languageViewHolder.download.setOnClickListener(null);

                })
                .addOnFailureListener(e -> {
                    language.setModelDownloading(false);
                    languageViewHolder.setVisibility(language);});

        language.setModelDownloading(true);
        languageViewHolder.setVisibility(language);
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

        public void setVisibility(Language language){

            if (language.isModelDownloading()) {
                progressBar.setVisibility(View.VISIBLE);
                download.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                download.setVisibility(View.VISIBLE);
            }
        }

    }

}
