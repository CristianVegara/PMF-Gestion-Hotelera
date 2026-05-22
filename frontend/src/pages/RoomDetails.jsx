import React, { useState, useEffect, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import * as DatePickerModule from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import es from 'date-fns/locale/es';
import './RoomDetails.css';

const RawPicker = DatePickerModule.default || DatePickerModule;
const DatePicker = (RawPicker.default) ? RawPicker.default : RawPicker;
const registerLocale = DatePickerModule.registerLocale || DatePicker.registerLocale;
const token = localStorage.getItem('user_token');

if (registerLocale && es) {
    registerLocale('es', es);
}

const RoomDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    
    const [room, setRoom] = useState(null);
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    
    const roomStatusMapping = {
        'LIBRE': 0,
        'OCUPADA': 1,
        'SUCIA': 2,
        'FUERA_DE_SERVICIO': 3
    };
    
    const roomStatusInverseMapping = {
        0: 'LIBRE',
        1: 'OCUPADA',
        2: 'SUCIA',
        3: 'FUERA_DE_SERVICIO'
    };
    
    const occupiedIntervals = useMemo(() => {
        return (bookings || []).map(b => ({
            start: new Date(b.fechaEntrada),
            end: new Date(b.fechaSalida)
        }));
    }, [bookings]);
    
    const handleUpdateStatus = async (newStatusText) => {
        const confirmar = window.confirm(`¿Está seguro de cambiar el estado de la habitación a ${newStatusText.replace(/_/g, ' ')}?`);
        if (!confirmar) return;
        
        try {
            const statusValue = roomStatusMapping[newStatusText];
            const token = localStorage.getItem('user_token');
            
            const response = await fetch(`http://localhost:8080/api/rooms/${room.id}/details`, {
                method: 'PATCH',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(statusValue)
            });
            
            if (response.ok) {
                setRoom(prevRoom => ({
                    ...prevRoom,
                    status: newStatusText
                }));
                alert("Estado de la habitación actualizado con éxito.");
            } else {
                throw new Error("No se pudo actualizar el estado.");
            }
        } catch (err) {
            console.error(err);
            alert("Error al actualizar el estado de la habitación.");
        }
    };
    
    const currentStatus = useMemo(() => {
        if (!room) return null;
        if (typeof room.status === 'number') {
            return roomStatusInverseMapping[room.status] || null;
        }
        return room.status;
    }, [room]);
    
    if (loading) return <div className="loading">Cargando...</div>;
    
    return (
    <div className="room-details-page">
        <button className="btn-back" onClick={() => navigate('/rooms')}>❮ Volver</button>
        
        <div className="details-header-card">
            <div className="header-main-info">
                <h1>Habitación {room?.number}</h1>
                <div className="room-actions-status-container">
                    {(() => {
                        switch (currentStatus) {
                            case 'LIBRE':
                            return (
                            <>
                            <button className="btn-status-action status-action-dirty" onClick={() => handleUpdateStatus('SUCIA')}>Marcar Sucia</button>
                            <button className="btn-status-action status-action-maintenance" onClick={() => handleUpdateStatus('FUERA_DE_SERVICIO')}>Fuera de Servicio</button>
                            </>
                            );
                            case 'SUCIA':
                            return (
                            <>
                            <button className="btn-status-action status-action-free" onClick={() => handleUpdateStatus('LIBRE')}>Marcar Libre</button>
                            <button className="btn-status-action status-action-maintenance" onClick={() => handleUpdateStatus('FUERA_DE_SERVICIO')}>Fuera de Servicio</button>
                            </>
                            );
                            case 'FUERA_DE_SERVICIO':
                            return (
                            <>
                            <button className="btn-status-action status-action-free" onClick={() => handleUpdateStatus('LIBRE')}>Marcar Libre</button>
                            <button className="btn-status-action status-action-dirty" onClick={() => handleUpdateStatus('SUCIA')}>Marcar Sucia</button>
                            </>
                            );
                            case 'OCUPADA':
                            return (
                            <span className="status-locked-notice">🔒 Habitación Ocupada (Gestión desde Reservas)</span>
                            );
                            default:
                            return null;
                        }
                    })()}
                </div>
            </div>
            
            <div className="info-grid">
                <div className="info-item"><span className="info-label">TIPO</span><span className="info-value">{room?.type}</span></div>
                <div className="info-item"><span className="info-label">PRECIO</span><span className="info-value">{room?.price}€</span></div>
                <div className="info-item">
                    <span className="info-label">ESTADO</span>
                    <span className={`info-value status-text-${currentStatus?.toLowerCase()}`}>
                        {currentStatus?.replace(/_/g, ' ')}
                    </span>
                </div>
            </div>
        </div>
        
        <div className="details-main-content">
            <div className="calendar-section">
                <h3>Disponibilidad</h3>
                <div className="datepicker-container">
                    <DatePicker
                    selected={startDate}
                    onChange={(dates) => { const [start, end] = dates; setStartDate(start); setEndDate(end); }}
                    startDate={startDate} 
                    endDate={endDate}
                    selectsRange 
                    inline 
                    locale="es"
                    excludeDateIntervals={occupiedIntervals}
                    calendarClassName="full-width-calendar"
                    />
                </div>
            </div>
            
            <div className="history-section">
                <h3>Historial de Estancias</h3>
                <div className="history-list">
                    {bookings.map(b => (
                        <div 
                        key={b.id} 
                        className="history-card"                                
                        onClick={() => navigate(`/clients/${b.cliente?.id}?highlight=${b.id}`)}
                        >
                        <div className="history-info">
                            <strong>{b.cliente?.nombre}</strong>
                            <span>{new Date(b.fechaEntrada).toLocaleDateString()} - {new Date(b.fechaSalida).toLocaleDateString()}</span>
                        </div>
                        <div className="history-arrow">❯</div>
                    </div>
                    ))}
                </div>
            </div>
        </div>
    </div>
    );
};

export default RoomDetails;