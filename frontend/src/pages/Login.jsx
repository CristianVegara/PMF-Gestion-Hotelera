import { useState } from "react";
import "./Login.css";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = (e) => {
    e.preventDefault();
    alert(`Usuario: ${username}\nContraseña: ${password}`);
  };

  return (
    <div className="login-page-wrapper">
      <div className="login-container">
        <h1>Gestión Mediterráneo</h1>
        <div className="login-placeholder">
          <h2>Bienvenido</h2>
          <p>Introduce tus credenciales para acceder al sistema</p>
          <form onSubmit={handleLogin}>
            <input
              type="text"
              placeholder="Usuario"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Contraseña"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <button type="submit" className="btn-login">
              Iniciar Sesión
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;