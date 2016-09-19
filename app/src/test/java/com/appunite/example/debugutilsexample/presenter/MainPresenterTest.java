package com.appunite.example.debugutilsexample.presenter;

import com.appunite.example.debugutilsexample.dao.GitHubDao;
import com.appunite.example.debugutilsexample.model.Owner;
import com.appunite.example.debugutilsexample.model.Repos;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;
import rx.observers.TestObserver;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    @Mock
    GitHubDao gitHubDao;

    private MainPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(gitHubDao.getReposObservable()).thenReturn(
                Observable.<List<Repos>>just(ImmutableList.<Repos>builder()
                        .add(new Repos(1, "name", "des1", true,
                                new Owner("login1", 1, "avatar_url1", "html_url1", "organisation")))
                        .add(new Repos(2, "name2", "des2", false,
                                new Owner("login2", 2, "avatar_url2", "html_url2", "organisation")))
                        .build())
        );

        presenter = new MainPresenter(gitHubDao);

    }

    @Test
    public void checkIfRepositoriesAreCorrect() {
        TestObserver<List<MainPresenter.BaseItem>> listTestObserver = new TestObserver<>();

        presenter.getItemListObservable().subscribe(listTestObserver);

        assert_().that(listTestObserver.getOnNextEvents()).isNotEmpty();
        assert_().that(getLastOnNextEvent(listTestObserver).size()).isEqualTo(2);
    }

    @Test
    public void checkIfRepoItemIsCorrectlyCreated() {
        TestObserver<List<MainPresenter.BaseItem>> listTestObserver = new TestObserver<>();

        presenter.getItemListObservable().subscribe(listTestObserver);

        MainPresenter.RepoItem repoItem = (MainPresenter.RepoItem) getLastOnNextEvent(listTestObserver).get(0);

        assert_().that(repoItem.getName()).isEqualTo("name");
        assert_().that(repoItem.getDescription()).isEqualTo("des1");
        assert_().that(repoItem.getFork()).isEqualTo(true);
        assert_().that(repoItem.getId()).isEqualTo(1);
    }

    @Test
    public void checkIfRepoItemMatches() {
        TestObserver<List<MainPresenter.BaseItem>> listTestObserver = new TestObserver<>();

        presenter.getItemListObservable().subscribe(listTestObserver);

        MainPresenter.RepoItem repoItem = (MainPresenter.RepoItem) getLastOnNextEvent(listTestObserver).get(0);
        MainPresenter.RepoItem repoItemClone = presenter.new RepoItem("name", "des1", true, 1);

        assert_().that(repoItem.equals(repoItemClone)).isTrue();
        assert_().that(repoItem.equals(new Object())).isFalse();
        assert_().that(repoItem.hashCode()).isEqualTo(repoItemClone.hashCode());
        assert_().that(repoItem.same(repoItemClone));
    }

    @Test
    public void checkIfTitleHasValue() {
        TestObserver<String> testObserver = new TestObserver<>();

        presenter.getTitleObservable().subscribe(testObserver);

        assert_().that(getLastOnNextEvent(testObserver)).isNotEmpty();
    }

    public static <T> T getLastOnNextEvent(TestObserver<T> testObserver) {
        return Iterables.getLast(testObserver.getOnNextEvents());
    }

    public static <T> T getFirstOnNextEvent(TestObserver<T> observer) {
        return Iterables.getFirst(observer.getOnNextEvents(), null);
    }
}
