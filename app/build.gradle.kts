plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.jeahwan.origin"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.jeahwan.origin"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        resourceConfigurations.add("zh")
        ndk { abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a")) }
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    //禁止生成依赖元数据 不上play用不到
    dependenciesInfo {
        includeInApk = false
    }

    // 优化编译速度 如果有用到kapt添加如下配置
    kapt {
        useBuildCache = true
        javacOptions {
            option("-Xmaxerrs", 500)
        }
    }

    //签名配置
    signingConfigs {
        create("config") {
            storeFile = file("../xxx.jks")
            storePassword = "xxx"
            keyAlias = "xxx"
            keyPassword = "xxx"
        }
    }
    buildFeatures {
        //开启dataBinding
        dataBinding = true
        //生成buildConfig
        buildConfig = true
//        compose = true
    }
    //jniLibs目录指向libs目录
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }
    buildTypes {
        val debug = getByName("debug") {
            // 签名
//            signingConfig = signingConfigs.getByName("config")
        }
        val alpha = create("alpha") {
            //继承debug配置
            initWith(debug)
            // 混淆
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 移除无用的resource文件
            isShrinkResources = true
        }
        getByName("release") {
            //继承alpha配置
            initWith(alpha)
            //关闭debug
            isJniDebuggable = false
            isDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    lint {
        //关闭lint检查
        abortOnError = false
        disable += "ResourceType"
    }
    // 自定义多渠道打包 apk名称
    applicationVariants.all {
        val buildType = buildType.name
        outputs.all {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                if (buildType == "release") {
//                    outputFileName = "DEMO_v${versionCode}_${flavorName}_${Versions.dateFormat}.apk"
                    outputFileName = "DEMO_v.apk"
                }
            }
        }
    }
}

dependencies {
    //support包统一最新版本
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //material
    implementation("com.google.android.material:material:1.11.0")
    //RxAndroid
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    //Retrofit2
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //网络请求日志打印
    implementation("com.github.ihsanbal:LoggingInterceptor:3.1.0") {
        exclude(group = "org.json", module = "json")
    }
    //抓包
    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")
    //底部tabBar
    implementation("me.majiajie:pager-bottom-tab-strip:2.4.0")
    //lottie
    implementation("com.airbnb.android:lottie:6.2.0")
    //Glide
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.16.0")
    //Indicator
    implementation("com.github.hackware1993:MagicIndicator:1.7.0")
    //shape selector 替代方案
    implementation("com.github.JavaNoober.BackgroundLibrary:libraryx:1.7.6")
    //自动换行布局
    implementation("am.widget:wraplayout:1.2.1")
    //崩溃activity
    implementation("cat.ereza:customactivityoncrash:2.4.0")
    //沉浸式状态栏
    implementation("com.geyifeng.immersionbar:immersionbar:3.2.2")
    implementation("com.geyifeng.immersionbar:immersionbar-ktx:3.2.2")
    //下拉刷新库
    implementation("io.github.scwang90:refresh-layout-kernel:2.1.0")  //核心必须依赖
    implementation("io.github.scwang90:refresh-header-material:2.1.0")  //谷歌刷新头
    implementation("io.github.scwang90:refresh-footer-classics:2.1.0")  //经典加载
    //版本更新
    implementation("com.github.AlexLiuSheng:CheckVersionLib:2.4.2")
    //权限
    kapt("com.github.permissions-dispatcher:permissionsdispatcher-processor:4.9.2")
    implementation("com.github.permissions-dispatcher:permissionsdispatcher:4.9.2")
}