package com.kogarasi.RepositoryConverter

import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.ArchiverFactory
import java.io.File

class Extractor {

    val archiver = ArchiverFactory.createArchiver( ArchiveFormat.ZIP )

    fun extract( archiveFilePath: String, destnationPath: String ){
        archiver.extract( File( archiveFilePath ), File( destnationPath) )
    }
}