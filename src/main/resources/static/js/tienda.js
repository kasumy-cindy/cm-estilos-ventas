const state = {
    products: [],
    cart: JSON.parse(
        localStorage.getItem("cm-estilos-cart") || "[]"
    )
};

const money = value =>
    `S/ ${Number(value || 0).toFixed(2)}`;

const saveCart = () => {
    localStorage.setItem(
        "cm-estilos-cart",
        JSON.stringify(state.cart)
    );
};

function escapeHtml(value) {
    return String(value ?? "").replace(
        /[&<>'"]/g,
        character => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            "'": "&#39;",
            '"': "&quot;"
        })[character]
    );
}

function stars(value) {
    const rating = Math.max(
        0,
        Math.min(5, Math.round(Number(value || 0)))
    );

    return "★".repeat(rating)
        + "☆".repeat(5 - rating);
}

function renderProducts() {
    const term = document
        .getElementById("searchInput")
        .value
        .toLowerCase()
        .trim();

    const products = state.products.filter(product =>
        `${product.productoNombre}
         ${product.sku}
         ${product.color}
         ${product.talla}`
            .toLowerCase()
            .includes(term)
    );

    const grid =
        document.getElementById("productGrid");

    document.getElementById("catalogMessage")
        .textContent = products.length
            ? ""
            : "No encontramos productos disponibles.";

    grid.innerHTML = products.map(product => `
        <article class="product-card">

            <div class="product-art">
                <img
                    src="${escapeHtml(
                        product.imagenUrl ||
                        "/images/accesorios.svg"
                    )}"
                    alt="${escapeHtml(
                        product.productoNombre
                    )}"
                    onerror="this.src='/images/accesorios.svg'">
            </div>

            <div class="product-info">
                <h3>
                    ${escapeHtml(product.productoNombre)}
                </h3>

                <p>
                    SKU: ${escapeHtml(product.sku)}
                    · ${escapeHtml(product.talla)}
                    · ${escapeHtml(product.color)}
                </p>

                <p>
                    ${product.stockActual}
                    unidad(es) disponibles
                </p>

                <p class="price">
                    ${money(product.precioVenta)}
                </p>

                <div
                    class="rating"
                    data-rating-product="${product.productoId}">
                    ★★★★★ Sin reseñas
                </div>

                <button
                    class="add-button"
                    type="button"
                    data-id="${product.idVariante}">
                    Agregar al carrito
                </button>

                <button
                    class="review-button"
                    type="button"
                    data-review-id="${product.productoId}"
                    data-review-name="${escapeHtml(
                        product.productoNombre
                    )}">
                    Ver / dejar reseña
                </button>
            </div>
        </article>
    `).join("");

    grid.querySelectorAll(".add-button")
        .forEach(button => {
            button.addEventListener("click", () => {
                addToCart(Number(button.dataset.id));
            });
        });

    grid.querySelectorAll(".review-button")
        .forEach(button => {
            button.addEventListener("click", () => {
                openReviews(
                    Number(button.dataset.reviewId),
                    button.dataset.reviewName
                );
            });
        });

    [
        ...new Set(
            products.map(product => product.productoId)
        )
    ].forEach(loadRating);
}

