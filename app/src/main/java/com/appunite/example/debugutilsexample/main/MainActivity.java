package com.appunite.example.debugutilsexample.main;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.appunite.example.debugutilsexample.App;
import com.appunite.example.debugutilsexample.BaseActivity;
import com.appunite.example.debugutilsexample.R;
import com.appunite.example.debugutilsexample.dagger.ActivityModule;
import com.appunite.example.debugutilsexample.dagger.BaseActivityComponent;
import com.appunite.example.debugutilsexample.details.DetailsActivity;
import com.appunite.example.debugutilsexample.presenter.MainPresenter;
import com.appunite.example.debugutilsexample.view.ColoredSnackBar;
import com.appunite.rx.android.widget.RxContextMore;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observer;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class MainActivity extends BaseActivity {

    @Inject
    MainPresenter presenter;

    @Inject
    MainAdapter adapter;

    @InjectView(R.id.simple_text)
    TextView simpleText;
    @InjectView(R.id.main_recyclerview)
    RecyclerView recyclerView;
    @InjectView(R.id.main_content)
    View view;

    private final SerialSubscription subscription = new SerialSubscription();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MainActivityComponent component = (MainActivityComponent) getActivityComponent();
        component.inject(this);
        ButterKnife.inject(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        subscription.set(new CompositeSubscription(
                presenter.getTitleObservable()
                        .compose(this.<String>bindToLifecycle())
                        .subscribe(RxTextView.text(simpleText)),
                presenter.clickObservable()
                        .subscribe(RxContextMore.startActivity(this, DetailsActivity.newIntent(this))),
                presenter.getItemListObservable()
                        .compose(this.<List<MainPresenter.BaseItem>>bindToLifecycle())
                        .subscribe(new Observer<List<MainPresenter.BaseItem>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(final Throwable e) {
                                Log.e("DebugHelper", e.getMessage());
                                if (e instanceof HttpException) {
                                    ColoredSnackBar
                                            .error(view, e.getMessage(), Snackbar.LENGTH_SHORT)
                                            .show();
                                }
                            }

                            @Override
                            public void onNext(final List<MainPresenter.BaseItem> baseItems) {
                                Log.d("DebugHelper", "onNext");
                                adapter.call(baseItems);
                            }
                        })

        ));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.set(Subscriptions.empty());
    }

    @Nonnull
    @Override
    public BaseActivityComponent createActivityComponent(@Nullable Bundle savedInstanceState) {
        return DaggerMainActivityComponent
                .builder()
                .activityModule(new ActivityModule(this))
                .appComponent(App.getAppComponent(getApplication()))
                .build();
    }
}
