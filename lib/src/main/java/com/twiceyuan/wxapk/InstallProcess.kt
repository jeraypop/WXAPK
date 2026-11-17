package com.twiceyuan.wxapk

import android.app.Application
import android.content.Context
import android.os.Build
import java.io.File

/**
 * 安装器进程判断工具。
 *
 * ### 为什么需要它
 * [InstallerActivity] 建议声明在独立进程（manifest 里 `android:process=":service"`），
 * 这样「从微信/QQ 点开 apk.1」只加载安装器本身，秒开、装完进程即退、不留痕迹。
 *
 * 但独立进程启动时**同样会执行宿主的 Application.onCreate**。如果宿主在那里做了完整业务
 * 初始化（数据库、广告 SDK、无障碍/UIA 引擎等），该进程会异常缓慢甚至直接崩溃
 * （典型：初始化 XpqAcc 时读不到已经 init 过的 Context → ExceptionInInitializerError）。
 *
 * ### 宿主用法
 * 在 `MyApplication.onCreate` 里尽量早地加一行：
 * ```kotlin
 * if (InstallProcess.isInstaller(this)) return
 * ```
 * 注意要放在必要的静态字段赋值（如 `instance = this`）之后，避免安装器里的 Toast 之类拿不到 Context。
 */
object InstallProcess {

    /** 默认的安装器进程后缀，与 manifest 里 `android:process=":service"` 对应。 */
    const val INSTALLER_PROCESS_SUFFIX: String = ":service"

    /** 当前进程是不是安装器进程（进程名 == 包名 + [INSTALLER_PROCESS_SUFFIX]）。 */
    @JvmStatic
    fun isInstaller(context: Context): Boolean = isInstaller(context, INSTALLER_PROCESS_SUFFIX)

    /**
     * 当前进程是不是安装器进程（自定义后缀）。
     *
     * 宿主给 InstallerActivity 配了别的进程名时用这个重载，例如
     * `android:process=":apk_installer"` → `isInstaller(this, ":apk_installer")`。
     */
    @JvmStatic
    fun isInstaller(context: Context, suffix: String): Boolean {
        val name = currentProcessName() ?: return false
        return name == context.packageName + suffix
    }

    /** 当前进程名；API 28+ 走官方 API，更低版本读 /proc/self/cmdline。 */
    @JvmStatic
    fun currentProcessName(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName()
        }
        return runCatching {
            File("/proc/self/cmdline").readText().trim('\u0000')
        }.getOrNull()
    }
}
