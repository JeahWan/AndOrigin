package com.base.and.utils.rxbus;


import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Rxjava封装工具类
 * Created by Makise on 2016/8/3.
 */
public class RxTask {

    /**
     * 在ui线程中工作
     *
     * @param uiTask
     */
    public static void doInUIThread(UITask uiTask) {
        doInUIThreadDelay(uiTask, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延时在主线程中执行任务
     *
     * @param uiTask
     * @param time
     * @param timeUnit
     */
    public static void doInUIThreadDelay(UITask uiTask, long time, TimeUnit timeUnit) {
        Observable.just(uiTask)
                .delay(time, timeUnit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UITask>() {
                    @Override
                    public void accept(UITask uitask) {
                        uitask.doInUIThread();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
//                        LogUtil.info(throwable.toString());
                    }
                });
    }

    /**
     * 在IO线程中执行任务
     */
    public static void doInIOThread(IOTask ioTask) {
        doInIOThreadDelay(ioTask, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延时在IO线程中执行任务
     */
    public static void doInIOThreadDelay(IOTask ioTask, long time, TimeUnit timeUnit) {
        Observable.just(ioTask)
                .delay(time, timeUnit)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<IOTask>() {
                    @Override
                    public void accept(IOTask ioTask) {
                        ioTask.doInIOThread();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
//                        LogUtil.info(throwable.toString());
                    }
                });
    }

    /**
     * 执行Rx通用任务 (IO线程中执行耗时操作 执行完成调用UI线程中的方法)
     *
     * @param t
     */
    public static void executeRxTask(CommonTask t) {
        executeRxTaskDelay(t, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延时执行Rx通用任务 (IO线程中执行耗时操作 执行完成调用UI线程中的方法)
     *
     * @param t
     */
    public static void executeRxTaskDelay(CommonTask t, long time, TimeUnit timeUnit) {
        MyOnSubscribe<CommonTask> onsubscribe = new MyOnSubscribe<CommonTask>(t) {
            @Override
            public void subscribe(@NonNull ObservableEmitter<CommonTask> e) throws Exception {
                getT().doInIOThread();
                e.onNext(getT());
                e.onComplete();
            }
        };
        Observable.create(onsubscribe)
                .delay(time, timeUnit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommonTask>() {
                    @Override
                    public void accept(CommonTask t) {
                        t.doInUIThread();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
//                        LogUtil.info(throwable.toString());
                    }
                });
    }

    /**
     * 在IO线程中执行的任务
     * Created by Makise on 2016/8/3.
     */
    public static abstract class IOTask {
//        private T t;
//
//        public IOTask(T t) {
//            setT(t);
//        }
//
//        public IOTask() {
//        }
//
//        public T getT() {
//            return t;
//        }
//
//        public void setT(T t) {
//            this.t = t;
//        }

        public abstract void doInIOThread();
    }

    /**
     * 在UI线程中执行的任务
     * Created by Makise on 2016/8/3.
     */
    public static abstract class UITask {
        public abstract void doInUIThread();
    }

    /**
     * 通用的Rx执行任务
     * Created by Makise on 2016/8/3.
     */
    public static abstract class CommonTask {
//        private T t;
//
//        public CommonTask(T t) {
//            setT(t);
//        }
//
//        public CommonTask() {
//
//        }
//
//        public T getT() {
//            return t;
//        }
//
//        public void setT(T t) {
//            this.t = t;
//        }

        public abstract void doInIOThread();

        public abstract void doInUIThread();

    }

    /**
     * 自定义OnSubscribe
     * Created by Makise on 2016/8/3.
     */
    public static abstract class MyOnSubscribe<T> implements ObservableOnSubscribe<T> {
        private T t;

        public MyOnSubscribe(T t) {
            setT(t);
        }

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }
}