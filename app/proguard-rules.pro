# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 指定代码的压缩级别
-optimizationpasses 5
# 混淆时不会产生形形色色的类名
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库类。
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
# 不预校验
-dontpreverify
# 混淆时是否记录日志
-verbose
# 忽略警告
-ignorewarnings
# 抛出异常时保留代码行号
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
# 优化
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#四大组件不建议混淆
-keep class com.google.**
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepattributes Signature
-keepattributes InnerClasses

#######supportv4 混淆start#######
-dontwarn android.support.v4.**
-dontwarn **CompatHoneycomb
-dontwarn **CompatHoneycombMR2
-dontwarn **CompatCreatorHoneycombMR2
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
#######supportv4 混淆end#######

####### RxJava RxAndroid ######
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
####### RxJava RxAndroid ######

#枚举不混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#注解不混淆
-keepattributes *Annotation*

####### Parcelable ######
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
####### Parcelable ######

###### Glide #######
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
###### Glide #######

####### Retrofit2 #######
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
####### Retrofit2 #######

####### Databinding #######
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }
####### Databinding #######