<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Phiếu Báo Cáo Chi Tiết Sản Phẩm #${sp.idSp} - TechNo</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/images/lg.png">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" />
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        body {
            background-color: #f3f4f6;
            color: #1f2937;
            padding: 20px;
        }

        .report-container {
            max-width: 900px;
            margin: 0 auto;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
            padding: 35px;
        }

        .no-print-toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 12px 20px;
            margin-bottom: 25px;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 8px 16px;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s;
            border: none;
        }

        .btn-success { background: #10b981; color: white; }
        .btn-success:hover { background: #059669; }
        .btn-secondary { background: #64748b; color: white; }
        .btn-secondary:hover { background: #475569; }

        .report-header {
            display: flex;
            justify-content: space-between;
            border-bottom: 2px solid #e2e8f0;
            padding-bottom: 20px;
            margin-bottom: 25px;
        }

        .company-info h2 {
            font-size: 22px;
            font-weight: 800;
            color: #0f172a;
        }
        .company-info h2 span { color: #2563eb; }
        .company-info p { font-size: 12px; color: #64748b; margin-top: 2px; }

        .report-title-section {
            text-align: right;
        }
        .report-title-section h1 {
            font-size: 19px;
            color: #1e3a8a;
            text-transform: uppercase;
        }
        .report-meta {
            font-size: 12px;
            color: #64748b;
            margin-top: 4px;
        }

        /* Khung thông tin chính */
        .product-overview-grid {
            display: grid;
            grid-template-columns: 280px 1fr;
            gap: 25px;
            margin-bottom: 30px;
        }

        .product-image-box {
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 15px;
            text-align: center;
            background: #fafafa;
        }

        .product-image-box img {
            max-width: 100%;
            height: 220px;
            object-fit: contain;
            border-radius: 6px;
        }

        .product-spec-table {
            width: 100%;
            border-collapse: collapse;
        }

        .product-spec-table th, .product-spec-table td {
            padding: 10px 14px;
            border-bottom: 1px solid #f1f5f9;
            font-size: 13px;
        }

        .product-spec-table th {
            width: 35%;
            color: #64748b;
            font-weight: 600;
            background: #f8fafc;
            text-align: left;
        }

        .product-spec-table td {
            color: #0f172a;
            font-weight: 500;
        }

        /* Thẻ thống kê tài chính */
        .metric-cards {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 15px;
            margin-bottom: 30px;
        }

        .metric-card {
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 15px;
            text-align: center;
        }
        .metric-label {
            font-size: 12px;
            color: #64748b;
            text-transform: uppercase;
            font-weight: 600;
        }
        .metric-value {
            font-size: 20px;
            font-weight: 700;
            color: #1e3a8a;
            margin-top: 6px;
        }

        .status-badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 9999px;
            font-size: 12px;
            font-weight: 600;
        }
        .status-instock { background: #dcfce7; color: #15803d; }
        .status-lowstock { background: #fef3c7; color: #b45309; }
        .status-outstock { background: #fee2e2; color: #b91c1c; }

        .section-heading {
            font-size: 15px;
            font-weight: 700;
            color: #1e3a8a;
            border-left: 4px solid #2563eb;
            padding-left: 10px;
            margin-bottom: 15px;
            text-transform: uppercase;
        }

        .desc-box {
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 15px 20px;
            font-size: 13px;
            color: #334155;
            line-height: 1.6;
            margin-bottom: 30px;
        }

        /* Chữ ký */
        .signature-section {
            display: flex;
            justify-content: space-between;
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e2e8f0;
            page-break-inside: avoid;
        }

        .signature-box {
            text-align: center;
            width: 220px;
        }
        .signature-box .role {
            font-weight: 700;
            color: #334155;
            font-size: 13px;
        }
        .signature-box .sub {
            font-size: 11px;
            color: #64748b;
            font-style: italic;
            margin-top: 2px;
        }
        .signature-box .space {
            height: 70px;
        }
        .signature-box .name {
            font-weight: 600;
            color: #0f172a;
            border-top: 1px dotted #cbd5e1;
            padding-top: 5px;
            font-size: 12px;
        }

        @media print {
            body { background: white; padding: 0; }
            .report-container { box-shadow: none; border-radius: 0; padding: 0; }
            .no-print-toolbar { display: none !important; }
        }
    </style>
</head>
<body>

<div class="report-container">
    <div class="no-print-toolbar">
        <div>
            <a href="${pageContext.request.contextPath}/report/sanpham" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Quay lại Danh Sách Báo Cáo
            </a>
        </div>
        <div>
            <button onclick="window.print()" class="btn btn-success">
                <i class="fas fa-print"></i> In Phiếu Chi Tiết
            </button>
        </div>
    </div>

    <!-- Header Báo Cáo -->
    <div class="report-header">
        <div class="company-info">
            <h2><img src="${pageContext.request.contextPath}/images/logo.png" alt="TechNo" style="width: 56px; height: 48px; object-fit: contain;"></h2>
            <p><i class="fas fa-map-marker-alt"></i> Hệ thống Bán Lẻ Thiết Bị Công Nghệ & Điện Tử</p>
            <p><i class="fas fa-phone"></i> Hotline: 1900-8888 | Website: techno.vn</p>
        </div>
        <div class="report-title-section">
            <h1>PHIẾU KIỂM KÊ & CHI TIẾT SẢN PHẨM</h1>
            <div class="report-meta">
                <p><strong>Mã phiếu:</strong> PK-SP-${sp.idSp}</p>
                <p><strong>Thời gian xuất:</strong> ${ngayLap}</p>
            </div>
        </div>
    </div>

    <!-- Thông Tin Tổng Quan -->
    <div class="product-overview-grid">
        <div class="product-image-box">
            <c:choose>
                <c:when test="${not empty sp.hinhAnhUrls}">
                    <img src="${sp.hinhAnhUrls[0]}" alt="${sp.tenSanPham}">
                </c:when>
                <c:otherwise>
                    <img src="https://placehold.co/300x300?text=No+Image" alt="No image">
                </c:otherwise>
            </c:choose>
            <div style="margin-top: 10px; font-size: 12px; color: #64748b;">
                Hình ảnh sản phẩm niêm yết
            </div>
        </div>

        <div>
            <table class="product-spec-table">
                <tr>
                    <th>Mã sản phẩm:</th>
                    <td><strong style="color: #2563eb;">#${sp.idSp}</strong></td>
                </tr>
                <tr>
                    <th>Tên sản phẩm:</th>
                    <td><strong>${sp.tenSanPham}</strong></td>
                </tr>
                <tr>
                    <th>Danh mục:</th>
                    <td>${sp.tenDanhMuc != null ? sp.tenDanhMuc : 'Chưa phân loại'}</td>
                </tr>
                <tr>
                    <th>Màu sắc:</th>
                    <td>${sp.tenMau != null ? sp.tenMau : 'Mặc định / Không có'}</td>
                </tr>
                <tr>
                    <th>Dung lượng / Phiên bản:</th>
                    <td>${sp.dungLuong != null ? sp.dungLuong : 'Tiêu chuẩn'}</td>
                </tr>
                <tr>
                    <th>Đơn giá niêm yết:</th>
                    <td><strong style="color: #dc2626; font-size: 15px;"><fmt:formatNumber value="${sp.giaBan}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></strong></td>
                </tr>
                <tr>
                    <th>Tình trạng kho:</th>
                    <td>
                        <c:choose>
                            <c:when test="${sp.soLuong <= 0}">
                                <span class="status-badge status-outstock">Hết hàng (0)</span>
                            </c:when>
                            <c:when test="${sp.soLuong < 5}">
                                <span class="status-badge status-lowstock">Sắp hết hàng (${sp.soLuong})</span>
                            </c:when>
                            <c:otherwise>
                                <span class="status-badge status-instock">Sẵn sàng bán (${sp.soLuong})</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <!-- Thống kê tài chính & tồn kho -->
    <div class="section-heading">Thống Kê Tồn Kho & Giá Trị Hàng Hóa</div>
    <div class="metric-cards">
        <div class="metric-card">
            <div class="metric-label">Số Lượng Tồn Hiện Tại</div>
            <div class="metric-value">${sp.soLuong} <span style="font-size: 13px; font-weight: normal; color: #64748b;">cái</span></div>
        </div>
        <div class="metric-card">
            <div class="metric-label">Tổng Giá Trị Tồn Kho</div>
            <div class="metric-value" style="color: #d97706;">
                <fmt:formatNumber value="${sp.giaBan * sp.soLuong}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
            </div>
        </div>
        <div class="metric-card">
            <div class="metric-label">Khuyến Nghị Quản Trị</div>
            <div class="metric-value" style="font-size: 15px; margin-top: 8px;">
                <c:choose>
                    <c:when test="${sp.soLuong <= 0}">
                        <span style="color: #dc2626;">🚨 Nhập hàng khẩn cấp</span>
                    </c:when>
                    <c:when test="${sp.soLuong < 5}">
                        <span style="color: #d97706;">⚠️ Đề xuất đặt hàng</span>
                    </c:when>
                    <c:otherwise>
                        <span style="color: #15803d;">✅ Tồn kho an toàn</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Mô Tả / Ghi Chú Kỹ Thuật -->
    <div class="section-heading">Mô Tả & Thông Tin Chi Tiết</div>
    <div class="desc-box">
        <c:choose>
            <c:when test="${not empty sp.moTaNgan}">
                ${sp.moTaNgan}
            </c:when>
            <c:otherwise>
                <em>Chưa có mô tả chi tiết cho sản phẩm này.</em>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Chữ Ký Phê Duyệt -->
    <div class="signature-section">
        <div class="signature-box">
            <div class="role">Người Lập Phiếu</div>
            <div class="sub">(Ký & Ghi rõ họ tên)</div>
            <div class="space"></div>
            <div class="name">Nhân viên quản lý kho</div>
        </div>
        <div class="signature-box">
            <div class="role">Kiểm Soát Viên</div>
            <div class="sub">(Đối chiếu số lượng thực tế)</div>
            <div class="space"></div>
            <div class="name">Thủ kho phụ trách</div>
        </div>
        <div class="signature-box">
            <div class="role">Quản Lý Cửa Hàng</div>
            <div class="sub">(Xác nhận & Phê duyệt)</div>
            <div class="space"></div>
            <div class="name">Ban Giám Đốc Store</div>
        </div>
    </div>
</div>

</body>
</html>
