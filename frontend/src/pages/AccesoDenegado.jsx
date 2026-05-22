import React from 'react';
import { useNavigate } from 'react-router-dom';
import './AccesoDenegado.css';

export default function AccesoDenegado() {
    const navigate = useNavigate();

    return (
        <div className="acceso-denegado-container">
            <div className="acceso-denegado-card">
                <h2 className="acceso-denegado-titulo">Acceso Denegado</h2>
                <p className="acceso-denegado-texto">
                    No tienes los permisos necesarios para acceder a esta sección del sistema del hotel.
                </p>
                <button className="acceso-denegado-boton" onClick={() => navigate('/')}>
                    Volver al Inicio
                </button>
            </div>
        </div>
    );
}