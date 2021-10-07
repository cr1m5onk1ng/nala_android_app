package com.example.nala.services.ocr

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception


class TesseractOCR(
    private val context: Context,
    private val language: String,
) {

    companion object {
        const val TAG = "TESSERACT_DEBUG"
    }

    private var tessModel: TessBaseAPI? = null

    fun init(fromAssets: Boolean = true) {
        tessModel = TessBaseAPI()
        if(fromAssets) loadModelFromAsset()
        else loadModelFromInternalStorage()
    }

    fun recognize(bitmap: Bitmap) : String {
        tessModel!!.setImage(bitmap)
        return tessModel!!.utF8Text
    }

    fun isInitialized() : Boolean {
        return tessModel != null
    }

    fun close() {
        tessModel?.recycle()
    }

    private fun loadModelFromInternalStorage(dataPath: String = "/tesseract/tessdata/", fileName: String = "jpn.traineddata") {
        val dstInitPathDir: String = context.filesDir.toString() + dataPath + fileName
        val success = tessModel!!.init(dstInitPathDir, language)
        if(!success) {
            throw IOException("Couldn't initialize tesseract model")
        }
    }

    /**
     * Copies the language weights for tesseract
     * from the asset folder to the tessdata folder
     * useful in case the data in stored in the assets foilder
     */
    private fun loadModelFromAsset(
        dataPath: String = "/tesseract/tessdata/",
        srcFile: String = "jpn.traineddata"
    ) {
        var fileExists = false

        val assetManager: AssetManager = context.assets

        val dstPathDir = context.filesDir.toString() + dataPath
        val tessPathDir: String = context.filesDir.toString() + "/tesseract"
        val langDataPath = dstPathDir + srcFile
        var inFile: InputStream? = null
        var outFile: FileOutputStream? = null

        try {
            val langFileInTessFolder = File(langDataPath)
            if(langFileInTessFolder.exists()) {
                tessModel!!.init(tessPathDir, language)
                return
            }
            inFile = assetManager.open(srcFile)
            val f = File(dstPathDir)
            if (!f.exists()) {
                if (!f.mkdirs()) {
                    Log.d(TAG,"$srcFile can't be created.")
                }
                outFile = FileOutputStream(langFileInTessFolder)
            } else {
                fileExists = true
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message!!)
        } finally {
            if (fileExists) {
                try {
                    inFile?.close()
                    tessModel!!.init(tessPathDir, language)
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message!!)
                }
            }
            if (inFile != null && outFile != null) {
                try {
                    //copy file
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inFile.read(buf).also { len = it } != -1) {
                        outFile.write(buf, 0, len)
                    }
                    inFile.close()
                    outFile.close()
                    tessModel!!.init(tessPathDir, language)
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message!!)
                }
            } else {
                Log.d( TAG, "$srcFile can't be read.")
            }
        }
    }

}