import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import * as DatePickerModule from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import es from 'date-fns/locale/es';
import './BookingForm.css';

const RawPicker = DatePickerModule.default || DatePickerModule;
const DatePicker = (RawPicker.default) ? RawPicker.default : RawPicker;
const registerLocale = DatePickerModule.registerLocale || DatePicker.registerLocale;


if (registerLocale && es) {
    registerLocale('es', es);
}

const BookingForm = () => {
    const navigate = useNavigate();
    
    const [clientes, setClientes] = useState([]);
    const [loadingClientes, setLoadingClientes] = useState(true);
    
    const [selectedClientId, setSelectedClientId] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [roomType, setRoomType] = useState("0"); 
    const [issubmitting, setIsSubmitting] = useState(false);
    
    const today = useMemo(() => {
        const d = new Date();
        d.setHours(0, 0, 0, 0);
        return d;
    }, []);
    
    useEffect(() => {
        const fetchClientes = async () => {
            try {
                const token = localStorage.getItem('user_token');
                
                const response = await fetch('http://localhost:8080/api/clients', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });                if (response.ok) {
                    const data = await response.json();
                    setClientes(Array.isArray(data) ? data : []);
                }
            } catch (err) {
                console.error("Error al cargar clientes:", err);
            } finally {
                setLoadingClientes(false);
            }
        };
        fetchClientes();
    }, []);
    
    const filteredClientes = useMemo(() => {
        const search = searchTerm.toLowerCase();
        return clientes.filter(c =>
        c.nombre?.toLowerCase().includes(search) ||
        c.dni?.toLowerCase().includes(search)
        );
    }, [clientes, searchTerm]);
    
    const handleStartDateChange = (date) => {
        setStartDate(date);
        if (endDate && date && date >= endDate) {
            setEndDate(null);
        }
    };
    
    const formatLocalDateString = (date) => {
        if (!date) return null;
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    };
    
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!selectedClientId) {
            alert("Por favor, selecciona un huésped.");
            return;
        }
        if (!startDate || !endDate) {
            alert("Por favor, selecciona ambas fechas.");
            return;
        }
        
        const bookingPayload = {
            fechaEntrada: formatLocalDateString(startDate),
            fechaSalida: formatLocalDateString(endDate),
            estado: 1, 
            checkInStatus: 0, 
            roomType: parseInt(roomType), 
            cliente: { id: parseInt(selectedClientId) },
            habitacion: null 
        };
        
        setIsSubmitting(true);
        try {
            const token = localStorage.getItem('user_token');
            
            const response = await fetch('http://localhost:8080/api/bookings', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(bookingPayload)
            });
            
            if (response.ok) {
                alert("Reserva creada correctamente.");
                navigate('/bookings'); 
            } else {
                alert("No se pudo procesar la reserva.");
            }
        } catch (err) {
            console.error(err);
            alert("Error de conexión con el servidor.");
        } finally {
            setIsSubmitting(false);
        }
    };
    
    return (
    <div className="booking-form-wrapper">
        <div className="form-card">
            <h2>Registrar Nueva Reserva</h2>
            <form onSubmit={handleSubmit} className="modern-form">
                
                <div className="form-group">
                    <label>Buscar Huésped (Nombre o DNI)</label>
                    <input 
                    type="text" 
                    placeholder="Escribe para filtrar..." 
                    value={searchTerm} 
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="form-control"
                    />
                    {loadingClientes ? (
                        <p className="loading-text">Cargando lista de clientes...</p>
                        ) : (
                        <select 
                        size="5" 
                        value={selectedClientId} 
                        onChange={(e) => setSelectedClientId(e.target.value)}
                        className="client-selector-box"
                        required
                        >
                        {filteredClientes.length === 0 ? (
                            <option disabled>No se encontraron clientes</option>
                            ) : (
                            filteredClientes.map(c => (
                            <option key={c.id} value={c.id}>
                                {c.nombre} — DNI: {c.dni}
                            </option>
                            ))
                            )}
                        </select>
                        )}
                    </div>
                    
                    <div className="dates-row">
                        <div className="form-group">
                            <label>Fecha de Entrada</label>
                            <DatePicker
                            selected={startDate}
                            onChange={handleStartDateChange}
                            minDate={today}
                            dateFormat="dd/MM/yyyy"
                            locale="es"
                            placeholderText="Seleccionar entrada"
                            className="form-control date-picker-input"
                            required
                            />
                        </div>
                        
                        <div className="form-group">
                            <label>Fecha de Salida</label>
                            <DatePicker
                            selected={endDate}
                            onChange={(date) => setEndDate(date)}
                            minDate={startDate ? new Date(startDate.getTime() + 86400000) : new Date(today.getTime() + 86400000)}
                            dateFormat="dd/MM/yyyy"
                            locale="es"
                            placeholderText="Seleccionar salida"
                            className="form-control date-picker-input"
                            disabled={!startDate}
                            required
                            />
                        </div>
                    </div>
                    
                    <div className="form-group">
                        <label>Tipo de Habitación</label>
                        <select 
                        value={roomType} 
                        onChange={(e) => setRoomType(e.target.value)}
                        className="form-control choice-box"
                        >
                        <option value="0">INDIVIDUAL</option>
                        <option value="1">DOBLE</option>
                        <option value="2">SUITE</option>
                    </select>
                </div>
                
                <div className="form-actions">
                    <button 
                    type="button" 
                    className="btn-secondary" 
                    onClick={() => navigate(-1)}
                    disabled={issubmitting}
                    >
                    Cancelar
                </button>
                <button 
                type="submit" 
                className="btn-primary"
                disabled={issubmitting}
                >
                {issubmitting ? "Guardando..." : "Confirmar Reserva"}
            </button>
        </div>
        
    </form>
</div>
</div>
);
};

export default BookingForm;