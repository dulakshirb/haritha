package com.haritha.harithaagriofficer;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewsAddFragment extends Fragment {

    private View view;
    private Button calendar_publishedDate, btn_postNews;
    private EditText txt_newsHeadline, txt_newsContent;
    private Spinner spinner_publishTo;
    private ProgressDialog loadingBar;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private String newsId, newsHeadline, newsContent, publishedDate, publishedTo, publishedBy;

    private DatabaseReference dbReferenceNews;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_news_add, container, false);

        mAuth = FirebaseAuth.getInstance();
        dbReferenceNews = FirebaseDatabase.getInstance().getReference("Officer").child("News");

        //initializeFields
        txt_newsHeadline = view.findViewById(R.id.txt_newsHeadline);
        txt_newsContent = view.findViewById(R.id.txt_newsContent);
        spinner_publishTo = view.findViewById(R.id.spinner_publishTo);
        btn_postNews = view.findViewById(R.id.btn_postNews);
        loadingBar = new ProgressDialog(getActivity());

        selectPublishedDate();

        btn_postNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNews();
            }
        });

        return view;
    }

    private void postNews() {
        newsHeadline = txt_newsHeadline.getText().toString().trim();
        newsContent = txt_newsContent.getText().toString().trim();

        if (TextUtils.isEmpty(newsHeadline)) {
            txt_newsHeadline.setError("News heading is required.");
            txt_newsHeadline.requestFocus();
        } else if (TextUtils.isEmpty(newsContent)) {
            txt_newsContent.setError("News content is required.");
            txt_newsContent.requestFocus();
        } else {
            publishedTo = spinner_publishTo.getSelectedItem().toString();

            loadingBar.setTitle("Posting");
            loadingBar.setMessage("Please wait, while we are things getting ready for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            newsId = dbReferenceNews.push().getKey();
            publishedBy = mAuth.getCurrentUser().getUid();
            News writeNews = new News(newsId, newsHeadline, newsContent, publishedDate, publishedTo, publishedBy);

            dbReferenceNews.child(newsId).setValue(writeNews).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "News posted successfully!", Toast.LENGTH_SHORT).show();
                        sendUserToNewsFragment();
                    }
                    else {
                        Toast.makeText(getActivity(), "Failed to post a news, Please try again!", Toast.LENGTH_SHORT).show();
                    }
                    loadingBar.dismiss();
                }
            });
        }
    }

    private void sendUserToNewsFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment newsFragment = new NewsFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, newsFragment).addToBackStack(null).commit();
    }

    private void selectPublishedDate() {
        //set current date by default
        calendar_publishedDate = view.findViewById(R.id.calendar_publishedDate);
        Calendar calendarToday = Calendar.getInstance();
        long currentDateInMillis = calendarToday.getTimeInMillis();
        Date currentDate = new Date(currentDateInMillis);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strCurrentDate = dateFormat.format(currentDate);
        calendar_publishedDate.setText(strCurrentDate);
        publishedDate = String.valueOf(currentDateInMillis);

        //open DatePickerDialog
        calendar_publishedDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, dateSetListener, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        //set selected date
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            final Calendar calendar = Calendar.getInstance();

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                long publishedDateInMillis = calendar.getTimeInMillis();
                Date publishedDateInDate = new Date(publishedDateInMillis);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String publishedDateInFormat = dateFormat.format(publishedDateInDate);
                calendar_publishedDate.setText(publishedDateInFormat);
                publishedDate = String.valueOf(publishedDateInMillis);
            }
        };
    }
}