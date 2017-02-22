package com.kogarasi.RepositoryConverter

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.Okio
import java.io.File

class Downloader {

    val extensionType: String = "zip"
    val url: String = "https://github.com/{user}/{repo}/archive/{branch}.{extension}"
    val savedPath: String = "/tmp/{filename}.{extension}"

    val okhttp = OkHttpClient()

    fun download( user: String, repo: String, branch: String, filename: String ): String {
        val modifiedUrl = url.replace( "{user}", user ).replace( "{repo}", repo ).replace( "{branch}", branch ).replace( "{extension}", extensionType )
        val savedPath = this.savedPath.replace( "{filename}", filename ).replace( "{extension}", extensionType )

        val savingFile = File( savedPath )
        Okio.buffer( Okio.sink( savingFile ) ).use { buffer ->
            val response = request(modifiedUrl)
            buffer.writeAll(response.body().source())
        }
        return savedPath
    }

    fun request( url: String ): Response{
        val request = Request.Builder().get().url( url ).build()
        return okhttp.newCall( request ).execute()
    }
}