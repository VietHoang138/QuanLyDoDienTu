// File: checkout.js
// Xử lý quá trình thanh toán và lưu đơn hàng

function checkoutApp() {
    return {
        cartItems: [],
        cartTotal: 0,
        shippingFee: 30000,
        discount: 0,
        discountCode: '',
        paymentMethod: 'COD',
        isLoading: false,
        showToast: false,
        toastMessage: '',
        toastType: 'success',
        formData: {
            hoKhachHang: '',
            tenKhachHang: '',
            soDienThoai: '',
            email: '',
            diaChi: '',
            tinhThanh: '',
            quanHuyen: '',
            phuongXa: '',
            ghiChuDonHang: ''
        },
        
        init() {
            // Khởi tạo dữ liệu giỏ hàng từ localStorage
            this.loadCartItems();
            
            // Nếu giỏ hàng trống, chuyển hướng về trang giỏ hàng
            if (this.cartItems.length === 0) {
                window.location.href = 'cart.html';
            }
            
            // Tính toán giá trị ban đầu
            this.calculateTotals();
        },
        
        loadCartItems() {
            const cart = localStorage.getItem('cart');
            this.cartItems = cart ? JSON.parse(cart) : [];
        },
        
        calculateTotals() {
            // Tính tổng tiền hàng
            this.cartTotal = this.cartItems.reduce((total, item) => {
                return total + (item.price * item.quantity);
            }, 0);
        },
        
        applyDiscountCode() {
            // Reset discount trước
            this.discount = 0;
            
            // Xử lý mã giảm giá (ví dụ)
            if (this.discountCode === 'GIAM10') {
                this.discount = this.cartTotal * 0.1;
                this.showToastMessage('Áp dụng mã giảm giá thành công!', 'success');
            } else if (this.discountCode.trim() !== '') {
                this.showToastMessage('Mã giảm giá không hợp lệ!', 'error');
            }
        },
        
        getOrderTotal() {
            return this.cartTotal + this.shippingFee - this.discount;
        },
        
        showToastMessage(message, type = 'success') {
            this.toastMessage = message;
            this.toastType = type;
            this.showToast = true;
            
            setTimeout(() => {
                this.showToast = false;
            }, 3000);
        },
        
        validateForm() {
            // Kiểm tra các trường bắt buộc
            const requiredFields = [
                'hoKhachHang', 'tenKhachHang', 'soDienThoai', 
                'email', 'diaChi', 'tinhThanh', 'quanHuyen', 'phuongXa'
            ];
            
            for (const field of requiredFields) {
                if (!this.formData[field]) {
                    this.showToastMessage(`Vui lòng điền đầy đủ thông tin bắt buộc!`, 'error');
                    return false;
                }
            }
            
            // Kiểm tra định dạng email
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(this.formData.email)) {
                this.showToastMessage('Email không hợp lệ!', 'error');
                return false;
            }
            
            // Kiểm tra số điện thoại
            const phoneRegex = /^[0-9]{10,11}$/;
            if (!phoneRegex.test(this.formData.soDienThoai)) {
                this.showToastMessage('Số điện thoại không hợp lệ!', 'error');
                return false;
            }
            
            return true;
        },
        
        placeOrder() {
            if (!this.validateForm()) {
                return;
            }
            
            this.isLoading = true;
            
            // Tạo dữ liệu đơn hàng
            const orderData = {
                id: 'DH' + Date.now().toString().slice(-6),
                customerName: this.formData.hoKhachHang + ' ' + this.formData.tenKhachHang,
                phone: this.formData.soDienThoai,
                email: this.formData.email,
                address: `${this.formData.diaChi}, ${this.formData.phuongXa}, ${this.formData.quanHuyen}, ${this.formData.tinhThanh}`,
                items: this.cartItems.map(item => ({
                    id: item.id,
                    name: item.name,
                    price: item.price,
                    quantity: item.quantity,
                    attributes: item.attributes || ''
                })),
                shippingFee: this.shippingFee,
                discount: this.discount,
                couponCode: this.discountCode,
                total: this.getOrderTotal(),
                status: 'Chờ xác nhận',
                date: new Date().toISOString(),
                note: this.formData.ghiChuDonHang,
                paymentMethod: this.paymentMethod
            };
            
            // Lưu đơn hàng vào localStorage
            this.saveOrder(orderData);
            
            // Mô phỏng gửi đơn hàng
            setTimeout(() => {
                this.isLoading = false;
                
                // Xóa giỏ hàng
                localStorage.removeItem('cart');
                
                // Chuyển hướng đến trang xác nhận đơn hàng
                window.location.href = `order-confirmation.html?id=${orderData.id}`;
            }, 1500);
        },
        
        saveOrder(orderData) {
            // Lấy danh sách đơn hàng hiện tại
            const orders = localStorage.getItem('orders');
            const orderList = orders ? JSON.parse(orders) : [];
            
            // Thêm đơn hàng mới
            orderList.push(orderData);
            
            // Lưu lại vào localStorage
            localStorage.setItem('orders', JSON.stringify(orderList));
        }
    };
} 