package com.example.tour.Activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tour.Class.Item;
import com.example.tour.Class.Ticket;
import com.example.tour.R;
import com.example.tour.databinding.ActivityDetailBinding;
import com.example.tour.databinding.ActivityMainBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;
    private Item item;
    private int quantity = 0;
    private int price = 0;
    private int totalPrice = 0;
    private Ticket ticket;
    String visitDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        item = (Item) getIntent().getSerializableExtra("item");

        binding.weatherTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = binding.addressTxt.getText().toString();
                Intent intent = new Intent(DetailActivity.this, WeatherActivity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            }
        });

        setVariable();
    }

    

    private void setVariable() {
        binding.titleTxt.setText(item.getTitle());
        binding.addressTxt.setText(item.getAddress());
        binding.ratingBar.setRating((float) item.getScore());
        binding.rateTxt.setText(""+item.getScore());
        binding.durationTxt.setText(item.getDuration());
        binding.distanceTxt.setText(item.getDistance());
        binding.bedTxt.setText(""+item.getBed());
        binding.descriptionTxt.setText(item.getDescription());
        binding.priceTxt.setText("$"+item.getPrice());
        binding.totalPriceTxt.setText("$"+item.getPrice());
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        // Thêm 1 tuần vào ngày hiện tại
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        // Chuyển đổi thành định dạng yyyy/MM/dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        visitDate = sdf.format(calendar.getTime());
        binding.dateTxt.setText(visitDate);

        Glide.with(DetailActivity.this)
                .load(item.getPic())
                .into(binding.pic);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.bookingCardView.setVisibility(View.VISIBLE);
                binding.overlayView.setVisibility(View.VISIBLE);
                binding.scrollView2.setOnTouchListener((v, event) -> true); //Chặn thao tác vuốt màn hình
                binding.backBtn.setClickable(false);
                ObjectAnimator animation = ObjectAnimator.ofFloat(binding.bookingCardView, "translationY", 1000f, 0f);
                animation.setDuration(300);  // Thời gian trượt lên (300ms)
                animation.start();
            }
        });
        binding.hideCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.bookingCardView.setVisibility(View.GONE);
                binding.overlayView.setVisibility(View.GONE);
                binding.scrollView2.setOnTouchListener(null); //Bỏ chặn thao tác vuốt màn hình
                binding.backBtn.setClickable(true);
            }
        });
        binding.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity < 5) {
                    quantity = Integer.parseInt(binding.quantityTxt.getText().toString());
                    quantity++;  // Tăng số lượng
                    binding.quantityTxt.setText(String.valueOf(quantity));

                    totalPrice = Integer.parseInt(binding.totalPriceTxt.getText().toString().replace("$", "").trim());
                    price = Integer.parseInt(binding.priceTxt.getText().toString().replace("$", "").trim());
                    totalPrice += price;  // Tăng giá trị theo mỗi lần nhấn
                    binding.totalPriceTxt.setText("$" + totalPrice);
                }
            }
        });
        binding.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 1) {
                    quantity = Integer.parseInt(binding.quantityTxt.getText().toString());
                    quantity--;  // Giảm số lượng
                    binding.quantityTxt.setText(String.valueOf(quantity));  // Cập nhật số lượng trên TextView

                    totalPrice = Integer.parseInt(binding.totalPriceTxt.getText().toString().replace("$", "").trim());
                    price = Integer.parseInt(binding.priceTxt.getText().toString().replace("$", "").trim());
                    totalPrice -= price;  // Tăng giá trị theo mỗi lần nhấn
                    binding.totalPriceTxt.setText("$" + totalPrice);
                }
            }
        });
        binding.bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = binding.titleTxt.getText().toString();
                visitDate = binding.dateTxt.getText().toString();
                String duration = binding.durationTxt.getText().toString();
                quantity = Integer.parseInt(binding.quantityTxt.getText().toString());
                totalPrice = Integer.parseInt(binding.totalPriceTxt.getText().toString().replace("$", ""));

                if (isValidDate(visitDate)) {
                    ticket = new Ticket(item.getId(), title, totalPrice, quantity, visitDate, duration);
                    Intent intent = new Intent(DetailActivity.this, PaymentActivity.class);
                    intent.putExtra("ticket", ticket);
                    startActivity(intent);
                } else {
                    Toast.makeText(DetailActivity.this, "Invalid date!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Hàm kiểm tra ngày hợp lệ
    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        sdf.setLenient(false);  // Không cho phép ngày không hợp lệ
        try {
            // Kiểm tra xem định dạng có đúng không
            Date inputDate = sdf.parse(date);

            // Lấy ngày hiện tại
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0); // Đặt giờ về 0 để tránh lỗi so sánh
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Tính ngày hiện tại + 30 ngày
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.DAY_OF_MONTH, 30);

            // So sánh ngày nhập
            if (!inputDate.before(today.getTime()) && inputDate.before(maxDate.getTime())) {
                return true;  // Ngày hợp lệ: không phải ngày đã qua và không vượt quá 30 ngày từ hôm nay
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;  // Ngày không hợp lệ
    }
}

