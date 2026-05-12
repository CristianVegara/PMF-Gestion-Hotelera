import React, { useState, useEffect, useMemo } from 'react';
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer, Legend
} from 'recharts';

const RoomPriceAll = () => {
  const [rawData, setRawData] = useState([]); // Datos originales del server
  const [loading, setLoading] = useState(true);
  const [visibility, setVisibility] = useState({}); // Estado de los checkboxes
  const [showAverage, setShowAverage] = useState(true); // Estado checkbox media

  useEffect(() => {
    fetch('http://localhost:8080/api/rooms/dynamic/chart/all-types')
      .then(res => res.json())
      .then(result => {
        const formattedData = result.labels.map((label, index) => {
          const entry = { mes: label };
          Object.keys(result.datasets).forEach(type => {
            entry[type] = result.datasets[type][index];
          });
          return entry;
        });

        // Inicializar visibilidad: todos los tipos activos por defecto
        const initialVisibility = {};
        Object.keys(result.datasets).forEach(type => {
          initialVisibility[type] = true;
        });

        setRawData(formattedData);
        setVisibility(initialVisibility);
        setLoading(false);
      })
      .catch(err => {
        console.error("Error:", err);
        setLoading(false);
      });
  }, []);

  // Calculamos la media dinámicamente según lo que esté visible
  const chartData = useMemo(() => {
    return rawData.map(item => {
      const activeTypes = Object.keys(visibility).filter(type => visibility[type]);
      let sum = 0;
      activeTypes.forEach(type => { sum += item[type] || 0; });
      
      return {
        ...item,
        MEDIA: activeTypes.length > 0 ? parseFloat((sum / activeTypes.length).toFixed(2)) : 0
      };
    });
  }, [rawData, visibility]);

  const toggleVisibility = (type) => {
    setVisibility(prev => ({ ...prev, [type]: !prev[type] }));
  };

  if (loading) return <div style={{ padding: '20px' }}>Cargando análisis dinámico...</div>;

  const colors = {
    INDIVIDUAL: '#4f46e5',
    DOBLE: '#10b981',
    SUITE: '#f59e0b',
    MEDIA: '#ef4444' // Rojo para la media
  };

  return (
    <div style={{ padding: '20px', background: '#fff', borderRadius: '12px', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
      <h2 style={{ textAlign: 'center' }}>Análisis Comparativo de Precios</h2>

      {/* PANEL DE CONTROL (Checkboxes) */}
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        gap: '20px', 
        marginBottom: '20px',
        padding: '10px',
        background: '#f8f9fa',
        borderRadius: '8px'
      }}>
        {Object.keys(visibility).map(type => (
          <label key={type} style={{ cursor: 'pointer', fontWeight: 'bold', color: colors[type] || '#333' }}>
            <input 
              type="checkbox" 
              checked={visibility[type]} 
              onChange={() => toggleVisibility(type)} 
            /> {type}
          </label>
        ))}
        <label style={{ cursor: 'pointer', fontWeight: 'bold', color: colors.MEDIA }}>
          <input 
            type="checkbox" 
            checked={showAverage} 
            onChange={() => setShowAverage(!showAverage)} 
          /> PROMEDIO
        </label>
      </div>

      <div style={{ width: '100%', height: 500 }}>
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={chartData} margin={{ top: 10, right: 30, left: 10, bottom: 50 }}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} />
            <XAxis dataKey="mes" tick={{ fontSize: 11 }} angle={-45} textAnchor="end" height={60} />
            <YAxis unit="€" tick={{ fontSize: 12 }} domain={['auto', 'auto']} />
            <Tooltip />
            <Legend verticalAlign="top" height={40} />

            {/* Líneas de tipos de habitación (condicionales) */}
            {Object.keys(visibility).map(type => (
              visibility[type] && (
                <Line
                  key={type}
                  type="monotone"
                  dataKey={type}
                  stroke={colors[type] || '#ccc'}
                  strokeWidth={2}
                  dot={{ r: 3 }}
                  animationDuration={300}
                />
              )
            ))}

            {/* Línea de Media (condicional) */}
            {showAverage && (
              <Line
                type="stepAfter"
                dataKey="MEDIA"
                stroke={colors.MEDIA}
                strokeWidth={4}
                strokeDasharray="5 5"
                dot={false}
                name="MEDIA (Activos)"
                animationDuration={300}
              />
            )}
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default RoomPriceAll;