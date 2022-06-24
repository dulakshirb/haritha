package com.haritha.haritha_farmer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BiogasAdapter extends FirebaseRecyclerAdapter<BiogasSell, BiogasAdapter.BiogasSellVH> {

    public BiogasAdapter(@NonNull FirebaseRecyclerOptions<BiogasSell> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BiogasAdapter.BiogasSellVH holder, int position, @NonNull BiogasSell model) {

        //get listedDate
        long getPublishedDate = Long.parseLong(model.listed_date);
        Date listedDate = new Date(getPublishedDate);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        holder.txt_rv_cylinderCount.setText(String.valueOf(model.cylinders));
        holder.txt_rv_listedDate.setText(dateFormat.format(listedDate));

        final DatabaseReference sellCylinderRef = getRef(position);
        final String sellCylinderKey = sellCylinderRef.getKey();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference dbReferenceBioGasSell = FirebaseDatabase.getInstance().getReference("Farmer").child("BioGas").child(mAuth.getCurrentUser().getUid()).child("sell");

        holder.btn_removeSellItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbReferenceBioGasSell.child(sellCylinderKey).removeValue();
            }
        });

    }

    @NonNull
    @Override
    public BiogasAdapter.BiogasSellVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_biogas_card, parent, false);
        return new BiogasSellVH(view);
    }

    public static class BiogasSellVH extends RecyclerView.ViewHolder {

        private final TextView txt_rv_cylinderCount, txt_rv_listedDate;
        private final Button btn_removeSellItem;

        public BiogasSellVH(@NonNull View itemView) {
            super(itemView);
            txt_rv_cylinderCount = itemView.findViewById(R.id.txt_rv_cylinderCount);
            txt_rv_listedDate = itemView.findViewById(R.id.txt_rv_listedDate);
            btn_removeSellItem = itemView.findViewById(R.id.btn_removeSellItem);

        }
    }
}
