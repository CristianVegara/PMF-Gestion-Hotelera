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
    precio: 0,
  });

  const [clients, setClients] = useState([]);
  const [errors, setErrors] = useState([]);
  const [loading, setLoading] = useState(false);

  const token = localStorage.getItem("user_token");

  useEffect(() => {
    fetch("/api/clients", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((data) => setClients(Array.isArray(data) ? data : []))
      .catch((err) => console.error("Error loading clients:", err));
  }, [token]);

  useEffect(() => {
    if (!id) return;

    fetch(`/api/invoice/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Not found");
        return res.json();
      })
      .then((data) => {
        setInvoice({
          clienteId: data.cliente?.id ?? "",
          concepto: data.concepto ?? "",
          noches: data.noches ?? 1,
          precio: data.precio ?? 0,
        });
      })
      .catch(() => navigate("/invoice"));
  }, [id, token, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setInvoice((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors([]);
    setLoading(true);

    const payload = {
      cliente: { id: invoice.clienteId },
      concepto: invoice.concepto,
      noches: Number(invoice.noches),
      precio: Number(invoice.precio),
    };

    const method = id ? "PUT" : "POST";
    const url = id ? `/api/invoice/${id}` : "/api/invoice";

    try {
      const res = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });

      const data = await res.json();

      if (res.ok) {
        alert(data.mensaje ?? "Ok");
        navigate("/invoice");
      } else if (res.status === 400 && data.errors) {
        setErrors(data.errors);
      } else {
        alert(data.mensaje ?? "Error");
      }
    } catch {
      alert("Error de conexión");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <h2>{id ? "Editar Factura" : "Nueva Factura"}</h2>

      {errors.length > 0 && (
        <div className="alert-errors" role="alert" aria-label="Lista de errores">
          <ul>
            {errors.map((err, i) => (
              <li key={i}>{err}</li>
            ))}
          </ul>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="clienteId">Cliente</label>
          <select
            id="clienteId"
            name="clienteId"
            value={invoice.clienteId}
            onChange={handleChange}
            required
          >
            <option value="">Selecciona un cliente</option>
            {clients.map((c) => (
              <option key={c.id} value={c.id}>
                {c.nombre} ({c.dni})
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="concepto">Concepto</label>
          <input
            id="concepto"
            type="text"
            name="concepto"
            value={invoice.concepto}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="noches">Nº de noches</label>
          <input
            id="noches"
            type="number"
            name="noches"
            value={invoice.noches}
            onChange={handleChange}
            min="1"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="precio">Precio por noche (€)</label>
          <input
            id="precio"
            type="number"
            name="precio"
            value={invoice.precio}
            onChange={handleChange}
            min="0"
            required
          />
        </div>

        <p className="info-note">
          El descuento por fidelidad, IVA y total se calcularán automáticamente
          al guardar según el historial de reservas del cliente.
        </p>

        <div className="button-group">
          <button type="submit" className="btn-save" disabled={loading}>
            {loading ? "Guardando..." : id ? "Actualizar" : "Crear Factura"}
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