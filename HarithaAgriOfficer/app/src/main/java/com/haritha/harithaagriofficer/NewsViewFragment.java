package com.haritha.harithaagriofficer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class NewsViewFragment extends Fragment {

    private View view;
    private Button calendar_publishedDate;
    private final String news_id;
    private String news_heading;
    private String news_content;
    private String news_published_date;
    private String news_publish_to;
    private String currentUserId;
    private Dialog deleteDialog;
    private ProgressDialog loadingBar;
    private DatePickerDialog.OnDateSetListener dateSetListener;

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
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        dbReferenceNews = FirebaseDatabase.getInstance().getReference("Officer").child("News").child(news_id);

        TextView txt_view_newsHeadline = view.findViewById(R.id.txt_view_newsHeadline);
        TextView txt_view_publishedDate = view.findViewById(R.id.txt_view_publishedDate);
        TextView txt_view_newsContent = view.findViewById(R.id.txt_view_newsContent);
        TextView txt_view_publishedTo = view.findViewById(R.id.txt_view_publishedTo);
        Button btn_edit_news = view.findViewById(R.id.btn_edit_news);
        Button btn_remove_news = view.findViewById(R.id.btn_remove_news);
        loadingBar = new ProgressDialog(getActivity());

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
        Button btn_dialog_delete = deleteDialog.findViewById(R.id.btn_dialog_delete);
        Button btn_dialog_cancel = deleteDialog.findViewById(R.id.btn_dialog_cancel);

        btn_remove_news.setOnClickListener(v -> deleteDialog.show());
        btn_dialog_cancel.setOnClickListener(v -> deleteDialog.dismiss());

        btn_dialog_delete.setOnClickListener(v -> {
            deleteNews();
            deleteDialog.dismiss();
        });

        //edit
        btn_edit_news.setOnClickListener(v -> onEditPress());

        return view;
    }

    private void onEditPress() {
        //open cropEditFragment dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_news_edit, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner_publishTo = dialogView.findViewById(R.id.spinner_publishTo);
        final TextView txt_newsHeadline = dialogView.findViewById(R.id.txt_newsHeadline);
        final TextView txt_newsContent = dialogView.findViewById(R.id.txt_newsContent);
        final Button btn_UpdateNews = dialogView.findViewById(R.id.btn_UpdateNews);
        calendar_publishedDate = dialogView.findViewById(R.id.calendar_publishedDate);

        dialogBuilder.setTitle("Updating");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //show country through spinner
        if (news_publish_to.equals("All")) {
            spinner_publishTo.setSelection(0);
        } else if (news_publish_to.equals("Ampara")) {
            spinner_publishTo.setSelection(1);
        } else if (news_publish_to.equals("Anuradhapura")) {
            spinner_publishTo.setSelection(2);
        } else if (news_publish_to.equals("Badulla")) {
            spinner_publishTo.setSelection(3);
        } else if (news_publish_to.equals("Batticaloa")) {
            spinner_publishTo.setSelection(4);
        } else if (news_publish_to.equals("Colombo")) {
            spinner_publishTo.setSelection(5);
        } else if (news_publish_to.equals("Galle")) {
            spinner_publishTo.setSelection(6);
        } else if (news_publish_to.equals("Gampaha")) {
            spinner_publishTo.setSelection(7);
        } else if (news_publish_to.equals("Hambantota")) {
            spinner_publishTo.setSelection(8);
        } else if (news_publish_to.equals("Jaffna")) {
            spinner_publishTo.setSelection(9);
        } else if (news_publish_to.equals("Kalutara")) {
            spinner_publishTo.setSelection(10);
        } else if (news_publish_to.equals("Kandy")) {
            spinner_publishTo.setSelection(11);
        } else if (news_publish_to.equals("Kegalle")) {
            spinner_publishTo.setSelection(12);
        } else if (news_publish_to.equals("Kilinochchi")) {
            spinner_publishTo.setSelection(13);
        } else if (news_publish_to.equals("Kurunegala")) {
            spinner_publishTo.setSelection(14);
        } else if (news_publish_to.equals("Mannar")) {
            spinner_publishTo.setSelection(15);
        } else if (news_publish_to.equals("Matale")) {
            spinner_publishTo.setSelection(16);
        } else if (news_publish_to.equals("Matara")) {
            spinner_publishTo.setSelection(17);
        } else if (news_publish_to.equals("Moneragala")) {
            spinner_publishTo.setSelection(18);
        } else if (news_publish_to.equals("Mullaitivu")) {
            spinner_publishTo.setSelection(19);
        } else if (news_publish_to.equals("Nuwara Eliya")) {
            spinner_publishTo.setSelection(20);
        } else if (news_publish_to.equals("Polonnaruwa")) {
            spinner_publishTo.setSelection(21);
        } else if (news_publish_to.equals("Puttalam")) {
            spinner_publishTo.setSelection(22);
        } else if (news_publish_to.equals("Ratnapura")) {
            spinner_publishTo.setSelection(23);
        } else if (news_publish_to.equals("Trincomalee")) {
            spinner_publishTo.setSelection(24);
        } else if (news_publish_to.equals("Vavuniya")) {
            spinner_publishTo.setSelection(25);
        }

        txt_newsHeadline.setText(news_heading);
        txt_newsContent.setText(news_content);
        long getPublishedDate = Long.parseLong(news_published_date);
        Date publishedDateInDate = new Date(getPublishedDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar_publishedDate.setText(dateFormat.format(publishedDateInDate));

        selectPublishedDate();

        btn_UpdateNews.setOnClickListener(v -> {
            news_heading = txt_newsHeadline.getText().toString().trim();
            news_content = txt_newsContent.getText().toString().trim();
            if (TextUtils.isEmpty(news_heading)) {
                txt_newsHeadline.setError("News heading is required.");
                txt_newsHeadline.requestFocus();
            } else if (TextUtils.isEmpty(news_content)) {
                txt_newsContent.setError("News content is required.");
                txt_newsContent.requestFocus();
            }else {
                news_publish_to = spinner_publishTo.getSelectedItem().toString();
                loadingBar.setTitle("Updating..");
                loadingBar.setMessage("Please wait, while we are things getting ready for you...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                News updateNews = new News(news_id, news_heading, news_content, news_published_date, news_publish_to, currentUserId);
                dbReferenceNews.setValue(updateNews).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "News updated successfully!", Toast.LENGTH_SHORT).show();
                        sendUserToNewsFragment();
                    }
                    else {
                        Toast.makeText(getActivity(), "Failed to update a news, Please try again!", Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                    loadingBar.dismiss();
                });
            }
        });
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

    private void selectPublishedDate() {
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
                java.sql.Date publishedDateInDate = new java.sql.Date(publishedDateInMillis);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String publishedDateInFormat = dateFormat.format(publishedDateInDate);
                calendar_publishedDate.setText(publishedDateInFormat);
                news_published_date = String.valueOf(publishedDateInMillis);
            }
        };
    }
}