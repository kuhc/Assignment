package com.utar.assignment.Util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utar.assignment.Activity.settleBill;
import com.utar.assignment.Activity.specified_group;
import com.utar.assignment.Model.Amount;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;

import java.util.List;

public class friend_adapter extends RecyclerView.Adapter<friend_adapter.ViewHolder>{

    private List<User> userList;
    private List<String> amountList;
    private Context context;

    public friend_adapter(List<User> userList, List<String> amountList, Context context)
    {
        this.userList=userList;
        this.amountList = amountList;
        this.context=context;
    }
    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_list, parent, false);
        return new friend_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  friend_adapter.ViewHolder holder, int position) {

        User user = userList.get(position);
        holder.txtName.setText(user.getUsername());
        holder.amount.setText(amountList.get(position));
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private TextView amount;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.userFriend);
            amount = itemView.findViewById(R.id.amountOwn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, settleBill.class);
                    int i = getAdapterPosition();
                    intent.putExtra("userID", userList.get(i).getUid());
                    intent.putExtra("position", i);
                    context.startActivity(intent);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
}
