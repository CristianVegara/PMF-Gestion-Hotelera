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

  // Traer clientes
  useEffect(() => {
    fetch("/api/clients")
      .then(res => res.json())
      .then(data => setClients(data))
      .catch(err => console.error("Error cargando clientes:", err));
  }, []);

  // Cargar invoice si es edición
  useEffect(() => {
    if (id) {
      fetch(`/api/invoice/${id}`)
        .then(res => {
          if (!res.ok) throw new Error("Error en la respuesta del servidor");
          return res.json();
        })
        .then(data => {
          if (data) {
            setInvoice({
              clienteId: data.cliente.id || "",
              concepto: data.concepto || "",
              noches: data.noches || 1,
              precio: data.precio || 0
            });
          }
        })
        .catch(err => {
          console.error("Error cargando factura:", err);
          navigate("/invoice");
        });
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

      if (response.status === 201 || response.status === 200) {
        alert(data.mensaje);
        navigate("/invoice");
      } else if (response.status === 400 && data.errors) {
        setErrors(data.errors);
      } else {
        alert(data.mensaje || "Error inesperado");
      }
    } catch (err) {
      console.error(err);
      alert("Error de conexión");
    }
  };

  return (
    <div className="form-container">
      <h2>{id ? "Editar Factura" : "Nueva Factura"}</h2>

      {errors.length > 0 && (
        <div className="alert-errors">
          <ul>
            {errors.map((err, index) => (
              <li key={index}>{err}</li>
            ))}
          </ul>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Cliente</label>
          <select
            name="clienteId"
            value={invoice.clienteId}
            onChange={handleChange}
            required
          >
            <option value="">Selecciona un cliente</option>
            {clients.map(c => (
              <option key={c.id} value={c.id}>
                {c.nombre} ({c.dni})
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Concepto</label>
          <input
            type="text"
            name="concepto"
            value={invoice.concepto}
            onChange={handleChange}
            placeholder="Habitación doble"
            required
          />
        </div>

        <div className="form-group">
          <label>Nº de noches</label>
          <input
            type="number"
            name="noches"
            value={invoice.noches}
            onChange={handleChange}
            min="1"
            required
          />
        </div>

        <div className="form-group">
          <label>Precio por noche (€)</label>
          <input
            type="number"
            name="precio"
            value={invoice.precio}
            onChange={handleChange}
            min="0"
            required
          />
        </div>

        <div className="invoice-summary">
          <p><strong>Subtotal:</strong> {subtotal.toFixed(2)} €</p>
          <p><strong>IVA (10%):</strong> {iva.toFixed(2)} €</p>
          <p><strong>Total:</strong> {total.toFixed(2)} €</p>
        </div>

        <div className="button-group">
          <button type="submit" className="btn-save">
            {id ? "Actualizar" : "Crear Factura"}
          </button>
          <button
            type="button"
            className="btn-cancel"
            onClick={() => navigate("/invoice")}
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
};

export default InvoiceForm;