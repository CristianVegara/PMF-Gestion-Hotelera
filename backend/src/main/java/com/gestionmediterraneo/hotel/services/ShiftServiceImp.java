package com.gestionmediterraneo.hotel.services;


import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IShiftDAO;
import com.gestionmediterraneo.hotel.entities.Shift;

@Service
public class ShiftServiceImp implements IShiftService {

    @Autowired
    private IShiftDAO shiftDao;

    @Override
    @Transactional(readOnly = true)
    public List<Shift> findAll() {
        return (List<Shift>) shiftDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shift> findShiftsByRange(LocalDate start, LocalDate end) {
        return shiftDao.findByFechaBetween(start, end);
    }

    @Override
    @Transactional
    public Shift save(Shift shift) {
        return shiftDao.save(shift);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        shiftDao.deleteById(id);
    }
}