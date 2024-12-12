package com.example.tour.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tour.Class.Ticket;
import com.example.tour.R;
import com.example.tour.databinding.ActivityTicketBinding;
import com.example.tour.databinding.ActivityTicketPendingBinding;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TicketPendingActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityTicketPendingBinding binding;
    private Ticket ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTicketPendingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTicket();
            }
        });

        database = FirebaseDatabase.getInstance();
        ticket = (Ticket) getIntent().getSerializableExtra("ticket");

        setVariable();
    }


    private void cancelTicket() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TicketPendingActivity.this);
        builder.setTitle("Cancel confirmation");
        builder.setMessage("Are you sure you want to cancel this booking?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference myRef = database.getReference("Ticket");

                myRef.child(String.valueOf(ticket.getId())).child("status").setValue(1);
                myRef.child(String.valueOf(ticket.getId())).child("note").setValue("Cancelled by the customer", new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        startActivity(new Intent(TicketPendingActivity.this, BookingsRemovedActivity.class));
                        finish();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng dialog
                dialog.dismiss();
            }
        });
        // Đảm bảo người dùng không thể đóng dialog khi bấm ngoài
        builder.setCancelable(false);
        // Tạo và hiển thị dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setVariable() {
        binding.titleTxt.setText(ticket.getTitle());
        binding.visitDateTxt.setText(ticket.getVisitDate());
        binding.durationTxt.setText(ticket.getDuration());
        binding.personsTxt.setText(""+ticket.getPersons());
        binding.idTxt.setText("Order Id: "+ticket.getId());
    }
}