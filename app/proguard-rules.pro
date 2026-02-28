# Add project specific ProGuard rules here.
# You can find general rules for popular libraries at
# https://github.com/square/proguard-rules/tree/master/rules

# Keep all data classes (used by Room and Gson)
-keep class * extends kotlin.coroutines.jvm.internal.SuspendLambda
-keep class com.chiefdenis.carsart.data.database.** { *; }
-keep class com.chiefdenis.carsart.data.model.** { *; }
-keep class com.chiefdenis.carsart.domain.** { *; }

# Hilt
-keep class * implements dagger.hilt.internal.GeneratedComponent
-keep class * implements dagger.hilt.internal.GeneratedComponentManager
-keep class * implements dagger.hilt.internal.GeneratedComponentManagerHolder
-keep class dagger.hilt.internal.processedrootsentinel.codegen.** { *; }

# SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# Keep custom views and composables
-keepclasseswithmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
