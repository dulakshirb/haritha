package com.haritha.haritha_farmer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.MissingResourceException;

public class CropsFragment extends Fragment {

    private View view;
    private RecyclerView cropsList;
    private DatabaseReference dbref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crops, container, false);

        //Add New button
        Button btnAddCrop = (Button) view.findViewById(R.id.btn_add_crop);
        btnAddCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment cropsAddFragment = new CropsAddFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, cropsAddFragment).addToBackStack(null).commit();
            }
        });

        //db query
        dbref = FirebaseDatabase.getInstance().getReference("Farmer").child("Crops");
        //crops recycleView
        cropsList = (RecyclerView) view.findViewById(R.id.rv_crops);
        cropsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Crops> options =
                new FirebaseRecyclerOptions.Builder<Crops>()
                        .setQuery(dbref, Crops.class)
                        .build();

        FirebaseRecyclerAdapter<Crops, CropsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Crops, CropsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CropsViewHolder holder, int position, @NonNull Crops model) {
                        final String cropsIDs = getRef(position).getKey();

                        dbref.child(cropsIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                final String retCropName = snapshot.child("cropName").getValue().toString();
                                final String retDaysToMaturity = snapshot.child("daysToMaturity").getValue().toString();
                                final String retPlantedDate = snapshot.child("plantedDate").getValue().toString();

                                holder.cropName.setText(retCropName);
                                holder.daysToMaturity.setText(retDaysToMaturity);
                                holder.plantedDate.setText(retPlantedDate);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CropsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_crops_card, parent, false);
                        return new CropsViewHolder(view);
                    }
                };

        cropsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class CropsViewHolder extends RecyclerView.ViewHolder {

        TextView cropName, daysToMaturity, plantedDate;

        public CropsViewHolder(@NonNull View itemView) {
            super(itemView);

            cropName = (TextView) itemView.findViewById(R.id.txt_rv_crop_heading);
            daysToMaturity = (TextView) itemView.findViewById(R.id.txt_rv_days_to_maturity);
            plantedDate = (TextView) itemView.findViewById(R.id.txt_rv_planted_date);
        }
    }
}