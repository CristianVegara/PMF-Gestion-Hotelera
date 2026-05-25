import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';




const Navbar = ({ user, setUser }) => {
    let userRole = localStorage.getItem('role') || '';
if (userRole.startsWith("ROLE_")) {
  userRole = userRole.replace("ROLE_", "");
}
    const navigate = useNavigate();

    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        localStorage.removeItem("user");
        
        if (setUser) {
            setUser(null);
        }
  
        navigate("/login");
    };

    if (!user) {
        return (
            <div className="sidebar">
                <div className="brand">Gestion Mediterraneo</div>
                <ul>
                    <li><Link to="/login">Login</Link></li>
                </ul>
            </div>
        );
    }

    return (
        <div className="sidebar">
            <div className="brand">Gestion Mediterraneo</div>

            <ul>
                <li><Link to="/clients">Recepción</Link></li>
                <li><Link to="/invoice">Facturas</Link></li>
                <li><Link to="/rooms">Habitaciones</Link></li>
                <li><Link to="/bookings">Reservas</Link></li>
                <li><Link to="/activities">Actividades</Link></li>
                <li><Link to="/shifts">Turnos</Link></li>
                <li><Link to="/rooms/price/all">Historial de precios</Link></li>
                {(userRole == 'ADMIN' || userRole == 'SUPERVISOR') && (
                    <li><Link to="/user/form">Crear usuario</Link></li>
                )}
                {(userRole == 'ADMIN') && (
                    <li><Link to="/employee/form">Crear empleado</Link></li>                    
                )}

            </ul>

            <div className="sidebar-footer">
                <p className="user-info">👤 {user.username}</p>
                <button onClick={logout} className="btn-logout">
                    Logout
                </button>
            </div>
        </div>
    );
};

export default Navbar;