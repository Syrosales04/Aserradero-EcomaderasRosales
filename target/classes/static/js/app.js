/* ============================================================
   Aserradero - app.js
   Lógica del frontend: autenticación, navegación y cada módulo.
   ============================================================ */

// ---------- Sesión ----------
const auth = sessionStorage.getItem('auth');
const usuario = JSON.parse(sessionStorage.getItem('usuario') || 'null');
if (!auth || !usuario) {
  location.href = 'login.html';
}

const esAdmin = usuario && usuario.rol === 'ADMINISTRADOR';

// ---------- Helpers de red ----------
async function api(method, path, body) {
  const opts = {
    method,
    headers: {
      'Authorization': 'Basic ' + auth,
      'Content-Type': 'application/json'
    }
  };
  if (body !== undefined) opts.body = JSON.stringify(body);

  const resp = await fetch('api/' + path, opts);

  if (resp.status === 401) {
    sessionStorage.clear();
    location.href = 'login.html';
    throw new Error('Sesión expirada');
  }

  const texto = await resp.text();
  const datos = texto ? JSON.parse(texto) : null;

  if (!resp.ok) {
    const msg = (datos && datos.mensaje) ? datos.mensaje : ('Error ' + resp.status);
    throw new Error(msg);
  }
  return datos;
}

// ---------- Utilidades ----------
const $ = (sel, root = document) => root.querySelector(sel);
const content = document.getElementById('content');

function toast(mensaje, tipo = '') {
  const cont = document.getElementById('toast');
  const t = document.createElement('div');
  t.className = 'toast ' + tipo;
  t.textContent = mensaje;
  cont.appendChild(t);
  setTimeout(() => t.remove(), 3500);
}

