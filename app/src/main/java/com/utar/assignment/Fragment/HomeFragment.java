package com.utar.assignment.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.utar.assignment.Activity.AddExpenses;
import com.utar.assignment.R;
import com.utar.assignment.Util.GeneralHelper;
import com.utar.assignment.Util.SplitCalHelper;

public class HomeFragment  extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = view.findViewById(R.id.btnAddPost);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), AddExpenses.class);
                startActivity(intent);

            }
        });

        return view;
    }
}
