package org.o7planning.project_04.model;

import java.util.List;

import javax.xml.transform.sax.SAXResult;

public class SpendingGroup {
    private int idDM;
    private String tnDM;
    private long tongChi;
    private String iconName;
    private List<GIAODICH> giaodichList;
    private boolean isExpanded = false;

    public SpendingGroup(int idDM, String tnDM, long tongChi, String iconName, List<GIAODICH> giaodichList) {
        this.idDM = idDM;
        this.tnDM = tnDM;
        this.tongChi = tongChi;
        this.iconName = iconName;
        this.giaodichList = giaodichList;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getIdDM() {
        return idDM;
    }

    public void setIdDM(int idDM) {
        this.idDM = idDM;
    }

    public String getTnDM() {
        return tnDM;
    }

    public void setTnDM(String tnDM) {
        this.tnDM = tnDM;
    }

    public long getTongChi() {
        return tongChi;
    }

    public void setTongChi(long tongChi) {
        this.tongChi = tongChi;
    }

    public List<GIAODICH> getGiaodichList() {
        return giaodichList;
    }

    public void setGiaodichList(List<GIAODICH> giaodichList) {
        this.giaodichList = giaodichList;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
