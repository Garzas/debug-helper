package com.appunite.example.debugutilsexample.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appunite.example.debugutilsexample.R;
import com.appunite.example.debugutilsexample.detector.ChangesDetector;
import com.appunite.example.debugutilsexample.detector.SimpleDetector;
import com.appunite.example.debugutilsexample.presenter.MainPresenter;
import com.google.common.collect.ImmutableList;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(@Nonnull MainPresenter.BaseItem item);

    public abstract void recycle();
}

public class MainAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        Action1<List<MainPresenter.BaseItem>>, ChangesDetector.ChangesAdapter  {

    private static final int TYPE_MAIN = 0;

    @Nonnull
    private final ChangesDetector<MainPresenter.BaseItem, MainPresenter.BaseItem> changesDetector;
    @Nonnull
    private List<MainPresenter.BaseItem> items = ImmutableList.of();

    @Inject
    public MainAdapter() {
        this.changesDetector = new ChangesDetector<>(new SimpleDetector<MainPresenter.BaseItem>());
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_MAIN) {
            return MainViewHolder.create(parent);
        }
        throw new RuntimeException("there is no type that matches the type "
                + viewType
                + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public void onViewRecycled(BaseViewHolder holder) {
        super.onViewRecycled(holder);
        holder.recycle();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void call(List<MainPresenter.BaseItem> baseItems) {
        this.items = baseItems;
        changesDetector.newData(this, baseItems, false);
    }


    static class MainViewHolder extends BaseViewHolder {

        private CompositeSubscription subscription;

        @InjectView(R.id.repo_name)
        TextView text;
        @InjectView(R.id.repo_description)
        TextView description;

        public MainViewHolder(@Nonnull View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void bind(@Nonnull MainPresenter.BaseItem item) {
            final MainPresenter.RepoItem repoItem = (MainPresenter.RepoItem) item;
            text.setText(repoItem.getName());
            description.setText(repoItem.getDescription());
            subscription = new CompositeSubscription(
                    RxView.clicks(text).subscribe(repoItem.clickObserver())
            );
        }

        @Override
        public void recycle() {
            subscription.unsubscribe();
        }


        public static MainViewHolder create(ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new MainViewHolder(inflater.inflate(
                    R.layout.repo_item, parent, false));
        }
    }
}
