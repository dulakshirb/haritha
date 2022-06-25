package com.haritha.harithaagriofficer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FarmBiogasSellAdapter extends FirebaseRecyclerAdapter<FarmBiogasSell, FarmBiogasSellAdapter.FarmBiogasSellVH> {

    public FarmBiogasSellAdapter(@NonNull FirebaseRecyclerOptions<FarmBiogasSell> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FarmBiogasSellVH holder, int position, @NonNull FarmBiogasSell model) {
        //get listedDate
        long getPublishedDate = Long.parseLong(model.listed_date);
        Date listedDate = new Date(getPublishedDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        holder.txt_rv_cylinderCount.setText(String.valueOf(model.cylinders));
        holder.txt_rv_listedDate.setText(dateFormat.format(listedDate));
    }

    @NonNull
    @Override
    public FarmBiogasSellVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_biogas_card, parent, false);
        return new FarmBiogasSellVH(view);
    }

    public class FarmBiogasSellVH extends RecyclerView.ViewHolder {

        private final TextView txt_rv_cylinderCount, txt_rv_listedDate;

        public FarmBiogasSellVH(@NonNull View itemView) {
            super(itemView);
            txt_rv_cylinderCount = itemView.findViewById(R.id.txt_rv_cylinderCount);
            txt_rv_listedDate = itemView.findViewById(R.id.txt_rv_listedDate);
        }
    }
}
