import { Link } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
    return (
        <div className="sidebar">
            <div className="brand">Gestion Mediterraneo</div>
            <ul>
            {/* <li><Link to="/">Home</Link></li> */}
                <li><Link to="/login">Login</Link></li>
                <li><Link to="/clients">Clientes</Link></li>
            {/* <li><Link to="/clients/form">Nuevo Cliente</Link></li> */}
                <li><Link to="/invoice">Facturas</Link></li>
                <li><Link to="/rooms">Habitaciones</Link></li
            {/* <li><Link to="/invoice/form">Nueva Factura</Link></li> */}
            </ul>
        </div>
    );
     {/*
  return (
    <nav className="navbar">
      <Link to="/" className="nav-link">Inicio</Link>
      <Link to="/clients" className="nav-link">Clientes</Link>
      <Link to="/invoice" className="nav-link">Facturas</Link>
      
      <Link to="/users" className="nav-link">Usuarios</Link> 
    </nav>
  );*/}
};

export default Navbar;