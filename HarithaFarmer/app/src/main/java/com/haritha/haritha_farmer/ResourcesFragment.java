package com.haritha.haritha_farmer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class ResourcesFragment extends Fragment {
    private View view;
    private ImageButton img_inventory,img_fertilizer,img_tools,img_vehicle,img_seeds;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_resources, container, false);

        super.onCreate(savedInstanceState);
            img_fertilizer = view.findViewById(R.id.img_fertilizer);
            img_inventory = view.findViewById(R.id.img_inventory);
            img_seeds = view.findViewById(R.id.img_seeds);
            img_tools = view.findViewById(R.id.img_tools);
            img_vehicle = view.findViewById(R.id.img_vehicle);

            img_vehicle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment vehicleFragment = new Vehicles();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, vehicleFragment).addToBackStack(null).commit();
                }
            });
            img_tools.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment toolsFragment = new ToolsFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, toolsFragment).addToBackStack(null).commit();
                }
            });
            img_seeds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment seedsFragment = new SeedsFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, seedsFragment).addToBackStack(null).commit();
                }
            });
            img_inventory.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   AppCompatActivity activity = (AppCompatActivity) view.getContext();
                   Fragment inventoryFragment = new InventoryFertilizerFragment();
                   activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, inventoryFragment).addToBackStack(null).commit();
               }
           });
            img_fertilizer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment fertilizerFragment = new FertilizerFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, fertilizerFragment).addToBackStack(null).commit();

                }
            });




return view;
    }
}