function esc(v) {
  if (v === null || v === undefined) return '';
  return String(v).replace(/[&<>"]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));
}
function num(v, dec = 2) {
  if (v === null || v === undefined || v === '') return '0';
  return Number(v).toLocaleString('es-CR', { minimumFractionDigits: 0, maximumFractionDigits: dec });
}
function money(v) { return '₡' + num(v, 2); }
function hoy() { return new Date().toISOString().slice(0, 10); }

// ============================================================
//  VISTAS
// ============================================================
const vistas = {};

// ---------- Panel / Dashboard ----------
vistas.dashboard = async () => {
  const lunes = (() => {
    const d = new Date();
    const day = (d.getDay() + 6) % 7;
    d.setDate(d.getDate() - day);
    return d.toISOString().slice(0, 10);
  })();

  content.innerHTML = `<div class="page-head"><div><h1>Panel</h1>
    <p>Resumen del periodo seleccionado</p></div></div>

    <div class="panel">
      <div class="form-grid" style="grid-template-columns:repeat(auto-fit,minmax(160px,1fr));">
        <div class="field"><label>Desde</label><input id="dDesde" type="date" value="${lunes}"></div>
        <div class="field"><label>Hasta</label><input id="dHasta" type="date" value="${hoy()}"></div>
      </div>
      <div class="actions-row">
        <button class="btn" id="dConsultar">Consultar periodo</button>
        <button class="btn secondary" id="dSemanaActual">Semana actual</button>
      </div>
    </div>

    <div id="dashPeriodo" class="muted" style="margin-bottom:12px;"></div>
    <div id="dashCards" class="cards"><div class="empty">Cargando…</div></div>`;

  const pintar = (r) => {
    $('#dashPeriodo').textContent = `Periodo mostrado: ${r.desde} a ${r.hasta}`;

    $('#dashCards').innerHTML = `
      <div class="card accent"><div class="label">Madera ingresada</div><div class="value small">${num(r.maderaIngresada)} pulgadas</div></div>
      <div class="card green"><div class="label">Procesado real</div><div class="value small">${num(r.maderaProcesada)} pulgadas</div></div>
      <div class="card"><div class="label">Aprovechamiento real</div><div class="value small">${num(r.aprovechamientoReal)} %</div></div>
      <div class="card red"><div class="label">Desperdicio real</div><div class="value small">${num(r.desperdicioReal)} pulgadas</div></div>
      <div class="card"><div class="label">Estándar esperado</div><div class="value small">${num(r.estandarEsperado, 0)} %</div></div>
      <div class="card"><div class="label">Ventas realizadas</div><div class="value small">${money(r.totalVentas)}</div></div>
      <div class="card"><div class="label">Compras realizadas</div><div class="value small">${money(r.totalCompras)}</div></div>
      <div class="card ${(r.gananciaActual || 0) >= 0 ? 'green' : 'red'}"><div class="label">Ganancia actual</div><div class="value small">${money(r.gananciaActual || 0)}</div></div>
      <div class="card"><div class="label">Pendiente por recuperar</div><div class="value small">${money(r.pendienteRecuperar || 0)}</div></div>
      <div class="card"><div class="label">Valor inventario restante</div><div class="value small">${money(r.valorInventarioRestante || 0)}</div></div>
      <div class="card ${(r.gananciaEstimada || 0) >= 0 ? 'green' : 'red'}"><div class="label">Ganancia estimada final</div><div class="value small">${money(r.gananciaEstimada || 0)}</div></div>`;
  };

  const consultar = async () => {
    const desde = $('#dDesde').value;
    const hasta = $('#dHasta').value;

    if (!desde || !hasta) {
      toast('Seleccione ambas fechas', 'err');
      return;
    }

    if (desde > hasta) {
      toast('La fecha desde no puede ser mayor que la fecha hasta', 'err');
      return;
    }

    try {
      const r = await api('GET', `reportes?desde=${desde}&hasta=${hasta}`);
      pintar(r);
    } catch (e) {
      toast(e.message, 'err');
    }
  };

  $('#dConsultar').onclick = consultar;

  $('#dSemanaActual').onclick = async () => {
    try {
      const r = await api('GET', 'reportes/semana-actual');
      $('#dDesde').value = r.desde;
      $('#dHasta').value = r.hasta;
      pintar(r);
    } catch (e) {
      toast(e.message, 'err');
    }
  };

  consultar();
};


// ---------- Clientes ----------
vistas.clientes = async () => {
  content.innerHTML = `<div class="page-head"><div><h1>Clientes</h1>
    <p>Quienes compran madera</p></div></div>
    <div class="panel">
      <h2 id="cliFormTitle">Nuevo cliente</h2>
      <div class="form-grid">
        <div class="field"><label>Nombre *</label><input id="cNombre"></div>
        <div class="field"><label>Cédula</label><input id="cCedula"></div>
        <div class="field"><label>Teléfono</label><input id="cTel"></div>
        <div class="field"><label>Correo</label><input id="cCorreo"></div>
        <div class="field"><label>Dirección</label><input id="cDir"></div>
      </div>
      <div class="actions-row">
        <button class="btn" id="cGuardar">Guardar</button>
        <button class="btn secondary hidden" id="cCancelar">Cancelar</button>
      </div>
    </div>
    <div class="panel"><div class="table-wrap"><table>
      <thead><tr><th>ID</th><th>Nombre</th><th>Cédula</th><th>Teléfono</th><th>Correo</th><th>Estado</th><th></th></tr></thead>
      <tbody id="cliBody"><tr><td colspan="7" class="empty">Cargando…</td></tr></tbody>
    </table></div></div>`;

  let editId = null;

  const limpiar = () => {
    editId = null;
    ['cNombre', 'cCedula', 'cTel', 'cCorreo', 'cDir'].forEach(i => $('#' + i).value = '');
    $('#cliFormTitle').textContent = 'Nuevo cliente';
    $('#cCancelar').classList.add('hidden');
  };

  const cargar = async () => {
    const lista = await api('GET', 'clientes');

    $('#cliBody').innerHTML = lista.length ? lista.map(c => `
      <tr>
        <td>${c.id}</td>
        <td>${esc(c.nombre)}</td>
        <td>${esc(c.cedula)}</td>
        <td>${esc(c.telefono)}</td>
        <td>${esc(c.correo)}</td>
        <td><span class="tag ${c.estado ? 'ok' : 'off'}">${c.estado ? 'Activo' : 'Inactivo'}</span></td>
        <td>
          <button class="link-btn" data-edit="${c.id}">Editar</button> ·
          ${c.estado
            ? `<button class="link-btn" data-del="${c.id}">Desactivar</button>`
            : `<button class="link-btn" data-act="${c.id}">Activar</button>`}
        </td>
      </tr>
    `).join('') : '<tr><td colspan="7" class="empty">Sin clientes aún</td></tr>';

    $('#cliBody').querySelectorAll('[data-edit]').forEach(b => b.onclick = () => {
      const c = lista.find(x => x.id == b.dataset.edit);
      editId = c.id;

      $('#cNombre').value = c.nombre || '';
      $('#cCedula').value = c.cedula || '';
      $('#cTel').value = c.telefono || '';
      $('#cCorreo').value = c.correo || '';
      $('#cDir').value = c.direccion || '';

      $('#cliFormTitle').textContent = 'Editar cliente #' + c.id;
      $('#cCancelar').classList.remove('hidden');
      window.scrollTo(0, 0);
    });

    $('#cliBody').querySelectorAll('[data-del]').forEach(b => b.onclick = async () => {
      if (!confirm('¿Desactivar este cliente?')) return;

      try {
        await api('DELETE', 'clientes/' + b.dataset.del);
        toast('Cliente desactivado', 'ok');
        cargar();
      } catch (e) {
        toast(e.message, 'err');
      }
    });

    $('#cliBody').querySelectorAll('[data-act]').forEach(b => b.onclick = async () => {
      if (!confirm('¿Activar este cliente?')) return;

      try {
        await api('PUT', 'clientes/' + b.dataset.act + '/activar');
        toast('Cliente activado', 'ok');
        cargar();
      } catch (e) {
        toast(e.message, 'err');
      }
    });
  };

  $('#cGuardar').onclick = async () => {
    const body = {
      nombre: $('#cNombre').value.trim(),
      cedula: $('#cCedula').value.trim(),
      telefono: $('#cTel').value.trim(),
      correo: $('#cCorreo').value.trim(),
      direccion: $('#cDir').value.trim()
    };

    if (!body.nombre) {
      toast('El nombre es obligatorio', 'err');
      return;
    }

    try {
      if (editId) {
        await api('PUT', 'clientes/' + editId, body);
        toast('Cliente actualizado', 'ok');
      } else {
        await api('POST', 'clientes', body);
        toast('Cliente creado', 'ok');
      }

      limpiar();
      cargar();
    } catch (e) {
      toast(e.message, 'err');
    }
  };

  $('#cCancelar').onclick = limpiar;

  cargar();
};

// ---------- Ingreso de madera ----------
vistas.ingresos = async () => {
  content.innerHTML = `<div class="page-head"><div><h1>Ingreso de madera</h1>
    <p>Registra cada camión. El sistema calcula las pulgadas madereras y el rendimiento 60/40.</p></div></div>

    <div class="panel">
      <h2>Nuevo ingreso</h2>

      <div class="form-grid">
        <div class="field"><label>Proveedor *</label><select id="iProv"></select></div>
        <div class="field"><label>Fecha</label><input id="iFecha" type="date" value="${hoy()}"></div>
        <div class="field"><label>Placa del camión</label><input id="iPlaca" placeholder="Ej: ABC-123"></div>
        <div class="field"><label>Tipo de madera</label><input id="iTipo" placeholder="Ej: Pino, Laurel, Melina"></div>

        <div class="field">
          <label>Largo del camión *</label>
          <input id="iLargo" type="number" step="0.01" min="0" placeholder="Ej: 6">
        </div>

        <div class="field">
          <label>Largo de la tuca *</label>
          <input id="iAncho" type="number" step="0.01" min="0" placeholder="Ej: 3">
        </div>

        <div class="field">
          <label>Alto del camión *</label>
          <input id="iAlto" type="number" step="0.01" min="0" placeholder="Ej: 2">
        </div>

        <div class="field">
          <label>Precio compra (₡/pulgada)</label>
          <input id="iPComp" type="number" step="0.01" min="0" placeholder="Ej: 170">
        </div>

        <div class="field">
          <label>Precio venta (₡/pulgada)</label>
          <input id="iPVent" type="number" step="0.01" min="0" placeholder="Ej: 250">
        </div>
      </div>

      <div class="calc-box" id="iCalc">
        <div><b>Fórmula:</b> alto del camión × largo del camión × largo de la tuca × 0.56 × 362</div>
        <hr>
        <div>Pulgadas calculadas: <b id="cVol">0</b> pulgadas</div>
        <div>Total compra: <b id="cCompra">₡0</b></div>
        <div class="muted" style="margin-top:6px;">Estándar esperado (solo referencia):</div>
        <div>Aprovechable estándar (60%): <b id="cApr">0</b> pulgadas</div>
        <div>Desperdicio estándar (40%): <b id="cDes">0</b> pulgadas</div>
      </div>

      <div class="actions-row">
        <button class="btn" id="iGuardar">Registrar ingreso</button>
      </div>
    </div>

    <div class="panel">
      <h2>Ingresos registrados</h2>
      <div class="table-wrap"><table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Fecha</th>
            <th>Proveedor</th>
            <th>Tipo</th>
            <th>Pulgadas</th>
            <th>Aprovechable est. (60%)</th>
            <th>Desperdicio est. (40%)</th>
          </tr>
        </thead>
        <tbody id="ingBody">
          <tr><td colspan="7" class="empty">Cargando…</td></tr>
        </tbody>
      </table></div>
    </div>`;

  const provs = await api('GET', 'proveedores');
  $('#iProv').innerHTML = '<option value="">— Seleccione —</option>' +
    provs.filter(p => p.estado).map(p => `<option value="${p.id}">${esc(p.nombre)}</option>`).join('');

const recalcular = () => {
  const largoCamion = parseFloat($('#iLargo').value) || 0;
  const largoTuca = parseFloat($('#iAncho').value) || 0;
  const altoCamion = parseFloat($('#iAlto').value) || 0;
  const precioCompra = parseFloat($('#iPComp').value) || 0;

  const pulgadas = altoCamion * largoCamion * largoTuca * 0.56 * 362;
  const totalCompra = pulgadas * precioCompra;

  $('#cVol').textContent = num(pulgadas);
  $('#cCompra').textContent = money(totalCompra);
  $('#cApr').textContent = num(pulgadas * 0.60);
  $('#cDes').textContent = num(pulgadas * 0.40);
};

['iLargo', 'iAncho', 'iAlto', 'iPComp'].forEach(i =>
  $('#' + i).addEventListener('input', recalcular)
);

  const cargar = async () => {
    const lista = await api('GET', 'ingresos');
    $('#ingBody').innerHTML = lista.length ? lista.map(i => {
      const r = i.rendimiento || {};
      return `<tr><td>${i.id}</td><td>${esc(i.fechaIngreso)}</td>
        <td>${esc(i.proveedor ? i.proveedor.nombre : '')}</td><td>${esc(i.tipoMadera)}</td>
        <td>${num(i.volumenTotal)} pulgadas</td><td>${num(r.volumenAprovechable)} pulgadas</td>
        <td>${num(r.volumenDesperdicio)} pulgadas</td></tr>`;
    }).join('') : '<tr><td colspan="7" class="empty">Sin ingresos aún</td></tr>';
  };

  $('#iGuardar').onclick = async () => {
    const body = {
      idProveedor: $('#iProv').value ? Number($('#iProv').value) : null,
      fechaIngreso: $('#iFecha').value || null,
      placaCamion: $('#iPlaca').value.trim(),
      tipoMadera: $('#iTipo').value.trim(),
      largoCamion: parseFloat($('#iLargo').value),
      anchoCamion: parseFloat($('#iAncho').value),
      altoCarga: parseFloat($('#iAlto').value),
      precioCompra: $('#iPComp').value ? parseFloat($('#iPComp').value) : null,
      precioVenta: $('#iPVent').value ? parseFloat($('#iPVent').value) : null
    };
    if (!body.idProveedor) { toast('Seleccione un proveedor', 'err'); return; }
    if (!body.largoCamion || !body.anchoCamion || !body.altoCarga) { toast('Largo, ancho y alto son obligatorios', 'err'); return; }
    try {
      const res = await api('POST', 'ingresos', body);
      const r = res.rendimiento || {};
      toast(`Ingreso registrado. Aprovechable: ${num(r.volumenAprovechable)} pulgadas`, 'ok');
      ['iPlaca', 'iTipo', 'iLargo', 'iAncho', 'iAlto', 'iPComp', 'iPVent'].forEach(i => $('#' + i).value = '');
      recalcular(); cargar();
    } catch (e) { toast(e.message, 'err'); }
  };
  cargar();
};



// ---------- Procesamiento diario ----------
vistas.procesamientos = async () => {
  content.innerHTML = `<div class="page-head"><div><h1>Procesamiento diario</h1>
    <p>Registra las pulgadas realmente procesadas día por día a partir de los ingresos de madera.</p></div></div>

    <div class="panel">
      <h2>Nuevo procesamiento</h2>
      <div class="form-grid">
        <div class="field"><label>Fecha</label><input id="prFecha" type="date" value="${hoy()}"></div>

        <div class="field">
          <label>Ingreso de madera *</label>
          <select id="prIngreso"></select>
        </div>

        <div class="field">
          <label>Tipo de madera</label>
          <input id="prTipo" disabled>
        </div>

        <div class="field">
          <label>Pulgadas ingresadas</label>
          <input id="prTotalIngreso" disabled>
        </div>

        <div class="field">
          <label>Pulgadas ya procesadas</label>
          <input id="prProcesado" disabled>
        </div>

        <div class="field">
          <label>Pulgadas disponibles</label>
          <input id="prDisponibleInput" disabled>
        </div>

        <div class="field">
          <label>Producto obtenido *</label>
          <input id="prProducto" placeholder="Ej: Tablas, tarimas">
        </div>

        <div class="field">
          <label>Pulgadas procesadas *</label>
          <input id="prPulgadas" type="number" step="0.01" min="0">
        </div>

        <div class="field">
          <label>Observación</label>
          <input id="prObs" placeholder="Opcional">
        </div>
      </div>

      <div class="calc-box">
        <div>Ingreso seleccionado: <b id="prInfoIngreso">—</b></div>
        <div>Pulgadas disponibles del ingreso: <b id="prDisponible">0</b> pulgadas</div>
      </div>

      <div class="actions-row">
        <button class="btn" id="prGuardar">Registrar procesamiento</button>
      </div>
    </div>

    <div class="panel">
      <h2>Procesamientos registrados</h2>
      <div class="table-wrap"><table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Fecha</th>
            <th>Ingreso</th>
            <th>Tipo</th>
            <th>Producto</th>
            <th>Pulgadas</th>
            <th>Estado</th>
            <th></th>
          </tr>
        </thead>
        <tbody id="prBody"><tr><td colspan="8" class="empty">Cargando…</td></tr></tbody>
      </table></div>
    </div>`;

  let ingresos = await api('GET', 'ingresos/disponibles-procesamiento');

  const cargarIngresos = async () => {
    ingresos = await api('GET', 'ingresos/disponibles-procesamiento');

    const ingresosActivos = ingresos.filter(i => (i.disponible || 0) > 0);

    $('#prIngreso').innerHTML = '<option value="">— Seleccione ingreso —</option>' +
      ingresosActivos.map(i => `
        <option value="${i.id}">
          #${i.id} - ${esc(i.tipoMadera)} - Disponible: ${num(i.disponible)} pulgadas
        </option>
      `).join('');
  };

  const limpiarIngresoSeleccionado = () => {
    $('#prTipo').value = '';
    $('#prTotalIngreso').value = '';
    $('#prProcesado').value = '';
    $('#prDisponibleInput').value = '';
    $('#prInfoIngreso').textContent = '—';
    $('#prDisponible').textContent = '0';
  };

  const actualizarIngresoSeleccionado = () => {
    const idIngreso = $('#prIngreso').value ? Number($('#prIngreso').value) : null;
    const ingreso = ingresos.find(i => Number(i.id) === idIngreso);

    if (!ingreso) {
      limpiarIngresoSeleccionado();
      return;
    }

    const totalIngreso = ingreso.volumenTotal || 0;
    const procesado = ingreso.procesado || 0;
    const disponible = ingreso.disponible || 0;

    $('#prTipo').value = ingreso.tipoMadera || '';
    $('#prTotalIngreso').value = `${num(totalIngreso)} pulgadas`;
    $('#prProcesado').value = `${num(procesado)} pulgadas`;
    $('#prDisponibleInput').value = `${num(disponible)} pulgadas`;
    $('#prInfoIngreso').textContent = `#${ingreso.id} - ${ingreso.tipoMadera}`;
    $('#prDisponible').textContent = num(disponible);
  };

  const cargar = async () => {
    const lista = await api('GET', 'procesamientos');

    $('#prBody').innerHTML = lista.length ? lista.map(p => `
      <tr>
        <td>${p.id}</td>
        <td>${esc(p.fecha)}</td>
        <td>${p.ingreso ? '#' + p.ingreso.id : '—'}</td>
        <td>${esc(p.tipoMadera)}</td>
        <td>${esc(p.productoObtenido)}</td>
        <td>${num(p.pulgadasProcesadas)} pulgadas</td>
        <td><span class="tag ${p.estado === 'ACTIVO' ? 'ok' : 'off'}">${esc(p.estado)}</span></td>
        <td>${p.estado === 'ACTIVO' ? `<button class="link-btn" data-anular="${p.id}">Anular</button>` : ''}</td>
      </tr>
    `).join('') : '<tr><td colspan="8" class="empty">Sin procesamientos aún</td></tr>';

    $('#prBody').querySelectorAll('[data-anular]').forEach(b => b.onclick = async () => {
      if (!confirm('¿Anular este procesamiento?')) return;

      try {
        await api('PUT', 'procesamientos/' + b.dataset.anular + '/anular');
        toast('Procesamiento anulado', 'ok');

        await cargarIngresos();
        limpiarIngresoSeleccionado();
        cargar();
      } catch (e) {
        toast(e.message, 'err');
      }
    });
  };

  $('#prIngreso').onchange = actualizarIngresoSeleccionado;

  $('#prGuardar').onclick = async () => {
    const idIngreso = $('#prIngreso').value ? Number($('#prIngreso').value) : null;
    const ingreso = ingresos.find(i => Number(i.id) === idIngreso);

    const body = {
      idIngreso: idIngreso,
      fecha: $('#prFecha').value || null,
      tipoMadera: $('#prTipo').value.trim(),
      productoObtenido: $('#prProducto').value.trim(),
      pulgadasProcesadas: $('#prPulgadas').value ? parseFloat($('#prPulgadas').value) : null,
      observacion: $('#prObs').value.trim()
    };

    if (!body.idIngreso || !ingreso) {
      toast('Seleccione un ingreso de madera', 'err');
      return;
    }

    if (!body.productoObtenido) {
      toast('Ingrese el producto obtenido', 'err');
      return;
    }

    if (!body.pulgadasProcesadas || body.pulgadasProcesadas <= 0) {
      toast('Ingrese pulgadas procesadas válidas', 'err');
      return;
    }

    const disponible = ingreso.disponible || 0;

    if (body.pulgadasProcesadas > disponible) {
      toast(`No puede procesar más de ${num(disponible)} pulgadas disponibles`, 'err');
      return;
    }

    try {
      await api('POST', 'procesamientos', body);
      toast('Procesamiento registrado e inventario actualizado', 'ok');

      $('#prProducto').value = '';
      $('#prPulgadas').value = '';
      $('#prObs').value = '';

      await cargarIngresos();
      limpiarIngresoSeleccionado();
      cargar();
    } catch (e) {
      toast(e.message, 'err');
    }
  };

  await cargarIngresos();
  cargar();
};


// ---------- Inventario ----------
const DIAS_SEMANA = ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];
function diaDeSemana(fechaIso) {
  if (!fechaIso) return '—';
  // fechaIso viene como 'YYYY-MM-DD'; se arma en UTC para no desfasar el día.
  const [a, m, d] = fechaIso.split('-').map(Number);
  const fecha = new Date(Date.UTC(a, m - 1, d));
  return DIAS_SEMANA[fecha.getUTCDay()];
}

vistas.inventario = async () => {
  content.innerHTML = `<div class="page-head"><div><h1>Inventario</h1>
    <p>Madera aprovechable disponible para la venta</p></div></div>
    <div class="panel"><div class="table-wrap"><table>
      <thead><tr><th>ID</th><th>Tipo de madera</th><th>Disponible</th><th>Total ingresado</th><th>Precio venta</th><th>Estado</th></tr></thead>
      <tbody id="invBody"><tr><td colspan="6" class="empty">Cargando…</td></tr></tbody>
    </table></div></div>
    <div class="panel">
      <h2>Historial de procesamiento</h2>
      <p class="muted" style="margin:-6px 0 12px;">Día calculado automáticamente a partir de la fecha de cada procesamiento.</p>
      <div class="table-wrap"><table>
        <thead><tr>
          <th>ID</th><th>Fecha</th><th>Día</th><th>Ingreso</th><th>Tipo de madera</th>
          <th>Producto obtenido</th><th>Pulgadas procesadas</th><th>Estado</th>
        </tr></thead>
        <tbody id="histBody"><tr><td colspan="8" class="empty">Cargando…</td></tr></tbody>
      </table></div>
    </div>`;
  try {
    const lista = await api('GET', 'productos');
    $('#invBody').innerHTML = lista.length ? lista.map(p => `
      <tr><td>${p.id}</td><td>${esc(p.tipoMadera)}</td>
      <td><b>${num(p.cantidadDisponible)}</b> ${esc(p.unidadMedida)}</td>
      <td>${num(p.cantidadTotal)} ${esc(p.unidadMedida)}</td>
      <td>${p.precioVenta ? money(p.precioVenta) : '—'}</td>
      <td><span class="tag ${p.cantidadDisponible > 0 ? 'ok' : 'warn'}">${esc(p.estado)}</span></td></tr>`).join('')
      : '<tr><td colspan="6" class="empty">El inventario está vacío. Registra un ingreso de madera para llenarlo.</td></tr>';
  } catch (e) { toast(e.message, 'err'); }

  try {
    const historial = await api('GET', 'procesamientos');
    const ordenado = [...historial].sort((a, b) => (a.fecha < b.fecha ? 1 : -1));
    $('#histBody').innerHTML = ordenado.length ? ordenado.map(p => `
      <tr>
        <td>${p.id}</td>
        <td>${esc(p.fecha)}</td>
        <td>${diaDeSemana(p.fecha)}</td>
        <td>${p.ingreso ? '#' + p.ingreso.id : '—'}</td>
        <td>${esc(p.tipoMadera)}</td>
        <td>${esc(p.productoObtenido)}</td>
        <td>${num(p.pulgadasProcesadas)} pulgadas</td>
        <td><span class="tag ${p.estado === 'ACTIVO' ? 'ok' : 'off'}">${esc(p.estado)}</span></td>
      </tr>
    `).join('') : '<tr><td colspan="8" class="empty">Aún no hay procesamientos registrados.</td></tr>';
  } catch (e) { toast(e.message, 'err'); }
};

// ---------- Ventas (factura cliente) ----------
vistas.ventas = async () => {
  content.innerHTML = `<div class="page-head"><div><h1>Ventas</h1>
    <p>Factura al cliente. Descuenta del inventario automáticamente.</p></div></div>
    <div class="panel">
      <h2>Nueva venta</h2>
      <div class="form-grid">
        <div class="field"><label>Cliente *</label><select id="vCliente"></select></div>
        <div class="field"><label>Fecha</label><input id="vFecha" type="date" value="${hoy()}"></div>
        <div class="field"><label>N° factura</label><input id="vNum"></div>
      </div>
      <h2 style="margin-top:18px; font-size:15px;">Productos</h2>
      <div class="table-wrap"><table class="lineas">
        <thead><tr><th>Producto</th><th>Disponible</th><th>Cantidad</th><th>Precio</th><th>Subtotal</th><th></th></tr></thead>
        <tbody id="vLineas"></tbody>
      </table></div>
      <div class="actions-row">
        <button class="btn secondary small" id="vAddLinea">+ Agregar producto</button>
        <div style="flex:1"></div>
        <div style="font-size:18px; font-weight:700; align-self:center;">Total: <span id="vTotal">₡0</span></div>
      </div>
      <div class="actions-row"><button class="btn" id="vGuardar">Registrar venta</button></div>
    </div>
    <div class="panel"><h2>Ventas registradas</h2><div class="table-wrap"><table>
      <thead><tr><th>ID</th><th>Fecha</th><th>Cliente</th><th>N° factura</th><th>Total</th><th>Estado</th><th></th></tr></thead>
      <tbody id="vBody"><tr><td colspan="7" class="empty">Cargando…</td></tr></tbody>
    </table></div></div>`;

  const [clientes, productos] = await Promise.all([api('GET', 'clientes'), api('GET', 'productos')]);
  $('#vCliente').innerHTML = '<option value="">— Seleccione —</option>' +
    clientes.filter(c => c.estado).map(c => `<option value="${c.id}">${esc(c.nombre)}</option>`).join('');

  const optProductos = () => '<option value="">— Producto —</option>' +
    productos.map(p => `<option value="${p.id}" data-disp="${p.cantidadDisponible}" data-precio="${p.precioVenta || 0}">${esc(p.tipoMadera)}</option>`).join('');

  const lineas = $('#vLineas');
  const recalcTotal = () => {
    let total = 0;
    lineas.querySelectorAll('tr').forEach(tr => {
      total += parseFloat(tr.querySelector('.subt').dataset.val || 0);
    });
    $('#vTotal').textContent = money(total);
  };
  const addLinea = () => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><select class="lp">${optProductos()}</select></td>
      <td class="disp muted">—</td>
      <td><input class="lc" type="number" step="0.01" min="0" style="width:90px"></td>
      <td class="lpr muted">—</td>
      <td class="subt" data-val="0">₡0</td>
      <td><button class="link-btn lx">✕</button></td>`;
    lineas.appendChild(tr);
    const sel = tr.querySelector('.lp'), cant = tr.querySelector('.lc');
    const recalc = () => {
      const opt = sel.options[sel.selectedIndex];
      const precio = opt ? parseFloat(opt.dataset.precio || 0) : 0;
      const disp = opt ? opt.dataset.disp : null;
      tr.querySelector('.disp').textContent = disp !== null && disp !== undefined ? num(disp) + ' pulgadas' : '—';
      tr.querySelector('.lpr').textContent = precio ? money(precio) : '—';
      const sub = (parseFloat(cant.value) || 0) * precio;
      const subEl = tr.querySelector('.subt');
      subEl.dataset.val = sub; subEl.textContent = money(sub);
      recalcTotal();
    };
    sel.onchange = recalc; cant.oninput = recalc;
    tr.querySelector('.lx').onclick = () => { tr.remove(); recalcTotal(); };
  };
  $('#vAddLinea').onclick = addLinea;
  addLinea();

  const cargar = async () => {
    const lista = await api('GET', 'facturas-cliente');
    $('#vBody').innerHTML = lista.length ? lista.map(f => `
      <tr><td>${f.id}</td><td>${esc(f.fechaFactura)}</td><td>${esc(f.cliente ? f.cliente.nombre : '')}</td>
      <td>${esc(f.numeroFactura)}</td><td>${money(f.total)}</td>
      <td><span class="tag ${f.estado === 'ACTIVA' ? 'ok' : 'off'}">${esc(f.estado)}</span></td>
      <td>${f.estado === 'ACTIVA' ? `<button class="link-btn" data-anular="${f.id}">Anular</button>` : ''}</td></tr>`).join('')
      : '<tr><td colspan="7" class="empty">Sin ventas aún</td></tr>';
    $('#vBody').querySelectorAll('[data-anular]').forEach(b => b.onclick = async () => {
      if (!confirm('¿Anular esta factura?')) return;
      try { await api('PUT', 'facturas-cliente/' + b.dataset.anular + '/anular'); toast('Factura anulada', 'ok'); cargar(); }
      catch (e) { toast(e.message, 'err'); }
    });
  };

  $('#vGuardar').onclick = async () => {
    const detalles = [];
    lineas.querySelectorAll('tr').forEach(tr => {
      const sel = tr.querySelector('.lp'); const cant = parseFloat(tr.querySelector('.lc').value);
      if (sel.value && cant > 0) detalles.push({ idProducto: Number(sel.value), cantidad: cant });
    });
    const body = {
      idCliente: $('#vCliente').value ? Number($('#vCliente').value) : null,
      fechaFactura: $('#vFecha').value || null,
      numeroFactura: $('#vNum').value.trim(),
      detalles
    };
    if (!body.idCliente) { toast('Seleccione un cliente', 'err'); return; }
    if (!body.numeroFactura) { toast('Ingrese el número de factura', 'err'); return; }
    if (!detalles.length) { toast('Agregue al menos un producto con cantidad', 'err'); return; }
    try {
      await api('POST', 'facturas-cliente', body);
      toast('Venta registrada e inventario actualizado', 'ok');
      vistas.ventas(); // recargar la vista completa para refrescar inventario en selects
    } catch (e) { toast(e.message, 'err'); }
  };
  cargar();
};

// ---------- Facturas de proveedor ----------
vistas.comprasprov = async () => {
  content.innerHTML = `<div class="page-head"><div><h1>Facturas de proveedor</h1>
    <p>Registra el costo de compra tomando los datos del ingreso de madera.</p></div></div>

    <div class="panel">
      <h2>Nueva factura</h2>

      <div class="form-grid">
        <div class="field"><label>Proveedor *</label><select id="fpProv"></select></div>
        <div class="field"><label>Ingreso de madera *</label><select id="fpIngreso"></select></div>
        <div class="field"><label>Fecha</label><input id="fpFecha" type="date" value="${hoy()}"></div>
        <div class="field"><label>N° factura</label><input id="fpNum" placeholder="Ej: F-001"></div>
      </div>

      <h2 style="margin-top:18px; font-size:15px;">Detalle automático</h2>

      <div class="table-wrap"><table>
        <thead>
          <tr>
            <th>Tipo de madera</th>
            <th>Pulgadas</th>
            <th>Precio compra</th>
            <th>Subtotal</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td id="fpTipo">—</td>
            <td id="fpCantidad">—</td>
            <td id="fpPrecio">—</td>
            <td id="fpSubtotal">₡0</td>
          </tr>
        </tbody>
      </table></div>

      <div class="calc-box" style="margin-top:14px;">
        <div><b>Fórmula:</b> pulgadas calculadas × precio compra</div>
        <div>Total factura: <b id="fpTotal">₡0</b></div>
      </div>

      <div class="actions-row">
        <button class="btn" id="fpGuardar">Registrar factura</button>
      </div>
    </div>

    <div class="panel">
      <h2>Facturas registradas</h2>
      <div class="table-wrap"><table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Fecha</th>
            <th>Proveedor</th>
            <th>N° factura</th>
            <th>Total</th>
            <th>Estado</th>
            <th></th>
          </tr>
        </thead>
        <tbody id="fpBody">
          <tr><td colspan="7" class="empty">Cargando…</td></tr>
        </tbody>
      </table></div>
    </div>`;

  const [provs, ingresos] = await Promise.all([
    api('GET', 'proveedores'),
    api('GET', 'ingresos')
  ]);

  $('#fpProv').innerHTML = '<option value="">— Seleccione proveedor —</option>' +
    provs.filter(p => p.estado)
      .map(p => `<option value="${p.id}">${esc(p.nombre)}</option>`)
      .join('');

  const cargarIngresos = () => {
    const idProveedor = $('#fpProv').value ? Number($('#fpProv').value) : null;

    const filtrados = ingresos.filter(i => {
      if (!idProveedor) return true;
      return i.proveedor && Number(i.proveedor.id) === idProveedor;
    });

    $('#fpIngreso').innerHTML = '<option value="">— Seleccione ingreso —</option>' +
      filtrados.map(i => `
        <option value="${i.id}">
          #${i.id} - ${esc(i.tipoMadera)} - ${num(i.volumenTotal)} pulgadas
        </option>
      `).join('');

    limpiarDetalle();
  };

  const limpiarDetalle = () => {
    $('#fpTipo').textContent = '—';
    $('#fpCantidad').textContent = '—';
    $('#fpPrecio').textContent = '—';
    $('#fpSubtotal').textContent = '₡0';
    $('#fpTotal').textContent = '₡0';
  };

  const mostrarDetalleIngreso = () => {
    const idIngreso = $('#fpIngreso').value ? Number($('#fpIngreso').value) : null;
    const ingreso = ingresos.find(i => Number(i.id) === idIngreso);

    if (!ingreso) {
      limpiarDetalle();
      return;
    }

    const pulgadas = ingreso.volumenTotal || 0;
    const precioCompra = ingreso.precioCompra || 0;
    const subtotal = pulgadas * precioCompra;

    $('#fpTipo').textContent = ingreso.tipoMadera || '—';
    $('#fpCantidad').textContent = `${num(pulgadas)} pulgadas`;
    $('#fpPrecio').textContent = money(precioCompra);
    $('#fpSubtotal').textContent = money(subtotal);
    $('#fpTotal').textContent = money(subtotal);
  };

  $('#fpProv').onchange = cargarIngresos;
  $('#fpIngreso').onchange = mostrarDetalleIngreso;

  cargarIngresos();

  const cargar = async () => {
    const lista = await api('GET', 'facturas-proveedor');

    $('#fpBody').innerHTML = lista.length ? lista.map(f => `
      <tr>
        <td>${f.id}</td>
        <td>${esc(f.fechaFactura)}</td>
        <td>${esc(f.proveedor ? f.proveedor.nombre : '')}</td>
        <td>${esc(f.numeroFactura)}</td>
        <td>${money(f.total)}</td>
        <td><span class="tag ${f.estado === 'ACTIVA' ? 'ok' : 'off'}">${esc(f.estado)}</span></td>
        <td>${f.estado === 'ACTIVA' ? `<button class="link-btn" data-anular="${f.id}">Anular</button>` : ''}</td>
      </tr>
    `).join('') : '<tr><td colspan="7" class="empty">Sin facturas aún</td></tr>';

    $('#fpBody').querySelectorAll('[data-anular]').forEach(b => b.onclick = async () => {
      if (!confirm('¿Anular esta factura?')) return;

      try {
        await api('PUT', 'facturas-proveedor/' + b.dataset.anular + '/anular');
        toast('Factura anulada', 'ok');
        cargar();
      } catch (e) {
        toast(e.message, 'err');
      }
    });
  };

  $('#fpGuardar').onclick = async () => {
    const body = {
      idProveedor: $('#fpProv').value ? Number($('#fpProv').value) : null,
      idIngreso: $('#fpIngreso').value ? Number($('#fpIngreso').value) : null,
      fechaFactura: $('#fpFecha').value || null,
      numeroFactura: $('#fpNum').value.trim(),
      detalles: []
    };

  if (!body.idProveedor) {
  toast('Seleccione un proveedor', 'err');
  return;
}

if (!body.idIngreso) {
  toast('Seleccione un ingreso de madera', 'err');
  return;
}

if (!body.numeroFactura) {
  toast('Ingrese el número de factura', 'err');
  return;
}

    try {
      await api('POST', 'facturas-proveedor', body);
      toast('Factura registrada con datos del ingreso', 'ok');
      vistas.comprasprov();
    } catch (e) {
      toast(e.message, 'err');
    }
  };

  cargar();
};


//   ---- Reportes ----
vistas.reportes = async () => {
  const lunes = (() => {
    const d = new Date();
    const day = (d.getDay() + 6) % 7;
    d.setDate(d.getDate() - day);
    return d.toISOString().slice(0, 10);
  })();

  content.innerHTML = `<div class="page-head"><div><h1>Reportes</h1>
    <p>Resumen real por rango de fechas. Las compras se asignan a la semana del ingreso de madera.</p></div></div>

    <div class="panel">
      <div class="form-grid" style="grid-template-columns:repeat(auto-fit,minmax(160px,1fr));">
        <div class="field"><label>Desde</label><input id="rDesde" type="date" value="${lunes}"></div>
        <div class="field"><label>Hasta</label><input id="rHasta" type="date" value="${hoy()}"></div>
      </div>

      <div class="actions-row">
        <button class="btn" id="rGenerar">Generar reporte</button>
        <button class="btn secondary" id="rPdf">📄 Descargar PDF semanal</button>
      </div>
    </div>

    <div id="rCards" class="cards"></div>

    <div class="panel hidden" id="rDetalle">
      <h2>Detalle del reporte</h2>
      <div class="table-wrap"><table>
        <tbody id="rTabla"></tbody>
      </table></div>
    </div>`;

  const validarFechas = () => {
    const desde = $('#rDesde').value;
    const hasta = $('#rHasta').value;

    if (!desde || !hasta) {
      toast('Seleccione ambas fechas', 'err');
      return null;
    }

    if (desde > hasta) {
      toast('La fecha desde no puede ser mayor que la fecha hasta', 'err');
      return null;
    }

    return { desde, hasta };
  };

  $('#rPdf').onclick = async () => {
    const fechas = validarFechas();
    if (!fechas) return;

    try {
      const resp = await fetch(`api/reportes/pdf?desde=${fechas.desde}&hasta=${fechas.hasta}`, {
        headers: { 'Authorization': 'Basic ' + auth }
      });

      if (resp.status === 401) {
        sessionStorage.clear();
        location.href = 'login.html';
        return;
      }

      if (!resp.ok) {
        toast('No se pudo generar el PDF (error ' + resp.status + ')', 'err');
        return;
      }

      const blob = await resp.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');

      a.href = url;
      a.download = `reporte-ecomaderas-rosales-${fechas.desde}_${fechas.hasta}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();

      URL.revokeObjectURL(url);
      toast('PDF generado correctamente', 'ok');
    } catch (e) {
      toast('No se pudo descargar el PDF', 'err');
    }
  };

  const generar = async () => {
    const fechas = validarFechas();
    if (!fechas) return;

    try {
      const r = await api('GET', `reportes?desde=${fechas.desde}&hasta=${fechas.hasta}`);

      $('#rCards').innerHTML = `
        <div class="card accent">
          <div class="label">Madera ingresada</div>
          <div class="value small">${num(r.maderaIngresada)} pulgadas</div>
        </div>

        <div class="card green">
          <div class="label">Procesado real</div>
          <div class="value small">${num(r.maderaProcesada)} pulgadas</div>
        </div>

        <div class="card">
          <div class="label">Aprovechamiento real</div>
          <div class="value small">${num(r.aprovechamientoReal)} %</div>
        </div>

        <div class="card red">
          <div class="label">Desperdicio real</div>
          <div class="value small">${num(r.desperdicioReal)} pulgadas</div>
        </div>

        <div class="card">
          <div class="label">Estándar esperado</div>
          <div class="value small">${num(r.estandarEsperado, 0)} %</div>
        </div>

        <div class="card">
          <div class="label">Ventas</div>
          <div class="value small">${money(r.totalVentas)}</div>
        </div>

        <div class="card">
          <div class="label">Compras</div>
          <div class="value small">${money(r.totalCompras)}</div>
        </div>

        <div class="card ${(r.gananciaActual || 0) >= 0 ? 'green' : 'red'}">
          <div class="label">Ganancia real</div>
          <div class="value small">${money(r.gananciaActual || 0)}</div>
        </div>`;

      $('#rDetalle').classList.remove('hidden');

      $('#rTabla').innerHTML = `
        <tr><th>Periodo</th><td>${esc(r.desde)} a ${esc(r.hasta)}</td></tr>
        <tr><th>Madera ingresada</th><td>${num(r.maderaIngresada)} pulgadas</td></tr>
        <tr><th>Madera procesada</th><td>${num(r.maderaProcesada)} pulgadas</td></tr>
        <tr><th>Aprovechamiento real</th><td>${num(r.aprovechamientoReal)} % (procesado real ÷ ingresado)</td></tr>
        <tr><th>Desperdicio real</th><td>${num(r.desperdicioReal)} pulgadas (${num(r.desperdicioRealPorcentaje)} %)</td></tr>
        <tr><th>Aprovechable según estándar 60%</th><td>${num(r.maderaAprovechableEstandar)} pulgadas (referencia)</td></tr>
        <tr><th>Ventas activas</th><td>${money(r.totalVentas)}</td></tr>
        <tr><th>Compras activas</th><td>${money(r.totalCompras)}</td></tr>
        <tr><th>Ganancia real</th><td>${money(r.gananciaActual || 0)}</td></tr>
        <tr><th>Ingresos registrados</th><td>${r.cantidadIngresos}</td></tr>
        <tr><th>Facturas de venta</th><td>${r.cantidadFacturasCliente}</td></tr>
        <tr><th>Facturas de compra</th><td>${r.cantidadFacturasProveedor}</td></tr>`;

      toast('Reporte generado correctamente', 'ok');
    } catch (e) {
      toast(e.message, 'err');
    }
  };

  $('#rGenerar').onclick = generar;
  generar();
};

