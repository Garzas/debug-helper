package com.appunite.debughelper.macro;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.appunite.debughelper.DebugHelperPreferences;
import com.appunite.debughelper.model.EditMacro;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;

public class MacroPresenter {

    @Nonnull
    private final PublishSubject<ViewGroup> createClickSubject = PublishSubject.create();
    @Nonnull
    private final Observable<List<MacroItem>> macroItemsObservable;
    @Nonnull
    private final Activity activity;
    @Nonnull
    private final DebugHelperPreferences debugHelperPreferences;
    @Nonnull
    private final PublishSubject<Integer> deleteMacroSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<EditMacro> changeNameSubject = PublishSubject.create();
    @Nonnull
    private final Observable<List<MacroItem>> saveMacrosObservable;
    @Nonnull
    public final Subscription subscribe;
    @Nonnull
    private final PublishSubject<String> selectMacroSubject = PublishSubject.create();

    public MacroPresenter(@Nonnull final Activity activity) {
        this.activity = activity;
        debugHelperPreferences = new DebugHelperPreferences(activity);

        macroItemsObservable = debugHelperPreferences.getJsonMacrosObservable()
                .map(new Func1<String, List<MacroItem>>() {
                    @Override
                    public List<MacroItem> call(final String s) {
                        return getMacroItems(s);
                    }
                });

        saveMacrosObservable = Observable.merge(
                createClickSubject
                        .withLatestFrom(macroItemsObservable, new Func2<ViewGroup, List<MacroItem>, List<MacroItem>>() {
                            @Override
                            public List<MacroItem> call(final ViewGroup viewGroup, final List<MacroItem> macroItems) {
                                macroItems.add(new MacroItem(mergeFields(viewGroup), activity.getClass().getSimpleName()));
                                return macroItems;
                            }
                        }),
                deleteMacroSubject
                        .withLatestFrom(macroItemsObservable, new Func2<Integer, List<MacroItem>, List<MacroItem>>() {
                            @Override
                            public List<MacroItem> call(final Integer position, final List<MacroItem> macroItems) {
                                final MacroItem macroItem = macroItems.get(position);
                                if (macroItem.isSelected()) {
                                    debugHelperPreferences.saveFastMacro(macroItem.getActivityName(), null);
                                }
                                macroItems.remove((int) position);
                                return macroItems;
                            }
                        }),
                changeNameSubject
                        .withLatestFrom(macroItemsObservable, new Func2<EditMacro, List<MacroItem>, List<MacroItem>>() {
                            @Override
                            public List<MacroItem> call(final EditMacro editMacro, final List<MacroItem> macroItems) {
                                macroItems.get(editMacro.getPosition()).setMacroName(editMacro.getName());
                                return macroItems;
                            }
                        }),
                selectMacroSubject.withLatestFrom(macroItemsObservable, new Func2<String, List<MacroItem>, List<MacroItem>>() {
                    @Override
                    public List<MacroItem> call(final String id, final List<MacroItem> macroItems) {
                        for (final MacroItem macroItem : macroItems) {
                            if (macroItem.getId().equals(id)) {
                                macroItem.setSelected(true);
                                debugHelperPreferences.saveFastMacro(macroItem.getActivityName(), createGson().toJson(macroItem));
                            } else {
                                macroItem.setSelected(false);
                            }
                        }
                        return macroItems;
                    }
                }))
                .publish()
                .refCount();

        subscribe = saveMacrosObservable
                .debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<MacroItem>>() {
                    @Override
                    public void call(final List<MacroItem> macroItems) {
                        final Gson macroGson = createGson();

                        final String serializedMacros = macroGson.toJson(macroItems);
                        debugHelperPreferences.saveMacroList(serializedMacros);
                    }
                });

    }

    public List<MacroPresenter.MacroItem> getMacroItems(final String json) {
        final Gson gson = createGson();

        Type collectionType = new TypeToken<List<MacroPresenter.MacroItem>>() {

        }.getType();
        final List<MacroPresenter.MacroItem> macroItems = gson.fromJson(json, collectionType);

        return Lists.newArrayList(Iterables
                .filter(macroItems, new Predicate<MacroItem>() {
                    @Override
                    public boolean apply(@Nullable final MacroPresenter.MacroItem input) {
                        assert input != null;
                        return input.getActivityName().equals(activity.getClass().getSimpleName());
                    }
                }));
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .create();
    }

    public List<SavedField> mergeFields(View view) {
        final List<SavedField> completeFieldList = new ArrayList<>();

        if (view instanceof RecyclerView) {
//            final RecyclerView recycleItem = (RecyclerView) view;
//            if (recycleItem.getAdapter() instanceof MacroRecyclerViewListener) {
//                final MacroRecyclerViewListener recyclerViewDataHolder = (MacroRecyclerViewListener) recycleItem.getAdapter();
//
//                final MacroData macroData = recyclerViewDataHolder.macroData();
//                //TODO more items is lagging
//                if (macroData.getAdapterList().size() <= 15) {
//                    final String json = adapterGson().toJson(macroData.getAdapterList());
//                    completeFieldList.add(new SavedField(recycleItem.getId(), json, macroData.getAdapterType()));
//                }
//            } else {
//                Toast toast = Toast.makeText(getActivity(), "If you want save "
//                        + recycleItem.getAdapter().getClass().getSimpleName()
//                        + " data, implement "
//                        + MacroRecyclerViewListener.class.getSimpleName(), Toast.LENGTH_LONG);
//                toast.show();
//            }
        } else if (view instanceof Spinner) {
            final Spinner spinner = (Spinner) view;
            completeFieldList.add(new SavedField(spinner.getId(), spinner.getSelectedItemPosition()));
        } else if (view instanceof SearchView) {
            final SearchView searchView = (SearchView) view;
            completeFieldList.add(new SavedField(searchView.getId(), searchView.getQuery().toString()));
        } else if (view instanceof ViewGroup) {
            final ViewGroup parentView = (ViewGroup) view;
            final int childCount = parentView.getChildCount();

            for (int i = 0; i < childCount; i++) {
                completeFieldList.addAll(mergeFields(parentView.getChildAt(i)));
            }
        } else if (view instanceof EditText) {
            final EditText editText = (EditText) view;
            completeFieldList.add(new SavedField(editText.getId(), editText.getText().toString()));
        } else if (view instanceof CompoundButton) {
            final CompoundButton button = (CompoundButton) view;
            completeFieldList.add(new SavedField(button.getId(), button.isChecked()));
        }
        return completeFieldList;
    }

    public Observer<ViewGroup> createClickObserver() {
        return createClickSubject;
    }

    public Observable<List<MacroItem>> getMacroListObservable() {
        return macroItemsObservable;
    }

    @Nonnull
    public final Observer<Integer> deleteMacroObserver() {
        return deleteMacroSubject;
    }

    @Nonnull
    public final Observer<String> selectMacroObserver() {
        return selectMacroSubject;
    }

    @Nonnull
    public Observable<List<MacroItem>> getSaveMacrosObservable() {
        return saveMacrosObservable;
    }

    public Observer<EditMacro> changeNameObserver() {
        return changeNameSubject;
    }

    public class MacroItem {

        private List<SavedField> baseFieldItems;
        private final String activityName;
        private String macroName;
        private boolean isSelected = false;
        final String id;

        public MacroItem(final List<SavedField> baseFieldItems, final String activityName) {
            this.baseFieldItems = baseFieldItems;
            this.activityName = activityName;
            macroName = activityName + " Macro";
            id = UUID.randomUUID().toString();

        }

        public String getActivityName() {
            return activityName;
        }

        public String getMacroName() {
            return macroName;
        }

        public void setMacroName(String macroName) {
            this.macroName = macroName;
        }

        public List<SavedField> getBaseFieldItems() {
            return baseFieldItems;
        }

        public void setSelected(final boolean selected) {
            isSelected = selected;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof MacroItem)) return false;
            final MacroItem macroItem = (MacroItem) o;
            return Objects.equal(baseFieldItems, macroItem.baseFieldItems) &&
                    Objects.equal(activityName, macroItem.activityName) &&
                    Objects.equal(macroName, macroItem.macroName);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(baseFieldItems, activityName, macroName);
        }

    }
}
