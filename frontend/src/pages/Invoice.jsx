import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./Invoice.css";

const Invoice = () => {
  
  const [invoices, setInvoices] = useState([]);
  const [sortBy, setSortBy] = useState("id");
  const [direction, setDirection] = useState("asc");
  
  useEffect(() => {
    fetchInvoices();
  }, [sortBy, direction]);
  
  const fetchInvoices = () => {
    fetch(`/api/invoice?sortBy=${sortBy}&direction=${direction}`)
    .then(res => res.json())
    .then(data => setInvoices(data))
    .catch(err => console.error("Error:", err));
  };
  
  const deleteInvoice = (id) => {
    if (window.confirm("¿Eliminar factura?")) {
      fetch(`/api/invoice/${id}`, { method: "DELETE" })
      .then(res => {
        if (res.ok) fetchInvoices();
      });
    }
  };
  
  return (
    <div className="clients-page-wrapper">
    
    <div className="clients-container">
    
    <div className="header-actions">
    <h2 className="title-list">Listado de Facturas</h2>
    
    <Link to="/invoice/form" className="btn-new">
    Nueva Factura
    </Link>
    </div>
    
    {/* CONTROLES */}
    <div className="sort-controls">
    
    <label>
    Ordenar por:
    <select value={sortBy} onChange={e => setSortBy(e.target.value)}>
    <option value="id">ID</option>
    <option value="cliente.nombre">Cliente</option>
    <option value="total">Total</option>
    </select>
    </label>
    
    <label>
    Dirección:
    <select value={direction} onChange={e => setDirection(e.target.value)}>
    <option value="asc">Ascendente</option>
    <option value="desc">Descendente</option>
    </select>
    </label>
    
    </div>
    
    {/* TABLA */}
    <div className="table-responsive">
    <table className="clients-table">
    
    <thead>
    <tr>
    <th>ID</th>
    <th>Cliente</th>
    <th>DNI</th>
    <th>Concepto</th>
    <th>Noches</th>
    <th>Total (€)</th>
    <th>Acciones</th>
    </tr>
    </thead>
    
    <tbody>
    {invoices.map(inv => (
      <tr key={inv.id}>
      <td><strong>{inv.id}</strong></td>
      <td>{inv.cliente?.nombre}</td>
      <td>{inv.cliente?.dni}</td>
      <td>{inv.concepto}</td>
      <td>{inv.noches}</td>
      <td>{inv.total?.toFixed(2)} €</td>
      
      <td className="text-center">
      <Link to={`/invoice/edit/${inv.id}`} className="btn-edit">
      Editar
      </Link>
      <button onClick={() => deleteInvoice(inv.id)} className="btn-delete">
      Eliminar
      </button>
      </td>
      </tr>
    ))}
    </tbody>
    
    </table>
    </div>
    
    </div>
    </div>
  );
};

export default Invoice;