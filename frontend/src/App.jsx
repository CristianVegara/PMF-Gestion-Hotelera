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
import ClientDetails from './pages/ClientDetails'
import Invoice from './pages/Invoice';
import InvoiceForm from './pages/InvoiceForm';
import Login from './pages/Login';
import Rooms from './pages/Rooms';
import RoomForm from './pages/RoomsForm';
import Bookings from './pages/Bookings';
import Activities from './pages/Activities';
import Activity from './pages/Activity';
import Shifts  from './pages/Shifts';
import RoomPriceType from './pages/RoomPriceType';
import RoomPriceAll from './pages/RoomPriceAll';


function App() {

  const [user, setUser] = useState(null);

  useEffect(() => {
    const savedUser = localStorage.getItem("user");
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
  }, []);

  return (
    <Router>
      <Navbar user={user} setUser={setUser} />

      <div className="main-content">
        <Routes>

          <Route path="/" element={<Login setUser={setUser} />} />
          <Route path="/login" element={<Login setUser={setUser} />} />

          <Route path="/activities" element={<Activities />} />
          <Route path="/activities/:id" element={<Activity />} />

          <Route path="/bookings" element={<Bookings />} />
          <Route path="/bookings/:id" element={<BookingDetails/>}/>
          <Route path="/bookings/form" element={<BookingForm/>}/>

          <Route path="/clients" element={<ClientsManager />} />
          <Route path="/clients/form" element={<ClientForm />} />
          <Route path="/clients/edit/:id" element={<ClientForm />} />
          <Route path="/clients/:id" element={<ClientDetails />} />

          <Route path="/invoice" element={<Invoice />} />
          <Route path="/invoice/form" element={<InvoiceForm />} />
          <Route path="/invoice/edit/:id" element={<InvoiceForm />} />

          <Route path="/rooms" element={<Rooms />} />
          <Route path="/rooms/form" element={<RoomForm />} />
          <Route path="/rooms/edit/:id" element={<RoomForm />} />
          <Route path="/rooms/:id" element={<RoomDetails />} />
          
          <Route path="/rooms/price/all" element={<RoomPriceAll />} />
          <Route path="/rooms/price/:type" element={<RoomPriceType />} />

          

          <Route path="/shifts" element={<Shifts />} /> 
          <Route path="*" element={<NotFound />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;