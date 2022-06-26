package com.haritha.harithaagriofficer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class MainFragment extends Fragment {

    private RelativeLayout rl_weather_home;
    private ProgressBar pb_Loading;
    private TextView txt_current_city_name, txt_current_temperature, txt_current_weather_condition;
    private TextInputEditText txt_search_city_name;
    private ImageView img_current_weather_icon, btn_search;
    private RecyclerView rv_weather;
    private ArrayList<Weather> weatherArrayList;
    private WeatherAdapter weatherAdapter;
    private String cityName;
    private GpsTracker gpsTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //Assign variables
        rl_weather_home = view.findViewById(R.id.rl_weather_home);
        pb_Loading = view.findViewById(R.id.pb_Loading);
        txt_current_city_name = view.findViewById(R.id.txt_current_city_name);
        txt_current_temperature = view.findViewById(R.id.txt_current_temperature);
        txt_current_weather_condition = view.findViewById(R.id.txt_current_weather_condition);
        txt_search_city_name = view.findViewById(R.id.txt_search_city_name);
        img_current_weather_icon = view.findViewById(R.id.img_current_weather_icon);
        btn_search = view.findViewById(R.id.btn_search);
        rv_weather = view.findViewById(R.id.rv_weather);

        weatherArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(getActivity(), weatherArrayList);
        rv_weather.setAdapter(weatherAdapter);

        try {
            if (ContextCompat.checkSelfPermission(gpsTracker.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        gpsTracker = new GpsTracker(getActivity());
        if(gpsTracker.canGetLocation()){
            double longitude= gpsTracker.getLongitude();
            double latitude = gpsTracker.getLatitude();
            cityName = getCityName(longitude,latitude);
            getWeatherInfo(cityName);
        }else{
            gpsTracker.showSettingsAlert();
        }



        btn_search.setOnClickListener(v -> {
            String city = Objects.requireNonNull(txt_search_city_name.getText()).toString().trim();
            if (city.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter city name", Toast.LENGTH_SHORT).show();
            } else {
                txt_current_city_name.setText(cityName);
                getWeatherInfo(city);
            }
        });

        ImageView img_rFarm = view.findViewById(R.id.img_rFarm);
        ImageView img_rNews = view.findViewById(R.id.img_rNews);
        ImageView img_rSettings = view.findViewById(R.id.img_rSettings);

        img_rFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment farmFragment = new FarmFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, farmFragment).addToBackStack(null).commit();

            }
        });

        img_rNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment newsFragment = new NewsFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, newsFragment).addToBackStack(null).commit();

            }
        });

        img_rSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment profileFragment = new ProfileFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, profileFragment).addToBackStack(null).commit();

            }
        });

        return view;
    }

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not found";
        Geocoder geocoder = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);
            for (Address address : addresses) {
                if (address != null) {
                    String city = address.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.d("TAG", "CITY NOT FOUND");
                        //Toast.makeText(getActivity(), "User City Not Found...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName) {
        String url = " http://api.weatherapi.com/v1/forecast.json?key=c29cd04a160e45c9b18140523220406&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        txt_current_city_name.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pb_Loading.setVisibility(View.GONE);
                rl_weather_home.setVisibility(View.VISIBLE);
                weatherArrayList.clear();

                try {
                    String currentTemperature = response.getJSONObject("current").getString("temp_c");
                    txt_current_temperature.setText(currentTemperature + "°c");
                    //int isDay = response.getJSONObject("current").getInt("is_day");
                    String currentCondition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String currentConditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(currentConditionIcon)).into(img_current_weather_icon);
                    txt_current_weather_condition.setText(currentCondition);

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temp = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String windSpeed = hourObj.getString("wind_kph");
                        weatherArrayList.add(new Weather(time, temp, img, windSpeed));
                    }
                    weatherAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> Toast.makeText(getActivity(), "Please enter valid city name", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }

}