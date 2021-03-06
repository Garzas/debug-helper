package com.appunite.debughelper.presenter;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.appunite.debughelper.DebugHelper;
import com.appunite.debughelper.interceptor.DebugInterceptor;
import com.appunite.debughelper.model.SelectOption;
import com.appunite.debughelper.model.SwitchOption;
import com.appunite.debughelper.utils.DebugOption;
import com.appunite.debughelper.utils.DebugTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.observers.Observers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static com.appunite.debughelper.DebugHelper.getDebugPreferences;

public class DebugPresenter {

    @Nonnull
    private final PublishSubject<SelectOption> selectSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<SwitchOption> switchOptionSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<Integer> actionSubject = PublishSubject.create();
    @Nonnull
    private final BehaviorSubject<List<BaseDebugItem>> simpleListSubject = BehaviorSubject.create();
    @Nonnull
    private final PublishSubject<Float> densitySubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> resolutionSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<Object> recreateActivitySubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<Object> httpCodeChangedSubject = PublishSubject.create();
    @Nonnull
    private final BehaviorSubject<Boolean> showScalpelSubject = BehaviorSubject.create(false);
    @Nonnull
    private final PublishSubject<String> informationSubject = PublishSubject.create();

    @Nonnull
    private final Observable<Boolean> scalpelObservable;
    @Nonnull
    private final Observable<Boolean> hideViewsObservable;
    @Nonnull
    private final Observable<Boolean> showIdObservable;
    @Nonnull
    private final Observable<Boolean> fpsLabelObservable;
    @Nonnull
    private final Observable<String> showLogObservable;
    @Nonnull
    private final Observable<Boolean> changeResponseObservable;
    @Nonnull
    private final Observable<Integer> showRequestObservable;
    @Nonnull
    private final Observable<Integer> showMacroObservable;
    @Nonnull
    private final Observable<Object> interceptorNotImplemented;
    @Nonnull
    private final Observable<Boolean> pinMacroObservable;
    @Nonnull
    private final Observable<String> installNotImplementedObservable;

    public abstract static class BaseDebugItem {

    }

    public class MainItem extends BaseDebugItem {

        private boolean mock;
        private boolean debug;

        public MainItem(boolean mock, boolean debug) {
            this.mock = mock;
            this.debug = debug;
        }

        public boolean isMock() {
            return mock;
        }

