## 包结构：

##### *api 包含了封装的网络请求框架，基础bean、接口定义等

    - converter 自定义实现了fastjson的解析库


##### *base 包含了activity、fragment等的基类

    - BaseAdapter 通用的adapter 任何地方需要使用直接new一个 调initList、setOnBindViewHolder即可 不用单独创建类；省去了adapter包

    - BaseTabFragment、BaseListFragment 分别是tab样式、list样式的页面封装，有新的页面直接继承实现必要的方法即可；减少样板代码

    - BaseBindingAdapter 利用DatabindingAdapter实现对原生view的属性扩展，如Glide封装，xml中直接指定必要的属性即可，无需重复编写glide加载相关代码

    - BaseDialog 通用的Dialog封装，使用时直接转入layout，binding即可使用，无需创建各种XXDialog


##### *data JavaBean（部分bean中包含了简单的逻辑方法，方便xml直接调用）

    - CollectionBean 集合类型数据通用bean；避免相同字段或者只有一个字段的情况去创建重复的bean


##### *ui app所有的页面，以页面层级划分

    - ContainerActivity fragment的容器activity，非必要的情况（如主页、启动页）下，新页面只需创建fragment然后直接调用此activity即可，无需一个页面一个activity，也避免了庞大的manifest文件

    - CrashActivity 测试环境下崩溃时的展示页面 可查看错误log及重启app，生产环境不会展示此页面，只展示toast


##### *utils 各种工具类

    - crash CrashActivity用到的相关代码

    - RxTask 使用Rxjava封装的线程工具类，方便在任意位置调用io或ui线程

    - WebViewCommonSet WebView常用设置封装

    - ShareUtils 基于友盟封装的一键分享工具类


##### *view 自定义view


##### *App app入口application


##### *Constants 各种常量集合，以内部类形式做分类


##### *CrashHandler 自定义的异常捕获，线上异常时弹toast


##### *JavaScriptInterface js交互类



## 依赖说明：

    *support库 使用androidx

    *fastjson json解析库

    *RxJava RxAndroid 使用在网络请求框架封装、启动页的倒计时、RxTask工具类等

    *Retrofit2 网络请求库

    *LoggingInterceptor 网络请求日志打印

    *umeng四件套 保持最新版本

    *material-dialogs 弹窗库，只能使用到0.9.6.0版本 高版本用kotlin重构了 与项目不兼容

    *pager-bottom-tab-strip 首页底部tabbar

    *systembartint 沉浸式状态栏

    *glide 图片加载

    *MagicIndicator tab样式页面使用

    *background shape selector 替代方案，避免创建大量的shape xml

    *wraplayout 自动换行布局 使用在首页的产品item标签上

    *SmartRefreshLayout 下拉刷新、上拉加载库

    *versionchecklib 版本更新库

    *rxpermissions 运行时权限处理

    *Android-PickerView 地区选择器



## plugin及task说明：

1、me.isming.fir plugin

> 自动上传至fir的插件

2.、uploadAlphaToFir Task

> 结合了打包与上传两步操作 执行此task可直接打测试包并自动上传至fir



## 其他说明：

> 1、App全局响应了摇一摇切换环境

> 2、资源文件使用tinyPng压缩后转webp，保持最佳质量与大小

## 使用

1、将新项目仓库clone到本地（空仓库）

> git clone https://XXX/XXX.git

2、clone本项目框架到本地

> git clone https://github.com/JeahWan/MK-BasicFrame

3、拷贝所有文件到新项目的目录中，并在根目录Git Bash中依次执行以下命令：

> git add .
> git commit -m init
> git push

4、studio中通过git导入项目并更改包名

> studio中project视图右上齿轮中取消勾选Compact Empty Middle Packages;

> 选中com下的base包按shift+f6更改新包名，注意选择下边两个选项（重要！）

> 检索完成后点击Do Refactor完成包名更改，Sync后即完成导入。
