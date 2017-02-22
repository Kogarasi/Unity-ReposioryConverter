package com.kogarasi.RepositoryConverter

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import java.io.File

class Handler {

    class Input{
        var user: String = ""
        var repo: String = ""
        var branch: String = ""
        var root: String = ""

    }

    val downloader: Downloader
    val extractor: Extractor
    val converter: Converter

    init {
        downloader = Downloader()
        extractor = Extractor()
        converter = Converter()
    }

    fun main( input: Input, context: Context): String {

        val filename = "github-" + context.awsRequestId
        val extractPath = "/tmp/$filename"
        val downloadFilePath = downloader.download( input.user, input.repo, input.branch, filename )

        extractor.extract( downloadFilePath, extractPath )

        val convettedFile = converter.convert( extractPath )

        println( "Convertted File:" + convettedFile )
        S3Client().uploadFile( "${filename}.unitypackage", File( convettedFile ) )

        clean( File( downloadFilePath ), File( extractPath ), File( convettedFile ) )

        return "${filename}.unitypackage"
    }

    fun clean( downloadFile: File, extractFile: File, converttedFile: File ){
        downloadFile.delete()
        extractFile.deleteRecursively()
        converttedFile.delete()
    }
}