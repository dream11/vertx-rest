package com.dream11.rest.app.inject;

import com.dream11.rest.route.ClassInjector;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;

import java.util.List;

public class AppContext implements ClassInjector {
    private static volatile AppContext contextInstance = null;

    private Injector injector;

    private AppContext(List<Module> modules) {
        injector = Guice.createInjector(modules);
    }

    public static synchronized void initialize(List<Module> modules) {
        if (contextInstance != null) {
            throw new RuntimeException("Application context is already initialized.");
        } else {
            contextInstance = new AppContext(modules);
        }
    }

    public static synchronized void reset() {
        contextInstance = null;
    }

    private static AppContext instance() {
        if (contextInstance != null) {
            return contextInstance;
        }
        throw new RuntimeException("Application context not initialized.");
    }

    public static AppContext getContextInstance() {
        return contextInstance;
    }

    public <T> T getInstance(Class<T> klazz) {
        return instance().injector.getInstance(klazz);
    }

    public <T> T getInstance(Class<T> klazz, String name) {
        return instance().injector.getInstance(Key.get(klazz, Names.named(name)));
    }
}
