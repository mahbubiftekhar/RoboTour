package com.example.david.robotour;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.david.robotour.R;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class Translator extends AppCompatActivity {
    private static final String API_KEY = "32ac0b5a9a4b0edd2714ea6e7c14b0956b683ad0";

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Handler textViewHandler = new Handler();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
                Translate translate = options.getService();
                final Translation translation = translate.translate("Hello World", Translate.TranslateOption.targetLanguage("de"));
                textViewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("+++"+translation.getTranslatedText());
                    }
                });
                return null;
            }
        }.execute();
    }
}
