package Network.AMSProtocol;

import Models.*;
import Network.RPCProtocol.Request;
import Network.RPCProtocol.Response;
import Network.RPCProtocol.ResponseType;
import Services.IServices;
import Services.IServicesAMS;
import Services.ServerException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class AMSRPCWorker implements Runnable {
    private IServicesAMS server;
    private Socket connection;



    private ObjectOutputStream output;
    private ObjectInputStream input;
    private volatile boolean connected;

    private static Response OK=new Response.Builder().type(ResponseType.OK).build();

    public AMSRPCWorker(IServicesAMS server1,Socket conn){
        this.server=server1;
        this.connection=conn;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void SendResponse(Response response) throws IOException{
        System.out.println("Sending response "+response);
        output.writeObject(response);
        output.flush();
    }

    private Response handleRequest(Request request){
        Response response=null;
        /*if(request.getType()==RequestType.LOGIN){
            DTOAngajat angajat=(DTOAngajat)request.getData();
            System.out.println("Login request by "+angajat);
            try{
                server.login(angajat,this);
                return OK;
            }catch (ServerException e){

                connected=false;
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if(request.getType()==RequestType.LOGOUT){
            DTOAngajat angajat=(DTOAngajat)request.getData();
            System.out.println("Logout request by "+angajat);
            try{
                server.logout(angajat,this);
                connected=false;
                return OK;
            }catch (ServerException e){
                //e.printStackTrace();
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if(request.getType()==RequestType.GET_LOGGED_EMPLOYEES){
            System.out.println("Get logged employees request");
            try{
                Angajat[] angajats=server.getLoggedEmployees();
                response=new Response.Builder().type(ResponseType.GET_LOGGED_EMPLOYEE).data(angajats).build();
                return response;
            }catch (ServerException e){
                //e.printStackTrace();
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if(request.getType()==RequestType.GET_CURRENT_CURSE){
            System.out.println("Get current curse request");
            try{
                DTOBJCursa[] curse=server.getCurseDisp();
                return new Response.Builder().type(ResponseType.GET_DISP_CURSE).data(curse).build();
            }catch (ServerException e){
                //e.printStackTrace();
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if(request.getType()==RequestType.SEARCH_BY_TEAM){
            System.out.println("Search by team requested");
            try{
                String team=(String)request.getData();
                DTOBJPartCapa[] partCap=server.searchByTeam(team);
                return new Response.Builder().type(ResponseType.GET_SEARCH_RESULT).data(partCap).build();
            }catch (ServerException e){
                //e.printStackTrace();
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if(request.getType()==RequestType.SUBMIT_INSC){
            System.out.println("Submit requested");
            try{
                DTOInfoSubmit infoSubmit=(DTOInfoSubmit)request.getData();
                server.submitInscriere(infoSubmit);
                return new Response.Builder().type(ResponseType.OK).data(infoSubmit.getWho()).build();
            }catch (ServerException e){
                e.printStackTrace();
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }*/
        String handlerName="handle"+(request).getType();
        System.out.println("HandlerName "+handlerName);
        try {
            Method method=this.getClass().getDeclaredMethod(handlerName, Request.class);
            response=(Response)method.invoke(this,request);
            System.out.println("Method "+handlerName+ " invoked");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return response;
    }
    private Response handleLOGIN(Request request){
        DTOAngajat angajat=(DTOAngajat)request.getData();
        System.out.println("Login request by "+angajat);
        try{
            server.login(angajat);
            return OK;
        }catch (ServerException e){
            connected=false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleLOGOUT(Request request){
        DTOAngajat angajat=(DTOAngajat)request.getData();
        System.out.println("Logout request by "+angajat);
        try{
            server.logout(angajat);
            connected=false;
            return OK;
        }catch (ServerException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleGET_CURRENT_CURSE(Request request){
        System.out.println("Get current curse request");
        try{
            DTOBJCursa[] curse=server.getCurseDisp();
            return new Response.Builder().type(ResponseType.GET_DISP_CURSE).data(curse).build();
        }catch (ServerException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleSEARCH_BY_TEAM(Request request){
        System.out.println("Search by team requested");
        try{
            String team=(String)request.getData();
            DTOBJPartCapa[] partCap=server.searchByTeam(team);
            return new Response.Builder().type(ResponseType.GET_SEARCH_RESULT).data(partCap).build();
        }catch (ServerException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleSUBMIT_INSC(Request request){
        System.out.println("Submit requested");
        try{
            DTOInfoSubmit infoSubmit=(DTOInfoSubmit)request.getData();
            server.submitInscriere(infoSubmit);
            return OK;
        }catch (ServerException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    @Override
    public void run() {
        while (connected){
            try{
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if(response!=null){
                    SendResponse(response);
                }
            }catch(IOException e){
                e.printStackTrace();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        try{
            input.close();
            output.close();
            connection.close();
        }catch(IOException e){
            System.out.println("Error "+e);
            //e.printStackTrace();
        }

    }
}
