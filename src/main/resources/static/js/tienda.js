const state = { products: [], cart: JSON.parse(localStorage.getItem('cm-estilos-cart') || '[]') };

const money = value => `S/ ${Number(value || 0).toFixed(2)}`;
const saveCart = () => localStorage.setItem('cm-estilos-cart', JSON.stringify(state.cart));

function renderProducts() {
    const term = document.getElementById('searchInput').value.toLowerCase().trim();
    const products = state.products.filter(p => `${p.productoNombre} ${p.sku} ${p.color} ${p.talla}`.toLowerCase().includes(term));
    const grid = document.getElementById('productGrid');
    document.getElementById('catalogMessage').textContent = products.length ? '' : 'No encontramos productos disponibles.';
    grid.innerHTML = products.map(p => `<article class="product-card"><div class="product-art"><img src="${escapeHtml(p.imagenUrl || '/images/accesorios.svg')}" alt="${escapeHtml(p.productoNombre)}" onerror="this.src='/images/accesorios.svg'"></div><div class="product-info"><h3>${escapeHtml(p.productoNombre)}</h3><p>SKU: ${escapeHtml(p.sku)} · ${escapeHtml(p.talla)} · ${escapeHtml(p.color)}</p><p>${p.stockActual} unidad(es) disponibles</p><p class="price">${money(p.precioVenta)}</p><div class="rating" data-rating-product="${p.productoId}">★★★★★ Sin reseñas</div><button class="add-button" data-id="${p.idVariante}">Agregar al carrito</button><button class="review-button" data-review-id="${p.productoId}" data-review-name="${escapeHtml(p.productoNombre)}">Ver / dejar reseña</button></div></article>`).join('');
    grid.querySelectorAll('.add-button').forEach(button => button.addEventListener('click', () => addToCart(Number(button.dataset.id))));
    grid.querySelectorAll('.review-button').forEach(button => button.addEventListener('click', () => openReviews(Number(button.dataset.reviewId), button.dataset.reviewName)));
    [...new Set(products.map(p => p.productoId))].forEach(loadRating);
}

function renderCart() {
    const items = state.cart.map(item => ({ ...item, product: state.products.find(p => p.idVariante === item.varianteId) })).filter(item => item.product);
    state.cart = items.map(item => ({ varianteId: item.varianteId, cantidad: item.cantidad }));
    const count = state.cart.reduce((sum, item) => sum + item.cantidad, 0);
    document.getElementById('cartCount').textContent = count;
    document.getElementById('cartTotal').textContent = money(items.reduce((sum, item) => sum + Number(item.product.precioVenta) * item.cantidad, 0));
    document.getElementById('cartItems').innerHTML = items.length ? items.map(item => `<div class="cart-line"><div><strong>${escapeHtml(item.product.productoNombre)}</strong><small>${escapeHtml(item.product.talla)} · ${escapeHtml(item.product.color)}</small></div><div class="cart-controls"><button data-minus="${item.varianteId}">−</button><span>${item.cantidad}</span><button data-plus="${item.varianteId}">+</button></div></div>`).join('') : '<p class="muted">Aún no agregaste productos.</p>';
    document.querySelectorAll('[data-minus]').forEach(button => button.addEventListener('click', () => changeQuantity(Number(button.dataset.minus), -1)));
    document.querySelectorAll('[data-plus]').forEach(button => button.addEventListener('click', () => changeQuantity(Number(button.dataset.plus), 1)));
    saveCart();
}

function addToCart(varianteId) {
    const product = state.products.find(p => p.idVariante === varianteId);
    const item = state.cart.find(i => i.varianteId === varianteId);
    if (item) item.cantidad = Math.min(item.cantidad + 1, product.stockActual);
    else state.cart.push({ varianteId, cantidad: 1 });
    renderCart();
}

function changeQuantity(varianteId, change) {
    const item = state.cart.find(i => i.varianteId === varianteId);
    const product = state.products.find(p => p.idVariante === varianteId);
    if (!item || !product) return;
    item.cantidad += change;
    if (item.cantidad <= 0) state.cart = state.cart.filter(i => i.varianteId !== varianteId);
    else item.cantidad = Math.min(item.cantidad, product.stockActual);
    renderCart();
}

