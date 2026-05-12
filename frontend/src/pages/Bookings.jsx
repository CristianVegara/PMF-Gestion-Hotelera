import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Bookings.css';

const Bookings = () => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    // MODALES
    const [showEditModal, setShowEditModal] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [currentBooking, setCurrentBooking] = useState(null);

    useEffect(() => {
        fetchBookings();
    }, []);

    const fetchBookings = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/bookings');
            if (!response.ok) throw new Error("Error en la respuesta del servidor");
            const data = await response.json();
            setBookings(data);
            setLoading(false);
        } catch (error) {
            console.error('Error al cargar reservas:', error);
            setLoading(false);
        }
    };

    // --- ACCIONES ---

    const confirmDelete = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/bookings/${currentBooking.id}`, {
                method: 'DELETE',
            });
            if (response.ok) {
                setBookings(prev => prev.filter(b => b.id !== currentBooking.id));
                setShowDeleteModal(false);
            }
        } catch (error) {
            console.error('Error al borrar:', error);
        }
    };

    const handleUpdate = async () => {
        try {
            // CONSTRUIMOS EL OBJETO LIMPIO PARA EVITAR RECURSIVIDAD
            const bookingToSend = {
                id: currentBooking.id,
                fechaEntrada: currentBooking.fechaEntrada,
                fechaSalida: currentBooking.fechaSalida,
                estado: currentBooking.estado,
                // Enviamos solo la referencia necesaria para que Spring no explote
                cliente: { id: currentBooking.cliente?.id },
                habitacion: { id: currentBooking.habitacion?.id }
            };

            const response = await fetch(`http://localhost:8080/api/bookings/${currentBooking.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(bookingToSend)
            });

            if (response.ok) {
                setShowEditModal(false);
                fetchBookings(); // Refrescar tabla
            } else {
                const errorData = await response.text();
                console.error("Error del servidor al actualizar:", errorData);
            }
        } catch (error) {
            console.error('Error en la petición PUT:', error);
        }
    };

    // --- HELPERS ---

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

    if (loading) return <div className="loading">Cargando gestión de reservas...</div>;

    return (
        <div className="bookings-page-wrapper">
            <div className="bookings-container">
                <div className="header-actions">
                    <h2 className="title-list">Listado de Reservas</h2>
                </div>

                <table className="bookings-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cliente</th>
                            <th>Habitación</th>
                            <th>Entrada</th>
                            <th>Salida</th>
                            <th>Estado</th>
                            <th className="text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {bookings.map(book => (
                            <tr key={book.id} className="booking-row">
                                <td className="clickable" onClick={() => navigate(`/clients/${book.cliente?.id}?highlight=${book.id}`)}>
                                    #{book.id}
                                </td>
                                <td className="clickable" onClick={() => navigate(`/clients/${book.cliente?.id}?highlight=${book.id}`)}>
                                    <strong>{book.cliente?.nombre || 'Sin Cliente'}</strong>
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
                                    <button className="btn-action btn-edit" onClick={() => {
                                        setCurrentBooking({ ...book });
                                        setShowEditModal(true);
                                    }}>
                                        Editar
                                    </button>
                                    <button className="btn-action btn-delete" onClick={() => {
                                        setCurrentBooking(book);
                                        setShowDeleteModal(true);
                                    }}>
                                        Borrar
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

                {/* MODAL EDICIÓN */}
                {showEditModal && currentBooking && (
                    <div className="modal-overlay">
                        <div className="modal-content">
                            <h3>Editar Reserva #{currentBooking.id}</h3>
                            <div className="form-group">
                                <label>Fecha Entrada</label>
                                <input
                                    type="date"
                                    value={currentBooking.fechaEntrada}
                                    onChange={(e) => setCurrentBooking({ ...currentBooking, fechaEntrada: e.target.value })}
                                />
                            </div>
                            <div className="form-group">
                                <label>Fecha Salida</label>
                                <input
                                    type="date"
                                    value={currentBooking.fechaSalida}
                                    onChange={(e) => setCurrentBooking({ ...currentBooking, fechaSalida: e.target.value })}
                                />
                            </div>
                            <div className="form-group">
                                <label>Estado</label>
                                <select
                                    value={currentBooking.estado}
                                    onChange={(e) => setCurrentBooking({ ...currentBooking, estado: e.target.value })}
                                >
                                    <option value="PRÓXIMA">PRÓXIMA</option>
                                    <option value="CONFIRMADA">CONFIRMADA</option>
                                    <option value="TERMINADA">TERMINADA</option>
                                    <option value="CANCELADA">CANCELADA</option>
                                </select>
                            </div>
                            <div className="modal-actions">
                                <button className="btn-save" onClick={handleUpdate}>Guardar Cambios</button>
                                <button className="btn-cancel" onClick={() => setShowEditModal(false)}>Cerrar</button>
                            </div>
                        </div>
                    </div>
                )}

                {/* MODAL BORRADO */}
                {showDeleteModal && currentBooking && (
                    <div className="modal-overlay">
                        <div className="modal-content">
                            <h3>¿Eliminar reserva #{currentBooking.id}?</h3>
                            <p>Esta acción no se puede deshacer.</p>
                            <div className="modal-actions">
                                <button className="btn-delete-confirm" onClick={confirmDelete}>Confirmar</button>
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