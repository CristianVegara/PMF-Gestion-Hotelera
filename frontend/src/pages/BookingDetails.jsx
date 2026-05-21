import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import './BookingDetails.css';

const token = localStorage.getItem('user_token');


const BookingDetails = () => {
  const { id } = useParams();
  
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  
  const roomTypesOrder = ['INDIVIDUAL', 'DOBLE', 'SUITE'];
  
  const roomStatusMapping = {
    'LIBRE': 0,
    'OCUPADA': 1,
    'SUCIA': 2
  };
  
  const bookingStatusMapping = {
    'PENDIENTE': 0,
    'CONFIRMADA': 1,
    'CANCELADA': 2
  };
  
  const checkInStatusMapping = {
    'PENDIENTE': 0,
    'DENTRO': 1,
    'FUERA': 2
  };
  
  useEffect(() => {
    fetchBooking();
  }, [id]);
  
  const fetchBooking = async () => {
    try {
      setLoading(true);
      const response = await fetch(`http://localhost:8080/api/bookings/${id}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });      if (!response.ok) throw new Error('No se pudo obtener la reserva');
      const data = await response.json();
      setBooking(data);
    } catch (error) {
      console.error('Error cargando la reserva:', error);
    } finally {
      setLoading(false);
    }
  };
  
  const handleCheckIn = async () => {
    const confirmar = window.confirm('¿Está seguro de que desea realizar el Check-In y dar entrada al cliente?');
    if (!confirmar) return;
    
    try {
      let currentIndex = roomTypesOrder.indexOf(booking.roomType);  
      let room;
      
      for (let i = 0; i < roomTypesOrder.length; i++) {
        const currentTypeToCheck = roomTypesOrder[currentIndex];
        const freeRoomResponse = await fetch(`http://localhost:8080/api/rooms/free/${currentTypeToCheck}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });        
        if (freeRoomResponse.status === 200) {         
          room = await freeRoomResponse.json();
          break;
        }
        
        currentIndex = currentIndex === roomTypesOrder.length - 1 ? 0 : currentIndex + 1;
      }
      
      if(room == null){
        window.alert("No hay habitaciones disponibles en el hotel por el momento");
        return;
      }
      
      if(room.type !== booking.roomType){
        const confirmarCambio = window.confirm("El tipo de habitación no está disponible, se le cambiará a una habitación del tipo " + booking.roomType)
        if(!confirmarCambio) return;
      }      
      
      const roomToUpdate = {
        id: room.id,
        number: room.number,
        type: roomTypesOrder.indexOf(room.type) !== -1 ? roomTypesOrder.indexOf(room.type) : room.type,
        price: room.price,
        status: roomStatusMapping['OCUPADA']
      };
      
      const updateRoomResponse = await fetch(`http://localhost:8080/api/rooms/${room.id}`, {
        method: 'PUT',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(roomToUpdate)
      });
      
      if (!updateRoomResponse.ok) 
        throw new Error('Error al actualizar la habitación');
      
      const roomData = await updateRoomResponse.json();
      const roomUpdated = roomData.room || roomData;
      
      const bookingToUpdate = {
        ...booking,
        habitacion: {
          ...roomUpdated,
          type: roomTypesOrder.indexOf(roomUpdated.type) !== -1 ? roomTypesOrder.indexOf(roomUpdated.type) : roomUpdated.type,
          status: typeof roomUpdated.status === 'string' ? roomStatusMapping[roomUpdated.status] : roomUpdated.status
        },
        roomType: roomTypesOrder.indexOf(booking.roomType) !== -1 ? roomTypesOrder.indexOf(booking.roomType) : booking.roomType,
        estado: bookingStatusMapping[booking.estado || 'CONFIRMADA'],
        checkInStatus: checkInStatusMapping['DENTRO']
      };
      
      const updateBookingResponse = await fetch(`http://localhost:8080/api/bookings/${booking.id}`, {
        method: 'PUT',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(bookingToUpdate)
      });
      
      if (updateBookingResponse.ok) {
        const updatedData = await updateBookingResponse.json();
        setBooking(updatedData);
        alert(`Check-In exitoso. Se asignó la habitación ${roomUpdated.number}.`);
      } else {
        throw new Error('Error al actualizar la reserva');
      }
      
    } catch (error) {
      console.error(error);
      alert('Hubo un error al procesar el Check-In');
    }
  };
  
  const handleCheckOut = async () => {
    const confirmar = window.confirm('¿Está seguro de que desea realizar el Check-Out y dar salida al cliente?');
    if (!confirmar) return;
    
    try {
      const hoy = new Date().toISOString().split('T')[0];
      
      const roomToUpdate = {
        id: booking.habitacion.id,
        number: booking.habitacion.number,
        type: roomTypesOrder.indexOf(booking.habitacion.type) !== -1 ? roomTypesOrder.indexOf(booking.habitacion.type) : booking.habitacion.type,
        price: booking.habitacion.price,
        status: roomStatusMapping['SUCIA']
      };
      
      const updateRoomResponse = await fetch(`http://localhost:8080/api/rooms/${booking.habitacion.id}`, {
        method: 'PUT',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(roomToUpdate)
      });
      
      if (!updateRoomResponse.ok) 
        throw new Error('Error al actualizar la habitación');
      
      const roomData = await updateRoomResponse.json();
      const roomUpdated = roomData.room || roomData;
      
      const currentEstadoText = typeof booking.estado === 'number' 
      ? Object.keys(bookingStatusMapping).find(key => bookingStatusMapping[key] === booking.estado) || 'CONFIRMADA'
      : booking.estado || 'CONFIRMADA';
      
      const bookingToUpdate = {
        ...booking,
        fechaSalida: hoy,
        habitacion: {
          ...roomUpdated,
          type: roomTypesOrder.indexOf(roomUpdated.type) !== -1 ? roomTypesOrder.indexOf(roomUpdated.type) : roomUpdated.type,
          status: typeof roomUpdated.status === 'string' ? roomStatusMapping[roomUpdated.status] : roomUpdated.status
        },
        roomType: roomTypesOrder.indexOf(booking.roomType) !== -1 ? roomTypesOrder.indexOf(booking.roomType) : booking.roomType,
        estado: bookingStatusMapping[currentEstadoText],
        checkInStatus: checkInStatusMapping['FUERA']
      };
      
      const updateBookingResponse = await fetch(`http://localhost:8080/api/bookings/${booking.id}`, {
        method: 'PUT',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(bookingToUpdate)
      });
      
      if (updateBookingResponse.ok) {
        const updatedData = await updateBookingResponse.json();
        setBooking(updatedData);
        alert('Check-Out exitoso. La reserva ha finalizado.');
      } else {
        throw new Error('Error al actualizar la reserva');
      }
      
    } catch (error) {
      console.error(error);
      alert('Hubo un error al procesar el Check-Out');
    }
  };
  
  const formatDate = (date) => {
    if (!date) return '---';
    
    return new Date(date).toLocaleDateString('es-ES', {
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    });
  };
  
  const isToday = (dateString) => {
    if (!dateString) return false;
    const hoy = new Date().toLocaleDateString('es-ES');
    const fechaAComparar = new Date(dateString).toLocaleDateString('es-ES');
    return hoy === fechaAComparar;
  };
  
  const getStatusClass = (status) => {
    switch (status) {
      case 'CONFIRMADA':
      case 1:
      return 'status-confirmed';
      case 'CANCELADA':
      case 2:
      return 'status-cancelled';
      case 'PENDIENTE':
      case 0:
      return 'status-pending';
      default:
      return 'status-default';
    }
  };
  
  const getCheckInClass = (status) => {
    switch (status) {
      case 'DENTRO':
      case 1:
      return 'checkin-in';
      case 'FUERA':
      case 2:
      return 'checkin-out';
      case 'PENDIENTE':
      case 0:
      return 'checkin-pending';
      default:
      return 'checkin-default';
    }
  };
  
  const displayStatusText = (status) => {
    if (typeof status === 'number') {
      const keys = Object.keys(bookingStatusMapping);
      return keys.find(key => bookingStatusMapping[key] === status) || status;
    }
    return status;
  };
  
  const displayCheckInText = (status) => {
    if (typeof status === 'number') {
      const keys = Object.keys(checkInStatusMapping);
      return keys.find(key => checkInStatusMapping[key] === status) || status;
    }
    return status;
  };
  
  const displayRoomTypeText = (type) => {
    if (typeof type === 'number') {
      return roomTypesOrder[type] || type;
    }
    return type;
  };
  
  if (loading) {
    return (
      <div className="booking-loading-container">
      <div className="loader"></div>
      <p>Cargando reserva...</p>
      </div>
    );
  }
  
  if (!booking) {
    return (
      <div className="booking-error-container">
      <h2>No se encontró la reserva</h2>
      <Link to="/bookings" className="btn-back">
      Volver
      </Link>
      </div>
    );
  }
  
  const currentCheckInStatusText = displayCheckInText(booking.checkInStatus);
  
  return (
    <div className="booking-page-wrapper">
    <div className="booking-details-container">
    <div className="booking-topbar">
    <div>
    <h1 className="booking-title">
    Detalles de la reserva
    </h1>
    </div>
    
    <div className="edit-action">
    {currentCheckInStatusText === 'PENDIENTE' && (
      <button className="btn-checkin-action" onClick={handleCheckIn}>
      Dar Entrada (Check-In)
      </button>
    )}
    
    {currentCheckInStatusText === 'DENTRO' && isToday(booking.fechaSalida) && (
      <button className="btn-checkout-action" onClick={handleCheckOut}>
      Dar Salida (Check-Out)
      </button>
    )}
    
    <button
    className="btn-edit-booking"
    onClick={() => console.log('Editar booking', booking.id)}
    disabled={checkInStatusMapping[booking.checkInStatus] === 2}>
    Editar reserva
    </button>
    </div>
    
    <Link to="/bookings" className="btn-back">
    Volver
    </Link>
    </div>
    
    <div className="booking-main-grid">
    <div className="booking-card large-card booking-card-clickable">
    <Link to={booking.cliente?.id ? `/clients/${booking.cliente.id}` : '#'} className="card-link-wrapper">
    <span className="card-label">CLIENTE</span>
    <h2 className="client-name">
    {booking.cliente?.nombre || 'Sin cliente'}
    </h2>
    <p className="client-dni">
    DNI: {booking.cliente?.dni || '---'}
    </p>
    </Link>
    </div>
    
    <div className="booking-card">
    <span className="card-label">ESTANCIA</span>
    <div className="dates-container">
    <div className="date-box">
    <span className="date-title">Entrada</span>
    <strong className="date-value">{formatDate(booking.fechaEntrada)}</strong>
    </div>
    
    <div className="date-separator">→</div>
    
    <div className="date-box">
    <span className="date-title">Salida</span>
    <strong className="date-value">{formatDate(booking.fechaSalida)}</strong>
    </div>
    </div>
    </div>
    
    <div className={`booking-card ${booking.habitacion?.id ? 'booking-card-clickable' : ''}`}>
    {booking.habitacion?.id ? (
      <Link to={`/rooms/${booking.habitacion.id}`} className="card-link-wrapper">
      <span className="card-label">HABITACIÓN</span>
      <h3 className="room-title">
      {displayRoomTypeText(booking.habitacion?.type || booking.roomType || booking.type || 'Sin tipo')}
      </h3>
      <p className="room-number">
      Habitación Nº {booking.habitacion?.number || '---'}
      </p>
      </Link>
    ) : (
      <div className="card-link-wrapper" style={{ cursor: 'default' }}>
      <span className="card-label">HABITACIÓN</span>
      <h3 className="room-title">
      {displayRoomTypeText(booking.roomType || 'Sin tipo')}
      </h3>
      <p className="room-number">
      Habitación Nº --- (No asignada)
      </p>
      </div>
    )}
    </div>
    
    <div className="booking-card">
    <span className="card-label">ESTADO</span>
    <div className="status-group">
    <div className={`status-badge ${getStatusClass(booking.estado)}`}>
    {displayStatusText(booking.estado)}
    </div>
    
    <div className={`status-badge ${getCheckInClass(booking.checkInStatus)}`}>
    {displayCheckInText(booking.checkInStatus)}
    </div>
    </div>            
    </div>
    </div>
    </div>
    </div>
  );
};

export default BookingDetails;