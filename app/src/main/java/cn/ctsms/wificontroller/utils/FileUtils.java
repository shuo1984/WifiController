package cn.ctsms.wificontroller.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.FileProvider;
import android.text.format.Formatter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author zhangshuo
 * @since 2014/12/29.
 */
public class FileUtils {

   // private static final Log logger = Log.build(FileUtils.class);

    private static File sdcard = Environment.getExternalStorageDirectory();

    private static final String ROOT = "/ctpim/";

    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * The system separator character.
     */
    private static final char SYSTEM_SEPARATOR = File.separatorChar;


    private static final String[][] MIME_MapTable={
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    /**
     * @return sd卡是否已经加载
     */
    public static boolean isCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File getStorgeFile() {
        String path = Environment.getExternalStorageDirectory().getPath();
        if (StringUtils.isNotEmpty(path) && StringUtils.startsWith(path, File.separator)) {
            path = StringUtils.substringBefore(StringUtils.substring(path, 1), File.separator);
            android.util.Log.e("@#@#@#getStorgeFile", "" + path);
        }
        return new File(File.separator + path + File.separator);
    }

    public static File getVCFCardPath(){
        String path = Environment.getExternalStorageDirectory().getPath();
        return new File(File.separator + path + File.separator);
    }



    private static File getBookDirectory() {
        return mkdirs(new File("ctpim" + File.separator + "books"));
    }

    private static File mkdirs(File directory) {
        if (!directory.exists()) {
            if (!FileUtils.isCardMounted()) {
                // throw new MediaFileOperationException("SD卡未装载");
                throw new RuntimeException("SD卡未装载");
            }
            directory = FileUtils.createFolder(directory.getAbsolutePath());
        }
        return directory;
    }

    public static void createBookFile(String name, String content) {
        FileWriter writer = null;
        try {
            File file = File.createTempFile(name, ".txt", getBookDirectory());
            writer = new FileWriter(file.getPath(), true);
            writer.write(content + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * create folder
     *
     * @param folder folder
     * @return successful
     */
    public static File createFolder(String folder) {
        File directory = null;
        String f = folder;
        if (!folder.startsWith("/")) {
            f = "/" + folder;
        }
        if (!folder.endsWith("/")) {
            f = f + "/";
        }
        if (isCardMounted()) {
            try {
                directory = new File(sdcard.getAbsolutePath() + f);
                if (!directory.exists() && directory.mkdirs()) {
                   // logger.info("create folder:%s success", folder);
                    return directory;
                }
            } catch (Exception ex) {
              //  logger.warn("can not create folder:%s\n%s", f, ex);
            }
        } else {
           // logger.warn("sdcard is not mounted,can not create folder");
        }
        return directory;
    }

    public static long bytesToMemory(long size) {
        return size / 1024;
    }

    public static File getAppFile(String filename) {
        File parent = null;

        if (isCardMounted()) {
            try {
                parent = new File(sdcard.getAbsolutePath() + ROOT + "apps");
                if (!parent.exists() && parent.mkdirs()) {
                   // logger.debug("create parent path %s successful", parent.getAbsolutePath());
                }
            } catch (Exception ex) {
                //logger.debug("init StorageWriteSupport error:%s", ex);
            }
        } else {
           // logger.debug("%s", "sdcard not mounted,ignore");
        }

        return new File(parent, filename);
    }


    public static String jieyaZip(Context context, byte[] bytes) {
        String file = null;
        try {
            File outFile = null;                       // 定义输出的文件对象
            ZipInputStream zipInput = new ZipInputStream(new ByteArrayInputStream(bytes));// 实例化ZIP输入流
            ZipEntry entry = null;                     // 定义一个 ZipEntry对象，用于接收压缩文件中  的每一个实体
            OutputStream out = null;                   // 定义输出流， 用于输出每一个实体内容
            while ((entry = zipInput.getNextEntry()) != null) { // 得到每一个  ZipEntry
                file = context.getFilesDir() + File.separator + entry.getName();
                outFile = new File(file);
                // 实例化输出文件
                if (!outFile.exists()) {                // 判断文件是否存在
                    outFile.createNewFile();             // 不存在则创建新文件
                }
                out = new FileOutputStream(outFile);     // 实例化输出流对象
                byte[] b = new byte[2048];
                int temp = 0;
                while ((temp = zipInput.read(b)) != -1) {   // 读取内容
                    out.write(b, 0, temp);                    // 输出内容
                }
                out.close();                        // 关闭输出流
            }
            zipInput.close(); // 关闭输入流
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

 /*   public static String readFileToStr(File file) {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(file);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return res;
    }*/

    //这个是手机内存的可用空间大小
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return ((availableBlocks * blockSize) / 1024) / 1024;
    }


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static long findSDMemory(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();

        long totalSize = totalBlocks * blockSize;
        long availSize = availableBlocks * blockSize;

        String totalStr = Formatter.formatFileSize(context, totalSize);
        String availStr = Formatter.formatFileSize(context, availSize);
        return bytesToMemory(availSize) / 1024;
    }

    public static boolean renameSDFile(File oldFile, File newFile) {
        return oldFile.renameTo(newFile);
    }

    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     */
    public static StringBuffer readFileByBytes(String fileName) {
        File file = new File(fileName);
        StringBuffer sb = new StringBuffer();
        InputStream in = null;
/*        try {
            System.out.println("以字节为单位读取文件内容，一次读一个字节：");
            // 一次读一个字节
            in = new FileInputStream(file);
            int tempbyte;
            while ((tempbyte = in.read()) != -1) {
                System.out.write(tempbyte);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }*/
        try {
            System.out.println("以字节为单位读取文件内容，一次读多个字节：");
            // 一次读多个字节
            byte[] tempbytes = new byte[100];
            int byteread = 0;
            in = new FileInputStream(fileName);
            FileUtils.showAvailableBytes(in);
            // 读入多个字节到字节数组中，byteread为一次读入的字节数
            while ((byteread = in.read(tempbytes)) != -1) {
                System.out.write(tempbytes, 0, byteread);
                sb.append(tempbytes);
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
        return sb;
    }

    /**
     * 显示输入流中还剩的字节数
     */
    private static void showAvailableBytes(InputStream in) {
        try {
            System.out.println("当前字节输入流中的字节数为:" + in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean delFile(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            return false;
        }
        return file.delete();
    }


    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }




    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (!file.isDirectory()) {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize);
    }

    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        return size;
    }

    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }

        if (!directory.delete()) {
            String message =
                    "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        if (isSystemWindows()) {
            return false;
        }
        File fileInCanonicalDir = null;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }

        if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
            return false;
        } else {
            return true;
        }
    }

    public static void cleanDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (File file : files) {
            try {
                forceDelete(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }
    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent){
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                String message =
                        "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    static boolean isSystemWindows() {
        return SYSTEM_SEPARATOR == WINDOWS_SEPARATOR;
    }

    public static void createFile(File file, StringBuffer buffer) {
        try {
            File newFile = new File(file.getAbsolutePath() + ".txt");
            if (newFile.exists())// 存在，则删除
                if (!newFile.delete())// 删除成功则创建
                {
                    System.err.println("删除文件" + newFile + "失败");
                }
            if (newFile.createNewFile()) {// 创建成功，则写入文件内容
                PrintWriter p = new PrintWriter(new FileOutputStream(newFile
                        .getAbsolutePath()));
                p.write(buffer.toString());
                p.close();
            } else {
                System.err.println("创建文件：" + newFile + "失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 根据byte数组，生成文件
     */
    public static void getFile(byte[] bfile, String filePath,String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath+"\\"+fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static String getFileNameFromFilePath(String path){
        File tempFile = new File(path.trim());
        String fileName = tempFile.getName();
        System.out.println("fileName = " + fileName);
        return fileName;
    }

    public static void writeFile(Context context,String fileName, byte[] data) {
        try {
            FileOutputStream fout = context.openFileOutput(fileName, MODE_PRIVATE);
            fout.write(data);
            fout.close();
        } catch (Exception err) {
        }
    }

    public static byte[] readFile(Context context , String fileName) {
        try {
            FileInputStream fin = context.openFileInput(fileName);
            int length = fin.available();// 获取文件长度
            byte[] bytes = new byte[length];
            fin.read(bytes);
            return bytes;
            //return EncodingUtils.getString(bytes, ENCODING);
        } catch (Exception err) {
            return null;
        }
    }

    /**
     * 打开文件
     * @param file
     */
    public static void openFile(Context context,File file){

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "cn.com.ctid.securitymodule.fileprovider"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(context, "cn.com.ctid.securitymodule.fileprovider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        //设置intent的data和Type属性。
        intent.setDataAndType(data, type);
        //跳转
        context.startActivity(intent); //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。

    }


    private static String getMIMEType(File file) {

        String type="*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
        /* 获取文件的后缀名*/
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }


    public static void main(String[] args) {
        int count =0;
        for(int i=0;i<10;i++){
            count = count++;
        }
        System.out.println("count======"+count);
    }

}
