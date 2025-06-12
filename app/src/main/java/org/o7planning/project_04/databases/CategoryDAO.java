package org.o7planning.project_04.databases;

import static android.database.sqlite.SQLiteDatabase.openDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.o7planning.project_04.model.Category;

import java.util.ArrayList;
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
    public boolean isCateNameExists(String name, String loaiDm, int id_tk) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM DANHMUC WHERE TenDM = ? AND LoaiDM = ? AND ID_TK = ?",
                new String[]{name, loaiDm, String.valueOf(id_tk)}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
    public List<Category> getAllCategories(String loaiDM, int id_tk) {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM DANHMUC WHERE LoaiDM = ? AND ID_TK = ?",
                new String[]{loaiDM, String.valueOf(id_tk)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int ID_DM = cursor.getInt(cursor.getColumnIndexOrThrow("ID_DM"));
                String TenDM = cursor.getString(cursor.getColumnIndexOrThrow("TenDM"));
                String LoaiDM = cursor.getString(cursor.getColumnIndexOrThrow("LoaiDM"));
                String HinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"));
                int DMMacDinh = cursor.getInt(cursor.getColumnIndexOrThrow("DMMacDinh"));

                Category cate = new Category(ID_DM, TenDM, LoaiDM, HinhAnh, DMMacDinh);
                categoryList.add(cate);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return categoryList;
    }

    //getcatebyID
    public Category getCategoryById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Category cate = null;
        Cursor cursor = db.rawQuery("select * from DANHMUC where ID_DM=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            int ID_DM = cursor.getInt(cursor.getColumnIndexOrThrow("ID_DM"));
            String TenDM = cursor.getString(cursor.getColumnIndexOrThrow("TenDM"));
            String LoaiDM = cursor.getString(cursor.getColumnIndexOrThrow("LoaiDM"));
            String HinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"));
            int DMMacDinh = cursor.getInt(cursor.getColumnIndexOrThrow("DMMacDinh"));
            cate = new Category(ID_DM, TenDM, LoaiDM, HinhAnh, DMMacDinh);

        }
        cursor.close();
        return cate;
    }
    //addcate
    public void addcate(Category cate, int id_tk) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("TenDM", cate.getTenDM());
        values.put("LoaiDM", cate.getLoaiDM());
        values.put("HinhAnh", cate.getHinhAnh());
        values.put("DMmacdinh", cate.getDMMacDinh());
        values.put("ID_TK", id_tk); // Thêm ID_TK

        db.insert("DANHMUC", null, values);
        db.close();
    }

    public void insertDefaultCategoriesIfNeeded(int id_tk) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM DANHMUC WHERE DMmacdinh = 1 AND ID_TK = ?",
                new String[]{String.valueOf(id_tk)});
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            if (count == 0) {
                addcate(new Category("Ăn uống", "ChiTieu", "cate_food", 1), id_tk);
                addcate(new Category("Đi lại", "ChiTieu", "cate_grab", 1), id_tk);
                addcate(new Category("Giải trí", "ChiTieu", "cate_entertaiment", 1), id_tk);
                addcate(new Category("Lương", "ThuNhap", "cate_salary", 1), id_tk);
                addcate(new Category("Thưởng", "ThuNhap", "cate_certificate", 1), id_tk);
            }
        }
        cursor.close();
        db.close();
    }
    //updatecate
    public boolean updateCategory(Category cate, int id_tk) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TenDM", cate.getTenDM());
        values.put("LoaiDM", cate.getLoaiDM());
        values.put("HinhAnh", cate.getHinhAnh());

        int rows = db.update("DANHMUC", values, "ID_DM = ? AND ID_TK = ?",
                new String[]{String.valueOf(cate.getID()), String.valueOf(id_tk)});
        db.close();
        return rows > 0;
    }
    //delete cate
    public boolean deleteCategory(int id_dm, int id_tk) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("DANHMUC", "ID_DM = ? AND ID_TK = ?",
                new String[]{String.valueOf(id_dm), String.valueOf(id_tk)});
        db.close();
        return rows > 0;
    }



}

