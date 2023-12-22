package com.example.notelearning;

import static com.example.notelearning.MainActivity.addTabFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTabFragment extends Fragment {
    String title;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_new_tab, container, false);

        v.findViewById(R.id.new_tab_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = ((EditText)v.findViewById(R.id.new_tab_title)).getText().toString();
                TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.main_tabs);

                if(!title.isEmpty()) {
                    tabLayout.addTab(tabLayout.newTab().setText(title));
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Users").child(MainActivity.uid).child("folder").child(title).setValue(new Folder(title));
                }
                else{
                    Toast.makeText(getActivity(), "Tab Name cannot be blank", Toast.LENGTH_SHORT).show();
                }
                getActivity().getSupportFragmentManager().beginTransaction().remove(addTabFragment).commitAllowingStateLoss();


            }
        });

        v.findViewById(R.id.new_tab_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(addTabFragment).commitAllowingStateLoss();
            }
        });

        return v;


    }

}
