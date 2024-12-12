package com.example.tour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.tour.Adapter.WeatherAdapter;
import com.example.tour.Class.Weather;
import com.example.tour.R;
import com.example.tour.databinding.ActivityHomeBinding;
import com.example.tour.databinding.ActivityWeatherBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.ReferenceQueue;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class WeatherActivity extends AppCompatActivity {

    ActivityWeatherBinding binding;
    String cityName = "Ha Noi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityWeatherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomMenu();
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityName = binding.searchTxt.getText().toString();
                getWeather(cityName);
                getWeather5day(cityName);
            }
        });
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("keyword")) {
            String keyword = intent.getStringExtra("keyword");
            getWeather(keyword);
            getWeather5day(keyword);
        }else {
            getWeather(cityName);
            getWeather5day(cityName);
        }
    }


    private void getWeather5day(String cityName) {
        ArrayList<Weather> list = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(WeatherActivity.this);
        String url = "https://api.openweathermap.org/data/2.5/forecast?q="+cityName+"&units=metric&appid=4a5e8cb1889fca84ae325540fbb0d411";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*Log.d("result", response);*/
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArrayList = jsonObject.getJSONArray("list"); //Danh sách thời tiết 5 ngày/3 tiếng
                            for (int i = 0; i < jsonArrayList.length(); i+=3){ //Lược bớt phần tử
                                JSONObject jsonObjList = jsonArrayList.getJSONObject(i);

                                //Lấy thời gian
                                String dt = jsonObjList.getString("dt");
                                long l = Long.parseLong(dt);
                                Date date = new Date(l*1000L); //Đưa về mini giây
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd  HH:mm", Locale.US);
                                String currentDate = sdf.format(date);

                                //Lấy thời tiết
                                JSONArray jsonArrayWeather = jsonObjList.getJSONArray("weather");
                                JSONObject jsonObjWeather = jsonArrayWeather.getJSONObject(0);
                                String weather = jsonObjWeather.getString("main");

                                //Lấy nhiệt độ
                                JSONObject jsonObjMain = jsonObjList.getJSONObject("main");
                                String stringTemp = jsonObjMain.getString("temp");
                                Double a = Double.valueOf(stringTemp); //Làm tròn nhiệt độ
                                String temperature = String.valueOf(a.intValue());

                                list.add(new Weather(temperature, weather, currentDate));
                            }
                            binding.recyclerView5day.setLayoutManager(new LinearLayoutManager(WeatherActivity.this,LinearLayoutManager.HORIZONTAL,false));
                            WeatherAdapter adapter = new WeatherAdapter(list);
                            binding.recyclerView5day.setAdapter(adapter);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }


    private void getWeather(String cityName) {
        RequestQueue requestQueue = Volley.newRequestQueue(WeatherActivity.this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&units=metric&appid=4a5e8cb1889fca84ae325540fbb0d411";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*Log.d("result", response);*/
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            //Lấy thời gian hiện tại + thành phố
                            String dt = jsonObject.getString("dt");
                            long l = Long.parseLong(dt);
                            Date date = new Date(l*1000L); //Đưa về mini giây
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE | MMMM dd", Locale.US);
                            String currentDate = sdf.format(date);
                            binding.dateTxt.setText(currentDate);
                            String name = jsonObject.getString("name");
                            binding.cityName.setText(name);

                            //Lấy thời tiết
                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjWeather = jsonArrayWeather.getJSONObject(0);
                            String weather = jsonObjWeather.getString("main");
                            binding.weatherTxt.setText(weather);
                            switch (weather.toLowerCase()) {
                                case "clear":
                                    binding.weatherIcon.setImageResource(R.drawable.sunny);  // Ảnh cho trời quang đãng
                                    break;
                                case "clouds":
                                    binding.weatherIcon.setImageResource(R.drawable.cloudy);  // Ảnh cho trời nhiều mây
                                    break;
                                case "rain":
                                    binding.weatherIcon.setImageResource(R.drawable.rainy);  // Ảnh cho trời mưa
                                    break;
                                case "thunderstorm":
                                    binding.weatherIcon.setImageResource(R.drawable.storm);  // Ảnh cho bão
                                    break;
                                case "snow":
                                    binding.weatherIcon.setImageResource(R.drawable.snowy);  // Ảnh cho tuyết
                                    break;
                                case "drizzle":
                                    binding.weatherIcon.setImageResource(R.drawable.rainy);  // Ảnh cho mưa phùn
                                    break;
                                case "mist":
                                    binding.weatherIcon.setImageResource(R.drawable.windy);  // Ảnh cho sương mù
                                    break;
                                default:
                                    binding.weatherIcon.setImageResource(R.drawable.cloudy_sunny);  // Ảnh mặc định nếu không khớp
                                    break;
                            }
                            /*String icon = jsonObjWeather.getString("icon");
                            Picasso.get()
                                    .load("https://openweathermap.org/img/wn/"+icon+"@2x.png")
                                    .into(binding.weatherIcon);*/

                            //Lấy nhiệt độ + độ ẩm
                            JSONObject jsonObjMain = jsonObject.getJSONObject("main");
                            String stringTemp = jsonObjMain.getString("temp");
                            Double a = Double.valueOf(stringTemp); //Làm tròn nhiệt độ
                            String temperature = String.valueOf(a.intValue());
                            binding.temperatureTxt.setText(temperature+"°");
                            String humidity = jsonObjMain.getString("humidity");
                            binding.humidityTxt.setText(humidity+"%");

                            //Lấy tốc độ gió + mây
                            JSONObject jsonObjWind = jsonObject.getJSONObject("wind");
                            String wind = jsonObjWind.getString("speed");
                            JSONObject jsonObjClouds = jsonObject.getJSONObject("clouds");
                            String all = jsonObjClouds.getString("all");
                            binding.windspeedTxt.setText(wind+"m/s");
                            binding.cloudsTxt.setText(all+"%");

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WeatherActivity.this, "City name not found",Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    private void bottomMenu(){
        binding.bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.weather) {
                    return true;
                } else if (itemId == R.id.home) {
                    startActivity(new Intent(WeatherActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.profile) {
                    startActivity(new Intent(WeatherActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.explorer) {
                    startActivity(new Intent(WeatherActivity.this, ExplorerActivity.class));
                    finish();
                    return true;
                } else {
                    return false;
                }
            }
        });
        binding.bottomMenu.setSelectedItemId(R.id.weather);
    }
}