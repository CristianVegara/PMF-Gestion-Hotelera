import { useState, useEffect, useMemo } from 'react';
import './Clients.css';

const formatDate = (value) => {
    if (!value) return '---';
    const [year, month, day] = value.split('-').map(Number);
    return new Date(year, month - 1, day).toLocaleDateString('es-ES');
};

const ClientsInHouse = () => {
    const [bookings, setBookings] = useState([]);
    const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(false);
    
    useEffect(() => {
        fetchInHouseBookings();
    }, [date]);
    
    const fetchInHouseBookings = async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8080/api/bookings/in-house?date=${date}`);
            const data = await response.json();
            setBookings(Array.isArray(data) ? data : []);
        } catch (err) {
            setBookings([]);
        } finally {
            setLoading(false);
        }
    };
    
    const filteredBookings = useMemo(() => {
        return bookings.filter(book => {
            const search = searchTerm.toLowerCase();
            return (
                book.cliente?.nombre?.toLowerCase().includes(search) ||
                book.habitacion?.number?.toString().includes(search)
            );
        }).sort((a, b) => (parseInt(a.habitacion?.number) || 0) - (parseInt(b.habitacion?.number) || 0));
    }, [bookings, searchTerm]);
    
    const stats = useMemo(() => ({
        total: filteredBookings.length,
        checkIns: filteredBookings.filter(b => b.fechaEntrada === date).length,
        checkOuts: filteredBookings.filter(b => b.fechaSalida === date).length
    }), [filteredBookings, date]);
    
    return (
        <div className="clients-page-wrapper">
        <div className="dashboard-container">
        
        <header className="dashboard-header">
        <div className="brand-section">
        <h1>Gestión de Huéspedes</h1>
        <span className="live-indicator">Control en tiempo real</span>
        </div>
        
        <div className="header-actions">
        <div className="search-box">
        <input 
        type="text" 
        placeholder="Buscar huésped o habitación..." 
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        />
        </div>
        <div className="date-box">
        <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
        </div>
        <button className="btn-print" onClick={() => window.print()}>Imprimir</button>
        </div>
        </header>
        
        <section className="stats-horizontal-bar">
        <div className="stat-item">
        <div className="stat-content">
        <span className="stat-value">{stats.total}</span>
        <span className="stat-label">Ocupación Total</span>
        </div>
        </div>
        <div className="stat-item">
        <div className="stat-content">
        <span className="stat-value">{stats.checkIns}</span>
        <span className="stat-label">Entradas Hoy</span>
        </div>
        </div>
        <div className="stat-item">
        <div className="stat-content">
        <span className="stat-value">{stats.checkOuts}</span>
        <span className="stat-label">Salidas Hoy</span>
        </div>
        </div>
        </section>
        
        <div className="table-card">
        <table className="modern-table">
        <thead>
        <tr>
        <th>Hab.</th>
        <th>Tipo</th>
        <th>Huésped</th>
        <th>Identificación</th>
        <th>Duración de la Estancia</th>
        <th className="text-center">Estado</th>
        </tr>
        </thead>
        <tbody>
        {loading ? (
            <tr><td colSpan="6" className="text-center">Cargando...</td></tr>
        ) : filteredBookings.map(book => (
            <tr key={book.id}>
            <td className="bold">#{book.habitacion?.number}</td>
            <td><span className="tag">{book.habitacion?.type}</span></td>
            <td className="bold">{book.cliente?.nombre}</td>
            <td>{book.cliente?.dni}</td>
            <td className="small-date">{formatDate(book.fechaEntrada)} - {formatDate(book.fechaSalida)}</td>
            <td className="text-center">
            <span className={`badge ${book.estado.toLowerCase()}`}>{book.estado}</span>
            </td>
            </tr>
        ))}
        </tbody>
        </table>
        </div>
        </div>
        </div>
    );
};

export default ClientsInHouse;