import React, { useState, useEffect, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import * as DatePickerModule from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import es from 'date-fns/locale/es';
import './RoomDetails.css';

const RawPicker = DatePickerModule.default || DatePickerModule;
const DatePicker = (RawPicker.default) ? RawPicker.default : RawPicker;
const registerLocale = DatePickerModule.registerLocale || DatePicker.registerLocale;

if (registerLocale && es) {
    registerLocale('es', es);
}

const RoomDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const token = localStorage.getItem('user_token');

    let userRole = localStorage.getItem('role') || '';
    if (userRole.startsWith("ROLE_")) {
        userRole = userRole.replace("ROLE_", "");
    }

    const [room, setRoom] = useState(null);
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);

    const roomStatusMapping = {
        'LIBRE': 0,
        'OCUPADA': 1,
        'SUCIA': 2,
        'FUERA_DE_SERVICIO': 3
    };

    const roomTypesOrder = ['INDIVIDUAL', 'DOBLE', 'SUITE'];

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [rRes, bRes] = await Promise.all([
                    fetch(`http://localhost:8080/api/rooms/${id}`, {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }),
                    fetch(`http://localhost:8080/api/bookings/room/${id}`, {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    })
                ]);

                setRoom(await rRes.json());
                setBookings(await bRes.json());
                setLoading(false);
            } catch (err) {
                console.error(err);
                setLoading(false);
            }
        };

        fetchData();
    }, [id, token]);

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

            const roomUpdated = {
                ...room,
                status: statusValue
            };

            const latestBooking = bookings[bookings.length - 1] || {};

            const bookingToUpdate = {
                ...latestBooking,
                habitacion: {
                    ...roomUpdated,
                    type: roomTypesOrder.indexOf(roomUpdated.type) !== -1
                        ? roomTypesOrder.indexOf(roomUpdated.type)
                        : roomUpdated.type,
                    status: typeof roomUpdated.status === 'string'
                        ? roomStatusMapping[roomUpdated.status]
                        : roomUpdated.status
                }
            };

            const response = await fetch(`http://localhost:8080/api/rooms/${room.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(bookingToUpdate.habitacion)
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
            return Object.keys(roomStatusMapping).find(
                key => roomStatusMapping[key] === room.status
            );
        }

        return room.status;
    }, [room]);

    if (loading) return <div className="loading">Cargando...</div>;

    return (
        <div className="room-details-page">
            <button className="btn-back" onClick={() => navigate('/rooms')}>
                ❮ Volver
            </button>

            <div className="details-header-card">
                <div className="header-main-info">
                    <h1>Habitación {room?.number}</h1>

                    {userRole !== 'USER' && (
                            <div className="room-actions-status-container">
                            
                                {currentStatus === 'LIBRE' && (
                                    <>
                                        <button
                                            className="btn-status-action status-action-dirty"
                                            onClick={() => handleUpdateStatus('SUCIA')}>
                                            Marcar Sucia
                                        </button>

                                        <button
                                            className="btn-status-action status-action-maintenance"
                                            onClick={() => handleUpdateStatus('FUERA_DE_SERVICIO')}>
                                            Fuera de Servicio
                                        </button>
                                    </>
                                )}

                                {currentStatus === 'SUCIA' && (
                                    <>
                                        <button
                                            className="btn-status-action status-action-free"
                                            onClick={() => handleUpdateStatus('LIBRE')}
                                        >
                                            Marcar Libre
                                        </button>

                                        <button
                                            className="btn-status-action status-action-maintenance"
                                            onClick={() => handleUpdateStatus('FUERA_DE_SERVICIO')}
                                        >
                                            Fuera de Servicio
                                        </button>
                                    </>
                                )}

                                {currentStatus === 'FUERA_DE_SERVICIO' && (
                                    <>
                                        <button
                                            className="btn-status-action status-action-free"
                                            onClick={() => handleUpdateStatus('LIBRE')}>
                                            Marcar Libre
                                        </button>

                                        <button
                                            className="btn-status-action status-action-dirty"
                                            onClick={() => handleUpdateStatus('SUCIA')}>
                                            Marcar Sucia
                                        </button>
                                    </>
                                )}

                                {currentStatus === 'OCUPADA' && (
                                    <span className="status-locked-notice">
                                        Habitación Ocupada (Gestión desde Reservas)
                                    </span>
                                )}
                                
                        </div>
                    )}
                </div>

                <div className="info-grid">
                    <div className="info-item">
                        <span className="info-label">TIPO</span>
                        <span className="info-value">{room?.type}</span>
                    </div>

                    <div className="info-item">
                        <span className="info-label">PRECIO</span>
                        <span className="info-value">{room?.price}€</span>
                    </div>

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

                                    <span>
                                        📅 {new Date(b.fechaEntrada).toLocaleDateString()} - {new Date(b.fechaSalida).toLocaleDateString()}
                                    </span>
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