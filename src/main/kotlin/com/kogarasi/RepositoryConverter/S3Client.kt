package com.kogarasi.RepositoryConverter

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import java.io.File

class S3Client( val bucket: String = "rc.kogarasi.com" ) {

    val client: AmazonS3

    init {
        client = AmazonS3ClientBuilder.defaultClient()
    }

    fun uploadFile( key: String, file: File ){


        val meta  = ObjectMetadata()
        meta.contentType = "application/force-download"
        val obj = client.putObject( bucket, key, file.inputStream(), meta )

        val acl = client.getObjectAcl( bucket, key )
        acl.grantPermission( GroupGrantee.AllUsers, Permission.Read )
        client.setObjectAcl( bucket, key, acl )
    }
}