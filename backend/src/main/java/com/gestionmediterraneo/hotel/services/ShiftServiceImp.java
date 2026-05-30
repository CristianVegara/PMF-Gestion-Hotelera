package com.gestionmediterraneo.hotel.services;


import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IShiftDAO;
import com.gestionmediterraneo.hotel.entities.Shift;

/**
 * Implementation of {@link IShiftService} for managing {@link Shift} entities.
 *
 * @author Gestión Mediterráneo
 */
@Service
public class ShiftServiceImp implements IShiftService {

    @Autowired
    private IShiftDAO shiftDao;

    /** Retrieve all shifts. */
    @Override
    @Transactional(readOnly = true)
    public List<Shift> findAll() {
        return (List<Shift>) shiftDao.findAll();
    }

    /** Find shifts within a date range. */
    @Override
    @Transactional(readOnly = true)
    public List<Shift> findShiftsByRange(LocalDate start, LocalDate end) {
        return shiftDao.findByFechaBetween(start, end);
    }

    /** Save a shift. */
    @Override
    @Transactional
    public Shift save(Shift shift) {
        return shiftDao.save(shift);
    }

    /** Delete a shift by ID. */
    @Override
    @Transactional
    public void delete(Long id) {
        shiftDao.deleteById(id);
    }

	/** Find all shifts with employee and schedule eagerly fetched. */
	@Override
	public List<Shift> findAllWithDetails() {
		return shiftDao.findAllWithDetails();
	}
}
