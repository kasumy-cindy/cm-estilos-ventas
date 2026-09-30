const SHIPPING_COST = 12.90;

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

function money(value) {
    return `S/ ${Number(value || 0).toFixed(2)}`;
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
    return IMAGENES_ANTIGUAS[image] || image || IMAGEN_POR_DEFECTO;
}

function saveCart() {
    localStorage.setItem(
        "cm-estilos-cart",
        JSON.stringify(state.cart)
    );
}

async function readResponse(response) {
    const text = await response.text();

    if (!text) {
        return {};
    }

    try {
        return JSON.parse(text);
    } catch (error) {
        return { message: text };
    }
}

function getSelectedDelivery() {
    return document.querySelector(
        'input[name="modalidadEntrega"]:checked'
    )?.value || "DELIVERY";
}

function getCartItems() {
    return state.cart
        .map(item => ({
            varianteId: Number(item.varianteId),
            cantidad: Number(item.cantidad || 0),
            product: state.products.find(product =>
                Number(product.idVariante) === Number(item.varianteId)
            )
        }))
        .filter(item => item.product && item.cantidad > 0);
}

function calculateSubtotal(items) {
    return items.reduce(
        (total, item) =>
            total +
            Number(item.product.precioVenta || 0) *
            item.cantidad,
        0
    );
}

function renderCart() {
    const cartItems = document.getElementById("cartItems");
    const cartSubtotal = document.getElementById("cartSubtotal");
    const cartTotal = document.getElementById("cartTotal");
    const shippingCost = document.getElementById("shippingCost");
    const summaryProducts = document.getElementById("summaryProducts");
    const cartMessage = document.getElementById("cartMessage");
    const continueButton = document.getElementById("continueCheckout");

    if (!cartItems) {
        return;
    }

    const items = getCartItems();

    state.cart = items.map(item => ({
        varianteId: item.varianteId,
        cantidad: Math.min(
            item.cantidad,
            Number(item.product.stockActual || item.cantidad)
        )
    }));

    saveCart();

    if (!items.length) {
        cartItems.innerHTML = `
            <div class="empty-cart">
                <h3>Tu carrito está vacío</h3>
                <p>Agrega productos desde nuestro catálogo.</p>
                <a class="primary" href="/tienda.html#catalogo">
                    Ver productos
                </a>
            </div>
        `;

        if (cartSubtotal) {
            cartSubtotal.textContent = money(0);
        }

        if (shippingCost) {
            shippingCost.textContent = money(0);
        }

        if (cartTotal) {
            cartTotal.textContent = money(0);
        }

        if (summaryProducts) {
            summaryProducts.textContent =
                "Subtotal (0 productos)";
        }

        if (continueButton) {
            continueButton.disabled = true;
        }

        return;
    }

    if (continueButton) {
        continueButton.disabled = false;
    }

    const subtotal = calculateSubtotal(items);
    const delivery = getSelectedDelivery();
    const shipping = delivery === "DELIVERY"
        ? SHIPPING_COST
        : 0;

    const total = subtotal + shipping;

    const quantity = state.cart.reduce(
        (sum, item) => sum + item.cantidad,
        0
    );

    if (cartSubtotal) {
        cartSubtotal.textContent = money(subtotal);
    }

    if (shippingCost) {
        shippingCost.textContent = shipping > 0
            ? money(shipping)
            : "Gratis";
    }

    if (cartTotal) {
        cartTotal.textContent = money(total);
    }

    if (summaryProducts) {
        summaryProducts.textContent =
            `Subtotal (${quantity} productos)`;
    }

    cartItems.innerHTML = items.map(item => `
        <article
            class="cart-product-item"
            data-cart-item="${item.varianteId}">

            <div class="cart-product-image">
                <img
                    src="${escapeHtml(getImage(item.product))}"
                    alt="${escapeHtml(item.product.productoNombre)}"
                    onerror="this.onerror=null;this.src='/images/vestido-floral.png';">
            </div>

            <div class="cart-product-info">
                <h3>
                    ${escapeHtml(item.product.productoNombre)}
                </h3>

                <p>
                    Talla: ${escapeHtml(item.product.talla)}
                </p>

                <p>
                    Color: ${escapeHtml(item.product.color)}
                </p>

                <strong>
                    ${money(item.product.precioVenta)}
                </strong>
            </div>

            <div class="cart-product-actions">
                <div class="quantity-control">
                    <button
                        type="button"
                        data-cart-minus="${item.varianteId}">
                        −
                    </button>

                    <span>${item.cantidad}</span>

                    <button
                        type="button"
                        data-cart-plus="${item.varianteId}">
                        +
                    </button>
                </div>

                <button
                    type="button"
                    class="remove-cart-item"
                    data-cart-remove="${item.varianteId}"
                    title="Eliminar producto">
                    🗑
                </button>
            </div>
        </article>
    `).join("");

    cartItems.querySelectorAll("[data-cart-minus]")
        .forEach(button => {
            button.addEventListener("click", () => {
                changeQuantity(
                    Number(button.dataset.cartMinus),
                    -1
                );
            });
        });

    cartItems.querySelectorAll("[data-cart-plus]")
        .forEach(button => {
            button.addEventListener("click", () => {
                changeQuantity(
                    Number(button.dataset.cartPlus),
                    1
                );
            });
        });

    cartItems.querySelectorAll("[data-cart-remove]")
        .forEach(button => {
            button.addEventListener("click", () => {
                removeFromCart(
                    Number(button.dataset.cartRemove)
                );
            });
        });

    if (cartMessage) {
        cartMessage.textContent = "";
    }
}

