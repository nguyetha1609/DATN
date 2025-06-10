package org.o7planning.project_04;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PrepopulatedDBHelper {

    private static final String DB_NAME = "QLCT.db";
    private final Context context;

    public PrepopulatedDBHelper(Context context) {
        this.context = context;
    }

    /**
     * Hàm kiểm tra và copy DB nếu chưa tồn tại.
     */
    public void checkAndCopyDatabase() {
        File dbFile = context.getDatabasePath(DB_NAME);
        if (!dbFile.exists()) {
            // DB chưa tồn tại => copy từ assetsa
            copyDatabaseFromAssets();
        }
    }

    /**
     * Hàm copy file .db từ thư mục assets sang /data/data/<package>/databases/
     */
    private void copyDatabaseFromAssets() {
        try {
            // 1. Mở file DB trong assets
            InputStream input = context.getAssets().open(DB_NAME);

            // 2. Tạo đường dẫn đích
            String outFileName = context.getDatabasePath(DB_NAME).getPath();

            // 3. Đảm bảo thư mục /databases/ tồn tại
            File f = new File(outFileName);
            f.getParentFile().mkdirs();

            // 4. Mở output stream tới file đích
            OutputStream output = new FileOutputStream(outFileName);

            // 5. Sao chép dữ liệu
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // 6. Đóng stream
            output.flush();
            output.close();
            input.close();

            System.out.println("Copy database from assets success!");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error copying database from assets: " + e.getMessage());
        }
    }

    /**
     * Hàm mở DB (đọc/ghi).
     * Trước khi mở, nên gọi checkAndCopyDatabase() để đảm bảo DB đã copy.
     */
    public SQLiteDatabase openDatabase() {
        checkAndCopyDatabase();
        String dbPath = context.getDatabasePath(DB_NAME).getPath();
        return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * Hàm xóa DB (nếu muốn reset DB)
     * Lưu ý: Cẩn thận khi dùng, có thể mất dữ liệu.
     */
    public void deleteDatabase() {
        String dbPath = context.getDatabasePath(DB_NAME).getPath();
        File dbFile = new File(dbPath);
        if (dbFile.exists()) {
            dbFile.delete();
            System.out.println("Database deleted: " + dbPath);
        }
    }
}
