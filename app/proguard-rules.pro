# 디버깅을 위한 소스 라인 정보 유지
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room Database 관련 규칙
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep class * implements androidx.room.Dao { *; }

# Kotlin Serialization 규칙
-keepattributes *Annotation*
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# Gson 규칙
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Coil 이미지 로딩 라이브러리 규칙
-keep class coil.** { *; }
-keep interface coil.** { *; }

# 코루틴 관련 규칙
-keep class kotlinx.coroutines.** { *; }
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }

# 초성마켓 데이터 모델 및 비즈니스 로직 보존
-keep class com.kwon.chosungmarket.data.db.** { *; }
-keep class com.kwon.chosungmarket.domain.model.** { *; }
-keep class com.kwon.chosungmarket.domain.usecase.** { *; }

# Compose 관련 규칙
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }

# Koin DI 규칙
-keep class org.koin.** { *; }
-keep interface org.koin.** { *; }

# 일반적인 안드로이드 규칙
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# 열거형 보존
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Parcelable 구현체 보존
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Serializable 구현체 보존
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 모든 클래스 이름 보존
-keepnames class **