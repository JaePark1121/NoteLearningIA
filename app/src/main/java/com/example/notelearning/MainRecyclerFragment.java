package com.example.notelearning;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainRecyclerFragment extends Fragment{
    private int count = 0;

    RecyclerView recyclerView;
    static int uidIndex;

    private View v;
    static NoteAdapter adapter = new NoteAdapter();

    static Toolbar toolbar;
    static FloatingActionButton recordButton;

    static ArrayList<String> memoUID = new ArrayList<>();




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main_recyclerview, container, false);
        recyclerView = v.findViewById(R.id.save_recyclerView);
        recordButton = v.findViewById(R.id.record_button_recycler);
        recordButton.setVisibility(View.VISIBLE);


        TextView notebook = v.findViewById(R.id.notebook_box);

        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.INVISIBLE);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

             if (itemId == R.id.delete) {
                    ConfirmDeleteFragment confirmDeleteFragment = new ConfirmDeleteFragment();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                    // Add the child fragment to the parent fragment
                    transaction.replace(R.id.child_fragment_container, confirmDeleteFragment).commit();
                    // Do something for "delete" selection
                    confirmDeleteFragment.listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                            transaction.remove(confirmDeleteFragment).commit();
                        }
                    };
                    return true;
                }

                else if (itemId == R.id.move_folder) {
                   ChangeFolderFragment changeFolderFragment = new ChangeFolderFragment();
                   FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                   transaction.replace(R.id.child_fragment_container, changeFolderFragment).commit();
                   changeFolderFragment.listener = new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                           transaction.remove(changeFolderFragment).commit();
                       }
                   };


                   return true;
                }
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.bottom_nav_menu); // Inflate your menu resource


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);


        TabLayout tabs = v.findViewById(R.id.main_tabs);







        adapter.setOnItemClickListener(new OnNotesItemClickListener(){
            @Override
            public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position) {

                Note item = adapter.getItem(position);


                Intent intent = new Intent(getContext(), SaveAudioActivity.class);
                ArrayList<String> memoUID1 = new ArrayList<>();
                memoUID1.add(NoteAdapter.noteKeys.get(position));
                uidIndex = 0;
                intent.putExtra("memoID", memoUID1);
                intent.putExtra("title",item.getTitle());
                intent.putExtra("mode", "edit");
                startActivity(intent);
            }
            @Override
            public void onLongClick(){
                toolbar.setVisibility(View.VISIBLE);
                recordButton.setVisibility(View.INVISIBLE);

            }
            @Override
            public void close() {

                toolbar.setVisibility(View.INVISIBLE);
                recordButton.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean onKeyDown(int keycode, KeyEvent event) {
                if(keycode ==KeyEvent.KEYCODE_BACK) {
                    toolbar.setVisibility(View.INVISIBLE);
                    recordButton.setVisibility(View.VISIBLE);
                    return true;
                }

                return false;
            }


        });






          recordButton = (FloatingActionButton) v.findViewById(R.id.record_button_recycler); //fragment에서 findViewByid는 view.을 이용해서 사용

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showNewNotesChoiceDialog();

            }
        });



        return v;
    }
    public void refresh() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        adapter.selected.clear();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.items.clear();
                adapter.noteKeys.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Memos memo = ds.getValue(Memos.class);
                    adapter.addItem(new Note(memo.getTitle(), memo.getDate(), memo.getIsBookmarked()),ds.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        };

        if ((MainActivity.curTab).equals("Bookmark")) {
            ref.child("Users").child(MainActivity.uid).child("folder").child(MainActivity.curTab).child("memos")
                    .orderByChild("isBookmarked").equalTo(true).addValueEventListener(eventListener);
        } else {
            ref.child("Users").child(MainActivity.uid).child("folder").child(MainActivity.curTab).child("memos")
                    .addValueEventListener(eventListener);
        }
    }

    private void showNewNotesChoiceDialog () {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); // Apply the custom style
        builder.setTitle("Notes Options");

        // Set up the buttons
        builder.setPositiveButton("Voice Record", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), RecordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("mode", "record");
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Notes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), SaveAudioActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("mode", "notes");
                startActivity(intent);
            }
        });
        builder.show();

    }







}

