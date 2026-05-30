import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './UserForm.css';

const UserForm = () => {
  const navigate = useNavigate();

  const [user, setUser] = useState({
    username: '',
    passwordHash: '',
    role: '',
    employee: ''
  });

  const [employees, setEmployees] = useState([]);
  const [errors, setErrors] = useState([]);

  useEffect(() => {
    const token = localStorage.getItem('user_token');

    fetch('/api/employees/no-user', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    .then(res => {
      if (!res.ok) throw new Error('Error cargando empleados');
      return res.json();
    })
    .then(data => setEmployees(data))
    .catch(err => {
      console.error(err);
      setEmployees([]);
    });
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setUser(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors([]);

    const payload = {
      username: user.username,
      passwordHash: user.passwordHash,
      role: user.role,
      employee: user.employee ? { id: user.employee } : null
    };


    try {
      const token = localStorage.getItem('user_token');

      const response = await fetch('/api/users', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      const data = await response.json();
      console.log("Respuesta del servidor:", data);

      if (response.status === 200 || response.status === 201) {
        alert(data.mensaje || 'Usuario creado correctamente');
        navigate('/employees');
      } else if (response.status === 400 && data.errors) {
        setErrors(data.errors);
      } else {
        alert(data.mensaje || 'Error inesperado');
      }
    } catch (err) {
      console.error("Error de conexión:", err);
      alert('Error de conexión');
    }
  };
  return (
    <div className="form-container">
      <h2>Nuevo Usuario</h2>

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
          <label>Username</label>
          <input
            type="text"
            name="username"
            value={user.username}
            onChange={handleChange} />
        </div>

        <div className="form-group">
          <label>Password</label>
          <input
            type="password"
            name="passwordHash"
            value={user.passwordHash}
            onChange={handleChange}
            required />
        </div>

        <div className="form-group">
          <label>Role</label>
          <select
            name="role"
            value={user.role}
            onChange={handleChange}
            required>
            <option value="">Seleccione un rol</option>
            <option value="ADMIN">ADMIN</option>
            <option value="SUPERVISOR">SUPERVISOR</option>
            <option value="RECEPCIONISTA">RECEPCIONISTA</option>
            <option value="USER">USER</option>
          </select>
        </div>

        <div className="form-group">
          <label>Empleado</label>
          <select
            name="employee"
            value={user.employee}
            onChange={handleChange}
          >
            <option value="">Sin asignar</option>
            {employees.map(emp => (
              <option key={emp.id} value={emp.id}>
                {`${emp.nombre} ${emp.apellido}`}
              </option>
            ))}
          </select>
        </div>

        <div className="button-group">
          <button type="submit" className="btn-save">
            Guardar
          </button>

          <button
            type="button"
            className="btn-cancel"
            onClick={() => navigate('/user/form')}>
            Cancelar
          </button>
        </div>

      </form>
    </div>
  );
};

export default UserForm;