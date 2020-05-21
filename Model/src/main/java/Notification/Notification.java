package Notification;

import Models.DTOBJCursa;

public class Notification {
    private NotificationType type;
    private DTOBJCursa[] data;

    public Notification(){

    }
    public Notification(NotificationType t,DTOBJCursa[] curse){
        this.type=t;
        this.data=curse;
    }

    public DTOBJCursa[] getData() {
        return data;
    }

    public NotificationType getType() {
        return type;
    }

    public void setData(DTOBJCursa[] data) {
        this.data = data;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
