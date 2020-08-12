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

# keep statements for all needed API classes
-keep class com.yieldlab.yieldprobe.Yieldprobe
-keepclassmembers class com.yieldlab.yieldprobe.Yieldprobe {
    public <methods>;
    public <fields>;
}
# keep data classes
-keep class com.yieldlab.yieldprobe.data.Bid { *;}
-keep class com.yieldlab.yieldprobe.data.Configuration { *;}
-keep class com.yieldlab.yieldprobe.data.DeviceMetaData { *;}
# keep events
-keep class com.yieldlab.yieldprobe.events.EventProbeFailure { *;}
-keep class com.yieldlab.yieldprobe.events.EventProbeSuccess { *;}
-keep class com.yieldlab.yieldprobe.events.EventProbeLog { *;}
# keep all exceptions
-keep class com.yieldlab.yieldprobe.exception.** { *;}
# keep needed google services classes
# See here: https://developers.google.com/android/guides/setup#Proguard
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
