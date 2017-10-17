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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

  -dontwarn okio.**
  -dontwarn javax.annotation.**
  -dontwarn javax.annotation.Nullable
  -dontwarn javax.annotation.ParametersAreNonnullByDefault
  -keepattributes Signature
  -dontwarn retrofit2.Platform$Java8
  -dontwarn retrofit.**
  -keep class retrofit.** { *; }
#删除文件日志
-printusage unused.txt
#混淆前后映射
-printmapping mapping.txt
-dontwarn android.support.multidex.**
-keep class android.support.multidex.**{*;}
  -keep class android.support.v4.** { *; }
  -keep interface android.support.v4.** { *; }
  # Keep the support library
  -keep class android.support.v7.** { *; }
  -keep interface android.support.v7.** { *; }
  -keep interface android.support.design.** { *; }

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep public class * extends android.view.View

  #【保护指定的类文件和类的成员】
#  -keep class * implements android.os.Parcelable {
#      public *;
#  }
  -keepclassmembers class * implements android.os.Parcelable {
       private void writeObject(java.io.ObjectOutputStream);
       private void readObject(java.io.ObjectInputStream);
       java.lang.Object writeReplace();
       java.lang.Object readResolve();
         public static final android.os.Parcelable$Creator *;
  }
  -keepclassmembers class **.R$* {
      public static <fields>;
  }
 -keepnames class com.yb.btcinfo.repository.entity.* implements java.io.Serializable{*;}
 -keep public class * implements java.io.Serializable {
    public *;
 }
 -dontwarn java.lang.invoke.*
 -keep class com.chad.library.adapter.** {
 *;
 }
 -keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
 -keep public class * extends com.chad.library.adapter.base.BaseViewHolder
 -keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder {
      <init>(android.view.View);
 }
# 华为推送

-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

-keep class com.huawei.hms.**{*;}