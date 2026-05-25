package com.gestionmediterraneo.hotel.services;

import java.time.LocalDate;
import java.util.List;

import com.gestionmediterraneo.hotel.entities.Shift;

public interface IShiftService {
    public List<Shift> findAll();
    public List<Shift> findShiftsByRange(LocalDate start, LocalDate end);
    public Shift save(Shift shift);
    public void delete(Long id);
    List<Shift> findAllWithDetails();
}