package org.o7planning.project_04.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.o7planning.project_04.model.GIAODICH;
import org.o7planning.project_04.model.Limit;
import org.o7planning.project_04.model.category;
import org.o7planning.project_04.model.spendingsummary;

import java.util.ArrayList;
import java.util.List;

public class LimitDAO {
    private DBHelper dbHelper;
    public LimitDAO (Context context){
        dbHelper = new DBHelper(context);
    }

    //them han muc
    public boolean insertLimit(Limit l, int id_tk) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("TenHM", l.getTenHM());
            values.put("SoTien", l.getSoTien());
            values.put("NgayBD", l.getNgayGD());
            values.put("NgayKT", l.getNgayKetThuc());
            values.put("ID_TK", id_tk);

            long idHanMuc = db.insert("HANMUC", null, values);
            if (idHanMuc == -1) {
                Log.e("DBHelper", "Insert HANMUC failed");
                return false;
            }

            // Gọi DAO đã tách ra để insert các mapping danh mục
            Limit_CateDAO hmdmDAO = new Limit_CateDAO(db);
            for (int idDM : l.getListDanhMuc()) {
                boolean result = hmdmDAO.insertMapping((int) idHanMuc, idDM);
                if (!result) {
                    Log.e("DBHelper", "Insert HANMUC_DANHMUC failed for ID_DM = " + idDM);
                    return false;
                }
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DBHelper", "insertLimit error", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    //Update limit
    public boolean updateLimit(Limit l, int idTK) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("TenHM", l.getTenHM());
            values.put("SoTien", l.getSoTien());
            values.put("NgayBD", l.getNgayGD());
            values.put("NgayKT", l.getNgayKetThuc());

            int rowsAffected = db.update(
                    "HANMUC",
                    values,
                    "ID_HM = ? AND ID_TK = ?",
                    new String[]{String.valueOf(l.getID_HM()), String.valueOf(idTK)}
            );

            if (rowsAffected <= 0) {
                Log.e("DBHelper", "Update HANMUC failed");
                return false;
            }

            Limit_CateDAO hmdmDAO = new Limit_CateDAO(db);
            hmdmDAO.deleteByLimitId(l.getID_HM());

            for (int idDM : l.getListDanhMuc()) {
                if (!hmdmDAO.insertMapping(l.getID_HM(), idDM)) {
                    Log.e("DBHelper", "Insert HANMUC_DANHMUC failed for ID_DM = " + idDM);
                    return false;
                }
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DBHelper", "updateLimit error", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }


    // Xóa hạn mức
    public boolean deleteLimit(int idHM, int idTK) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Limit_CateDAO hmdmDAO = new Limit_CateDAO(db);
            hmdmDAO.deleteByLimitId(idHM);

            int rows = db.delete("HANMUC", "ID_HM = ? AND ID_TK = ?", new String[]{
                    String.valueOf(idHM),
                    String.valueOf(idTK)
            });

            if (rows <= 0) {
                Log.e("DBHelper", "Delete HANMUC failed");
                return false;
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DBHelper", "deleteLimit error", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }


    public List<Limit> getAllLimits(int idTK) {
        List<Limit> limits = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        Cursor cursor = db.rawQuery("SELECT * FROM HANMUC WHERE ID_TK = ?", new String[]{String.valueOf(idTK)});

        Limit_CateDAO limitCateDAO = new Limit_CateDAO(db);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_HM"));
                String tenHM = cursor.getString(cursor.getColumnIndexOrThrow("TenHM"));
                long soTien = cursor.getLong(cursor.getColumnIndexOrThrow("SoTien"));
                String ngayBD = cursor.getString(cursor.getColumnIndexOrThrow("NgayBD"));
                String ngayKT = cursor.getString(cursor.getColumnIndexOrThrow("NgayKT"));
                List<Integer> listDM =limitCateDAO.getDMByHM(id);

                limits.add(new Limit(id, tenHM, soTien, ngayBD, ngayKT, listDM));
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return limits;
    }



    public Limit getLimitById(int limitId) {
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        String query = "Select * from HANMUC where ID_HM =?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limitId)});

        Limit_CateDAO limitCateDAO = new Limit_CateDAO(db);

        if (cursor != null && cursor.moveToFirst()) {
            Limit limit = new Limit();
            limit.setID_HM(cursor.getInt(cursor.getColumnIndexOrThrow("ID_HM")));
            limit.setTenHM(cursor.getString(cursor.getColumnIndexOrThrow("TenHM")));
            limit.setSoTien(cursor.getLong(cursor.getColumnIndexOrThrow("SoTien")));
            limit.setNgayGD(cursor.getString(cursor.getColumnIndexOrThrow("NgayBD")));
            limit.setNgayKetThuc(cursor.getString(cursor.getColumnIndexOrThrow("NgayKT")));

            limit.setListDanhMuc(limitCateDAO.getDMByHM(limitId));

            cursor.close();
            return limit;

        }
        return null;

    }

    public long getTienDaDung(int limitId) {
        SQLiteDatabase db =dbHelper.getReadableDatabase();

        // Lay ngay bắt đầu và kết thúc của hạn mức
        String limitQuery = "Select NgayGD,NgayKetThuc from HANMUC where ID_HM=?";
        Cursor limitCusor = db.rawQuery(limitQuery, new String[]{String.valueOf(limitId)});

        if (!limitCusor.moveToFirst()) {
            limitCusor.close();
            return 0;
        }

        String ngayBD = limitCusor.getString(limitCusor.getColumnIndexOrThrow("NgayGD"));
        String ngayKT = limitCusor.getString(limitCusor.getColumnIndexOrThrow("NgayKetThuc"));
        limitCusor.close();

        // lay cac ID_DM lien ket voi han muc
        String categoryQuery = "Select ID_DM from HANMUC_DANHMUC where ID_HM =?";
        Cursor categoryCursor = db.rawQuery(categoryQuery, new String[]{String.valueOf(limitId)});

        if (!categoryCursor.moveToFirst()) {
            categoryCursor.close();
            return 0;
        }
        StringBuilder dmPlaceholders = new StringBuilder();
        List<String> args = new ArrayList<>();

        do {
            if (dmPlaceholders.length() > 0) dmPlaceholders.append(",");
            dmPlaceholders.append("?");
            args.add(categoryCursor.getString(0));
        } while (categoryCursor.moveToNext());

        categoryCursor.close();

        args.add(ngayBD);
        args.add(ngayKT);

        //truy van tong tien giao dich trong các danh mục + trong khoang ngày của hạn mức
        String sql = "Select SUM(SoTien) from GIAODICH where ID_DM in(" + dmPlaceholders + ") and ThoiGian Between ? and ?";
        Cursor cursor = db.rawQuery(sql, args.toArray(new String[0]));

        long tongtien = 0;
        if (cursor.moveToFirst()) {
            tongtien = cursor.isNull(0) ? 0 : cursor.getLong(0);
        }
        cursor.close();
        return tongtien;
    }




   // Tính tổng số tiền đã chi của toàn bộ hạn mức (gộp tất cả danh mục bên trong).
    public long getTotalSpentInLimit(int limitId,int userId, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT SUM(gd.SoTien) FROM GIAODICH gd " +
                "JOIN HANMUC_DANHMUC hmdm ON gd.ID_DM = hmdm.ID_DM " +
                "WHERE hmdm.ID_HM = ? AND gd.ID_TK = ? AND gd.ThoiGian BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(sql, new String[]{
                String.valueOf(limitId),
                String.valueOf(userId),
                startDate,
                endDate
        });
        Log.d("LIMIT_DEBUG", "Query: " + sql);
        Log.d("LIMIT_DEBUG", "Params: " + limitId + ", " + userId + ", " + startDate + ", " + endDate);


        long total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    public List<Limit> getLimitsByCategory(int categoryId) {
        List<Limit> limits = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT hm.* FROM HANMUC hm " +
                "JOIN HANMUC_DANHMUC hmdm ON hm.ID_HM = hmdm.ID_HM " +
                "WHERE hmdm.ID_DM = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(categoryId)});
        Limit_CateDAO limitCateDAO = new Limit_CateDAO(db);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_HM"));
                String tenHM = cursor.getString(cursor.getColumnIndexOrThrow("TenHM"));
                long soTien = cursor.getLong(cursor.getColumnIndexOrThrow("SoTien"));
                String ngayBD = cursor.getString(cursor.getColumnIndexOrThrow("NgayBD"));
                String ngayKT = cursor.getString(cursor.getColumnIndexOrThrow("NgayKT"));
                List<Integer> listDM = limitCateDAO.getDMByHM(id);

                limits.add(new Limit(id, tenHM, soTien, ngayBD, ngayKT, listDM));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return limits;
    }


}
