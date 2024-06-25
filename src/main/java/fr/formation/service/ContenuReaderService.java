package fr.formation.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.formation.dto.ContenuDto;
@Service
public class ContenuReaderService {

    private static final Logger log = LoggerFactory.getLogger(ContenuReaderService.class);

    // méthode pour lire tous les fichiers dans un répertoire
    public List<ContenuDto> readAllFilesInDirectory(String directoryPath) {

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        List<ContenuDto> allWinds = new ArrayList<>();

        log.debug("Lecture de tous les fichiers dans le répertoire {} ...", directoryPath);

        for (File file : files) {
            if (file.isFile()) {
                log.debug("Lecture du fichier {} ...", file.getName());
                allWinds.addAll(read(file.getAbsolutePath()));
            }
        }
        log.debug("Tous les fichiers dans le répertoire {} ont été lus.", directoryPath);
        return allWinds;
    }


    public List<ContenuDto> read(String filename) {
        List<ContenuDto> winds = new ArrayList<>();

        log.debug("Ouverture du fichier {} ...", filename);

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            //int index = 0;

            while ((line = br.readLine()) != null) {
                // En-tête : 
                /*
                 if (index++ < 1) { // On est dans l'en-tête, donc on ignore
                    continue; // Boucler directement
                } 
                    */
                String[] infos = line.split(":");
                ContenuDto dto = new ContenuDto();

                dto.setMot(infos[0]);
      
                winds.add(dto);
            }
            //log.debug("{} solar winds processed!", index);
            log.debug("{} mots de passe traités!", winds.size());
        }
        catch (Exception ex) {
            log.error("Erreur pendant la lecture du fichier {}...", filename);
        }
        return winds;
    }

    

}