function escapeHtml(value) { return String(value ?? '').replace(/[&<>'"]/g, c => ({ '&':'&amp;', '<':'&lt;', '>':'&gt;', "'":'&#39;', '"':'&quot;' })[c]); }

function stars(value) { return '★'.repeat(Math.round(Number(value || 0))) + '☆'.repeat(5 - Math.round(Number(value || 0))); }

async function loadRating(productoId) {
    try {
        const response = await fetch(`/api/tienda/productos/${productoId}/resenas`);
        if (!response.ok) return;
        const summary = await response.json();
        document.querySelectorAll(`[data-rating-product="${productoId}"]`).forEach(element => {
            element.textContent = summary.cantidad ? `${stars(summary.promedio)} ${summary.promedio} (${summary.cantidad})` : '★★★★★ Sin reseñas';
        });
    } catch (error) { /* El catálogo sigue funcionando si las reseñas no responden. */ }
}

async function openReviews(productoId, productoNombre) {
    document.getElementById('reviewProductId').value = productoId;
    document.getElementById('reviewTitle').textContent = `Reseñas: ${productoNombre}`;
    document.getElementById('reviewMessage').textContent = '';
    document.getElementById('reviewModal').hidden = false;
    const list = document.getElementById('reviewList');
    list.innerHTML = '<p class="muted">Cargando reseñas...</p>';
    try {
        const response = await fetch(`/api/tienda/productos/${productoId}/resenas`);
        const summary = await response.json();
        document.getElementById('reviewSummary').textContent = summary.cantidad
            ? `${stars(summary.promedio)} ${summary.promedio}/5 · ${summary.cantidad} opinión(es)`
            : 'Este producto todavía no tiene reseñas. Sé el primero en opinar.';
        list.innerHTML = summary.resenas.length ? summary.resenas.map(r => `<div class="review-item"><strong>${escapeHtml(r.nombreCliente)}</strong><div class="rating">${stars(r.puntuacion)}</div><p>${escapeHtml(r.comentario)}</p></div>`).join('') : '<p class="muted">Todavía no hay opiniones.</p>';
    } catch (error) { list.innerHTML = '<p class="muted">No se pudieron cargar las reseñas.</p>'; }
}

async function loadStore() {
    try {
        const [catalogResponse, paymentResponse] = await Promise.all([fetch('/api/catalogo?soloDisponibles=true'), fetch('/api/tienda/metodos-pago')]);
        if (!catalogResponse.ok) throw new Error('No se pudo cargar el catálogo');
        state.products = await catalogResponse.json();
        renderProducts(); renderCart();
        if (paymentResponse.ok) {
            const methods = await paymentResponse.json();
            document.getElementById('paymentMethod').insertAdjacentHTML('beforeend', methods.map(m => `<option value="${m.idMetodoPago}">${escapeHtml(m.nombre)}</option>`).join(''));
        }
    } catch (error) { document.getElementById('catalogMessage').textContent = error.message; }
}

document.getElementById('searchInput').addEventListener('input', renderProducts);
document.getElementById('cartButton').addEventListener('click', () => document.getElementById('checkout').scrollIntoView({ behavior: 'smooth' }));
document.getElementById('closeReview').addEventListener('click', () => document.getElementById('reviewModal').hidden = true);
document.getElementById('reviewModal').addEventListener('click', event => { if (event.target.id === 'reviewModal') event.currentTarget.hidden = true; });
document.getElementById('reviewForm').addEventListener('submit', async event => {
    event.preventDefault();
    const productId = document.getElementById('reviewProductId').value;
    const message = document.getElementById('reviewMessage');
    message.textContent = 'Publicando reseña...';
    const body = { nombreCliente: document.getElementById('reviewName').value, correo: document.getElementById('reviewEmail').value || null, puntuacion: Number(document.getElementById('reviewRating').value), comentario: document.getElementById('reviewComment').value };
    try {
        const response = await fetch(`/api/tienda/productos/${productId}/resenas`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
        const result = await response.json();
        if (!response.ok) throw new Error(result.message || 'No se pudo publicar la reseña');
        event.target.reset();
        message.textContent = '¡Gracias! Tu opinión fue publicada.';
        await openReviews(Number(productId), document.getElementById('reviewTitle').textContent.replace('Reseñas: ', ''));
        loadRating(Number(productId));
    } catch (error) { message.textContent = error.message; }
});
document.getElementById('checkoutForm').addEventListener('submit', async event => {
    event.preventDefault();
    if (!state.cart.length) { document.getElementById('checkoutMessage').textContent = 'Agrega al menos un producto.'; return; }
    const form = new FormData(event.target);
    const payload = Object.fromEntries(form.entries());
    payload.metodoPagoId = payload.metodoPagoId ? Number(payload.metodoPagoId) : null;
    payload.items = state.cart;
    const message = document.getElementById('checkoutMessage');
    message.textContent = 'Registrando pedido...';
    try {
        const response = await fetch('/api/tienda/checkout', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
        const result = await response.json();
        if (!response.ok) throw new Error(result.message || 'No se pudo registrar el pedido');
        state.cart = []; saveCart(); renderCart(); event.target.reset();
        message.textContent = `Pedido #${result.idVenta} registrado. Total: ${money(result.montoTotal)}. ¡Gracias por tu compra!`;
    } catch (error) { message.textContent = error.message; }
});

loadStore();
