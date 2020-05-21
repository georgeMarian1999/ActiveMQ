package client.GUI;

import Models.DTOAngajat;
import Models.DTOBJCursa;
import Models.DTOBJPartCapa;
import Models.DTOInfoSubmit;
import Notification.Notification;
import Services.IServicesAMS;
import Services.NotificationReceiver;
import Services.NotificationSubscriber;
import Services.ServerException;
//CLASS NOT USED
public class Controller implements NotificationSubscriber {

    //private RacesTable racesTable;
    private DTOAngajat crtAngajat;
    private IServicesAMS server;
    private NotificationReceiver receiver;

    public Controller(IServicesAMS servicesAMS){
        this.server=servicesAMS;
    }


    public void login(DTOAngajat angajat)throws ServerException{
        //DTOAngajat angajat=new DTOAngajat(username,password);
        server.login(angajat);
        this.crtAngajat=angajat;
        receiver.start(this);
    }
    public void logout(){
        try {
            server.logout(crtAngajat);
        } catch (ServerException e) {
            System.out.println("Logout error "+e);
        }finally {
            receiver.stop();
        }
    }
    public DTOBJCursa[] getCurse()throws ServerException{
        return server.getCurseDisp();
    }
    public DTOBJPartCapa[] getSearchResult(String team)throws ServerException{
        return server.searchByTeam(team);
    }
    public void submit(DTOInfoSubmit infoSubmit)throws ServerException{
        server.submitInscriere(infoSubmit);
    }

    public void setReceiver(NotificationReceiver receiver) {
        this.receiver = receiver;
    }

    //public RacesTable getRacesTable() {
       // return racesTable;
    //}

    @Override
    public void notificationReceived(Notification notification) {

    }
}
