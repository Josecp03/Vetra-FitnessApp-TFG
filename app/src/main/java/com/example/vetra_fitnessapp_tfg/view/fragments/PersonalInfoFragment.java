package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.vetra_fitnessapp_tfg.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PersonalInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalInfoFragment newInstance(String param1, String param2) {
        PersonalInfoFragment fragment = new PersonalInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);

        LinearLayout optionMan = view.findViewById(R.id.optionMan);
        LinearLayout optionWoman = view.findViewById(R.id.optionWoman);
        final View checkMan = view.findViewById(R.id.checkMan);
        final View checkWoman = view.findViewById(R.id.checkWoman);

        // Cuando se pulsa "Man"
        optionMan.setOnClickListener(v -> {
            checkMan.setVisibility(View.VISIBLE);
            checkWoman.setVisibility(View.GONE);
        });

        // Cuando se pulsa "Woman"
        optionWoman.setOnClickListener(v -> {
            checkMan.setVisibility(View.GONE);
            checkWoman.setVisibility(View.VISIBLE);
        });

        return view;
    }

}