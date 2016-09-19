package com.appunite.debughelper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appunite.debughelper.adapter.DebugAdapter;
import com.appunite.debughelper.dialog.OptionsDialog;
import com.appunite.debughelper.interceptor.DebugInterceptor;
import com.appunite.debughelper.interceptor.SampleInterceptor;
import com.appunite.debughelper.macro.FieldManager;
import com.appunite.debughelper.macro.MacroFragment;
import com.appunite.debughelper.macro.MacroService;
import com.appunite.debughelper.model.SelectOption;
import com.appunite.debughelper.presenter.DebugPresenter;
import com.appunite.debughelper.utils.DebugOption;
import com.appunite.debughelper.utils.DebugTools;
import com.codemonkeylabs.fpslibrary.TinyDancer;
import com.github.pedrovgs.lynx.LynxActivity;
import com.github.pedrovgs.lynx.LynxConfig;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.squareup.leakcanary.LeakCanary;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okhttp3.Interceptor;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public class DebugHelper {

    private static Context appContext;
    private static Boolean fpsVisibility = false;
    private static boolean isInterceptorInstalled = false;

    private static Activity currentActivity;
    private static SerialSubscription subscription = new SerialSubscription();
    private static DebugPresenter debugPresenter = null;
    private static ScalpelFrameLayout scalpelFrame;
    private static DebugHelperPreferences debugPreferences;
    private static DebugAdapter debugAdapter;
    private static DisplayMetrics metrics;
    private static ViewGroup mainFrame;
    private static RecyclerView debugRecyclerView;
    private static DrawerLayout drawerLayout;
    private static Activity appActivity;

    public static void setActivity(Activity activity) {
        currentActivity = activity;
        debugPreferences = new DebugHelperPreferences(currentActivity.getApplicationContext());
        debugPresenter = new DebugPresenter(currentActivity);
        debugAdapter = new DebugAdapter(debugPreferences);
        FieldManager.init(debugPreferences);
    }

    @Nonnull
    public static View setContentView(int childId) {
        final View child = currentActivity.getLayoutInflater().inflate(childId, null);
        return setContentView(child);
    }

    @Nonnull
    public static View setContentView(@Nonnull View child) {
        final View root;
        root = currentActivity.getLayoutInflater().inflate(R.layout.debug_layout, null);

        drawerLayout = (DrawerLayout) root;
        mainFrame = (ViewGroup) root.findViewById(R.id.main_frame);
        mainFrame.removeAllViews();
        mainFrame.addView(child);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(currentActivity);
        debugRecyclerView = (RecyclerView) root.findViewById(R.id.debug_drawer);
        debugRecyclerView.setBackgroundColor(Color.parseColor("#cc222222"));
        debugRecyclerView.setLayoutManager(layoutManager);
        debugRecyclerView.setAdapter(debugAdapter);

        return root;
    }

    public static void reSubscribe(final Activity activity) {
        appActivity = activity;
        debugPreferences = new DebugHelperPreferences(activity.getApplicationContext());
        debugPresenter = new DebugPresenter(activity);

        final ViewGroup mainView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);

        final ViewGroup debugView = (ViewGroup) mainView.getChildAt(0);
        scalpelFrame = (ScalpelFrameLayout) debugView.getChildAt(0);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);

        debugRecyclerView = (RecyclerView) debugView.findViewById(R.id.debug_drawer);
        debugRecyclerView.setBackgroundColor(Color.parseColor("#cc222222"));

        debugRecyclerView.setLayoutManager(layoutManager);
        debugRecyclerView.setAdapter(debugAdapter);

        metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        subscription.set(Subscriptions.from(
                debugPresenter.simpleListObservable()
                        .subscribe(debugAdapter),

                Observable.just(activity.getResources().getDisplayMetrics().density * 160f)
                        .subscribe(debugPresenter.densityObserver()),

                Observable.just(metrics.widthPixels + "x" + metrics.heightPixels)
                        .subscribe(debugPresenter.resolutionObserver()),

                debugPresenter.setScalpelObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                scalpelFrame.setLayerInteractionEnabled(aBoolean);
                            }
                        }),

                debugPresenter.setDrawViewsObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                scalpelFrame.setDrawViews(!aBoolean);
                            }
                        }),

                debugPresenter.setShowIdsObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                scalpelFrame.setDrawIds(aBoolean);
                            }
                        }),

                debugPresenter.getFpsLabelObservable()
                        .filter(isInstalled())
                        .mergeWith(debugPresenter.getPinMacroObservable())
                        .filter(new Func1<Boolean, Boolean>() {
                            @Override
                            public Boolean call(final Boolean aBoolean) {
                                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(appContext);
                            }
                        })
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(final Boolean aBoolean) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + appContext.getPackageName()));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                appContext.startActivity(intent);
                            }
                        }),

                debugPresenter.getFpsLabelObservable()
                        .filter(isInstalled())
                        .filter(canDrawOverlays())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean isSet) {
                                if (isSet) {
                                    TinyDancer.create().show(appContext);
                                } else {
                                    TinyDancer.hide(appContext);
                                }
                                fpsVisibility = isSet;
                            }
                        }),

                debugPresenter.getShowLogObservable()
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                LynxConfig lynxConfig = new LynxConfig();
                                lynxConfig.setMaxNumberOfTracesToShow(4000);
                                Intent lynxActivityIntent = LynxActivity.getIntent(activity, lynxConfig);
                                activity.startActivity(lynxActivityIntent);
                            }
                        }),

                debugPresenter.getChangeResponseObservable()
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                DebugInterceptor.setEmptyResponse(aBoolean);
                                activity.recreate();
                            }
                        }),

                debugPresenter.interceptorNotImplementedObservable()
                        .delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                        .filter(isInterceptorNotImplemented())
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(final Object o) {
                                Toast.makeText(currentActivity, R.string.interceptor_not_implemented, Toast.LENGTH_LONG).show();
                            }
                        }),

                debugPresenter.showOptionsDialog()
                        .subscribe(new Action1<SelectOption>() {
                            @Override
                            public void call(SelectOption selectOption) {
                                OptionsDialog.newInstance(selectOption).show(activity.getFragmentManager(), null);
                            }
                        }),

                debugPresenter.getShowRequestObservable()
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                activity.getFragmentManager()
                                        .beginTransaction()
                                        .add(InfoListFragment.newInstance(), "REQUEST_COUNTER")
                                        .disallowAddToBackStack()
                                        .commit();
                            }
                        }),
                debugPresenter.getShowMacroObservable()
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                activity.getFragmentManager()
                                        .beginTransaction()
                                        .add(MacroFragment.newInstance(), "MACRO_FRAGMENT")
                                        .disallowAddToBackStack()
                                        .commit();
                            }
                        }),
                debugPresenter.getPinMacroObservable()
                        .filter(canDrawOverlays())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(final Boolean aBoolean) {
                                if (aBoolean) {
                                    appContext.startService(MacroService.newInstance(activity));
                                } else {
                                    appContext.stopService(MacroService.newInstance(activity));
                                }
                            }
                        }),
                debugPresenter.recreateActivityObservable()
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                activity.recreate();
                            }
                        })
        ));


    }

    private static Func1<Object, Boolean> isInterceptorNotImplemented() {
        return new Func1<Object, Boolean>() {
            @Override
            public Boolean call(final Object o) {
                return !isInterceptorInstalled;
            }
        };
    }

    private static Func1<Boolean, Boolean> isInstalled() {
        return new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(final Boolean aBoolean) {
                if (appContext == null) {
                    Toast.makeText(currentActivity, R.string.not_implemented_install, Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    return true;
                }
            }
        };
    }

    private static Func1<Boolean, Boolean> canDrawOverlays() {
        return new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(final Boolean aBoolean) {
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(appContext);
            }
        };
    }

    public static void unSubscribe() {
        subscription.set(Subscriptions.empty());
        if (appActivity != null) {
            appContext.stopService(new Intent(appActivity, MacroService.class));
        }
    }

    public static void install(Context context) {
        appContext = context;
        if (DebugTools.isDebuggable(context)) {
            LeakCanary.install((Application) context);
        }
    }

    @Nonnull
    public static Interceptor getResponseInterceptor() {
        return new SampleInterceptor();
    }

    public static DebugHelperPreferences getDebugPreferences() {
        return debugPreferences;
    }

    public static Boolean isFpsVisible() {
        return fpsVisibility;
    }

    public static void updateOption(SelectOption option) {
        switch (option.getOption()) {
            case DebugOption.SET_HTTP_CODE:
                DebugInterceptor.setResponseCode(option.getValues().get(option.getCurrentPosition()));
                debugPresenter.httpCodeChangedObserver().onNext(null);
        }
        debugAdapter.notifyDataSetChanged();
    }

    public static void hide() {
        drawerLayout.closeDrawer(Gravity.RIGHT);
    }

    public static void interceptorEnabled() {
        isInterceptorInstalled = true;
    }
}