package com.example.tour.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tour.Class.Item;
import com.example.tour.Class.Payment;
import com.example.tour.Class.Ticket;
import com.example.tour.R;
import com.example.tour.Zalopay.CreateOrder;
import com.example.tour.databinding.ActivityDetailBinding;
import com.example.tour.databinding.ActivityPaymentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FirebaseUser user;
    ActivityPaymentBinding binding;
    private Ticket ticket;
    private ProgressDialog processDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //ZaloPay
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        processDialog = new ProgressDialog(this);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNum = binding.cardNum.getText().toString().trim();
                String expir = binding.expir.getText().toString().trim();
                String cvv = binding.cvv.getText().toString().trim();
                String firtName = binding.firtName.getText().toString().trim();
                String lastName = binding.lastName.getText().toString().trim();
                String zipCode = binding.zipCode.getText().toString().trim();
                if (cardNum.isEmpty() || expir.isEmpty() || cvv.isEmpty() || firtName.isEmpty() || lastName.isEmpty() || zipCode.isEmpty()){
                    Toast.makeText(PaymentActivity.this, "Please enter your card information", Toast.LENGTH_SHORT).show();
                    return;
                }
                confirmPayment("card");
            }
        });
        binding.zalopayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestZalopay();
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ticket = (Ticket) getIntent().getSerializableExtra("ticket");
        setVariable();
    }

    private void requestZalopay() {
        CreateOrder orderApi = new CreateOrder();
        int totalPrice = Integer.parseInt(binding.totalPriceTxt.getText().toString().replace("$", "").trim())*25000;
        try {
            JSONObject data = orderApi.createOrder(String.valueOf(totalPrice));
            String code = data.getString("return_code");
            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");

                ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        confirmPayment("e-wallet");
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {

                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }


    private void confirmPayment(String method){
        DatabaseReference myRef = database.getReference("Payment");

        int totalPrice = Integer.parseInt(binding.totalPriceTxt.getText().toString().replace("$", "").trim());
        String email = user.getEmail();
        // Lấy thời gian hiện tại
        Date currentTime = Calendar.getInstance().getTime();
        // Định dạng thời gian thành yyyy/MM/dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String date = sdf.format(currentTime);

        myRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int id = 100000;  // Nếu chưa có phần tử nào, bắt đầu từ 0
                for (DataSnapshot data:snapshot.getChildren()) {
                    // Lấy khóa cuối cùng
                    String lastKey = data.getKey();
                    if (lastKey != null) {
                        // Tạo khóa mới bằng cách tăng giá trị của khóa cuối cùng
                        id = Integer.parseInt(lastKey)+1;
                    }
                }
                Payment newPayment = new Payment(id, totalPrice, date, email, method);
                int paymentId = id;
                myRef.child(String.valueOf(id)).setValue(newPayment, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        createTicket(paymentId);
                        processDialog.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void createTicket(int paymentId){
        DatabaseReference myRef = database.getReference("Ticket");

        String title = binding.titleTxt.getText().toString();
        int totalPrice = Integer.parseInt(binding.totalPriceTxt.getText().toString().replace("$", "").trim());
        int persons = Integer.parseInt(binding.personsTxt.getText().toString());
        String visitDate = binding.visitDateTxt.getText().toString();
        String email = user.getEmail();
        // Lấy thời gian hiện tại
        Date currentTime = Calendar.getInstance().getTime();
        // Định dạng thời gian thành yyyy/MM/dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String date = sdf.format(currentTime);
        String duration = ticket.getDuration();

        processDialog.show();
        myRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int ticketId = 100000;  // Nếu chưa có phần tử nào, bắt đầu từ 0
                for (DataSnapshot data:snapshot.getChildren()) {
                    // Lấy khóa cuối cùng
                    String lastKey = data.getKey();
                    if (lastKey != null) {
                        // Tạo khóa mới bằng cách tăng giá trị của khóa cuối cùng
                        ticketId = Integer.parseInt(lastKey)+1;
                    }
                }
                Ticket newTicket = new Ticket(ticketId,title, totalPrice, persons, visitDate, email, date, 0, duration, paymentId);

                myRef.child(String.valueOf(ticketId)).setValue(newTicket, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        processDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                        builder.setTitle("Payment Sucessfull!");
                        builder.setMessage("Your payment has been completed");
                        builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.setCancelable(false);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference userRef = database.getReference("Users").child(user.getUid());
        // Lấy giá trị hiện tại của NoB
        userRef.child("numberOfBookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy giá trị NoB hiện tại
                    int currentNoB = dataSnapshot.getValue(Integer.class);

                    // Tăng NoB lên 1
                    int newNoB = currentNoB + 1;

                    // Cập nhật giá trị NoB vào cơ sở dữ liệu
                    userRef.child("numberOfBookings").setValue(newNoB);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference itemRef = database.getReference("Item").child(String.valueOf(ticket.getTourId()));
        // Lấy giá trị hiện tại của NoB
        itemRef.child("numberOfBookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy giá trị NoB hiện tại
                    int currentNoB = dataSnapshot.getValue(Integer.class);

                    // Tăng NoB lên 1
                    int newNoB = currentNoB + 1;

                    // Cập nhật giá trị NoB vào cơ sở dữ liệu
                    itemRef.child("numberOfBookings").setValue(newNoB);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setVariable() {
        binding.titleTxt.setText(ticket.getTitle());
        binding.visitDateTxt.setText(ticket.getVisitDate());
        binding.personsTxt.setText(""+ticket.getPersons());
        binding.totalPriceTxt.setText("$"+ticket.getTotalPrice());
    }
}