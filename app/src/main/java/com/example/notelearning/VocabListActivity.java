package com.example.notelearning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VocabListActivity extends AppCompatActivity{


    public static ArrayList<String> memoUID = new ArrayList<>();

    private FragmentTransaction transaction;
    public static AddVocabFragment addVocabFragment;

    private RecyclerView vocabRecyclerView;
    private VocabAdapter vocabAdapter;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference vocabReference;
    private String currentUserUID;

    private ArrayList<String> words = new ArrayList<>();
    private ArrayList<String> definitions = new ArrayList<>();

    static String memoId = "";

    static int uidIndex = 0;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocab_list);




        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        currentUserUID = mAuth.getCurrentUser().getUid();


        //memo id에는 두가지 종류... 첫번째는 recycler view에서 가져오는것-notekeys 쓰면됨
        //두번째는 새로운 노트를 만들었을때... 그러면 save audio activity에서 가져와야됨

        String mode = getIntent().getStringExtra("mode_vocab");
        String mode_save = getIntent().getStringExtra("mode_vocab_save");

        if ("new_list".equals(mode)) {

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String memoKey = sharedPreferences.getString("latestMemoKey", null);
            memoId = memoKey;
            System.out.println("New List:" + "" + memoId);

        } else if ("old_list".equals(mode)) {
            memoUID = MainRecyclerFragment.memoUID;
            uidIndex = MainRecyclerFragment.uidIndex;
            memoId = memoUID.get(uidIndex);
        }
        database = FirebaseDatabase.getInstance();




        if (memoId != null) {
            vocabReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUserUID)
                    .child("folder")
                    .child(MainActivity.curTab)
                    .child("memos")
                    .child(memoId)
                    .child("vocablist");
            fetchVocabFromFirebase();
        }




        ImageView home = findViewById(R.id.vocab_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        ImageView addVocabBtn = findViewById(R.id.add_vocab_btn);
        addVocabBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                transaction = getSupportFragmentManager().beginTransaction();
                addVocabFragment = new AddVocabFragment();
                transaction.add(R.id.add_vocab_container, addVocabFragment);
                transaction.commit();
            }
        });


        // Initialize RecyclerView and set its layout manager
        vocabRecyclerView = findViewById(R.id.vocabRecyclerView);
        vocabRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    private void fetchVocabFromFirebase() {
     chatGPTVocab();
        vocabReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Vocab> words = new ArrayList<>();
                for (DataSnapshot vocabSnapshot: dataSnapshot.getChildren()) {
                    String word = vocabSnapshot.getKey();
                    String definition = vocabSnapshot.getValue(String.class);
                    words.add(new Vocab(word, definition));
                }

                vocabAdapter = new VocabAdapter(words);
                vocabRecyclerView.setAdapter(vocabAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }







    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private void chatGPTVocab() {
        Intent vocabIntent = getIntent();
        if(vocabIntent != null) {
            words = vocabIntent.getStringArrayListExtra("words"); // Assuming you're passing them as String ArrayLists
            definitions = vocabIntent.getStringArrayListExtra("definitions");
        }

        if(words != null) {
            for (int i = 0; i < words.size(); i++) {
              //  이게 안나
                Toast.makeText(this, "New", Toast.LENGTH_SHORT).show();
                System.out.println(words.get(0));
                mDatabase.child("Users").child(uid).child("folder").child(MainActivity.curTab).child("memos")
                        .child(memoId).child("vocablist").child(words.get(i)).setValue(definitions.get(i));
            }
        }
    }




}
