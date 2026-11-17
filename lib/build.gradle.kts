plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.twiceyuan.wxapk"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        // 取最低值，保证能被 minSdk 24/26 之类的宿主直接依赖
        minSdk = libs.versions.minSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    // AppFileProvider 继承 androidx.core.content.FileProvider，属于公开 API，用 api 暴露给宿主
    api(libs.androidx.core)
    implementation(libs.androidx.documentfile)
}
