import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';


const RoomPriceByType = () => {
  const { type } = useParams();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    if (type) {
      const token = localStorage.getItem('user_token');
      
      fetch(`http://localhost:8080/api/dynamic/chart/type/${type.toUpperCase()}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })        .then(res => {
        if (!res.ok) throw new Error('Error al obtener datos del historial');
        return res.json();
      })
      .then(result => {
        if (result && result.labels && result.datasets) {
          const combined = result.labels.map((label, index) => ({
            mes: label,
            valor: result.datasets[index]
          }));
          setData(combined);
        }
        setLoading(false);
      })
      .catch(err => {
        console.error('Error cargando historial:', err);
        setLoading(false);
      });
    }
  }, [type]);
  
  if (loading) return <div className="loading">Cargando historial de {type}...</div>;
  
  return (
  <div style={{ width: '100%', height: 450, padding: '20px', background: 'white', borderRadius: '8px' }}>
    <h2 style={{ color: '#333', marginBottom: '20px' }}>Evolución de Precios: {type}</h2>
    <ResponsiveContainer width="100%" height="90%">
      <AreaChart data={data}>
        <CartesianGrid strokeDasharray="3 3" vertical={false} />
        <XAxis dataKey="mes" />
        <YAxis unit="€" />
        <Tooltip 
        formatter={(value) => [`${value}€`, 'Precio']}
        />
        <Legend verticalAlign="top" height={36}/>
        <Area 
        type="monotone" 
        dataKey="valor" 
        stroke="#82ca9d" 
        fill="#82ca9d" 
        fillOpacity={0.2} 
        name="Histórico de Precio (€)"
        />
      </AreaChart>
    </ResponsiveContainer>
  </div>
  );
};

export default RoomPriceByType;