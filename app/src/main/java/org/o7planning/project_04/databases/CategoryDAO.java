package org.o7planning.project_04.databases;

import static android.database.sqlite.SQLiteDatabase.openDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.o7planning.project_04.model.category;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class CategoryDAO {
    private DBHelper dbHelper;

    public CategoryDAO (Context context){
        dbHelper = new DBHelper(context);
    }

    public boolean isCategoryUsedAnywhere(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int usedInTransactions = 0;
        int usedInLimits = 0;

        // Kiểm tra trong bảng GiaoDich
        Cursor cursor1 = db.rawQuery("SELECT COUNT(*) FROM GIAODICH WHERE ID_DM = ?", new String[]{String.valueOf(categoryId)});
        if (cursor1.moveToFirst()) {
            usedInTransactions = cursor1.getInt(0);
        }
        cursor1.close();

        // Kiểm tra trong bảng HanMucDanhMuc (nhiều hạn mức có thể dùng chung danh mục)
        Cursor cursor2 = db.rawQuery("SELECT COUNT(*) FROM HANMUC_DANHMUC WHERE ID_DM = ?", new String[]{String.valueOf(categoryId)});
        if (cursor2.moveToFirst()) {
            usedInLimits = cursor2.getInt(0);
        }
        cursor2.close();

        return (usedInTransactions > 0 || usedInLimits > 0);
    }
    //Kiem tra xem ten danh muc da ton taij chua
    public  boolean isCateNameExists(String name, String loaiDm){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "Select * from DANHMUC where TenDM =? And LoaiDM =?",
                new String[]{name,loaiDm}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
    public List<category> getAllCategories(String LoaiDM) {
        List<category> categoryList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM DANHMUC WHERE LoaiDM=?", new String[]{LoaiDM});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int ID_DM = cursor.getInt(cursor.getColumnIndexOrThrow("ID_DM"));
                String TenDM = cursor.getString(cursor.getColumnIndexOrThrow("TenDM"));
                String loaiDM = cursor.getString(cursor.getColumnIndexOrThrow("LoaiDM"));
                String HinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"));
                int DMMacDinh = cursor.getInt(cursor.getColumnIndexOrThrow("DMMacDinh"));

                category cate = new category(ID_DM, TenDM, loaiDM, HinhAnh, DMMacDinh);
                categoryList.add(cate);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return categoryList;
    }
    //getcatebyID
    public category getCategoryById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        category cate = null;
        Cursor cursor = db.rawQuery("select * from DANHMUC where ID_DM=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            int ID_DM = cursor.getInt(cursor.getColumnIndexOrThrow("ID_DM"));
            String TenDM = cursor.getString(cursor.getColumnIndexOrThrow("TenDM"));
            String LoaiDM = cursor.getString(cursor.getColumnIndexOrThrow("LoaiDM"));
            String HinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"));
            int DMMacDinh = cursor.getInt(cursor.getColumnIndexOrThrow("DMMacDinh"));
            cate = new category(ID_DM, TenDM, LoaiDM, HinhAnh, DMMacDinh);

        }
        cursor.close();
        return cate;
    }
    //addcate
    public void addcate(category cate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("TenDM", cate.getTenDM());
        values.put("LoaiDM", cate.getLoaiDM());
        values.put("HinhAnh", cate.getHinhAnh()); // lưu tên icon
        values.put("DMmacdinh", cate.getDMMacDinh());

        db.insert("DANHMUC", null, values);
        db.close();
    }

    public void insertDefaultCategoriesIfNeeded() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM DANHMUC WHERE DMmacdinh = 1", null);
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            if (count == 0) {
                // Chi tiêu
                addcate(new category("Ăn uống", "ChiTieu", "cate_food", 1));
                addcate(new category("Đi lại", "ChiTieu", "cate_grab", 1));
                addcate(new category("Giải trí", "ChiTieu", "cate_entertaiment", 1));


                // Thu nhập
                addcate(new category("Lương", "ThuNhap", "cate_salary", 1));
                addcate(new category("Thưởng", "ThuNhap", "cate_certificate", 1));
            }
        }
        cursor.close();

    }
    //updatecate
    public void updateCategory(category cate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TenDM", cate.getTenDM());
        values.put("LoaiDM", cate.getLoaiDM());
        values.put("HinhAnh", cate.getHinhAnh());
        db.update("DANHMUC", values, "ID_DM= ?", new String[]{String.valueOf(cate.getID())});
    }
    // delete cate
    public boolean deleteCategory(int id, Context context) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("DANHMUC", "ID_DM= ?", new String[]{String.valueOf(id)});
        return true;
    }



}