// ---------- Usuarios (solo admin) ----------
vistas.usuarios = async () => {
  if (!esAdmin) { content.innerHTML = '<div class="panel"><div class="empty">Solo el administrador puede ver esta sección.</div></div>'; return; }
  content.innerHTML = `<div class="page-head"><div><h1>Usuarios</h1>
    <p>Gestión de usuarios y roles del sistema</p></div></div>
    <div class="panel">
      <h2>Nuevo usuario</h2>
      <div class="form-grid">
        <div class="field"><label>Usuario *</label><input id="uUser"></div>
        <div class="field"><label>Contraseña *</label><input id="uPass" type="password"></div>
        <div class="field"><label>Nombre completo *</label><input id="uNombre"></div>
        <div class="field"><label>Rol *</label><select id="uRol"></select></div>
      </div>
      <div class="actions-row"><button class="btn" id="uGuardar">Crear usuario</button></div>
    </div>
    <div class="panel"><div class="table-wrap"><table>
      <thead><tr><th>ID</th><th>Usuario</th><th>Nombre</th><th>Rol</th><th>Estado</th><th></th></tr></thead>
      <tbody id="uBody"><tr><td colspan="6" class="empty">Cargando…</td></tr></tbody>
    </table></div></div>`;

  const roles = await api('GET', 'roles');
  $('#uRol').innerHTML = roles.map(r => `<option value="${r.id}">${esc(r.nombre)}</option>`).join('');

  const cargar = async () => {
    const lista = await api('GET', 'usuarios');
    $('#uBody').innerHTML = lista.map(u => `
      <tr><td>${u.id}</td><td>${esc(u.username)}</td><td>${esc(u.nombre)}</td>
      <td>${esc(u.rol ? u.rol.nombre : '')}</td>
      <td><span class="tag ${u.estado ? 'ok' : 'off'}">${u.estado ? 'Activo' : 'Inactivo'}</span></td>
      <td>${u.estado ? `<button class="link-btn" data-del="${u.id}">Desactivar</button>` : ''}</td></tr>`).join('');
    $('#uBody').querySelectorAll('[data-del]').forEach(b => b.onclick = async () => {
      if (!confirm('¿Desactivar este usuario?')) return;
      try { await api('DELETE', 'usuarios/' + b.dataset.del); toast('Usuario desactivado', 'ok'); cargar(); }
      catch (e) { toast(e.message, 'err'); }
    });
  };

  $('#uGuardar').onclick = async () => {
    const body = {
      username: $('#uUser').value.trim(), password: $('#uPass').value,
      nombre: $('#uNombre').value.trim(), idRol: Number($('#uRol').value)
    };
    if (!body.username || !body.password || !body.nombre) { toast('Complete todos los campos', 'err'); return; }
    try {
      await api('POST', 'usuarios', body);
      toast('Usuario creado', 'ok');
      $('#uUser').value = ''; $('#uPass').value = ''; $('#uNombre').value = '';
      cargar();
    } catch (e) { toast(e.message, 'err'); }
  };
  cargar();
};

// ============================================================
//  Navegación
// ============================================================
function navegar(vista) {
  document.querySelectorAll('.navlink').forEach(b => b.classList.toggle('active', b.dataset.view === vista));
  (vistas[vista] || vistas.dashboard)();
}

document.querySelectorAll('.navlink').forEach(btn => {
  btn.addEventListener('click', () => navegar(btn.dataset.view));
});

// Oculta la sección de usuarios si no es administrador
if (!esAdmin) {
  document.querySelectorAll('[data-admin="true"]').forEach(b => b.classList.add('hidden'));
}

// Datos del usuario en la barra lateral
document.getElementById('userName').textContent = usuario.nombre || usuario.username;
document.getElementById('userRole').textContent = usuario.rol || '';
document.getElementById('btnLogout').addEventListener('click', () => {
  sessionStorage.clear();
  location.href = 'login.html';
});

// Vista inicial
navegar('dashboard');
