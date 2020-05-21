package server;

import Models.*;
import Service.Service;
import Services.INotificationService;
import Services.IObserver;
import Services.IServicesAMS;
import Services.ServerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServicesAMSImpl implements IServicesAMS {

    private Service service;
    private Map<String, DTOAngajat>  loggedEmployees;
    private INotificationService notificationService;
    private Iterable<DTOBJCursa> cursedisp;

    public ServicesAMSImpl(Service service1,INotificationService notificationService1){
        this.service=service1;
        this.notificationService=notificationService1;
        this.loggedEmployees=new ConcurrentHashMap<>();
        this.cursedisp=this.service.GroupByCapacitate();
    }


    @Override
    public synchronized void login(DTOAngajat angajat) throws ServerException {
        boolean isEmployee=this.service.LocalLogin(angajat.getUsername(),angajat.getPassword());
        if(isEmployee){
            if(loggedEmployees.get(angajat.getUsername())!=null){
                throw new ServerException("User is already logged in");
            }
            loggedEmployees.put(angajat.getUsername(),angajat);
        }else{
            System.out.println("Authentification failed");
            throw new ServerException("Wrong username or password");
        }
    }

    @Override
    public void logout(DTOAngajat angajat) throws ServerException {
        DTOAngajat localClient=loggedEmployees.remove(angajat.getUsername());
        if(localClient==null){
            throw new ServerException("User "+angajat.getUsername()+" is not logged in");
        }
        else System.out.println("Logout succesful for "+angajat.getUsername());
    }
    public DTOBJCursa[] convert(Iterable<DTOBJCursa> source){
        ArrayList<DTOBJCursa> result=new ArrayList<>();
        for (DTOBJCursa c : source){
            result.add(c);
        }
        return result.toArray(new DTOBJCursa[result.size()]);
    }
    @Override
    public void submitInscriere(DTOInfoSubmit infoSubmit) throws ServerException {
        System.out.println("Submitting by "+infoSubmit.getUserWho()+" ....");
        this.service.InscriereParticipant(infoSubmit.getCapacitate(),infoSubmit.getNumePart(),infoSubmit.getNumeEchipa());
        System.out.println("New submit saved in database");
        this.cursedisp=this.service.GroupByCapacitate();
        DTOBJCursa[] newCurse=convert(this.cursedisp);
        notificationService.newSubmit(newCurse);
    }

    @Override
    public DTOBJCursa[] getCurseDisp() throws ServerException {
        ArrayList<DTOBJCursa> result=new ArrayList<>();
        for(DTOBJCursa c :this.cursedisp){
            result.add(c);
        }
        return result.toArray(new DTOBJCursa[result.size()]);
    }

    @Override
    public DTOBJPartCapa[] searchByTeam(String team) throws ServerException {
        Iterable<DTOBJPartCapa> result=this.service.cautare(team);
        ArrayList<DTOBJPartCapa> ret=new ArrayList<>();
        for(DTOBJPartCapa part : result){
            ret.add(part);
        }

        return ret.toArray(new DTOBJPartCapa[ret.size()]);
    }
}