function renderCart() {
    const items = state.cart
        .map(item => ({
            ...item,
            product: state.products.find(
                product =>
                    product.idVariante === item.varianteId
            )
        }))
        .filter(item => item.product);

    state.cart = items.map(item => ({
        varianteId: item.varianteId,
        cantidad: item.cantidad
    }));

    const count = state.cart.reduce(
        (total, item) => total + item.cantidad,
        0
    );

    const total = items.reduce(
        (sum, item) =>
            sum
            + Number(item.product.precioVenta)
            * item.cantidad,
        0
    );

    document.getElementById("cartCount")
        .textContent = count;

    document.getElementById("cartTotal")
        .textContent = money(total);

    document.getElementById("cartItems")
        .innerHTML = items.length
            ? items.map(item => `
                <div class="cart-line">

                    <div>
                        <strong>
                            ${escapeHtml(
                                item.product.productoNombre
                            )}
                        </strong>

                        <small>
                            ${escapeHtml(item.product.talla)}
                            ·
                            ${escapeHtml(item.product.color)}
                        </small>
                    </div>

                    <div class="cart-controls">
                        <button
                            type="button"
                            data-minus="${item.varianteId}">
                            −
                        </button>

                        <span>${item.cantidad}</span>

                        <button
                            type="button"
                            data-plus="${item.varianteId}">
                            +
                        </button>
                    </div>
                </div>
            `).join("")
            : `
                <p class="muted">
                    Aún no agregaste productos.
                </p>
            `;

    document.querySelectorAll("[data-minus]")
        .forEach(button => {
            button.addEventListener("click", () => {
                changeQuantity(
                    Number(button.dataset.minus),
                    -1
                );
            });
        });

    document.querySelectorAll("[data-plus]")
        .forEach(button => {
            button.addEventListener("click", () => {
                changeQuantity(
                    Number(button.dataset.plus),
                    1
                );
            });
        });

    saveCart();
}

function addToCart(varianteId) {
    const product = state.products.find(
        item => item.idVariante === varianteId
    );

    if (!product || product.stockActual <= 0) {
        return;
    }

    const item = state.cart.find(
        current => current.varianteId === varianteId
    );

    if (item) {
        item.cantidad = Math.min(
            item.cantidad + 1,
            product.stockActual
        );
    } else {
        state.cart.push({
            varianteId,
            cantidad: 1
        });
    }

    renderCart();
}

function changeQuantity(varianteId, change) {
    const item = state.cart.find(
        current => current.varianteId === varianteId
    );

    const product = state.products.find(
        current => current.idVariante === varianteId
    );

    if (!item || !product) {
        return;
    }

    item.cantidad += change;

    if (item.cantidad <= 0) {
        state.cart = state.cart.filter(
            current => current.varianteId !== varianteId
        );
    } else {
        item.cantidad = Math.min(
            item.cantidad,
            product.stockActual
        );
    }

    renderCart();
}

async function loadRating(productoId) {
    try {
        const response = await fetch(
            `/api/tienda/productos/${productoId}/resenas`
        );

        if (!response.ok) {
            return;
        }

        const summary = await response.json();

        document.querySelectorAll(
            `[data-rating-product="${productoId}"]`
        ).forEach(element => {
            element.textContent = summary.cantidad
                ? `${stars(summary.promedio)}
                   ${summary.promedio}
                   (${summary.cantidad})`
                : "★★★★★ Sin reseñas";
        });

    } catch (error) {
        console.error("Error cargando reseñas", error);
    }
}

async function openReviews(productoId, productoNombre) {
    const modal =
        document.getElementById("reviewModal");

    const list =
        document.getElementById("reviewList");

    document.getElementById("reviewProductId")
        .value = productoId;

    document.getElementById("reviewTitle")
        .textContent = `Reseñas: ${productoNombre}`;

    modal.hidden = false;

    list.innerHTML = `
        <p class="muted">
            Cargando reseñas...
        </p>
    `;

    try {
        const response = await fetch(
            `/api/tienda/productos/${productoId}/resenas`
        );

        const summary = await response.json();

        document.getElementById("reviewSummary")
            .textContent = summary.cantidad
                ? `${stars(summary.promedio)}
                   ${summary.promedio}/5 ·
                   ${summary.cantidad} opinión(es)`
                : "Este producto todavía no tiene reseñas.";

        list.innerHTML = summary.resenas.length
            ? summary.resenas.map(review => `
                <div class="review-item">
                    <strong>
                        ${escapeHtml(
                            review.nombreCliente
                        )}
                    </strong>

                    <div class="rating">
                        ${stars(review.puntuacion)}
                    </div>

                    <p>
                        ${escapeHtml(
                            review.comentario
                        )}
                    </p>
                </div>
            `).join("")
            : `
                <p class="muted">
                    Todavía no hay opiniones.
                </p>
            `;

    } catch (error) {
        list.innerHTML = `
            <p class="muted">
                No se pudieron cargar las reseñas.
            </p>
        `;
    }
}

