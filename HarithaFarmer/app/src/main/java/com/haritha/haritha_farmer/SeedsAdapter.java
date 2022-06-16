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

public class SeedsAdapter extends FirebaseRecyclerAdapter<Seeds, SeedsAdapter.SeedsVH>  {
    public SeedsAdapter(FirebaseRecyclerOptions<Seeds> options){super(options);}

    protected void onBindViewHolder(@NonNull SeedsAdapter.SeedsVH holder, int position, @NonNull Seeds model){
        holder.seed_name.setText(model.getSeed_name());
        holder.seed_type.setText(model.getSeed_type());
        holder.seed_amount.setText(model.getSeed_amount().toString());

        holder.itemView.setOnClickListener(view ->{
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ViewSeedsFragment(
                            model.getSeed_id(),
                            model.getSeed_name(),
                            model.getSeed_description(),
                            model.getSeed_variety(),
                            model.getSeed_type(),
                            model.getSeed_amount(),
                            model.getSeed_restockAmount()))
                    .addToBackStack(null).commit();
        } );

    }

    public SeedsAdapter.SeedsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_seeds_card, parent, false);
        return new SeedsAdapter.SeedsVH(view);
    }

    public static class SeedsVH extends RecyclerView.ViewHolder {

        private final TextView seed_name, seed_type, seed_amount;

        public SeedsVH(@NonNull View itemView) {
            super(itemView);
            seed_name = itemView.findViewById(R.id.txt_rv_seeds_heading);
            seed_type = itemView.findViewById(R.id.txt_seeds_type);
            seed_amount = itemView.findViewById(R.id.txt_seeds_amount);

        }
    }

}
