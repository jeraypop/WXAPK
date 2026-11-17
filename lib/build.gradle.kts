import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    // JitPack 构建时会执行 :lib:publishToMavenLocal，这里必须声明发布插件
    `maven-publish`
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

    // 告诉 AGP 哪些 variant 要暴露成一个可发布的软件组件。
    // 注意：Kotlin DSL 里字符串用双引号，写成 'release' 会报
    // "Too many characters in a character literal"。
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// components["release"] 要等 AGP 配置完才存在，所以放进 afterEvaluate
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                // JitPack 会把它换成 com.github.jeraypop.WXAPK:lib:<tag>，
                // 这三个值只在本地 publishToMavenLocal 时起作用
                groupId = "com.github.jeraypop"
                artifactId = "lib"
                version = "1.0.0"
            }
        }
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    // AppFileProvider 继承 androidx.core.content.FileProvider，属于公开 API，用 api 暴露给宿主
    api(libs.androidx.core)
    implementation(libs.androidx.documentfile)
}
