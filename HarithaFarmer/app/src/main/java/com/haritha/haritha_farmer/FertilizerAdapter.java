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

public class FertilizerAdapter extends FirebaseRecyclerAdapter<Fertilizer, FertilizerAdapter.FertilizerVH> {

    public FertilizerAdapter(FirebaseRecyclerOptions<Fertilizer> options){super(options);}

    @Override
    protected void onBindViewHolder(@NonNull FertilizerAdapter.FertilizerVH holder, int position, @NonNull Fertilizer model) {
        holder.fertilizer_name.setText(model.getFertilizer_name());
        holder.fertilizer_exp.setText(model.getFertilizer_crops_touse());
        holder.fertilizer_amount.setText(model.getFertilizer_amount().toString());

        holder.itemView.setOnClickListener(view ->{
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ViewFertilizerFragment(
                            model.getFertilizer_id(),
                            model.getFertilizer_name(),
                            model.getFertilizer_description(),
                            model.getFertilizer_crops_touse(),
                            model.getFertilizer_amount(),
                            model.getFertilizer_restock_amount()))
                    .addToBackStack(null).commit();
        } );
    }

    public FertilizerAdapter.FertilizerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_fertilizer_card, parent, false);
        return new FertilizerAdapter.FertilizerVH(view);
    }

    public static class FertilizerVH extends RecyclerView.ViewHolder {

        private final TextView fertilizer_name, fertilizer_exp, fertilizer_amount;

        public FertilizerVH(@NonNull View itemView) {
            super(itemView);
            fertilizer_name = itemView.findViewById(R.id.txt_rv_fertilizer_heading);
            fertilizer_exp = itemView.findViewById(R.id.txt_fertilizer_exp);
            fertilizer_amount = itemView.findViewById(R.id.txt_fertilizer_amount);

        }
    }
}
