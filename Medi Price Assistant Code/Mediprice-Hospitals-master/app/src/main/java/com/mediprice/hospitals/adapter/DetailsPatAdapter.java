package com.mediprice.hospitals.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mediprice.hospitals.R;
import com.mediprice.hospitals.model.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DetailsPatAdapter extends RecyclerView.Adapter<DetailsPatAdapter.MyViewHolder> {
    ArrayList<model> datalist;
    Context context;

    public DetailsPatAdapter(ArrayList<model> datalist,Context context) {
        this.datalist = datalist;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public DetailsPatAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_info,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DetailsPatAdapter.MyViewHolder holder, int position) {
        holder.t1.setText(datalist.get(position).getName());
        holder.t2.setText(datalist.get(position).getAge());
        holder.t3.setText(datalist.get(position).getGender());
        holder.t4.setText(datalist.get(position).getSymptoms());
        holder.t6.setText(datalist.get(position).getPhone());
        holder.t7.setText(datalist.get(position).getGenId());

        ArrayList<String> paths = datalist.get(position).getImages();
        if(paths!=null){
            if(paths.size()==1) {
                holder.i1.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(paths.get(0))
                        .into(holder.i1);

            }
            else if(paths.size()==2){
                holder.i1.setVisibility(View.VISIBLE);
                holder.i2.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(paths.get(0))
                        .into(holder.i1);
                Glide.with(context)
                        .load(paths.get(1))
                        .into(holder.i2);
            }
        }
        model current = datalist.get(position);
        holder.setData(current,position);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
    public void filteredList(ArrayList<model> filterList) {
        datalist = filterList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView t1,t2,t3,t4,t6,t7;
        ImageView i1,i2;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(R.id.t1);
            t2 = itemView.findViewById(R.id.t2);
            t3 = itemView.findViewById(R.id.t3);
            t4 = itemView.findViewById(R.id.t4);
            t6 = itemView.findViewById(R.id.t6);
            t7 = itemView.findViewById(R.id.t7);
            i1 = itemView.findViewById(R.id.i1);
            i2 = itemView.findViewById(R.id.i2);
        }
        public void setData(model currentObject, int position) {

        }
    }
}
