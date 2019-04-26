package com.base.and.utils.rxbus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Makise on 2016/8/5.
 */
public class RxSubscriptions {
    private static CompositeDisposable mSubscriptions = new CompositeDisposable();

    public static boolean isUnsubscribed() {
        return mSubscriptions.isDisposed();
    }

    public static void add(Disposable s) {
        if (s != null) {
            mSubscriptions.add(s);
        }
    }

    public static void remove(Disposable... s) {
        for (Disposable disposable : s) {
            if (disposable != null) {
                mSubscriptions.remove(disposable);
            }
        }
    }

    public static void clear() {
        mSubscriptions.clear();
    }

    public static void unsubscribe() {
        mSubscriptions.dispose();
    }

//    public static boolean hasSubscriptions() {
//        return mSubscriptions.hasSubscriptions();
//    }
}
