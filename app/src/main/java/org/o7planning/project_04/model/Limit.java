package org.o7planning.project_04.model;

import java.util.List;

public class Limit {
    private int ID_HM;
    private String TenHM;
    private long SoTien;
    private String NgayGD;
    private  String NgayKetThuc;
    private List<Integer> listDanhMuc;

    public Limit(){}

    public Limit(int ID_HM, String tenHM, long soTien, String ngayGD, String ngayKetThuc, List<Integer> listDanhMuc) {
        this.ID_HM = ID_HM;
        TenHM = tenHM;
        SoTien = soTien;
        NgayGD = ngayGD;
        NgayKetThuc = ngayKetThuc;
        this.listDanhMuc = listDanhMuc;
    }

    public List<Integer> getListDanhMuc() {
        return listDanhMuc;
    }

    public void setListDanhMuc(List<Integer> listDanhMuc) {
        this.listDanhMuc = listDanhMuc;
    }

    public int getID_HM() {
        return ID_HM;
    }

    public void setID_HM(int ID_HM) {
        this.ID_HM = ID_HM;
    }

    public String getTenHM() {
        return TenHM;
    }

    public void setTenHM(String tenHM) {
        TenHM = tenHM;
    }



    public long getSoTien() {
        return SoTien;
    }

    public void setSoTien(long soTien) {
        SoTien = soTien;
    }

    public String getNgayGD() {
        return NgayGD;
    }

    public void setNgayGD(String ngayGD) {
        NgayGD = ngayGD;
    }

    public String getNgayKetThuc() {
        return NgayKetThuc;
    }

    public void setNgayKetThuc(String ngayKetThuc) {
        NgayKetThuc = ngayKetThuc;
    }
}
