import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import Navbar from './components/Navbar'; 
import Home from './pages/Home';
import Clients from './pages/Clients';
import ClientForm from './pages/ClientForm';
import Invoice from './pages/Invoice';
import InvoiceForm from './pages/InvoiceForm';
import Login from './pages/Login';
import Rooms from './pages/Rooms';
import RoomForm from './pages/RoomsForm';
import Activities from './pages/Activities';
import Activity from './pages/Activity';
import Shifts  from './pages/Shifts';


function App() {

  const [user, setUser] = useState(null);

  // cargar sesión al inicio
  useEffect(() => {
    const savedUser = localStorage.getItem("user");
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
  }, []);

  return (
    <Router>

      {/* pasamos user al navbar */}
      <Navbar user={user} setUser={setUser} />

      <div className="main-content">
        <Routes>

          <Route path="/" element={<Login setUser={setUser} />} />
          <Route path="/login" element={<Login setUser={setUser} />} />

          <Route path="/activities" element={<Activities />} />
          <Route path="/activities/:id" element={<Activity />} />

          <Route path="/clients" element={<Clients />} />
          <Route path="/clients/form" element={<ClientForm />} />
          <Route path="/clients/edit/:id" element={<ClientForm />} />

          <Route path="/invoice" element={<Invoice />} />
          <Route path="/invoice/form" element={<InvoiceForm />} />
          <Route path="/invoice/edit/:id" element={<InvoiceForm />} />

          <Route path="/rooms" element={<Rooms />} />
          <Route path="/rooms/form" element={<RoomForm />} />
          <Route path="/rooms/edit/:id" element={<RoomForm />} />

          <Route path="/shifts" element={<Shifts />} /> 
        </Routes>
      </div>
    </Router>
  );
}

export default App;