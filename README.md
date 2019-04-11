## 简单易用，低耦合的权限请求库

<li>前言：本库是学习aspectj的练手项目，对权限请求充分解耦。<br>
<font color='#ff5454'><strong>注：本库底层权限请求是基于rxpermission2的基础上进行开发的</strong></font>
<li>功能：

            1，支持多个权限按组请求
            2，支持对权限设置是否必须，如果非必须及时未授权，会当成请求成功

<li>配置：
 project根目录的build.gradle加入下列配置


    buildscript {
        dependencies {
            classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.+'
        }
    }

在app的build.gradle加入插件

    apply plugin: 'android-aspectjx'

最好同时配置下aspectjx的配置，可以加快构建速度

    aspectjx {
    //排除所有package路径中包含`android.support`的class文件及库（jar文件）
        exclude 'android.support', 'android.arch'
    //    ajcArgs '-Xlint:warning'
    }

<li>使用：
 1，普通的请求方式,这种方式方法的第一个参数必须说FragmentActivity或者v4.Fragment或者是View(但是view.context需是FragmentActivity)

        @NeedPermission(permissions = {
               Manifest.permission.CAMERA,
               Manifest.permission.RECORD_AUDIO,
       })
       public void testPermission1(Activity activity) {
           Toast.makeText(this, "权限申请成功", Toast.LENGTH_SHORT).show();
       }

 2，匿名内部类的请求方式，场景是点击事件回调上直接添加权限请求：

     findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

                 @NeedPermission(permissions = {
                         Manifest.permission.CAMERA,
                         Manifest.permission.RECORD_AUDIO,
                 })
                 @Override
                 public void onClick(View v) {
                     testPermission2();
                 }
             });

 3，针对不同权限的必要性分别请求，场景是：比如对camare的权限很依赖，但是对phone_state的权限可有可无，则可以采用如下方式。（设置necessary=false,即表示当前权限不是必须的，即使拒绝授权也不影响程序执行，默认为true）

      @NeedPermissions({
                 @NeedPermission(permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}),
                 @NeedPermission(permissions = Manifest.permission.READ_PHONE_STATE, necessary = false)
         })
         public void testPermission3(Activity activity) {
             Toast.makeText(activity, "权限申请成功", Toast.LENGTH_SHORT).show();
         }

<li>最后    有问题的话欢迎讨论！！！

