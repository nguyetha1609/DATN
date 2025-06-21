package org.o7planning.project_04.databases;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.o7planning.project_04.model.GIAODICH;
import org.o7planning.project_04.model.category;
import org.o7planning.project_04.model.spendingsummary;

import java.util.ArrayList;
import java.util.List;

public class Limit_CateDAO {
    private SQLiteDatabase db;

    public Limit_CateDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Lấy ID danh mục theo ID hạn mức
    public List<Integer> getDMByHM(int idHM) {
        List<Integer> listDM = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT ID_DM FROM HANMUC_DANHMUC WHERE ID_HM = ?", new String[]{String.valueOf(idHM)});
        while (cursor.moveToNext()) {
            listDM.add(cursor.getInt(0));
        }
        cursor.close();
        return listDM;
    }

    // Lấy danh sách danh mục (category) cho 1 hạn mức
    public List<category> getCategoriesForLimit(int idHM) {
        List<category> categories = new ArrayList<>();
        String query = "SELECT dm.ID_DM, dm.TenDM, dm.HinhAnh " +
                "FROM HANMUC_DANHMUC hmdm " +
                "JOIN DANHMUC dm ON hmdm.ID_DM = dm.ID_DM " +
                "WHERE hmdm.ID_HM = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idHM)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_DM"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("TenDM"));
            String iconName = cursor.getString(cursor.getColumnIndexOrThrow("HinhANh"));
            categories.add(new category(id, name, iconName));
        }
        cursor.close();
        return categories;
    }

    // Lấy danh sách ID danh mục theo hạn mức
    public ArrayList<Integer> getCategoryIdsByLimitId(int limitId) {
        ArrayList<Integer> categoryIds = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT ID_DM FROM HANMUC_DANHMUC WHERE ID_HM = ?", new String[]{String.valueOf(limitId)});
        while (cursor.moveToNext()) {
            categoryIds.add(cursor.getInt(0));
        }
        cursor.close();
        return categoryIds;
    }

    // Lấy tổng chi từng danh mục trong 1 hạn mức
    public List<spendingsummary> getSpendingsByLimit(int limitID, String startDate, String endDate, int userId) {
        List<spendingsummary> list = new ArrayList<>();
        String sql = "SELECT dm.ID_DM, dm.TenDM, dm.HinhAnh, IFNULL(SUM(gd.SoTien), 0) AS TongChi " +
                "FROM HANMUC_DANHMUC hmdm " +
                "JOIN DANHMUC dm ON hmdm.ID_DM = dm.ID_DM " +
                "LEFT JOIN GIAODICH gd ON gd.ID_DM = dm.ID_DM " +
                "    AND gd.ThoiGian BETWEEN ? AND ? " +
                "    AND gd.ID_TK = ? " +
                "WHERE hmdm.ID_HM = ? " +
                "GROUP BY dm.ID_DM, dm.TenDM, dm.HinhAnh";

        Cursor cursor = db.rawQuery(sql, new String[]{
                startDate, endDate,
                String.valueOf(userId),
                String.valueOf(limitID)
        });

        while (cursor.moveToNext()) {
            spendingsummary summary = new spendingsummary();
            summary.setIdDM(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DM")));
            summary.setTenDM(cursor.getString(cursor.getColumnIndexOrThrow("TenDM")));
            summary.setHinhAnh(cursor.getString(cursor.getColumnIndexOrThrow("HinhANh")));
            summary.setTongChi(cursor.getLong(cursor.getColumnIndexOrThrow("TongChi")));
            list.add(summary);
        }
        cursor.close();
        return list;
    }

    // Lấy giao dịch theo danh mục và hạn mức
    public List<GIAODICH> getTransactionsByCategoryAndLimit(int categoryId, String startDate, String endDate, int limitId, int userId) {
        List<GIAODICH> list = new ArrayList<>();
        String sql = "SELECT gd.ID_GD, gd.ID_DM, gd.SoTien, gd.ThoiGian, gd.GhiChu " +
                "FROM GIAODICH gd " +
                "INNER JOIN HANMUC_DANHMUC hmdm ON gd.ID_DM = hmdm.ID_DM " +
                "WHERE gd.ID_DM = ? AND hmdm.ID_HM = ? AND gd.ThoiGian BETWEEN ? AND ? AND gd.ID_TK = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{
                String.valueOf(categoryId),
                String.valueOf(limitId),
                startDate,
                endDate,
                String.valueOf(userId)
        });

        while (cursor.moveToNext()) {
            GIAODICH g = new GIAODICH();
            g.setID_GD(cursor.getInt(cursor.getColumnIndexOrThrow("ID_GD")));
            g.setID_DM(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DM")));
            g.setSoTien(cursor.getLong(cursor.getColumnIndexOrThrow("SoTien")));
            g.setThoiGian(cursor.getString(cursor.getColumnIndexOrThrow("ThoiGian")));
            g.setGhiChu(cursor.getString(cursor.getColumnIndexOrThrow("GhiChu")));
            list.add(g);
        }
        cursor.close();
        return list;
    }

    // Thêm mapping hạn mức - danh mục
    public boolean insertMapping(int idHM, int idDM) {
        ContentValues values = new ContentValues();
        values.put("ID_HM", idHM);
        values.put("ID_DM", idDM);
        long result = db.insert("HANMUC_DANHMUC", null, values);
        return result != -1;
    }

    // Xoá toàn bộ mapping theo hạn mức
    public void deleteByLimitId(int idHM) {
        db.delete("HANMUC_DANHMUC", "ID_HM = ?", new String[]{String.valueOf(idHM)});
    }
}
