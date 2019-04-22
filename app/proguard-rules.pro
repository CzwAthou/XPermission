# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-optimizationpasses 5
-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-dontshrink
-dontoptimize

#不混淆使用了 annotation的类
-keepattributes *Annotation*
#不混淆javascript
-keepattributes JavascriptInterface
#不混淆 使用反射机制的类
-keepattributes Signature
#忽略警告
-ignorewarnings

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepattributes *Annotation*

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep class android.view.** { *; }

# ****** 混淆配置 ******
-keepclassmembers class * {
    @com.athou.xpermission.annotation.NeedPermission *;
}
-keepclassmembers class * {
    @com.athou.xpermission.annotation.NeedPermissions *;
}
-keepclassmembers class * {
    @com.athou.xpermission.annotation.PermissionDenied *;
}