import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Clients from './Clients';
import ClientsInHouse from './ClientsInHouse';
import ArrivalsToday from './ArrivalsToday';
import DeparturesToday from './DeparturesToday';
import './ClientsManager.css';

const ClientsManager = () => {
    const [activeTab, setActiveTab] = useState('inhouse');
    const [sharedDate, setSharedDate] = useState(new Date().toISOString().split('T')[0]);
    const navigate = useNavigate();

    let userRole = localStorage.getItem('role') || '';
    if (userRole.startsWith("ROLE_")) {
        userRole = userRole.replace("ROLE_", "");
    }
    
    return (
    <div className="manager-container">
        <header className="dashboard-header">
            <h1>Panel de Recepción</h1>
            <div className="date-box"/> 
            
            {userRole !== 'USER' && (
                <button type="button" onClick={() => navigate('/bookings/form')} className="btn-edit">
                    Nueva reserva
                </button>
            )}
        </header>
        
        <div className="tabs-container">
            <button className={`tab-button ${activeTab === 'inhouse' ? 'active' : ''}`} onClick={() => setActiveTab('inhouse')}>In-House</button>
            <button className={`tab-button ${activeTab === 'arrivals' ? 'active' : ''}`} onClick={() => setActiveTab('arrivals')}>Entradas</button>
            <button className={`tab-button ${activeTab === 'departures' ? 'active' : ''}`} onClick={() => setActiveTab('departures')}>Salidas</button>
            <button className={`tab-button ${activeTab === 'list' ? 'active' : ''}`} onClick={() => setActiveTab('list')}>Listado General</button>
        </div>
        
        <div className="tab-content">
            {activeTab === 'inhouse' && <ClientsInHouse date={sharedDate} />}
            {activeTab === 'arrivals' && <ArrivalsToday date={sharedDate} />}
            {activeTab === 'departures' && <DeparturesToday date={sharedDate} />}
            {activeTab === 'list' && <Clients />}
        </div>
    </div>
    );
};

export default ClientsManager;