import { useState, useEffect, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './Activity.css';

const ActivityDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [activity, setActivity] = useState(null);
  const [availableClients, setAvailableClients] = useState([]);
  const [showSelection, setShowSelection] = useState(false);
  const [error, setError] = useState(null);

  const todayStr = useMemo(() => {
    const local = new Date();
    const offset = local.getTimezoneOffset();
    const adjusted = new Date(local.getTime() - (offset * 60 * 1000));
    return adjusted.toISOString().split('T')[0];
  }, []);

  useEffect(() => {
    if (id) {
      const token = localStorage.getItem('user_token');

      fetch(`/api/activities/${id}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      .then(res => res.ok ? res.json() : Promise.reject('Error'))
      .then(data => setActivity(data))
      .catch(err => setError(err.message));

      const fetchInHouseBookings = async () => {
        try {
          const response = await fetch(`/api/bookings/date?date=${todayStr}`, {
            headers: { 'Authorization': `Bearer ${token}` }
          });
          const data = await response.json();
          const bookings = Array.isArray(data) ? data : [];
          const inHouse = bookings.filter(book => book.checkInStatus === 'DENTRO');
          setAvailableClients(inHouse.map(b => b.cliente).filter(c => c !== null));
        } catch (err) {
          setAvailableClients([]);
        }
      };

      fetchInHouseBookings();
    }
  }, [id, todayStr]);

  const syncClientsWithServer = (updatedActivity) => {
    const token = localStorage.getItem('user_token');
    
    fetch(`/api/activities/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(updatedActivity)
    })
    .catch(err => console.error(err));
  };

  const addClient = (client) => {
    setActivity(prev => {
      const updatedList = [...(prev.clients || []), client];
      const updatedActivity = { ...prev, clients: updatedList };
      syncClientsWithServer(updatedActivity);
      return updatedActivity;
    });
    setShowSelection(false);
  };

  const removeClient = (clientId) => {
    setActivity(prev => {
      const updatedList = (prev.clients || []).filter(c => c.id !== clientId);
      const updatedActivity = { ...prev, clients: updatedList };
      syncClientsWithServer(updatedActivity);
      return updatedActivity;
    });
  };

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
  if (!activity) return <div>Cargando actividad...</div>;

  const clients = activity.clients || [];
  const totalHuecosVacios = Math.max(0, (activity.maxParticipantes || 0) - clients.length);

  return (
    <div className="activity-page">
      <div className="activity-card-header">
        <div className="header-info">
          <h1>{activity.descripcion}</h1>
          <p className="fecha-texto">{formatRangoHorario(activity.fechaComienzo, activity.fechaFin)}</p>
        </div>
        <div className="stats-badge">
          <span className="current">{clients.length}</span>
          <span className="separator">/</span>
          <span className="total">{activity.maxParticipantes} plazas</span>
        </div>
      </div>

      {showSelection && (
        <div className="client-selection-modal">
          <h3>Seleccionar cliente:</h3>
          <div className="selection-list">
            {availableClients
              .filter(c => !clients.find(oc => oc.id === c.id))
              .map(client => (
                <button key={client.id} onClick={() => addClient(client)}>
                  {client.nombre}
                </button>
              ))}
          </div>
          <button onClick={() => setShowSelection(false)}>Cancelar</button>
        </div>
      )}

      <div className="slots-grid">
        {clients.map((cliente) => (
          <div key={cliente.id} className="activity-slot occupied">
            <div className="slot-content">
              <div className="user-icon">👤</div>
              <div className="client-data">
                <span className="client-name">{cliente.nombre}</span>
                <span className="client-dni">{cliente.dni}</span>
              </div>
            </div>
            <div 
                className="status-tag clickable" 
                style={{ cursor: 'pointer' }} 
                onClick={() => removeClient(cliente.id)}
            >
                Eliminar
            </div>
          </div>
        ))}

        {[...Array(totalHuecosVacios)].map((_, index) => (
          <div key={`empty-${index}`} className="activity-slot available" onClick={() => setShowSelection(true)}>
            <div className="slot-content">
              <span className="plus-icon">+</span>
              <span className="avail-text">Plaza Libre</span>
            </div>
          </div>
        ))}
      </div>

      <button className="btn-back" onClick={() => navigate(-1)}>← Volver al listado</button>
    </div>
  );
};

export default ActivityDetail;