package com.duke.dkskin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.duke.dkskin.R;

public class SkinSettingsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    public SkinSettingsFragment() {
        // Required empty public constructor
    }

    public static SkinSettingsFragment newInstance(String param1, String param2) {
        SkinSettingsFragment fragment = new SkinSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_skin_settings, container, false);
    }
}
