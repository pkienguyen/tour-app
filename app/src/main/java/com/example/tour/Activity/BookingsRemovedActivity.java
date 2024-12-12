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

import com.example.tour.Adapter.TicketAdapter;
import com.example.tour.Class.Ticket;
import com.example.tour.R;
import com.example.tour.databinding.ActivityBookingsRemovedBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class BookingsRemovedActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityBookingsRemovedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBookingsRemovedBinding.inflate(getLayoutInflater());
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
        binding.myBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookingsRemovedActivity.this, BookingsActivity.class));
                finish();
            }
        });

        initTicket();
    }



    private void initTicket() {
        DatabaseReference myRef = database.getReference("Ticket");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        binding.progressBarTicket.setVisibility(View.VISIBLE);
        ArrayList<Ticket> list = new ArrayList<>();

        myRef.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot i:snapshot.getChildren()){
                        Ticket ticket = i.getValue(Ticket.class);
                        if (ticket.getEmail().equals(user.getEmail()) && ticket.getStatus() == 1){
                            list.add(ticket);
                        }
                    }
                    if (!list.isEmpty()){
                        // Đảo ngược danh sách để có thứ tự giảm dần
                        Collections.reverse(list);
                        binding.recyclerViewTicket.setLayoutManager(new LinearLayoutManager(BookingsRemovedActivity.this,LinearLayoutManager.VERTICAL,false));
                        RecyclerView.Adapter<TicketAdapter.Viewholder> adapter = new TicketAdapter(list);
                        binding.recyclerViewTicket.setAdapter(adapter);
                    }
                    binding.progressBarTicket.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}