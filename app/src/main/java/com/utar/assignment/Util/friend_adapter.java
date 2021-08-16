package com.utar.assignment.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.utar.assignment.Activity.MainActivity;
import com.utar.assignment.Activity.settleBill;
import com.utar.assignment.Model.Amount;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;

import java.util.List;

public class friend_adapter extends RecyclerView.Adapter<friend_adapter.ViewHolder>{

    private List<User> userList;
    private List<Amount> amountList;
    List<String> userFriendList;
    String uid;
    List<String> amountListOwner;
    List<String> listOwnerUsername;
    private Context context;

    public friend_adapter(List<User> userList, List<Amount> amountList, List<String> userFriendList,String uid,List<String> amountListOwner, List<String> listOwnerUsername, Context context)
    {
        this.userList=userList;
        this.amountList = amountList;
        this.userFriendList = userFriendList;
        this.uid = uid;
        this.amountListOwner = amountListOwner;
        this.listOwnerUsername = listOwnerUsername;
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

        //User user = userList.get(position);
        holder.txtName.setText(listOwnerUsername.get(position));
        double amount = amountList.get(position).getAmount();
        String amountST = Double.toString(amount);
        holder.amount.setText(amountST);
        if(amount < 0)
        {
           holder.amount.setTextColor(Color.RED);
        }
        else
            holder.amount.setTextColor(Color.GREEN);
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

                    PopupMenu popupMenu = new PopupMenu(context,v);
                    popupMenu.getMenuInflater().inflate(R.menu.friend_menu,popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.settle_bill:

                                    Intent intent = new Intent(context, settleBill.class);
                                    int a = getAdapterPosition();
                                    int b = userFriendList.size()-1;
                                    int c = b-a;
                                    intent.putExtra("userID", userList.get(a).getUid());
                                    intent.putExtra("userName", listOwnerUsername.get(a));
                                    intent.putExtra("amounttoPay", amountList.get(a).getAmount());
                                    intent.putExtra("position", a);
                                    context.startActivity(intent);
                                    //GeneralHelper.showMessage(context,"This is testing " + userList.get(a).getUsername());
                                    break;

                            }
                            return true;
                        }
                    });
                    popupMenu.show();

                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return userFriendList.size();
    }
}
