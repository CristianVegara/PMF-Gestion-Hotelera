import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';

const Navbar = ({ user, setUser }) => {
    const navigate = useNavigate();

    const logout = () => {
        localStorage.removeItem("user");
        setUser(null);
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
                <li><Link to="/clients">Clientes</Link></li>
                <li><Link to="/invoice">Facturas</Link></li>
                <li><Link to="/rooms">Habitaciones</Link></li>
                <li><Link to="/activities">Actividades</Link></li>
                <li><Link to="/shifts">Turnos</Link></li>
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