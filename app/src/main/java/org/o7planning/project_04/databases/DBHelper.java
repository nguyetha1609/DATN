package org.o7planning.project_04.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

import org.o7planning.project_04.model.CategoryStat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "QLCT.db";
    private static final int DB_VERSION = 1;

    private final Context context;
    private final String dbPath;
    private SQLiteDatabase database;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.dbPath = context.getDatabasePath(DB_NAME).getPath();
        createDatabaseIfNone(); // tự động copy database khi khởi tạo
    }

    public void createDatabaseIfNone() {
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            Log.d("DBHelper", "Database not found. Copying from assets...");
            copyDatabaseFromAssets();
        } else {
            Log.d("DBHelper", "Database already exists. Skipping copy.");
        }
    }
    private void copyDatabaseFromAssets() {
        try {
            InputStream input = context.getAssets().open(DB_NAME);
            File dbDir = new File(context.getApplicationInfo().dataDir + "/databases/");

            if (!dbDir.exists()) dbDir.mkdirs();

            OutputStream output = new FileOutputStream(dbPath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            input.close();

            Log.d("DBHelper", "Database copied from assets successfully.");
        } catch (Exception e) {
            Log.e("DBHelper", "Error copying database from assets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public SQLiteDatabase openDatabase() {
        if (database == null || !database.isOpen()) {
            database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return database;
    }

    // Truy vấn giao dich theo nhóm danh mục vex bieu do thong ke
    public List<CategoryStat> getStatsByDateRange(String loaiDM, String startDate, String endDate) {
        List<CategoryStat> stats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
//Ktra lai dịnh dạng ThoiGian trong bảng GiaoDịch khi truy vấn
        String query = "Select DANHMUC.TenDM,SUM(GIAODICH.SoTien) as TongTien " +
                "From GIAODICH Inner join DANHMUC on GIAODICH.ID_DM = DANHMUC.ID_DM " +
                "Where DANHMUC.LoaiDM =? And ThoiGian between ? And ? Group by DANHMUC.TenDM";
        Cursor cursor = db.rawQuery(query, new String[]{loaiDM, startDate, endDate});
        while (cursor.moveToNext()) {
            String tenDM = cursor.getString(0);
            float tongtien = cursor.getFloat(1);

            int color = getRandomColor();

            stats.add(new CategoryStat(tenDM, tongtien, color));
        }
        cursor.close();
        return stats;
    }

    private int getRandomColor() {
        Random rnd = new Random();
        return Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }




    // Lấy email theo ID
    public String getEmailById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Email FROM TAIKHOAN WHERE ID_TK = ?", new String[]{String.valueOf(userId)});
        String email = "";
        if (cursor.moveToFirst()) {
            email = cursor.getString(0);
        }
        cursor.close();
        return email;
    }

    // Cập nhật thông tin người dùng
    public boolean updateUserInformation(long userId, String newEmail, String oldPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT password FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            String currentPassword = cursor.getString(0);
            if (!currentPassword.equals(oldPassword)) {
                cursor.close();
                return false;
            }
        } else {
            cursor.close();
            return false;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("email", newEmail);
        if (!newPassword.isEmpty()) {
            values.put("password", newPassword);
        }

        int rows = db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }
}
