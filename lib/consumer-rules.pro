# ===========================================
# WXAPK 库的 consumer rules（会被打进宿主的 AAR 依赖里自动生效）
#
# 关键点：宿主 release 通常开了 R8。下面这些类是系统按「清单里的类名」或
# 「provide 约定」反射实例化的，被混淆就 ClassNotFoundException，表现为
# 点开 apk.1 后什么都不发生。
# ===========================================

# FileProvider 子类：系统按 manifest 的 android:name 反射创建
-keep class com.twiceyuan.wxapk.AppFileProvider { *; }

# 保险：所有 FileProvider 子类都别动
-keep class * extends androidx.core.content.FileProvider { *; }

# 清单里声明的 Activity（AGP 通常会自动 keep，这里显式写一遍防宿主规则误伤）
-keep class com.twiceyuan.wxapk.InstallerActivity { *; }
-keep class com.twiceyuan.wxapk.FileBrowserActivity { *; }
-keep class com.twiceyuan.wxapk.PermissionHandlerActivity { *; }

# 库里用到的公共扩展
-keep class com.twiceyuan.wxapk.InstallProcess { *; }

# 反射/清单里出现过的资源 id 常量不需要额外 keep；
# 如有 -dontwarn 需求由宿主自行追加
