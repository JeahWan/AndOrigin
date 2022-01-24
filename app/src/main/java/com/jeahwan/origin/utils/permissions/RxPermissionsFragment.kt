package com.jeahwan.origin.utils.permissions

import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.*

class RxPermissionsFragment : Fragment() {
    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private val mSubjects: MutableMap<String?, PublishSubject<Permission>?> = HashMap()
    private val mCreateSubject: Subject<Any> = PublishSubject.create()
    private var mLogging = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mCreateSubject.onNext(RxPermissions.TRIGGER)
        mCreateSubject.onComplete()
    }

    fun transformer(): Observable<Any> {
        return if (isAdded) {
            Observable.just(RxPermissions.TRIGGER)
        } else mCreateSubject
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissions(permissions: Array<String>?) {
        requestPermissions(permissions!!, PERMISSIONS_REQUEST_CODE)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSIONS_REQUEST_CODE) return
        val shouldShowRequestPermissionRationale = BooleanArray(permissions.size)
        for (i in permissions.indices) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(
                permissions[i]
            )
        }
        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale)
    }

    fun onRequestPermissionsResult(
        permissions: Array<String>,
        grantResults: IntArray,
        shouldShowRequestPermissionRationale: BooleanArray
    ) {
        var i = 0
        val size = permissions.size
        while (i < size) {
            // Find the corresponding subject
            val subject = mSubjects[permissions[i]]
            if (subject == null) {
                // No subject found
                Log.e(
                    RxPermissions.TAG,
                    "RxPermissions.onRequestPermissionsResult invoked but didn't find the corresponding permission request."
                )
                return
            }
            mSubjects.remove(permissions[i])
            val granted = grantResults[i] == PackageManager.PERMISSION_GRANTED
            subject.onNext(
                Permission(
                    permissions[i],
                    granted,
                    shouldShowRequestPermissionRationale[i]
                )
            )
            subject.onComplete()
            i++
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun isGranted(activity: Activity?, permission: String?): Boolean {
        return activity != null && activity.checkSelfPermission(permission!!) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun isRevoked(activity: Activity?, permission: String?): Boolean {
        return activity != null && activity.packageManager.isPermissionRevokedByPolicy(
            permission!!,
            activity.packageName
        )
    }

    fun setLogging(logging: Boolean) {
        mLogging = logging
    }

    fun getSubjectByPermission(permission: @NonNull String?): PublishSubject<Permission>? {
        return mSubjects[permission]
    }

    fun containsByPermission(permission: @NonNull String?): Boolean {
        return mSubjects.containsKey(permission)
    }

    fun setSubjectForPermission(
        permission: @NonNull String?,
        subject: @NonNull PublishSubject<Permission>?
    ) {
        mSubjects[permission] = subject
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 42
    }
}