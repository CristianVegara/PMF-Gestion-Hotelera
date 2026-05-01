import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './ClientForm.css';

const ClientForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [client, setClient] = useState({
    dni: '',
    nombre: '',
    telefono: '',
    correo: ''
  });

  const [errors, setErrors] = useState([]);

  useEffect(() => {
    if (id) {
      fetch(`/api/clients/${id}`)
        .then(res => {
          if (!res.ok) throw new Error('Error en la respuesta del servidor');
          return res.json();
        })
        .then(data => {
          if (data) {
            setClient({
              dni: data.dni || '',
              nombre: data.nombre || '',
              telefono: data.telefono || '',
              correo: data.correo || ''
            });
          }
        })
        .catch(err => {
          console.error('Error cargando cliente:', err);
          navigate('/clients');
        });
    }
  }, [id, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setClient(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors([]);

    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/clients/${id}` : '/api/clients';

    try {
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(client)
      });

      const data = await response.json();

      if (response.status === 200 || response.status === 201) {
        alert(data.mensaje);
        navigate('/clients');
      } else if (response.status === 400 && data.errors) {
        setErrors(data.errors);
      } else {
        alert(data.mensaje || 'Error inesperado');
      }
    } catch (err) {
      alert('Error de conexión');
    }
  };

  return (
    <div className="form-container">
      <h2>{id ? 'Editar Cliente' : 'Nuevo Cliente'}</h2>

      {errors.length > 0 && (
        <div className="alert-errors">
          <ul>
            {errors.map((err, index) => (
              <li key={index}>{err}</li>
            ))}
          </ul>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="dni">DNI</label>
          <input
            id="dni"
            type="text"
            name="dni"
            value={client.dni}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="nombre">Nombre Completo</label>
          <input
            id="nombre"
            type="text"
            name="nombre"
            value={client.nombre}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="telefono">Teléfono</label>
          <input
            id="telefono"
            type="text"
            name="telefono"
            value={client.telefono}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="correo">Correo Electrónico</label>
          <input
            id="correo"
            type="email"
            name="correo"
            value={client.correo}
            onChange={handleChange}
            required
          />
        </div>

        <div className="button-group">
          <button type="submit" className="btn-save">
            {id ? 'Actualizar' : 'Guardar'}
          </button>

          <button
            type="button"
            className="btn-cancel"
            onClick={() => navigate('/clients')}
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
};

export default ClientForm;