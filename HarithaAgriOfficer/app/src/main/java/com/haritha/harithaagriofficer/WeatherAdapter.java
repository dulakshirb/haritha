package com.haritha.harithaagriofficer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Weather> weatherArrayList;

    public WeatherAdapter(Context context, ArrayList<Weather> weatherArrayList) {
        this.context = context;
        this.weatherArrayList = weatherArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_weather_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Weather weather = weatherArrayList.get(position);

        holder.rv_txt_temperature.setText(weather.getTemperature() + "°c");
        holder.rv_txt_windSpeed.setText(weather.getWindSpeed() + "Km/h");
        Picasso.get().load("http:".concat(weather.getIcon())).into(holder.rv_img_condition);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date date = input.parse(weather.getTime());
            holder.rv_txt_time.setText(output.format(date));
        }catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rv_txt_time, rv_txt_temperature, rv_txt_windSpeed;
        private ImageView rv_img_condition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rv_txt_time = itemView.findViewById(R.id.rv_txt_time);
            rv_txt_temperature = itemView.findViewById(R.id.rv_txt_temperature);
            rv_txt_windSpeed = itemView.findViewById(R.id.rv_txt_windSpeed);
            rv_img_condition = itemView.findViewById(R.id.rv_img_condition);
        }
    }
}
