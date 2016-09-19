package com.appunite.debughelper.presenter;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.appunite.debughelper.DebugHelper;
import com.appunite.debughelper.interceptor.DebugInterceptor;
import com.appunite.debughelper.utils.DebugOption;
import com.appunite.debughelper.utils.DebugTools;
import com.appunite.debughelper.model.SelectOption;
import com.appunite.debughelper.model.SwitchOption;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

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

public class DebugPresenter {

    @Nonnull
    private final PublishSubject<SelectOption> selectSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<SwitchOption> switchOptionSubject = PublishSubject.create();
    @Nonnull
    private final Observable<Boolean> scalpelObservable;
    @Nonnull
    private final Observable<List<BaseDebugItem>> scalpelUtilsList;
    @Nonnull
    private final Observable<Boolean> drawViewsObservable;
    @Nonnull
    private final Observable<Boolean> showIdObservable;
    @Nonnull
    private final Observable<List<BaseDebugItem>> utilList;
    @Nonnull
    private final Observable<Boolean> fpsLabelObservable;
    @Nonnull
    private final PublishSubject<Integer> actionSubject = PublishSubject.create();
    @Nonnull
    private final Observable<String> showLogObservable;
    @Nonnull
    private final Observable<Boolean> changeResponseObservable;
    @Nonnull
    private final BehaviorSubject<List<BaseDebugItem>> simpleListSubject = BehaviorSubject.create();
    @Nonnull
    private final Observable<List<InformationItem>> deviceInfoList;
    @Nonnull
    private final Observable<List<InformationItem>> buildInfoList;
    @Nonnull
    private final PublishSubject<Float> densitySubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<String> resolutionSubject = PublishSubject.create();
    @Nonnull
    private final Context context;
    @Nonnull
    private final PublishSubject<Object> recreateActivitySubject = PublishSubject.create();
    @Nonnull
    private final Observable<ActivityManager.MemoryInfo> memorySubject;
    @Nonnull
    private final Observable<Integer> showRequestObservable;
    @Nonnull
    private final Observable<Integer> showMacroObservable;
    @Nonnull
    private final Observable<Object> interceptorNotImplemented;
    @Nonnull
    private final PublishSubject httpCodeChangedSubject = PublishSubject.create();
    @Nonnull
    private final Observable<Boolean> pinMacroObservable;

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

        public CategoryItem(@Nonnull String title) {
            this.title = title;
        }

