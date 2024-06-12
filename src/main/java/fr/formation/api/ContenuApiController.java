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

    
    @PostMapping
    public void readAndSave() {
    	//try (Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/solarwind", "postgres", "root")) {
        //try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3310/solarwind", "root", "root")) {
        
        try (Connection connection = DriverManager.getConnection("jdbc:clickhouse://localhost:8123/contenu", "default", "")) {
            connection.setAutoCommit(false);

            for (int i = 1; i <= 12; i++) {
                this.readAndSave(connection, i);
            }
        }

        catch (Exception ex) {
            ex.printStackTrace();
            log.error("Impossible de se connecter ...");
        }
    }
    
    
    private void readAndSave(Connection connection, int mois) {
        List<ContenuDto> winds = this.readerService.read("D:/SSDStock/Edouard/Documents/cours/Formation Ajc/Projet Gestionnaire Contenu securise/pwnedpasswords/pwnedpasswords/00000.txt");

        // Récupérer un Statement pour exécuter la requête (un PreparedStatement est encore mieux !)
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO contenu (mot) VALUES (?)")) {
            int batchIndex = 0;
            
            for (ContenuDto wind : winds) {
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
