package com.utar.assignment.Util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utar.assignment.Activity.MainActivity;
import com.utar.assignment.Activity.settleBill;
import com.utar.assignment.Activity.specified_group;
import com.utar.assignment.Model.Amount;
import com.utar.assignment.Model.User;
import com.utar.assignment.R;

import java.util.List;

public class friend_adapter extends RecyclerView.Adapter<friend_adapter.ViewHolder>{

    private List<User> userList;
    private List<String> amountList;
    List<String> userFriendList;
    String uid;
    private Context context;

    public friend_adapter(List<User> userList, List<String> amountList, List<String> userFriendList,String uid, Context context)
    {
        this.userList=userList;
        this.amountList = amountList;
        this.userFriendList = userFriendList;
        this.uid = uid;
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

                    PopupMenu popupMenu = new PopupMenu(context,v);
                    popupMenu.getMenuInflater().inflate(R.menu.friend_menu,popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.settle_bill:

                                    Intent intent2 = new Intent(context, settleBill.class);
                                    int a = getAdapterPosition();
                                    int b = userFriendList.size()-1;
                                    int c = b-a;
                                    intent2.putExtra("userID", userList.get(a).getUid());
                                    intent2.putExtra("position", c);
                                    context.startActivity(intent2);
                                    GeneralHelper.showMessage(context,"This is testing " + userList.get(a).getUsername());
                                    break;

                                case R.id.delete_friend:

                                    List<String> newUserList = userFriendList;
                                    int z = getAdapterPosition();
                                    int x = userFriendList.size()-1;
                                    int y = x-z;
                                    GeneralHelper.showMessage(context, "You have remove friend with email : " + newUserList.get(x-z));
                                    newUserList.remove(y);


                                    FirestoreHelper.addFriend(uid, newUserList, new FirebaseCallback() {
                                        @Override
                                        public void onResponse() {
                                            GeneralHelper.showMessage(context, "Successfully Deleted!");

                                            Intent intent = new Intent(context, MainActivity.class);
                                            context.startActivity(intent);

                                        }
                                    });
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
        return userList.size();
    }
}
