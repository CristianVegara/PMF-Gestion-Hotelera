import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import Navbar from './components/Navbar'; 
import Home from './pages/Home';
import Bookings from './pages/Bookings';
import BookingDetails from './pages/BookingDetails';
import BookingForm from './pages/BookingForm';
import ClientsManager from './pages/ClientsManager';
import ClientForm from './pages/ClientForm';
import ClientDetails from './pages/ClientDetails';
import EmployeeForm from './pages/EmployeeForm';
import Employees from './pages/Employees';
import Invoice from './pages/Invoice';
import InvoiceForm from './pages/InvoiceForm';
import Login from './pages/Login';
import NotFound from './pages/NotFound';
import Rooms from './pages/Rooms';
import RoomForm from './pages/RoomsForm';
import Activities from './pages/Activities';
import Activity from './pages/Activity';
import Shifts from './pages/Shifts';
import RoomPriceType from './pages/RoomPriceType';
import RoomPriceAll from './pages/RoomPriceAll';
import RoomDetails from './pages/RoomDetails';
import UserForm from './pages/UserForm'

import ProtectedLayout from './components/ProtectedLayout';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedUser = localStorage.getItem("user");
    const token = localStorage.getItem("token");

    if (savedUser && token) {
      setUser(JSON.parse(savedUser));
    } else {
      setUser(null);
    }
    setLoading(false);
  }, []);

  if (loading) {
    return <div className="loading-screen">Cargando aplicación...</div>;
  }

  return (
    <Router>
      <Navbar user={user} setUser={setUser} />

      <div className="main-content">
        <Routes>
          <Route path="/" element={<Login setUser={setUser} />} />
          <Route path="/login" element={<Login setUser={setUser} />} />

          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/home" element={<Home />} />
          </Route>
          
          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/activities" element={<Activities />} />
          </Route>
          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/activities/:id" element={<Activity />} />
          </Route>

          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/bookings" element={<Bookings />} />
          </Route>
          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/bookings/:id" element={<BookingDetails />} />
          </Route>
          <Route element={<ProtectedLayout rolesPermitidos={['RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/bookings/form" element={<BookingForm />} />
          </Route>

          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/clients" element={<ClientsManager />} />
          </Route>
          <Route element={<ProtectedLayout rolesPermitidos={['RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/clients/form" element={<ClientForm />} />
          </Route>
          
          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/clients/:id" element={<ClientDetails />} />
          </Route>

          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/clients/edit/:id" element={<ClientForm />} />
          </Route>


          <Route element={<ProtectedLayout rolesPermitidos={['RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/employees" element={<Employees />} />
            <Route path="/employees/form" element={<EmployeeForm />} />
            <Route path="/employees/edit/:id" element={<EmployeeForm />} />
          </Route>

          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/invoice" element={<Invoice />} />
          </Route>
          <Route element={<ProtectedLayout rolesPermitidos={['RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/invoice/form" element={<InvoiceForm />} />
          </Route>
          <Route element={<ProtectedLayout rolesPermitidos={['RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/invoice/edit/:id" element={<InvoiceForm />} />
          </Route>

          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/rooms" element={<Rooms />} />
          </Route>
          <Route element={<ProtectedLayout rolesPermitidos={['RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/rooms/form" element={<RoomForm />} />
          </Route>
          <Route element={<ProtectedLayout rolesPermitidos={['RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/rooms/edit/:id" element={<RoomForm />} />
          </Route>
          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/rooms/:id" element={<RoomDetails />} />
          </Route>
          
          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/rooms/price/all" element={<RoomPriceAll />} />
          </Route>
          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/rooms/price/:type" element={<RoomPriceType />} />
          </Route>          

          <Route element={<ProtectedLayout rolesPermitidos={['USER', 'RECEPCIONISTA', 'SUPERVISOR', 'ADMIN']} />} >
            <Route path="/shifts" element={<Shifts />} />
          </Route>

          <Route element={<ProtectedLayout rolesPermitidos={['ADMIN']} />} >
            <Route path="/user/form" element={<UserForm />} />
          </Route>

          <Route path="*" element={<NotFound />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;