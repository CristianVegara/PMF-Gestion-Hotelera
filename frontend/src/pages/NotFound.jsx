import { useNavigate } from 'react-router-dom';
import './NotFound.css';

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <div className="notfound-wrapper">
      <div className="notfound-grid-bg" aria-hidden="true" />

      <div className="notfound-content">
        <div className="notfound-code-block">
          <span className="notfound-label">ERROR</span>
          <h1 className="notfound-number">404</h1>
        </div>

        <div className="notfound-divider" />

        <div className="notfound-message">
          <h2 className="notfound-title">Página no encontrada</h2>
          <p className="notfound-desc">
            La dirección que has introducido no existe en el sistema de gestión.
          </p>
        </div>

        <div className="notfound-actions">
          <button className="notfound-btn-primary" onClick={() => navigate('/clients')}>
            Ir al inicio
          </button>
          <button className="notfound-btn-secondary" onClick={() => navigate(-1)}>
            ← Volver atrás
          </button>
        </div>
      </div>
    </div>
  );
};

export default NotFound;
