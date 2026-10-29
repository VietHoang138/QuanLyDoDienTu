// Wait for the DOM to be fully loaded
document.addEventListener('DOMContentLoaded', () => {

    // --- Configuration & Selectors ---
    const Config = {
        FLASH_SALE_PRODUCT_COUNT: 5,
        NEW_ARRIVALS_PRODUCT_COUNT: 8,
        COUNTDOWN_HOURS: 12, // Hours from now for countdown end
        TOAST_DURATION: 4000, // Milliseconds
        MAX_CART_QUANTITY: 99,
        WISHLIST_STORAGE_KEY: 'monoZoneWishlist',
        CART_STORAGE_KEY: 'monoZoneCart'
    };

    // Centralized DOM element selectors
    const Selectors = {
        flashSaleContainer: '#flash-sale-products',
        newArrivalsContainer: '#new-arrivals-grid',
        countdownHours: '#hours',
        countdownMinutes: '#minutes',
        countdownSeconds: '#seconds',
        countdownContainer: '#sale-countdown',
        cartIcon: '#cart-icon',
        cartSidebar: '#cart-sidebar',
        closeCartBtn: '#close-cart',
        cartItemsContainer: '#cart-items',
        cartCount: '#cart-count',
        cartTotalAmount: '#cart-total-amount',
        emptyCartMessage: '#empty-cart-message',
        cartFooter: '.cart-footer',
        shopNowBtn: '#shop-now-btn',
        overlay: '#overlay',
        loginModalTrigger: '#show-login',
        loginModal: '#login-modal',
        quickViewModal: '#quick-view-modal',
        quickViewContent: '#quick-view-content',
        closeModalBtns: '.close-modal',
        tabBtns: '.tab-btn', // Selects all tab buttons in modals
        tabContents: '.tab-content', // Selects all tab content panels
        loginForm: '#login-form',
        registerForm: '#register-form',
        newsletterForm: '#newsletter-form',
        toastContainer: '#toast-container',
        currentYear: '#current-year',
        productCard: '.product-card', // Base product card selector
        quickViewBtn: '.quick-view', // Quick view button within product card
        addToCartBtnCard: '.btn-add-to-cart', // Add to cart button on product card
        addToCartBtnModal: '.add-to-cart-from-modal', // Add to cart button within quick view modal
        addToWishlistBtn: '.add-to-wishlist', // Add to wishlist button on product card
        wishlistCount: '#wishlist-count' // Wishlist count badge in header
    };

    // --- Utility Functions ---
    const formatCurrency = (amount) => {
        if (amount == null) return '';
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
    };

    const createStarRating = (rating) => {
        const full = Math.floor(rating);
        const half = rating % 1 >= 0.5;
        const empty = 5 - full - (half ? 1 : 0);
        let starsHTML = '';
        for (let i = 0; i < full; i++) starsHTML += '<i class="fas fa-star"></i>';
        if (half) starsHTML += '<i class="fas fa-star-half-alt"></i>';
        for (let i = 0; i < empty; i++) starsHTML += '<i class="far fa-star"></i>';
        return starsHTML;
    };

    const showToast = (type, message) => {
        const container = document.querySelector(Selectors.toastContainer);
        if (!container) return;
        const toast = document.createElement('div');
        toast.className = `toast ${type}`; // e.g., toast success
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        const iconClass = { success: 'fa-check-circle', error: 'fa-exclamation-circle', info: 'fa-info-circle' }[type] || 'fa-bell';
        toast.innerHTML = `
            <i class="fas ${iconClass} toast-icon"></i>
            <div class="toast-message">${message}</div>
            <button class="toast-close" aria-label="Đóng thông báo"><i class="fas fa-times"></i></button>
        `;
        container.appendChild(toast);

        const removeToast = () => {
            toast.style.animation = 'toastSlideOut 0.3s ease forwards';
            // Use 'animationend' event for cleaner removal after animation
            toast.addEventListener('animationend', () => toast.remove(), { once: true });
        };

        const timer = setTimeout(removeToast, Config.TOAST_DURATION);

        toast.querySelector('.toast-close').addEventListener('click', () => {
            clearTimeout(timer); // Prevent automatic removal if closed manually
            removeToast();
        });
    };

    // --- Product Card Rendering ---
    const createProductCard = (product, isWishlisted = false) => {
        const oldPriceHTML = product.oldPrice ? `<span class="old-price">${formatCurrency(product.oldPrice)}</span>` : '';
        const badgeHTML = product.badge ? `<span class="product-badge ${product.badge.toLowerCase()}">${product.badge}</span>` : '';
        const wishlistIconClass = isWishlisted ? 'fas' : 'far'; // Solid 'fas' if wishlisted, regular 'far' otherwise

        return `
            <div class="product-card" data-product-id="${product.id}">
                <div class="product-image">
                    <a href="/product/${product.id}" aria-label="Xem chi tiết ${product.name}">
                        <img src="${product.image}" alt="${product.name}" loading="lazy">
                    </a>
                    ${badgeHTML}
                    <div class="product-overlay">
                        <div class="product-actions">
                            <button class="action-btn quick-view" title="Xem nhanh" data-product-id="${product.id}" aria-label="Xem nhanh ${product.name}">
                                <i class="fas fa-eye"></i>
                            </button>
                            <button class="action-btn add-to-wishlist" title="Thêm vào yêu thích" data-product-id="${product.id}" aria-label="Thêm ${product.name} vào yêu thích">
                                <i class="${wishlistIconClass} fa-heart"></i>
                            </button>
                        </div>
                    </div>
                </div>
                <div class="product-info">
                    <div class="product-category">${product.category}</div>
                    <h3 class="product-title"><a href="/product/${product.id}">${product.name}</a></h3>
                    <div class="product-rating">
                        ${product.rating ? `<div class="stars">${createStarRating(product.rating)}</div><span class="rating-count">(${product.reviewCount})</span>` : ''}
                    </div>
                    <div class="product-price">
                        <span class="current-price">${formatCurrency(product.price)}</span>
                        ${oldPriceHTML}
                    </div>
                     <button class="btn btn-dark btn-add-to-cart" data-product-id="${product.id}">Thêm vào giỏ</button>
                </div>
            </div>
        `;
    };

    // Load products into a specified container
    const loadProducts = (containerSelector, products, wishlist = []) => {
        const container = document.querySelector(containerSelector);
        if (container) {
            // Map products to HTML cards, checking against the wishlist
            container.innerHTML = products.map(p => createProductCard(p, wishlist.includes(p.id))).join('');
        } else {
            console.warn(`Product container "${containerSelector}" not found.`);
        }
    };

    // --- Countdown Timer ---
    const startCountdown = (endTime) => {
        const hoursEl = document.querySelector(Selectors.countdownHours);
        const minutesEl = document.querySelector(Selectors.countdownMinutes);
        const secondsEl = document.querySelector(Selectors.countdownSeconds);
        const container = document.querySelector(Selectors.countdownContainer);

        // Ensure all elements exist before starting the timer
        if (!hoursEl || !minutesEl || !secondsEl || !container) {
            console.warn("Countdown elements not found.");
            return;
        }

        const update = () => {
            const now = Date.now();
            const timeLeft = endTime - now;

            if (timeLeft <= 0) {
                clearInterval(timer);
                container.innerHTML = '<span>Flash Sale đã kết thúc</span>'; // Display end message
                return;
            }

            // Calculate hours, minutes, seconds
            const hours = Math.floor(timeLeft / (1000 * 60 * 60));
            const minutes = Math.floor((timeLeft % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((timeLeft % (1000 * 60)) / 1000);

            // Update display with leading zeros
            hoursEl.textContent = hours.toString().padStart(2, '0');
            minutesEl.textContent = minutes.toString().padStart(2, '0');
            secondsEl.textContent = seconds.toString().padStart(2, '0');
        };

        update(); // Initial call to display immediately
        const timer = setInterval(update, 1000); // Update every second
    };

    // --- Wishlist Logic (using localStorage) ---
    const wishlist = {
        items: [], // Array of product IDs

        // Load wishlist items from localStorage
        _load() {
            try {
                this.items = JSON.parse(localStorage.getItem(Config.WISHLIST_STORAGE_KEY) || '[]');
                if (!Array.isArray(this.items)) this.items = []; // Ensure it's an array
            } catch (e) {
                console.error("Error loading wishlist from localStorage:", e);
                this.items = [];
            }
            this._updateCountUI();
        },

        // Save wishlist items to localStorage
        _save() {
            try {
                localStorage.setItem(Config.WISHLIST_STORAGE_KEY, JSON.stringify(this.items));
            } catch (e) {
                console.error("Error saving wishlist to localStorage:", e);
            }
            this._updateCountUI();
            this._updateProductCardIcons(); // Reflect changes on product cards
        },

        // Update the wishlist count badge in the header
        _updateCountUI() {
            const countEl = document.querySelector(Selectors.wishlistCount);
            if (countEl) {
                countEl.textContent = this.items.length;
                countEl.style.display = this.items.length > 0 ? 'flex' : 'none'; // Show/hide badge
            }
        },

        // Update the heart icons on product cards based on wishlist status
        _updateProductCardIcons() {
            document.querySelectorAll(`${Selectors.productCard} ${Selectors.addToWishlistBtn}`).forEach(btn => {
                 const productId = parseInt(btn.dataset.productId, 10);
                 const icon = btn.querySelector('i.fa-heart'); // Target the heart icon specifically
                 if (!icon) return;

                 if (this.items.includes(productId)) {
                     icon.classList.remove('far'); // Make it regular
                     icon.classList.add('fas'); // Make it solid
                 } else {
                     icon.classList.remove('fas');
                     icon.classList.add('far');
                 }
            });
        },

        // Toggle a product's presence in the wishlist
        toggle(productId) {
            // Note: Product lookup will need to be handled differently without sampleProducts
            const index = this.items.indexOf(productId);
            if (index > -1) {
                // Remove from wishlist
                this.items.splice(index, 1);
                showToast('info', `Đã xóa sản phẩm khỏi yêu thích.`);
            } else {
                // Add to wishlist
                this.items.push(productId);
                showToast('success', `Đã thêm sản phẩm vào yêu thích.`);
            }
            this._save(); // Save changes and update UI
        },

        // Check if a product is in the wishlist
        isWishlisted(productId) {
            return this.items.includes(productId);
        },

        // Initialize wishlist on page load
        init() {
            this._load();
            this._updateProductCardIcons(); // Ensure icons are correct initially
        }
    };

    // --- Cart Logic (using localStorage) ---
    const cart = {
        items: [], // Array of { productId, quantity, product }

        // Load cart items from localStorage
        _load() {
            let storedItems = [];
            try {
                storedItems = JSON.parse(localStorage.getItem(Config.CART_STORAGE_KEY) || '[]');
                if (!Array.isArray(storedItems)) storedItems = [];
            } catch (e) {
                console.error("Error loading cart from localStorage:", e);
                storedItems = [];
            }

            // Validate and clean stored items
            this.items = storedItems.filter(item => 
                item.id && item.name && item.price && item.quantity > 0
            ).map(item => ({
                productId: item.id || item.productId,
                quantity: item.quantity,
                product: item.product || {
                    id: item.id || item.productId,
                    name: item.name,
                    price: item.price,
                    image: item.image,
                    hinhAnhUrls: item.image ? [item.image] : []
                }
            }));
            
            this._save(); // Save back to clean up potentially invalid items
        },

        // Save cart items (only productId and quantity) to localStorage
        _save() {
            const itemsToStore = this.items.map(item => ({
                id: item.productId,
                productId: item.productId,
                quantity: item.quantity,
                name: item.product?.name || 'Sản phẩm',
                price: item.product?.price || 0,
                image: this.getProductImage(item.product),
                product: item.product
            }));
            try {
                localStorage.setItem(Config.CART_STORAGE_KEY, JSON.stringify(itemsToStore));
            } catch (e) {
                console.error("Error saving cart to localStorage:", e);
            }
        },

        // Get product image with fallback
        getProductImage(product) {
            if (product && product.hinhAnhUrls && product.hinhAnhUrls.length > 0) {
                return product.hinhAnhUrls[0];
            }
            if (product && product.image) {
                return product.image;
            }
            return 'https://placehold.co/300x300?text=No+Image';
        },

        // Update the cart UI (sidebar content, count, total)
        _updateUI() {
            const container = document.querySelector(Selectors.cartItemsContainer);
            const countEl = document.querySelector(Selectors.cartCount);
            const totalEl = document.querySelector(Selectors.cartTotalAmount);
            const emptyMsg = document.querySelector(Selectors.emptyCartMessage);
            const footer = document.querySelector(Selectors.cartFooter);

            if (!container || !countEl || !totalEl || !emptyMsg || !footer) {
                console.warn("Cart UI elements not found.");
                return;
            }

            const totalQuantity = this.items.reduce((sum, item) => sum + item.quantity, 0);
            const totalPrice = this.items.reduce((sum, item) => sum + (item.product?.price * item.quantity || 0), 0);

            // Update header count badge
            countEl.textContent = totalQuantity;
            countEl.style.display = totalQuantity > 0 ? 'flex' : 'none'; // Show/hide badge

            // Update sidebar total
            totalEl.textContent = formatCurrency(totalPrice);

            // Show/hide empty message and footer
            if (this.items.length === 0) {
                container.innerHTML = ''; // Clear items list
                emptyMsg.style.display = 'flex'; // Show empty message
                footer.classList.add('hidden'); // Hide footer
            } else {
                emptyMsg.style.display = 'none'; // Hide empty message
                footer.classList.remove('hidden'); // Show footer
                // Render cart items
                container.innerHTML = this.items.map(item => `
                    <div class="cart-item" data-product-id="${item.productId}">
                        <div class="cart-item-image">
                            <a href="/product/${item.productId}">
                                <img src="${this.getProductImage(item.product)}" alt="${item.product?.name || 'Sản phẩm'}" loading="lazy" onerror="this.src='https://placehold.co/300x300?text=No+Image'">
                            </a>
                        </div>
                        <div class="cart-item-details">
                            <h4 class="cart-item-title">
                                <a href="/product/${item.productId}">${item.product?.name || 'Sản phẩm'}</a>
                            </h4>
                            <div class="cart-item-price">${formatCurrency(item.product?.price || 0)}</div>
                        </div>
                        <div class="cart-item-actions">
                             <div class="quantity-control">
                                <button class="quantity-btn decrease-quantity" data-product-id="${item.productId}" aria-label="Giảm số lượng">-</button>
                                <input type="number" class="quantity-input" value="${item.quantity}" min="1" max="${Config.MAX_CART_QUANTITY}" data-product-id="${item.productId}" aria-label="Số lượng">
                                <button class="quantity-btn increase-quantity" data-product-id="${item.productId}" aria-label="Tăng số lượng">+</button>
                            </div>
                            <button class="remove-item" data-product-id="${item.productId}" aria-label="Xóa sản phẩm khỏi giỏ hàng">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </div>`).join('');
            }
        },

        // Attach event listeners for cart item actions (using event delegation)
        _attachListeners() {
            const container = document.querySelector(Selectors.cartItemsContainer);
            if (!container) return;

             // Handle clicks on buttons within the cart items container
             container.addEventListener('click', (e) => {
                // Find the closest ancestor with a product ID
                const cartItemElement = e.target.closest('[data-product-id]');
                if (!cartItemElement) return; // Clicked outside an item action
                const productId = parseInt(cartItemElement.dataset.productId, 10);

                if (e.target.closest('.increase-quantity')) {
                    const item = this.items.find(i => i.productId === productId);
                    if (item) this.updateQuantity(productId, item.quantity + 1);
                } else if (e.target.closest('.decrease-quantity')) {
                    const item = this.items.find(i => i.productId === productId);
                    if (item) this.updateQuantity(productId, item.quantity - 1);
                } else if (e.target.closest('.remove-item')) {
                    this.removeItem(productId);
                }
             });

              // Handle changes to quantity input fields
              container.addEventListener('change', (e) => {
                 if (e.target.classList.contains('quantity-input')) {
                     const productId = parseInt(e.target.dataset.productId, 10);
                     let quantity = parseInt(e.target.value, 10);

                     // Validate quantity
                     if (isNaN(quantity) || quantity < 1) {
                         quantity = 1;
                     }
                     if (quantity > Config.MAX_CART_QUANTITY) {
                         quantity = Config.MAX_CART_QUANTITY;
                     }
                     e.target.value = quantity; // Update input field if value was adjusted
                     this.updateQuantity(productId, quantity);
                 }
             });
        },

        // Add an item to the cart or increase its quantity
        addItem(productId, quantity = 1) {
            const existingItem = this.items.find(item => item.productId === productId);
            if (existingItem) {
                // Update quantity, respecting max limit
                existingItem.quantity = Math.min(existingItem.quantity + quantity, Config.MAX_CART_QUANTITY);
            } else {
                // Add new item - create a basic product object
                const product = {
                    id: productId,
                    name: 'Sản phẩm',
                    price: 0,
                    image: 'https://placehold.co/300x300?text=No+Image',
                    hinhAnhUrls: ['https://placehold.co/300x300?text=No+Image']
                };
                
                this.items.push({ 
                    productId, 
                    quantity: Math.min(quantity, Config.MAX_CART_QUANTITY), 
                    product 
                });
            }
            this._save();
            this._updateUI();
            showToast('success', `Sản phẩm đã được thêm vào giỏ hàng.`);
        },

        // Remove an item from the cart
        removeItem(productId) {
            const index = this.items.findIndex(item => item.productId === productId);
            if (index > -1) {
                const productName = this.items[index].product?.name || 'Sản phẩm';
                this.items.splice(index, 1); // Remove item from array
                this._save();
                this._updateUI();
                showToast('info', `${productName} đã được xóa khỏi giỏ hàng.`);
            }
        },

        // Update the quantity of a specific item
        updateQuantity(productId, quantity) {
            const item = this.items.find(item => item.productId === productId);
            if (item) {
                if (quantity <= 0) {
                    // Remove item if quantity is zero or less
                    this.removeItem(productId);
                } else {
                    // Update quantity, respecting max limit
                    item.quantity = Math.min(quantity, Config.MAX_CART_QUANTITY);
                    this._save();
                    this._updateUI();
                }
            }
        },

        // Initialize the cart on page load
        init() {
            this._load();
            this._updateUI();
            this._attachListeners();
        }
    };

    // --- Modal Management ---
    const modalManager = {
        overlay: document.querySelector(Selectors.overlay),
        modals: document.querySelectorAll('.modal'), // All elements with class 'modal'
        closeButtons: document.querySelectorAll(Selectors.closeModalBtns), // All elements with class 'close-modal'
        activeModal: null, // Track the currently open modal
        lastFocusedElement: null, // To return focus after closing

        // Open a modal by its ID
        _open(modalId) {
            const modal = document.getElementById(modalId);
            if (modal && this.overlay) {
                this.lastFocusedElement = document.activeElement; // Store focus
                this.activeModal = modal;
                modal.classList.add('active');
                this.overlay.classList.add('active');
                modal.setAttribute('aria-hidden', 'false');
                // Focus the first focusable element inside the modal
                const focusable = modal.querySelectorAll('button, [href], input:not([type="hidden"]), select, textarea, [tabindex]:not([tabindex="-1"])');
                if (focusable.length) focusable[0].focus();
                 // Trap focus within the modal (implementation recommended for accessibility)
                 this._trapFocus(modal);
            } else {
                console.warn(`Modal with ID "${modalId}" not found.`);
            }
        },

        // Close a specific modal element
        _close(modal) {
            if (modal && this.overlay && modal.classList.contains('active')) {
                modal.classList.remove('active');
                modal.setAttribute('aria-hidden', 'true');
                this.overlay.classList.remove('active'); // Assume closing one modal closes overlay
                this.activeModal = null;
                // Restore focus to the element that opened the modal
                if (this.lastFocusedElement) {
                    this.lastFocusedElement.focus();
                    this.lastFocusedElement = null;
                }
                // Remove focus trap listener
                document.removeEventListener('keydown', this._handleFocusTrap);
            }
        },

        // Close the currently active modal (if any)
        closeActive() {
            if (this.activeModal) {
                this._close(this.activeModal);
            }
            // Also ensure sidebar is closed if overlay is clicked
            sidebarManager._close(); // Use sidebarManager's method
        },

         // Basic focus trapping (improve for robustness if needed)
         _handleFocusTrap(event) {
             if (event.key !== 'Tab' || !modalManager.activeModal) return;

             const modal = modalManager.activeModal;
             const focusable = Array.from(modal.querySelectorAll('button, [href], input:not([type="hidden"]), select, textarea, [tabindex]:not([tabindex="-1"])')).filter(el => el.offsetParent !== null); // Filter only visible elements
             if (focusable.length === 0) return;

             const firstElement = focusable[0];
             const lastElement = focusable[focusable.length - 1];

             if (event.shiftKey) { // Shift + Tab
                 if (document.activeElement === firstElement) {
                     lastElement.focus();
                     event.preventDefault();
                 }
             } else { // Tab
                 if (document.activeElement === lastElement) {
                     firstElement.focus();
                     event.preventDefault();
                 }
             }
         },

         _trapFocus(modal) {
            document.removeEventListener('keydown', this._handleFocusTrap); // Remove previous listener
            document.addEventListener('keydown', this._handleFocusTrap);
         },

        // Initialize modal triggers and close buttons
        init() {
             // Login modal trigger
            const loginTrigger = document.querySelector(Selectors.loginModalTrigger);
            if(loginTrigger) {
                loginTrigger.addEventListener('click', () => this._open(Selectors.loginModal.substring(1)));
            }

            // Close buttons inside modals
            this.closeButtons.forEach(btn => {
                btn.addEventListener('click', (e) => {
                    // Find the closest parent modal or sidebar and close it
                    const modalToClose = e.target.closest('.modal');
                    if (modalToClose) {
                        this._close(modalToClose);
                    }
                });
            });

            // Overlay click closes active modal/sidebar
            this.overlay?.addEventListener('click', () => this.closeActive());

            // Escape key closes active modal/sidebar
            document.addEventListener('keydown', (e) => {
                if (e.key === 'Escape') {
                    this.closeActive();
                }
            });

             // --- Tab Switching Logic within Modals ---
             document.querySelectorAll('.modal').forEach(modal => {
                 const tabs = modal.querySelectorAll(Selectors.tabBtns);
                 const contents = modal.querySelectorAll(Selectors.tabContents);

                 tabs.forEach(tab => {
                     tab.addEventListener('click', () => {
                         const targetTabId = tab.dataset.tab; // e.g., "login", "register"

                         // Update button states
                         tabs.forEach(t => {
                             t.classList.toggle('active', t === tab);
                             t.setAttribute('aria-selected', t === tab ? 'true' : 'false');
                         });

                         // Update content visibility
                         contents.forEach(content => {
                             const contentId = content.id; // e.g., "login-tab", "register-tab"
                             const isTargetContent = contentId === `${targetTabId}-tab`;
                             content.classList.toggle('hidden', !isTargetContent);
                             content.toggleAttribute('hidden', !isTargetContent);
                         });
                     });
                 });
             });
        }
    };

     // --- Quick View Modal Logic ---
    const quickView = {
         modal: document.querySelector(Selectors.quickViewModal),
         contentContainer: document.querySelector(Selectors.quickViewContent),

         // Render product details inside the quick view modal
         _render(product) {
             if (!this.contentContainer) return;

             const isWishlisted = wishlist.isWishlisted(product.id);
             const wishlistIconClass = isWishlisted ? 'fas' : 'far';
             const oldPriceHTML = product.oldPrice ? `<span class="old-price">${formatCurrency(product.oldPrice)}</span>` : '';

             this.contentContainer.innerHTML = `
                <div class="modal-product-image">
                    <img src="${product.image}" alt="${product.name}">
                </div>
                <div class="modal-product-info">
                    <h2 class="modal-product-title" id="quick-view-modal-title">${product.name}</h2>
                    <div class="product-rating">
                         ${product.rating ? `<div class="stars">${createStarRating(product.rating)}</div><span class="rating-count">(${product.reviewCount} đánh giá)</span>` : ''}
                    </div>
                    <div class="modal-product-price">
                        <span class="current-price">${formatCurrency(product.price)}</span>
                        ${oldPriceHTML}
                    </div>
                    <p class="modal-product-desc">Mô tả nhanh cho sản phẩm ${product.name}. Chi tiết hơn có thể được thêm vào đây.</p>
                    <div class="product-quantity">
                        <label for="modal-quantity-${product.id}" class="variation-title">Số lượng:</label>
                        <div class="quantity-control">
                            <button class="quantity-btn decrease-modal-quantity" aria-label="Giảm số lượng">-</button>
                            <input type="number" class="quantity-input" id="modal-quantity-${product.id}" value="1" min="1" max="${Config.MAX_CART_QUANTITY}" aria-label="Số lượng">
                            <button class="quantity-btn increase-modal-quantity" aria-label="Tăng số lượng">+</button>
                        </div>
                    </div>
                    <div class="modal-action-buttons">
                        <button class="btn btn-dark add-to-cart-from-modal" data-product-id="${product.id}">
                            <i class="fas fa-shopping-bag"></i> Thêm vào giỏ
                        </button>
                         <button class="btn btn-outline-dark add-to-wishlist-from-modal" data-product-id="${product.id}" title="Thêm vào yêu thích">
                            <i class="${wishlistIconClass} fa-heart"></i> Yêu thích
                        </button>
                    </div>
                </div>`;
             this._attachListeners(product.id); // Attach listeners after rendering
         },

         // Attach event listeners specific to the quick view modal content
         _attachListeners(productId) {
            const modal = this.modal; // Reference the modal element
            if(!modal) return;

             const qtyInput = modal.querySelector(`#modal-quantity-${productId}`);
             const decreaseBtn = modal.querySelector('.decrease-modal-quantity');
             const increaseBtn = modal.querySelector('.increase-modal-quantity');
             const addToCartBtn = modal.querySelector(Selectors.addToCartBtnModal);
             const addToWishlistBtn = modal.querySelector('.add-to-wishlist-from-modal');

             if (!qtyInput || !decreaseBtn || !increaseBtn || !addToCartBtn || !addToWishlistBtn) {
                 console.warn("Quick view modal elements not found for attaching listeners.");
                 return;
             }

             // Quantity buttons
             increaseBtn.addEventListener('click', () => {
                 let currentVal = parseInt(qtyInput.value, 10);
                 if (currentVal < Config.MAX_CART_QUANTITY) qtyInput.value = currentVal + 1;
             });
             decreaseBtn.addEventListener('click', () => {
                  let currentVal = parseInt(qtyInput.value, 10);
                 if (currentVal > 1) qtyInput.value = currentVal - 1;
             });

             // Add to Cart button
             addToCartBtn.addEventListener('click', () => {
                 const quantity = parseInt(qtyInput.value, 10);
                 cart.addItem(productId, quantity);
                 modalManager._close(modal); // Close modal after adding
             });

             // Add to Wishlist button
             addToWishlistBtn.addEventListener('click', (e) => {
                 wishlist.toggle(productId); // Toggle wishlist status
                 // Update icon state immediately
                 const icon = e.currentTarget.querySelector('i.fa-heart');
                 if (icon) {
                     icon.classList.toggle('far');
                     icon.classList.toggle('fas');
                 }
                 // Optional: Add visual feedback like button text change or animation
             });
         },

         // Show the quick view modal for a specific product
         show(productId) {
             // Note: Without sampleProducts, product lookup will need to be handled differently
             showToast('info', 'Chức năng xem nhanh sẽ được kết nối với API sản phẩm.');
         }
    };

    // --- Sidebar Management (Cart Sidebar) ---
    const sidebarManager = {
        sidebar: document.querySelector(Selectors.cartSidebar),
        overlay: document.querySelector(Selectors.overlay),
        trigger: document.querySelector(Selectors.cartIcon),
        closeBtn: document.querySelector(Selectors.closeCartBtn),
        shopNowBtn: document.querySelector(Selectors.shopNowBtn), // Button inside empty cart message

        _open() {
            if(this.sidebar && this.overlay) {
                this.sidebar.classList.add('active');
                this.overlay.classList.add('active'); // Show overlay when sidebar opens
                 this.sidebar.setAttribute('aria-hidden', 'false');
                 // Optional: Focus management for sidebar
                 const focusable = this.sidebar.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
                 if (focusable.length) focusable[0].focus();
            }
        },
        _close() {
             if(this.sidebar && this.overlay && this.sidebar.classList.contains('active')) {
                this.sidebar.classList.remove('active');
                this.overlay.classList.remove('active'); // Hide overlay when sidebar closes
                 this.sidebar.setAttribute('aria-hidden', 'true');
                 // Optional: Restore focus if needed (handled by modalManager if overlay is clicked)
            }
        },
        init() {
            // Open sidebar on cart icon click
            this.trigger?.addEventListener('click', (e) => {
                 e.stopPropagation(); // Prevent triggering overlay close
                 this._open();
            });
            // Close sidebar on its close button click
            this.closeBtn?.addEventListener('click', () => this._close());
            // Close sidebar via shop now button (in empty cart)
            this.shopNowBtn?.addEventListener('click', () => this._close());

            // Overlay click handled by modalManager
            // Escape key handled by modalManager
        }
    };

    // --- Global Event Listeners (using event delegation on document) ---
    document.addEventListener('click', (e) => {
        const target = e.target;

        // Check for clicks on specific buttons using closest()
        const quickViewBtn = target.closest(Selectors.quickViewBtn);
        const addToCartCardBtn = target.closest(Selectors.addToCartBtnCard);
        const wishlistCardBtn = target.closest(`${Selectors.productCard} ${Selectors.addToWishlistBtn}`); // Specific wishlist btn on cards

        if (quickViewBtn) {
            const productId = parseInt(quickViewBtn.dataset.productId, 10);
            if (!isNaN(productId)) quickView.show(productId);
        } else if (addToCartCardBtn) {
            const productId = parseInt(addToCartCardBtn.dataset.productId, 10);
             if (!isNaN(productId)) cart.addItem(productId);
        } else if (wishlistCardBtn) {
            const productId = parseInt(wishlistCardBtn.dataset.productId, 10);
             if (!isNaN(productId)) wishlist.toggle(productId);
        }
        // Note: Login trigger is handled within modalManager.init()
        // Note: Cart trigger is handled within sidebarManager.init()
    });

    // --- Initializations ---
    modalManager.init(); // Setup modals, overlay, tabs, close buttons
    sidebarManager.init(); // Setup cart sidebar trigger and close
    wishlist.init(); // Load wishlist from localStorage, update UI
    cart.init(); // Load cart from localStorage, update UI, attach listeners

    // Note: Product loading will need to be connected to actual API endpoints
    // loadProducts(Selectors.flashSaleContainer, [], wishlist.items);
    // loadProducts(Selectors.newArrivalsContainer, [], wishlist.items);

    // Start countdown timer if container exists
    if (document.querySelector(Selectors.countdownContainer)) {
        startCountdown(new Date(Date.now() + Config.COUNTDOWN_HOURS * 3600 * 1000));
    }

    // Update Footer Year
    const currentYearEl = document.querySelector(Selectors.currentYear);
    if (currentYearEl) {
        currentYearEl.textContent = new Date().getFullYear();
    }

    // Prevent actual form submissions for demo purposes
    document.querySelector(Selectors.loginForm)?.addEventListener('submit', e => {
        e.preventDefault();
        showToast('info', 'Chức năng đăng nhập đang được phát triển.');
    });
    document.querySelector(Selectors.registerForm)?.addEventListener('submit', e => {
        e.preventDefault();
        showToast('info', 'Chức năng đăng ký đang được phát triển.');
    });
    document.querySelector(Selectors.newsletterForm)?.addEventListener('submit', e => {
        e.preventDefault();
        const emailInput = document.querySelector('#newsletter-email-input');
        if (emailInput && emailInput.value) {
             showToast('success', `Đã đăng ký nhận tin với email: ${emailInput.value}`);
             emailInput.value = ''; // Clear input after "submission"
        } else {
             showToast('error', 'Vui lòng nhập địa chỉ email.');
        }
    });

    console.log("TechNo Storefront Initialized - Ready for API integration.");

}); // End DOMContentLoaded

// Intersection Observer for animations
const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
        if (entry.isIntersecting) {
            entry.target.classList.add('show');
        } else {
            entry.target.classList.remove('show');
        }
    });
}); 

