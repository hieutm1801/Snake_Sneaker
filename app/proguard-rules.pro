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

#------------------- stripe ----------------------------#

# Rules for IconTextInputLayout
-keep class com.google.android.material.textfield.TextInputLayout { *; }
-keep class com.google.android.material.internal.CollapsingTextHelper { *; }

# Rules for BouncyCastle
-keep class org.bouncycastle.jcajce.provider.** { *; }
-keep class org.bouncycastle.jce.provider.** { *; }

-keep class com.stripe.android.** { *; }
-dontwarn com.stripe.android.view.**


# Rules for Kotlin Coroutines
# https://github.com/Kotlin/kotlinx.coroutines/blob/master/ui/kotlinx-coroutines-android/example-app/app/proguard-rules.pro
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

#------------------- stripe ----------------------------#