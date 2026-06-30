package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object AppUpdater {

    private val client = OkHttpClient()

    // Default GitHub repository path (user can change this or set it dynamically)
    private const val GITHUB_REPO = "cihanozel10/okul-paylasim"
    private const val LATEST_RELEASE_URL = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"

    sealed class UpdateCheckResult {
        data class UpdateAvailable(val latestVersion: String, val downloadUrl: String, val changelog: String) : UpdateCheckResult()
        object NoUpdate : UpdateCheckResult()
        data class Error(val message: String) : UpdateCheckResult()
    }

    /**
     * Checks if there is a new release on GitHub.
     * Falls back to showing diagnostic details if the repo doesn't exist yet on GitHub.
     */
    suspend fun checkForUpdates(currentVersion: String): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(LATEST_RELEASE_URL)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "OkulPaylasim-App")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    if (response.code == 404) {
                        return@withContext UpdateCheckResult.Error(
                            "GitHub deposu henüz oluşturulmamış veya ilk sürüm yayınlanmamış.\n" +
                            "Repo: https://github.com/$GITHUB_REPO"
                        )
                    }
                    return@withContext UpdateCheckResult.Error("Hata Kodu: ${response.code}")
                }

                val bodyString = response.body?.string() ?: return@withContext UpdateCheckResult.Error("Yanıt boş.")
                val json = JSONObject(bodyString)
                val tagName = json.getString("tag_name").replace("v", "").trim()
                val body = json.optString("body", "Yeni özellikler eklendi.")

                val assets = json.getJSONArray("assets")
                var downloadUrl = ""
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    if (asset.getString("name").endsWith(".apk")) {
                        downloadUrl = asset.getString("browser_download_url")
                        break
                    }
                }

                if (downloadUrl.isEmpty()) {
                    return@withContext UpdateCheckResult.Error("Sürümde yüklenebilir APK dosyası bulunamadı.")
                }

                // Simple version comparison (e.g. "1.1" vs "1.0")
                if (isNewerVersion(tagName, currentVersion)) {
                    UpdateCheckResult.UpdateAvailable(tagName, downloadUrl, body)
                } else {
                    UpdateCheckResult.NoUpdate
                }
            }
        } catch (e: Exception) {
            UpdateCheckResult.Error("Bağlantı hatası: ${e.localizedMessage}")
        }
    }

    private fun isNewerVersion(newVer: String, currentVer: String): Boolean {
        return try {
            val newParts = newVer.split(".").map { it.toIntOrNull() ?: 0 }
            val curParts = currentVer.split(".").map { it.toIntOrNull() ?: 0 }
            val length = maxOf(newParts.size, curParts.size)
            for (i in 0 until length) {
                val newPart = if (i < newParts.size) newParts[i] else 0
                val curPart = if (i < curParts.size) curParts[i] else 0
                if (newPart > curPart) return true
                if (newPart < curPart) return false
            }
            false
        } catch (e: Exception) {
            newVer != currentVer
        }
    }

    /**
     * Downloads the APK file from [apkUrl] and saves it locally in the cache folder.
     * Reports progress back to [onProgress].
     */
    suspend fun downloadApk(
        context: Context,
        apkUrl: String,
        onProgress: (Int) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        try {
            val url = URL(apkUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext null
            }

            val fileLength = connection.contentLength
            val input: InputStream = connection.inputStream
            val outputFile = File(context.cacheDir, "okul_paylasim_update.apk")
            
            // Clean old update file if exists
            if (outputFile.exists()) {
                outputFile.delete()
            }

            val output = FileOutputStream(outputFile)
            val data = ByteArray(4096)
            var total: Long = 0
            var count: Int
            while (input.read(data).also { count = it } != -1) {
                total += count
                if (fileLength > 0) {
                    val progress = ((total * 100) / fileLength).toInt()
                    onProgress(progress)
                }
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()

            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Triggers the package installer for the downloaded APK.
     */
    fun installApk(context: Context, apkFile: File) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val apkUri: Uri = FileProvider.getUriForFile(context, authority, apkFile)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Cleans up any cached APK files.
     * Call this on app startup.
     */
    fun cleanUpCachedApks(context: Context) {
        try {
            context.cacheDir?.listFiles()?.forEach { file ->
                if (file.name.endsWith(".apk")) {
                    file.delete()
                }
            }
            context.getExternalCacheDir()?.listFiles()?.forEach { file ->
                if (file.name.endsWith(".apk")) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
