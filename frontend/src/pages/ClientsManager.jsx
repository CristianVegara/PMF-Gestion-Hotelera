import { useState } from 'react';
import Clients from './Clients';
import ClientsInHouse from './ClientsInHouse';
import './ClientsManager.css';

const ClientsManager = () => {
    const [activeTab, setActiveTab] = useState('inhouse');

    return (
        <div className="manager-container">
            <div className="tabs-container">
                <button 
                    className={`tab-button ${activeTab === 'inhouse' ? 'active' : ''}`}
                    onClick={() => setActiveTab('inhouse')}
                >
                    Huéspedes In-House
                </button>
                <button 
                    className={`tab-button ${activeTab === 'list' ? 'active' : ''}`}
                    onClick={() => setActiveTab('list')}
                >
                    Listado General
                </button>
            </div>

            <div className="tab-content">
                {activeTab === 'inhouse' ? <ClientsInHouse /> : <Clients />}
            </div>
        </div>
    );
};

export default ClientsManager;