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
    public boolean insertLimit(Limit l) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("TenHM", l.getTenHM());
            values.put("SoTien", l.getSoTien());
            values.put("NgayBD", l.getNgayGD());
            values.put("NgayKT", l.getNgayKetThuc());

            long idHanMuc = db.insert("HANMUC", null, values);
            if (idHanMuc == -1) {
                Log.e("DBHelper", "Insert HANMUC failed");
                return false;
            }

            for (int idDM : l.getListDanhMuc()) {
                ContentValues cv = new ContentValues();
                cv.put("ID_HM", idHanMuc);
                cv.put("ID_DM", idDM);
                long result = db.insert("HANMUC_DANHMUC", null, cv);
                if (result == -1) {
                    Log.e("DBHelper", "Insert HANMUC_DANHMUC failed for ID_DM=" + idDM);
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
    public boolean updateLimit(Limit l){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put("TenHM",l.getTenHM());
            values.put("SoTien",l.getSoTien());
            values.put("NgayBD",l.getNgayGD());
            values.put("NgayKT",l.getNgayKetThuc());

            int rowsAffected = db.update("HANMUC",values,"ID_HM =?",new String[]{String.valueOf(l.getID_HM())});
            if(rowsAffected <=0){
                Log.e("DBHelper","Update HANMUC failed");
                return  true;
            }
            db.delete("HANMUC_DANHMUC","ID_HM=?",new String[]{String.valueOf(l.getID_HM())});

            // chen lai ds dm moi
            for(int idDM : l.getListDanhMuc()){
                ContentValues cv= new ContentValues();
                cv.put("ID_HM",l.getID_HM());
                cv.put("ID_DM",idDM);
                long result = db.insert("HANMUC_DANHMUC",null,cv);
                if(result ==-1){
                    Log.e("DBHelper","Insert HANMUC_DANHMUC failed for ID_DM ="+idDM);
                    return false;
                }
            }
            db.setTransactionSuccessful();
            return true;
        }catch (Exception e){
            Log.e("DBHelper","updateLimit error",e);
            return false;
        }finally {
            db.endTransaction();
        }

    }

    // Xóa hạn mức
    public void deleteLimit(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("HANMUC_DANHMUC", "ID_HM = ?", new String[]{String.valueOf(id)});
        db.delete("HANMUC", "ID_HM = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Limit> getAllLimits() {
        List<Limit> limits = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        Cursor cursor = db.rawQuery("Select * from HANMUC", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_HM"));
                String TenHM = cursor.getString(cursor.getColumnIndexOrThrow("TenHM"));
                long sotien = cursor.getLong(cursor.getColumnIndexOrThrow("SoTien"));
                String NgayBD = cursor.getString(cursor.getColumnIndexOrThrow("NgayGD"));
                String ngayKT = cursor.getString(cursor.getColumnIndexOrThrow("NgayKetThuc"));

                List<Integer> listDM = getDMByHM(id);

                limits.add(new Limit(id, TenHM, sotien, NgayBD, ngayKT, listDM));

            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return limits;
    }
    public List<Integer> getDMByHM(int idHM) {
        List<Integer> listDM = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select ID_DM from HANMUC_DANHMUC where ID_HM =?", new String[]{String.valueOf(idHM)});
        while (cursor.moveToNext()) {
            listDM.add(cursor.getInt(0));
        }
        cursor.close();
        db.close();
        return listDM;
    }



    public Limit getLimitById(int limitId) {
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        String query = "Select * from HANMUC where ID_HM =?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limitId)});
        if (cursor != null && cursor.moveToFirst()) {
            Limit limit = new Limit();
            limit.setID_HM(cursor.getInt(cursor.getColumnIndexOrThrow("ID_HM")));
            limit.setTenHM(cursor.getString(cursor.getColumnIndexOrThrow("TenHM")));
            limit.setSoTien(cursor.getLong(cursor.getColumnIndexOrThrow("SoTien")));
            limit.setNgayGD(cursor.getString(cursor.getColumnIndexOrThrow("NgayGD")));
            limit.setNgayKetThuc(cursor.getString(cursor.getColumnIndexOrThrow("NgayKetThuc")));
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

    public ArrayList<Integer> getCategoryIdsByLimitId(int limitId) {
        ArrayList<Integer> categoryIds = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select ID_DM from HANMUC_DANHMUC where ID_HM =?", new String[]{String.valueOf(limitId)});
        if (cursor.moveToFirst()) {
            do {
                categoryIds.add(cursor.getInt(0));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return categoryIds;
    }
    public List<category> getCategoriesForLimit(int idHM) {
        List<category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT dm.ID_DM, dm.TenDM, dm.HinhAnh " +
                "FROM HANMUC_DANHMUC lc " +
                "INNER JOIN DANHMUC dm ON lc.ID_DM = dm.ID_DM " +
                "WHERE lc.ID_HM = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idHM)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_DM"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("TenDM"));
                String iconName = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"));

                category cate = new category(id, name, iconName);
                categories.add(cate);
            }
            cursor.close();
        }
        return categories;
    }

    //truy vấn các khaonr chi tiêu thuộc hạn mức

    public List<spendingsummary> getSpendingsByLimit(int limitID, String startDate, String endDate) {
        List<spendingsummary> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT dm.ID_DM AS ID_DM, dm.TenDM,dm.HinhAnh,  IFNULL(SUM(gd.SoTien), 0) AS TongChi " +
                "FROM HANMUC_DANHMUC hmdm " +
                "JOIN DANHMUC dm ON hmdm.ID_DM = dm.ID_DM " +
                "LEFT JOIN GIAODICH gd ON gd.ID_DM = dm.ID_DM " +
                "    AND gd.ThoiGian BETWEEN ? AND ? " +
                "WHERE hmdm.ID_HM = ? " +
                "GROUP BY dm.ID_DM, dm.TenDM";

        Cursor cursor = db.rawQuery(sql, new String[]{
                startDate,
                endDate,
                String.valueOf(limitID)
        });
        if (cursor != null && cursor.moveToFirst()) {
            do {
                spendingsummary summary = new spendingsummary();
                summary.setIdDM(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DM")));
                summary.setTenDM(cursor.getString(cursor.getColumnIndexOrThrow("TenDM")));
                summary.setTongChi(cursor.getLong(cursor.getColumnIndexOrThrow("TongChi")));
                summary.setHinhAnh(cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh")));
                list.add(summary);

            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
    public List<GIAODICH> getTransactionsByCategoryAndLimit(int categoryId, String startDate, String endDate, int limitId) {
        List<GIAODICH> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT gd.* FROM GIAODICH gd " +
                "INNER JOIN HANMUC_DANHMUC hm ON gd.ID_DM = hm.ID_DM " +
                "WHERE gd.ID_DM = ? AND hm.ID_HM = ? AND gd.ThoiGian BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(sql, new String[]{
                String.valueOf(categoryId),
                String.valueOf(limitId),
                startDate, endDate
        });

        if (cursor != null && cursor.moveToFirst()) {
            do {
                GIAODICH g = new GIAODICH();
                g.setID_GD(cursor.getInt(cursor.getColumnIndexOrThrow("ID_GD")));
                g.setID_DM(cursor.getInt(cursor.getColumnIndexOrThrow("ID_DM")));
                g.setSoTien(cursor.getLong(cursor.getColumnIndexOrThrow("SoTien")));
                g.setThoiGian(cursor.getString(cursor.getColumnIndexOrThrow("ThoiGian")));
                g.setGhiChu(cursor.getString(cursor.getColumnIndexOrThrow("GhiChu")));
                list.add(g);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}
