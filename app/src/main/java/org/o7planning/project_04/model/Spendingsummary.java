package org.o7planning.project_04.model;

public class Spendingsummary {
    private int idDM;
    private String tenDM;
    private  long TongChi;
    private String HinhAnh;

    public Spendingsummary(int idDM, String tenDM, long tongChi, String hinhAnh) {
        this.idDM = idDM;
        this.tenDM = tenDM;
        TongChi = tongChi;
        HinhAnh = hinhAnh;
    }

    public String getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        HinhAnh = hinhAnh;
    }

    public Spendingsummary() {
    }

    public int getIdDM() {
        return idDM;
    }

    public void setIdDM(int idDM) {
        this.idDM = idDM;
    }

    public String getTenDM() {
        return tenDM;
    }

    public void setTenDM(String tenDM) {
        this.tenDM = tenDM;
    }

    public long getTongChi() {
        return TongChi;
    }

    public void setTongChi(long tongChi) {
        TongChi = tongChi;
    }
}
