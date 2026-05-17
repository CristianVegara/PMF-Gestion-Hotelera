import { useState, useEffect, useMemo } from 'react';
import { useParams, Link } from 'react-router-dom';

const ArrivalsToday = ({ date }) => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setLoading(true);
        fetch(`http://localhost:8080/api/bookings/date?date=${date}`)
            .then(res => res.json())
            .then(data => {
                setBookings(Array.isArray(data) ? data.filter(b => b.fechaEntrada === date) : []);
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
                <h2>Listado de Entradas</h2>
                <p>Fecha: {new Date(date).toLocaleDateString()}</p>
            </div>
            <table className="modern-table">
                <thead>
                    <tr>
                        <th>Hab.</th>
                        <th>Huésped</th>
                        <th className="text-center">Estado</th>
                    </tr>
                </thead>
                <tbody>
                    {loading ? (
                        <tr><td colSpan="3" className="text-center">Cargando...</td></tr>
                    ) : sortedBookings.map(b => (
                        <tr key={b.id}>
                            <td className="bold">#{b.habitacion?.number}</td>
                            <td>{b.cliente?.nombre}</td>
                            <td className="text-center">
                                <span className={`badge ${b.checkInStatus === 1 ? 'status-confirmada' : 'status-sin-confirmar'}`}>
                                    {b.checkInStatus === 1 ? 'DENTRO' : 'PENDIENTE'}
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

export default ArrivalsToday;