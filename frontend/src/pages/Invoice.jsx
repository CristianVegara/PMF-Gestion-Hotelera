import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./Invoice.css";

const formatCurrency = (value) => `${Number(value ?? 0).toFixed(2)} €`;

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
  const [reportClientDni, setReportClientDni] = useState("");
  const [reportType, setReportType] = useState("");
  const [reportData, setReportData] = useState(null);
  const [reportError, setReportError] = useState(null);

  const [expenseClientDni, setExpenseClientDni] = useState("");
  const [expenseFrom, setExpenseFrom] = useState("");
  const [expenseTo, setExpenseTo] = useState("");
  const [expenseResult, setExpenseResult] = useState(null);
  const [expenseError, setExpenseError] = useState(null);

  const token = localStorage.getItem("user_token");

  let userRole = localStorage.getItem("role") ?? "";
  if (userRole.startsWith("ROLE_")) userRole = userRole.replace("ROLE_", "");

  useEffect(() => {
    fetchInvoices();
  }, [sortBy, direction]);

  const fetchInvoices = () => {
    fetch(`/api/invoice?sortBy=${sortBy}&direction=${direction}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((res) => {
        setInvoices(Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : []);
      })
      .catch((err) => {
        console.error("Error loading invoices:", err);
        setInvoices([]);
      });
  };

  const deleteInvoice = (id) => {
    if (!window.confirm("¿Eliminar factura?")) return;
    fetch(`/api/invoice/${id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    }).then((res) => {
      if (res.ok) fetchInvoices();
    });
  };

  const fetchReport = async () => {
    if (!reportFrom || !reportTo) {
      setReportError("Debes indicar el rango de fechas desde y hasta.");
      setReportData(null);
      return;
    }

    setReportError(null);
    setReportData(null);

    try {
      let clientId = null;

      if (reportClientDni.trim()) {
        const clientRes = await fetch(
          `/api/clients/by-dni/${encodeURIComponent(reportClientDni.trim())}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        if (!clientRes.ok) {
          setReportError("No se encontró ningún cliente con ese DNI.");
          return;
        }
        const clientData = await clientRes.json();
        clientId = clientData.id;
      }

      let url = `/api/invoice/report?from=${reportFrom}&to=${reportTo}`;
      if (reportRoomId.trim()) url += `&roomId=${encodeURIComponent(reportRoomId.trim())}`;
      if (clientId)           url += `&clientId=${clientId}`;
      if (reportType.trim())  url += `&type=${encodeURIComponent(reportType.trim())}`;

      const res = await fetch(url, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const json = await res.json();
      setReportData(json?.data ?? json);
    } catch (err) {
      setReportError("No se pudo generar el informe. Comprueba el rango y los filtros.");
      console.error("Error report:", err);
    }
  };

  const fetchClientExpenses = () => {
    if (!expenseClientDni || !expenseFrom || !expenseTo) {
      setExpenseError("Debes indicar el DNI del cliente y el rango de fechas.");
      setExpenseResult(null);
      return;
    }

    setExpenseError(null);
    setExpenseResult(null);

    fetch(
      `/api/invoice/client/by-dni/${encodeURIComponent(expenseClientDni.trim())}/expenses?from=${expenseFrom}&to=${expenseTo}`,
      { headers: { Authorization: `Bearer ${token}` } }
    )
      .then((res) => res.json())
      .then((res) => {
        if (res?.mensaje) {
          setExpenseError(res.mensaje);
        } else {
          setExpenseResult(res);
        }
      })
      .catch((err) => {
        setExpenseError("No se pudieron obtener los gastos del cliente.");
        console.error("Error expenses:", err);
      });
  };

  const printClientExpenses = (data) => {
    const win = window.open("", "_blank", "width=900,height=700");
    if (!win) { window.print(); return; }

    const rows =
      Array.isArray(data.invoices) && data.invoices.length > 0
        ? data.invoices
            .map(
              (inv) => `
              <tr>
                <td>${escapeHtml(inv.id)}</td>
                <td>${escapeHtml(inv.fechaEmision ?? "-")}</td>
                <td>${escapeHtml(inv.booking?.fechaEntrada ?? "-")}</td>
                <td>${escapeHtml(inv.booking?.fechaSalida ?? "-")}</td>
                <td>${escapeHtml(inv.concepto ?? "-")}</td>
                <td>${inv.pagada ? "Pagada" : "Pendiente"}</td>
                <td class="number">${formatCurrency(inv.total)}</td>
              </tr>`
            )
            .join("")
        : `<tr><td colspan="7" style="text-align:center">No hay gastos en este periodo.</td></tr>`;

    win.document.write(`
      <!doctype html><html lang="es"><head>
      <meta charset="utf-8"/>
      <title>Gastos cliente ${escapeHtml(data.client?.dni ?? "")}</title>
      <style>
        @page{size:A4;margin:18mm}
        *{box-sizing:border-box}
        body{margin:0;color:#1f2933;font-family:Arial,sans-serif;line-height:1.45}
        .header{display:flex;justify-content:space-between;border-bottom:3px solid #1a252f;margin-bottom:26px;padding-bottom:16px}
        .brand{font-size:26px;font-weight:800;margin:0}
        .muted{color:#687483}
        .grid{display:grid;grid-template-columns:1fr 1fr;gap:18px;margin-bottom:24px}
        .box{border:1px solid #d8dee6;border-radius:8px;padding:16px}
        table{width:100%;border-collapse:collapse;margin-top:10px}
        th{background:#1a252f;color:#fff;padding:10px;text-align:left}
        td{border-bottom:1px solid #d8dee6;padding:10px}
        .number{text-align:right}
        .totals{width:320px;margin-left:auto;margin-top:22px;border-top:2px solid #1a252f;padding-top:12px}
        .totals-row{display:flex;justify-content:space-between;padding:4px 0}
        .grand-total{font-size:20px;font-weight:800;margin-top:6px;padding-top:8px;border-top:2px solid #1a252f}
      </style>
      </head><body><main>
      <section class="header">
        <div><h1 class="brand">Hotel Mediterráneo</h1><p class="muted">Resumen de gastos por cliente</p></div>
        <div><strong>Gastos del cliente</strong><div class="muted">Fecha: ${new Date().toLocaleDateString("es-ES")}</div></div>
      </section>
      <section class="grid">
        <div class="box">
          <h3>Cliente</h3>
          <p><strong>${escapeHtml(data.client?.nombre ?? "-")}</strong></p>
          <p>DNI: ${escapeHtml(data.client?.dni ?? "-")}</p>
          <p>Teléfono: ${escapeHtml(data.client?.telefono ?? "-")}</p>
          <p>Correo: ${escapeHtml(data.client?.correo ?? "-")}</p>
        </div>
        <div class="box">
          <h3>Periodo</h3>
          <p>Desde: ${escapeHtml(data.from ?? "-")}</p>
          <p>Hasta: ${escapeHtml(data.to ?? "-")}</p>
          <p>Facturas: ${escapeHtml(data.invoiceCount ?? data.invoices?.length ?? 0)}</p>
        </div>
      </section>
      <table>
        <thead><tr>
          <th>Factura</th><th>Emisión</th><th>Entrada</th><th>Salida</th>
          <th>Concepto</th><th>Estado</th><th class="number">Total</th>
        </tr></thead>
        <tbody>${rows}</tbody>
      </table>
      <section class="totals">
        <div class="totals-row"><span>Total pagado</span><strong>${formatCurrency(data.paidAmount ?? 0)}</strong></div>
        <div class="totals-row"><span>Pendiente</span><strong>${formatCurrency(data.pendingAmount ?? 0)}</strong></div>
        <div class="totals-row grand-total"><span>Total</span><span>${formatCurrency(data.totalSpent ?? 0)}</span></div>
      </section>
      </main>
      <script>window.onload=()=>{window.print();window.close()}</script>
      </body></html>
    `);
    win.document.close();
  };
  const editInvoice = (invoice) => {
    if (!invoice?.id) return;
    window.location.href = `/invoice/edit/${invoice.id}`;
  };


  const printInvoice = (invoice) => {
    const win = window.open("", "_blank", "width=900,height=700");
    if (!win) { window.print(); return; }

    const subtotalBeforeDiscount =
      invoice.subtotalBeforeDiscount ??
      Number(invoice.noches ?? 0) * Number(invoice.precio ?? 0);
    const discountAmount     = invoice.discountAmount ?? 0;
    const discountPercentage = invoice.discountPercentage ?? 0;
    const subtotal           = invoice.subtotal ?? subtotalBeforeDiscount - discountAmount;
    const iva                = invoice.iva ?? Number(subtotal) * 0.10;
    const total              = invoice.total ?? Number(subtotal) + Number(iva);

    win.document.write(`
      <!doctype html><html lang="es"><head>
      <meta charset="utf-8"/>
      <title>Factura ${escapeHtml(invoice.id)}</title>
      <style>
        @page{size:A4;margin:18mm}
        *{box-sizing:border-box}
        body{margin:0;color:#1f2933;font-family:Arial,sans-serif;line-height:1.45}
        .header{display:flex;justify-content:space-between;border-bottom:3px solid #1a252f;margin-bottom:28px;padding-bottom:18px}
        .brand{font-size:26px;font-weight:800;margin:0}
        .muted{color:#687483}
        .grid{display:grid;grid-template-columns:1fr 1fr;gap:18px;margin-bottom:28px}
        .box{border:1px solid #d8dee6;padding:16px;border-radius:8px}
        table{width:100%;border-collapse:collapse}
        th{background:#1a252f;color:#fff;padding:12px;text-align:left}
        td{border-bottom:1px solid #d8dee6;padding:12px}
        .number{text-align:right}
        .totals{width:320px;margin-left:auto;margin-top:24px}
        .totals-row{display:flex;justify-content:space-between;padding:6px 0}
        .grand-total{border-top:2px solid #1a252f;font-size:20px;font-weight:800;margin-top:8px;padding-top:12px}
      </style>
      </head><body><main>
      <section class="header">
        <div><h1 class="brand">Hotel Mediterráneo</h1><p class="muted">Gestión Hotelera</p></div>
        <div>
          <div style="font-size:20px;font-weight:800">Factura #${escapeHtml(invoice.id)}</div>
          <div class="muted">Fecha: ${new Date().toLocaleDateString("es-ES")}</div>
          <div class="muted">Estado: ${invoice.pagada ? "Pagada" : "Pendiente"}</div>
        </div>
      </section>
      <section class="grid">
        <div class="box">
          <h3>Cliente</h3>
          <p><strong>${escapeHtml(invoice.cliente?.nombre ?? "Sin nombre")}</strong></p>
          <p>DNI: ${escapeHtml(invoice.cliente?.dni ?? "-")}</p>
          <p>Teléfono: ${escapeHtml(invoice.cliente?.telefono ?? "-")}</p>
          <p>Correo: ${escapeHtml(invoice.cliente?.correo ?? "-")}</p>
        </div>
        <div class="box">
          <h3>Detalle</h3>
          <p>Concepto: ${escapeHtml(invoice.concepto ?? "-")}</p>
          <p>Noches: ${invoice.noches ?? 0}</p>
          <p>Precio/noche: ${formatCurrency(invoice.precio)}</p>
          <p>Rango fidelidad: ${escapeHtml(invoice.loyaltyRank ?? "Sin rango")}</p>
        </div>
      </section>
      <table>
        <thead><tr>
          <th>Concepto</th>
          <th class="number">Noches</th>
          <th class="number">Precio</th>
          <th class="number">Importe</th>
        </tr></thead>
        <tbody><tr>
          <td>${escapeHtml(invoice.concepto ?? "-")}</td>
          <td class="number">${invoice.noches ?? 0}</td>
          <td class="number">${formatCurrency(invoice.precio)}</td>
          <td class="number">${formatCurrency((invoice.noches ?? 0) * (invoice.precio ?? 0))}</td>
        </tr></tbody>
      </table>
      <section class="totals">
        <div class="totals-row"><span>Subtotal</span><strong>${formatCurrency(subtotalBeforeDiscount)}</strong></div>
        <div class="totals-row"><span>Descuento (${discountPercentage}%)</span><strong>- ${formatCurrency(discountAmount)}</strong></div>
        <div class="totals-row"><span>Subtotal final</span><strong>${formatCurrency(subtotal)}</strong></div>
        <div class="totals-row"><span>IVA 10%</span><strong>${formatCurrency(iva)}</strong></div>
        <div class="totals-row grand-total"><span>Total</span><span>${formatCurrency(total)}</span></div>
      </section>
      </main>
      <script>window.onload=()=>{window.print();window.close()}</script>
      </body></html>
    `);
    win.document.close();
  };

  const printReport = (data) => {
    const win = window.open("", "_blank", "width=900,height=700");
    if (!win) { window.print(); return; }

    const rows =
      Array.isArray(data.breakdown) && data.breakdown.length > 0
        ? data.breakdown
            .map(
              (item) => `
              <tr>
                <td>${escapeHtml(item.category ?? item.type ?? "Detalle")}</td>
                <td class="number">${formatCurrency(item.totalNet ?? 0)}</td>
                <td class="number">${formatCurrency(item.totalTax ?? 0)}</td>
                <td class="number">${formatCurrency(item.totalGross ?? 0)}</td>
              </tr>`
            )
            .join("")
        : `<tr><td colspan="4" style="text-align:center">No hay detalles para este informe.</td></tr>`;

    win.document.write(`
      <!doctype html><html lang="es"><head>
      <meta charset="utf-8"/>
      <title>Informe de Ganancias</title>
      <style>
        @page{size:A4;margin:18mm}
        *{box-sizing:border-box}
        body{margin:0;color:#1f2933;font-family:Arial,sans-serif;line-height:1.45}
        .header{display:flex;justify-content:space-between;border-bottom:3px solid #1a252f;margin-bottom:28px;padding-bottom:18px}
        .brand{font-size:26px;font-weight:800;margin:0}
        .muted{color:#687483}
        .grid{display:grid;grid-template-columns:1fr 1fr;gap:18px;margin-bottom:28px}
        .box{border:1px solid #d8dee6;padding:16px;border-radius:8px}
        table{width:100%;border-collapse:collapse;margin-top:10px}
        th{background:#1a252f;color:#fff;padding:12px;text-align:left}
        td{border-bottom:1px solid #d8dee6;padding:12px}
        .number{text-align:right}
        .totals{width:320px;margin-left:auto;margin-top:24px}
        .totals-row{display:flex;justify-content:space-between;padding:6px 0}
        .grand-total{border-top:2px solid #1a252f;font-size:20px;font-weight:800;margin-top:8px;padding-top:12px}
      </style>
      </head><body><main>
      <section class="header">
        <div><h1 class="brand">Hotel Mediterráneo</h1><p class="muted">Ganancias cobradas</p></div>
        <div>
          <div style="font-size:20px;font-weight:800">Informe de Ganancias</div>
          <div class="muted">Emisión: ${new Date().toLocaleDateString("es-ES")}</div>
        </div>
      </section>
      <section class="grid">
        <div class="box">
          <h3>Rango del informe</h3>
          <p>Desde: ${escapeHtml(reportFrom)}</p>
          <p>Hasta: ${escapeHtml(reportTo)}</p>
        </div>
        <div class="box">
          <h3>Filtros aplicados</h3>
          <p>Habitación: ${escapeHtml(reportRoomId || "Todas")}</p>
          <p>Cliente DNI: ${escapeHtml(reportClientDni || "Todos")}</p>
          <p>Tipo: ${escapeHtml(reportType || "Todos")}</p>
        </div>
      </section>
      <h3>Desglose</h3>
      <table>
        <thead><tr>
          <th>Concepto / Tipo</th>
          <th class="number">Neto</th>
          <th class="number">IVA</th>
          <th class="number">Total bruto</th>
        </tr></thead>
        <tbody>${rows}</tbody>
      </table>
      <section class="totals">
        <div class="totals-row"><span>Total neto</span><strong>${formatCurrency(data.totalNet ?? 0)}</strong></div>
        <div class="totals-row"><span>Total IVA</span><strong>${formatCurrency(data.totalTax ?? 0)}</strong></div>
        <div class="totals-row grand-total"><span>Total bruto</span><span>${formatCurrency(data.totalGross ?? 0)}</span></div>
      </section>
      </main>
      <script>window.onload=()=>{window.print();window.close()}</script>
      </body></html>
    `);
    win.document.close();
  };

  return (
    <div className="invoices-page-wrapper">
      <div className="invoices-container">

        <div className="header-actions">
          <h2 className="title-list">Listado de Facturas</h2>
          {userRole !== "USER" && (
            <Link to="/invoice/form" className="btn-new">
              Nueva Factura
            </Link>
          )}
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
            <h3 className="section-title">Informe de ganancias cobradas</h3>

            <div className="report-grid">
              <label>
                Desde
                <input type="date" value={reportFrom} onChange={(e) => setReportFrom(e.target.value)} />
              </label>
              <label>
                Hasta
                <input type="date" value={reportTo} onChange={(e) => setReportTo(e.target.value)} />
              </label>
              <label>
                ID de habitación
                <input type="text" placeholder="Opcional" value={reportRoomId} onChange={(e) => setReportRoomId(e.target.value)} />
              </label>
              <label>
                DNI de cliente
                <input type="text" placeholder="Opcional" value={reportClientDni} onChange={(e) => setReportClientDni(e.target.value)} />
              </label>
              <label>
                Tipo de cargo
                <select value={reportType} onChange={(e) => setReportType(e.target.value)}>
                  <option value="">Todos</option>
                  <option value="HABITACION">Habitación</option>
                  <option value="MINIBAR">Minibar</option>
                  <option value="ACTIVIDAD">Actividad</option>
                  <option value="LIMPIEZA">Limpieza</option>
                  <option value="EXTRA">Extra</option>
                </select>
              </label>
            </div>

            <button className="btn-new" type="button" onClick={fetchReport}>
              Generar informe
            </button>

            {reportError && <p className="error-text">{reportError}</p>}

            {reportData && (
              <div className="report-result">
                <div className="summary-row">
                  <div><strong>Total bruto:</strong> {formatCurrency(reportData.totalGross)}</div>
                  <div><strong>Total neto:</strong> {formatCurrency(reportData.totalNet)}</div>
                  <div><strong>Total IVA:</strong> {formatCurrency(reportData.totalTax)}</div>
                  <button
                    className="btn-new"
                    style={{ marginLeft: "10px", backgroundColor: "#2c3e50" }}
                    onClick={() => printReport(reportData)}
                  >
                    Imprimir PDF
                  </button>
                </div>

                {Array.isArray(reportData.breakdown) && reportData.breakdown.length > 0 ? (
                  <div className="table-responsive">
                    <table className="invoices-table">
                      <thead>
                        <tr>
                          <th>Concepto</th>
                          <th className="text-center">Neto</th>
                          <th className="text-center">IVA</th>
                          <th className="text-center">Total bruto</th>
                        </tr>
                      </thead>
                      <tbody>
                        {reportData.breakdown.map((item, i) => (
                          <tr key={i}>
                            <td>{item.category ?? "Detalle"}</td>
                            <td className="text-center">{formatCurrency(item.totalNet ?? 0)}</td>
                            <td className="text-center">{formatCurrency(item.totalTax ?? 0)}</td>
                            <td className="text-center">{formatCurrency(item.totalGross ?? 0)}</td>
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
                DNI de cliente
                <input
                  type="text"
                  placeholder="Ej: 12345678X"
                  value={expenseClientDni}
                  onChange={(e) => setExpenseClientDni(e.target.value)}
                />
              </label>
              <label>
                Desde
                <input type="date" value={expenseFrom} onChange={(e) => setExpenseFrom(e.target.value)} />
              </label>
              <label>
                Hasta
                <input type="date" value={expenseTo} onChange={(e) => setExpenseTo(e.target.value)} />
              </label>
            </div>

            <button className="btn-new" type="button" onClick={fetchClientExpenses}>
              Buscar gastos
            </button>

            {expenseError && <p className="error-text">{expenseError}</p>}

            {expenseResult && (
              <div className="report-result">
                <p><strong>Cliente:</strong> {expenseResult.client?.nombre ?? "-"} — {expenseResult.client?.dni ?? "-"}</p>
                <p><strong>Periodo:</strong> {expenseResult.from} / {expenseResult.to}</p>
                <p><strong>Facturas:</strong> {expenseResult.invoiceCount ?? 0}</p>
                <p><strong>Total pagado:</strong> {formatCurrency(expenseResult.paidAmount ?? 0)}</p>
                <p><strong>Pendiente:</strong> {formatCurrency(expenseResult.pendingAmount ?? 0)}</p>
                <p><strong>Total gastado:</strong> {formatCurrency(expenseResult.totalSpent ?? 0)}</p>

                <button
                  className="btn-new"
                  style={{ backgroundColor: "#2c3e50" }}
                  type="button"
                  onClick={() => printClientExpenses(expenseResult)}
                >
                  Imprimir PDF
                </button>

                {Array.isArray(expenseResult.invoices) && expenseResult.invoices.length > 0 && (
                  <div className="table-responsive">
                    <table className="invoices-table">
                      <thead>
                        <tr>
                          <th>ID</th>
                          <th>Emisión</th>
                          <th>Entrada</th>
                          <th>Salida</th>
                          <th>Concepto</th>
                          <th className="text-center">Estado</th>
                          <th className="text-center">Total</th>
                        </tr>
                      </thead>
                      <tbody>
                        {expenseResult.invoices.map((inv) => (
                          <tr key={inv.id}>
                            <td>{inv.id}</td>
                            <td>{inv.fechaEmision ?? "-"}</td>
                            <td>{inv.booking?.fechaEntrada ?? "-"}</td>
                            <td>{inv.booking?.fechaSalida ?? "-"}</td>
                            <td>{inv.concepto ?? "-"}</td>
                            <td className="text-center">
                              {inv.pagada ? "Pagada" : "Pendiente"}
                            </td>
                            <td className="text-center">{formatCurrency(inv.total)}</td>
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
                <th className="text-center">Estado</th>
                <th className="text-center">Total</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {invoices.map((inv) => (
                <tr key={inv.id}>
                  <td><strong>{inv.id}</strong></td>
                  <td>{inv.cliente?.nombre ?? "-"}</td>
                  <td>{inv.cliente?.dni ?? "-"}</td>
                  <td>{inv.concepto ?? "-"}</td>
                  <td>{inv.noches ?? "-"}</td>
                  <td>{inv.loyaltyRank ?? "Sin rango"}</td>
                  <td>
                    {formatCurrency(inv.discountAmount)} (
                    {Number(inv.discountPercentage ?? 0).toFixed(0)}%)
                  </td>
                  <td className="text-center">
                    {inv.pagada ? "Pagada" : "Pendiente"}
                  </td>
                  <td className="text-center">{formatCurrency(inv.total)}</td>
                  <td> 
                    {(userRole === "ADMIN" || userRole === "SUPERVISOR") && (
                    <button onClick={() => editInvoice(inv)}>Editar</button>
                    )}
                  </td>
                  <td>
                    <button onClick={() => printInvoice(inv)}>PDF</button></td>
                  <td>
                    {(userRole === "ADMIN" || userRole === "SUPERVISOR") && (
                    <button onClick={() => deleteInvoice(inv.id)}>Eliminar</button>
                    )}
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