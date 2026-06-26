package com.esprit.microservice.driverclient.config;

import com.esprit.microservice.driverclient.model.*;
import com.esprit.microservice.driverclient.repository.ClientRepository;
import com.esprit.microservice.driverclient.repository.DriverRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seedData(DriverRepository driverRepository, ClientRepository clientRepository) {
        return args -> {
            if (driverRepository.count() == 0) {
                driverRepository.save(buildDriver(
                        "Karim", "Ben Ali", "karim.benali@deliverx.tn",
                        "+216 20 111 222", "DL-2024-001",
                        DriverStatus.AVAILABLE, 1L));

                driverRepository.save(buildDriver(
                        "Sami", "Trabelsi", "sami.trabelsi@deliverx.tn",
                        "+216 22 333 444", "DL-2024-002",
                        DriverStatus.ON_DELIVERY, 2L));

                driverRepository.save(buildDriver(
                        "Amine", "Gharbi", "amine.gharbi@deliverx.tn",
                        "+216 98 555 666", "DL-2024-003",
                        DriverStatus.OFF_DUTY, null));
            }

            if (clientRepository.count() == 0) {
                clientRepository.save(buildClient(
                        "Leila", "Mansouri", "leila.mansouri@gmail.com",
                        "+216 50 777 888", null,
                        "12 Avenue Habib Bourguiba", "Tunis",
                        ClientType.INDIVIDUAL, ClientStatus.ACTIVE));

                clientRepository.save(buildClient(
                        "Mohamed", "Jebali", "contact@techstore.tn",
                        "+216 71 999 000", "TechStore SARL",
                        "Zone Industrielle Charguia", "Ariana",
                        ClientType.BUSINESS, ClientStatus.ACTIVE));

                clientRepository.save(buildClient(
                        "Nadia", "Saidi", "nadia.saidi@yahoo.fr",
                        "+216 55 123 456", null,
                        "45 Rue de la Liberté", "Sfax",
                        ClientType.INDIVIDUAL, ClientStatus.INACTIVE));
            }
        };
    }

    private Driver buildDriver(String firstName, String lastName, String email,
                               String phone, String licenseNumber,
                               DriverStatus status, Long vehicleId) {
        Driver driver = new Driver();
        driver.setFirstName(firstName);
        driver.setLastName(lastName);
        driver.setEmail(email);
        driver.setPhone(phone);
        driver.setLicenseNumber(licenseNumber);
        driver.setStatus(status);
        driver.setVehicleId(vehicleId);
        return driver;
    }

    private Client buildClient(String firstName, String lastName, String email,
                               String phone, String companyName,
                               String address, String city,
                               ClientType type, ClientStatus status) {
        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        client.setPhone(phone);
        client.setCompanyName(companyName);
        client.setAddress(address);
        client.setCity(city);
        client.setType(type);
        client.setStatus(status);
        return client;
    }
}
