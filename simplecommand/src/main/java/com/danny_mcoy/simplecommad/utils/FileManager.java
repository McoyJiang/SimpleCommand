package com.danny_mcoy.simplecommad.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.danny_mcoy.simplecommad.log.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Danny_姜新星 on 6/28/2016.
 */
public class FileManager {

    private static final String EXTRA_PREFIX = Logger.TAG + "_";

    private static final String FOLDER_FOR_TEMP_DATA = "mediahub";

    private ContentResolver cr;

    public FileManager(Context context) {
        this.cr = context.getContentResolver();
    }

    public static String extractNameFromPath(final String path) {
        if (TextUtils.isEmpty(path)) return "";
        String[] tokens = path.split("/");
        if (tokens.length == 0) {
            return path;
        } else {
            String[] partsOfName = tokens[tokens.length - 1].split("\\.");
            return partsOfName.length == 0 ? "" : partsOfName[0];
        }
    }

    public static String extractNameWithSuffixFromPath(final String path) {
        if (TextUtils.isEmpty(path)) return "";
        String[] tokens = path.split("/");
        return tokens.length == 0 ? "" : tokens[tokens.length - 1];
    }

    public static String extractSuffixFromPath(final String path) {
        if (TextUtils.isEmpty(path)) return "";
        String[] tokens = path.split("\\.");
        return tokens.length == 0 ? "" : tokens[tokens.length - 1];
    }

    public static String extractPrefixFromPath(final String path) {
        if (TextUtils.isEmpty(path)) return "";
        String[] tokens = path.split("\\.");
        return tokens.length == 0 ? "" : ".".concat(tokens[tokens.length-1]);
    }

    public static String extractPathWithoutName(final String from) {
        String[] tokens = from.split("/");
        if (tokens.length == 0)
            throw new IllegalArgumentException("Did you pass valid image path?");

        final int pathEndIdx = from.lastIndexOf("/");
        return from.substring(0, pathEndIdx);
    }

    public static String getMimeType(final String from) {
        if (TextUtils.isEmpty(from)) return "";

        String ext = extractSuffixFromPath(from);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    public String saveFile(File root, String name, Bitmap bitmap) {
        return saveFile(root, name, bitmap, null);
    }

    public String saveFile(File root, String name, Bitmap bitmap, boolean hideFromScan) {
        return saveFile(root, name, bitmap, ".jpg");
    }

    public String saveFile(File root, String name, Bitmap bitmap, String suffix) {
        File tempFile = null;
        File file = null;
        FileOutputStream out = null;
        try {
            StringBuilder directory = new StringBuilder();
            directory.append(root.toString());

            file = new File(directory.toString());
            if (file.mkdirs() || file.isDirectory()) {
                String newName = TextUtils.isEmpty(suffix) ? name : name.concat(suffix);
                tempFile = new File(file, newName);
                out = new FileOutputStream(tempFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                out.flush();
                return tempFile.toString();
            }
        } catch (IOException e) {
            Logger.d(Logger.TAG, "Failed to copy file", e);
        } finally {
            if (bitmap != null) bitmap.recycle();
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Logger.d(Logger.TAG, "Failed to close stream", e);
                }
            }
        }
        return null;
    }

    public File copyFile(String uri, File directory) {
        File tempFile = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            final String str = "file://" + uri;
            Uri.Builder builder = Uri.parse(str).buildUpon();
            builder.scheme("file");
            Log.d(Logger.TAG, "Path to file: " + builder.toString());
            in = cr.openInputStream(builder.build());
            tempFile = File.createTempFile(
                    EXTRA_PREFIX.concat(FileManager.extractNameFromPath(uri)),
                    FileManager.extractPrefixFromPath(uri),
                    directory
            );
            out = new FileOutputStream(tempFile, false);
            byte[] buffer = new byte[4 * 1024];
            while (in.read(buffer) != -1) {
                out.write(buffer);
            }
        } catch (IOException e) {
            Log.d(Logger.TAG, "Failed to copy file", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(Logger.TAG, "Failed to close stream", e);
                }
            }
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    Log.e(Logger.TAG, "Failed to close stream", e);
                }
            }
        }
        return tempFile;
    }

    public File addHiddenFolderToPath(File file) {
        StringBuilder directory = new StringBuilder();
        directory.append(file.getAbsolutePath());
        directory.append(File.separator);
        directory.append(".nomedia");
        directory.append(File.separator);
        directory.append(FOLDER_FOR_TEMP_DATA);
        File newFile = new File(directory.toString());
        newFile.mkdirs();
        return newFile;
    }

    public boolean deleteCache(Context context, File root) {


        deleteFile(root, root);

        return true;
    }

    private void deleteFile(File file, File directory) {
        String[] files = file.list();
        for (String tmp : files) {
            File newFile = new File(directory, tmp.toString());
            if (newFile.isDirectory()) deleteFile(newFile, newFile);
            newFile.delete();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


}
