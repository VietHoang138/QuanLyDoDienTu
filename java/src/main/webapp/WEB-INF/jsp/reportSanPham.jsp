<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Báo Cáo Thống Kê Danh Sách Sản Phẩm - TechNo</title>
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
            max-width: 1200px;
            margin: 0 auto;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
            overflow: hidden;
            padding: 30px;
        }

        /* Toolbar điều khiển (ẩn khi in) */
        .no-print-toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 15px 20px;
            margin-bottom: 25px;
        }

        .toolbar-group {
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
        }

        .filter-form {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-wrap: wrap;
        }

        .filter-form input, .filter-form select {
            padding: 7px 12px;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            font-size: 13px;
            outline: none;
        }

        .filter-form input:focus, .filter-form select:focus {
            border-color: #3b82f6;
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

        .btn-primary {
            background-color: #2563eb;
            color: white;
        }
        .btn-primary:hover {
            background-color: #1d4ed8;
        }

        .btn-success {
            background-color: #10b981;
            color: white;
        }
        .btn-success:hover {
            background-color: #059669;
        }

        .btn-secondary {
            background-color: #64748b;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #475569;
        }

        .btn-outline {
            background-color: white;
            border: 1px solid #cbd5e1;
            color: #334155;
        }
        .btn-outline:hover {
            background-color: #f1f5f9;
        }

        /* Header Báo Cáo */
        .report-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            border-bottom: 2px solid #e2e8f0;
            padding-bottom: 20px;
            margin-bottom: 25px;
        }

        .company-info h2 {
            font-size: 24px;
            color: #0f172a;
            font-weight: 800;
        }
        .company-info h2 span {
            color: #2563eb;
        }
        .company-info p {
            font-size: 13px;
            color: #64748b;
            margin-top: 3px;
        }

        .report-title-section {
            text-align: right;
        }
        .report-title-section h1 {
            font-size: 22px;
            color: #1e3a8a;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .report-meta {
            font-size: 12px;
            color: #64748b;
            margin-top: 5px;
            line-height: 1.5;
        }

        /* Thẻ thống kê tổng quan */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 15px;
            margin-bottom: 25px;
        }

        .stat-card {
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 15px;
            border-left: 4px solid #3b82f6;
        }

        .stat-card.green { border-left-color: #10b981; }
        .stat-card.yellow { border-left-color: #f59e0b; }
        .stat-card.red { border-left-color: #ef4444; }

        .stat-label {
            font-size: 12px;
            font-weight: 600;
            color: #64748b;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .stat-value {
            font-size: 20px;
            font-weight: 700;
            color: #0f172a;
            margin-top: 6px;
        }

        .stat-sub {
            font-size: 11px;
            color: #94a3b8;
            margin-top: 4px;
        }

        /* Chi tiết bộ lọc đang áp dụng */
        .active-filters {
            background: #eff6ff;
            border: 1px dashed #93c5fd;
            border-radius: 6px;
            padding: 10px 15px;
            margin-bottom: 20px;
            font-size: 13px;
            color: #1e40af;
            display: flex;
            align-items: center;
            gap: 15px;
            flex-wrap: wrap;
        }

        /* Bảng dữ liệu */
        .table-responsive {
            width: 100%;
            overflow-x: auto;
            margin-bottom: 30px;
        }

        table.report-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        table.report-table thead tr {
            background-color: #1e3a8a;
            color: #ffffff;
            text-align: left;
        }

        table.report-table th, table.report-table td {
            padding: 10px 12px;
            border: 1px solid #cbd5e1;
        }

        table.report-table tbody tr:nth-child(even) {
            background-color: #f8fafc;
        }

        table.report-table tbody tr:hover {
            background-color: #f1f5f9;
        }

        .badge-status {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 9999px;
            font-size: 11px;
            font-weight: 600;
        }
        .badge-instock {
            background: #dcfce7;
            color: #15803d;
        }
        .badge-outstock {
            background: #fee2e2;
            color: #b91c1c;
        }

        .text-right { text-align: right; }
        .text-center { text-align: center; }

        /* Chữ ký phê duyệt */
        .signature-section {
            display: flex;
            justify-content: space-between;
            margin-top: 40px;
            padding-top: 20px;
            page-break-inside: avoid;
        }

        .signature-box {
            text-align: center;
            width: 200px;
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
            margin-top: 3px;
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

        /* Định dạng in ấn Print Media */
        @media print {
            body {
                background: white;
                padding: 0;
            }
            .report-container {
                box-shadow: none;
                border-radius: 0;
                padding: 0;
                max-width: 100%;
            }
            .no-print-toolbar {
                display: none !important;
            }
            table.report-table th {
                background-color: #1e3a8a !important;
                color: #ffffff !important;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }
            .stat-card {
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }
        }
    </style>
</head>
<body>

<div class="report-container">
    <!-- Thanh điều khiển & Bộ lọc (Ẩn khi in ra máy in hoặc PDF) -->
    <div class="no-print-toolbar">
        <form class="filter-form" action="${pageContext.request.contextPath}/report/sanpham" method="GET">
            <input type="text" name="keyword" placeholder="Tìm tên/mã SP..." value="${param.keyword}" style="width: 150px;">
            
            <select name="danhMucId">
                <option value="">-- Tất cả danh mục --</option>
                <c:forEach var="dm" items="${danhMucList}">
                    <option value="${dm.idDanhMuc}" ${param.danhMucId == dm.idDanhMuc.toString() ? 'selected' : ''}>
                        ${dm.tenDanhMuc}
                    </option>
                </c:forEach>
            </select>

            <select name="trangThai">
                <option value="all" ${param.trangThai == 'all' ? 'selected' : ''}>-- Tất cả trạng thái --</option>
                <option value="con-hang" ${param.trangThai == 'con-hang' ? 'selected' : ''}>Còn hàng (SL > 0)</option>
                <option value="het-hang" ${param.trangThai == 'het-hang' ? 'selected' : ''}>Hết hàng (SL = 0)</option>
            </select>

            <button type="submit" class="btn btn-primary">
                <i class="fas fa-filter"></i> Lọc
            </button>
            <a href="${pageContext.request.contextPath}/report/sanpham" class="btn btn-outline" title="Đặt lại bộ lọc">
                <i class="fas fa-redo"></i> Reset
            </a>
        </form>

        <div class="toolbar-group">
            <button onclick="window.print()" class="btn btn-success">
                <i class="fas fa-print"></i> In Báo Cáo
            </button>
            <button onclick="exportToExcel()" class="btn btn-outline">
                <i class="fas fa-file-excel"></i> Xuất CSV/Excel
            </button>
            <a href="${pageContext.request.contextPath}/fe/adminSanPham.html" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Quay Lại Quản Lý
            </a>
        </div>
    </div>

    <!-- Header Báo Cáo -->
    <div class="report-header">
        <div class="company-info">
            <h2>Tech<span>No</span> Store</h2>
            <p><i class="fas fa-map-marker-alt"></i> Hệ thống Bán Lẻ Thiết Bị Công Nghệ & Điện Tử</p>
            <p><i class="fas fa-phone"></i> Hotline: 1900-8888 | Website: techno.vn</p>
        </div>
        <div class="report-title-section">
            <h1>BÁO CÁO THỐNG KÊ SẢN PHẨM</h1>
            <div class="report-meta">
                <p><strong>Mã báo cáo:</strong> BC-SP-${maBaoCao}</p>
                <p><strong>Ngày lập:</strong> ${ngayLap}</p>
                <p><strong>Người lập:</strong> ${nguoiLap != null ? nguoiLap : 'Ban Quản Trị Hệ Thống'}</p>
            </div>
        </div>
    </div>

    <!-- Tóm tắt bộ lọc áp dụng -->
    <div class="active-filters">
        <span><i class="fas fa-info-circle"></i> <strong>Điều kiện thống kê:</strong></span>
        <span>Từ khóa: <strong>${not empty param.keyword ? param.keyword : 'Tất cả'}</strong></span> |
        <span>Danh mục: <strong>${not empty tenDanhMucDaChon ? tenDanhMucDaChon : 'Toàn bộ'}</strong></span> |
        <span>Tình trạng tồn: <strong>
            <c:choose>
                <c:when test="${param.trangThai == 'con-hang'}">Còn hàng</c:when>
                <c:when test="${param.trangThai == 'het-hang'}">Hết hàng</c:when>
                <c:otherwise>Tất cả</c:otherwise>
            </c:choose>
        </strong></span>
    </div>

    <!-- Thẻ Thống Kê Tổng Hợp -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Tổng Số Mặt Hàng</div>
            <div class="stat-value">${tongSoSanPham} <span style="font-size: 13px; font-weight: normal; color: #64748b;">mẫu</span></div>
            <div class="stat-sub">Đang lưu hành trong danh mục</div>
        </div>
        <div class="stat-card green">
            <div class="stat-label">Tổng Số Lượng Tồn Kho</div>
            <div class="stat-value"><fmt:formatNumber value="${tongSoLuongTon}" type="number" /> <span style="font-size: 13px; font-weight: normal; color: #64748b;">sản phẩm</span></div>
            <div class="stat-sub">Sẵn sàng cung ứng</div>
        </div>
        <div class="stat-card yellow">
            <div class="stat-label">Tổng Giá Trị Tồn Kho</div>
            <div class="stat-value" style="color: #d97706;"><fmt:formatNumber value="${tongGiaTriTonKho}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></div>
            <div class="stat-sub">Ước tính theo giá niêm yết</div>
        </div>
        <div class="stat-card red">
            <div class="stat-label">Sản Phẩm Hết Hàng</div>
            <div class="stat-value" style="color: #dc2626;">${soSanPhamHetHang} <span style="font-size: 13px; font-weight: normal; color: #64748b;">mẫu</span></div>
            <div class="stat-sub">Cần nhập thêm hàng ngay</div>
        </div>
    </div>

    <!-- Bảng Danh Sách Sản Phẩm -->
    <div class="table-responsive">
        <table class="report-table" id="dataTable">
            <thead>
                <tr>
                    <th class="text-center" style="width: 45px;">STT</th>
                    <th class="text-center" style="width: 65px;">Mã SP</th>
                    <th>Tên Sản Phẩm</th>
                    <th style="width: 140px;">Danh Mục</th>
                    <th class="text-right" style="width: 120px;">Đơn Giá (VNĐ)</th>
                    <th class="text-center" style="width: 85px;">Tồn Kho</th>
                    <th class="text-right" style="width: 135px;">Thành Tiền (Tồn)</th>
                    <th class="text-center" style="width: 95px;">Tình Trạng</th>
                    <th class="text-center no-print-toolbar" style="width: 60px;">Chi tiết</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty sanPhams}">
                        <tr>
                            <td colspan="9" class="text-center" style="padding: 25px; color: #94a3b8; font-style: italic;">
                                Không có sản phẩm nào phù hợp với điều kiện tìm kiếm.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="sp" items="${sanPhams}" varStatus="status">
                            <tr>
                                <td class="text-center">${status.index + 1}</td>
                                <td class="text-center"><strong>#${sp.idSp}</strong></td>
                                <td>
                                    <strong>${sp.tenSanPham}</strong>
                                    <c:if test="${not empty sp.moTaNgan}">
                                        <div style="font-size: 11px; color: #64748b; margin-top: 2px;">${sp.moTaNgan}</div>
                                    </c:if>
                                </td>
                                <td>${sp.tenDanhMuc}</td>
                                <td class="text-right">
                                    <fmt:formatNumber value="${sp.giaBan}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </td>
                                <td class="text-center font-weight-bold">
                                    <strong>${sp.soLuong}</strong>
                                </td>
                                <td class="text-right font-weight-bold">
                                    <fmt:formatNumber value="${sp.giaBan * sp.soLuong}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${sp.soLuong > 0}">
                                            <span class="badge-status badge-instock">Còn hàng</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-status badge-outstock">Hết hàng</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center no-print-toolbar">
                                    <a href="${pageContext.request.contextPath}/report/sanpham/${sp.idSp}" target="_blank" title="Xem chi tiết & in mẫu đơn này" style="color: #2563eb;">
                                        <i class="fas fa-print"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
            <tfoot>
                <tr style="background: #f1f5f9; font-weight: bold;">
                    <td colspan="5" class="text-right">TỔNG CỘNG:</td>
                    <td class="text-center"><fmt:formatNumber value="${tongSoLuongTon}" type="number" /></td>
                    <td class="text-right" style="color: #d97706;">
                        <fmt:formatNumber value="${tongGiaTriTonKho}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                    </td>
                    <td colspan="2"></td>
                </tr>
            </tfoot>
        </table>
    </div>

    <!-- Khu Vực Ký Tên Khi In Báo Cáo -->
    <div class="signature-section">
        <div class="signature-box">
            <div class="role">Người Lập Báo Cáo</div>
            <div class="sub">(Ký và ghi rõ họ tên)</div>
            <div class="space"></div>
            <div class="name">${nguoiLap != null ? nguoiLap : 'Nhân viên quản lý'}</div>
        </div>
        <div class="signature-box">
            <div class="role">Thủ Kho</div>
            <div class="sub">(Xác nhận số lượng tồn)</div>
            <div class="space"></div>
            <div class="name">Bộ phận Quản lý kho</div>
        </div>
        <div class="signature-box">
            <div class="role">Kế Toán Trưởng</div>
            <div class="sub">(Kiểm duyệt định mức giá)</div>
            <div class="space"></div>
            <div class="name">Phòng Tài chính - Kế toán</div>
        </div>
        <div class="signature-box">
            <div class="role">Giám Đốc / Trưởng Cửa Hàng</div>
            <div class="sub">(Phê duyệt & Đóng dấu)</div>
            <div class="space"></div>
            <div class="name">Ban Giám Đốc</div>
        </div>
    </div>
</div>

<script>
    // Hàm xuất dữ liệu bảng ra file CSV Excel
    function exportToExcel() {
        const table = document.getElementById("dataTable");
        let csv = [];
        for (let i = 0; i < table.rows.length; i++) {
            let row = [], cols = table.rows[i].querySelectorAll("td, th");
            // Bỏ cột cuối (nút chi tiết)
            for (let j = 0; j < cols.length - 1; j++) {
                let data = cols[j].innerText.replace(/(\r\n|\n|\r)/gm, "").trim();
                data = data.replace(/"/g, '""');
                row.push('"' + data + '"');
            }
            csv.push(row.join(","));
        }
        const csvContent = "\uFEFF" + csv.join("\n");
        const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = "BaoCao_DanhSach_SanPham_" + new Date().toISOString().slice(0,10) + ".csv";
        link.click();
    }
</script>
</body>
</html>

