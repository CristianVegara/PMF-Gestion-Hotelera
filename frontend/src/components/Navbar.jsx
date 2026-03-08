import { Link } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  return (
    <nav className="navbar">
      <Link to="/" className="nav-link">Inicio</Link>
      <Link to="/clients" className="nav-link">Clientes</Link>
      <Link to="/invoice" className="nav-link">Facturas</Link>
      <Link to="/rooms">Habitaciones</Link>
      {/* <Link to="/users" className="nav-link">Usuarios</Link> */}
    </nav>
  );
};

export default Navbar;