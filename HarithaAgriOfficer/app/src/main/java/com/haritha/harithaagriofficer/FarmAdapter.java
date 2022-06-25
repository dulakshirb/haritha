package com.haritha.harithaagriofficer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class FarmAdapter extends FirebaseRecyclerAdapter<Farm, FarmAdapter.FarmVH> {

    public FarmAdapter(@NonNull FirebaseRecyclerOptions<Farm> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FarmAdapter.FarmVH holder, int position, @NonNull Farm model) {

        holder.txt_rv_farmName.setText(model.getFarmName());
        holder.txt_rv_farmDistract.setText(model.getDistrict());
        holder.txt_rv_farmOwner.setText(model.getUserName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, new FarmViewFragment(
                                model.getUserId(),
                                model.getFarmName(),
                                model.getUserName(),
                                model.getEmail(),
                                model.getMobile(),
                                model.getGender(),
                                model.getDistrict(),
                                model.getLocation()))
                        .addToBackStack(null).commit();
            }
        });
    }

    @NonNull
    @Override
    public FarmAdapter.FarmVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_farm_card, parent, false);
        return new FarmVH(view);
    }

    public static class FarmVH extends RecyclerView.ViewHolder {

        private final TextView txt_rv_farmName, txt_rv_farmDistract, txt_rv_farmOwner;

        public FarmVH(@NonNull View itemView) {
            super(itemView);
            txt_rv_farmName = itemView.findViewById(R.id.txt_rv_farmName);
            txt_rv_farmDistract = itemView.findViewById(R.id.txt_rv_farmDistract);
            txt_rv_farmOwner = itemView.findViewById(R.id.txt_rv_farmOwner);
        }
    }
}
