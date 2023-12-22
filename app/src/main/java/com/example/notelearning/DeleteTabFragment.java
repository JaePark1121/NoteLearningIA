package com.example.notelearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DeleteTabFragment extends Fragment {
    View view;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_delete_tab, container, false);
        Button confirm = view.findViewById(R.id.tab_delete_btn);
        Button cancel = view.findViewById(R.id.tab_cancel_btn);

        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Users").child(MainActivity.uid).child("folder")
                        .child(MainActivity.curTab).setValue(null);

                getActivity().getSupportFragmentManager().beginTransaction().remove(MainActivity.deleteTabFragment).commitAllowingStateLoss();



            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(MainActivity.deleteTabFragment).commitAllowingStateLoss();
        }
        });




        return view;
    }

}

