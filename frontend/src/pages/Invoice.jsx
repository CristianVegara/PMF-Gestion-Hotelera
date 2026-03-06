import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./Invoice.css";

const Invoice = () => {
  const [invoices, setInvoices] = useState([]);

  useEffect(() => {
    fetchInvoices();
  }, []);

  const fetchInvoices = () => {
    fetch("/api/invoice") 
      .then(res => res.json())
      .then(data => setInvoices(data))
      .catch(err => console.error("Error cargando facturas:", err));
  };

  const deleteInvoice = (id) => {
    if (window.confirm("¿Estás seguro de eliminar esta factura?")) {
      fetch(`/api/invoice/${id}`, { method: "DELETE" })
        .then(res => {
          if (res.ok) fetchInvoices();
        })
        .catch(err => console.error(err));
    }
  };

  return (
    <div className="clients-page-wrapper">
      <div className="clients-container">
        <div className="header-actions">
          <h2 className="title-list">Listado de Facturas</h2>
          <Link to="/invoice/form" className="btn-new">Nueva Factura</Link>
        </div>

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
                <th className="text-center">Acciones</th>
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
                  <td>{inv.total?.toFixed(2)}</td>
                  <td className="text-center">
                    <Link to={`/invoice/edit/${inv.id}`} className="btn-edit">Editar</Link>
                    <button onClick={() => deleteInvoice(inv.id)} className="btn-delete">Eliminar</button>
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