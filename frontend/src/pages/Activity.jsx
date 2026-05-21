import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './Activity.css';

const token = localStorage.getItem('user_token');


const ActivityDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [activity, setActivity] = useState(null);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    if (id) {
      fetch(`/api/activities/${id}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      .then(res => {
        if (!res.ok) throw new Error('No se pudo encontrar la actividad');
        return res.json();
      })
      .then(data => setActivity(data))
      .catch(err => setError(err.message));
    }
  }, [id]);
  
  const formatRangoHorario = (inicio, fin) => {
    if (!inicio || !fin) return "Fecha no disponible";
    const dateIn = new Date(inicio);
    const dateOut = new Date(fin);
    
    const dia = dateIn.getDate().toString().padStart(2, '0');
    const mes = (dateIn.getMonth() + 1).toString().padStart(2, '0');
    const anio = dateIn.getFullYear();
    const horaIn = dateIn.getHours().toString().padStart(2, '0');
    const minIn = dateIn.getMinutes().toString().padStart(2, '0');
    const horaOut = dateOut.getHours().toString().padStart(2, '0');
    const minOut = dateOut.getMinutes().toString().padStart(2, '0');
    
    return `${dia}/${mes}/${anio} ${horaIn}:${minIn}h - ${horaOut}:${minOut}h`;
  };
  
  if (error) return <div className="error-msg">Error: {error}</div>;
  if (!activity) return <div className="loader">Cargando actividad...</div>;
  
  const nombre = activity.descripcion;
  const clientes = activity.clients || [];
  const maxParticipantes = activity.maxParticipantes || 0;
  const totalHuecosVacios = Math.max(0, maxParticipantes - clientes.length);
  
  return (
    <div className="activity-page">
    <div className="activity-card-header">
    <div className="header-info">
    <h1>{nombre}</h1>
    <p className="fecha-texto">{formatRangoHorario(activity.fechaComienzo, activity.fechaFin)}</p>
    </div>
    <div className="stats-badge">
    <span className="current">{clientes.length}</span>
    <span className="separator">/</span>
    <span className="total">{maxParticipantes} plazas</span>
    </div>
    </div>
    
    <div className="slots-grid">
    {clientes.map((cliente) => (
      <div 
      key={cliente.id} 
      className="activity-slot occupied clickable"
      onClick={() => navigate(`/clients/edit/${cliente.id}`)}
      >
      <div className="slot-content">
      <div className="user-icon">👤</div>
      <div className="client-data">
      <span className="client-name">{cliente.nombre}</span>
      <span className="client-dni">{cliente.dni}</span>
      </div>
      </div>
      <div className="status-tag">Ocupado</div>
      </div>
    ))}
    
    {[...Array(totalHuecosVacios)].map((_, index) => (
      <div 
      key={`empty-${index}`} 
      className="activity-slot available"
      onClick={() => console.log("Hueco vacío")}
      >
      <div className="slot-content">
      <span className="plus-icon">+</span>
      <span className="avail-text">Plaza Libre</span>
      </div>
      </div>
    ))}
    </div>
    
    <button className="btn-back" onClick={() => navigate(-1)}>
    ← Volver al listado
    </button>
    </div>
  );
};

export default ActivityDetail;