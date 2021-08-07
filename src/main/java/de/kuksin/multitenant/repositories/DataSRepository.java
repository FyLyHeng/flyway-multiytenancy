package de.kuksin.multitenant.repositories;

import de.kuksin.multitenant.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSRepository extends JpaRepository<Car, Long> {
}
