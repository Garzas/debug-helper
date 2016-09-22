package com.appunite.debughelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.appunite.debughelper.DebugHelperPreferences;
import com.appunite.debughelper.utils.DebugOption;
import com.appunite.debughelper.R;
import com.appunite.debughelper.interceptor.DebugInterceptor;
import com.appunite.debughelper.presenter.DebugPresenter;
import com.google.common.collect.ImmutableList;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import java.util.List;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.appunite.debughelper.DebugHelper.getDebugPreferences;

abstract class BaseDebugHolder extends RecyclerView.ViewHolder {

    public BaseDebugHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(@Nonnull DebugPresenter.BaseDebugItem item);

    public abstract void recycle();
}

public class DebugAdapter extends RecyclerView.Adapter<BaseDebugHolder> implements
        Action1<List<DebugPresenter.BaseDebugItem>> {

    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_INFORMATION = 1;
    private static final int TYPE_SWITCH = 2;
    private static final int TYPE_OPTION = 3;
    private static final int TYPE_ACTION = 4;
    private static final int TYPE_MAIN = 5;

    static class MainOptionHolder extends BaseDebugHolder {

        private Subscription mSubscription;

        ToggleButton mockButton;

        public MainOptionHolder(@Nonnull final View itemView) {
            super(itemView);

            mockButton = (ToggleButton) itemView.findViewById(R.id.mock_toggle);
        }

        @Override
        public void bind(@Nonnull final DebugPresenter.BaseDebugItem item) {
            final DebugPresenter.MainItem mainItem = (DebugPresenter.MainItem) item;

            mockButton.setChecked(getDebugPreferences().getMockState());

            mSubscription = new CompositeSubscription(
                    RxCompoundButton.checkedChanges(mockButton)
                            .skip(1)
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    getDebugPreferences().saveMockState(aBoolean);
                                    mainItem.clickObserver().onNext(new Object());
                                }
                            })
            );
        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }

        }

        public static MainOptionHolder create(@Nonnull final ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new MainOptionHolder(inflater.inflate(R.layout.debug_main_item, parent, false));
        }
    }

    static class CategoryHolder extends BaseDebugHolder {

        TextView title;

        public CategoryHolder(@Nonnull final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.debug_category_title);
        }

        @Override
        public void bind(@Nonnull final DebugPresenter.BaseDebugItem item) {
            final DebugPresenter.CategoryItem categoryItem = (DebugPresenter.CategoryItem) item;
            title.setText(categoryItem.getTitle());

        }

        @Override
        public void recycle() {
        }

        public static CategoryHolder create(@Nonnull final ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new CategoryHolder(inflater.inflate(R.layout.debug_category_item, parent, false));
        }

    }

    static class InformationHolder extends BaseDebugHolder {

        TextView name;
        TextView value;

        public InformationHolder(@Nonnull final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.debug_info_name);
            value = (TextView) itemView.findViewById(R.id.debug_info_value);
        }

        @Override
        public void bind(@Nonnull final DebugPresenter.BaseDebugItem item) {
            final DebugPresenter.InformationItem informationItem = (DebugPresenter.InformationItem) item;

            name.setText(informationItem.getName());
            value.setText(informationItem.getValue());
            value.setSelected(true);
        }

        @Override
        public void recycle() {
        }

        public static InformationHolder create(@Nonnull final ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new InformationHolder(inflater.inflate(R.layout.debug_info_item, parent, false));
        }

    }

    static class OptionHolder extends BaseDebugHolder {

        private final View view;
        private Subscription mSubscription;

        TextView optionName;
        Button button;
        View mockDisabled;

        public OptionHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            optionName = (TextView) itemView.findViewById(R.id.debug_option_name);
            button = (Button) itemView.findViewById(R.id.debug_option_value);
            mockDisabled = itemView.findViewById(R.id.mock_disabled_layout);
        }

        @Override
        public void bind(@Nonnull final DebugPresenter.BaseDebugItem item) {
            recycle();
            final DebugPresenter.OptionItem optionItem = (DebugPresenter.OptionItem) item;

            if (optionItem.isMockDepends()) {
                mockDisabled.setVisibility(getDebugPreferences().getMockState() ? View.GONE : View.VISIBLE);
            }

            button.setText(String.format("%d", DebugInterceptor.getResponseCode()));
            optionName.setText(optionItem.getName());
            mSubscription = new CompositeSubscription(
                    RxView.clicks(button).subscribe(optionItem.clickObserver()));

        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }

        public static OptionHolder create(@Nonnull final ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new OptionHolder(inflater.inflate(R.layout.debug_option_item, parent, false));
        }

    }

    static class SwitchHolder extends BaseDebugHolder {

        private final View view;
        private DebugHelperPreferences debugPreferences;
        private Subscription mSubscription;

        Switch debugSwitch;
        TextView title;
        View mockDisabled;

        public SwitchHolder(@Nonnull final View itemView, @Nonnull final DebugHelperPreferences debugPreferences) {
            super(itemView);
            this.view = itemView;
            this.debugPreferences = debugPreferences;
            debugSwitch = (Switch) itemView.findViewById(R.id.debug_switch);
            title = (TextView) itemView.findViewById(R.id.debug_switch_title);
            mockDisabled = itemView.findViewById(R.id.mock_disabled_layout);
        }

        @Override
        public void bind(@Nonnull final DebugPresenter.BaseDebugItem item) {
            recycle();
            final DebugPresenter.SwitchItem switchItem = (DebugPresenter.SwitchItem) item;

            title.setText(switchItem.getTitle());
            if (switchItem.isMockDepends()) {
                mockDisabled.setVisibility(getDebugPreferences().getMockState() ? View.GONE : View.VISIBLE);
            } else {
                mockDisabled.setVisibility(View.GONE);
            }

            mSubscription = new CompositeSubscription(
                    Observable.just(switchItem.isStaticSwitcher())
                            .filter(new Func1<Boolean, Boolean>() {
                                @Override
                                public Boolean call(Boolean aBoolean) {
                                    return (switchItem.getOption() == DebugOption.SET_EMPTY_RESPONSE
                                            || switchItem.getOption() == DebugOption.FPS_LABEL);
                                }
                            })
                            .subscribe(RxCompoundButton.checked(debugSwitch)),
                    RxCompoundButton.checkedChanges(debugSwitch)
                            .skip(1)
                            .subscribe(switchItem.switchOption())
            );
        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }

        public static SwitchHolder create(@Nonnull final ViewGroup parent, @Nonnull final DebugHelperPreferences debugPreferences) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new SwitchHolder(inflater.inflate(R.layout.debug_switch_item, parent, false), debugPreferences);
        }

    }

    static class ActionHolder extends BaseDebugHolder {

        private final View view;
        private Subscription mSubscription;

        TextView actionName;

        public ActionHolder(@Nonnull final View itemView) {
            super(itemView);
            this.view = itemView;
            actionName = (TextView) itemView.findViewById(R.id.debug_action_name);
        }

        @Override
        public void bind(@Nonnull final DebugPresenter.BaseDebugItem item) {
            recycle();
            DebugPresenter.ActionItem actionItem = (DebugPresenter.ActionItem) item;

            actionName.setText(actionItem.getName());

            mSubscription = new CompositeSubscription(
                    RxView.clicks(view)
                            .subscribe(actionItem.actionOption())
            );


        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }

        public static ActionHolder create(@Nonnull final ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ActionHolder(inflater.inflate(R.layout.debug_action_item, parent, false));
        }

    }

    @Nonnull
    private List<DebugPresenter.BaseDebugItem> baseDebugItems = ImmutableList.of();

    @Nonnull
    private DebugHelperPreferences debugPreferences;


    public DebugAdapter(@Nonnull final DebugHelperPreferences debugPreferences) {
        this.debugPreferences = debugPreferences;
    }


    @Override
    public void call(@Nonnull final List<DebugPresenter.BaseDebugItem> baseDebugItems) {
        this.baseDebugItems = baseDebugItems;
    }

    @Override
    public BaseDebugHolder onCreateViewHolder(@Nonnull final ViewGroup parent, final int viewType) {
        if (viewType == TYPE_CATEGORY) {
            return CategoryHolder.create(parent);
        } else if (viewType == TYPE_INFORMATION) {
            return InformationHolder.create(parent);
        } else if (viewType == TYPE_SWITCH) {
            return SwitchHolder.create(parent, debugPreferences);
        } else if (viewType == TYPE_ACTION) {
            return ActionHolder.create(parent);
        } else if (viewType == TYPE_OPTION) {
            return OptionHolder.create(parent);
        } else if (viewType == TYPE_MAIN) {
            return MainOptionHolder.create(parent);
        }
        throw new RuntimeException("there is no type that matches the type "
                + viewType
                + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(@Nonnull final BaseDebugHolder holder, final int position) {
        holder.bind(baseDebugItems.get(position));
    }


    @Override
    public int getItemViewType(final int position) {
        final DebugPresenter.BaseDebugItem item = baseDebugItems.get(position);
        if (item instanceof DebugPresenter.CategoryItem) {
            return TYPE_CATEGORY;
        } else if (item instanceof DebugPresenter.InformationItem) {
            return TYPE_INFORMATION;
        } else if (item instanceof DebugPresenter.SwitchItem) {
            return TYPE_SWITCH;
        } else if (item instanceof DebugPresenter.OptionItem) {
            return TYPE_OPTION;
        } else if (item instanceof DebugPresenter.ActionItem) {
            return TYPE_ACTION;
        } else if (item instanceof DebugPresenter.MainItem) {
            return TYPE_MAIN;
        } else {
            throw new IllegalStateException("Cannot find item for position" + position);
        }
    }

    @Override
    public int getItemCount() {
        return baseDebugItems.size();
    }

    @Override
    public void onViewRecycled(@Nonnull final BaseDebugHolder holder) {
        holder.recycle();
        super.onViewRecycled(holder);
    }
}
