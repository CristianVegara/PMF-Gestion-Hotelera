import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./InvoiceForm.css";

const InvoiceForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [invoice, setInvoice] = useState({
    clienteId: "",
    concepto: "",
    noches: 1,
    precio: 0
  });

  const [clients, setClients] = useState([]);
  const [errors, setErrors] = useState([]);

  useEffect(() => {
    fetch("/api/clients")
      .then(res => res.json())
      .then(data => setClients(data))
      .catch(err => console.error(err));
  }, []);

  useEffect(() => {
    if (id) {
      fetch(`/api/invoice/${id}`)
        .then(res => {
          if (!res.ok) throw new Error();
          return res.json();
        })
        .then(data => {
          if (data) {
            setInvoice({
              clienteId: data.cliente?.id || "",
              concepto: data.concepto || "",
              noches: data.noches || 1,
              precio: data.precio || 0
            });
          }
        })
        .catch(() => navigate("/invoice"));
    }
  }, [id, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setInvoice(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const subtotal = invoice.noches * invoice.precio;
  const iva = subtotal * 0.10;
  const total = subtotal + iva;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors([]);

    const method = id ? "PUT" : "POST";
    const url = id ? `/api/invoice/${id}` : "/api/invoice";

    const payload = {
      cliente: { id: invoice.clienteId },
      concepto: invoice.concepto,
      noches: invoice.noches,
      precio: invoice.precio,
      subtotal,
      iva,
      total,
      pagada: false
    };

    try {
      const response = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      const data = await response.json();

      if (response.ok) {
        alert(data.mensaje || "Ok");
        navigate("/invoice");
      } else if (response.status === 400 && data.errors) {
        setErrors(data.errors);
      } else {
        alert(data.mensaje || "Error");
      }
    } catch (err) {
      alert("Error de conexión");
    }
  };

  return (
    <div className="form-container">
      <h2>{id ? "Editar Factura" : "Nueva Factura"}</h2>

      {errors.length > 0 && (
        <div className="alert-errors" role="alert" aria-label="Lista de errores">
          <ul>
            {errors.map((err, index) => (
              <li key={index}>{err}</li>
            ))}
          </ul>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="clienteId">Cliente</label>
          <select id="clienteId" name="clienteId" value={invoice.clienteId} onChange={handleChange} required>
            <option value="">Selecciona un cliente</option>
            {clients.map(c => (
              <option key={c.id} value={c.id}>{c.nombre} ({c.dni})</option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="concepto">Concepto</label>
          <input id="concepto" type="text" name="concepto" value={invoice.concepto} onChange={handleChange} required />
        </div>

        <div className="form-group">
          <label htmlFor="noches">Nº de noches</label>
          <input id="noches" type="number" name="noches" value={invoice.noches} onChange={handleChange} min="1" required />
        </div>

        <div className="form-group">
          <label htmlFor="precio">Precio por noche (€)</label>
          <input id="precio" type="number" name="precio" value={invoice.precio} onChange={handleChange} min="0" required />
        </div>

        <div className="invoice-summary">
          <p>Subtotal: <span data-testid="subtotal-val">{subtotal.toFixed(2)} €</span></p>
          <p>Total: <span data-testid="total-val">{total.toFixed(2)} €</span></p>
        </div>

        <div className="button-group">
          <button type="submit" className="btn-save">{id ? "Actualizar" : "Crear Factura"}</button>
          <button type="button" className="btn-cancel" onClick={() => navigate("/invoice")}>Cancelar</button>
        </div>
      </form>
    </div>
  );
};

export default InvoiceForm;