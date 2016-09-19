package com.appunite.example.debugutilsexample.dagger;

import dagger.Component;

@ActivityScope
@Component(
        modules = {
                FragmentModule.class
        }
)
public interface BaseFragmentComponent {

}
