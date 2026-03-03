import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import Navbar from './components/Navbar'; 
import Home from './pages/Home';
import Clients from './pages/Clients';
import ClientForm from './pages/ClientForm';

function App() {
  return (
    <Router>
      <Navbar />
      <div style={{ marginTop: '80px', padding: '20px' }}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/clients" element={<Clients />} />
          <Route path="/clients/form" element={<ClientForm />} />
          <Route path="/clients/edit/:id" element={<ClientForm />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;