package org.o7planning.project_04.model;

public class GIAODICH {
    private int ID_GD;
   private int ID_DM;
   private long SoTien;
   private String ThoiGian;
   private String GhiChu;

    public GIAODICH(int ID_GD, int ID_DM, long soTien, String ThoiGian, String ghiChu) {
        this.ID_GD = ID_GD;
        this.ID_DM = ID_DM;
        SoTien = soTien;
        ThoiGian = ThoiGian;
        GhiChu = ghiChu;
    }

    public GIAODICH() {
    }

    public int getID_GD() {
        return ID_GD;
    }

    public void setID_GD(int ID_GD) {
        this.ID_GD = ID_GD;
    }

    public int getID_DM() {
        return ID_DM;
    }

    public void setID_DM(int ID_DM) {
        this.ID_DM = ID_DM;
    }

    public long getSoTien() {
        return SoTien;
    }

    public void setSoTien(long soTien) {
        SoTien = soTien;
    }

    public String getThoiGian() {
        return ThoiGian;
    }

    public void setThoiGian(String ThoiGian) {
        ThoiGian = ThoiGian;
    }

    public String getGhiChu() {
        return GhiChu;
    }

    public void setGhiChu(String ghiChu) {
        GhiChu = ghiChu;
    }
}
