package Services;

import Models.DTOAngajat;
import Models.DTOBJCursa;
import Models.DTOBJPartCapa;
import Models.DTOInfoSubmit;

public interface IServicesAMS {
    void login(DTOAngajat angajat) throws ServerException;
    void logout(DTOAngajat angajat) throws ServerException;
    void submitInscriere(DTOInfoSubmit infoSubmit) throws ServerException;
    DTOBJCursa[] getCurseDisp() throws ServerException;
    DTOBJPartCapa[] searchByTeam(String team) throws ServerException;
}
