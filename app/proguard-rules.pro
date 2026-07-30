# Add project specific ProGuard rules here.

# --- Kept for when isMinifyEnabled is turned back on ---
# InputMethodService and Application subclasses are only referenced from
# AndroidManifest.xml (by string name), so R8 can't see they're entry
# points and may strip/rename members R8-unsafely without these rules.
-keep class com.mkpro.keyboard.ime.KeyboardService { *; }
-keep class com.mkpro.keyboard.MkProApplication { *; }
-keep class com.mkpro.keyboard.MainActivity { *; }

# Compose runtime + Kotlin metadata (needed for ComposeView hosted outside
# an Activity, e.g. inside our IME).
-keep class androidx.compose.runtime.** { *; }
-keepclassmembers class kotlin.Metadata { *; }
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions

# Kotlin coroutines (state machines depend on unstripped debug metadata).
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler
-dontwarn kotlinx.coroutines.**

# AndroidX DataStore (SettingsRepository) uses protobuf-lite reflection.
-keep class androidx.datastore.*.** { *; }

# Our own model/data classes - kept whole so no field is silently removed
# (Kotlin data class equals/copy plus KeyModel used across Compose + reflection-free logic).
-keep class com.mkpro.keyboard.core.** { *; }
-keep class com.mkpro.keyboard.domain.** { *; }
