import { useState, useEffect, useMemo } from 'react';
import { useParams, Link } from 'react-router-dom';

const token = localStorage.getItem('user_token');

const DeparturesToday = ({ date }) => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    
    useEffect(() => {
        setLoading(true);
        const token = localStorage.getItem('user_token');
        
        fetch(`http://localhost:8080/api/bookings/date?date=${date}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(res => res.json())
        .then(data => {
            setBookings(Array.isArray(data) ? data.filter(b => b.fechaSalida === date && b.checkInStatus === 'DENTRO') : []);
            setLoading(false);
        })
        .catch(() => {
            setBookings([]);
            setLoading(false);
        });
    }, [date]);
    
    const sortedBookings = useMemo(() => {
        return [...bookings].sort((a, b) => a.checkInStatus - b.checkInStatus);
    }, [bookings]);
    
    return (
    <div className="table-card">
        <div className="table-header-custom">
            <h2>Listado de Salidas</h2>
            <p>Fecha: {new Date(date).toLocaleDateString()}</p>
        </div>
        <table className="modern-table">
            <thead>
                <tr>
                    <th>Hab.</th>
                    <th>Huésped</th>
                    <th className="text-center">Estado</th>
                    <th className="text-center">Acciones</th>
                </tr>
            </thead>
            <tbody>
                {loading ? (
                    <tr><td colSpan="4" className="text-center">Cargando...</td></tr>
                    ) : sortedBookings.map(b => (
                    <tr key={b.id}>
                        <td className="bold">#{b.habitacion?.number}</td>
                        <td>{b.cliente?.nombre}</td>
                        <td className="text-center">
                            <span className={`badge ${b.checkInStatus === 2 ? 'status-cancelada' : 'status-dentro'}`}>
                                {b.checkInStatus}
                            </span>
                        </td>
                        <td><Link to={`/bookings/${b.id}`} className="btn-edit">Booking</Link></td>   
                    </tr>
                    ))}
                </tbody>
            </table>
        </div>
        );
    };
    
    export default DeparturesToday;