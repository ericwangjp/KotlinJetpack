package com.example.kotlinjetpack.utils

import android.graphics.Bitmap
import java.io.*


/**
 * desc: 数据流处理工具类
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/6/7 7:11 下午
 */
object StreamTool {

    /**
     * 拷贝图片文件
     * @param oldPath 原图片所在路径
     * @param newPath 新图片所在路径
     * @throws IOException
     */
    @Throws(IOException::class)
    fun copy(oldPath: String?, newPath: String?) {
        val inputStream = FileInputStream(oldPath)
        val fileOutputStream = FileOutputStream(newPath)
        val buffer = ByteArray(1024)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            fileOutputStream.write(buffer, 0, len)
        }
        fileOutputStream.flush()
        fileOutputStream.close()
        inputStream.close()
    }

    /**
     * 将byte[]写入指定的文件
     * @param filePath 指定文件的路径
     * @param data byte[]
     * @throws IOException
     */
    @Throws(IOException::class)
    fun write(filePath: String?, data: ByteArray?) {
        val fileOutputStream = FileOutputStream(filePath)
        fileOutputStream.write(data)
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    /**
     * 根据文件路径获取byte[]
     * @param path 文件路径
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun read(path: String?): ByteArray? {
        return read(FileInputStream(path))
    }

    /**
     * 从输入流读取数据
     *
     * @param inStream
     * @return  byte[]
     * @throws IOException
     */
    @Throws(IOException::class)
    fun read(inStream: InputStream): ByteArray? {
        val outSteam = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len: Int
        while (inStream.read(buffer).also { len = it } != -1) {
            outSteam.write(buffer, 0, len)
        }
        outSteam.flush()
        outSteam.close()
        inStream.close()
        return outSteam.toByteArray()
    }

    /**
     * 将Bitmap对象转换成byte[]
     * @param bitmap Bitmap
     * @return byte[]
     * @throws IOException
     */
    @Throws(IOException::class)
    fun read(bitmap: Bitmap): ByteArray? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        baos.flush()
        baos.close()
        return baos.toByteArray()
    }
}