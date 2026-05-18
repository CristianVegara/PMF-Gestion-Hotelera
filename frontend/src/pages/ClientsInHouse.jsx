import { useState, useEffect, useMemo } from 'react';
import { Link } from 'react-router-dom';
import './Clients.css';

const formatDate = (value) => {
    if (!value) return '---';
    const [year, month, day] = value.split('-').map(Number);
    return new Date(year, month - 1, day).toLocaleDateString('es-ES');
};

const ClientsInHouse = () => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    
    const todayStr = useMemo(() => {
        const local = new Date();
        const offset = local.getTimezoneOffset();
        const adjusted = new Date(local.getTime() - (offset * 60 * 1000));
        return adjusted.toISOString().split('T')[0];
    }, []);

    useEffect(() => {
        fetchInHouseBookings();
    }, [todayStr]);
    
    const fetchInHouseBookings = async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8080/api/bookings/date?date=${todayStr}`);
            const data = await response.json();
            setBookings(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error(err);
            setBookings([]);
        } finally {
            setLoading(false);
        }
    };
    
    const filteredBookings = useMemo(() => {
        return bookings
            .filter(book => book.checkInStatus === 'DENTRO')
            .filter(book => {
                const search = searchTerm.toLowerCase();
                return (
                    book.cliente?.nombre?.toLowerCase().includes(search) ||
                    book.habitacion?.number?.toString().includes(search)
                );
            })
            .sort((a, b) => (parseInt(a.habitacion?.number) || 0) - (parseInt(b.habitacion?.number) || 0));
    }, [bookings, searchTerm]);
    
    const stats = useMemo(() => {
        return {
            total: filteredBookings.length,
            checkIns: bookings.filter(b => b.fechaEntrada === todayStr && b.checkInStatus === 'PENDIENTE').length,
            checkOuts: bookings.filter(b => b.fechaSalida === todayStr && b.checkInStatus === 'DENTRO').length
        };
    }, [bookings, filteredBookings, todayStr]);
    
    return (
        <div className="clients-page-wrapper">
            <div className="dashboard-container">
            
                <header className="dashboard-header">
                    <div className="brand-section">
                        <h1>Gestión de Huéspedes</h1>
                        <span className="live-indicator">Control en tiempo real — Hoy ({formatDate(todayStr)})</span>
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
                        <button className="btn-print" onClick={() => window.print()}>Imprimir</button>
                    </div>
                </header>
                
                <section className="stats-horizontal-bar">
                    <div className="stat-item">
                        <div className="stat-content">
                            <span className="stat-value">{stats.total}</span>
                            <span className="stat-label">En Casa (Dentro)</span>
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
                                <th className="text-center">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {loading ? (
                                <tr><td colSpan="7" className="text-center">Cargando...</td></tr>
                            ) : filteredBookings.length === 0 ? (
                                <tr><td colSpan="7" className="text-center">No hay huéspedes "Dentro" registrados para hoy.</td></tr>
                            ) : filteredBookings.map(book => (
                                <tr key={book.id}>
                                    <td className="bold">#{book.habitacion?.number || '---'}</td>
                                    <td><span className="tag">{book.habitacion?.type || book.roomType}</span></td>
                                    <td className="bold">{book.cliente?.nombre}</td>
                                    <td>{book.cliente?.dni}</td>
                                    <td className="small-date">{formatDate(book.fechaEntrada)} - {formatDate(book.fechaSalida)}</td>
                                    <td className="text-center">
                                        <span className={`badge ${book.checkInStatus.toLowerCase()}`}>{book.checkInStatus}</span>
                                    </td>
                                    <td className="text-center">
                                        <Link to={`/bookings/${book.id}`} className="btn-edit">Booking</Link>
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