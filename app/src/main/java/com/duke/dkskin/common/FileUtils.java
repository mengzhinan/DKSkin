package com.duke.dkskin.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Author: duke
 * DateTime: 2017-05-25 10:31
 * Description: 手机内部、外部存储空间操作 version 2.0 <br/>
 * <p>
 * 1、context.getPackageCodePath():/data/app/your_app.apk
 * 2、context.getPackageResourcePath():/data/app/your_app.apk
 * 3、context.getDatabasePath("dk"):/data/data/<package name>/databases/dk
 * 4、context.getDir("dk", Context.MODE_PRIVATE):/data/data/<package name>/app_dk
 * 5、context.getCacheDir():/data/data/<package name>/cache
 * 6、context.getFilesDir():/data/data/<package name>/files
 * 7、context.getExternalFilesDir("dk"):/storage/sdcard0/Android/data/<package name>/files/dk
 * 8、context.getExternalFilesDir(null):/storage/sdcard0/Android/data/<package name>/files
 * 9、context.getExternalFilesDir():/storage/sdcard0/Android/data/<package name>/files -->"清除数据",随app下载而删除
 * 10、context.getExternalCacheDir():/storage/sdcard0/Android/data/<package name>/cache -->"清除缓存",随app下载而删除
 * <p/>
 * 11、context.getLocalClassName():MainActivity
 * <p/>
 * 12、Environment.getRootDirectory():/system
 * 13、Environment.getDataDirectory():/data
 * 14、Environment.getDownloadCacheDirectory():/cache
 * 15、Environment.getExternalStorageDirectory():/storage/sdcard0
 * 16、Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS):/storage/sdcard0/Download
 */
public class FileUtils {

    public static String getExtensionNameNoPoint(String path) {
        return getExtensionNameNoPoint(newFile(path));
    }

    public static String getExtensionNameNoPoint(File file) {
        if (file == null) {
            return null;
        }
        String name = file.getName();
        if ("".equals(name.trim())) {
            return null;
        }
        if (!name.contains(".")) {
            return null;
        }
        String[] arr = name.split("\\.");
        return arr[arr.length - 1];
    }

    public static File newFile(String path) {
        if (path == null || "".equals(path.trim())) {
            return null;
        }
        return new File(path.trim());
    }

