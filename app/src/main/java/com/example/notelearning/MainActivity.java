package com.example.notelearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    MainRecyclerFragment fragment1;
    public TabLayout tabLayout;
    private FragmentTransaction transaction;

    private FragmentTransaction transaction3;

    private FragmentTransaction transaction4;



    public static AddTabFragment addTabFragment;

    public static DeleteTabFragment deleteTabFragment;

    public static LogOutFragment logOutFragment;

    public static String uid;
    public static String curTab=null;

    public static HashMap<String, Folder> userData = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.main_tabs);



        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        curTab = String.valueOf(tabLayout.getSelectedTabPosition());

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(uid)
                .child("folder").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userData.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            if(ds.child("name").getValue()!=null){
                                userData.put(ds.child("name").getValue().toString(), ds.getValue(Folder.class));
                            }

                        }

                        refreshTabs(curTab);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        fragment1 = new MainRecyclerFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView, fragment1).commit();

        ImageView home = (ImageView) findViewById(R.id.home_icon);
        ImageButton add = (ImageButton) findViewById(R.id.add_tab_btn);
        ImageView logoutBtn = (ImageView) findViewById(R.id.LogoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction4 = getSupportFragmentManager().beginTransaction();
                logOutFragment = new LogOutFragment();
                transaction4.add(R.id.LogoutContainer, logOutFragment);
                logOutFragment.listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getSupportFragmentManager().beginTransaction().remove(logOutFragment).commitAllowingStateLoss();
                    }
                };
                transaction4.commit();

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction = getSupportFragmentManager().beginTransaction();
                addTabFragment = new AddTabFragment();
                transaction.add(R.id.new_tab_container, addTabFragment);
                transaction.commit();
                //tabLayout.addTab(tabLayout.newTab().setText("New"));

            }
        });





        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                curTab = tab.getText().toString();
                fragment1.refresh();
            }




            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }
    public void refreshTabs(String currentTab) {
        TabLayout.Tab selectedTab = null;
        tabLayout.removeAllTabs();
        // Add default tabs
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Bookmark"));

        // Add user specific tabs
        for (String folderName : userData.keySet()) {
            // Check if the tab already exists to avoid duplicates
            boolean exists = false;
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                if (tabLayout.getTabAt(i).getText().toString().equals(folderName)) {
                    exists = true;
                    break;
                }
            }
            // If the tab doesn't already exist, add it
            if (!exists) {
                tabLayout.addTab(tabLayout.newTab().setText(folderName));
            }

        }
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            // Chat GPT: Unfortunately, you can't get the View directly, so you need to loop through the ViewGroup
            ViewGroup tabViewGroup = (ViewGroup) tabLayout.getChildAt(0);
            View tabView = tabViewGroup.getChildAt(i);
            if (tabView != null) {
                final int finalI = i;
                tabView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!MainActivity.curTab.equals("Home") && !MainActivity.curTab.equals("Bookmark")) {

                            transaction3 = getSupportFragmentManager().beginTransaction();
                            deleteTabFragment = new DeleteTabFragment();
                            transaction3.add(R.id.delete_tab_container, deleteTabFragment);
                            transaction3.commit();

                        }

                        return true;
                    }
                });
            }
        }
        for(int i = 0; i < tabLayout.getTabCount(); i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if(tab.getText().toString().equals(currentTab)){
                selectedTab = tab;
            }
        }


       tabLayout.selectTab(selectedTab);


    }



}