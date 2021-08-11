package com.utar.assignment.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utar.assignment.Model.User;
import com.utar.assignment.R;

import java.util.List;

public class member_adapter extends RecyclerView.Adapter<member_adapter.ViewHolder>{

    private List<User> userList;
    private Context context;

    public member_adapter(List<User> userList,Context context)
    {
this.userList=userList;
this.context=context;
    }
    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_container, parent, false);
        return new member_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  member_adapter.ViewHolder holder, int position) {
    User user=userList.get(position);
        holder.txtName.setText(user.getUsername());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.member_tv_id);
        }
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
}


