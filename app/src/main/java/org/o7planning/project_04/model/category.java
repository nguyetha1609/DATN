package org.o7planning.project_04.model;

import java.net.CacheRequest;
import java.util.Locale;

public class category {
    private int ID_DM;
    private String TenDM;
    private String LoaiDM;
    private String HinhAnh;
    private int DMMacDinh;

    public category(int ID_DM,String TenDM,String LoaiDM, String HinhAnh,int DMMacDinh){
        this.ID_DM = ID_DM;
        this.TenDM=TenDM;
        this.LoaiDM= LoaiDM;
        this.HinhAnh=HinhAnh;
        this.DMMacDinh=DMMacDinh;
    }

    public category(int ID_DM, String tenDM, String hinhAnh) {
        this.ID_DM = ID_DM;
        TenDM = tenDM;
        HinhAnh = hinhAnh;
    }

    public category(String tenDM, String loai, String HinhAnh, int i) {
        this.TenDM=tenDM;
        this.LoaiDM= loai;
        this.HinhAnh=HinhAnh;
        this.DMMacDinh=i;
    }

    public int getID(){return ID_DM;}
    public  String getTenDM(){
        return TenDM;
    }
    public String getLoaiDM(){return LoaiDM;}
    public String getHinhAnh(){return HinhAnh;}
    public int getDMMacDinh(){return DMMacDinh;}

    public void setID_DM(int ID_DM){this.ID_DM=ID_DM;}
    public void setTenDM(String TenDM){this.TenDM=TenDM;}
    public  void setLoaiDM(String LoaiDM){this.LoaiDM=LoaiDM;}
    public void setHinhAnh(String HinhAnh){this.HinhAnh=HinhAnh;}
    public void setDMMacDinh(int DMMacDinh){this.DMMacDinh=DMMacDinh;}

}
