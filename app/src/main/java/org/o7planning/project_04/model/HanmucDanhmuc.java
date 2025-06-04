package org.o7planning.project_04.model;

public class HanmucDanhmuc {
    private  int ID;
    private int ID_HM;
    private int ID_DM;

    public HanmucDanhmuc(int ID, int ID_HM, int ID_DM) {
        this.ID = ID;
        this.ID_HM = ID_HM;
        this.ID_DM = ID_DM;
    }

    public HanmucDanhmuc() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID_HM() {
        return ID_HM;
    }

    public void setID_HM(int ID_HM) {
        this.ID_HM = ID_HM;
    }

    public int getID_DM() {
        return ID_DM;
    }

    public void setID_DM(int ID_DM) {
        this.ID_DM = ID_DM;
    }
}
