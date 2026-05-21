import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./Invoice.css";

const token = localStorage.getItem('user_token');

const formatCurrency = (value) => {
  const amount = Number(value || 0);
  return `${amount.toFixed(2)} €`;
};

const escapeHtml = (value) =>
  String(value ?? "")
.replace(/&/g, "&amp;")
.replace(/</g, "&lt;")
.replace(/>/g, "&gt;")
.replace(/"/g, "&quot;")
.replace(/'/g, "&#039;");

const Invoice = () => {
  const [invoices, setInvoices] = useState([]);
  const [sortBy, setSortBy] = useState("id");
  const [direction, setDirection] = useState("asc");
  
  const [reportFrom, setReportFrom] = useState("");
  const [reportTo, setReportTo] = useState("");
  const [reportRoomId, setReportRoomId] = useState("");
  const [reportClientId, setReportClientId] = useState("");
  const [reportType, setReportType] = useState("");
  const [reportData, setReportData] = useState(null);
  const [reportError, setReportError] = useState(null);
  
  const [expenseClientId, setExpenseClientId] = useState("");
  const [expenseFrom, setExpenseFrom] = useState("");
  const [expenseTo, setExpenseTo] = useState("");
  const [expenseResult, setExpenseResult] = useState(null);
  const [expenseError, setExpenseError] = useState(null);
  
  useEffect(() => {
    fetchInvoices();
  }, [sortBy, direction]);
  
  const fetchInvoices = () => {
    fetch(`/api/invoice?sortBy=${sortBy}&direction=${direction}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })      .then((res) => res.json())
    .then((res) => {
      if (Array.isArray(res)) {
        setInvoices(res);
      } else if (Array.isArray(res?.data)) {
        setInvoices(res.data);
      } else {
        console.error("Unexpected API response:", res);
        setInvoices([]);
      }
    })
    .catch((err) => {
      console.error("Error:", err);
      setInvoices([]);
    });
  };
  
  const deleteInvoice = (id) => {
    if (window.confirm("¿Eliminar factura?")) {
      fetch(`/api/invoice/${id}`, { 
        method: "DELETE",
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }).then((res) => {
        if (res.ok) fetchInvoices();
      });
    }
  };
  
  const fetchReport = () => {
    if (!reportFrom || !reportTo) {
      setReportError("Debes indicar el rango de fechas desde y hasta.");
      setReportData(null);
      return;
    }
    
    setReportError(null);
    setReportData(null);
    
    let url = `/api/invoice/report?from=${reportFrom}&to=${reportTo}`;
    if (reportRoomId) url += `&roomId=${encodeURIComponent(reportRoomId)}`;
    if (reportClientId) url += `&clientId=${encodeURIComponent(reportClientId)}`;
    if (reportType) url += `&type=${encodeURIComponent(reportType)}`;
    
    fetch(url, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })    .then((res) => res.json())
    .then((res) => {
      const data = res?.data ?? res;
      setReportData(data);
    })
    .catch((err) => {
      setReportError("No se pudo generar el informe. Comprueba el rango y los filtros.");
      console.error("Error informe:", err);
    });
  };
  
  const fetchClientExpenses = () => {
    if (!expenseClientId || !expenseFrom || !expenseTo) {
      setExpenseError("Debes indicar cliente y rango de fechas.");
      setExpenseResult(null);
      return;
    }
    
    setExpenseError(null);
    setExpenseResult(null);
    
    fetch(`/api/invoice/client/${encodeURIComponent(expenseClientId)}/expenses?from=${expenseFrom}&to=${expenseTo}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })    
    .then((res) => res.json())
    .then((res) => {
      if (res?.mensaje) {
        setExpenseError(res.mensaje);
        setExpenseResult(null);
      } else {
        setExpenseResult(res);
      }
    })
    .catch((err) => {
      setExpenseError("No se pudieron obtener los gastos del cliente.");
      console.error("Error gastos cliente:", err);
    });
  };
  
  const printInvoice = (invoice) => {
    const printWindow = window.open("", "_blank", "width=900,height=700");
    
    if (!printWindow) {
      window.print();
      return;
    }
    
    const invoiceDate = new Date().toLocaleDateString("es-ES");
    
    const subtotalBeforeDiscount =
    invoice.subtotalBeforeDiscount ??
    Number(invoice.noches || 0) * Number(invoice.precio || 0);
    
    const discountAmount = invoice.discountAmount ?? 0;
    const discountPercentage = invoice.discountPercentage ?? 0;
    
    const subtotal = invoice.subtotal ?? subtotalBeforeDiscount - discountAmount;
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
            .invoice-print { width: 100%; }
            .invoice-header {
              display: flex;
              justify-content: space-between;
              border-bottom: 3px solid #1a252f;
              margin-bottom: 28px;
              padding-bottom: 18px;
            }
            .brand {
              font-size: 26px;
              font-weight: 800;
              margin: 0;
            }
            .muted { color: #687483; }
            .invoice-number {
              font-size: 20px;
              font-weight: 800;
              text-align: right;
            }
            .grid {
              display: grid;
              grid-template-columns: 1fr 1fr;
              gap: 18px;
              margin-bottom: 28px;
            }
            .box {
              border: 1px solid #d8dee6;
              padding: 16px;
              border-radius: 8px;
            }
            table {
              width: 100%;
              border-collapse: collapse;
            }
            th {
              background: #1a252f;
              color: #fff;
              padding: 12px;
              text-align: left;
            }
            td {
              border-bottom: 1px solid #d8dee6;
              padding: 12px;
            }
            .number { text-align: right; }
            .totals {
              width: 320px;
              margin-left: auto;
              margin-top: 24px;
            }
            .totals-row {
              display: flex;
              justify-content: space-between;
              padding: 6px 0;
            }
            .grand-total {
              border-top: 2px solid #1a252f;
              font-size: 20px;
              font-weight: 800;
              margin-top: 8px;
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
                <div class="invoice-number">Factura #${escapeHtml(invoice.id)}</div>
                <div class="muted">Fecha: ${invoiceDate}</div>
                <div class="muted">Estado: ${invoice.pagada ? "Pagada" : "Pendiente"}</div>
              </div>
            </section>
      
            <section class="grid">
              <div class="box">
                <h3>Cliente</h3>
                <p><strong>${escapeHtml(invoice.cliente?.nombre || "Sin nombre")}</strong></p>
                <p>DNI: ${escapeHtml(invoice.cliente?.dni || "-")}</p>
                <p>Teléfono: ${escapeHtml(invoice.cliente?.telefono || "-")}</p>
                <p>Correo: ${escapeHtml(invoice.cliente?.correo || "-")}</p>
              </div>
      
              <div class="box">
                <h3>Detalle</h3>
                <p>Concepto: ${escapeHtml(invoice.concepto || "-")}</p>
                <p>Noches: ${invoice.noches || 0}</p>
                <p>Precio: ${formatCurrency(invoice.precio)}</p>
                <p>Rango: ${escapeHtml(invoice.loyaltyRank || "Sin rango")}</p>
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
                  <td class="number">${invoice.noches || 0}</td>
                  <td class="number">${formatCurrency(invoice.precio)}</td>
                  <td class="number">${formatCurrency(
      (invoice.noches || 0) * (invoice.precio || 0)
    )}</td>
                </tr>
              </tbody>
            </table>
    
            <section class="totals">
              <div class="totals-row">
                <span>Subtotal</span>
                <strong>${formatCurrency(subtotalBeforeDiscount)}</strong>
              </div>
    
              <div class="totals-row">
                <span>Descuento (${discountPercentage}%)</span>
                <strong>- ${formatCurrency(discountAmount)}</strong>
              </div>
    
              <div class="totals-row">
                <span>Subtotal final</span>
                <strong>${formatCurrency(subtotal)}</strong>
              </div>
    
              <div class="totals-row">
                <span>IVA 10%</span>
                <strong>${formatCurrency(iva)}</strong>
              </div>
    
              <div class="totals-row grand-total">
                <span>Total</span>
                <span>${formatCurrency(total)}</span>
              </div>
            </section>
    
          </main>
    
          <script>
            window.onload = () => {
              window.print();
              window.close();
            };
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
    
    <div className="sort-controls">	
    <label>
    Ordenar por:
    <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
    <option value="id">ID</option>
    <option value="cliente.nombre">Cliente</option>
    <option value="total">Total</option>
    </select>
    </label>
    
    <label>
    Dirección:
    <select value={direction} onChange={(e) => setDirection(e.target.value)}>
    <option value="asc">Ascendente</option>
    <option value="desc">Descendente</option>
    </select>
    </label>
    </div>
    
    <div className="reports-wrapper">
    <div className="section-card">
    <h3 className="section-title">Informe de facturación</h3>
    
    <div className="report-grid">
    <label>
    Desde
    <input
    type="date"
    value={reportFrom}
    onChange={(e) => setReportFrom(e.target.value)}
    />
    </label>
    
    <label>
    Hasta
    <input
    type="date"
    value={reportTo}
    onChange={(e) => setReportTo(e.target.value)}
    />
    </label>
    
    <label>
    ID de habitación
    <input
    type="text"
    placeholder="Opcional"
    value={reportRoomId}
    onChange={(e) => setReportRoomId(e.target.value)}
    />
    </label>
    
    <label>
    ID de cliente
    <input
    type="text"
    placeholder="Opcional"
    value={reportClientId}
    onChange={(e) => setReportClientId(e.target.value)}
    />
    </label>
    
    <label>
    Tipo de factura
    <input
    type="text"
    placeholder="Opcional"
    value={reportType}
    onChange={(e) => setReportType(e.target.value)}
    />
    </label>
    </div>
    
    <button className="btn-new" type="button" onClick={fetchReport}>
    Generar informe
    </button>
    
    {reportError && <p className="error-text">{reportError}</p>}
    
    {reportData && (
      <div className="report-result">
      <div className="summary-row">
      <div>
      <strong>Total bruto:</strong> {formatCurrency(reportData.totalGross ?? reportData.total)}
      </div>
      <div>
      <strong>Total neto:</strong> {formatCurrency(reportData.totalNet ?? reportData.total)}
      </div>
      <div>
      <strong>Total IVA:</strong> {formatCurrency(reportData.totalTax ?? 0)}
      </div>
      </div>
      
      {Array.isArray(reportData.breakdown) && reportData.breakdown.length > 0 ? (
        <div className="table-responsive">
        <table className="invoices-table">
        <thead>
        <tr>
        <th>Concepto</th>
        <th className="text-center">Importe</th>
        <th className="text-center">Neto</th>
        <th className="text-center">IVA</th>
        </tr>
        </thead>
        <tbody>
        {reportData.breakdown.map((item, index) => (
          <tr key={index}>
          <td>{item.type || item.description || "Detalle"}</td>
          <td className="text-center">{formatCurrency(item.amount ?? item.total ?? 0)}</td>
          <td className="text-center">{formatCurrency(item.netAmount ?? item.amount ?? 0)}</td>
          <td className="text-center">{formatCurrency(item.tax ?? 0)}</td>
          </tr>
        ))}
        </tbody>
        </table>
        </div>
      ) : (
        <p className="empty-message">No hay detalles de desglose para este informe.</p>
      )}
      </div>
    )}
    </div>
    
    <div className="section-card">
    <h3 className="section-title">Gastos de cliente</h3>
    
    <div className="report-grid">
    <label>
    ID de cliente
    <input
    type="text"
    value={expenseClientId}
    onChange={(e) => setExpenseClientId(e.target.value)}
    />
    </label>
    
    <label>
    Desde
    <input
    type="date"
    value={expenseFrom}
    onChange={(e) => setExpenseFrom(e.target.value)}
    />
    </label>
    
    <label>
    Hasta
    <input
    type="date"
    value={expenseTo}
    onChange={(e) => setExpenseTo(e.target.value)}
    />
    </label>
    </div>
    
    <button className="btn-new" type="button" onClick={fetchClientExpenses}>
    Buscar gastos
    </button>
    
    {expenseError && <p className="error-text">{expenseError}</p>}
    
    {expenseResult && (
      <div className="report-result">
      <p>
      <strong>Cliente:</strong> {expenseResult.client?.nombre || "-"} - {expenseResult.client?.dni || "-"}
      </p>
      <p>
      <strong>Periodo:</strong> {expenseResult.from} / {expenseResult.to}
      </p>
      <p>
      <strong>Factura(s):</strong> {expenseResult.invoiceCount ?? (expenseResult.invoices?.length ?? 0)}
      </p>
      <p>
      <strong>Total gastado:</strong> {formatCurrency(expenseResult.totalSpent ?? 0)}
      </p>
      
      {Array.isArray(expenseResult.invoices) && expenseResult.invoices.length > 0 && (
        <div className="table-responsive">
        <table className="invoices-table">
        <thead>
        <tr>
        <th>ID</th>
        <th>Fecha</th>
        <th>Concepto</th>
        <th className="text-center">Total</th>
        </tr>
        </thead>
        <tbody>
        {expenseResult.invoices.map((invoice) => (
          <tr key={invoice.id}>
          <td>{invoice.id}</td>
          <td>{invoice.fecha || invoice.fechaCreacion || invoice.fechaFactura || "-"}</td>
          <td>{invoice.concepto || "-"}</td>
          <td className="text-center">{formatCurrency(invoice.total)}</td>
          </tr>
        ))}
        </tbody>
        </table>
        </div>
      )}
      </div>
    )}
    </div>
    </div>
    
    <div className="table-responsive">
    <table className="invoices-table">
    <thead>
    <tr>
    <th>ID</th>
    <th>Cliente</th>
    <th>DNI</th>
    <th>Concepto</th>
    <th>Noches</th>
    <th>Rango</th>
    <th>Descuento</th>
    <th>Total</th>
    <th>Acciones</th>
    </tr>
    </thead>
    
    <tbody>
    {Array.isArray(invoices) &&
      invoices.map((inv) => (
        <tr key={inv.id}>
        <td><strong>{inv.id}</strong></td>
        <td>{inv.cliente?.nombre}</td>
        <td>{inv.cliente?.dni}</td>
        <td>{inv.concepto}</td>
        <td>{inv.noches}</td>
        <td>{inv.loyaltyRank || "Sin rango"}</td>
        <td>
        {formatCurrency(inv.discountAmount)} (
          {Number(inv.discountPercentage || 0).toFixed(0)}%)
          </td>
          <td>{formatCurrency(inv.total)}</td>
          
          <td>
          <div className="action-group">
          <Link to={`/invoice/edit/${inv.id}`}>Editar</Link>
          <button onClick={() => printInvoice(inv)}>PDF</button>
          <button onClick={() => deleteInvoice(inv.id)}>Eliminar</button>
          </div>
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