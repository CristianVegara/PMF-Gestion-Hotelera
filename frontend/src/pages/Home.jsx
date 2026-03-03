import { useState, useEffect } from 'react';

const Home = () => {
  const [message, setMessage] = useState('Cargando...');

  useEffect(() => {
    fetch('/api/test') 
      .then((res) => res.text())
      .then((data) => setMessage(data))
      .catch(() => setMessage('Error de conexión'));
  }, []);

  return (
    <div>
      <h1>Bienvenido al Sistema de Gestión</h1>
      <p>Utiliza el menú superior para gestionar los clientes o usuarios.</p>
      <p>Backend Status: {message}</p>
    </div>
  );
};

export default Home;