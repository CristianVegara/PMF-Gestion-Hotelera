import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; 
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import './Activities.css';

const token = localStorage.getItem('user_token');

const Activities = () => {
  const [activities, setActivities] = useState([]);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const navigate = useNavigate(); 
  
  const [newActivity, setNewActivity] = useState({
    descripcion: '',
    precio: '',
    fechaComienzo: '',
    fechaFin: ''
  });
  
  useEffect(() => {
    fetchActivities();
  }, []);
  
  const fetchActivities = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/activities', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });
      const data = await response.json();
      setActivities(data);
    } catch (error) {
      console.error(error);
    }
  };
  
  const formatForBackend = (value) => {
    if (!value) return null;
    return value.length === 16 ? value + ':00' : value;
  };
  
  const createActivity = async (e) => {
    e.preventDefault();
    const payload = {
      descripcion: newActivity.descripcion,
      precio: parseFloat(newActivity.precio),
      fechaComienzo: formatForBackend(newActivity.fechaComienzo),
      fechaFin: formatForBackend(newActivity.fechaFin)
    };
    
    try {
      const response = await fetch('http://localhost:8080/api/activities', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });
      if (response.ok) {
        setNewActivity({ descripcion: '', precio: '', fechaComienzo: '', fechaFin: '' });
        fetchActivities();
      }
    } catch (error) {
      console.error(error);
    }
  };
  
  const parseDate = (str) => (str ? new Date(str.replace('T', ' ')) : null);
  const normalize = (d) => new Date(d.getFullYear(), d.getMonth(), d.getDate());
  
  const formatTime = (dateTime) => {
    const date = parseDate(dateTime);
    return date && !isNaN(date) 
    ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) 
    : '';
  };
  
  const activitiesOfDay = activities.filter(act => {
    const start = parseDate(act.fechaComienzo);
    const end = parseDate(act.fechaFin);
    const selected = normalize(selectedDate);
    return start && end && selected >= normalize(start) && selected <= normalize(end);
  });
  
  const hasActivity = (date) => {
    const day = normalize(date);
    return activities.some(act => {
      const start = parseDate(act.fechaComienzo);
      const end = parseDate(act.fechaFin);
      return start && end && day >= normalize(start) && day <= normalize(end);
    });
  };
  
  const deleteActivity = async (id, e) => {
    e.stopPropagation(); 
    if (!window.confirm('¿Eliminar esta actividad?')) return;
    try {
      const response = await fetch(`http://localhost:8080/api/activities/${id}`, { 
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (response.ok) fetchActivities();
    } catch (error) {
      console.error(error);
    }
  };
  
  return (
    <div className="activities-page">
    <header className="activities-header">
    <h1>Gestión de Actividades</h1>
    <p>Organiza y supervisa el calendario de eventos</p>
    </header>
    
    <section className="activity-form-container">
    <div className="section-title">
    <h2>Añadir Nueva Actividad</h2>
    </div>
    <form onSubmit={createActivity} className="activity-form">
    <div className="input-group">
    <label>Descripción</label>
    <input
    type="text"
    placeholder="Ej: Clase de Yoga"
    value={newActivity.descripcion}
    onChange={(e) => setNewActivity({ ...newActivity, descripcion: e.target.value })}
    required
    />
    </div>
    <div className="input-group">
    <label>Precio (€)</label>
    <input
    type="number"
    placeholder="0.00"
    value={newActivity.precio}
    onChange={(e) => setNewActivity({ ...newActivity, precio: e.target.value })}
    required
    />
    </div>
    <div className="input-group">
    <label>Inicio</label>
    <input
    type="datetime-local"
    value={newActivity.fechaComienzo}
    onChange={(e) => setNewActivity({ ...newActivity, fechaComienzo: e.target.value })}
    required
    />
    </div>
    <div className="input-group">
    <label>Fin</label>
    <input
    type="datetime-local"
    value={newActivity.fechaFin}
    onChange={(e) => setNewActivity({ ...newActivity, fechaFin: e.target.value })}
    required
    />
    </div>
    <button type="submit" className="btn-primary">Crear actividad</button>
    </form>
    </section>
    
    <main className="calendar-container">
    <div className="calendar-card">
    <Calendar
    onChange={setSelectedDate}
    value={selectedDate}
    tileClassName={({ date, view }) =>
      view === 'month' && hasActivity(date) ? 'highlight' : null
  }
  />
  </div>
  
  <div className="activities-day">
  <div className="section-title">
  <h3>Actividades del {selectedDate.toLocaleDateString()}</h3>
  </div>
  
  <div className="activities-list">
  {activitiesOfDay.length === 0 ? (
    <div className="empty-state">
    <p>No hay eventos programados para este día.</p>
    </div>
  ) : (
    activitiesOfDay.map((act) => (
      <div 
      key={act.id} 
      className="activity-card clickable-card" 
      onClick={() => navigate(`/activities/${act.id}`)} 
      >
      <div className="activity-info">
      <strong>{act.descripcion}</strong>
      <div className="activity-meta">
      <span>🕒 {formatTime(act.fechaComienzo)} - {formatTime(act.fechaFin)}</span>
      <span className="price-tag">{act.precio}€</span>
      </div>
      </div>
      <button
      className="delete-icon"
      onClick={(e) => deleteActivity(act.id, e)} 
      >
      Eliminar
      </button>
      </div>
    ))
  )}
  </div>
  </div>
  </main>
  </div>
);
};

export default Activities;