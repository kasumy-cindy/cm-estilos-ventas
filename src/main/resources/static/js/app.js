const App = (() => {
  const API = '/api';

  const $ = (selector, root = document) =>
    root.querySelector(selector);

  const $$ = (selector, root = document) =>
    [...root.querySelectorAll(selector)];

  function token() {
    return localStorage.getItem('cm_token');
  }

  function user() {
    try {
      return JSON.parse(
        localStorage.getItem('cm_user') || '{}'
      );
    } catch {
      return {};
    }
  }

  function logout() {
    localStorage.removeItem('cm_token');
    localStorage.removeItem('cm_user');
    location.href = '/login.html';
  }

  function money(value) {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN'
    }).format(Number(value || 0));
  }

  function esc(value) {
    return String(value ?? '').replace(
      /[&<>'"]/g,
      c => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        "'": '&#39;',
        '"': '&quot;'
      }[c])
    );
  }

  function date(value) {
    return value
      ? new Date(value).toLocaleString('es-PE')
      : '-';
  }

  function showToast(message, error = false) {
    const old = $('.toast');

    if (old) {
      old.remove();
    }

    const element = document.createElement('div');

    element.className = 'toast';
    element.textContent = message;

    if (error) {
      element.style.background = '#b53636';
    }

    document.body.appendChild(element);

    setTimeout(() => element.remove(), 3500);
  }

  function showError(message) {
    showToast(
      message || 'Ocurrió un error',
      true
    );
  }

  async function api(path, options = {}) {
    const headers = {
      ...(options.headers || {})
    };

    if (
      options.body &&
      typeof options.body !== 'string'
    ) {
      headers['Content-Type'] =
        'application/json';

      options.body =
        JSON.stringify(options.body);
    }

    if (token()) {
      headers.Authorization =
        `Bearer ${token()}`;
    }

    const response = await fetch(
      API + path,
      {
        ...options,
        headers
      }
    );

    const text = await response.text();

    let data = null;

    try {
      data = text ? JSON.parse(text) : null;
    } catch {
      data = text;
    }

    if (
      response.status === 401 &&
      document.body.dataset.page !== 'login'
    ) {
      logout();
      return;
    }

    if (!response.ok) {
      throw new Error(
        data?.message ||
        data?.error ||
        'No se pudo completar la operación'
      );
    }

    return data;
  }

  function guard() {
    if (!token()) {
      location.href = '/login.html';
      return false;
    }

    return true;
  }

  function setupLayout() {
    const current =
      document.body.dataset.page;

    const currentUser = user();

    const name = $('#currentUser');
    const role = $('#currentRole');
    const avatar = $('#avatar');

    if (name) {
      name.textContent =
        currentUser.username || 'Usuario';
    }

    if (role) {
      role.textContent =
        currentUser.rol || '';
    }

    if (avatar) {
      avatar.textContent =
        (
          currentUser.username || 'U'
        )
          .charAt(0)
          .toUpperCase();
    }

    $$('.nav-link').forEach(link => {
      if (link.dataset.page === current) {
        link.classList.add('active');
      }
    });

    const logoutButton =
      $('#logoutBtn');

    if (logoutButton) {
      logoutButton.addEventListener(
        'click',
        logout
      );
    }

    const menu =
      $('#mobileMenu');

    if (menu) {
      menu.addEventListener(
        'click',
        () => document.body.classList.toggle(
          'menu-open'
        )
      );
    }
  }

  function fillSelect(
    select,
    values,
    valueKey,
    labelFunction,
    placeholder = 'Selecciona...'
  ) {
    if (!select) {
      return;
    }

    select.innerHTML =
      `<option value="">${placeholder}</option>`;

    values.forEach(value => {
      const option =
        document.createElement('option');

      option.value = value[valueKey];
      option.textContent =
        labelFunction(value);

      select.appendChild(option);
    });
  }

  function formObject(form) {
    const data = {};

    new FormData(form).forEach(
      (value, key) => {
        data[key] = value;
      }
    );

    $$(
      'input[type="checkbox"]',
      form
    ).forEach(checkbox => {
      data[checkbox.name] =
        checkbox.checked;
    });

    return data;
  }

  function emptyRow(
    columns,
    text = 'No hay registros'
  ) {
    return `
      <tr>
        <td colspan="${columns}" class="empty">
          ${text}
        </td>
      </tr>
    `;
  }

  async function loginPage() {
    if (token()) {
      location.href =
        '/dashboard.html';

      return;
    }

    const form =
      $('#loginForm');

    if (!form) {
      return;
    }

    form.addEventListener(
      'submit',
      async event => {
        event.preventDefault();

        const button =
          $('button', form);

        button.disabled = true;

        try {
          const data = await api(
            '/auth/login',
            {
              method: 'POST',
              body: formObject(form)
            }
          );

          localStorage.setItem(
            'cm_token',
            data.token
          );

          localStorage.setItem(
            'cm_user',
            JSON.stringify(data)
          );

          location.href =
            '/dashboard.html';

        } catch (error) {
          showError(error.message);
          button.disabled = false;
        }
      }
    );
  }

  async function dashboardPage() {
    const [
      sales,
      stock
    ] = await Promise.all([
      api('/ventas'),
      api('/inventario')
    ]);

    $('#statSales').textContent =
      sales.length;

    $('#statRevenue').textContent =
      money(
        sales.reduce(
          (sum, sale) =>
            sum + Number(
              sale.montoTotal || 0
            ),
          0
        )
      );

    const critical =
      stock.filter(
        variant =>
          variant.stockActual <=
          variant.stockCritico
      );

    $('#statCritical').textContent =
      critical.length;

    $('#statStock').textContent =
      stock.reduce(
        (sum, variant) =>
          sum + Number(
            variant.stockActual || 0
          ),
        0
      );

    $('#criticalBody').innerHTML =
      critical.length
        ? critical.map(variant => `
            <tr>
              <td>${esc(variant.sku)}</td>
              <td>${esc(variant.productoNombre)}</td>
              <td>
                ${esc(variant.talla)}
                /
                ${esc(variant.color)}
              </td>
              <td>${variant.stockActual}</td>
              <td>
                <span class="badge badge-red">
                  Crítico
                </span>
              </td>
            </tr>
          `).join('')
        : emptyRow(
            5,
            'No existen variantes con stock crítico'
          );

    const days = {};

    sales.forEach(sale => {
      const key =
        new Date(
          sale.fechaHora
        ).toLocaleDateString(
          'es-PE',
          {
            day: '2-digit',
            month: '2-digit'
          }
        );

      days[key] =
        (days[key] || 0) +
        Number(sale.montoTotal || 0);
    });

    const entries =
      Object.entries(days).slice(-7);

    const max =
      Math.max(
        ...entries.map(item => item[1]),
        1
      );

    $('#chartBars').innerHTML =
      entries.length
        ? entries.map(
            ([label, value]) => `
              <div
                class="bar"
                style="height:${
                  Math.max(
                    8,
                    value / max * 100
                  )
                }%">
                <small>
                  ${money(value)}
                </small>
              </div>
            `
          ).join('')
        : `
          <span class="empty">
            Sin ventas registradas
          </span>
        `;

    $('#chartLabels').innerHTML =
      entries.map(
        item => `<span>${item[0]}</span>`
      ).join('');
  }

  async function categoriesPage() {
    let editing = null;

    const form =
      $('#categoryForm');

    async function load() {
      const rows =
        await api('/categorias');

      $('#categoryBody').innerHTML =
        rows.length
          ? rows.map(category => `
              <tr>
                <td>
                  ${category.idCategoria}
                </td>
                <td>
                  ${esc(category.nombre)}
                </td>
                <td class="actions">
                  <button
                    class="btn btn-light btn-sm edit-cat"
                    data-id="${category.idCategoria}"
                    data-name="${esc(category.nombre)}">
                    Editar
                  </button>

                  <button
                    class="btn btn-danger btn-sm delete-cat"
                    data-id="${category.idCategoria}">
                    Eliminar
                  </button>
                </td>
              </tr>
            `).join('')
          : emptyRow(3);

      $$('.edit-cat').forEach(button => {
        button.onclick = () => {
          editing =
            button.dataset.id;

          $('#categoryName').value =
            button.dataset.name;

          $('#categorySubmit')
            .textContent = 'Actualizar';
        };
      });

      $$('.delete-cat').forEach(button => {
        button.onclick = async () => {
          if (!confirm(
            '¿Eliminar esta categoría?'
          )) {
            return;
          }

          try {
            await api(
              '/categorias/' +
              button.dataset.id,
              {
                method: 'DELETE'
              }
            );

            showToast(
              'Categoría eliminada'
            );

            load();

          } catch (error) {
            showError(error.message);
          }
        };
      });
    }

    form.onsubmit = async event => {
      event.preventDefault();

      try {
        await api(
          editing
            ? '/categorias/' + editing
            : '/categorias',
          {
            method: editing
              ? 'PUT'
              : 'POST',
            body: formObject(form)
          }
        );

        showToast(
          editing
            ? 'Categoría actualizada'
            : 'Categoría creada'
        );

        editing = null;
        form.reset();

        $('#categorySubmit')
          .textContent = 'Guardar';

        load();

      } catch (error) {
        showError(error.message);
      }
    };

    $('#categoryCancel').onclick = () => {
      editing = null;
      form.reset();

      $('#categorySubmit')
        .textContent = 'Guardar';
    };

    await load();
  }

  async function productsPage() {
    let products = [];
    let categories = [];
    let editing = null;

    const form =
      $('#productForm');

    if (!$('#productImage')) {
      const imageField =
        document.createElement('div');

      imageField.className =
        'field';

      imageField.innerHTML = `
        <label>Imagen (URL o ruta)</label>
        <input
          id="productImage"
          name="imagenUrl"
          maxlength="500"
          placeholder="/images/blusa.svg">
      `;

      form
        .querySelector('.form-grid')
        .insertBefore(
          imageField,
          form.querySelector('.check')
        );
    }

    categories =
      await api('/categorias');

    fillSelect(
      $('#productCategory'),
      categories,
      'idCategoria',
      category => category.nombre
    );

    async function load() {
      products =
        await api('/productos');

      $('#productBody').innerHTML =
        products.length
          ? products.map(product => `
              <tr>
                <td>${product.idProducto}</td>
                <td>
                  <strong>
                    ${esc(product.sku)}
                  </strong>
                </td>
                <td>
                  ${esc(product.nombre)}
                </td>
                <td>
                  ${esc(product.categoriaNombre)}
                </td>
                <td>
                  ${money(product.precioVenta)}
                </td>
                <td>
                  <span class="badge ${
                    product.activo
                      ? 'badge-green'
                      : 'badge-gray'
                  }">
                    ${
                      product.activo
                        ? 'Activo'
                        : 'Descontinuado'
                    }
                  </span>
                </td>
                <td class="actions">
                  <button
                    class="btn btn-light btn-sm edit-product"
                    data-id="${product.idProducto}">
                    Editar
                  </button>

                  <button
                    class="btn ${
                      product.activo
                        ? 'btn-danger'
                        : 'btn-success'
                    } btn-sm state-product"
                    data-id="${product.idProducto}"
                    data-active="${product.activo}">
                    ${
                      product.activo
                        ? 'Descontinuar'
                        : 'Activar'
                    }
                  </button>

                  <button
                    class="btn btn-danger btn-sm delete-product"
                    data-id="${product.idProducto}">
                    Borrar
                  </button>
                </td>
              </tr>
            `).join('')
          : emptyRow(7);

      $$('.edit-product').forEach(button => {
        button.onclick = () => {
          const product =
            products.find(
              item =>
                item.idProducto ==
                button.dataset.id
            );

          editing =
            product.idProducto;

          $('#productCategory').value =
            product.categoriaId;

          $('#productSku').value =
            product.sku;

          $('#productName').value =
            product.nombre;

          $('#productPrice').value =
            product.precioVenta;

          $('#productImage').value =
            product.imagenUrl || '';

          $('#productActive').checked =
            product.activo;

          $('#productSubmit')
            .textContent = 'Actualizar';

          window.scrollTo({
            top: 0,
            behavior: 'smooth'
          });
        };
      });

      $$('.state-product').forEach(button => {
        button.onclick = async () => {
          try {
            await api(
              `/productos/${
                button.dataset.id
              }/estado?activo=${
                button.dataset.active !== 'true'
              }`,
              {
                method: 'PATCH'
              }
            );

            showToast(
              'Estado actualizado'
            );

            load();

          } catch (error) {
            showError(error.message);
          }
        };
      });

      $$('.delete-product').forEach(button => {
        button.onclick = async () => {
          if (!confirm(
            '¿Borrar producto? Solo será posible si no tiene dependencias.'
          )) {
            return;
          }

          try {
            await api(
              '/productos/' +
              button.dataset.id,
              {
                method: 'DELETE'
              }
            );

            showToast(
              'Producto borrado'
            );

            load();

          } catch (error) {
            showError(error.message);
          }
        };
      });
    }

    form.onsubmit = async event => {
      event.preventDefault();

      const data =
        formObject(form);

      data.categoriaId =
        Number(data.categoriaId);

      data.precioVenta =
        Number(data.precioVenta);

      try {
        await api(
          editing
            ? '/productos/' + editing
            : '/productos',
          {
            method: editing
              ? 'PUT'
              : 'POST',
            body: data
          }
        );

        showToast(
          editing
            ? 'Producto actualizado'
            : 'Producto creado'
        );

        editing = null;
        form.reset();

        $('#productActive').checked =
          true;

        $('#productSubmit')
          .textContent = 'Guardar';

        load();

      } catch (error) {
        showError(error.message);
      }
    };

    $('#productCancel').onclick = () => {
      editing = null;
      form.reset();

      $('#productActive').checked =
        true;

      $('#productSubmit')
        .textContent = 'Guardar';
    };

    await load();
  }

  async function reportsPage() {
    async function load() {
      const query =
        new URLSearchParams();

      if ($('#fromDate').value) {
        query.set(
          'desde',
          $('#fromDate').value
        );
      }

      if ($('#toDate').value) {
        query.set(
          'hasta',
          $('#toDate').value
        );
      }

      const [
        resumen,
        ventas
      ] = await Promise.all([
        api(
          '/reportes/resumen?' +
          query.toString()
        ),
        api(
          '/reportes/ventas?' +
          query.toString()
        )
      ]);

      $('#reportSales').textContent =
        resumen.cantidadVentas;

      $('#reportPhysical').textContent =
        resumen.ventasFisicas;

      $('#reportOnline').textContent =
        resumen.ventasOnline;

      $('#reportCritical').textContent =
        resumen.variantesStockCritico;

      $('#reportSubtotal').textContent =
        money(resumen.subtotal);

      $('#reportTax').textContent =
        money(resumen.impuesto);

      $('#reportTotal').textContent =
        money(resumen.total);

      $('#reportBody').innerHTML =
        ventas.length
          ? ventas.map(venta => `
              <tr>
                <td>#${venta.idVenta}</td>
                <td>${date(venta.fechaHora)}</td>
                <td>${esc(venta.cliente)}</td>
                <td>${esc(venta.tipoVenta)}</td>
                <td>
                  ${esc(
                    venta.metodoPago || '-'
                  )}
                </td>
                <td>
                  ${money(venta.subtotal)}
                </td>
                <td>
                  ${money(venta.montoTotal)}
                </td>
              </tr>
            `).join('')
          : emptyRow(
              7,
              'No hay ventas en el periodo'
            );
    }

    $('#reportFilter').onsubmit =
      event => {
        event.preventDefault();

        load().catch(error => {
          showError(error.message);
        });
      };

    $('#clearReport').onclick =
      () => {
        $('#reportFilter').reset();

        load().catch(error => {
          showError(error.message);
        });
      };

    $('#downloadPdf').onclick =
      async () => {
        try {
          const query =
            new URLSearchParams();

          if ($('#fromDate').value) {
            query.set(
              'desde',
              $('#fromDate').value
            );
          }

          if ($('#toDate').value) {
            query.set(
              'hasta',
              $('#toDate').value
            );
          }

          const response =
            await fetch(
              '/api/reportes/pdf?' +
              query.toString(),
              {
                method: 'GET',
                headers: {
                  Authorization:
                    'Bearer ' +
                    localStorage.getItem(
                      'cm_token'
                    )
                }
              }
            );

          if (!response.ok) {
            throw new Error(
              'No se pudo generar el PDF'
            );
          }

          const file =
            await response.blob();

          const url =
            window.URL.createObjectURL(file);

          const link =
            document.createElement('a');

          link.href = url;
          link.download =
            'reporte_ventas.pdf';

          document.body.appendChild(link);
          link.click();
          link.remove();

          window.URL.revokeObjectURL(url);

        } catch (error) {
          showError(error.message);
        }
      };

    await load();
  }

  async function start() {
    const page =
      document.body.dataset.page;

    if (page === 'login') {
      return loginPage();
    }

    if (!guard()) {
      return;
    }

    setupLayout();

    try {
      const pages = {
        dashboard: dashboardPage,
        categorias: categoriesPage,
        productos: productsPage,
        reportes: reportsPage
      };

      if (pages[page]) {
        await pages[page]();
      }

    } catch (error) {
      showError(error.message);
    }
  }

  return {
    api,
    money,
    esc,
    logout,
    start
  };
})();

