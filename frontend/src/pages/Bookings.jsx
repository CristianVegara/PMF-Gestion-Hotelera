import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Bookings.css';

const token = localStorage.getItem('user_token');


const Bookings = () => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    
    const [filterClient, setFilterClient] = useState('');
    const [filterRoom, setFilterRoom] = useState('');
    
    const currentYear = new Date().getFullYear().toString();
    const [filterYear, setFilterYear] = useState(currentYear);
    const [filterMonth, setFilterMonth] = useState('');
    const [filterDay, setFilterDay] = useState('');
    
    const [showEditModal, setShowEditModal] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [currentBooking, setCurrentBooking] = useState(null);
    
    useEffect(() => {
        fetchBookings();
    }, []);
    
    const fetchBookings = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/bookings', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });            if (!response.ok) throw new Error("Error en el servidor");
            const data = await response.json();
            setBookings(data);
            setLoading(false);
        } catch (error) {
            console.error('Error al cargar reservas:', error);
            setLoading(false);
        }
    };
    
    const filteredBookings = bookings.filter(book => {
        const matchesClient = book.cliente?.nombre?.toLowerCase().includes(filterClient.toLowerCase());
        const matchesRoom = filterRoom === '' || book.habitacion?.number?.toString().includes(filterRoom);
        
        if (!book.fechaEntrada) return false;
        const [year, month, day] = book.fechaEntrada.split('-');
        
        const matchesYear = filterYear === '' || year === filterYear;
        const matchesMonth = filterMonth === '' || month === filterMonth.padStart(2, '0');
        const matchesDay = filterDay === '' || day === filterDay.padStart(2, '0');
        
        return matchesClient && matchesRoom && matchesYear && matchesMonth && matchesDay;
    });
    
    const confirmDelete = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/bookings/${currentBooking.id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (response.ok) {
                setBookings(prev => prev.filter(b => b.id !== currentBooking.id));
                setShowDeleteModal(false);
            }
        } catch (error) { console.error(error); }
    };
    
    const handleUpdate = async () => {
        try {           
            const response = await fetch(`http://localhost:8080/api/bookings/${currentBooking.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(currentBooking)
            });
            if (response.ok) {
                setShowEditModal(false);
                fetchBookings(); 
            }
        } catch (error) { console.error(error); }
    };
    
    const getStatusStyle = (estado) => {
        switch (estado) {
            case 'TERMINADA': return { backgroundColor: '#e9ecef', color: '#6c757d' };
            case 'CONFIRMADA': return { backgroundColor: '#d4edda', color: '#155724' };
            case 'PRÓXIMA': return { backgroundColor: '#cce5ff', color: '#004085' };
            case 'CANCELADA': return { backgroundColor: '#f8d7da', color: '#721c24' };
            default: return { backgroundColor: '#fff3cd', color: '#856404' };
        }
    };
    
    const formatDate = (dateStr) => {
        if (!dateStr) return "---";
        const [year, month, day] = dateStr.split('-');
        return `${day}/${month}/${year}`;
    };
    
    if (loading) return <div className="loading">Cargando reservas...</div>;
    
    return (
        <div className="bookings-page-wrapper">
        <div className="bookings-container">
        <div className="header-actions">
        <h2 className="title-list">Gestión de Reservas</h2>
        </div>
        
        <div className="sort-controls">
        <input 
        type="text" 
        placeholder="Buscar cliente..." 
        value={filterClient}
        onChange={(e) => setFilterClient(e.target.value)}
        />
        <input 
        type="text" 
        placeholder="Hab..." 
        style={{ width: '60px' }}
        value={filterRoom}
        onChange={(e) => setFilterRoom(e.target.value)}
        />
        
        <div className="date-filter-group" style={{ display: 'flex', gap: '5px', alignItems: 'center' }}>
        <span>Entrada:</span>
        <input 
        type="number" 
        placeholder="Año" 
        style={{ width: '70px' }}
        value={filterYear}
        onChange={(e) => setFilterYear(e.target.value)}
        />
        <input 
        type="number" 
        placeholder="Mes" 
        min="1" max="12"
        style={{ width: '55px' }}
        value={filterMonth}
        onChange={(e) => setFilterMonth(e.target.value)}
        />
        <input 
        type="number" 
        placeholder="Día" 
        min="1" max="31"
        style={{ width: '55px' }}
        value={filterDay}
        onChange={(e) => setFilterDay(e.target.value)}
        />
        </div>
        
        <button className="btn-clear-search" onClick={() => {
            setFilterClient('');
            setFilterRoom('');
            setFilterYear(currentYear);
            setFilterMonth('');
            setFilterDay('');
        }}> Limpiar </button>
        </div>
        
        <p className="results-counter">
        Filtradas: <strong>{filteredBookings.length}</strong> de {bookings.length}
        </p>
        
        <div className="table-responsive">
        <table className="bookings-table">
        <thead>
        <tr>
        <th>ID</th><th>Cliente</th><th>Habitación</th><th>Entrada</th><th>Salida</th><th>Estado</th><th className="text-center">Acciones</th>
        </tr>
        </thead>
        <tbody>
        {filteredBookings.map(book => (
            <tr key={book.id} className="booking-row">
            <td className="clickable" onClick={() => navigate(`/clients/${book.cliente?.id}`)}>#{book.id}</td>
            <td className="clickable" onClick={() => navigate(`/clients/${book.cliente?.id}`)}>
            <strong>{book.cliente?.nombre || '---'}</strong>
            </td>
            <td><span className="badge-room">Hab. {book.habitacion?.number}</span></td>
            <td>{formatDate(book.fechaEntrada)}</td>
            <td>{formatDate(book.fechaSalida)}</td>
            <td>
            <span className="status-pill" style={getStatusStyle(book.estado)}>
            {book.estado}
            </span>
            </td>
            <td className="actions-cell">
            <button className="btn-edit" onClick={() => { setCurrentBooking({...book}); setShowEditModal(true); }}>Editar</button>
            <button className="btn-delete" onClick={() => { setCurrentBooking(book); setShowDeleteModal(true); }}>Borrar</button>
            </td>
            </tr>
        ))}
        </tbody>
        </table>
        </div>
        
        {showEditModal && currentBooking && (
            <div className="modal-overlay">
            <div className="modal-content">
            <h3>Editar Reserva #{currentBooking.id}</h3>
            <div className="form-group">
            <label>Entrada</label>
            <input type="date" value={currentBooking.fechaEntrada} onChange={(e) => setCurrentBooking({...currentBooking, fechaEntrada: e.target.value})} />
            </div>
            <div className="form-group">
            <label>Salida</label>
            <input type="date" value={currentBooking.fechaSalida} onChange={(e) => setCurrentBooking({...currentBooking, fechaSalida: e.target.value})} />
            </div>
            <div className="form-group">
            <label>Estado</label>
            <select value={currentBooking.estado} onChange={(e) => setCurrentBooking({...currentBooking, estado: e.target.value})}>
            <option value="PRÓXIMA">PRÓXIMA</option>
            <option value="CONFIRMADA">CONFIRMADA</option>
            <option value="TERMINADA">TERMINADA</option>
            <option value="CANCELADA">CANCELADA</option>
            </select>
            </div>
            <div className="modal-actions">
            <button className="btn-save" onClick={handleUpdate}>Guardar</button>
            <button className="btn-cancel" onClick={() => setShowEditModal(false)}>Cerrar</button>
            </div>
            </div>
            </div>
        )}
        
        {showDeleteModal && currentBooking && (
            <div className="modal-overlay">
            <div className="modal-content">
            <h3>¿Eliminar reserva #{currentBooking.id}?</h3>
            <div className="modal-actions">
            <button className="btn-delete-confirm" onClick={confirmDelete}>Eliminar</button>
            <button className="btn-cancel" onClick={() => setShowDeleteModal(false)}>Cancelar</button>
            </div>
            </div>
            </div>
        )}
        </div>
        </div>
    );
};

export default Bookings;