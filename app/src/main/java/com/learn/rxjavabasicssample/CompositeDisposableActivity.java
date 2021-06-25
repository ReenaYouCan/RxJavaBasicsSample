package com.learn.rxjavabasicssample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CompositeDisposableActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final StringBuilder builder = new StringBuilder();
    private CompositeDisposable disposable = new CompositeDisposable();
    TextView tvLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLanguage = findViewById(R.id.tvLanguages);

        init();
    }

    private void init() {
        Observable<String> observable = getLanguageObservable();

        disposable.add(
                observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).filter(new Predicate<String>() {
                    @Override
                    public boolean test(@NonNull String s) throws Exception {
                        return s.toLowerCase().startsWith("j");
                    }
                }).subscribeWith(getLanguageObserver)
        );

        disposable.add(
                observable.subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        filter(new Predicate<String>() {
                            @Override
                            public boolean test(@NonNull String s) throws Exception {
                                return s.toLowerCase().startsWith("k");
                            }
                        }).
                        map(new Function<String, String>() {
                            @Override
                            public String apply(@NonNull String s) throws Exception {
                                return s.toUpperCase();
                            }
                        }).
                        subscribeWith(getLanguagesAllCapsObserver));
    }

    private final DisposableObserver<String> getLanguageObserver = new DisposableObserver<String>() {

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

    private final DisposableObserver<String> getLanguagesAllCapsObserver = new DisposableObserver<String>() {
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
        return Observable.fromArray("Java", "Kotlin", "JS", "ReactNative", "Swift", "Node.js");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}