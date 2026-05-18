import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Rooms.css';

const Rooms = () => {
  const [rooms, setRooms] = useState([]);
  const navigate = useNavigate();

  const fetchRooms = () => {
    fetch('http://localhost:8080/api/rooms')
      .then(res => res.json())
      .then(data => setRooms(data))
      .catch(err => console.error("Error cargando habitaciones:", err));
  };

  const fetchRoomBookings = (roomId) => {
    if (!roomId) return;
    fetch(`http://localhost:8080/api/bookings/room/${roomId}`)
      .then(res => res.json())
      .then(data => setRooms(data));
  }, []);

  return (
    <div className="rooms-page-wrapper">
      <div className="rooms-container">
        <div className="header-actions">
          <h2 className="title-list">Estado de Habitaciones ({rooms.length})</h2>
        </div>

        <div className="rooms-grid">
          {rooms.map(room => (
            <div 
              key={room.id} 
              className={`room-card ${room.status.toLowerCase()}`}
              onClick={() => navigate(`/rooms/${room.id}`)} // Redirige al detalle
            >
              <div className="room-card-number">{room.number}</div>
              <div className="room-card-type">{room.type}</div>
              <div className="room-card-status">{room.status}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Rooms;