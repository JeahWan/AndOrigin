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

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepattributes Signature
-keepattributes InnerClasses

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