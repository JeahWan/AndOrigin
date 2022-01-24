package com.jeahwan.origin.utils.permissions

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*

class RxPermissions {
    @VisibleForTesting
    var mRxPermissionsFragment: Lazy<RxPermissionsFragment>?

    constructor(activity: @NonNull FragmentActivity?) {
        mRxPermissionsFragment = getLazySingleton(activity?.supportFragmentManager)
    }

    constructor(fragment: @NonNull Fragment?) {
        mRxPermissionsFragment = getLazySingleton(fragment?.childFragmentManager)
    }

    private fun getLazySingleton(fragmentManager: @NonNull FragmentManager?): @NonNull Lazy<RxPermissionsFragment> {
        return object : Lazy<RxPermissionsFragment> {
            private var rxPermissionsFragment: RxPermissionsFragment? = null

            @Synchronized
            override fun get(): RxPermissionsFragment {
                if (rxPermissionsFragment == null) {
                    rxPermissionsFragment = getRxPermissionsFragment(fragmentManager)
                }
                return rxPermissionsFragment!!
            }
        }
    }

    private fun getRxPermissionsFragment(fragmentManager: @NonNull FragmentManager?): RxPermissionsFragment {
        var rxPermissionsFragment = findRxPermissionsFragment(fragmentManager)
        val isNewInstance = rxPermissionsFragment == null
        if (isNewInstance) {
            rxPermissionsFragment = RxPermissionsFragment()
            fragmentManager
                ?.beginTransaction()
                ?.add(rxPermissionsFragment, TAG)
                ?.commitAllowingStateLoss()
        }
        return rxPermissionsFragment!!
    }

    private fun findRxPermissionsFragment(fragmentManager: @NonNull FragmentManager?): RxPermissionsFragment? {
        return fragmentManager?.findFragmentByTag(TAG) as RxPermissionsFragment?
    }

    fun setLogging(logging: Boolean) {
        mRxPermissionsFragment!!.get().setLogging(logging)
    }

    /**
     * Map emitted items from the source observable into `true` if permissions in parameters
     * are granted, or `false` if not.
     *
     *
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    fun <T> ensure(vararg permissions: String?): ObservableTransformer<T, Boolean> {
        return ObservableTransformer { o ->
            request(
                o,
                *permissions as Array<out String>
            ) // Transform Observable<Permission> to Observable<Boolean>
                .buffer(permissions.size)
                .flatMap<Boolean>(Function<List<Permission>, ObservableSource<Boolean>> { permissions ->
                    if (permissions.isEmpty()) {
                        // Occurs during orientation change, when the subject receives onComplete.
                        // In that case we don't want to propagate that empty list to the
                        // subscriber, only the onComplete.
                        return@Function Observable.empty<Boolean>()
                    }
                    // Return true if all permissions are granted.
                    for (p in permissions) {
                        if (!p.granted) {
                            return@Function Observable.just(false)
                        }
                    }
                    Observable.just(true)
                })
        }
    }

    /**
     * Map emitted items from the source observable into [Permission] objects for each
     * permission in parameters.
     *
     *
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    fun <T> ensureEach(vararg permissions: String?): ObservableTransformer<T, Permission> {
        return ObservableTransformer { o -> request(o, *permissions as Array<out String>) }
    }

    /**
     * Map emitted items from the source observable into one combined [Permission] object. Only if all permissions are granted,
     * permission also will be granted. If any permission has `shouldShowRationale` checked, than result also has it checked.
     *
     *
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    fun <T> ensureEachCombined(vararg permissions: String?): ObservableTransformer<T, Permission> {
        return ObservableTransformer { o ->
            request(o, *permissions as Array<out String>)
                .buffer(permissions.size)
                .flatMap<Permission>(Function<List<Permission?>, ObservableSource<Permission>> { permissions ->
                    if (permissions.isEmpty()) {
                        Observable.empty()
                    } else Observable.just(Permission(permissions as List<Permission>))
                })
        }
    }

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    fun request(vararg permissions: String?): Observable<Boolean> {
        return Observable.just(TRIGGER).compose(ensure(*permissions))
    }

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    fun requestEach(vararg permissions: String?): Observable<Permission> {
        return Observable.just(TRIGGER).compose(ensureEach(*permissions))
    }

    /**
     * Request permissions immediately, **must be invoked during initialization phase
     * of your application**.
     */
    fun requestEachCombined(vararg permissions: String?): Observable<Permission> {
        return Observable.just(TRIGGER).compose(ensureEachCombined(*permissions))
    }

