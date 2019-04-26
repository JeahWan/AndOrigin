#and_base
    android基础框架，方便新建项目时直接基于此项目使用

####目录
    *App
        app的入口类

    *Constants
        常量类，用来放至URL等常量信息

    *CrashHandler
        全局异常捕获类

    *JavaScriptInterface
        js与native交互的接口类

    *base
        包含BaseActivity，BaseFragment，BaseHttpMethods等必要组件的基类

    *http
        app的网络层，HttpContract定义网络请求的方法实现，ResultException异常返回的统一处理

        使用方法示例（见requestTest方法）：
        1、HttpContract中分别在Services和Methods接口中增加requestTest方法
        2、HttpMethods中实现1中新增的方法requestTest
        3、调用：HttpMethods.getInstance().requestTest(Subscriber,String)

    *ui
        具体UI展示层，包括所有需要实现的页面效果
		**ViewpagerActivity使用：
			1、Constants中定义一个页面type用来区分页面类型
			2、在viewpagerAct中switch代码块中添加case，add需要的fragment
			3、启动该act时指定type
				putExtra("type",Constants中定义的type)
		**WebViewPageActivity使用：
			1、启动页面时，通过intent传入url
			2、checkParams（）方法检查url中携带的参数信息，控制页面显示
			3、通过WebViewCommonSet工具类，设置js，以及开启webview的缓存和清除缓存

####打包类型说明

    debug：

        开发用（线下）

    alpha：

        测试用（线下）

    release：

        发布用（线上），也可用于测试

    applicationIdSuffix 控制包名后缀，可实现一机安装多版本app;

    src/alpha(debug、main)/res/values/string.xml 中app_name字段设置不同打包类型下显示的app名称

####使用

    1、将新项目仓库clone到本地（空仓库）

        git clone https://git.oschina.net/XXX/XXX.git

    2、clone本项目框架到本地

        git clone https://github.com/MakiseK/and_base

	3、拷贝所有文件到新项目的目录中，并在根目录Git Bash中依次执行以下命令：

	    git add .
	    git commit -m init
	    git push

    4、studio中通过git导入项目并更改包名

        studio中project视图右上齿轮中取消勾选Compact Empty Middle Packages;

        选中com下的base包按shift+f6更改新包名，注意选择下边两个选项（重要！）

        检索完成后点击Do Refactor完成包名更改，Sync后即完成导入。