function updateDeliveryFields() {
    const deliveryMode =
        document.getElementById("deliveryMode");

    const addressField =
        document.getElementById(
            "deliveryAddressField"
        );

    const addressInput =
        document.getElementById(
            "deliveryAddress"
        );

    const isDelivery =
        deliveryMode.value === "DELIVERY";

    addressField.hidden = !isDelivery;
    addressInput.required = isDelivery;

    if (!isDelivery) {
        addressInput.value = "";
    }
}

function configureDelivery() {
    const deliveryMode =
        document.getElementById("deliveryMode");

    deliveryMode.addEventListener(
        "change",
        updateDeliveryFields
    );

    updateDeliveryFields();
}

async function loadStore() {
    try {
        const [
            catalogResponse,
            paymentResponse
        ] = await Promise.all([
            fetch(
                "/api/catalogo?soloDisponibles=true"
            ),
            fetch("/api/tienda/metodos-pago")
        ]);

        if (!catalogResponse.ok) {
            throw new Error(
                "No se pudo cargar el catálogo"
            );
        }

        state.products =
            await catalogResponse.json();

        renderProducts();
        renderCart();

        if (paymentResponse.ok) {
            const methods =
                await paymentResponse.json();

            document.getElementById(
                "paymentMethod"
            ).insertAdjacentHTML(
                "beforeend",
                methods.map(method => `
                    <option value="${method.idMetodoPago}">
                        ${escapeHtml(method.nombre)}
                    </option>
                `).join("")
            );
        }

    } catch (error) {
        document.getElementById(
            "catalogMessage"
        ).textContent = error.message;
    }
}

async function submitCheckout(event) {
    event.preventDefault();

    const message =
        document.getElementById(
            "checkoutMessage"
        );

    if (!state.cart.length) {
        message.textContent =
            "Agrega al menos un producto.";
        return;
    }

    const formData =
        new FormData(event.target);

    const payload =
        Object.fromEntries(formData.entries());

    payload.metodoPagoId =
        payload.metodoPagoId
            ? Number(payload.metodoPagoId)
            : null;

    payload.items = state.cart;

    try {
        message.textContent =
            "Registrando pedido...";

        const response = await fetch(
            "/api/tienda/checkout",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            }
        );

        const result = await response.json();

        if (!response.ok) {
            throw new Error(
                result.message ||
                "No se pudo registrar el pedido"
            );
        }

        state.cart = [];
        saveCart();
        renderCart();

        event.target.reset();

        document.getElementById(
            "deliveryMode"
        ).dispatchEvent(
            new Event("change")
        );

        message.innerHTML = `
            Pedido <strong>#${result.idVenta}</strong>
            registrado correctamente.<br>
            Total: <strong>
                ${money(result.montoTotal)}
            </strong>
        `;

        document.getElementById(
            "trackingOrderId"
        ).value = result.idVenta;

        document.getElementById(
            "trackingEmail"
        ).value = payload.correo;

    } catch (error) {
        message.textContent =
            error.message;
    }
}

async function consultarPedido(event) {
    event.preventDefault();

    const id =
        document.getElementById(
            "trackingOrderId"
        ).value;

    const correo =
        document.getElementById(
            "trackingEmail"
        ).value;

    const result =
        document.getElementById(
            "trackingResult"
        );

    try {
        result.textContent =
            "Consultando pedido...";

        const response = await fetch(
            `/api/tienda/pedidos/${id}` +
            `?correo=${encodeURIComponent(correo)}`
        );

        const pedido =
            await response.json();

        if (!response.ok) {
            throw new Error(
                pedido.message ||
                "No se pudo consultar el pedido"
            );
        }

        const puedeCancelar = [
            "PENDIENTE",
            "CONFIRMADO"
        ].includes(pedido.estadoPedido);

        result.innerHTML = `
            <h3>Pedido #${pedido.idVenta}</h3>

            <p>
                <strong>Estado:</strong>
                ${escapeHtml(
                    pedido.estadoPedido || "-"
                )}
            </p>

            <p>
                <strong>Pago:</strong>
                ${escapeHtml(
                    pedido.estadoPago || "-"
                )}
            </p>

            <p>
                <strong>Entrega:</strong>
                ${escapeHtml(
                    pedido.modalidadEntrega || "-"
                )}
            </p>

            <p>
                <strong>Total:</strong>
                ${money(pedido.montoTotal)}
            </p>

            ${
                puedeCancelar
                    ? `
                        <button
                            id="cancelCustomerOrder"
                            class="primary"
                            type="button">
                            Cancelar pedido
                        </button>
                    `
                    : ""
            }
        `;

        if (puedeCancelar) {
            document.getElementById(
                "cancelCustomerOrder"
            ).addEventListener(
                "click",
                () => cancelarPedido(id, correo)
            );
        }

    } catch (error) {
        result.textContent =
            error.message;
    }
}

