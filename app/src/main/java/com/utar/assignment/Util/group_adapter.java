package com.utar.assignment.Util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utar.assignment.Activity.specified_group;
import com.utar.assignment.Model.Group;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;

import java.util.List;

public class group_adapter extends RecyclerView.Adapter<group_adapter.ViewHolder> {


    private List<Group> groupList;
    private Context context;

    public group_adapter(List<Group> groupList, Context context) {
        this.groupList = groupList;
        this.context = context;
    }

    @NonNull

    @Override
    public group_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_container, parent, false);
        return new group_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull group_adapter.ViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.txtName.setText(group.getGroupName());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.group_tv_id);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, specified_group.class);
                    int i = getAdapterPosition();
                    intent.putExtra("g_id", groupList.get(i).getGroupId());
                    intent.putExtra("name", groupList.get(i).getGroupName());
                    context.startActivity(intent);
                }
            });




        }
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}