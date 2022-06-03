package com.haritha.haritha_farmer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CropsAdapter extends FirebaseRecyclerAdapter<Crop, CropsAdapter.CropsVH> {


    public CropsAdapter(@NonNull FirebaseRecyclerOptions<Crop> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CropsVH holder, int position, @NonNull Crop model) {

        //calculate remaining maturity days
        Calendar calendarToday = Calendar.getInstance();
        long currentDate = calendarToday.getTimeInMillis();
        long longPlantedDate = Long.parseLong(model.getPlanted_date());
        System.out.println("currentDate" + currentDate + " plantedDate ");
        long duration = currentDate - longPlantedDate;
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
        System.out.println("Difference " + diffInDays);
        int remainingMaturityDays = model.getDays_to_maturity() - (int) diffInDays;
        //  System.out.println("Remaining " + remainingMaturityDays);*/

        //get values to recyclerview
        holder.txt_rv_crop_heading.setText(model.getCrop_name());
        holder.txt_rv_days_to_maturity.setText(String.valueOf(remainingMaturityDays));
        long plantedDate = Long.parseLong(model.getPlanted_date());
        Date datePlantedDate = new Date(plantedDate);
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        holder.txt_rv_planted_date.setText(simpleDateFormat.format(datePlantedDate));


        holder.itemView.setOnClickListener(view -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, new CropViewFragment(
                            model.getCrop_id(),
                            model.getCrop_type(),
                            model.getCrop_name(),
                            model.getCrop_variety(),
                            model.getBotanical_name(),
                            model.getPlanted_date(),
                            model.getStart_method(),
                            model.getLight_profile(),
                            model.getPlanting_details(),
                            model.getDays_to_emerge(),
                            model.getHarvest_unit(),
                            model.getDays_to_maturity(),
                            model.getHarvest_window(),
                            model.getPlant_spacing(),
                            model.getRow_spacing(),
                            model.getPlanting_depth(),
                            model.getEstimated_revenue(),
                            model.getExpected_yield()))
                    .addToBackStack(null).commit();
        });
    }

    @NonNull
    @Override
    public CropsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_crops_card, parent, false);
        return new CropsVH(view);
    }

    public static class CropsVH extends RecyclerView.ViewHolder {

        private final TextView txt_rv_crop_heading, txt_rv_days_to_maturity, txt_rv_planted_date;

        public CropsVH(@NonNull View itemView) {
            super(itemView);
            txt_rv_crop_heading = itemView.findViewById(R.id.txt_rv_crop_heading);
            txt_rv_days_to_maturity = itemView.findViewById(R.id.txt_rv_days_to_maturity);
            txt_rv_planted_date = itemView.findViewById(R.id.txt_rv_planted_date);

        }
    }

}
