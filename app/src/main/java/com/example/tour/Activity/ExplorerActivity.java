package com.example.tour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tour.Adapter.CategoryAdapter;
import com.example.tour.Adapter.PopularAdapter;
import com.example.tour.Class.Category;
import com.example.tour.Class.Item;
import com.example.tour.R;
import com.example.tour.databinding.ActivityExplorerBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExplorerActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityExplorerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityExplorerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();

        bottomMenu();
        initCategory();
        initItem();
    }



    private void initItem() {
        DatabaseReference myRef = database.getReference("Item");
        binding.progressBarItem.setVisibility(View.VISIBLE);
        ArrayList<Item> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i:snapshot.getChildren()){
                        list.add(i.getValue(Item.class));
                    }
                    if (!list.isEmpty()){
                        binding.recyclerViewItem.setLayoutManager(new LinearLayoutManager(ExplorerActivity.this,LinearLayoutManager.VERTICAL,false));
                        RecyclerView.Adapter<PopularAdapter.Viewholder> adapter = new PopularAdapter(list);
                        binding.recyclerViewItem.setAdapter(adapter);
                    }
                    binding.progressBarItem.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void bottomMenu(){
        binding.bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.explorer) {
                    return true;
                } else if (itemId == R.id.home) {
                    startActivity(new Intent(ExplorerActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.profile) {
                    startActivity(new Intent(ExplorerActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.weather) {
                    startActivity(new Intent(ExplorerActivity.this, WeatherActivity.class));
                    finish();
                    return true;
                } else {
                    return false;
                }
            }
        });
        binding.bottomMenu.setSelectedItemId(R.id.explorer);
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot i : snapshot.getChildren()){
                        list.add(i.getValue(Category.class));
                    }
                    if(!list.isEmpty()){
                        binding.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(ExplorerActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        RecyclerView.Adapter<CategoryAdapter.Viewholder> adapter = new CategoryAdapter(list);
                        binding.recyclerViewCategory.setAdapter(adapter);
                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}