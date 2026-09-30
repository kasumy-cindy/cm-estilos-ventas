const IMAGEN_POR_DEFECTO = "/images/vestido-floral.png";

const IMAGENES_ANTIGUAS = {
    "/images/blusa.svg": "/images/blusa-elegante.png",
    "/images/vestido.svg": "/images/vestido-floral.png",
    "/images/pantalon.svg": "/images/jean-mom-fit.png",
    "/images/accesorios.svg": "/images/bolso-clasico.png",
    "/images/polo.svg": "/images/blusa-volantes.png",
    "/images/falda.svg": "/images/vestido-midi.png",
    "/images/casaca.svg": "/images/chaqueta-rosa.png",
    "/images/short.svg": "/images/jean-mom-fit.png",
    "/images/chompa.svg": "/images/chaqueta-rosa.png",
    "/images/conjunto.svg": "/images/vestido-midi.png",
    "/images/zapatos.svg": "/images/tenis-casual.png",
    "/images/cartera.svg": "/images/bolso-clasico.png"
};

function readStorage(key, defaultValue) {
    try {
        const value = localStorage.getItem(key);
        return value ? JSON.parse(value) : defaultValue;
    } catch (error) {
        return defaultValue;
    }
}

const state = {
    products: readStorage("cm-estilos-products", []),
    cart: readStorage("cm-estilos-cart", [])
};

const money = value =>
    `S/ ${Number(value || 0).toFixed(2)}`;

function saveCart() {
    localStorage.setItem(
        "cm-estilos-cart",
        JSON.stringify(state.cart)
    );
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[character]);
}

function getImage(product) {
    const image = String(product?.imagenUrl || "").trim();

    return IMAGENES_ANTIGUAS[image]
        || image
        || IMAGEN_POR_DEFECTO;
}

function stars(value) {
    const rating = Math.max(
        0,
        Math.min(5, Math.round(Number(value || 0)))
    );

    return "★".repeat(rating) + "☆".repeat(5 - rating);
}

function renderCartCount() {
    const cartCount =
        document.getElementById("cartCount");

    if (!cartCount) {
        return;
    }

    const count = state.cart.reduce(
        (total, item) =>
            total + Number(item.cantidad || 0),
        0
    );

    cartCount.textContent = count;
}

function addToCart(varianteId) {
    const product = state.products.find(item =>
        Number(item.idVariante) === Number(varianteId)
    );

    if (!product) {
        alert("No se encontró el producto seleccionado.");
        return;
    }

    if (Number(product.stockActual || 0) <= 0) {
        alert("Este producto no tiene stock disponible.");
        return;
    }

    const item = state.cart.find(current =>
        Number(current.varianteId) === Number(varianteId)
    );

    if (item) {
        item.cantidad = Math.min(
            Number(item.cantidad || 0) + 1,
            Number(product.stockActual)
        );
    } else {
        state.cart.push({
            varianteId: Number(varianteId),
            cantidad: 1
        });
    }

    saveCart();
    renderCartCount();

    const button = document.querySelector(
        `.add-button[data-id="${varianteId}"]`
    );

    if (button) {
        const text = button.textContent;

        button.textContent = "Agregado ✓";
        button.disabled = true;

        setTimeout(() => {
            button.textContent = text;
            button.disabled = false;
        }, 900);
    }
}

function renderProducts() {
    const searchInput =
        document.getElementById("searchInput");

    const grid =
        document.getElementById("productGrid");

    const message =
        document.getElementById("catalogMessage");

    if (!searchInput || !grid || !message) {
        return;
    }

    const term =
        searchInput.value.toLowerCase().trim();

    const products = state.products.filter(product => {
        const text = [
            product.productoNombre,
            product.sku,
            product.color,
            product.talla
        ]
            .join(" ")
            .toLowerCase();

        return text.includes(term);
    });

    message.textContent = products.length
        ? ""
        : "No encontramos productos disponibles.";

    grid.innerHTML = products.map(product => `
        <article class="product-card">
            <div class="product-art">
                <img
                    src="${escapeHtml(getImage(product))}"
                    alt="${escapeHtml(product.productoNombre)}"
                    onerror="this.onerror=null;this.src='/images/vestido-floral.png';">
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
                    ${Number(product.stockActual || 0)}
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
                    data-review-name="${escapeHtml(product.productoNombre)}">
                    Ver / dejar reseña
                </button>
            </div>
        </article>
    `).join("");

    grid.querySelectorAll(".add-button")
        .forEach(button => {
            button.addEventListener("click", () => {
                addToCart(
                    Number(button.dataset.id)
                );
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
            products.map(product =>
                product.productoId
            )
        )
    ].forEach(loadRating);
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
                ? `${stars(summary.promedio)} ${summary.promedio} (${summary.cantidad})`
                : "★★★★★ Sin reseñas";
        });
    } catch (error) {
        console.error(
            "Error cargando reseñas:",
            error
        );
    }
}