// Apply observer to hidden elements
const hiddenElements = document.querySelectorAll('.hidden');
hiddenElements.forEach((el) => observer.observe(el));

// Slideshow functionality
let sliderImages = document.querySelectorAll('.slide'),
    current = 0;

function reset() {
    for (let i = 0; i < sliderImages.length; i++) {
        sliderImages[i].classList.add('slide-out');
        sliderImages[i].classList.remove('slide-in');
    }
}

function startSlide() {
    reset();
    if (sliderImages.length > 0) {
        sliderImages[0].classList.remove('slide-out');
        sliderImages[0].classList.add('slide-in');
    }
}

function nextSlide() {
    if (sliderImages.length === 0) return;
    reset();
    current++;
    sliderImages[current % sliderImages.length].classList.remove('slide-out');
    sliderImages[current % sliderImages.length].classList.add('slide-in');
}

// Start slideshow
if (sliderImages.length > 0) {
    setInterval(nextSlide, 5000);
    startSlide();
}

// Brand slider functionality
document.addEventListener('DOMContentLoaded', () => {
    const track = document.querySelector('.brand-slider-track');
    if (track) {
        track.innerHTML += track.innerHTML; // Double the content for seamless loop
    }
});

// Main slideshow with background images
document.addEventListener('DOMContentLoaded', () => {
    const slides = document.querySelectorAll('.poster.slide');
    const backgrounds = [
        'IMAGES/dongho.jpg',
        'IMAGES/ipad.png'
    ];
    let currentSlide = 0;

    // Set initial background images
    slides.forEach((slide, i) => {
        if (backgrounds[i]) {
            slide.style.backgroundImage = `url(${backgrounds[i]})`;
        }
        slide.style.zIndex = 0;
        slide.classList.remove('active', 'out-left');
    });

    // Show first slide
    if (slides.length > 0) {
        slides[currentSlide].classList.add('active');
    }

    function nextSlide() {
        if (slides.length === 0) return;
        
        const nextSlideIndex = (currentSlide + 1) % slides.length;

        // Current slide moves out to the left
        slides[currentSlide].classList.remove('active');
        slides[currentSlide].classList.add('out-left');

        // Next slide comes in from the right
        slides[nextSlideIndex].classList.remove('out-left');
        slides[nextSlideIndex].classList.add('active');

        // Reset old slide after transition
        setTimeout(() => {
            slides[currentSlide].classList.remove('out-left');
            slides[currentSlide].style.zIndex = 0;
        }, 1000); // Match with CSS transition duration

        currentSlide = nextSlideIndex;
    }

    // Auto-advance slideshow
    if (slides.length > 1) {
        setInterval(nextSlide, 5000);
    }
});

// Variation option handlers
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.variation-option').forEach(btn => {
        btn.addEventListener('click', function () {
            // Remove 'selected' from sibling buttons
            this.parentElement.querySelectorAll('.variation-option').forEach(b => b.classList.remove('selected'));
            this.classList.add('selected');

            // Update image if data-image attribute exists
            const imageUrl = this.getAttribute('data-image');
            if (imageUrl) {
                const mainImage = document.getElementById('main-product-image');
                if (mainImage) {
                    mainImage.src = imageUrl;
                }
            }
        });
    });
});

