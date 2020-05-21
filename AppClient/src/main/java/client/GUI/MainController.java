package client.GUI;

import Models.*;
import Notification.Notification;
import Services.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable,NotificationSubscriber {

    ObservableList<DTOBJCursa> Curse= FXCollections.observableArrayList();
    ObservableList<DTOBJPartCapa> Participanti=FXCollections.observableArrayList();
    //ObservableList<Integer> tipuri=FXCollections.observableArrayList();

    private DTOAngajat crtAngajat;
    //private Controller controller;
    private IServicesAMS server;
    private NotificationReceiver receiver;

    @FXML
    Button LogoutButton;
    @FXML
    TableView<DTOBJCursa> TabelCurse;
    @FXML
    TableColumn<DTOBJCursa,Integer> IdCol;
    @FXML
    TableColumn<DTOBJCursa,Integer> CapCol;
    @FXML
    TableColumn<DTOBJCursa,Integer> NrCol;
    @FXML
    Button clearsearch;
    @FXML
    TextField TFSearch;
    @FXML
    Button SearchByTeam;
    @FXML
    TableView<DTOBJPartCapa> TabelPart;
    @FXML
    TableColumn<DTOBJPartCapa,Integer> IDPart;
    @FXML
    TableColumn<DTOBJPartCapa,Integer> NumePart;
    @FXML
    TableColumn<DTOBJPartCapa,Integer> CapacitatePart;

    @FXML
    Button InscButton;
    @FXML
    ComboBox<Integer> CapBox;
    @FXML
    TextField TFNumePart;
    @FXML
    TextField TFNumeEchipa;




    public void setServer(IServicesAMS server) {
        this.server = server;
    }

    public void setReceiver(NotificationReceiver receiver) {
        this.receiver = receiver;
    }
    public MainController(IServicesAMS servicesAMS){
        this.server=servicesAMS;
    }
    public void login(DTOAngajat angajat)throws ServerException{
        this.server.login(angajat);
        this.crtAngajat=angajat;
        startReceiver();
    }
    public void startReceiver(){
        this.receiver.start(this);
    }

    public void setUser(DTOAngajat crtAngajat){
        this.crtAngajat=crtAngajat;
    }

    @FXML
    public void handleclear(){
        TFSearch.setText("");
        TFNumePart.setText("");
        TFNumeEchipa.setText("");

    }

    public MainController(){

    }
    public void AngajatSubmitted(DTOBJCursa[] result) throws ServerException {
        Platform.runLater(()->{
            System.out.println("S-a apelat AngajatSubmitted din MainCtrl");
            AddNewDataCurse(result);
        });
    }
    @FXML
    public void handlesubmit(){
        try{
            Inscriere();
        }catch (ServerException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Submit failure");
            alert.setContentText("An error occured :"+e.getMessage());
            alert.showAndWait();
        }
    }
    public void AddNewDataCurse(DTOBJCursa [] source){
        this.Curse.clear();
        for(DTOBJCursa c:source){
            this.Curse.add(c);
        }
        this.TabelCurse.setItems(this.Curse);
    }
    @FXML
    public void handleLogout(ActionEvent actionEvent){
        logout();
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();


    }
    @FXML
    public void handlesearch(){
        try{
            this.TabelPart.getItems().clear();
            String team=TFSearch.getText();
            DTOBJPartCapa[] result=this.server.searchByTeam(team);
            for(DTOBJPartCapa part : result){
                Participanti.add(part);
            }
            this.TabelPart.setItems(Participanti);
        }catch (ServerException e){
            System.out.println("Error when searching "+e);
        }
    }
    public void logout() {
            try {
                server.logout(crtAngajat);
            }catch (ServerException e){
                System.out.println("Error when loggin out"+e);
            }
    }

    public void Inscriere()throws ServerException{
        System.out.println("se apeleaza Inscriere");
        String numePart=TFNumePart.getText();
        String numeEchipa=TFNumeEchipa.getText();
        int capacitate= CapBox.getValue();
        System.out.println(numePart+" "+numeEchipa+" "+capacitate);
        DTOInfoSubmit submit=new DTOInfoSubmit(crtAngajat,capacitate,numePart,numeEchipa);
        this.server.submitInscriere(submit);


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
    public void initialiazeTabels(){
        IdCol.setCellValueFactory(new PropertyValueFactory<DTOBJCursa,Integer>("id"));
        CapCol.setCellValueFactory(new PropertyValueFactory<DTOBJCursa,Integer>("capacitate"));
        NrCol.setCellValueFactory(new PropertyValueFactory<DTOBJCursa,Integer>("Nrinscrisi"));
        IDPart.setCellValueFactory(new PropertyValueFactory<DTOBJPartCapa,Integer>("id"));
        NumePart.setCellValueFactory(new PropertyValueFactory<DTOBJPartCapa,Integer>("Nume"));
        CapacitatePart.setCellValueFactory(new PropertyValueFactory<DTOBJPartCapa,Integer>("capactiate"));
    }
    public void setCurseTabel() {


                try{
                    this.TabelCurse.getItems().clear();
                    DTOBJCursa[] curse=this.server.getCurseDisp();
                    for(DTOBJCursa c:curse){
                        Curse.add(c);
                    }
                    TabelCurse.setItems(Curse);
                }catch (ServerException e){
                    System.out.println("Error when setting CurseTabel"+e);
                }
            }



    public void setComboBox() {
        ObservableList<Integer> tipuriCurse=FXCollections.observableArrayList();
        for(DTOBJCursa c : this.Curse){
            tipuriCurse.add(c.getCapacitate());
        }
        CapBox.setItems(tipuriCurse);
    }

    @Override
    public void notificationReceived(Notification notification) {
        try{
            AngajatSubmitted(notification.getData());
        }catch (ServerException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Refreshing failure");
            alert.setContentText("An error occured :"+e.getMessage());
            alert.showAndWait();
        }

    }
}
