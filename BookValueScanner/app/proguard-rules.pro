# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/lib/android/sdk/tools/proguard/proguard-android-optimize.txt
# You can edit the include path and order by changing the proguardFiles
# attribute in build.gradle.kts

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify a method name to be called from JS.
# -keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public static void method_name(...);
# }

# If you use SnakeYAML for parsing YAML, uncomment the following lines.
# -dontwarn org.yaml.snakeyaml.**

# Add any project specific keep rules here below.

# If you use Kotlin Coroutines, be sure to include the following rules:
# -keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
# -keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
# -keepnames class kotlinx.coroutines.CoroutineExceptionHandlerImpl {}
# -keepnames class kotlinx.coroutines.JobSupport {}
# -keepnames class kotlinx.coroutines.SupervisorJobImpl {}
# -keepnames class kotlinx.coroutines.AbstractCoroutine {}
# -keepnames class kotlinx.coroutines.channels.** { *; }
# -keepnames class kotlinx.coroutines.flow.** { *; }
# -keepnames class kotlinx.coroutines.selects.SelectBuilderImpl {}
