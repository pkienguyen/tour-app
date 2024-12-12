package com.example.tour.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tour.Activity.DetailActivity;
import com.example.tour.Activity.TicketActivity;
import com.example.tour.Activity.TicketPendingActivity;
import com.example.tour.Class.Item;
import com.example.tour.Class.Ticket;
import com.example.tour.R;
import com.example.tour.databinding.ViewholderPopularBinding;
import com.example.tour.databinding.ViewholderTicketBinding;

import java.util.ArrayList;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.Viewholder> {
    private final ArrayList<Ticket> tickets;
    private Context context;
    ViewholderTicketBinding binding;

    public TicketAdapter(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public TicketAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ViewholderTicketBinding.inflate(inflater,parent,false);

        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketAdapter.Viewholder holder, int position) {
        Ticket ticket = tickets.get(position);
        holder.binding.titleTxt.setText(ticket.getTitle());
        holder.binding.totalPriceTxt.setText("$"+ticket.getTotalPrice());
        holder.binding.dateTxt.setText(ticket.getDate());
        holder.binding.visitDateTxt.setText(ticket.getVisitDate());
        holder.binding.personsTxt.setText(""+ticket.getPersons());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TicketActivity.class);
                intent.putExtra("ticket", tickets.get(position));
                context.startActivity(intent);
            }
        });

        if (ticket.getStatus() == 1){
            holder.binding.imageView.setImageResource(R.drawable.ic_removed_img);
            holder.binding.totalPriceTxt.setText(ticket.getNote());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }

        if (ticket.getStatus() == 0){
            holder.binding.imageView.setImageResource(R.drawable.ic_pendingticket_img);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TicketPendingActivity.class);
                    intent.putExtra("ticket", tickets.get(position));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public final ViewholderTicketBinding binding;
        public Viewholder(ViewholderTicketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
