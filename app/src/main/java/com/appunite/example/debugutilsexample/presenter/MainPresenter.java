package com.appunite.example.debugutilsexample.presenter;


import com.appunite.example.debugutilsexample.dao.GitHubDao;
import com.appunite.example.debugutilsexample.detector.SimpleDetector;
import com.appunite.example.debugutilsexample.model.Repos;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Observers;

public class MainPresenter {

    @Nonnull
    private final Observable<String> titleObservable;
    @Nonnull
    private final Observable<List<RepoItem>> repositoriesList;
    @Nonnull
    private final Observable<List<BaseItem>> itemListObservable;

    @Inject
    public MainPresenter(@Nonnull final GitHubDao gitHubDao) {

        titleObservable = Observable.just("Appunite Repositories");

        repositoriesList = gitHubDao.getReposObservable()
                .map(new Func1<List<Repos>, List<RepoItem>>() {
                    @Override
                    public List<RepoItem> call(List<Repos> repositories) {
                        return ImmutableList.copyOf(Iterables.transform(repositories, new Function<Repos, RepoItem>() {
                            @Nullable
                            @Override
                            public RepoItem apply(Repos repos) {
                                return new RepoItem(
                                        repos.getName(),
                                        repos.getDescription(),
                                        repos.getFork(),
                                        repos.getId());
                            }
                        }));
                    }
                });

        itemListObservable = repositoriesList
                .map(new Func1<List<RepoItem>, List<BaseItem>>() {
                    @Override
                    public List<BaseItem> call(List<RepoItem> repoItems) {
                        return ImmutableList.<BaseItem>copyOf(repoItems);
                    }
                });
    }

    @Nonnull
    public Observable<String> getTitleObservable() {
        return titleObservable;
    }

    @Nonnull
    public Observable<List<BaseItem>> getItemListObservable() {
        return itemListObservable;
    }

    public abstract static class BaseItem implements SimpleDetector.Detectable<BaseItem> {
    }

    public class RepoItem extends BaseItem {

        private String name;
        private String description;
        private Boolean fork;
        private Integer id;

        public RepoItem(String name, String description, Boolean fork, Integer id) {
            this.name = name;
            this.description = description;
            this.fork = fork;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RepoItem)) {
                return false;
            }

            final RepoItem that = (RepoItem) o;

            return name.equals(that.name);
        }

        @Override
        public boolean matches(@Nonnull BaseItem item) {
            return Objects.equal(name, ((RepoItem) item).getName());
        }

        @Override
        public boolean same(@Nonnull BaseItem item) {
            return equals(item);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Nonnull
        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                }
            });
        }

        @Nonnull
        public String getName() {
            return name;
        }

        @Nonnull
        public String getDescription() {
            return description;
        }

        @Nonnull
        public Boolean getFork() {
            return fork;
        }

        @Nonnull
        public Integer getId() {
            return id;
        }
    }

}