        public boolean isDebug() {
            return debug;
        }

        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    recreateActivitySubject.onNext(o);
                }
            });
        }
    }

    public class CategoryItem extends BaseDebugItem {

        @Nonnull
        private final String title;

        public CategoryItem(@Nonnull final String title) {
            this.title = title;
        }

        @Nonnull
        public String getTitle() {
            return title;
        }

    }

    public class InformationItem extends BaseDebugItem {

        @Nonnull
        private final String name;
        @Nonnull
        private final String value;

        public InformationItem(@Nonnull final String name, @Nonnull final String value) {
            this.name = name;
            this.value = value;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        @Nonnull
        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InformationItem)) return false;

            final InformationItem that = (InformationItem) o;
            return name.equals(that.name) && value.equals(that.value);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (value.hashCode());
            return result;
        }

        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    informationSubject.onNext(name);
                }
            });
        }

    }

    public class OptionItem extends BaseDebugItem {

        @Nonnull
        private final String name;
        private final int option;
        @Nonnull
        private final List<Integer> values;
        private int currentPosition;

        public OptionItem(@Nonnull String name, int option, @Nonnull List<Integer> values, int currentPosition) {
            this.name = name;
            this.option = option;
            this.values = values;
            this.currentPosition = currentPosition;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        @Nonnull
        public List<Integer> getValues() {
            return values;
        }

        public int getOption() {
            return option;
        }

        public int getCurrentPosition() {
            return currentPosition;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OptionItem)) return false;

            final OptionItem that = (OptionItem) o;
            return name.equals(that.name) && (values == that.values)
                    && option == that.option
                    && currentPosition == that.currentPosition;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (values.hashCode());
            return result;
        }

        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    selectSubject.onNext(new SelectOption(option, currentPosition, values));
                }
            });
        }
    }

    public class SwitchItem extends BaseDebugItem {

        @Nonnull
        private final String title;
        private int option;
        private Boolean staticSwitcher;

        public SwitchItem(@Nonnull String title, int option, Boolean staticSwitcher) {
            this.title = title;
            this.option = option;
            this.staticSwitcher = staticSwitcher;
        }

        public SwitchItem(@Nonnull final String title, final int option) {
            this.option = option;
            this.title = title;
        }

        @Nonnull
        public String getTitle() {
            return title;
        }

        public int getOption() {
            return option;
        }

        public Boolean isStaticSwitcher() {
            return staticSwitcher;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SwitchItem)) return false;

            final SwitchItem that = (SwitchItem) o;
            return title.equals(that.title)
                    && option == that.option
                    && staticSwitcher == that.staticSwitcher;
        }

        @Override
        public int hashCode() {
            int result = title.hashCode();
            result = 31 * result + (staticSwitcher.hashCode());
            return result;
        }

        public Observer<Boolean> switchOption() {
            return Observers.create(new Action1<Boolean>() {
                @Override
                public void call(Boolean set) {
                    switchOptionSubject.onNext(new SwitchOption(set, option));
                }
            });
        }
    }

    public class ActionItem extends BaseDebugItem {

        @Nonnull
        private final String name;
        private final int action;

        public ActionItem(@Nonnull final String name, final int action) {
            this.name = name;
            this.action = action;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        public int getAction() {
            return action;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ActionItem)) return false;

            final ActionItem that = (ActionItem) o;
            return name.equals(that.name)
                    && action == that.action;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result;
            return result;
        }

        public Observer<Object> actionOption() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    actionSubject.onNext(action);
                }
            });
        }
    }

    public DebugPresenter(@Nonnull final Context context) {

        final Observable<ActivityManager.MemoryInfo> memoryObservable = Observable.just((Long) null)
                .mergeWith(Observable.<Long>never())
                .map(new Func1<Long, ActivityManager.MemoryInfo>() {
                    @Override
                    public ActivityManager.MemoryInfo call(Long aLong) {
                        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                        activityManager.getMemoryInfo(mi);
                        return mi;
                    }
                });

        final Observable<List<InformationItem>> deviceInfoList = Observable.combineLatest(
                resolutionSubject,
                densitySubject,
                memoryObservable,
                new Func3<String, Float, ActivityManager.MemoryInfo, List<InformationItem>>() {
                    @Override
                    public List<InformationItem> call(String resolution, Float density, ActivityManager.MemoryInfo mi) {
                        final ArrayList<InformationItem> newList = new ArrayList<>();

                        newList.add(new InformationItem("Model", Build.MANUFACTURER + " " + Build.MODEL));
                        newList.add(new InformationItem("SDK", DebugTools.checkSDKName(
                                Build.VERSION.SDK_INT)
                                + "(" + Build.VERSION.SDK_INT
                                + " API)"));
                        newList.add(new InformationItem("Release", Build.VERSION.RELEASE));
                        newList.add(new InformationItem("Resolution", resolution));
                        newList.add(new InformationItem("Density", Math.round(density) + "dpi"));
                        newList.add(new InformationItem("Free Memory", (mi.availMem / 1048576L) + " MB"));

                        return newList;
                    }
                });

        final Observable<List<InformationItem>> buildInfoList = densitySubject
                .map(new Func1<Float, List<InformationItem>>() {
                    @Override
                    public List<InformationItem> call(Float density) {
                        final ArrayList<InformationItem> newList = new ArrayList<>();

                        newList.add(new InformationItem("Name", DebugTools.getApplicationName(context)));
                        newList.add(new InformationItem("Package", context.getPackageName()));
                        newList.add(new InformationItem("Build Type", DebugTools.getBuildType(context)));
                        newList.add(new InformationItem("Version", DebugTools.getBuildVersion(context)));
                        return newList;
                    }
                });

        final Observable<List<BaseDebugItem>> scalpelUtilsList = showScalpelSubject
                .map(new Func1<Boolean, List<BaseDebugItem>>() {
                    @Override
                    public List<BaseDebugItem> call(final Boolean aBoolean) {
                        final ArrayList<BaseDebugItem> newList = new ArrayList<>();

                        newList.add(new SwitchItem("Turn Scalpel ", DebugOption.SET_SCALPEL));
                        if (aBoolean) {
                            newList.add(new SwitchItem("Hide Views", DebugOption.SCALPEL_HIDE_VIEWS));
                            newList.add(new SwitchItem("Show Ids", DebugOption.SCALPEL_SHOW_ID));
                        }
                        return newList;
                    }
                });

        final Observable<List<BaseDebugItem>> utilList = Observable.just(new Object())
                .mergeWith(Observable.never())
                .map(new Func1<Object, List<BaseDebugItem>>() {
                    @Override
                    public List<BaseDebugItem> call(Object o) {
                        final ArrayList<BaseDebugItem> newList = new ArrayList<>();

                        newList.add(new SwitchItem("FPS Label", DebugOption.FPS_LABEL, DebugHelper.isFpsVisible()));
                        if (DebugTools.isLeakCanaryInstalled()) {
                            newList.add(new InformationItem("LeakCanary", "enabled"));
                        } else {
                            newList.add(new InformationItem("LeakCanary", "disabled"));
                        }
                        newList.add(new ActionItem("Show Log", DebugOption.SHOW_LOG));
                        return newList;
                    }
                });

        Observable.combineLatest(
                deviceInfoList,
                buildInfoList,
                scalpelUtilsList,
                utilList,
                new Func4<List<InformationItem>, List<InformationItem>, List<BaseDebugItem>,
                        List<BaseDebugItem>, List<BaseDebugItem>>() {
                    @Override
                    public List<BaseDebugItem> call(
                            List<InformationItem> deviceInfo,
                            List<InformationItem> buildInfo,
                            List<BaseDebugItem> scalpelUtils,
                            List<BaseDebugItem> utils) {
                        final ArrayList<BaseDebugItem> debugList = new ArrayList<>();

                        debugList.add(new MainItem(true, true));
                        debugList.add(new CategoryItem("Device Information"));
                        debugList.addAll(deviceInfo);
                        debugList.add(new CategoryItem("About app"));
                        debugList.addAll(buildInfo);
                        debugList.add(new CategoryItem("Macro"));
                        debugList.add(new ActionItem("Show Macro", DebugOption.SHOW_MACRO));
                        debugList.add(new SwitchItem("Pin", DebugOption.PIN_MACRO, false));
                        debugList.add(new CategoryItem("OkHttp options"));
                        if (getDebugPreferences().getMockState()) {
                            debugList.add(new SwitchItem("Return empty response", DebugOption.SET_EMPTY_RESPONSE, DebugInterceptor.getEmptyResponse()));
                            debugList.add(new OptionItem("Http code", DebugOption.SET_HTTP_CODE,
                                    Arrays.asList(200,
                                            201, 202, 203, 204, 205,
                                            206, 300, 301, 302, 303,
                                            304, 305, 400, 401, 402,
                                            403, 404, 405, 406, 407,
                                            408, 409, 410, 411, 412,
                                            413, 414, 415, 500, 501,
                                            502, 503, 504, 505),
                                    DebugTools.selectHttpCodePosition(DebugInterceptor.getResponseCode())));
                            debugList.add(new ActionItem("Request counter", DebugOption.SHOW_REQUEST));
                        } else {
                            debugList.add(new InformationItem("","Turn on Mock mode to use this feature"));
                        }
                        debugList.add(new CategoryItem("Scalpel Utils"));
                        debugList.addAll(scalpelUtils);
                        debugList.add(new CategoryItem("Tools"));
                        debugList.addAll(utils);
                        return debugList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(simpleListSubject);

        scalpelObservable = switchOptionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.SET_SCALPEL;
                    }
                })
                .map(checkSet());

        hideViewsObservable = switchOptionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.SCALPEL_HIDE_VIEWS;
                    }
                })
                .map(checkSet());

        showIdObservable = switchOptionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.SCALPEL_SHOW_ID;
                    }
                })
                .map(checkSet());

        fpsLabelObservable = switchOptionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.FPS_LABEL;
                    }
                })
                .map(checkSet());

        changeResponseObservable = switchOptionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.SET_EMPTY_RESPONSE;
                    }
                })
                .map(checkSet());

        pinMacroObservable = switchOptionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(final SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.PIN_MACRO;
                    }
                })
                .map(checkSet());

        showLogObservable = actionSubject
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer.equals(DebugOption.SHOW_LOG);
                    }
                })
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(final Integer integer) {
                        return "d";
                    }
                });

        showRequestObservable = actionSubject
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer.equals(DebugOption.SHOW_REQUEST);
                    }
                });

        showMacroObservable = actionSubject
                .filter(new Func1<Integer, Boolean>() {
                            @Override
                            public Boolean call(Integer integer) {
                                return integer.equals(DebugOption.SHOW_MACRO);
                            }
                        }
                );

        installNotImplementedObservable = informationSubject
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(final String s) {
                        return s.equals("LeakCanary");
                    }
                });

        interceptorNotImplemented = Observable.merge(
                changeResponseObservable,
                showRequestObservable,
                httpCodeChangedSubject
        );

    }

    @Nonnull
    private Func1<SwitchOption, Boolean> checkSet() {
        return new Func1<SwitchOption, Boolean>() {
            @Override
            public Boolean call(SwitchOption switchOption) {
                return switchOption.isSet();
            }
        };
    }

    @Nonnull
    public Observer<Float> densityObserver() {
        return densitySubject;
    }

    @Nonnull
    public Observer<Object> httpCodeChangedObserver() {
        return httpCodeChangedSubject;
    }

    @Nonnull
    public Observer<String> resolutionObserver() {
        return resolutionSubject;
    }

    @Nonnull
    public Observer<Boolean> showScalpelOptionsObserver() {
        return showScalpelSubject;
    }

    @Nonnull
    public Observable<List<BaseDebugItem>> debugListObservable() {
        return simpleListSubject;
    }

    @Nonnull
    public Observable<Boolean> getFpsLabelObservable() {
        return fpsLabelObservable;
    }

    @Nonnull
    public Observable<String> getShowLogObservable() {
        return showLogObservable;
    }

    @Nonnull
    public Observable<Boolean> setScalpelObservable() {
        return scalpelObservable;
    }

    @Nonnull
    public Observable<Boolean> setHideViewsObservable() {
        return hideViewsObservable;
    }

    @Nonnull
    public Observable<Boolean> setShowIdsObservable() {
        return showIdObservable;
    }

    @Nonnull
    public Observable<SelectOption> showOptionsDialog() {
        return selectSubject;
    }

    @Nonnull
    public Observable<Boolean> getChangeResponseObservable() {
        return changeResponseObservable;
    }

    @Nonnull
    public Observable<Object> recreateActivityObservable() {
        return recreateActivitySubject;
    }

    @Nonnull
    public Observable<Integer> getShowRequestObservable() {
        return showRequestObservable;
    }

    @Nonnull
    public Observable<Integer> getShowMacroObservable() {
        return showMacroObservable;
    }

    @Nonnull
    public Observable<Boolean> getPinMacroObservable() {
        return pinMacroObservable;
    }

    @Nonnull
    public Observable<Object> interceptorNotImplementedObservable() {
        return interceptorNotImplemented;
    }

    @Nonnull
    public Observable<String> getInstallNotImplementedObservable() {
        return installNotImplementedObservable;
    }
}
