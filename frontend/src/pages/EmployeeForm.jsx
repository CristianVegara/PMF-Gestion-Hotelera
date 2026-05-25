import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './EmployeeForm.css';

const EmployeeForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [employee, setEmployee] = useState({
    nombre: '',
    apellido: '',
    cargo: ''
  });
  
  const [errors, setErrors] = useState([]);
  
  useEffect(() => {
    if (id) {
      const token = localStorage.getItem('user_token');
      
      fetch(`/api/employees/${id}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      .then(res => res.ok ? res.json() : Promise.reject())
      .then(data => {
        setEmployee({
          nombre: data.nombre || '',
          apellido: data.apellido || '',
          cargo: data.cargo || ''
        });
      })
      .catch(() => navigate('/employees'));
    }
  }, [id, navigate]);
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setEmployee(prev => ({ ...prev, [name]: value }));
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors([]);
    
    const token = localStorage.getItem('user_token');
    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/employees/${id}` : '/api/employees';
    
    try {
      const response = await fetch(url, {
        method,
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(employee)
      });
      
      if (response.status === 200 || response.status === 201) {
        navigate('/employees');
      } else {
        const data = await response.json();
        if (data.errors) setErrors(data.errors);
        else alert(data.mensaje || 'Error al guardar');
      }
    } catch (err) {
      alert('Error de conexión');
    }
  };
  
  return (
    <div className="form-container">
      <h2>{id ? 'Editar Empleado' : 'Nuevo Empleado'}</h2>
      
      {errors.length > 0 && (
        <div className="alert-errors">
          <ul>
            {errors.map((err, index) => <li key={index}>{err}</li>)}
          </ul>
        </div>
      )}
        
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="nombre">Nombre</label>
          <input
            id="nombre"
            type="text"
            name="nombre"
            value={employee.nombre}
            onChange={handleChange}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="apellido">Apellido</label>
          <input
            id="apellido"
            type="text"
            name="apellido"
            value={employee.apellido}
            onChange={handleChange}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="cargo">Cargo</label>
          <select
            id="cargo"
            name="cargo"
            value={employee.cargo}
            onChange={handleChange}
            required
          >
            <option value="">Seleccione un cargo...</option>
            <option value="RECEPCIONISTA">Recepcionista</option>
            <option value="SUPERVISOR">Supervisor</option>
            <option value="ADMIN">Administrador</option>
            <option value="LIMPIEZA">Limpieza</option>
            <option value="MANTENIMIENTO">Mantenimiento</option>
          </select>
        </div>
        
        <div className="button-group">
          <button type="submit" className="btn-save">
            {id ? 'Actualizar' : 'Guardar'}
          </button>
          <button
            type="button"
            className="btn-cancel"
            onClick={() => navigate('/employees')}
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
};

export default EmployeeForm;