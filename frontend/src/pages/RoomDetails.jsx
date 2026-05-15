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

    const [room, setRoom] = useState(null);
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [clientes, setClientes] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedClientId, setSelectedClientId] = useState("");

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [rRes, bRes, cRes] = await Promise.all([
                    fetch(`http://localhost:8080/api/rooms/${id}`),
                    fetch(`http://localhost:8080/api/bookings/room/${id}`),
                    fetch(`http://localhost:8080/api/clients`)
                ]);
                setRoom(await rRes.json());
                setBookings(await bRes.json());
                setClientes(await cRes.json());
                setLoading(false);
            } catch (err) { console.error(err); setLoading(false); }
        };
        fetchData();
    }, [id]);

    const occupiedIntervals = useMemo(() => {
        return (bookings || []).map(b => ({
            start: new Date(b.fechaEntrada),
            end: new Date(b.fechaSalida)
        }));
    }, [bookings]);

    const filteredClientes = clientes.filter(c =>
        c.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
        c.dni.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const handleCreateBooking = async () => {
        const clientIdNum = parseInt(selectedClientId);
        if (!startDate || !endDate || isNaN(clientIdNum)) {
            alert("Selecciona fechas y un cliente.");
            return;
        }

        const newBooking = {
            habitacion: { id: parseInt(id) },
            cliente: { id: clientIdNum }, 
            fechaEntrada: startDate.toISOString().split('T')[0],
            fechaSalida: endDate.toISOString().split('T')[0]
        };

        try {
            const res = await fetch('http://localhost:8080/api/bookings', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newBooking)
            });

            if (res.ok) {
                window.location.reload();
            } else {
                alert("La habitación ya está ocupada en esas fechas");
            }
        } catch (err) { alert("Error de conexión."); }
    };

    if (loading) return <div className="loading">Cargando...</div>;

    return (
        <div className="room-details-page">
            <button className="btn-back" onClick={() => navigate('/rooms')}>❮ Volver</button>
            
            <div className="details-header-card">
                <h1>Habitación {room?.number}</h1>
                <div className="info-grid">
                    <div className="info-item"><span className="info-label">TIPO</span><span className="info-value">{room?.type}</span></div>
                    <div className="info-item"><span className="info-label">PRECIO</span><span className="info-value">{room?.price}€</span></div>
                    <div className="info-item"><span className="info-label">ESTADO</span><span className="info-value" style={{ color: '#10b981' }}>{room?.status}</span></div>
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
                    <button className="btn-new-res" onClick={() => setIsModalOpen(true)}>Nueva Reserva</button>
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
                                    <span>📅 {new Date(b.fechaEntrada).toLocaleDateString()} - {new Date(b.fechaSalida).toLocaleDateString()}</span>
                                </div>
                                <div className="history-arrow">❯</div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Confirmar Nueva Reserva</h2>
                        <div className="modal-dates">
                            <p><strong>Entrada:</strong> {startDate?.toLocaleDateString()}</p>
                            <p><strong>Salida:</strong> {endDate?.toLocaleDateString()}</p>
                        </div>
                        <div className="client-selector">
                            <input type="text" placeholder="Buscar cliente..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
                            <select size="6" value={selectedClientId} onChange={(e) => setSelectedClientId(e.target.value)}>
                                {filteredClientes.map(c => (
                                    <option key={c.id} value={c.id}>{c.nombre} — {c.dni}</option>
                                ))}
                            </select>
                        </div>
                        <div className="modal-actions">
                            <button className="btn-modal btn-cancel" onClick={() => setIsModalOpen(false)}>Cancelar</button>
                            <button className="btn-modal btn-confirm" onClick={handleCreateBooking}>Crear Reserva</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default RoomDetails;