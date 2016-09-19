package com.appunite.example.debugutilsexample.dao;

import com.appunite.example.debugutilsexample.model.Owner;
import com.appunite.example.debugutilsexample.model.Repos;
import com.appunite.example.debugutilsexample.service.GitHubService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import rx.Observable;
import rx.observers.TestObserver;
import rx.schedulers.Schedulers;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Mockito.when;

public class GitHubDaoTest {

    @Mock
    GitHubService service;

    private GitHubDao gitHubDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(service.getRepos()).thenReturn(
                Observable.<List<Repos>>just(ImmutableList.<Repos>builder()
                        .add(new Repos(1, "name", "des1", true,
                                new Owner("login1", 1, "avatar_url1", "html_url1", "organisation")))
                        .add(new Repos(2, "name2", "des2", false,
                                new Owner("login2", 2, "avatar_url2", "html_url2", "organisation")))
                        .build())
        );

        gitHubDao = new GitHubDao(service, Schedulers.immediate(), Schedulers.immediate());
    }

    @Test
    public void checkIfDataIsMapingProperly() {
        TestObserver<List<Repos>> listTestObserver = new TestObserver<>();

        gitHubDao.getReposObservable().subscribe(listTestObserver);

        assert_().that(getLastOnNextEvent(listTestObserver)).hasSize(2);
    }

    public static <T> T getLastOnNextEvent(TestObserver<T> testObserver) {
        return Iterables.getLast(testObserver.getOnNextEvents());
    }

    public static <T> T getFirstOnNextEvent(TestObserver<T> observer) {
        return Iterables.getFirst(observer.getOnNextEvents(), null);
    }
}
