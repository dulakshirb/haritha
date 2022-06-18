package com.haritha.haritha_farmer;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private NewsAdapter newsAdapter;
    private String cityName, farmerDistrict;
    private GpsTracker gpsTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        DatabaseReference dbReferenceNews = FirebaseDatabase.getInstance().getReference("Officer").child("News");

        //get Current User district
        DatabaseReference dbReferenceUser = FirebaseDatabase.getInstance().getReference("Farmer").child("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        dbReferenceUser.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && (snapshot.hasChild("district"))){
                    farmerDistrict = Objects.requireNonNull(snapshot.child("district").getValue()).toString();
                    System.out.println("Farmer District " + farmerDistrict);
                    //news
                    RecyclerView newsRecyclerView = view.findViewById(R.id.rv_news);
                    newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    FirebaseRecyclerOptions<News> options = new FirebaseRecyclerOptions.Builder<News>()
                            .setQuery(dbReferenceNews.orderByChild("news_publish_to").equalTo(farmerDistrict), News.class)
                            .build();
                    newsAdapter = new NewsAdapter(options);
                    newsRecyclerView.setAdapter(newsAdapter);
                    newsAdapter.startListening();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
   //     newsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
//        newsAdapter.stopListening();
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