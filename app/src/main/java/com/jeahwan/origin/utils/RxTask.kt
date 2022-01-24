package com.jeahwan.origin.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Rxjava封装工具类
 * Created by Jeah on 2016/8/3.
 */
object RxTask {
    /**
     * 在ui线程中工作
     *
     * @param uiTask
     */
    fun doInMainThread(doInUIThread: () -> Unit) {
        doInMainThreadDelay(doInUIThread, 0, TimeUnit.MILLISECONDS)
    }

    /**
     * 延时在主线程中执行任务
     *
     * @param uiTask
     * @param time
     * @param timeUnit
     */
    fun doInMainThreadDelay(doInUIThread: () -> Unit, time: Long, timeUnit: TimeUnit?) {
        Observable.just(1)
            .delay(time, timeUnit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ doInUIThread() }) { throwable: Throwable? -> }
    }

    /**
     * 在IO线程中执行任务
     */
    fun doInIOThread(doInIOThread: () -> Unit) {
        doInIOThreadDelay(doInIOThread, 0, TimeUnit.MILLISECONDS)
    }

    /**
     * 延时在IO线程中执行任务
     */
    fun doInIOThreadDelay(doInIOThread: () -> Unit, time: Long, timeUnit: TimeUnit?) {
        Observable.just(1)
            .delay(time, timeUnit)
            .observeOn(Schedulers.io())
            .subscribe({ doInIOThread() }) { throwable: Throwable? -> }
    }

    /**
     * 执行Rx通用任务 (IO线程中执行耗时操作 执行完成调用UI线程中的方法)
     */
    fun <T> executeRxTask(doInIoThread: () -> T, doInUIThread: (t: T?) -> Unit) {
        executeRxTaskDelay(doInIoThread, doInUIThread, 0, TimeUnit.MILLISECONDS)
    }

    /**
     * 延时执行Rx通用任务 (IO线程中执行耗时操作 执行完成调用UI线程中的方法)
     *
     */
    fun <T> executeRxTaskDelay(
        doInIoThread: () -> T,
        doInUIThread: (t: T?) -> Unit,
        time: Long,
        timeUnit: TimeUnit?,
    ) {
        var tResult: T? = null
        Observable.create(ObservableOnSubscribe<Any> {
            tResult = doInIoThread()
            it.onNext(1)
            it.onComplete()
        })
            .delay(time, timeUnit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { doInUIThread(tResult) }
    }
}