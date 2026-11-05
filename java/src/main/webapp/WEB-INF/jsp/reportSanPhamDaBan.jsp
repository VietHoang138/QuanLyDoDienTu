<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Báo Cáo Thống Kê Sản Phẩm Đã Bán & Doanh Thu - TechNo</title>
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
            max-width: 1200px;
            margin: 0 auto;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
            overflow: hidden;
            padding: 30px;
        }

        /* Toolbar điều khiển (ẩn khi in) */
        div.no-print-toolbar {
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
            border-color: #7c3aed;
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
            background-color: #7c3aed;
            color: white;
        }
        .btn-primary:hover {
            background-color: #6d28d9;
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
            background-color: #ffffff;
            color: #334155;
            border: 1px solid #cbd5e1;
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
            margin-bottom: 20px;
        }

        .company-info h2 {
            font-size: 24px;
            font-weight: 800;
            color: #0f172a;
            margin-bottom: 4px;
        }
        .company-info h2 span { color: #7c3aed; }
        .company-info p {
            font-size: 12px;
            color: #64748b;
            margin-top: 2px;
        }

        .report-title-section {
            text-align: right;
        }

        .report-title-section h1 {
            font-size: 20px;
            font-weight: 800;
            color: #5b21b6;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 6px;
        }

        .report-meta {
            font-size: 12px;
            color: #64748b;
            line-height: 1.5;
        }

        /* Thẻ thống kê KPI */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 15px;
            margin-bottom: 25px;
        }

        .stat-card {
            background-color: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 15px;
            border-left: 4px solid #7c3aed;
        }
        .stat-card.green { border-left-color: #10b981; }
        .stat-card.blue { border-left-color: #0284c7; }
        .stat-card.orange { border-left-color: #f97316; }

        .stat-label {
            font-size: 12px;
            color: #64748b;
            font-weight: 500;
            margin-bottom: 4px;
        }

        .stat-value {
            font-size: 19px;
            font-weight: 700;
            color: #0f172a;
        }

        /* Tóm tắt bộ lọc */
        .active-filters {
            background-color: #faf5ff;
            border: 1px solid #e9d5ff;
            border-radius: 6px;
            padding: 10px 15px;
            margin-bottom: 20px;
            font-size: 12.5px;
            color: #6b21a8;
            display: flex;
            align-items: center;
            gap: 15px;
            flex-wrap: wrap;
        }

        /* Bảng Báo Cáo */
        table.report-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 12.5px;
            margin-bottom: 30px;
        }

        table.report-table th, table.report-table td {
            border: 1px solid #cbd5e1;
            padding: 10px 12px;
            text-align: left;
        }

        table.report-table th {
            background-color: #5b21b6;
            color: #ffffff;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 11.5px;
            letter-spacing: 0.5px;
        }

        table.report-table tbody tr:nth-child(even) {
            background-color: #f8fafc;
        }

        table.report-table tbody tr:hover {
            background-color: #f3e8ff;
        }

        .text-center { text-align: center !important; }
        .text-right { text-align: right !important; }

        .badge-id {
            background: #ede9fe;
            color: #6d28d9;
            padding: 2px 6px;
            border-radius: 4px;
            font-weight: 700;
            font-size: 11px;
        }

        .badge-qty {
            background: #ede9fe;
            color: #6d28d9;
            padding: 3px 8px;
            border-radius: 12px;
            font-weight: 700;
            font-size: 12px;
            display: inline-block;
        }

        .total-amount {
            color: #059669;
            font-weight: 700;
        }

        .table-thumb {
            width: 40px;
            height: 40px;
            object-fit: cover;
            border-radius: 6px;
            border: 1px solid #e2e8f0;
        }

        .btn-print-row-link {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 30px;
            height: 30px;
            border-radius: 6px;
            background-color: #ede9fe;
            color: #7c3aed;
            border: 1px solid #ddd6fe;
            text-decoration: none;
            transition: all 0.2s;
        }
        .btn-print-row-link:hover {
            background-color: #7c3aed;
            color: #ffffff;
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
            height: 65px;
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
            .report-container { box-shadow: none; border-radius: 0; padding: 0; max-width: 100%; }
            .no-print-toolbar, .col-no-print { display: none !important; }
            table.report-table th {
                background-color: #5b21b6 !important;
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
    <!-- Toolbar (Ẩn khi in) -->
    <div class="no-print-toolbar">
        <form class="filter-form" action="${pageContext.request.contextPath}/report/sanpham/da-ban" method="GET">
            <input type="text" name="keyword" placeholder="Tìm tên/mã SP..." value="${param.keyword}" style="width: 160px;">
            
            <select name="danhMucId">
                <option value="">-- Tất cả danh mục --</option>
                <c:forEach var="dm" items="${danhMucList}">
                    <option value="${dm.idDanhMuc}" ${param.danhMucId == dm.idDanhMuc.toString() ? 'selected' : ''}>
                        ${dm.tenDanhMuc}
                    </option>
                </c:forEach>
            </select>

            <button type="submit" class="btn btn-primary">
                <i class="fas fa-filter"></i> Lọc dữ liệu
            </button>
            <a href="${pageContext.request.contextPath}/report/sanpham/da-ban" class="btn btn-outline" title="Đặt lại bộ lọc">
                <i class="fas fa-redo"></i> Reset
            </a>
        </form>

        <div class="toolbar-group">
            <button onclick="window.print()" class="btn btn-success">
                <i class="fas fa-print"></i> In Báo Cáo (A4)
            </button>
            <button onclick="exportToExcel()" class="btn btn-outline">
                <i class="fas fa-file-excel"></i> Xuất CSV/Excel
            </button>
            <a href="javascript:void(0)" onclick="if (window.opener && !window.opener.closed) { window.opener.focus(); window.close(); } else { window.history.back(); }" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Quay Lại Quản Lý
            </a>
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
            <h1>BÁO CÁO DOANH THU & SẢN PHẨM ĐÃ BÁN</h1>
            <div class="report-meta">
                <p><strong>Mã báo cáo:</strong> BC-${maBaoCao}</p>
                <p><strong>Thời gian lập:</strong> ${ngayLap}</p>
                <p><strong>Người lập:</strong> Ban Quản Trị Hệ Thống TechNo</p>
            </div>
        </div>
    </div>

    <!-- Tóm tắt bộ lọc áp dụng -->
    <div class="active-filters">
        <span><i class="fas fa-info-circle"></i> <strong>Điều kiện thống kê:</strong></span>
        <span>Từ khóa: <strong>${not empty param.keyword ? param.keyword : 'Tất cả'}</strong></span> |
        <span>Danh mục: <strong>${not empty tenDanhMucDaChon ? tenDanhMucDaChon : 'Toàn bộ danh mục'}</strong></span>
    </div>

    <!-- Thẻ Thống Kê Tổng Hợp -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Số Mặt Hàng Đã Bán</div>
            <div class="stat-value">${tongSoMatHang} <small style="font-size:12px;color:#64748b;">mặt hàng</small></div>
        </div>
        <div class="stat-card green">
            <div class="stat-label">Tổng Doanh Thu Đã Bán</div>
            <div class="stat-value" style="color:#059669;">
                <fmt:formatNumber value="${tongDoanhThu}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
            </div>
        </div>
        <div class="stat-card blue">
            <div class="stat-label">Tổng Lượng Sản Phẩm Bán Ra</div>
            <div class="stat-value">${tongSoLuongDaBan} <small style="font-size:12px;color:#64748b;">chiếc</small></div>
        </div>
        <div class="stat-card orange">
            <div class="stat-label">Đơn Hàng Phát Sinh</div>
            <div class="stat-value">${tongSoDonHang} <small style="font-size:12px;color:#64748b;">đơn</small></div>
        </div>
    </div>

    <!-- Bảng Dữ Liệu Sản Phẩm Đã Bán -->
    <table class="report-table" id="reportSoldTable">
        <thead>
            <tr>
                <th style="width: 45px;" class="text-center">STT</th>
                <th style="width: 75px;" class="text-center">Mã SP</th>
                <th style="width: 60px;" class="text-center">Hình ảnh</th>
                <th>Tên sản phẩm</th>
                <th style="width: 120px;">Danh mục</th>
                <th style="width: 110px;" class="text-right">Đơn giá niêm yết</th>
                <th style="width: 90px;" class="text-center">Số lượng bán</th>
                <th style="width: 125px;" class="text-right">Tổng tiền thu</th>
                <th style="width: 90px;" class="text-center">Tồn kho còn</th>
                <th style="width: 80px;" class="text-center">Số đơn</th>
                <th style="width: 50px;" class="text-center col-no-print">Chi tiết</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty sanPhams}">
                    <tr>
                        <td colspan="11" class="text-center" style="padding: 40px; color: #94a3b8;">
                            <i class="fas fa-inbox" style="font-size: 30px; display: block; margin-bottom: 8px;"></i>
                            Không có sản phẩm đã bán nào phù hợp với điều kiện tìm kiếm.
                        </td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="sp" items="${sanPhams}" varStatus="status">
                        <tr>
                            <td class="text-center">${status.index + 1}</td>
                            <td class="text-center">
                                <span class="badge-id">#SP<fmt:formatNumber value="${sp.idSp}" pattern="000"/></span>
                            </td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${not empty sp.hinhAnh and (sp.hinhAnh.startsWith('http') or sp.hinhAnh.startsWith('/'))}">
                                        <img src="${sp.hinhAnh}" alt="${sp.tenSanPham}" class="table-thumb" onerror="this.src='https://placehold.co/100x100?text=SP'">
                                    </c:when>
                                    <c:when test="${not empty sp.hinhAnh}">
                                        <img src="${pageContext.request.contextPath}/api/images/${sp.hinhAnh}" alt="${sp.tenSanPham}" class="table-thumb" onerror="this.src='https://placehold.co/100x100?text=SP'">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="https://placehold.co/100x100?text=SP" alt="No image" class="table-thumb">
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <strong>${sp.tenSanPham}</strong>
                            </td>
                            <td>${not empty sp.tenDanhMuc ? sp.tenDanhMuc : 'Thiết bị'}</td>
                            <td class="text-right">
                                <fmt:formatNumber value="${sp.giaBan}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </td>
                            <td class="text-center">
                                <span class="badge-qty">${sp.soLuongDaBan} cái</span>
                            </td>
                            <td class="text-right total-amount">
                                <fmt:formatNumber value="${sp.tongTienBan}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${sp.soLuongTonKho > 0}">
                                        <span style="color:#059669;font-weight:600;">Còn ${sp.soLuongTonKho}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color:#dc2626;font-weight:600;">Hết hàng</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <strong>${not empty sp.danhSachDonHang ? sp.danhSachDonHang.size() : (not empty sp.soDonHang ? sp.soDonHang : 0)}</strong>
                            </td>
                            <td class="text-center col-no-print">
                                <a href="${pageContext.request.contextPath}/report/sanpham/${sp.idSp}" target="_blank" class="btn-print-row-link" title="In phiếu chi tiết sản phẩm này">
                                    <i class="fas fa-print"></i>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
        <c:if test="${not empty sanPhams}">
            <tfoot>
                <tr style="background-color: #f8fafc; font-weight: 700;">
                    <td colspan="6" class="text-right" style="padding: 12px;">TỔNG CỘNG DOANH THU ĐÃ BÁN:</td>
                    <td class="text-center" style="color: #6d28d9; font-size: 13.5px;">${tongSoLuongDaBan} cái</td>
                    <td class="text-right total-amount" style="font-size: 14px;">
                        <fmt:formatNumber value="${tongDoanhThu}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                    </td>
                    <td colspan="3"></td>
                </tr>
            </tfoot>
        </c:if>
    </table>

    <!-- Chữ ký người có trách nhiệm -->
    <div class="signature-section">
        <div class="signature-box">
            <div class="role">Người Lập Báo Cáo</div>
            <div class="sub">(Ký, ghi rõ họ tên)</div>
            <div class="space"></div>
            <div class="name">Nguyễn Văn Quản Trị</div>
        </div>
        <div class="signature-box">
            <div class="role">Kế Toán Bán Hàng</div>
            <div class="sub">(Ký, ghi rõ họ tên)</div>
            <div class="space"></div>
            <div class="name">Trần Thị Thu Ngân</div>
        </div>
        <div class="signature-box">
            <div class="role">Giám Đốc Bán Lẻ</div>
            <div class="sub">(Ký, đóng dấu)</div>
            <div class="space"></div>
            <div class="name">TechNo Store Manager</div>
        </div>
    </div>
</div>

<script>
    function exportToExcel() {
        var table = document.getElementById("reportSoldTable");
        var rows = table.querySelectorAll("tr");
        var csv = [];
        for (var i = 0; i < rows.length; i++) {
            var row = [], cols = rows[i].querySelectorAll("td, th");
            for (var j = 0; j < cols.length - 1; j++) {
                var text = cols[j].innerText.replace(/"/g, '""').trim();
                row.push('"' + text + '"');
            }
            csv.push(row.join(","));
        }
        var csvContent = "\uFEFF" + csv.join("\n");
        var blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
        var link = document.createElement("a");
        var url = URL.createObjectURL(blob);
        link.setAttribute("href", url);
        link.setAttribute("download", "BaoCao_SanPhamDaBan_" + new Date().toISOString().slice(0,10) + ".csv");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
</script>

</body>
</html>

