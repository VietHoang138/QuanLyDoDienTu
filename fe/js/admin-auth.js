/**
 * Bảo vệ các trang admin — chỉ cho phép truy cập nếu đã đăng nhập với vai trò ADMIN.
 * Được nhúng vào: admins.html, adminSanPham.html, UserAd.html, AdDH.html, AdDH-detail.html
 */
document.addEventListener('DOMContentLoaded', function () {

    const isLoggedIn = localStorage.getItem('isLoggedIn');
    const vaiTro     = localStorage.getItem('vaiTro');

    // Chưa đăng nhập hoặc không phải ADMIN → đá về trang đăng nhập
    if (isLoggedIn !== 'true' || vaiTro !== 'ADMIN') {
        alert('Bạn cần đăng nhập bằng tài khoản Admin để truy cập trang này!');
        window.location.href = 'login-register.html';
        return;
    }

    // Hiển thị tên admin trên header nếu có element
    const adminNameEl = document.getElementById('admin-name');
    if (adminNameEl) {
        adminNameEl.textContent = localStorage.getItem('hoTen') || 'Admin';
    }

    console.log('✅ Admin auth OK —', localStorage.getItem('hoTen'), '|', localStorage.getItem('email'));
});

/**
 * Đăng xuất khỏi hệ thống admin
 */
function adminLogout() {
    localStorage.removeItem('idKH');
    localStorage.removeItem('hoTen');
    localStorage.removeItem('userInfo');
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('vaiTro');
    localStorage.removeItem('email');
    localStorage.removeItem('cart');
    localStorage.removeItem('wishlist');

    window.location.href = 'login-register.html';
}
