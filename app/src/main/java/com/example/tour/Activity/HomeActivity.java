package com.example.tour.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tour.Adapter.CategoryAdapter;
import com.example.tour.Adapter.PopularAdapter;
import com.example.tour.Adapter.RecommendedAdapter;
import com.example.tour.Adapter.SliderAdapter;
import com.example.tour.Class.Category;
import com.example.tour.Class.Item;
import com.example.tour.Class.Location;
import com.example.tour.Class.SliderItem;
import com.example.tour.R;
import com.example.tour.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });;

        database = FirebaseDatabase.getInstance();

        initLocation();
        initBanner();
        initCategory();
        initRecommended();
        initPopular();
        bottomMenu();
        onClickSearch();
    }



    private void onClickSearch() {
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = binding.searchTxt.getText().toString();
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            }
        });
    }

    private void initPopular() {
        DatabaseReference myRef = database.getReference("Item");
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        ArrayList<Item> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i:snapshot.getChildren()){
                        Item item = i.getValue(Item.class);
                        if(item.getIsPopular() == 1){
                            list.add(item);
                        }

                    }
                    if (!list.isEmpty()){
                        binding.recyclerViewPopular.setLayoutManager(new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.VERTICAL,false));
                        binding.recyclerViewPopular.setNestedScrollingEnabled(false);
                        RecyclerView.Adapter<PopularAdapter.Viewholder> adapter = new PopularAdapter(list);
                        binding.recyclerViewPopular.setAdapter(adapter);
                    }
                    binding.progressBarPopular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initRecommended() {
        DatabaseReference myRef = database.getReference("Item");
        binding.progressBarRecommended.setVisibility(View.VISIBLE);
        ArrayList<Item> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i:snapshot.getChildren()){
                        Item item = i.getValue(Item.class);
                        if(item.getIsRecommended() == 1){
                            list.add(item);
                        }
                    }
                    if (!list.isEmpty()){
                        binding.recyclerViewRecommended.setLayoutManager(new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.HORIZONTAL,false));
                        RecyclerView.Adapter<RecommendedAdapter.Viewholder> adapter = new RecommendedAdapter(list);
                        binding.recyclerViewRecommended.setAdapter(adapter);
                    }
                    binding.progressBarRecommended.setVisibility(View.GONE);
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

                if (itemId == R.id.home) {
                    return true;
                } else if (itemId == R.id.explorer) {
                    startActivity(new Intent(HomeActivity.this, ExplorerActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.profile) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.weather) {
                    startActivity(new Intent(HomeActivity.this, WeatherActivity.class));
                    finish();
                    return true;
                } else {
                    return false;
                }
            }
        });
        binding.bottomMenu.setSelectedItemId(R.id.home);
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
                        binding.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
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

    private void initLocation() {
        DatabaseReference myRef = database.getReference("Location");
        ArrayList<Location> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot i : snapshot.getChildren()){
                        list.add(i.getValue(Location.class));
                    }
                    ArrayAdapter<Location> adapter = new ArrayAdapter<>(HomeActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.locationSp.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initBanner(){
        DatabaseReference myRef = database.getReference("Banner");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItem> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot i : snapshot.getChildren()){
                        items.add(i.getValue(SliderItem.class));
                    }
                    binding.viewPagerSlider.setAdapter(new SliderAdapter(items,binding.viewPagerSlider));
                    binding.viewPagerSlider.setClipToPadding(false);
                    binding.viewPagerSlider.setClipChildren(false);
                    binding.viewPagerSlider.setOffscreenPageLimit(3);
                    binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                    compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                    binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
                    binding.progressBarBanner.setVisibility(View.GONE);
                    // Gọi hàm tự động chuyển slide
                    setupAutoSlider();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Handler sliderHandler = new Handler();

    private void setupAutoSlider() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = binding.viewPagerSlider.getCurrentItem();
                int totalItems = binding.viewPagerSlider.getAdapter().getItemCount();

                // Kiểm tra nếu chưa đến trang cuối thì chuyển sang trang tiếp theo
                if (currentItem < totalItems - 1) {
                    binding.viewPagerSlider.setCurrentItem(currentItem + 1);
                } else {
                    // Nếu đã đến trang cuối, quay về trang đầu tiên
                    binding.viewPagerSlider.setCurrentItem(0);
                }
                // Tiếp tục chạy lại sau 5 giây
                sliderHandler.postDelayed(this, 5000);
            }
        };

        // Bắt đầu tự động chuyển slide sau khi thiết lập ViewPager2
        sliderHandler.postDelayed(runnable, 5000);

        // Xử lý khi người dùng vuốt thủ công để dừng tự động cuộn trong khi vuốt
        binding.viewPagerSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Đặt lại thời gian khi người dùng vuốt
                sliderHandler.removeCallbacks(runnable);
                sliderHandler.postDelayed(runnable, 3000);
            }
        });
    }

}