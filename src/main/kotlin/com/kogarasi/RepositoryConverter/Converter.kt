package com.kogarasi.RepositoryConverter

import org.ho.yaml.Yaml
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.ArchiverFactory
import org.rauschig.jarchivelib.CompressionType
import java.io.File
import java.util.*

class Converter {

    class Entity {
        var guid: String? = null
        var path: String? = null
        var asset: File? = null
        var meta: File? = null
    }

    val archiver = ArchiverFactory.createArchiver( ArchiveFormat.TAR, CompressionType.GZIP )
    var entities = HashMap<String, Entity>()

    fun convert( rootPath: String ): String {
        entities.clear()

        File(rootPath).walkTopDown().iterator().forEach {
            if( it.isFile ){
                println( "File:" + it.path )
                if( it.extension == "meta" ){
                    addMetadata( it, rootPath )
                } else {
                    addAsset( it )
                }
            }
        }

        val archiveRoot = File( "${rootPath}-converted/" )
        archiveRoot.mkdir()

        var archiveFileList = ArrayList<File>()

        entities.map { it.value }.forEach {
            println( it.guid!! )
            println( it.asset?.name )
            println( it.meta?.name)

            val assetDirPath = archiveRoot.path + "/" + it.guid
            File( assetDirPath ).mkdir()


            val assetFile = File( assetDirPath + "/asset" )
            val metaFile = File( assetDirPath + "/asset.meta" )
            val pathFile = File( assetDirPath + "/pathname" )

            it.asset?.copyTo( assetFile, true )
            it.meta?.copyTo( metaFile, true )

            pathFile.writeText( it.path!! )

            archiveFileList.add( File(assetDirPath) )
        }

        return archiver.create( "unitypakcage", File( "/tmp/" ), archiveRoot ).path

    }

    fun addMetadata( file: File, rootPath: String ){
        val key = extractAssetPathFromMetadataPath( file.path )

        if( entities[ key ] == null ){
            entities[ key ] = Entity()
        }

        entities[ key ]?.meta = file
        entities[ key ]?.guid = getGUID( file )

        val index = rootPath.length + 1
        val path = key.substring( index )
        entities[ key ]?.path = path
    }

    fun addAsset( file: File ){
        if( entities[ file.path ] == null ){
            entities[ file.path ] = Entity()
        }

        entities[ file.path ]?.asset = file
    }

    fun extractAssetPathFromMetadataPath( path: String ): String{
        val index = path.lastIndexOf(".meta" )
        return path.substring(0, index )
    }

    fun getGUID( metadata: File ): String{
        var yaml = Yaml.load( metadata ) as HashMap<String, Object>
        return yaml[ "guid" ] as String
    }
}