        @Nonnull
        public String getTitle() {
            return title;
        }

        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                }
            });
        }

    }

    public class InformationItem extends BaseDebugItem {

        @Nonnull
        private final String name;
        @Nonnull
        private final String value;

        public InformationItem(@Nonnull String name, @Nonnull String value) {
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
            if (this == o) {
                return true;
            }
            if (!(o instanceof InformationItem)) {
                return false;
            }
            InformationItem that = (InformationItem) o;
            return Objects.equal(name, that.name)
                    && Objects.equal(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, value);
        }

        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
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
        private boolean mockDepends;

        public OptionItem(@Nonnull String name, int option, @Nonnull List<Integer> values, int currentPosition, boolean mockDepends) {
            this.name = name;
            this.option = option;
            this.values = values;
            this.currentPosition = currentPosition;
            this.mockDepends = mockDepends;
        }

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

        public boolean isMockDepends() {
            return mockDepends;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof OptionItem)) {
                return false;
            }
            OptionItem that = (OptionItem) o;
            return Objects.equal(name, that.name)
                    && Objects.equal(values, that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, values);
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
        private Boolean mockDepends;

        public SwitchItem(@Nonnull String title, int option, Boolean staticSwitcher, Boolean mockDepends) {
            this.title = title;
            this.option = option;
            this.staticSwitcher = staticSwitcher;
            this.mockDepends = mockDepends;
        }

        public SwitchItem(@Nonnull String title, int option, Boolean mockDepends) {
            this.title = title;
            this.option = option;
            this.mockDepends = mockDepends;
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

        public Boolean isMockDepends() {
            return mockDepends;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SwitchItem)) {
                return false;
            }
            SwitchItem that = (SwitchItem) o;
            return Objects.equal(title, that.title)
                    && Objects.equal(option, that.option);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(title, option);
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
        private int action;

        public ActionItem(@Nonnull String name, int action) {
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
            if (this == o) {
                return true;
            }
            if (!(o instanceof ActionItem)) {
                return false;
            }
            ActionItem that = (ActionItem) o;
            return Objects.equal(name, that.name)
                    && Objects.equal(action, that.action);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, action);
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
        this.context = context;

        memorySubject = Observable.just((Long) null).map(new Func1<Long, ActivityManager.MemoryInfo>() {
            @Override
            public ActivityManager.MemoryInfo call(Long aLong) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.getMemoryInfo(mi);
                return mi;
            }
        });

        deviceInfoList = Observable.combineLatest(
                resolutionSubject,
                densitySubject,
                memorySubject,
                new Func3<String, Float, ActivityManager.MemoryInfo, List<InformationItem>>() {
                    @Override
                    public List<InformationItem> call(String resolution, Float density, ActivityManager.MemoryInfo mi) {
                        return ImmutableList.of(
                                new InformationItem("Model", Build.MANUFACTURER + " " + Build.MODEL),
                                new InformationItem("SDK", DebugTools.checkSDKName(
                                        Build.VERSION.SDK_INT)
                                        + "(" + Build.VERSION.SDK_INT
                                        + " API)"),
                                new InformationItem("Release", Build.VERSION.RELEASE),
                                new InformationItem("Resolution", resolution),
                                new InformationItem("Density", Math.round(density) + "dpi"),
                                new InformationItem("Free Memory", (mi.availMem / 1048576L) + " MB"));
                    }
                });

        buildInfoList = densitySubject
                .map(new Func1<Float, List<InformationItem>>() {
                    @Override
                    public List<InformationItem> call(Float density) {
                        return ImmutableList.of(
                                new InformationItem("Name", DebugTools.getApplicationName(context)),
                                new InformationItem("Package", context.getPackageName()),
                                new InformationItem("Build Type", DebugTools.getBuildType(context)),
                                new InformationItem("Version", DebugTools.getBuildVersion(context))
                        );

                    }
                });

        scalpelUtilsList = Observable.just(new Object())
                .map(new Func1<Object, List<BaseDebugItem>>() {
                    @Override
                    public List<BaseDebugItem> call(Object o) {
                        return ImmutableList.<BaseDebugItem>of(
                                new SwitchItem("Turn Scalpel ", DebugOption.SET_SCALPEL, false),
                                new SwitchItem("Draw Views", DebugOption.SCALPEL_DRAW_VIEWS, false),
                                new SwitchItem("Show Ids", DebugOption.SCALPEL_SHOW_ID, false));
                    }
                });

        utilList = Observable.just(new Object())
                .map(new Func1<Object, List<BaseDebugItem>>() {
                    @Override
                    public List<BaseDebugItem> call(Object o) {
                        return ImmutableList.<BaseDebugItem>of(
                                new SwitchItem("FPS Label", DebugOption.FPS_LABEL, DebugHelper.isFpsVisible(), false),
                                new InformationItem("LeakCanary", "enabled"),
                                new ActionItem("Show Log", DebugOption.SHOW_LOG));
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
                        return ImmutableList.<BaseDebugItem>builder()
                                .add(new MainItem(true, true))
                                .add(new CategoryItem("Device Information"))
                                .addAll(deviceInfo)
                                .add(new CategoryItem("About app"))
                                .addAll(buildInfo)
                                .add(new CategoryItem("Macro"))
                                .add(new ActionItem("Show Macro", DebugOption.SHOW_MACRO))
                                .add(new SwitchItem("Pin", DebugOption.PIN_MACRO, false, false))
                                .add(new CategoryItem("OKHTTP options"))
                                .add(new SwitchItem("Return empty response", DebugOption.SET_EMPTY_RESPONSE, DebugInterceptor.getEmptyResponse(), true))
                                .add(new OptionItem("Http code", DebugOption.SET_HTTP_CODE,
                                        ImmutableList.of(200,
                                                201, 202, 203, 204, 205,
                                                206, 300, 301, 302, 303,
                                                304, 305, 400, 401, 402,
                                                403, 404, 405, 406, 407,
                                                408, 409, 410, 411, 412,
                                                413, 414, 415, 500, 501,
                                                502, 503, 504, 505),
                                        DebugTools.selectHttpCodePosition(DebugInterceptor.getResponseCode()), true))
                                .add(new ActionItem("Request counter", DebugOption.SHOW_REQUEST))
                                .add(new CategoryItem("Scalpel Utils"))
                                .addAll(scalpelUtils)
                                .add(new CategoryItem("Tools"))
                                .addAll(utils)
                                .build();
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

        drawViewsObservable = switchOptionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugOption.SCALPEL_DRAW_VIEWS;
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
                    public String call(Integer integer) {
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
    public Observable<List<BaseDebugItem>> simpleListObservable() {
        return simpleListSubject;
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
    public Observable<Boolean> setDrawViewsObservable() {
        return drawViewsObservable;
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
}
