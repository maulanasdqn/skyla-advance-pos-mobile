# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.reflect.jvm.internal.**

# Kotlinx Serialization
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.skyla.pos.**$$serializer { *; }
-keepclassmembers class com.skyla.pos.** {
    *** Companion;
}
-keepclasseswithmembers class com.skyla.pos.** {
    kotlinx.serialization.KSerializer serializer(...);
}
