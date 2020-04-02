package Network.RPCProtocol;

import Models.*;
import Services.IObserver;
import Services.IServices;
import Services.ServerException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientServicesProxy implements IServices {

    private String host;
    private int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> responses;
    private volatile boolean finished=true;

    public ClientServicesProxy(String host,int port){
        this.host=host;
        this.port=port;
        responses=new LinkedBlockingQueue<Response>();
    }

    @Override
    public void login(DTOAngajat angajat, IObserver client) throws ServerException {
        initializeconnection();
        Request request=new Request.Builder().type(RequestType.LOGIN).data(angajat).build();
        sendRequest(request);
        Response response=readResponse();
        if(response.type()==ResponseType.OK){
            this.client=client;
            return;
        }
        if(response.type()==ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            throw new ServerException("Eroare raspuns login "+err);
        }
    }

    @Override
    public void logout(DTOAngajat angajat, IObserver client) throws ServerException {
        Request request=new Request.Builder().type(RequestType.LOGOUT).data(angajat).build();
        sendRequest(request);
        Response response=readResponse();
        closeConnection();
        if(response.type()==ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServerException("Eroare raspuns logout "+err);
        }
    }

    @Override
    public void submitInscriere(DTOInfoSubmit infoSubmit) throws ServerException {
        System.out.println("Se apeleaza submitInscriere din ClientServicesProxy");
        System.out.println(infoSubmit.getUserWho()+" submitted ");
        Request request=new Request.Builder().type(RequestType.SUBMIT_INSC).data(infoSubmit).build();
        System.out.println("Sending submit request "+request);
        sendRequest(request);
        System.out.println("Awaiting response");
        Response response=readResponse();
        System.out.println("Response received"+response);
        if(response.type()==ResponseType.OK){
            System.out.println("Succesfully submitted");
        }
        if(response.type()==ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServerException("Eroare raspuns submit "+err);
        }
    }

    @Override
    public Angajat[] getLoggedEmployees() throws ServerException {
        Request request=new Request.Builder().type(RequestType.GET_LOGGED_EMPLOYEES).build();
        sendRequest(request);
        Response response=readResponse();
        if(response.type()==ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServerException(err);
        }
        return (Angajat[])response.data();
    }

    @Override
    public DTOBJCursa[] getCurseDisp() throws ServerException {
        Request request=new Request.Builder().type(RequestType.GET_CURRENT_CURSE).build();
        sendRequest(request);
        Response response=readResponse();
        if(response.type()==ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServerException("Error getting all Cursa"+err);
        }

        return (DTOBJCursa[]) response.data();
    }

    @Override
    public DTOBJPartCapa[] searchByTeam(String team) throws ServerException {
        Request request=new Request.Builder().type(RequestType.SEARCH_BY_TEAM).data(team).build();
        sendRequest(request);
        Response response=readResponse();
        if(response.type()==ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServerException("Error searching by team"+err);
        }
        return (DTOBJPartCapa[])response.data();
    }

    private Response readResponse() throws ServerException{
        Response response=null;
        try{
            response=responses.take();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return response;
    }
    private void sendRequest(Request request) throws ServerException {
        try{
            output.writeObject(request);
            output.flush();
        }catch (IOException e){
            throw new ServerException("Error sending object "+e);
        }
    }
    private void closeConnection(){
        finished=true;
        try{
            output.close();
            input.close();
            connection.close();
            client=null;
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void initializeconnection()throws ServerException{
        try{
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }
    private class ReaderThread implements Runnable

    {

        @Override
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{

                        try {
                            responses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }

    private void handleUpdate(Response response) {
        if(response.type()==ResponseType.EMPLOYEE_LOGGED_IN){
            DTOAngajat employee=(DTOAngajat)response.data();
            System.out.println("Employee logged in "+employee);
            try{
                client.AngajatLoggedIn(employee);
            }catch (ServerException e){
                e.printStackTrace();
            }
        }
        if(response.type()==ResponseType.EMPLOYEE_LOGGED_OUT){
            DTOAngajat employee=(DTOAngajat)response.data();
            System.out.println("Employee logged out "+employee);
            try{
                client.AngajatLoggedOut(employee);
            }catch (ServerException e){
                e.printStackTrace();
            }
        }
        if(response.type()==ResponseType.NEW_SUBMIT){
            DTOBJCursa[] cursa=(DTOBJCursa[]) response.data();
            System.out.println("Employee submitted from handleUpdate ClientSerivcesProxy");
            try{
                client.AngajatSubmitted(cursa);
            }catch (ServerException e){
                e.printStackTrace();
            }
        }

    }

    private boolean isUpdate(Response response) {
        return response.type()==ResponseType.EMPLOYEE_LOGGED_IN || response.type()==ResponseType.EMPLOYEE_LOGGED_OUT || response.type()==ResponseType.NEW_SUBMIT;
    }
}
