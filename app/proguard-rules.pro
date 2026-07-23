# Keep IME service and related classes
-keep class com.mkpro.keyboard.ime.** { *; }
-keep class com.mkpro.keyboard.core.** { *; }
-keep class com.mkpro.keyboard.ui.** { *; }

# Keep Android framework classes used via reflection
-keepclassmembers class android.view.KeyEvent {
    public static final int KEYCODE_*;
}

# Keep Compose-related classes
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.savedstate.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep data classes
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
