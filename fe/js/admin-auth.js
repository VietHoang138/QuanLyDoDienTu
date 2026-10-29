/**
 * Kiểm tra quyền admin khi truy cập các trang quản trị
 */
document.addEventListener('DOMContentLoaded', function() {
    // CHỨC NĂNG TẠM THỜI: Đặt vai trò ADMIN cho mục đích kiểm thử
    // Xóa dòng này khi đưa vào sản phẩm thực tế
    localStorage.setItem('vaiTro', 'ADMIN');
    
    // Kiểm tra vai trò từ localStorage
    const vaiTro = localStorage.getItem('vaiTro');
    console.log('Vai trò người dùng hiện tại:', vaiTro);
    
    // Nếu không phải admin, chuyển về trang chủ
    if (vaiTro !== 'ADMIN') {
        alert('Bạn không có quyền truy cập trang này!');
        window.location.href = 'index.html';
        return;
    }
    
    // Kiểm tra xem có phải tài khoản admin đặc biệt không
    const email = localStorage.getItem('email');
    if (email === 'viethoang123@gmail.com' || email === 'admin@techno.vn') {
        console.log('Đăng nhập với tài khoản admin đặc biệt');
    }
    
    // Hiển thị thông tin admin
    const adminName = document.getElementById('admin-name');
    if (adminName) {
        const hoTen = localStorage.getItem('hoTen');
        adminName.textContent = hoTen || 'Admin';
    }
});

/**
 * Đăng xuất khỏi hệ thống admin
 */
function adminLogout() {
    // Xóa thông tin đăng nhập
    localStorage.removeItem('idKH');
    localStorage.removeItem('hoTen');
    localStorage.removeItem('userInfo');
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('vaiTro');
    localStorage.removeItem('email');
    
    // Xóa dữ liệu giỏ hàng và yêu thích
    localStorage.removeItem('cart');
    localStorage.removeItem('wishlist');
    
    // Chuyển về trang đăng nhập
    window.location.href = 'login-register.html';
} 