function changeQuantity(varianteId, change) {
    const item = state.cart.find(current =>
        Number(current.varianteId) === Number(varianteId)
    );

    const product = state.products.find(current =>
        Number(current.idVariante) === Number(varianteId)
    );

    if (!item || !product) {
        return;
    }

    item.cantidad = Number(item.cantidad || 0) + change;

    if (item.cantidad <= 0) {
        removeFromCart(varianteId);
        return;
    }

    item.cantidad = Math.min(
        item.cantidad,
        Number(product.stockActual || 0)
    );

    saveCart();
    renderCart();
}

function removeFromCart(varianteId) {
    state.cart = state.cart.filter(item =>
        Number(item.varianteId) !== Number(varianteId)
    );

    saveCart();
    renderCart();
}

function updateDeliveryOptions() {
    const deliveryHome =
        document.getElementById("deliveryHome");

    const addressBox =
        document.getElementById("cartDeliveryAddress");

    const address =
        document.getElementById("cartAddress");

    const deliveryPrice =
        document.getElementById("deliveryPrice");

    const checkoutMode =
        document.getElementById("checkoutDeliveryMode");

    const checkoutAddress =
        document.getElementById("checkoutAddress");

    const isDelivery =
        deliveryHome?.checked === true;

    if (addressBox) {
        addressBox.hidden = !isDelivery;
    }

    if (address) {
        address.required = isDelivery;
    }

    if (deliveryPrice) {
        deliveryPrice.textContent = isDelivery
            ? money(SHIPPING_COST)
            : "Gratis";
    }

    if (checkoutMode) {
        checkoutMode.value = isDelivery
            ? "DELIVERY"
            : "RECOJO_TIENDA";
    }

    if (checkoutAddress && address) {
        checkoutAddress.value = address.value;
        checkoutAddress.required = isDelivery;
    }

    document.querySelectorAll(".delivery-option")
        .forEach(option => {
            const input = option.querySelector("input");

            option.classList.toggle(
                "selected",
                input?.checked === true
            );
        });

    renderCart();
}

async function loadProducts() {
    try {
        const response = await fetch(
            "/api/catalogo?soloDisponibles=true"
        );

        if (response.ok) {
            state.products = await readResponse(response);

            localStorage.setItem(
                "cm-estilos-products",
                JSON.stringify(state.products)
            );
        }
    } catch (error) {
        console.error(
            "No se pudo actualizar el catálogo:",
            error
        );
    }

    renderCart();
}

