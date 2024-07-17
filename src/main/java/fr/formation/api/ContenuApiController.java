package fr.formation.api;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.formation.dto.ContenuDto;
import fr.formation.service.ContenuReaderService;

@RestController
@RequestMapping("/api/contenu")
public class ContenuApiController {

    private static final Logger log = LoggerFactory.getLogger(ContenuApiController.class);

    @Autowired
    private ContenuReaderService readerService;
   
    @PostMapping("/read-and-save")
    public void readAndSave() {
     
        try (Connection connection = DriverManager.getConnection("jdbc:clickhouse://localhost:8123/contenu", "default", "")) {
            connection.setAutoCommit(false);
 
            // la lecture de tous les fichiers dans le répertoire
            List<ContenuDto> allWinds = this.readerService.readAllFilesInDirectory("C:/Users/hedib/ProjetFinal_GestionSecu/pwnedpasswords");
            this.saveAll(connection, allWinds);

        }
        catch (Exception ex) {
            ex.printStackTrace();
            log.error("Impossible de se connecter ...");
        }
    }
      
    //elle accepte une liste de ContenuDto
    private void saveAll(Connection connection, List<ContenuDto> allWinds) {
        //List<ContenuDto> winds = this.readerService.read("C:/Users/hedib/ProjetFinal_GestionSecu/pwnedpasswords/00000.txt");

        // Récupérer un Statement pour exécuter la requête (un PreparedStatement est encore mieux !)
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO contenu (mot) VALUES (?)")) {
            int batchIndex = 0;
            
            //for (ContenuDto wind : winds) {
            for (ContenuDto wind : allWinds) {
                statement.setString(1, wind.getMot());
                
                statement.addBatch();

                if (batchIndex == 100_000) {
                    statement.executeBatch();
                    connection.commit();
                    batchIndex = -1;
                }
                batchIndex++;
            }
            statement.executeBatch();
            connection.commit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            log.error("Problème dans la requête ...");
        }
    }
}
