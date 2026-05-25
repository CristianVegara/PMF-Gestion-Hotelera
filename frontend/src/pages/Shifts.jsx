import React, { useState, useEffect } from 'react';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import './Shifts.css';


const Shifts = ({ token }) => {
  const [shifts, setShifts] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [schedules, setSchedules] = useState([]);
  const [selectedDate, setSelectedDate] = useState(new Date());
  
  const [newShift, setNewShift] = useState({
    employeeId: '',
    scheduleId: '',
    fecha: '',
    observaciones: ''
  });
  
  useEffect(() => {
    const controller = new AbortController();
    
    const fetchInitialData = async () => {
      try {
        const token = localStorage.getItem('user_token');
        
        const [resShifts, resEmps, resSchs] = await Promise.all([
        fetch('http://localhost:8080/api/shifts', { 
          signal: controller.signal,
          headers: { 'Authorization': `Bearer ${token}` }
        }),
        fetch('http://localhost:8080/api/employees', { 
          signal: controller.signal,
          headers: { 'Authorization': `Bearer ${token}` }
        }),
        fetch('http://localhost:8080/api/schedules', { 
          signal: controller.signal,
          headers: { 'Authorization': `Bearer ${token}` }
        })
        ]);
        
        const dataShifts = await resShifts.json();
        const dataEmps = await resEmps.json();
        const dataSchs = await resSchs.json();
        
        setShifts(dataShifts || []);
        setEmployees(dataEmps || []);
        setSchedules(dataSchs || []);
      } catch (error) {
        if (error.name !== 'AbortError') {
          console.error("Error al cargar datos:", error);
        }
      }
    };
    
    fetchInitialData();
    
    return () => {
      controller.abort();
    };
  }, [token]);
  
  const refetchData = async () => {
    try {
      const token = localStorage.getItem('user_token');
      
      const [resShifts, resEmps, resSchs] = await Promise.all([
      fetch('http://localhost:8080/api/shifts', { 
        headers: { 'Authorization': `Bearer ${token}` }
      }),
      fetch('http://localhost:8080/api/employees', { 
        headers: { 'Authorization': `Bearer ${token}` }
      }),
      fetch('http://localhost:8080/api/schedules', { 
        headers: { 'Authorization': `Bearer ${token}` }
      })
      ]);
      
      setShifts(await resShifts.json() || []);
      setEmployees(await resEmps.json() || []);
      setSchedules(await resSchs.json() || []);
    } catch (error) {
      console.error("Error al recargar datos:", error);
    }
  };
  
  const createShift = async (e) => {
    e.preventDefault();
    const payload = {
      fecha: `${newShift.fecha}T00:00:00`,
      employee: { id: parseInt(newShift.employeeId) },
      schedule: { id: parseInt(newShift.scheduleId) },
      observaciones: newShift.observaciones
    };
    
    try {
      const token = localStorage.getItem('user_token');
      
      const response = await fetch('http://localhost:8080/api/shifts', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });
      if (response.ok) {
        setNewShift({ employeeId: '', scheduleId: '', fecha: '', observaciones: '' });
        refetchData();
        alert("Turno asignado con éxito");
      }
    } catch (error) {
      console.error(error);
    }
  };
  
  const deleteShift = async (id, e) => {
    e.stopPropagation();
    if (!window.confirm('¿Eliminar este turno?')) return;
    try {
      const token = localStorage.getItem('user_token');
      
      const response = await fetch(`http://localhost:8080/api/shifts/${id}`, { 
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (response.ok) refetchData();
    } catch (error) {
      console.error(error);
    }
  };
  
  const normalize = (d) => new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime();
  
  const shiftsOfDay = shifts.filter(s => {
    const shiftDate = new Date(s.fecha);
    return normalize(shiftDate) === normalize(selectedDate);
  });
  
  const hasShift = (date) => {
    const day = normalize(date);
    return shifts.some(s => normalize(new Date(s.fecha)) === day);
  };
  
  return (
  <div className="shifts-page">
    <header className="shifts-header">
      <h1>Gestión de Turnos</h1>
      <p>Planificación de horarios y personal del hotel</p>
    </header>
    
    <section className="shift-form-container">
      <div className="section-title">
        <h2>Asignar Nuevo Turno</h2>
      </div>
      <form onSubmit={createShift} className="shift-form">
        <div className="input-group">
          <label>Empleado</label>
          <select 
          value={newShift.employeeId} 
          onChange={(e) => setNewShift({...newShift, employeeId: e.target.value})}
          required
          >
          <option value="">Seleccionar empleado...</option>
          {employees.map(emp => (
            <option key={emp.id} value={emp.id}>{emp.nombre} {emp.apellido}</option>
            ))}
          </select>
        </div>
        <div className="input-group">
          <label>Turno</label>
          <select 
          value={newShift.scheduleId} 
          onChange={(e) => setNewShift({...newShift, scheduleId: e.target.value})}
          required
          >
          <option value="">Seleccionar horario...</option>
          {schedules.map(sch => (
            <option key={sch.id} value={sch.id}>{sch.nombreTurno} ({sch.horaEntrada.substring(0,5)}-{sch.horaSalida.substring(0,5)})</option>
            ))}
          </select>
        </div>
        <div className="input-group">
          <label>Fecha</label>
          <input
          type="date"
          value={newShift.fecha}
          onChange={(e) => setNewShift({ ...newShift, fecha: e.target.value })}
          required
          />
        </div>
        <div className="input-group">
          <label>Notas</label>
          <input
          type="text"
          placeholder="Opcional..."
          value={newShift.observaciones}
          onChange={(e) => setNewShift({ ...newShift, observaciones: e.target.value })}
          />
        </div>
        <button type="submit" className="btn-primary">Asignar</button>
      </form>
    </section>
    
    <main className="calendar-grid">
      <div className="calendar-card">
        <Calendar
        onChange={setSelectedDate}
        value={selectedDate}
        tileClassName={({ date, view }) =>
        view === 'month' && hasShift(date) ? 'highlight-yellow' : null
      }
      />
    </div>
    
    <div className="shifts-list-container">
      <div className="section-title">
        <h3>Turnos: {selectedDate.toLocaleDateString()}</h3>
      </div>
      <div className="shifts-list">
        {shiftsOfDay.length === 0 ? (
          <div className="empty-state">
            <p>Sin turnos asignados hoy.</p>
          </div>
          ) : (
          shiftsOfDay.map((shift) => (
          <div key={shift.id} className="shift-card">
            <div className="shift-info">
              <strong>{shift.employee?.nombre} {shift.employee?.apellido}</strong>
              <div className="shift-meta">
                <span>{shift.schedule?.horaEntrada.substring(0,5)} - {shift.schedule?.horaSalida.substring(0,5)}</span>
                <span className="badge-yellow">{shift.schedule?.nombreTurno}</span>
              </div>
              {shift.observaciones && <small>{shift.observaciones}</small>}
            </div>
            
            <button className="btn-delete" onClick={(e) => deleteShift(shift.id, e)}>Eliminar</button>
          </div>
          ))
          )}
        </div>
      </div>
    </main>
  </div>
  );
};

export default Shifts;