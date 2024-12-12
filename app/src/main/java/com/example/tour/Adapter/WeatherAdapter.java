package com.example.tour.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tour.Class.Item;
import com.example.tour.Class.Weather;
import com.example.tour.R;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.viewholder> {
    ArrayList<Weather> items;
    Context context;

    public WeatherAdapter(ArrayList<Weather> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public WeatherAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_weather,parent,false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.viewholder holder, int position) {
        Weather item = items.get(position);
        holder.dayTxt.setText(item.getDate());
        holder.tempTxt.setText(item.getTemp()+"°");
        switch (item.getStatus().toLowerCase()) {
            case "clear":
                holder.weatherIcon.setImageResource(R.drawable.sunny);  // Ảnh cho trời quang đãng
                break;
            case "clouds":
                holder.weatherIcon.setImageResource(R.drawable.cloudy);  // Ảnh cho trời nhiều mây
                break;
            case "rain":
                holder.weatherIcon.setImageResource(R.drawable.rainy);  // Ảnh cho trời mưa
                break;
            case "thunderstorm":
                holder.weatherIcon.setImageResource(R.drawable.storm);  // Ảnh cho bão
                break;
            case "snow":
                holder.weatherIcon.setImageResource(R.drawable.snowy);  // Ảnh cho tuyết
                break;
            case "drizzle":
                holder.weatherIcon.setImageResource(R.drawable.rainy);  // Ảnh cho mưa phùn
                break;
            case "mist":
                holder.weatherIcon.setImageResource(R.drawable.windy);  // Ảnh cho sương mù
                break;
            default:
                holder.weatherIcon.setImageResource(R.drawable.cloudy_sunny);  // Ảnh mặc định nếu không khớp
                break;
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{
        TextView dayTxt, tempTxt;
        ImageView weatherIcon;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            dayTxt = itemView.findViewById(R.id.dayTxt);
            tempTxt = itemView.findViewById(R.id.tempTxt);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
        }
    }
}
