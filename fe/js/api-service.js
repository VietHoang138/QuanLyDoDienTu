// API Service cho dự án TechNo
class ApiService {
    constructor() {
        this.baseURL = 'http://localhost:8080/api';
        this.setupAxios();
    }

    setupAxios() {
        // Cấu hình axios mặc định
        axios.defaults.baseURL = this.baseURL;
        axios.defaults.headers.common['Content-Type'] = 'application/json';
        axios.defaults.withCredentials = false;
        
        // Interceptor để xử lý lỗi chung
        axios.interceptors.response.use(
            (response) => response,
            (error) => {
                console.error('API Error:', error);
                return Promise.reject(error);
            }
        );
    }

    // === SẢN PHẨM ===
    async getAllProducts() {
        try {
            const response = await axios.get('/sanpham');
            return response.data;
        } catch (error) {
            console.error('Lỗi khi lấy danh sách sản phẩm:', error);
            throw error;
        }
    }

    async getLatestProducts(limit = 8) {
        try {
            const response = await axios.get('/sanpham/latest', {
                params: { limit }
            });
            return response.data;
        } catch (error) {
            console.error('Lỗi khi lấy sản phẩm mới nhất:', error);
            throw error;
        }
    }

    async getProductDetail(productId) {
        try {
            const response = await axios.get(`/sanpham/chitiet/${productId}`);
            return response.data;
        } catch (error) {
            console.error('Lỗi khi lấy chi tiết sản phẩm:', error);
            throw error;
        }
    }

    async getProductsByCategory(categoryId) {
        try {
            const response = await axios.get(`/sanpham/danhmuc/${categoryId}`);
            return response.data;
        } catch (error) {
            console.error('Lỗi khi lấy sản phẩm theo danh mục:', error);
            throw error;
        }
    }

    async searchProducts(keyword) {
        try {
            const response = await axios.get('/sanpham/search', {
                params: { keyword }
            });
            return response.data;
        } catch (error) {
            console.error('Lỗi khi tìm kiếm sản phẩm:', error);
            throw error;
        }
    }

    // === DANH MỤC ===
    async getCategories() {
        try {
            const response = await axios.get('/danhmuc');
            return response.data;
        } catch (error) {
            console.error('Lỗi khi lấy danh sách danh mục:', error);
            throw error;
        }
    }

    // === GIỎ HÀNG ===
    async getCart(customerId) {
        try {
            const response = await axios.get(`/cart/${customerId}`);
            return response.data;
        } catch (error) {
            console.error('Lỗi khi lấy giỏ hàng:', error);
            throw error;
        }
    }

    async addToCart(customerId, productId, quantity = 1) {
        try {
            const response = await axios.post(`/cart/add`, null, {
                params: {
                    idKH: customerId,
                    idSP: productId,
                    soLuong: quantity
                }
            });
            return response.data;
        } catch (error) {
            console.error('Lỗi khi thêm vào giỏ hàng:', error);
            throw error;
        }
    }

    async updateCartItem(customerId, productId, quantity) {
        try {
            const response = await axios.put(`/cart/update`, null, {
                params: {
                    idKH: customerId,
                    idSP: productId,
                    soLuong: quantity
                }
            });
            return response.data;
        } catch (error) {
            console.error('Lỗi khi cập nhật giỏ hàng:', error);
            throw error;
        }
    }

    async removeFromCart(customerId, productId) {
        try {
            const response = await axios.delete(`/cart/remove`, {
                params: {
                    idKH: customerId,
                    idSP: productId
                }
            });
            return response.data;
        } catch (error) {
            console.error('Lỗi khi xóa khỏi giỏ hàng:', error);
            throw error;
        }
    }

    // === DANH SÁCH YÊU THÍCH ===
    async getWishlist(customerId) {
        try {
            const response = await axios.get(`/wishlist/${customerId}`);
            return response.data;
        } catch (error) {
            console.error('Lỗi khi lấy danh sách yêu thích:', error);
            throw error;
        }
    }

    async addToWishlist(customerId, productId) {
        try {
            const response = await axios.post(`/wishlist/add`, null, {
                params: {
                    idKH: customerId,
                    idSP: productId
                }
            });
            return response.data;
        } catch (error) {
            console.error('Lỗi khi thêm vào danh sách yêu thích:', error);
            throw error;
        }
    }

    async removeFromWishlist(customerId, productId) {
        try {
            const response = await axios.delete(`/wishlist/remove`, {
                params: {
                    idKH: customerId,
                    idSP: productId
                }
            });
            return response.data;
        } catch (error) {
            console.error('Lỗi khi xóa khỏi danh sách yêu thích:', error);
            throw error;
        }
    }

    // === KHÁCH HÀNG ===
    async login(email, password) {
        try {
            const response = await axios.post('/auth/login', {
                email,
                matKhau: password
            });
            return response.data;
        } catch (error) {
            console.error('Lỗi khi đăng nhập:', error);
            throw error;
        }
    }

    async register(userData) {
        try {
            const response = await axios.post('/auth/register', userData);
            return response.data;
        } catch (error) {
            console.error('Lỗi khi đăng ký:', error);
            throw error;
        }
    }

    // === ĐƠN HÀNG ===
    async createOrder(orderData) {
        try {
            const response = await axios.post('/donhang/create', orderData);
            return response.data;
        } catch (error) {
            console.error('Lỗi khi tạo đơn hàng:', error);
            throw error;
        }
    }

    async getOrderHistory(customerId) {
        try {
            const response = await axios.get(`/donhang/customer/${customerId}`);
            return response.data;
        } catch (error) {
            console.error('Lỗi khi lấy lịch sử đơn hàng:', error);
            throw error;
        }
    }

    // === FLASH SALE ===
    async getFlashSaleProducts() {
        try {
            const response = await axios.get('/flash-sale/products');
            return response.data;
        } catch (error) {
            console.error('Lỗi khi lấy sản phẩm flash sale:', error);
            throw error;
        }
    }
}

// Khởi tạo instance global
window.apiService = new ApiService(); 