package com.utar.assignment.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.utar.assignment.R;
import com.utar.assignment.Util.GeneralHelper;

public class GroupFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        GeneralHelper.showMessage(getActivity(),"This is Group");

        return inflater.inflate(R.layout.fragment_group, container, false);
    }
}