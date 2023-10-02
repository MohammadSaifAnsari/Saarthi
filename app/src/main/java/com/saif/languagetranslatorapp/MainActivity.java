package com.saif.languagetranslatorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.saif.languagetranslatorapp.Model.LanguageModel;
import com.saif.languagetranslatorapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.Manifest;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;

    private TranslatorOptions translatorOptions;
    private Translator translator;
    private ArrayList<LanguageModel> languageList;
    ArrayList<String> SpinnerTitleList = new ArrayList<>();
    
    private String sourceLanguageTitle= "English";
    private String sourceLanguageCode= "en";
    private String destinationLanguageCode= "ur";
    private String destinationLanguageTitle= "Urdu";

    private String sourceLanguageText = "";

    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        checkpermission();
        loadAvailableLanguage();

        for (int i = 0;i<languageList.size();i++){
            SpinnerTitleList.add(languageList.get(i).getLangTitle());
            Log.d("lang123",SpinnerTitleList.toString());
        }

        initresult();


        activityMainBinding.fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sourceLanguageCode = languageList.get(position).getLangCode();
                sourceLanguageTitle = languageList.get(position).getLangTitle();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(this,R.layout.spinner_item_list,SpinnerTitleList);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityMainBinding.fromSpinner.setAdapter(fromAdapter);



        activityMainBinding.toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destinationLanguageCode = languageList.get(position).getLangCode();
                destinationLanguageTitle = languageList.get(position).getLangTitle();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter toAdapter = new ArrayAdapter(this,R.layout.spinner_item_list,SpinnerTitleList);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityMainBinding.toSpinner.setAdapter(fromAdapter);


        activityMainBinding.idTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceLanguageText = activityMainBinding.idInputText.getText().toString().trim();
                
                if (sourceLanguageText.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter text to translate..", Toast.LENGTH_SHORT).show();
                }else{
                    startTranslation();
                }
            }
        });

        activityMainBinding.micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startrecording(v);
            }
        });

    }

    private void startrecording(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);

        speechRecognizer.startListening(intent);
    }

    private void initresult() {
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> speechResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    activityMainBinding.idInputText.setText(speechResult.get(0));

                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void startTranslation() {
        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(destinationLanguageCode)
                .build();

        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        translator.translate(sourceLanguageText)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String translatedText) {
                                        activityMainBinding.resultText.setText(translatedText);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Failed to translate due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to ready model due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAvailableLanguage() {
        languageList = new ArrayList<>();
        List<String> langCodeList = TranslateLanguage.getAllLanguages();

        for (String langCode:langCodeList){
            String langTitle = new Locale(langCode).getDisplayLanguage();


            LanguageModel languageModel = new LanguageModel(langCode,langTitle);
            languageList.add(languageModel);
        }
    }

    public  void checkpermission(){
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED ) {
            String[] permission = { Manifest.permission.RECORD_AUDIO};
            requestPermissions(permission, 66);
        }
    }
}