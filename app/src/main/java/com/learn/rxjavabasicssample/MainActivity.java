package com.learn.rxjavabasicssample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final StringBuilder builder = new StringBuilder();
    private Disposable disposable = null;
    TextView tvLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLanguage = findViewById(R.id.tvLanguages);
        tvLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity();
            }
        });
        basicObservableAndObserverOps();

    }

    private void navigateToActivity() {
        Intent intent = new Intent(this, CompositeDisposableActivity.class);
        startActivity(intent);
    }

    private void basicObservableAndObserverOps() {
        Observable<String> observable = getLanguageObservable();
        observable.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(getLanguageObserver);
    }

    private final Observer<String> getLanguageObserver = new Observer<String>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG, "onSubscribe");
            disposable = d;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onNext(@NonNull String s) {
            Log.d(TAG, "onNext");
            builder.append(s).append(" ");
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(TAG, "onError" + e.getMessage());
        }

        @Override
        public void onComplete() {
            tvLanguage.setText(builder.toString());
            Log.d(TAG, "All items are emitted!");
        }
    };

    private Observable<String> getLanguageObservable() {
        return Observable.just("Java", "Kotlin", "JS", "ReactNative", "Swift", "Node.js");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}