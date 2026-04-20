package com.gestionmediterraneo.hotel.daos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionmediterraneo.hotel.entities.Activity;

public interface IActivityDAO extends JpaRepository<Activity, Long> {
	List<Activity> findByFechaComienzoBetween(LocalDateTime inicio, LocalDateTime fin);
}