async function openReviews(
    productoId,
    productoNombre
) {
    const modal =
        document.getElementById("reviewModal");

    const list =
        document.getElementById("reviewList");

    if (!modal || !list) {
        return;
    }

    document.getElementById(
        "reviewProductId"
    ).value = productoId;

    document.getElementById(
        "reviewTitle"
    ).textContent = `Reseñas: ${productoNombre}`;

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

        if (!response.ok) {
            throw new Error(
                summary.message ||
                "No se pudieron cargar las reseñas."
            );
        }

        document.getElementById(
            "reviewSummary"
        ).textContent = summary.cantidad
            ? `${stars(summary.promedio)} ${summary.promedio}/5 · ${summary.cantidad} opinión(es)`
            : "Este producto todavía no tiene reseñas.";

        list.innerHTML = summary.resenas?.length
            ? summary.resenas.map(review => `
                <div class="review-item">
                    <strong>
                        ${escapeHtml(review.nombreCliente)}
                    </strong>

                    <div class="rating">
                        ${stars(review.puntuacion)}
                    </div>

                    <p>
                        ${escapeHtml(review.comentario)}
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
                ${escapeHtml(error.message)}
            </p>
        `;
    }
}

async function loadStore() {
    const message =
        document.getElementById("catalogMessage");

    try {
        const response = await fetch(
            "/api/catalogo?soloDisponibles=true"
        );

        if (!response.ok) {
            throw new Error(
                "No se pudo cargar el catálogo."
            );
        }

        const products = await response.json();

        if (!Array.isArray(products)) {
            throw new Error(
                "El catálogo no tiene un formato válido."
            );
        }

        state.products = products;

        localStorage.setItem(
            "cm-estilos-products",
            JSON.stringify(state.products)
        );

        renderProducts();
        renderCartCount();
    } catch (error) {
        if (message) {
            message.textContent = error.message;
        }

        renderProducts();
        renderCartCount();
    }
}

function configureReviews() {
    const closeReview =
        document.getElementById("closeReview");

    const reviewModal =
        document.getElementById("reviewModal");

    const reviewForm =
        document.getElementById("reviewForm");

    if (closeReview && reviewModal) {
        closeReview.addEventListener("click", () => {
            reviewModal.hidden = true;
        });
    }

    if (reviewModal) {
        reviewModal.addEventListener("click", event => {
            if (event.target === reviewModal) {
                reviewModal.hidden = true;
            }
        });
    }

    if (!reviewForm) {
        return;
    }

    reviewForm.addEventListener(
        "submit",
        async event => {
            event.preventDefault();

            const productId =
                document.getElementById(
                    "reviewProductId"
                ).value;

            const message =
                document.getElementById(
                    "reviewMessage"
                );

            const body = {
                nombreCliente:
                    document.getElementById(
                        "reviewName"
                    ).value.trim(),

                correo:
                    document.getElementById(
                        "reviewEmail"
                    ).value.trim() || null,

                puntuacion:
                    Number(
                        document.getElementById(
                            "reviewRating"
                        ).value
                    ),

                comentario:
                    document.getElementById(
                        "reviewComment"
                    ).value.trim()
            };

            try {
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

                if (!response.ok) {
                    throw new Error(
                        result.message ||
                        "No se pudo publicar la reseña."
                    );
                }

                message.textContent =
                    "¡Gracias! Tu opinión fue publicada.";

                event.target.reset();
                loadRating(Number(productId));
            } catch (error) {
                message.textContent =
                    error.message;
            }
        }
    );
}

function configureTracking() {
    const form =
        document.getElementById("trackingForm");

    if (!form) {
        return;
    }

    form.addEventListener(
        "submit",
        async event => {
            event.preventDefault();

            const id =
                document.getElementById(
                    "trackingOrderId"
                ).value;

            const correo =
                document.getElementById(
                    "trackingEmail"
                ).value.trim();

            const result =
                document.getElementById(
                    "trackingResult"
                );

            result.textContent =
                "Consultando pedido...";

            try {
                const response = await fetch(
                    `/api/tienda/pedidos/${encodeURIComponent(id)}?correo=${encodeURIComponent(correo)}`
                );

                const pedido =
                    await response.json();

                if (!response.ok) {
                    throw new Error(
                        pedido.message ||
                        "No se pudo consultar el pedido."
                    );
                }

                result.innerHTML = `
                    <strong>
                        Pedido #${pedido.idVenta}
                    </strong>
                    <br>
                    Estado:
                    ${escapeHtml(pedido.estadoPedido || "-")}
                    <br>
                    Pago:
                    ${escapeHtml(pedido.estadoPago || "-")}
                    <br>
                    Entrega:
                    ${escapeHtml(pedido.modalidadEntrega || "-")}
                `;
            } catch (error) {
                result.textContent =
                    error.message;
            }
        }
    );
}

document.addEventListener(
    "DOMContentLoaded",
    () => {
        const searchInput =
            document.getElementById("searchInput");

        if (searchInput) {
            searchInput.addEventListener(
                "input",
                renderProducts
            );
        }

        const cartButton =
            document.getElementById("cartButton");

        if (cartButton) {
            cartButton.addEventListener(
                "click",
                () => {
                    window.location.href =
                        "/carrito.html";
                }
            );
        }

        renderCartCount();
        configureReviews();
        configureTracking();
        loadStore();
    }
);