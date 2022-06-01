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

public class CropsAdapter extends FirebaseRecyclerAdapter<Crop, CropsAdapter.CropsVH> {


    public CropsAdapter(@NonNull FirebaseRecyclerOptions<Crop> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CropsVH holder, int position, @NonNull Crop model) {
        holder.crop_name.setText(model.getCrop_name());
        holder.days_to_maturity.setText(model.getDays_to_maturity().toString());
        holder.planted_date.setText(model.getPlanted_date());

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

        private final TextView crop_name, days_to_maturity, planted_date;

        public CropsVH(@NonNull View itemView) {
            super(itemView);
            crop_name = itemView.findViewById(R.id.txt_rv_crop_heading);
            days_to_maturity = itemView.findViewById(R.id.txt_rv_days_to_maturity);
            planted_date = itemView.findViewById(R.id.txt_rv_planted_date);

        }
    }

}
