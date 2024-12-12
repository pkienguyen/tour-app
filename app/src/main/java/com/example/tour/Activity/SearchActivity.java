package com.example.tour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tour.Adapter.PopularAdapter;
import com.example.tour.Class.Item;
import com.example.tour.R;
import com.example.tour.databinding.ActivitySearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivitySearchBinding binding;
    String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");
        binding.searchTxt.setText(keyword);

        onClickSearch();
        initItem();
    }



    private void onClickSearch() {
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword = binding.searchTxt.getText().toString();
                Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
                finish();
            }
        });
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
                        Item item = i.getValue(Item.class);
                        if(item.getTitle().toLowerCase().contains(keyword.toLowerCase())){
                            list.add(item);
                        }
                    }
                    if (!list.isEmpty()){
                        binding.recyclerViewItem.setLayoutManager(new LinearLayoutManager(SearchActivity.this,LinearLayoutManager.VERTICAL,false));
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
}