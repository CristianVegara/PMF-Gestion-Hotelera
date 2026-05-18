package com.gestionmediterraneo.hotel.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestionmediterraneo.hotel.daos.IBookingDAO;
import com.gestionmediterraneo.hotel.daos.IRoomDAO;
import com.gestionmediterraneo.hotel.entities.Room;
import com.gestionmediterraneo.hotel.enums.RoomStatus;
import com.gestionmediterraneo.hotel.enums.RoomType;

import java.time.LocalDate;


@Service
public class RoomServiceImp implements IRoomService {

    @Autowired
    private IRoomDAO roomDao;
    
    @Autowired
    private IBookingDAO bookingDao;

    @Override
    public List<Room> getAllRoomsWithDynamicPrice(LocalDate date) {
        List<Room> rooms = roomDao.findAll();
        double globalOccupancy = calculateOccupancyByDate(date, rooms.size());

        for (Room room : rooms) {
            applyPricingLogic(room, rooms, globalOccupancy, date);
        }
        return rooms;
    }
    
    @Override
    public Double getPriceByTypeAndDate(RoomType type, LocalDate date) {
        List<Room> allRooms = roomDao.findAll();
        
        Room referenceRoom = allRooms.stream()
                .filter(r -> r.getType() == type)
                .findFirst()
                .orElse(null);

        if (referenceRoom == null) return 0.0;

        double globalOccupancy = calculateOccupancyByDate(date, allRooms.size());
        double multiplier = calculateMultiplier(type, date, allRooms, globalOccupancy);

        return Math.round((referenceRoom.getPrice() * multiplier) * 100.0) / 100.0;
    }

    @Override
    public Room getRoomWithDynamicPrice(Long id, LocalDate date) {
        Room room = roomDao.findById(id).orElse(null);
        if (room == null) return null;

        List<Room> allRooms = roomDao.findAll();
        double globalOccupancy = calculateOccupancyByDate(date, allRooms.size());

        double multiplier = calculateMultiplier(room.getType(), date, allRooms, globalOccupancy);
        
        room.setDynamicPrice(Math.round((room.getPrice() * multiplier) * 100.0) / 100.0);
        return room;
    }

    private double calculateMultiplier(RoomType type, LocalDate date, List<Room> allRooms, double globalOccupancy) {
        double multiplier = 1.0;
        
        double monthFactor = Math.cos((date.getMonthValue() - 7) * Math.PI / 6); 
        multiplier += (monthFactor * 0.7) + 0.5;

        if (globalOccupancy > 0.5) {
            multiplier += (globalOccupancy - 0.5) * 0.8;
        }

        long totalOfType = allRooms.stream().filter(r -> r.getType() == type).count();
        long occupiedOfType = bookingDao.countOccupiedByDateAndType(date, type);
        double typeOccupancy = (totalOfType == 0) ? 0 : (double) occupiedOfType / totalOfType;

        if (typeOccupancy > 0.3) {
            multiplier += (typeOccupancy - 0.3) * 1.2;
        }

        return Math.max(0.7, multiplier);
    }

    private double calculateOccupancyByDate(LocalDate date, int totalRooms) {
        if (totalRooms == 0) return 0;
        long occupiedOnDate = bookingDao.countOccupiedByDate(date);
        return (double) occupiedOnDate / totalRooms;
    }

    private void applyPricingLogic(Room room, List<Room> allRooms, double globalOccupancy, LocalDate date) {
        double multiplier = 1.0;
        
        double monthFactor = Math.cos((date.getMonthValue() - 7) * Math.PI / 6); 
        multiplier += Math.max(0, monthFactor * 0.5);

        multiplier += Math.max(0, (globalOccupancy - 0.5) * 0.5);

        long totalOfType = allRooms.stream().filter(r -> r.getType() == room.getType()).count();
        long occupiedOfType = bookingDao.countOccupiedByDateAndType(date, room.getType());
        double typeOccupancy = (totalOfType == 0) ? 0 : (double) occupiedOfType / totalOfType;

        multiplier += Math.max(0, (typeOccupancy - 0.5) * 0.3);

        double finalPrice = room.getPrice() * multiplier;
        room.setDynamicPrice(Math.round(finalPrice * 100.0) / 100.0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return (List<Room>) roomDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Room findById(Long id) {
        return roomDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Room save(Room room) {
        return roomDao.save(room);
    }

    @Override
    @Transactional
    public void delete(Room room) {
        roomDao.delete(room);
    }

	@Override
	public Optional<Room> findFirstByStatusAndType(RoomStatus status, RoomType type) {
		return roomDao.findFirstByStatusAndType(status, type);
	}
}