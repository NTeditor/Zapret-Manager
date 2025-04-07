package ru.nteditor.zapretkotlin

import java.io.File

class IsShell() {

    private fun isSUFile(): Boolean {
        fun isFile(): Boolean {
            return File("/system/bin/su").exists() ||
                    File("/system/xbin/su").exists() ||
                    File("/system/sbin/su").exists() ||
                    File("/bin/su").exists() ||
                    File("/xbin/su").exists() ||
                    File("/sbin/su").exists()
        }
        if (!isFile()) {
            return false
        }
        val isPermission = Shell(listOf("su", "-s", "ls")).start()
        return isPermission != ""
    }

    private fun isZapretFile(): Boolean {
        return File("/system/bin/zapret").exists()
    }

    fun isZapret(): Boolean {
        return isZapretFile()
    }

    fun isRoot(): Boolean {
        return isSUFile()
    }
}