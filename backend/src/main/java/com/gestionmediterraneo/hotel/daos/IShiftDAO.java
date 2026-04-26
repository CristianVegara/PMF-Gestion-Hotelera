package com.gestionmediterraneo.hotel.daos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.gestionmediterraneo.hotel.entities.Shift;

public interface IShiftDAO extends CrudRepository<Shift, Long> {
	List<Shift> findByFechaBetween(LocalDate inicio, LocalDate fin);

}
