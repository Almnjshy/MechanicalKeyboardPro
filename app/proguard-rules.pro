# ============================================
# KEEP ALL APPLICATION CLASSES
# ============================================
-keep class com.mkpro.keyboard.** { *; }

# ============================================
# KEEP IME SERVICE
# ============================================
-keep class * extends android.inputmethodservice.InputMethodService { *; }
-keep class * extends android.app.Service { *; }

# ============================================
# KEEP ANDROID FRAMEWORK (reflection)
# ============================================
-keepclassmembers class android.view.KeyEvent {
    public static final int KEYCODE_*;
}

# ============================================
# KEEP COMPOSE
# ============================================
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.material.** { *; }
-keep class androidx.compose.animation.** { *; }
-keep class androidx.compose.foundation.** { *; }

# ============================================
# KEEP LIFECYCLE
# ============================================
-keep class androidx.lifecycle.** { *; }
-keep class androidx.savedstate.** { *; }
-keep class androidx.activity.** { *; }
-keep class androidx.fragment.** { *; }

# ============================================
# KEEP KOTLIN
# ============================================
-keep class kotlin.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.coroutines.android.** { *; }

# ============================================
# KEEP METADATA
# ============================================
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes SourceFile
-keepattributes LineNumberTable

# ============================================
# KEEP ENUMS
# ============================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================
# KEEP DATA CLASSES
# ============================================
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================
# DON'T WARN
# ============================================
-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn org.jetbrains.kotlin.**