async function loadPaymentMethods() {
    const paymentMethod =
        document.getElementById("paymentMethod");

    if (!paymentMethod) {
        return;
    }

    paymentMethod.innerHTML = `
        <option value="">
            Cargando métodos de pago...
        </option>
    `;

    try {
        const response = await fetch(
            "/api/tienda/metodos-pago"
        );

        const methods = await readResponse(response);

        if (!response.ok || !Array.isArray(methods)) {
            throw new Error(
                methods.message ||
                "No se pudieron cargar los métodos de pago."
            );
        }

        if (!methods.length) {
            paymentMethod.innerHTML = `
                <option value="">
                    No hay métodos disponibles
                </option>
            `;

            return;
        }

        paymentMethod.innerHTML = `
            <option value="">
                Selecciona un método
            </option>

            ${methods.map(method => `
                <option value="${method.idMetodoPago}">
                    ${escapeHtml(method.nombre)}
                </option>
            `).join("")}
        `;
    } catch (error) {
        paymentMethod.innerHTML = `
            <option value="">
                Error al cargar métodos
            </option>
        `;

        showCartMessage(error.message);
    }
}

function showCartMessage(message) {
    const element =
        document.getElementById("cartMessage");

    if (element) {
        element.textContent = message;
    }
}

function showStep(step) {
    const cartStep =
        document.getElementById("cartStep");

    const checkoutStep =
        document.getElementById("checkoutStep");

    const confirmationStep =
        document.getElementById("confirmationStep");

    cartStep?.classList.toggle(
        "active",
        step === "cart"
    );

    checkoutStep?.classList.toggle(
        "active",
        step === "checkout"
    );

    confirmationStep?.classList.toggle(
        "active",
        step === "confirmation"
    );

    document.getElementById("progressCart")
        ?.classList.toggle(
            "active",
            step === "cart"
        );

    document.getElementById("progressCheckout")
        ?.classList.toggle(
            "active",
            step === "checkout"
        );

    document.getElementById("progressConfirmation")
        ?.classList.toggle(
            "active",
            step === "confirmation"
        );

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}

function configureContinueButton() {
    const button =
        document.getElementById("continueCheckout");

    const address =
        document.getElementById("cartAddress");

    if (!button) {
        return;
    }

    button.addEventListener("click", () => {
        if (!state.cart.length) {
            showCartMessage(
                "Agrega al menos un producto."
            );

            return;
        }

        const delivery =
            getSelectedDelivery();

        if (
            delivery === "DELIVERY" &&
            address &&
            !address.value.trim()
        ) {
            showCartMessage(
                "Ingresa tu dirección de entrega."
            );

            address.focus();
            return;
        }

        const checkoutMode =
            document.getElementById(
                "checkoutDeliveryMode"
            );

        const checkoutAddress =
            document.getElementById(
                "checkoutAddress"
            );

        if (checkoutMode) {
            checkoutMode.value = delivery;
        }

        if (checkoutAddress && address) {
            checkoutAddress.value =
                address.value.trim();

            checkoutAddress.required =
                delivery === "DELIVERY";
        }

        localStorage.setItem(
            "cm-estilos-delivery",
            JSON.stringify({
                modalidadEntrega: delivery,
                direccionEntrega:
                    address?.value.trim() || ""
            })
        );

        showStep("checkout");
        loadPaymentMethods();
    });
}

function configureCheckoutDelivery() {
    const mode =
        document.getElementById(
            "checkoutDeliveryMode"
        );

    const addressField =
        document.getElementById(
            "checkoutAddressField"
        );

    const address =
        document.getElementById(
            "checkoutAddress"
        );

    if (!mode || !addressField || !address) {
        return;
    }

    mode.addEventListener("change", () => {
        const isDelivery =
            mode.value === "DELIVERY";

        addressField.hidden = !isDelivery;
        address.required = isDelivery;

        if (!isDelivery) {
            address.value = "";
        }
    });
}

function configureCheckoutForm() {
    const form =
        document.getElementById(
            "checkoutForm"
        );

    if (!form) {
        return;
    }

    form.addEventListener(
        "submit",
        submitCheckout
    );
}

