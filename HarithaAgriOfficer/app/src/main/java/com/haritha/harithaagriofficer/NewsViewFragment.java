package com.haritha.harithaagriofficer;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NewsViewFragment extends Fragment {

    private View view;
    private TextView txt_view_newsHeadline, txt_view_publishedDate, txt_view_newsContent, txt_view_publishedTo;
    private Button btn_edit_news, btn_remove_news, btn_dialog_cancel, btn_dialog_delete;
    private String news_id, news_heading, news_content, news_published_date, news_publish_to;
    private Dialog deleteDialog;

    private DatabaseReference dbReferenceNews;

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
        view = inflater.inflate(R.layout.fragment_news_view, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        dbReferenceNews = FirebaseDatabase.getInstance().getReference("Officer").child("News").child(currentUserId).child(news_id);

        txt_view_newsHeadline = view.findViewById(R.id.txt_view_newsHeadline);
        txt_view_publishedDate = view.findViewById(R.id.txt_view_publishedDate);
        txt_view_newsContent = view.findViewById(R.id.txt_view_newsContent);
        txt_view_publishedTo = view.findViewById(R.id.txt_view_publishedTo);
        btn_edit_news = view.findViewById(R.id.btn_edit_news);
        btn_remove_news = view.findViewById(R.id.btn_remove_news);

        //view news
        long getPublishedDate = Long.parseLong(news_published_date);
        Date publishedDateInDate = new Date(getPublishedDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        txt_view_publishedDate.setText(dateFormat.format(publishedDateInDate));
        txt_view_newsHeadline.setText(news_heading);
        txt_view_newsContent.setText(news_content);
        txt_view_publishedTo.setText(news_publish_to);

        //remove
        deleteDialog = new Dialog(getActivity());
        deleteDialog.setContentView(R.layout.delete_dialog);
        deleteDialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteDialog.setCancelable(false);
        deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        btn_dialog_delete = deleteDialog.findViewById(R.id.btn_dialog_delete);
        btn_dialog_cancel = deleteDialog.findViewById(R.id.btn_dialog_cancel);

        btn_remove_news.setOnClickListener(v -> deleteDialog.show());
        btn_dialog_cancel.setOnClickListener(v -> deleteDialog.dismiss());

        btn_dialog_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNews();
                deleteDialog.dismiss();
            }
        });

        return view;
    }

    private void deleteNews() {
        if (dbReferenceNews != null)
            dbReferenceNews.removeValue();
        sendUserToNewsFragment();
    }

    private void sendUserToNewsFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment newsFragment = new NewsFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, newsFragment).addToBackStack(null).commit();
    }
}