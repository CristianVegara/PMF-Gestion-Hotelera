import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './EmployeeForm.css';

const EmployeeForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [employee, setEmployee] = useState({
    nombre: '',
    apellido: '',
    cargo: '',
    user: null
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
          cargo: data.cargo || '',
          user: data.user || null
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

    const payload = {
      nombre: employee.nombre,
      apellido: employee.apellido,
      cargo: employee.cargo,
      user: (employee.user && employee.user.id) ? { id: employee.user.id } : null   
    };
    
    try {
      const response = await fetch(url, {
        method,
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });
      
      if (response.status === 200 || response.status === 201 || response.status === 204) {
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

  const userRole = localStorage.getItem('role')

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

        {(employee.user && localStorage.getItem('role') === 'ROLE_ADMIN') && (
          <div className="form-group">
            <label htmlFor="userRole">Rol del Usuario</label>
            <select
              id="userRole"
              name="userRole"
              // Al usar el valor que viene de la base de datos, 
              // el select se posicionará automáticamente en la opción correspondiente
              value={employee.user?.role || ''} 
              onChange={(e) => {
                setEmployee(prev => ({
                  ...prev,
                  user: { ...prev.user, role: e.target.value }
                }));
              }}
              required>
              <option value="">Seleccione un rol...</option>
              <option value="ROLE_RECEPCIONISTA">Recepcionista</option>
              <option value="ROLE_SUPERVISOR">Supervisor</option>
              <option value="ROLE_ADMIN">Administrador</option>
            </select>
          </div>
        )}
        
        <div className="button-group">
          <button type="submit" className="btn-save">
            {id ? 'Actualizar' : 'Guardar'}
          </button>
          <button
            type="button"
            className="btn-cancel"
            onClick={() => navigate('/employees')}>
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
};

export default EmployeeForm;