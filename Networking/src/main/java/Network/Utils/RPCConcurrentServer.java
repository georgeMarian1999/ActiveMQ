package Network.Utils;

import Network.AMSProtocol.AMSRPCWorker;
import Network.RPCProtocol.ClientRPCWorker;
import Services.IServices;
import Services.IServicesAMS;
import Services.ServerException;

import java.net.Socket;

public class RPCConcurrentServer extends AbstractConcurrentServer {

    private IServicesAMS server;

    public RPCConcurrentServer(int port ,IServicesAMS server1){
        super(port);
        this.server=server1;
        System.out.println("Building the concurrent server");
    }

    @Override
    protected Thread createWorker(Socket client) {
        AMSRPCWorker worker=new AMSRPCWorker(server,client);
        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop() throws ServerException {
        System.out.println("Stopping server....");
        super.stop();
    }
}
