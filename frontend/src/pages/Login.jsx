import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Login.css";

const Login = ({ setUser }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();

    setError("");
    if (username.trim() === "" || password.trim() === "") {
      setError("Rellena todos los campos");
      return;
    }

    if (username === "admin" && password === "admin") {
      const user = {
        username,
        role: "ADMIN"
      };

      localStorage.setItem("user", JSON.stringify(user));

      if (setUser) {
        setUser(user);
      }

      navigate("/clients");
    } else {
      setError("Credenciales incorrectas");
    }
  };

  return (
    <div className="login-page-wrapper">
      <div className="login-container">

        <h1>Gestión Mediterráneo</h1>

        <div className="login-placeholder">
          <h2>Bienvenido</h2>
          <p>Introduce tus credenciales para acceder al sistema</p>

          {error && (
            <p style={{ color: "red", marginBottom: "10px" }}>
              {error}
            </p>
          )}

          <form onSubmit={handleLogin}>
            <input
              type="text"
              placeholder="Usuario"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
            />

            <input
              type="password"
              placeholder="Contraseña"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
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