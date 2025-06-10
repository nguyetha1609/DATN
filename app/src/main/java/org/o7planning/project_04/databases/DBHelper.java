package org.o7planning.project_04.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.CpuUsageInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.o7planning.project_04.model.GIAODICH;
import org.o7planning.project_04.model.Limit;
import org.o7planning.project_04.model.category;
import org.o7planning.project_04.model.categoryStat;
import org.o7planning.project_04.model.spendingsummary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.xml.transform.sax.SAXResult;

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
    public List<categoryStat> getStatsByDateRange(String loaiDM, String startDate, String endDate) {
        List<categoryStat> stats = new ArrayList<>();
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

            stats.add(new categoryStat(tenDM, tongtien, color));
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

}
