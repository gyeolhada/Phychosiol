package com.example.phychosiolz.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.util.Calendar

/***
 * 文件工具类
 * 用于获取文件路径
 */
object FileUtil {
    //文件根目录
    private val ROOT = (Environment.getExternalStorageDirectory().absolutePath + "/PhychosiolZ/")
    const val QUESTIONNAIRE = "QUESTIONNAIRE/"

    fun removeFile(path: String?): Boolean {
        val file = File(path)
        return if (file.exists()) {
            file.delete()
        } else false
    }

    fun removeAllFileInDir(path: String?) {
        val file = File(path)
        if (file.exists()) {
            val files = file.listFiles()
            if (files != null) for (f in files) {
                f.delete()
            }
        }
    }

    /***
     * 获取文件夹下所有文件的路径
     * @param dirPath 文件夹路径
     * @return 文件路径列表
     */
    fun getPathsListInDir(dirPath: String?): List<String> {
        val pathsList: MutableList<String> = ArrayList()
        val dir = File(dirPath)
        if (dir.exists()) {
            val files = dir.listFiles()
            if (files != null) for (file in files) {
                pathsList.add(file.absolutePath)
            }
        }
        return pathsList
    }

    /***
     * 获取该用户的录音文件夹路径
     */
    fun getUserPath(uid: Int, type: String): String {
        val rs = ROOT + type + uid + "/"
        //if dir not exist
        val file = File(rs)
        if (!file.exists()) {
            file.mkdirs()
        }
        return rs
    }

    /***
     * 分享文件
     */
    fun shareFile(context: Context, path: String?) {
        val file = File(path)
        val share = Intent(Intent.ACTION_SEND)
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        share.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(context, "com.example.smartearphone.fileprovider", file)
        )
        share.setType("*/*") //此处可发送多种文件
        context.startActivity(Intent.createChooser(share, "发送"))
    }

    /***
     * 检测文件是否存在
     */
    fun isFileNotExist(path: String?): Boolean {
        if (path == null) {
            return true
        }
        val file = File(path)
        return !file.exists()
    }

    /***
     * @param uid 用户id,使用用户id作为文件夹名，因为用户名可能会改变
     * @param calendar 日期 用于创建文件夹：年：月：日
     * @return 文件路径
     * !!!注意:月份从0开始
     * 例：2020年5月1日
     * 路径示范：/storage/emulated/0/2021-03-12/18:30BIG-5.txt
     * 文件的内部：HeartRateForFile的json字符串
     */
    fun getQuestionnairSavedPath(
        uid: Int, questionnairType: String
    ): String {
        val calendar = Calendar.getInstance()
        return try {
            var path = ROOT + QUESTIONNAIRE
            ensureDir(path)
            path += "$uid/"
            ensureDir(path)
            path += ((calendar[Calendar.YEAR].toString() + "-"
                    + (if (calendar[Calendar.MONTH] < 9) "0" else "") +
                    (calendar[Calendar.MONTH] + 1).toString() + "-" +
                    (if (calendar[Calendar.DAY_OF_MONTH] < 10) "0" else "") + calendar[Calendar.DAY_OF_MONTH]))
            ensureDir(path)
            //18:30BIG-5.txt
            var leafName =
                (if (calendar[Calendar.HOUR_OF_DAY] < 10) "0" else "") + calendar[Calendar.HOUR_OF_DAY] + "-" +
                        (if (calendar[Calendar.MINUTE] < 10) "0" else "") + calendar[Calendar.MINUTE] + questionnairType + ".txt"
            Log.i("FileUtil", "getQuestionnairSavedPath: $path/$leafName")
            path += ("/$leafName")
            ensureFile(path)
            path
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /*-------读取---------*/
    /***
     * 读取某个用户 ID 的所有时间的问卷
     * @param uid 用户 ID
     * @return 文件路径列表
     */
    fun getAllQuestionnairesByUserId(uid: String): List<String> {
        var path = ROOT + QUESTIONNAIRE
        ensureDir(path)
        path += "$uid/"
        return getDirectoryPaths(path)
    }
    /***
     * 读取某个用户 ID 的某一天的所有文件
     * @param uid 用户 ID
     * @param string 日期
     * @return 文件路径列表
     */
    fun getAllFilesByUserIdAndDay(uid: Int, calendar: String): List<String> {
        val path = "$ROOT$QUESTIONNAIRE$uid/$calendar/"
        return getFilePaths(path)
    }
    fun getFilePaths(path: String): List<String> {
        val fileList = mutableListOf<String>()
        val directory = File(path)
        if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                if (file.isFile && file.extension == "txt") {
                    fileList.add(file.path)
                }
            }
        }
        return fileList
    }
    fun getDirectoryPaths(path: String): List<String> {
        val fileList = mutableListOf<String>()
        val directory = File(path)
        if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    fileList.add(file.path)
                }
            }
        }
        return fileList
    }
    /***
     * 读取文本文件内容
     * @param filePath 文件路径
     * @return 文件内容的字符串，如果读取失败，则返回空字符串
     */
    fun readTextFile(filePath: String): String {
        val file = File(filePath)
        if (file.exists() && file.isFile) {
            try {
                return file.readText()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }
    /*-------读取---------*/

    /***
     * 确保文件夹存在
     * @param path 文件夹路径
     * @throws Exception 文件夹不存在，创建失败
     */
    @Throws(Exception::class)
    private fun ensureDir(path: String) {
        if (isFileNotExist(path)) File(path).mkdirs()
    }

    /***
     * 确保文件夹存在
     * @param path 文件夹路径
     * @throws Exception 文件夹不存在，创建失败
     */
    @Throws(Exception::class)
    private fun ensureFile(path: String) {
        if (isFileNotExist(path))
            File(path).createNewFile()
    }

    /***
     * 根据日期获取文件夹下的文件，返回名称list
     */
    fun getNameOfListByCalendar(uid: Int, type: String, calendar: Calendar): List<String> {
        val list: MutableList<String> = ArrayList()
        var path = ROOT + type + uid
        if (isFileNotExist(path)) return list
        path += "/" + calendar[Calendar.YEAR]
        if (isFileNotExist(path)) return list
        path += "/" + calendar[Calendar.MONTH]
        if (isFileNotExist(path)) return list
        path += "/" + calendar[Calendar.DAY_OF_MONTH]
        if (isFileNotExist(path)) return list
        val file = File(path)
        val files = file.listFiles() ?: return list
        for (f in files) { //日级文件夹，判断里面是否有文件
            if (f != null && f.exists()) list.add(f.getName())
        }
        return list
    }


    /***
     * delete file
     */
    fun deleteFile(path: String?) {
        val file = File(path)
        if (file.exists()) file.delete()
    }
}