async function cancelarPedido(id, correo) {
    const confirmar = window.confirm(
        "¿Deseas cancelar este pedido?"
    );

    if (!confirmar) {
        return;
    }

    const result =
        document.getElementById(
            "trackingResult"
        );

    try {
        const response = await fetch(
            `/api/tienda/pedidos/${id}/cancelar` +
            `?correo=${encodeURIComponent(correo)}`,
            {
                method: "PATCH"
            }
        );

        const pedido =
            await response.json();

        if (!response.ok) {
            throw new Error(
                pedido.message ||
                "No se pudo cancelar el pedido"
            );
        }

        result.innerHTML = `
            <p>
                El pedido #${pedido.idVenta}
                fue cancelado correctamente.
            </p>
        `;

    } catch (error) {
        result.textContent =
            error.message;
    }
}

document.addEventListener(
    "DOMContentLoaded",
    () => {
        document
            .getElementById("searchInput")
            .addEventListener(
                "input",
                renderProducts
            );

        document
            .getElementById("cartButton")
            .addEventListener(
                "click",
                () => {
                    document
                        .getElementById("checkout")
                        .scrollIntoView({
                            behavior: "smooth"
                        });
                }
            );

        document
            .getElementById("closeReview")
            .addEventListener(
                "click",
                () => {
                    document
                        .getElementById("reviewModal")
                        .hidden = true;
                }
            );

        document
            .getElementById("reviewModal")
            .addEventListener(
                "click",
                event => {
                    if (
                        event.target.id ===
                        "reviewModal"
                    ) {
                        event.currentTarget.hidden =
                            true;
                    }
                }
            );

        document
            .getElementById("reviewForm")
            .addEventListener(
                "submit",
                async event => {
                    event.preventDefault();

                    const productId =
                        document.getElementById(
                            "reviewProductId"
                        ).value;

                    const body = {
                        nombreCliente:
                            document.getElementById(
                                "reviewName"
                            ).value,

                        correo:
                            document.getElementById(
                                "reviewEmail"
                            ).value || null,

                        puntuacion: Number(
                            document.getElementById(
                                "reviewRating"
                            ).value
                        ),

                        comentario:
                            document.getElementById(
                                "reviewComment"
                            ).value
                    };

                    const response = await fetch(
                        `/api/tienda/productos/${productId}/resenas`,
                        {
                            method: "POST",
                            headers: {
                                "Content-Type":
                                    "application/json"
                            },
                            body: JSON.stringify(body)
                        }
                    );

                    const result =
                        await response.json();

                    document.getElementById(
                        "reviewMessage"
                    ).textContent = response.ok
                        ? "¡Gracias! Tu opinión fue publicada."
                        : (
                            result.message ||
                            "No se pudo publicar la reseña"
                        );

                    if (response.ok) {
                        event.target.reset();

                        await openReviews(
                            Number(productId),
                            document.getElementById(
                                "reviewTitle"
                            ).textContent
                        );

                        loadRating(
                            Number(productId)
                        );
                    }
                }
            );

        document
            .getElementById("checkoutForm")
            .addEventListener(
                "submit",
                submitCheckout
            );

        document
            .getElementById("trackingForm")
            .addEventListener(
                "submit",
                consultarPedido
            );

        configureDelivery();
        loadStore();
    }
);