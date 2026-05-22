import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import AccesoDenegado from '../pages/AccesoDenegado';

export default function ProtectedLayout({ rolesPermitidos }) {    
    const token = localStorage.getItem('token');
    
    if (!token) {
        return <Navigate to="/login" replace />;
    }

    let userRole = localStorage.getItem('role'); 

    if (!userRole) {
        return <Navigate to="/login" replace />;
    }

    if (userRole.startsWith("ROLE_")) {
        userRole = userRole.replace("ROLE_", "");
    }

    if (!rolesPermitidos.includes(userRole)) {
        return <AccesoDenegado />;
    }

    return <Outlet />;
}