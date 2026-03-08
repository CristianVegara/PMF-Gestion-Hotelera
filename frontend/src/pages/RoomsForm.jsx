import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './Rooms.css';

const RoomForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [room, setRoom] = useState({
    number: '',
    type: '',
    price: '',
    available: true
  });

  const [errors, setErrors] = useState([]);

  useEffect(() => {
    if (id) {
      fetch(`/api/rooms/${id}`)
        .then(res => res.json())
        .then(data => setRoom({
          number: data.number || '',
          type: data.type || '',
          price: data.price || '',
          available: data.available
        }))
        .catch(err => {
          console.error("Error cargando habitación:", err);
          navigate('/rooms');
        });
    }
  }, [id, navigate]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setRoom(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors([]);

    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/rooms/${id}` : '/api/rooms';

    try {
      const response = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          ...room,
          price: parseFloat(room.price)
        })
      });

      const data = await response.json();

      if (response.status === 201 || response.status === 200) {
        alert(data.mensaje);
        navigate('/rooms');
      } else if (response.status === 400 && data.errors) {
        setErrors(data.errors);
      } else {
        alert(data.mensaje || "Error inesperado");
      }
    } catch (err) {
      alert("Error de conexión");
    }
  };

  return (
    <div className="form-container">
      <h2>{id ? 'Editar Habitación' : 'Nueva Habitación'}</h2>

      {errors.length > 0 && (
        <div className="alert-errors">
          <ul>
            {errors.map((err, i) => <li key={i}>{err}</li>)}
          </ul>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Número</label>
          <input type="number" name="number" value={room.number} onChange={handleChange} required />
        </div>

        <div className="form-group">
          <label>Tipo</label>
          <input type="text" name="type" value={room.type} onChange={handleChange} required />
        </div>

        <div className="form-group">
          <label>Precio</label>
          <input type="number" step="0.01" name="price" value={room.price} onChange={handleChange} required />
        </div>

        <div className="form-group">
          <label>
            <input type="checkbox" name="available" checked={room.available} onChange={handleChange} />
            Disponible
          </label>
        </div>

        <div className="button-group">
          <button type="submit" className="btn-save">{id ? 'Actualizar' : 'Guardar'}</button>
          <button type="button" className="btn-cancel" onClick={() => navigate('/rooms')}>Cancelar</button>
        </div>
      </form>
    </div>
  );
};

export default RoomForm;