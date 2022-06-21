package com.haritha.haritha_farmer;

import android.os.Bundle;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;;


public class InventoryFertilizerFragment extends Fragment {

    private View view;
    private AppCompatButton calendar_fertilizerReceivedDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button btnInventoryFertilizerSave;
    private EditText inFertilizerAmount, inFertilizerCost;
    private Spinner inFertilizerName;
    private ProgressDialog loadingBar;

    private String currentUserId, in_fertilizer_name, in_fertilizer_date;
    private Float in_fertilizer_amount, in_fertilizer_cost;
    private FirebaseAuth mAuth;
    private DatabaseReference dbInventoryFertilizer;


    public InventoryFertilizerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_inventory_fertilizer, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        dbInventoryFertilizer = FirebaseDatabase.getInstance().getReference("Farmer").child("InventoryFertilizer").child(currentUserId);

        initializeFields();

        btnInventoryFertilizerSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInventoryFertilizer();
            }
        });
        return view;
    }
    private void initializeFields(){

        inFertilizerName = (Spinner) view.findViewById(R.id.spinner_inventoryFertilizerName);

        inFertilizerAmount = (EditText)view.findViewById(R.id.txt_inventoryFertilizerAmount);
        inFertilizerCost = (EditText)view.findViewById(R.id.txt_inventoryFertilizerCost) ;
        btnInventoryFertilizerSave = (Button) view.findViewById(R.id.btn_inventory_fertilizer_save);
        loadingBar = new ProgressDialog(getActivity());

    }

    private void selectPlantedDate() {
        //Set current date by default
        calendar_fertilizerReceivedDate = view.findViewById(R.id.calendar_fertilizerReceivedDate);
        Calendar calendarToday = Calendar.getInstance();
        long currentDateInMillis = calendarToday.getTimeInMillis();
        Date currentDate = new Date(currentDateInMillis);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strCurrentDate = dateFormat.format(currentDate);
        calendar_fertilizerReceivedDate.setText(strCurrentDate);
        in_fertilizer_date = String.valueOf(currentDateInMillis);

        //open DatePickerDialog
        calendar_fertilizerReceivedDate.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, dateSetListener, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        //Set Selected Date
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            final Calendar calendarFertilizerReceivedDate = Calendar.getInstance();

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendarFertilizerReceivedDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                long longFertilizerReceivedDate = calendarFertilizerReceivedDate.getTimeInMillis();
                Date dateFertilizerReceivedDate = new Date(longFertilizerReceivedDate);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String strFertilizerReceivedDate = dateFormat.format(dateFertilizerReceivedDate);
                calendar_fertilizerReceivedDate.setText(strFertilizerReceivedDate);
                System.out.println("calFertilizerReceivedDate" + strFertilizerReceivedDate);
                in_fertilizer_date = String.valueOf(longFertilizerReceivedDate);
            }
        };
    }

    private void sendUsertoFertilizerFragment() {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment fertilizerFragment = new FertilizerFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame,fertilizerFragment).addToBackStack(null).commit();
    }
    private void saveInventoryFertilizer(){
        in_fertilizer_name = inFertilizerName.getSelectedItem().toString();

        in_fertilizer_amount = Float.valueOf(0);
        in_fertilizer_cost = Float.valueOf(0);



        if (inFertilizerAmount.length() > 0)
            in_fertilizer_amount = Float.parseFloat(inFertilizerAmount.getText().toString().trim());
        if (inFertilizerCost.length() > 0)
            in_fertilizer_cost = Float.parseFloat(inFertilizerCost.getText().toString().trim());

        loadingBar.setTitle("Please wait");
        loadingBar.setMessage("Saving...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        String in_fertilizer_id = dbInventoryFertilizer.push().getKey();
        InventoryFertilizer inventoryFertilizer = new InventoryFertilizer(in_fertilizer_id, in_fertilizer_name, in_fertilizer_date, in_fertilizer_amount, in_fertilizer_cost);
        dbInventoryFertilizer.child(in_fertilizer_id).setValue(inventoryFertilizer);
        sendUsertoFertilizerFragment();
        loadingBar.dismiss();

    }
}