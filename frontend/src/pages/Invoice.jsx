import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./Invoice.css";

const formatCurrency = (value) => {
  const amount = Number(value || 0);
  return `${amount.toFixed(2)} €`;
};

const escapeHtml = (value) => String(value ?? "")
  .replace(/&/g, "&amp;")
  .replace(/</g, "&lt;")
  .replace(/>/g, "&gt;")
  .replace(/"/g, "&quot;")
  .replace(/'/g, "&#039;");

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

  const printInvoice = (invoice) => {
    const printWindow = window.open("", "_blank", "width=900,height=700");

    if (!printWindow) {
      window.print();
      return;
    }

    const invoiceDate = new Date().toLocaleDateString("es-ES");
    const subtotal = invoice.subtotal ?? Number(invoice.noches || 0) * Number(invoice.precio || 0);
    const iva = invoice.iva ?? Number(subtotal || 0) * 0.10;
    const total = invoice.total ?? Number(subtotal || 0) + Number(iva || 0);

    printWindow.document.write(`
      <!doctype html>
      <html lang="es">
        <head>
          <meta charset="utf-8" />
          <title>Factura ${escapeHtml(invoice.id)}</title>
          <style>
            @page { size: A4; margin: 18mm; }
            * { box-sizing: border-box; }
            body {
              margin: 0;
              color: #1f2933;
              font-family: Arial, Helvetica, sans-serif;
              line-height: 1.45;
            }
            .invoice-print {
              width: 100%;
            }
            .invoice-header {
              align-items: flex-start;
              border-bottom: 3px solid #1a252f;
              display: flex;
              justify-content: space-between;
              margin-bottom: 28px;
              padding-bottom: 18px;
            }
            .brand {
              color: #1a252f;
              font-size: 26px;
              font-weight: 800;
              margin: 0 0 6px;
            }
            .muted {
              color: #687483;
              margin: 2px 0;
            }
            .invoice-number {
              color: #1a252f;
              font-size: 20px;
              font-weight: 800;
              margin: 0 0 6px;
              text-align: right;
            }
            .grid {
              display: grid;
              gap: 18px;
              grid-template-columns: 1fr 1fr;
              margin-bottom: 28px;
            }
            .box {
              border: 1px solid #d8dee6;
              border-radius: 8px;
              padding: 16px;
            }
            .box h2 {
              color: #1a252f;
              font-size: 14px;
              letter-spacing: 0;
              margin: 0 0 10px;
              text-transform: uppercase;
            }
            table {
              border-collapse: collapse;
              width: 100%;
            }
            th {
              background: #1a252f;
              color: white;
              font-size: 13px;
              padding: 12px;
              text-align: left;
            }
            td {
              border-bottom: 1px solid #d8dee6;
              padding: 12px;
            }
            .number {
              text-align: right;
            }
            .totals {
              margin-left: auto;
              margin-top: 24px;
              width: 320px;
            }
            .totals-row {
              display: flex;
              justify-content: space-between;
              padding: 8px 0;
            }
            .grand-total {
              border-top: 2px solid #1a252f;
              color: #1a252f;
              font-size: 20px;
              font-weight: 800;
              margin-top: 8px;
              padding-top: 12px;
            }
            .footer {
              border-top: 1px solid #d8dee6;
              color: #687483;
              font-size: 12px;
              margin-top: 48px;
              padding-top: 12px;
            }
          </style>
        </head>
        <body>
          <main class="invoice-print">
            <section class="invoice-header">
              <div>
                <h1 class="brand">Hotel Mediterráneo</h1>
                <p class="muted">Gestión Hotelera</p>
              </div>
              <div>
                <p class="invoice-number">Factura #${escapeHtml(invoice.id)}</p>
                <p class="muted">Fecha: ${escapeHtml(invoiceDate)}</p>
                <p class="muted">Estado: ${invoice.pagada ? "Pagada" : "Pendiente"}</p>
              </div>
            </section>

            <section class="grid">
              <div class="box">
                <h2>Cliente</h2>
                <p><strong>${escapeHtml(invoice.cliente?.nombre || "Cliente sin nombre")}</strong></p>
                <p>DNI: ${escapeHtml(invoice.cliente?.dni || "-")}</p>
                <p>Teléfono: ${escapeHtml(invoice.cliente?.telefono || "-")}</p>
                <p>Correo: ${escapeHtml(invoice.cliente?.correo || "-")}</p>
              </div>
              <div class="box">
                <h2>Detalle</h2>
                <p>Concepto: ${escapeHtml(invoice.concepto || "-")}</p>
                <p>Noches: ${escapeHtml(invoice.noches || 0)}</p>
                <p>Precio por noche: ${escapeHtml(formatCurrency(invoice.precio))}</p>
              </div>
            </section>

            <table>
              <thead>
                <tr>
                  <th>Concepto</th>
                  <th class="number">Noches</th>
                  <th class="number">Precio</th>
                  <th class="number">Importe</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>${escapeHtml(invoice.concepto || "-")}</td>
                  <td class="number">${escapeHtml(invoice.noches || 0)}</td>
                  <td class="number">${escapeHtml(formatCurrency(invoice.precio))}</td>
                  <td class="number">${escapeHtml(formatCurrency(Number(invoice.noches || 0) * Number(invoice.precio || 0)))}</td>
                </tr>
              </tbody>
            </table>

            <section class="totals">
              <div class="totals-row">
                <span>Subtotal</span>
                <strong>${escapeHtml(formatCurrency(subtotal))}</strong>
              </div>
              <div class="totals-row">
                <span>IVA 10%</span>
                <strong>${escapeHtml(formatCurrency(iva))}</strong>
              </div>
              <div class="totals-row grand-total">
                <span>Total</span>
                <span>${escapeHtml(formatCurrency(total))}</span>
              </div>
            </section>

            <p class="footer">Documento generado desde PMF Gestión Hotelera.</p>
          </main>
          <script>
            window.addEventListener("load", () => {
              window.print();
              window.close();
            });
          </script>
        </body>
      </html>
    `);
    printWindow.document.close();
  };
  
  return (
    <div className="invoices-page-wrapper">
    
    <div className="invoices-container">
    
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
    <table className="invoices-table">
    
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
      <td>{formatCurrency(inv.total)}</td>
      
      <td className="text-center">
      <Link to={`/invoice/edit/${inv.id}`} className="btn-edit">
      Editar
      </Link>
      <button onClick={() => printInvoice(inv)} className="btn-print">
      PDF
      </button>
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