    /**
     * 检查和创建文件
     */
    public static boolean checkAndCreateFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            return false;
        }
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                return false;
            }
        }
        try {
            if (file.createNewFile()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void deleteFiles(String path) {
        deleteFiles(newFile(path));
    }

    /**
     * 删除指定路径下的所有文件
     *
     * @param deleteFile parent文件对象
     */
    public static void deleteFiles(File deleteFile) {
        if (deleteFile == null || !deleteFile.exists()) {
            return;
        }
        if (deleteFile.isFile()) {
            try {
                deleteFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File[] files = deleteFile.listFiles();
            int size = files.length;
            for (File file : files) {
                deleteFiles(file);
            }
            try {
                //delete this empty directory
                deleteFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static long getFileSize(String path) {
        return getFileSize(newFile(path));
    }

    public static long getFileSize(File file) {
        if (file == null || !file.exists()) {
            return -1;
        }
        //数组是引用传递
        long[] arr = {0};
        countFile(file, arr);
        return arr[0];
    }

    private static void countFile(File file, long[] totalArr) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            totalArr[0] += file.length();
        } else {
            File[] files = file.listFiles();
            int size = files.length;
            for (File value : files) {
                countFile(value, totalArr);
            }
        }
    }

    public static boolean renameFile(String oldPath, String newPath) {
        return renameFile(newFile(oldPath), newFile(newPath));
    }

    public static boolean renameFile(File oldFile, File newFile) {
        if (oldFile == null || !oldFile.exists()) {
            return false;
        }
        if (newFile == null) {
            return false;
        }
        if (!checkAndCreateFile(newFile)) {
            return false;
        }
        return oldFile.renameTo(newFile);
    }


    public static boolean saveFile(InputStream inputStream, String savePath) {
        if (inputStream == null) {
            return false;
        }
        File file = newFile(savePath);
        if (file == null) {
            return false;
        }
        if (!checkAndCreateFile(file)) {
            return false;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[8192];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    outputStream = null;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    inputStream = null;
                }
            }
        }
        return false;
    }

    public static boolean saveFile(String data, String filePath) {
        if (data == null || "".equals(data.trim())) {
            return false;
        }
        return saveFile(data.getBytes(), filePath);
    }

    public static boolean saveFile(byte[] data, String filePath) {
        if (data == null || data.length <= 0) {
            return false;
        }
        File file = newFile(filePath);
        if (!checkAndCreateFile(file)) {
            return false;
        }
        if (!file.canWrite()) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    fos = null;
                }
            }
        }
        return false;
    }

    public static boolean copyOnlyFile(File oldFile, File newFile) {
        if (oldFile == null || !oldFile.exists()) {
            return false;
        }
        if (oldFile.isDirectory()) {
            return false;
        }
        if (newFile == null || newFile.isDirectory()) {
            return false;
        }
        if (!checkAndCreateFile(newFile)) {
            return false;
        }
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(oldFile);
            outputStream = new FileOutputStream(newFile);
            byte[] buffer = new byte[8192];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    outputStream = null;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    inputStream = null;
                }
            }
        }
        return false;
    }

    /**
     * 获取文件路径空间大小
     */
    private static long getUsableSpace(File path) {
        try {
            final StatFs stats = new StatFs(path.getPath());
            return stats.getBlockSizeLong() * stats.getAvailableBlocksLong();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 是否挂载了SD卡和有读写SD卡权限
     */
    public static boolean hasExternalStorageAndPermission(Context context) {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                && (context.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * 获取外部或内部缓存目录(无SD卡，则获取内部缓存目录)
     *
     * @return 返回目录文件对象
     */
    public static File getExOrInternalCacheDir(Context context) {
        File cacheDir = null;
        if (hasExternalStorageAndPermission(context)) {
            // /storage/sdcard0/Android/data/<package name>/cache
            cacheDir = context.getExternalCacheDir();
        } else {
            // /data/data/<package name>/cache
            cacheDir = context.getCacheDir();
        }
        if (cacheDir == null) {
            //data/data/packagename/files/cache/
            String cacheDirPath = context.getFilesDir().getPath() + context.getPackageName() + "/cache/";
            cacheDir = new File(cacheDirPath);
        }
        if (!cacheDir.exists()) {
            try {
                cacheDir.exists();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                //创建.momedia文件，不被系统扫描到有媒体文件
                new File(cacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cacheDir;
    }

    /**
     * 获取SD卡跟目录
     */
    public static File getExternalStorageDirectory(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return getExternalStorageDirectory(context, null);
        }
        return null;
    }

    /**
     * 获取SD卡指定目录
     *
     * @param dir 目录名
     */
    public static File getExternalStorageDirectory(Context context, String dir) {
        File externalFile = null;
        if (hasExternalStorageAndPermission(context)) {
            if (!TextUtils.isEmpty(dir)) {
                if (!dir.equals(Environment.DIRECTORY_ALARMS)
                        || !dir.equals(Environment.DIRECTORY_DCIM)
                        || !dir.equals(Environment.DIRECTORY_DOCUMENTS)
                        || !dir.equals(Environment.DIRECTORY_DOWNLOADS)
                        || !dir.equals(Environment.DIRECTORY_MOVIES)
                        || !dir.equals(Environment.DIRECTORY_MUSIC)
                        || !dir.equals(Environment.DIRECTORY_NOTIFICATIONS)
                        || !dir.equals(Environment.DIRECTORY_PICTURES)
                        || !dir.equals(Environment.DIRECTORY_PODCASTS)
                        || !dir.equals(Environment.DIRECTORY_RINGTONES)) {
                    externalFile = Environment.getExternalStorageDirectory();
                } else {
                    externalFile = Environment.getExternalStoragePublicDirectory(dir);
                }
            } else {
                externalFile = Environment.getExternalStorageDirectory();
            }
        }
        return externalFile;
    }

    /*
     * ==分割线=============分割线============分割线========================分割线====================分割线==================分割线=============
     * 以下方法操作的都是/data/data/<package name>/路径下的目录或文件
     * ==分割线=============分割线============分割线========================分割线====================分割线==================分割线=============
     */

    /**
     * 保存在/data/data/<package name>/files/目录中 <br/>
     * 内部文件不存在时，系统会自动创建 <br/>
     *
     * @param fileName 文件名，不能包含目录
     * @param text     需要写入的内容
     */
    public static void createAndWriteInternalFile(Context context, String fileName, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        FileOutputStream output = null;
        try {
            output = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            output.write(text.getBytes());
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取内部文件内容
     *
     * @param fileName 文件名，不包含目录
     * @return 文件内容
     */
    public static StringBuilder readInternalFile(Context context, String fileName) {
        FileInputStream input = null;
        StringBuilder stringBuilder = null;
        try {
            input = context.openFileInput(fileName);
            stringBuilder = new StringBuilder();
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = input.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, len, Charset.defaultCharset()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder;
    }

    /**
     * 读取内部文件列表
     *
     * @return 文件名字符串数组
     */
    public static String[] fileListInternal(Context context) {
        return context.fileList();
    }

    /**
     * 删除内部文件
     *
     * @param fileName 文件名，不包含目录
     * @return 是否删除成功
     */
    public static boolean deleteInternalFile(Context context, String fileName) {
        return context.deleteFile(fileName);
    }

    /**
     * 删除内部数据库
     *
     * @param databaseName 数据库名
     * @return 是否删除成功
     */
    public static boolean deleteInternalDatabase(Context context, String databaseName) {
        return context.deleteDatabase(databaseName);
    }

    /**
     * 获取内部数据库文件对象
     */
    public static File getDatabasePath(Context context, String databaseName) {
        return context.getDatabasePath(databaseName);
    }

    /**
     * 创建一个目录，需要传入目录名称，它返回 一个内部文件对象用到操作路径
     */
    public static File createInternalDirectory(Context context, String dirName) {
        return context.getDir(dirName, Context.MODE_PRIVATE);
    }

    /**
     * 查看指定内部文件
     */
    public static File findInternalFile(Context context, String fileName) {
        return context.getFileStreamPath(fileName);
    }

    /**
     * 获取内部缓存目录
     */
    public static File getInternalCacheDir(Context context) {
        return context.getCacheDir();
    }

    /**
     * 赋值assets下的文件到其他位置
     *
     * @param assetFileNameAndSuffix assets下的文件名(包含后缀)
     * @param saveFile               目标文件位置
     */
    public static boolean copyAssetsFileTo(Context context, String assetFileNameAndSuffix, File saveFile) {
        if (context == null
                || TextUtils.isEmpty(assetFileNameAndSuffix)
                || saveFile == null) {
            return false;
        }
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = context.getAssets().open(assetFileNameAndSuffix);
            if (!checkAndCreateFile(saveFile)) {
                return false;
            }
            outputStream = new FileOutputStream(saveFile);
            byte[] buffer = new byte[8092];
            int len;
            //len = -1,读到文件的结尾
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
            //复制完成
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    outputStream = null;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    inputStream = null;
                }
            }
        }
        return false;
    }
}
