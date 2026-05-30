import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Employees.css';

const Employees = () => {
  const [employees, setEmployees] = useState([]);
  const [sortBy, setSortBy] = useState('id');
  const [direction, setDirection] = useState('asc');
  const [nameSearch, setNameSearch] = useState('');
  
  const navigate = useNavigate();
  
  useEffect(() => {
    fetchEmployees();
  }, [sortBy, direction]);

  let userRole = localStorage.getItem('role') || '';
  if (userRole.startsWith("ROLE_")) {
    userRole = userRole.replace("ROLE_", "");
  }
  
  const fetchEmployees = async () => {
    try {
      const token = localStorage.getItem('user_token');
      
      const response = await fetch(`/api/employees?sortBy=${sortBy}&direction=${direction}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      const data = await response.json();
      setEmployees(data);
    } catch (error) {
      console.error('Error al cargar los empleados:', error);
    }
  };
  
  const deleteEmployee = async (e, id) => {
    e.stopPropagation();
    if (window.confirm('¿Estás seguro de eliminar este empleado?')) {
      try {
        const token = localStorage.getItem('user_token');
        
        const res = await fetch(`/api/employees/${id}`, { 
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        console.log("Respuesta del servidor:", res.status, await res.text());
        
        if (res.ok) {
          fetchEmployees();
        } else {
          alert(`Error al eliminar (Código: ${res.status})`);
        }
      } catch (err) {
        console.error("Error de red:", err);
      }
    }
  };
  
  const filteredEmployees = employees.filter(employee => {
    const fullName = `${employee.nombre} ${employee.apellido}`.toLowerCase();
    return fullName.includes(nameSearch.trim().toLowerCase());
  });
  
  return (
    <div className="clients-page-wrapper">
      <div className="clients-container">
        <div className="header-actions">
          <h2 className="title-list">Listado de Empleados</h2>
          {(userRole === 'ADMIN' || userRole === 'SUPERVISOR') && (
            <Link to="/employees/form" className="btn-new">Nuevo Empleado</Link>
          )}
        </div>
        
        <div className="sort-controls">
          <label className="dni-search-label">
            Buscar por Nombre:
            <input
              type="search"
              value={nameSearch}
              onChange={e => setNameSearch(e.target.value)}
              placeholder="Ej: Juan Perez"
            />
          </label>
          <label>
            Ordenar por:
            <select value={sortBy} onChange={e => setSortBy(e.target.value)}>
              <option value="id">ID</option>
              <option value="nombre">Nombre</option>
              <option value="cargo">Cargo</option>
            </select>
          </label>
        </div>
        
        <div className="table-responsive">
          <table className="modern-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Apellido</th>
                <th>Cargo</th>
                <th>Usuario</th>
                <th className="text-center">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredEmployees.map(employee => (
                <tr 
                  key={employee.id} 
                  onClick={() => navigate(`/employees/edit/${employee.id}`)} 
                  className="clickable-row"
                  title="Editar empleado"
                >
                  <td><strong>{employee.id}</strong></td>
                  <td>{employee.nombre}</td>
                  <td>{employee.apellido}</td>
                  <td>{employee.cargo}</td>
                  <td>{employee.user ? employee.user.username : 'Sin usuario'}</td>
                  <td className="text-center">
                    <div className="action-group">
                      {(userRole === 'ADMIN' || userRole === 'SUPERVISOR') && (
                        <button 
                          onClick={(e) => {
                            e.stopPropagation();
                            navigate(`/employees/edit/${employee.id}`);
                          }}
                          className="btn-edit">
                          Editar
                        </button>
                      )}                      
                    </div>
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

export default Employees;