document.addEventListener(
  'DOMContentLoaded',
  App.start
);
function obtenerTokenOnline() {
    return localStorage.getItem('cm_token');
}

function escaparHtmlOnline(valor) {
    if (valor === null || valor === undefined) {
        return '';
    }

    return String(valor)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

function mostrarMensajeOnline(mensaje, tipo) {
    const elemento = document.getElementById('onlineOrdersMessage');

    if (!elemento) {
        return;
    }

    elemento.textContent = mensaje;
    elemento.className = 'alert alert-' + tipo;
}

function formatoDineroOnline(valor) {
    const numero = Number(valor || 0);

    return 'S/ ' + numero.toFixed(2);
}

function formatoFechaOnline(fecha) {
    if (!fecha) {
        return '-';
    }

    const fechaConvertida = new Date(fecha);

    if (Number.isNaN(fechaConvertida.getTime())) {
        return fecha;
    }

    return fechaConvertida.toLocaleString('es-PE');
}

function colorEstadoOnline(estado) {
    if (!estado) {
        return '';
    }

    return estado.toLowerCase();
}

async function cargarPedidosOnline() {

    const cuerpo =
        document.getElementById('onlineReportBody');

    const filtro =
        document.getElementById('onlineStatusFilter');

    if (!cuerpo) {
        return;
    }

    cuerpo.innerHTML = `
        <tr>
            <td colspan="8" class="empty">
                Cargando pedidos online...
            </td>
        </tr>
    `;

    try {
        const estado = filtro ? filtro.value : '';

        let url =
            '/api/reportes/ventas-online';

        if (estado) {
            url += '?estado='
                + encodeURIComponent(estado);
        }

        const respuesta = await fetch(url, {
            method: 'GET',
            headers: {
                'Authorization':
                    'Bearer ' + obtenerTokenOnline(),
                'Accept': 'application/json'
            }
        });

        if (respuesta.status === 401
                || respuesta.status === 403) {

            throw new Error(
                'No tienes permiso para consultar los pedidos online'
            );
        }

        if (!respuesta.ok) {
            throw new Error(
                'No se pudieron cargar los pedidos online'
            );
        }

        const pedidos = await respuesta.json();

        if (!pedidos || pedidos.length === 0) {

            cuerpo.innerHTML = `
                <tr>
                    <td colspan="8" class="empty">
                        No hay pedidos online para este filtro.
                    </td>
                </tr>
            `;

            return;
        }

        cuerpo.innerHTML = pedidos.map(pedido => {

            const estadoPedido =
                pedido.estadoPedido || 'PENDIENTE';

            const estadoPago =
                pedido.estadoPago || 'PENDIENTE';

            return `
                <tr>
                    <td>
                        #${escaparHtmlOnline(pedido.idVenta)}
                    </td>

                    <td>
                        ${escaparHtmlOnline(pedido.cliente)}
                    </td>

                    <td>
                        ${escaparHtmlOnline(
                            formatoFechaOnline(pedido.fechaHora)
                        )}
                    </td>

                    <td>
                        ${escaparHtmlOnline(
                            formatoDineroOnline(pedido.montoTotal)
                        )}
                    </td>

                    <td>
                        ${escaparHtmlOnline(
                            pedido.metodoPago || '-'
                        )}
                    </td>

                    <td>
                        <span class="badge">
                            ${escaparHtmlOnline(estadoPago)}
                        </span>
                    </td>

                    <td>
                        <span class="badge estado-${escaparHtmlOnline(
                            colorEstadoOnline(estadoPedido)
                        )}">
                            ${escaparHtmlOnline(estadoPedido)}
                        </span>
                    </td>

                    <td>
                        <select
                            class="online-state-select"
                            data-id="${escaparHtmlOnline(
                                pedido.idVenta
                            )}">

                            <option value="PENDIENTE"
                                ${estadoPedido === 'PENDIENTE'
                                    ? 'selected'
                                    : ''}>
                                Pendiente
                            </option>

                            <option value="CONFIRMADO"
                                ${estadoPedido === 'CONFIRMADO'
                                    ? 'selected'
                                    : ''}>
                                Confirmado
                            </option>

                            <option value="PREPARANDO"
                                ${estadoPedido === 'PREPARANDO'
                                    ? 'selected'
                                    : ''}>
                                Preparando
                            </option>

                            <option value="ENVIADO"
                                ${estadoPedido === 'ENVIADO'
                                    ? 'selected'
                                    : ''}>
                                Enviado
                            </option>

                            <option value="ENTREGADO"
                                ${estadoPedido === 'ENTREGADO'
                                    ? 'selected'
                                    : ''}>
                                Entregado
                            </option>

                            <option value="CANCELADO"
                                ${estadoPedido === 'CANCELADO'
                                    ? 'selected'
                                    : ''}>
                                Cancelado
                            </option>

                        </select>

                        <button
                            type="button"
                            class="btn btn-primary btn-small"
                            data-action="update-online-state"
                            data-id="${escaparHtmlOnline(
                                pedido.idVenta
                            )}">
                            Guardar
                        </button>
                    </td>
                </tr>
            `;
        }).join('');

        configurarEventosPedidosOnline();

    } catch (error) {

        cuerpo.innerHTML = `
            <tr>
                <td colspan="8" class="empty">
                    ${escaparHtmlOnline(error.message)}
                </td>
            </tr>
        `;

        mostrarMensajeOnline(
            error.message,
            'danger'
        );
    }
}

function configurarEventosPedidosOnline() {

    const filtro =
        document.getElementById('onlineStatusFilter');

    if (filtro && !filtro.dataset.configurado) {

        filtro.addEventListener(
            'change',
            cargarPedidosOnline
        );

        filtro.dataset.configurado = 'true';
    }

    document
        .querySelectorAll(
            '[data-action="update-online-state"]'
        )
        .forEach(boton => {

            if (boton.dataset.configurado) {
                return;
            }

            boton.addEventListener(
                'click',
                actualizarEstadoPedidoOnline
            );

            boton.dataset.configurado = 'true';
        });
}

async function actualizarEstadoPedidoOnline(evento) {

    const boton = evento.currentTarget;

    const idVenta =
        boton.dataset.id;

    const selector =
        document.querySelector(
            `.online-state-select[data-id="${idVenta}"]`
        );

    if (!selector) {
        return;
    }

    const estado =
        selector.value;

    boton.disabled = true;
    boton.textContent = 'Guardando...';

    try {

        const respuesta = await fetch(
            `/api/ventas/${idVenta}/estado?estado=${encodeURIComponent(estado)}`,
            {
                method: 'PATCH',
                headers: {
                    'Authorization':
                        'Bearer ' + obtenerTokenOnline(),
                    'Accept': 'application/json'
                }
            }
        );

        if (respuesta.status === 401
                || respuesta.status === 403) {

            throw new Error(
                'No tienes permiso para actualizar el pedido'
            );
        }

        if (!respuesta.ok) {
            throw new Error(
                'No se pudo actualizar el estado del pedido'
            );
        }

        await respuesta.json();

        mostrarMensajeOnline(
            'El estado del pedido se actualizó correctamente.',
            'success'
        );

        await cargarPedidosOnline();

    } catch (error) {

        mostrarMensajeOnline(
            error.message,
            'danger'
        );

        boton.disabled = false;
        boton.textContent = 'Guardar';
    }
}

function iniciarPedidosOnline() {

    if (!document.body) {
        return;
    }

    if (
        document.body.dataset.page
        !== 'pedidos-online'
    ) {
        return;
    }

    cargarPedidosOnline();
}

document.addEventListener(
    'DOMContentLoaded',
    iniciarPedidosOnline
);
async function cargarProductosEnInventario() {
    const select = document.getElementById('variantProduct');

    if (!select) {
        return;
    }

    try {
        const token = localStorage.getItem('cm_token');

        const respuesta = await fetch(
            '/api/productos?todos=true',
            {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Accept': 'application/json'
                }
            }
        );

        if (!respuesta.ok) {
            throw new Error(
                'No se pudieron cargar los productos'
            );
        }

        const resultado = await respuesta.json();

        const productos = Array.isArray(resultado)
            ? resultado
            : resultado.content || [];

        select.innerHTML =
            '<option value="">Seleccione un producto</option>';

        productos.forEach(producto => {

            const id = producto.idProducto;
            const nombre = producto.nombre || 'Producto';
            const sku = producto.sku || '';

            const option =
                document.createElement('option');

            option.value = id;
            option.textContent =
                sku + ' - ' + nombre;

            select.appendChild(option);
        });

    } catch (error) {

        select.innerHTML =
            '<option value="">Error al cargar productos</option>';

        console.error(
            'Error cargando productos:',
            error
        );
    }
}

document.addEventListener(
    'DOMContentLoaded',
    function () {

        if (
            document.body.dataset.page
            === 'inventario'
        ) {
            cargarProductosEnInventario();
        }
    }
);