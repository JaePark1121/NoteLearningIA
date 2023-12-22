package com.example.notelearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;



public class SaveAudioActivity extends AppCompatActivity {

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static EditText contentView;
    public static EditText titleView;

    public static String newMemoKey;


    static ArrayList<String> words = new ArrayList<>();
    static ArrayList<String> definitions = new ArrayList<>();


    public static String uid;
    public static HashMap<String, Folder> userData = new HashMap<>();

    public ArrayList<String> memoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_audio_file);



        TextView save = (TextView) findViewById(R.id.save_button);
        titleView = (EditText) findViewById(R.id.save_notebook_title);
        contentView = (EditText) findViewById(R.id.save_audio_notes);
        TextView dateView = (TextView) findViewById(R.id.save_notes_date);
        dateView.setText(getTime());

        ImageView home = (ImageView) findViewById(R.id.saved_home2);
        ImageView menu = (ImageView) findViewById(R.id.saved_menu2);

        home.setVisibility(View.INVISIBLE);
        menu.setVisibility(View.INVISIBLE);


        String mode = getIntent().getStringExtra("mode");
         // This will be the memo's key if in edit mode
        memoId = getIntent().getStringArrayListExtra("memoID");


        if ("edit".equals(mode)) {
            fetchMemoFromFirebase(memoId.get(MainRecyclerFragment.uidIndex));
        } else if ("create".equals(mode)) {
            contentView.setText(getIntent().getStringExtra("content"));
            titleView.setText(getIntent().getStringExtra("title"));
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                String title = titleView.getText().toString();
                String content = contentView.getText().toString();
                String date = dateView.getText().toString();

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                DatabaseReference memoReference;
                System.out.println("CurrentTab: " + MainActivity.curTab);
                if ("edit".equals(mode)) {
                    memoReference = mDatabase.child("Users").child(uid).child("folder").child(MainActivity.curTab).child("memos").child(memoId.get(0));
                    System.out.println("Edit mode: " + memoId.get(0));
                    memoReference.child("title").setValue(title);
                    memoReference.child("content").setValue(content);
                    // Update the note in the Home tab as well
                    mDatabase.child("Users").child(uid).child("folder").child("Home").child("memos").child(memoId.get(0)).child("title").setValue(title);
                    mDatabase.child("Users").child(uid).child("folder").child("Home").child("memos").child(memoId.get(0)).child("content").setValue(content);
                    mDatabase.child("Users").child(uid).child("folder").child("Home").child("memos").child(memoId.get(0)).child("date").setValue(date);


                    //Is this right?
                    newMemoKey = memoId.get(0);
                } else {
                    Memos newMemo = new Memos(date, title, content, false);
                    memoReference = mDatabase.child("Users").child(uid).child("folder").child(MainActivity.curTab).child("memos").push();
                    newMemoKey = memoReference.getKey();
                    System.out.println();
                    mDatabase.child("Users").child(uid).child("folder").child("Home").child("memos").child(newMemoKey).setValue(newMemo);
                    memoReference.setValue(newMemo);
                }
                //memoReference.setValue(newMemo);



                save.setVisibility(View.INVISIBLE);
                home.setVisibility(View.VISIBLE);
                menu.setVisibility(View.VISIBLE);
            }

        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                // popup_layout이 팝업창으로 불러올 화면
                View popupView = inflater.inflate(R.layout.fragment_saved_menu, null);

                // ConstraintLayout 부분은 어떤 Layout인지 따라 달라짐, LinearLayout이면 LinearLayout.~~ 이런식
                int width = FrameLayout.LayoutParams.WRAP_CONTENT;
                int height = FrameLayout.LayoutParams.WRAP_CONTENT;
                //팝업창 바깥을 클릭했을 때 팝업 종료하기 기능, false면 꺼짐
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // AnyViewLayout은 현재 메인 Layout에 있는 View중 아무거나 가져오면 됨, 토큰용
                popupWindow.showAtLocation(menu, Gravity.CENTER, 300, -550);



                Button delete = (Button) popupView.findViewById(R.id.delete_button);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the first popup
                        popupWindow.dismiss();

                        LayoutInflater inflater = (LayoutInflater)
                                getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView2 = inflater.inflate(R.layout.fragment_confirm_delete, null);

                        int width = FrameLayout.LayoutParams.WRAP_CONTENT;
                        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true;
                        final PopupWindow popupWindow2 = new PopupWindow(popupView2, width, height, focusable);

                        popupWindow2.showAtLocation(delete, Gravity.CENTER, 0, 0);

                        Button confirm = (Button) popupView2.findViewById(R.id.confirm_delete_btn);
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                                startActivity(intent);
                            }
                        });

                        Button cancel = (Button) popupView2.findViewById(R.id.confirm_cancel_btn);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popupWindow2.dismiss();
                            }
                        });
                    }
                });


            }

        });


        ImageView saveAudioBack = (ImageView) findViewById(R.id.save_audio_back_button);
        saveAudioBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setVisibility(View.INVISIBLE);
                menu.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        });

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setVisibility(View.INVISIBLE);
                menu.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
            }
        });
    }

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }



    private void fetchMemoFromFirebase(String memoId) {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference memoReference = mDatabase.child("Users").child(uid).child("folder").child("Home").child("memos").child(memoId);

        memoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Memos existingMemo = dataSnapshot.getValue(Memos.class);
                if (existingMemo != null) {
                    titleView.setText(existingMemo.getTitle());
                    contentView.setText(existingMemo.getContent());
                    // Set other fields if needed
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error here
                Toast.makeText(SaveAudioActivity.this, "Failed to load memo.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void onBackPressed(){
        super.onBackPressed();
    }
}