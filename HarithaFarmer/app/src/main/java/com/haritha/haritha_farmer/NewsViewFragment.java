package com.haritha.haritha_farmer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsViewFragment extends Fragment {

    String news_id, news_heading, news_content, news_published_date, news_publish_to;

    public NewsViewFragment(String news_id, String news_heading, String news_content, String news_published_date, String news_publish_to) {
        this.news_id = news_id;
        this.news_heading = news_heading;
        this.news_content = news_content;
        this.news_published_date = news_published_date;
        this.news_publish_to = news_publish_to;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_view, container, false);

        TextView txt_view_newsHeadline = view.findViewById(R.id.txt_view_newsHeadline);
        TextView txt_view_publishedDate = view.findViewById(R.id.txt_view_publishedDate);
        TextView txt_view_newsContent = view.findViewById(R.id.txt_view_newsContent);
        TextView txt_view_publishedTo = view.findViewById(R.id.txt_view_publishedTo);

        //view news
        long getPublishedDate = Long.parseLong(news_published_date);
        Date publishedDateInDate = new Date(getPublishedDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        txt_view_publishedDate.setText(dateFormat.format(publishedDateInDate));
        txt_view_newsHeadline.setText(news_heading);
        txt_view_newsContent.setText(news_content);
        txt_view_publishedTo.setText(news_publish_to);

        return view;
    }
}