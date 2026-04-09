package com.example.demo.db.world

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSLog
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringByAppendingPathComponent

actual class WorldDatabaseFileManager {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun deleteDatabase(id: Long) {
        val docsDir = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).firstOrNull() as? NSString
            ?: error("Could not resolve iOS Documents directory")

        val fm = NSFileManager.defaultManager

        val fileName = docsDir.stringByAppendingPathComponent("save_${id}.db")
        val wal = "$fileName-wal"
        val shm = "$fileName-shm"

        deleteIfExists(fm, fileName)
        deleteIfExists(fm, wal)
        deleteIfExists(fm, shm)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun deleteIfExists(fileManager: NSFileManager, path: String) {
        if (fileManager.fileExistsAtPath(path)) {
            memScoped {
                val removed = fileManager.removeItemAtPath(path, null)
                if (!removed) {
                    error("Could not remove file at path: $path")
                }
            }
        }
    }
}