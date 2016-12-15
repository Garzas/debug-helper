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

    private static Boolean fpsVisibility = false;
    private static boolean isInterceptorInstalled = false;

    private static SerialSubscription subscription = new SerialSubscription();
    private static DebugPresenter debugPresenter = null;
    private static DebugHelperPreferences debugPreferences;
    private static DebugAdapter debugAdapter;

    public static void setActivity(final Activity activity) {
        debugPreferences = new DebugHelperPreferences(activity.getApplicationContext());
        debugPresenter = new DebugPresenter(activity);
        debugAdapter = new DebugAdapter(debugPreferences);
        FieldManager.init(debugPreferences);
    }

    @Nonnull
    public static View setContentView(final int childId, final Activity activity) {
        final View child = activity.getLayoutInflater().inflate(childId, null);
        return setContentView(child, activity);
    }

    @Nonnull
    public static View setContentView(@Nonnull View child, final Activity activity) {
        final View root;
        root = activity.getLayoutInflater().inflate(R.layout.debug_layout, null);

        final ViewGroup mainFrame = (ViewGroup) root.findViewById(R.id.main_frame);
        mainFrame.removeAllViews();
        mainFrame.addView(child);

        return root;
    }

    public static void reSubscribe(final Activity activity) {
        debugPreferences = new DebugHelperPreferences(activity.getApplicationContext());
        debugPresenter = new DebugPresenter(activity);

        final ViewGroup mainView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);

        final ViewGroup debugView = (ViewGroup) mainView.getChildAt(0);
        final ScalpelFrameLayout scalpelFrame = (ScalpelFrameLayout) debugView.getChildAt(0);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);

        final RecyclerView debugRecyclerView = (RecyclerView) debugView.findViewById(R.id.debug_drawer);
        debugRecyclerView.setBackgroundColor(Color.parseColor("#cc222222"));

        debugRecyclerView.setLayoutManager(layoutManager);
        debugRecyclerView.setAdapter(debugAdapter);

        final DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (debugPresenter != null) {
            subscription.set(Subscriptions.from(
                    debugPresenter.debugListObservable()
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

                    debugPresenter.setScalpelObservable()
                            .subscribe(debugPresenter.showScalpelOptionsObserver()),

                    debugPresenter.setHideViewsObservable()
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
                            .mergeWith(debugPresenter.getPinMacroObservable())
                            .filter(new Func1<Boolean, Boolean>() {
                                @Override
                                public Boolean call(final Boolean aBoolean) {
                                    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity);
                                }
                            })
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(final Boolean aBoolean) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:" + activity.getPackageName()));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.startActivity(intent);
                                }
                            }),

                    debugPresenter.getFpsLabelObservable()
                            .filter(canDrawOverlays(activity))
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean isSet) {
                                    if (isSet) {
                                        TinyDancer.create().show(activity);
                                    } else {
                                        TinyDancer.hide(activity);
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
                                    Toast.makeText(activity, R.string.interceptor_not_implemented, Toast.LENGTH_LONG).show();
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
                            .filter(canDrawOverlays(activity))
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(final Boolean aBoolean) {
                                    if (aBoolean) {
                                        activity.startService(MacroService.newInstance(activity));
                                    } else {
                                        activity.stopService(MacroService.newInstance(activity));
                                    }
                                }
                            }),
                    debugPresenter.recreateActivityObservable()
                            .subscribe(new Action1<Object>() {
                                @Override
                                public void call(Object o) {
                                    activity.recreate();
                                }
                            }),
                    debugPresenter.getInstallNotImplementedObservable()
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(final String s) {
                                    Toast.makeText(activity, R.string.not_implemented_install, Toast.LENGTH_LONG).show();
                                }
                            })
            ));
        }

    }

    private static Func1<Object, Boolean> isInterceptorNotImplemented() {
        return new Func1<Object, Boolean>() {
            @Override
            public Boolean call(final Object o) {
                return !isInterceptorInstalled;
            }
        };
    }

    private static Func1<Boolean, Boolean> canDrawOverlays(final Activity activity) {
        return new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(final Boolean aBoolean) {
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(activity);
            }
        };
    }

    public static void unSubscribe(final Activity activity) {
        subscription.set(Subscriptions.empty());
        if (activity != null) {
            activity.stopService(new Intent(activity, MacroService.class));
        }

    }

    public static void install(final Context context) {
        DebugTools.setLeakCanaryState(true);
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

    public static void hide(final Activity activity) {
        final View root = activity.getLayoutInflater().inflate(R.layout.debug_layout, null);
        final DrawerLayout drawerLayout = (DrawerLayout) root;
        drawerLayout.closeDrawer(Gravity.RIGHT);
    }

    public static void interceptorEnabled() {
        isInterceptorInstalled = true;
    }

    public static void resetActivity() {
        debugAdapter = null;
        debugPresenter = null;
    }
}