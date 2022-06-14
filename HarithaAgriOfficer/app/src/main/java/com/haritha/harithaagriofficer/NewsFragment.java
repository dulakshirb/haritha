package com.haritha.harithaagriofficer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class NewsFragment extends Fragment {

    private NewsAdapter newsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference dbReferenceNews = FirebaseDatabase.getInstance().getReference("Officer").child("News");

        //add new button
        Button btn_add_news = view.findViewById(R.id.btn_add_news);
        btn_add_news.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            Fragment newsAddFragment = new NewsAddFragment();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, newsAddFragment).addToBackStack(null).commit();
        });

        RecyclerView newsRecyclerView = view.findViewById(R.id.rv_news);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<News> options = new FirebaseRecyclerOptions.Builder<News>()
                .setQuery(dbReferenceNews.orderByChild("news_published_by").equalTo(currentUserId), News.class)
                .build();

        newsAdapter = new NewsAdapter(options);
        newsRecyclerView.setAdapter(newsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        newsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        newsAdapter.stopListening();
    }
}