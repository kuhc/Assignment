package com.utar.assignment.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utar.assignment.Activity.specified_group;
import com.utar.assignment.Activity.sub_activity;
import com.utar.assignment.Model.Group;
import com.utar.assignment.Model.MainActivity;
import com.utar.assignment.Model.SubActivity;
import com.utar.assignment.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class main_act_adapter extends RecyclerView.Adapter<main_act_adapter.ViewHolder>{
    private Context context;
    private List<MainActivity> main_act;
private String group_id;
    public main_act_adapter(Context context, List<MainActivity> main_act,String group_id) {
        this.context = context;
        this.main_act = main_act;
        this.group_id=group_id;
    }

    @Override
    public main_act_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_act_container, parent, false);
        return new main_act_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  main_act_adapter.ViewHolder holder, int position) {
MainActivity main_act1=main_act.get(position);
holder.txtName.setText(main_act1.getName());
    }
    public class ViewHolder  extends RecyclerView.ViewHolder{
        private TextView txtName;
        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.main_act_tv_id);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, sub_activity.class);
                    int i = getAdapterPosition();
                    MainActivity main_act1=main_act.get(i);

/*ArrayList<SubActivity> sub = new ArrayList<SubActivity>();


                    Bundle x = new Bundle();
x.putSerializable("list",(Serializable) main_act1.getSubActivityList());*/

                        //Toast.makeText(context, main_act1.getSubActivityList().get(0).getOwnerId() , Toast.LENGTH_SHORT).show();
                    //show sub 0 or sub 1


//
//                    int x = main_act1.getSubActivityList().size();
//                    String y = String.valueOf(x);
//                     Toast.makeText(context, y, Toast.LENGTH_SHORT).show();
//                      show 1,2,1


                   intent.putExtra("main_id",main_act1.getId());
                    intent.putExtra("group_id",group_id);

                    context.startActivity(intent);
                }
            });


        }
    }
    @Override
    public int getItemCount() {
        return main_act.size();
    }


}
