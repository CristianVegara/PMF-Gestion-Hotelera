import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Bookings.css';

const Bookings = () => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    const [filterClient, setFilterClient] = useState('');
    const [filterRoom, setFilterRoom] = useState('');

    const [filterYear, setFilterYear] = useState(new Date().getFullYear().toString());
    const [filterMonth, setFilterMonth] = useState('');
    const [filterDay, setFilterDay] = useState('');

    const [showEditModal, setShowEditModal] = useState(false);
    const [currentBooking, setCurrentBooking] = useState(null);

    useEffect(() => {
        fetchBookings();
    }, []);

    const fetchBookings = async () => {
        try {
            const token = localStorage.getItem('user_token');
            const response = await fetch('http://localhost:8080/api/bookings', {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!response.ok) throw new Error("Error en el servidor");

            const data = await response.json();
            data.forEach( (d) => {console.log(d.estado)})
            setBookings(data);
            setLoading(false);
        } catch (error) {
            console.error(error);
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

    const getStatusStyle = (estado) => {
        console.log("Evaluando estado:", estado); // <-- Esto te dirá qué llega exactamente
        switch (estado) {
            case 'CONFIRMADA':
                return { backgroundColor: '#d4edda', color: '#155724' };
            case 'CANCELADA':
                return { backgroundColor: '#f8d7da', color: '#721c24' };
            case 'SIN_CONFIRMAR':
                return { backgroundColor: '#fff3cd', color: '#856404' };
            default:
                console.warn("Estado desconocido:", estado);
                return { backgroundColor: '#e9ecef', color: '#6c757d' };
        }
    };

    const handleUpdate = async () => {
        try {
            const token = localStorage.getItem('user_token');

            const response = await fetch(`http://localhost:8080/api/bookings/${currentBooking.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(currentBooking)
            });

            if (response.ok) {
                alert("Reserva actualizada con éxito");
                setShowEditModal(false);
                fetchBookings();
            } else {
                alert("Error al actualizar");
            }
        } catch (error) {
            console.error(error);
            alert("Error de conexión");
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
                        placeholder="Buscar habitación..."
                        value={filterRoom}
                        onChange={(e) => setFilterRoom(e.target.value)}
                    />
                </div>

                <p className="results-counter">
                    Filtradas: <strong>{filteredBookings.length}</strong> de {bookings.length}
                </p>

                <div className="table-responsive">
                    <table className="modern-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Cliente</th>
                                <th>Habitación</th>
                                <th>Entrada</th>
                                <th>Salida</th>
                                <th>Estado</th>
                                <th>Check-In</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>

                        <tbody>
                            {filteredBookings.map(book => (
                                <tr key={book.id}>
                                    <td onClick={() => navigate(`/clients/${book.cliente?.id}`)}>
                                        #{book.id}
                                    </td>

                                    <td onClick={() => navigate(`/clients/${book.cliente?.id}`)}>
                                        <strong>{book.cliente?.nombre || '---'}</strong>
                                    </td>

                                    <td>
                                        <span className="badge-room">
                                            Hab. {book.habitacion?.number}
                                        </span>
                                    </td>

                                    <td>{formatDate(book.fechaEntrada)}</td>
                                    <td>{formatDate(book.fechaSalida)}</td>

                                    <td>
                                        <span
                                            className="status-pill"
                                            style={getStatusStyle(book.estado)}>
                                            {book.estado}
                                        </span>
                                    </td>

                                    <td>{book.checkInStatus}</td>

                                    <td>
                                        <button
                                            className="btn-edit"
                                            onClick={() => {
                                                setCurrentBooking(book);
                                                setShowEditModal(true);
                                            }}
                                        >
                                            Editar
                                        </button>
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
                                <label>Fecha Entrada</label>
                                <input
                                    type="date"
                                    value={currentBooking.fechaEntrada}
                                    onChange={(e) =>
                                        setCurrentBooking({
                                            ...currentBooking,
                                            fechaEntrada: e.target.value
                                        })
                                    }
                                />
                            </div>

                            <div className="form-group">
                                <label>Fecha Salida</label>
                                <input
                                    type="date"
                                    value={currentBooking.fechaSalida}
                                    onChange={(e) =>
                                        setCurrentBooking({
                                            ...currentBooking,
                                            fechaSalida: e.target.value
                                        })
                                    }
                                />
                            </div>

                            <div className="form-group">
                                <label>Estado</label>
                                <select
                                    value={currentBooking.estado}
                                    onChange={(e) =>
                                        setCurrentBooking({
                                            ...currentBooking,
                                            estado: e.target.value
                                        })
                                    }
                                >
                                    <option value="SIN_CONFIRMAR">SIN_CONFIRMAR</option>
                                    <option value="CONFIRMADA">CONFIRMADA</option>
                                    <option value="CANCELADA">CANCELADA</option>
                                </select>
                            </div>

                            <div className="form-group">
                                <label>Check-In Status</label>
                                <select
                                    value={currentBooking.checkInStatus}
                                    onChange={(e) =>
                                        setCurrentBooking({
                                            ...currentBooking,
                                            checkInStatus: e.target.value
                                        })
                                    }
                                >
                                    <option value="PENDIENTE">PENDIENTE</option>
                                    <option value="DENTRO">DENTRO</option>
                                    <option value="FUERA">FUERA</option>
                                </select>
                            </div>

                            <div className="form-group">
                                <label>Tipo habitación</label>
                                <select
                                    value={currentBooking.roomType}
                                    onChange={(e) =>
                                        setCurrentBooking({
                                            ...currentBooking,
                                            roomType: e.target.value
                                        })
                                    }
                                >
                                    <option value="INDIVIDUAL">INDIVIDUAL</option>
                                    <option value="DOBLE">DOBLE</option>
                                    <option value="SUITE">SUITE</option>
                                </select>
                            </div>

                            <div className="modal-actions">
                                <button className="btn-save" onClick={handleUpdate}>
                                    Guardar
                                </button>

                                <button className="btn-cancel" onClick={() => setShowEditModal(false)}>
                                    Cerrar
                                </button>
                            </div>

                        </div>
                    </div>
                )}

            </div>
        </div>
    );
};

export default Bookings;