    private fun request(
        trigger: Observable<*>,
        vararg permissions: String
    ): Observable<Permission> {
        require(!(permissions == null || permissions.size == 0)) { "RxPermissions.request/requestEach requires at least one input permission" }
        return oneOf(trigger, pending(*permissions))
            .compose { mRxPermissionsFragment!!.get().transformer() }
            .flatMap { requestImplementation(*permissions) }
    }

    private fun pending(vararg permissions: String): Observable<*> {
        for (p in permissions) {
            if (!mRxPermissionsFragment!!.get().containsByPermission(p)) {
                return Observable.empty<Any>()
            }
        }
        return Observable.just(TRIGGER)
    }

    private fun oneOf(trigger: Observable<*>?, pending: Observable<*>): Observable<*> {
        return if (trigger == null) {
            Observable.just(TRIGGER)
        } else Observable.merge(trigger, pending)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestImplementation(vararg permissions: String): Observable<Permission> {
        val list: MutableList<Observable<Permission>?> = ArrayList(permissions.size)
        val unrequestedPermissions: MutableList<String> = ArrayList()

        // In case of multiple permissions, we create an Observable for each of them.
        // At the end, the observables are combined to have a unique response.
        for (permission in permissions) {
            if (isGranted(mRxPermissionsFragment!!.get().activity, permission)) {
                // Already granted, or not Android M
                // Return a granted Permission object.
                list.add(
                    Observable.just(
                        Permission(
                            permission,
                            granted = true,
                            shouldShowRequestPermissionRationale = false
                        )
                    )
                )
                continue
            }
            if (isRevoked(mRxPermissionsFragment!!.get().activity, permission)) {
                // Revoked by a policy, return a denied Permission object.
                list.add(
                    Observable.just(
                        Permission(
                            permission,
                            granted = false,
                            shouldShowRequestPermissionRationale = false
                        )
                    )
                )
                continue
            }
            var subject = mRxPermissionsFragment!!.get().getSubjectByPermission(permission)
            // Create a new subject if not exists
            if (subject == null) {
                unrequestedPermissions.add(permission)
                subject = PublishSubject.create()
                mRxPermissionsFragment!!.get().setSubjectForPermission(permission, subject)
            }
            list.add(subject)
        }
        if (unrequestedPermissions.isNotEmpty()) {
            val unrequestedPermissionsArray = unrequestedPermissions.toTypedArray()
            requestPermissionsFromFragment(unrequestedPermissionsArray)
        }
        return Observable.concat(Observable.fromIterable(list))
    }

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps
     * the returned value in an observable.
     *
     *
     * In case of multiple permissions, only emits true if
     * Activity.shouldShowRequestPermissionRationale returned true for
     * all revoked permissions.
     *
     *
     * You shouldn't call this method if all permissions have been granted.
     *
     *
     * For SDK &lt; 23, the observable will always emit false.
     */
    fun shouldShowRequestPermissionRationale(
        activity: Activity,
        vararg permissions: String?
    ): Observable<Boolean> {
        return if (!isMarshmallow) {
            Observable.just(false)
        } else Observable.just(
            shouldShowRequestPermissionRationaleImplementation(
                activity,
                *permissions as Array<out String>
            )
        )
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun shouldShowRequestPermissionRationaleImplementation(
        activity: Activity,
        vararg permissions: String
    ): Boolean {
        for (p in permissions) {
            if (!isGranted(activity, p) && !activity.shouldShowRequestPermissionRationale(p)) {
                return false
            }
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsFromFragment(permissions: Array<String>?) {
        mRxPermissionsFragment!!.get().requestPermissions(permissions)
    }

    /**
     * Returns true if the permission is already granted.
     *
     *
     * Always true if SDK &lt; 23.
     */
    fun isGranted(activity: Activity?, permission: String?): Boolean {
        return !isMarshmallow || mRxPermissionsFragment!!.get().isGranted(activity, permission)
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     *
     *
     * Always false if SDK &lt; 23.
     */
    fun isRevoked(activity: Activity?, permission: String?): Boolean {
        return isMarshmallow && mRxPermissionsFragment!!.get().isRevoked(activity, permission)
    }

    val isMarshmallow: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray) {
        mRxPermissionsFragment!!.get()
            .onRequestPermissionsResult(permissions, grantResults, BooleanArray(permissions.size))
    }

    fun interface Lazy<V> {
        fun get(): V
    }

    companion object {
        val TAG = RxPermissions::class.java.simpleName
        val TRIGGER = Any()
    }
}