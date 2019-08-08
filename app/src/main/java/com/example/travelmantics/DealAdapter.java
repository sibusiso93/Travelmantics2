package com.example.travelmantics;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.ViewHolder>
{

    ArrayList<Deals> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    public  DealAdapter()
    {
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        this.deals = FirebaseUtil.mDeals;
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Deals deal = dataSnapshot.getValue(Deals.class);

                deal.setId(dataSnapshot.getKey());
                deals.add(deal);
                notifyItemInserted(deals.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        };
        mDatabaseReference.addChildEventListener(mChildListener);

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Deals deal = deals.get(position);
        holder.bind(deal);


    }

    @Override
    public int getItemCount()

    {
        return deals.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView Title;
        TextView Description;
        TextView price;
        ImageView Image;


        public ViewHolder(@NonNull View itemView)

        {
            super(itemView);
            Title = itemView.findViewById(R.id.Text_Dealname);
            Description = itemView.findViewById(R.id.text_Description);
            price = itemView.findViewById(R.id.text_Price);
            Image = itemView.findViewById(R.id.Image_Deal);
            itemView.setOnClickListener(this);
        }
        public void bind(Deals deal)
        {
            Title.setText(deal.getTitle());
            Description.setText(deal.getDescription());
            price.setText(deal.getPrice());
            Glide.with(Image.getContext())
                    .load(deal.getImageurl())
                    .into(Image);

        }
        @Override
        public void onClick(View view)
        {
            int position = getAdapterPosition();

            Deals selectedDeal = deals.get(position);
            Intent intent = new Intent(view.getContext(), ViewDeals.class);
            intent.putExtra("Deal", selectedDeal);
            view.getContext().startActivity(intent);

        }
    }
}
