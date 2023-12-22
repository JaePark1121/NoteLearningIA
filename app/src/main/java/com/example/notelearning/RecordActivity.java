package com.example.notelearning;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class RecordActivity extends AppCompatActivity {

    Intent intent;
    SpeechRecognizer speechRecognizer;
    final int PERMISSION = 1;
    boolean recording = false;  //현재 녹음중인지 여부
    FloatingActionButton recordBtn;

    FloatingActionButton finish;
    FloatingActionButton reload;

    ImageView home;

    EditText title;
   TextView contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        CheckPermission();  //녹음 퍼미션 체크

        title = (EditText) findViewById(R.id.notebook_title_record);
        home = (ImageView) findViewById(R.id.record_back);
        finish = (FloatingActionButton) findViewById(R.id.record_finish);
        recordBtn = (FloatingActionButton) findViewById(R.id.record_start);
        reload = (FloatingActionButton) findViewById(R.id.record_reload);
        contents = (TextView) findViewById(R.id.contents);




        recordBtn.setOnClickListener(click);

        //RecognizerIntent 객체 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-GB");   //한국어


        finish.setVisibility(View.INVISIBLE);
        reload.setVisibility(View.INVISIBLE);



        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });




        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                startActivity(intent);
            }
        });



        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SaveAudioActivity.class);
                System.out.println(contents.getText().toString());
                intent.putExtra("mode", "create");
                intent.putExtra("content", contents.getText().toString());
                intent.putExtra("title", title.getText().toString());
                startActivity(intent);
            }
        });

        ImageView record_back = (ImageView) findViewById(R.id.record_back);
        record_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });





    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //녹음 버튼
            if (view.getId() == R.id.record_start) {
                if (!recording) {   //녹음 시작
                    finish.setVisibility(View.VISIBLE);
                    reload.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Recording started.", Toast.LENGTH_SHORT).show();
                    StartRecord();
                } else {  //이미 녹음 중이면 녹음 중지
                    StopRecord();

                }
            }
        }
    };

    //퍼미션 체크
    void CheckPermission() {
        //안드로이드 버전이 6.0 이상
        if (Build.VERSION.SDK_INT >= 23) {
            //인터넷이나 녹음 권한이 없으면 권한 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET,
                                Manifest.permission.RECORD_AUDIO}, PERMISSION);
            }
        }
    }





    //녹음 시작
    void StartRecord() {
        recording = true;

        //마이크 이미지와 텍스트 변경
        recordBtn.setImageResource(R.drawable.pause);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(intent);
    }

    //녹음 중지
    void StopRecord() {
        recording = false;

        //마이크 이미지와 텍스트 변경
        recordBtn.setImageResource(R.drawable.play);

        speechRecognizer.stopListening();   //녹음 중지
        Toast.makeText(getApplicationContext(), "Recording stopped.", Toast.LENGTH_SHORT).show();
    }

    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {
            //사용자가 말하기 시작
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            //사용자가 말을 멈추면 호출
            //인식 결과에 따라 onError나 onResults가 호출됨
        }

        @Override
        public void onError(int error) {    //토스트 메세지로 에러 출력
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "Audio Error";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    //message = "클라이언트 에러";
                    //speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient Permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "Network Error";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "Network Timeout";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    //message = "찾을 수 없음";
                    //녹음을 오래하거나 speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    //speechRecognizer를 다시 생성하여 녹음 재개
                    if (recording)
                        StartRecord();
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER is busy";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "Server Error";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "Speech Timeout";
                    break;
                default:
                    message = "Error Unknown";
                    break;
            }
            Toast.makeText(getApplicationContext(), "Error occured. : " + message, Toast.LENGTH_SHORT).show();
        }

        //인식 결과가 준비되면 호출
        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);    //인식 결과를 담은 ArrayList
            String originText = contents.getText().toString();  //기존 text

            //인식 결과
            String newText = "";
            for (int i = 0; i < matches.size(); i++) {
                System.out.println("matched: " + matches.get(i));
                newText += matches.get(i);
            }

            contents.setText(originText + newText + " ");    //기존의 text에 인식 결과를 이어붙임
            speechRecognizer.startListening(intent);    //녹음버튼을 누를 때까지 계속 녹음해야 하므로 녹음 재개
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }


    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}


