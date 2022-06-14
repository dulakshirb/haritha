package com.haritha.haritha_farmer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsAdapter extends FirebaseRecyclerAdapter<News, NewsAdapter.NewsVH> {

    public NewsAdapter(@NonNull FirebaseRecyclerOptions<News> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NewsVH holder, int position, @NonNull News model) {
        long getPublishedDate = Long.parseLong(model.getNews_published_date());
        Date publishedDateInDate = new Date(getPublishedDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        holder.txt_rv_publishedDate.setText(dateFormat.format(publishedDateInDate));
        holder.txt_rv_newsHeadline.setText(model.getNews_heading());
        holder.txt_rv_newsContent.setText(model.getNews_content());
    }

    @NonNull
    @Override
    public NewsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_news_card, parent, false);
        return new NewsVH(view);
    }

    public static class NewsVH extends RecyclerView.ViewHolder {

        private final TextView txt_rv_newsHeadline, txt_rv_publishedDate, txt_rv_newsContent;

        public NewsVH(@NonNull View itemView) {
            super(itemView);
            txt_rv_newsHeadline = itemView.findViewById(R.id.txt_rv_newsHeadline);
            txt_rv_publishedDate = itemView.findViewById(R.id.txt_rv_publishedDate);
            txt_rv_newsContent = itemView.findViewById(R.id.txt_rv_newsContent);
        }
    }
}