async function submitCheckout(event) {
    event.preventDefault();

    const message =
        document.getElementById(
            "checkoutMessage"
        );

    if (!state.cart.length) {
        if (message) {
            message.textContent =
                "Tu carrito está vacío.";
        }

        showStep("cart");
        return;
    }

    const formData =
        new FormData(event.target);

    const payload =
        Object.fromEntries(
            formData.entries()
        );

    const delivery =
        payload.modalidadEntrega;

    payload.metodoPagoId =
        payload.metodoPagoId
            ? Number(payload.metodoPagoId)
            : null;

    payload.items = state.cart.map(item => ({
        varianteId: Number(item.varianteId),
        cantidad: Number(item.cantidad)
    }));

    if (!payload.metodoPagoId) {
        if (message) {
            message.textContent =
                "Selecciona un método de pago.";
        }

        return;
    }

    if (
        delivery === "DELIVERY" &&
        !String(
            payload.direccionEntrega || ""
        ).trim()
    ) {
        if (message) {
            message.textContent =
                "Ingresa tu dirección de entrega.";
        }

        return;
    }

    if (delivery === "RECOJO_TIENDA") {
        payload.direccionEntrega = null;
    }

    if (message) {
        message.textContent =
            "Registrando pedido...";
    }

    try {
        const response = await fetch(
            "/api/tienda/checkout",
            {
                method: "POST",
                headers: {
                    "Content-Type":
                        "application/json"
                },
                body: JSON.stringify(payload)
            }
        );

        const result =
            await readResponse(response);

        if (!response.ok) {
            throw new Error(
                result.message ||
                "No se pudo registrar el pedido."
            );
        }

        showConfirmation(
            result,
            payload
        );
    } catch (error) {
        if (message) {
            message.textContent =
                error.message;
        }
    }
}

function showConfirmation(result, payload) {
    const paymentSelect =
        document.getElementById(
            "paymentMethod"
        );

    const selectedPayment =
        paymentSelect
            ?.selectedOptions?.[0]
            ?.textContent
            .trim();

    document
        .getElementById(
            "confirmationOrderId"
        )
        ?.replaceChildren(
            document.createTextNode(
                `#${result.idVenta ?? "-"}`
            )
        );

    document
        .getElementById(
            "confirmationPaymentMethod"
        )
        ?.replaceChildren(
            document.createTextNode(
                result.metodoPago ||
                selectedPayment ||
                "-"
            )
        );

    document
        .getElementById(
            "confirmationOrderStatus"
        )
        ?.replaceChildren(
            document.createTextNode(
                result.estadoPedido ||
                "PENDIENTE"
            )
        );

    document
        .getElementById(
            "confirmationPaymentStatus"
        )
        ?.replaceChildren(
            document.createTextNode(
                result.estadoPago ||
                "PENDIENTE"
            )
        );

    document
        .getElementById(
            "confirmationDelivery"
        )
        ?.replaceChildren(
            document.createTextNode(
                payload.modalidadEntrega ===
                "DELIVERY"
                    ? "Entrega a domicilio"
                    : "Recojo en tienda"
            )
        );

    document
        .getElementById(
            "confirmationTotal"
        )
        ?.replaceChildren(
            document.createTextNode(
                money(result.montoTotal)
            )
        );

    state.cart = [];
    saveCart();
    renderCart();

    localStorage.removeItem(
        "cm-estilos-delivery"
    );

    localStorage.setItem(
        "cm-estilos-last-order",
        JSON.stringify(result)
    );

    showStep("confirmation");
}

function configureBackButton() {
    document
        .getElementById("backToCart")
        ?.addEventListener(
            "click",
            () => showStep("cart")
        );
}

function configureDiscount() {
    const button =
        document.getElementById(
            "applyDiscount"
        );

    const input =
        document.getElementById(
            "discountCode"
        );

    if (!button || !input) {
        return;
    }

    button.addEventListener(
        "click",
        () => {
            const code =
                input.value.trim().toUpperCase();

            showCartMessage(
                code
                    ? "El código será validado al confirmar el pedido."
                    : "Ingresa un código de descuento."
            );
        }
    );
}

document.addEventListener(
    "DOMContentLoaded",
    () => {
        document
            .querySelectorAll(
                'input[name="modalidadEntrega"]'
            )
            .forEach(input => {
                input.addEventListener(
                    "change",
                    updateDeliveryOptions
                );
            });

        configureDiscount();
        configureContinueButton();
        configureCheckoutDelivery();
        configureCheckoutForm();
        configureBackButton();

        updateDeliveryOptions();
        loadProducts();
    }
);