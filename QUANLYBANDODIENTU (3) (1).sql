USE master;
GO

-- Drop database if it exists and no connections are active
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'QUANLYBANDODIENTU')
BEGIN
    ALTER DATABASE [QUANLYBANDODIENTU] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE [QUANLYBANDODIENTU];
END;
GO

CREATE DATABASE [QUANLYBANDODIENTU];
GO

USE [QUANLYBANDODIENTU];
GO

-- TRẠNG THÁI THANH TOÁN
CREATE TABLE TRANG_THAI_THANH_TOAN (
    IdTttt       INT IDENTITY(1,1) PRIMARY KEY,
    TenTrangThai NVARCHAR(100) NOT NULL
);
GO

-- PHƯƠNG THỨC THANH TOÁN
CREATE TABLE PHUONG_THUC_THANH_TOAN (
    IdPttt    INT IDENTITY(1,1) PRIMARY KEY,
    TenPttt   NVARCHAR(100)  NOT NULL,
    TrangThai BIT            DEFAULT(1)
);
GO

-- TRẠNG THÁI ĐƠN HÀNG
CREATE TABLE TRANG_THAI_DON_HANG (
    IdTtdh       INT IDENTITY(1,1) PRIMARY KEY,
    TenTrangThai NVARCHAR(100) NOT NULL
);
GO


-- PHƯƠNG THỨC VẬN CHUYỂN
CREATE TABLE PHUONG_THUC_VAN_CHUYEN (
    IdPtvc    INT IDENTITY(1,1) PRIMARY KEY,
    TenPtvc   NVARCHAR(100)  NOT NULL,
    PhiCoBan  DECIMAL(18,2),
    TrangThai BIT            DEFAULT(1)
);
GO

-- DANH MỤC SẢN PHẨM
CREATE TABLE DANH_MUC (
    IdDanhMuc   INT IDENTITY(1,1) PRIMARY KEY,
    TenDanhMuc  NVARCHAR(100)  NOT NULL UNIQUE,
    TrangThai   BIT            DEFAULT(1)
);
GO


-- THƯƠNG HIỆU
CREATE TABLE THUONG_HIEU (
    IdThuongHieu  INT IDENTITY(1,1)    PRIMARY KEY,
    TenThuongHieu NVARCHAR(100) NOT NULL UNIQUE,
    QuocGia       NVARCHAR(100) NOT NULL
);
GO

-- DANH MỤC CHA CHO SẢN PHẨM
CREATE TABLE SAN_PHAM_DANHMUCCHA (
    IdDanhMucCha   INT IDENTITY(1,1)  PRIMARY KEY,
    TenDanhMucCha  NVARCHAR(255)
);
GO

-- MÀU SẮC
CREATE TABLE SANPHAM_MAUSAC(
    IdMau     INT IDENTITY(1,1)   PRIMARY KEY,
    TenMau    NVARCHAR(255) NOT NULL
);
GO

-- DUNG LƯỢNG
CREATE TABLE SANPHAM_DUNGLUONG(
    IdDungLuong INT IDENTITY(1,1)   PRIMARY KEY,
    DungLuong   NVARCHAR(255) NOT NULL
);
GO

-- SẢN PHẨM
CREATE TABLE SAN_PHAM (
    IdSP           INT IDENTITY(1,1) PRIMARY KEY,
    TenSanPham     NVARCHAR(200)  NOT NULL,
    MoTaNgan       NVARCHAR(255)  NULL,
    GiaBan         DECIMAL(18,2)  CHECK (GiaBan >= 0),
    SoLuong        INT            CHECK (SoLuong >= 0),
    DanhMucChaId   INT NULL,
    DanhMucConId   INT NULL,
    ThuongHieuId   INT NULL,
    BaoHanh        NVARCHAR(100)  NULL,
    CanNang        NVARCHAR(50)   NULL,
    KichThuoc      NVARCHAR(100)  NULL,
    MauSac         INT NULL,
    DungLuong      INT NULL,
    TrangThai      BIT            NOT NULL DEFAULT 1,

    CONSTRAINT FK_SP_DanhMucCha
        FOREIGN KEY (DanhMucChaId)
        REFERENCES SAN_PHAM_DANHMUCCHA(IdDanhMucCha)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    CONSTRAINT FK_SP_DanhMuc
        FOREIGN KEY (DanhMucConId)
        REFERENCES DANH_MUC(IdDanhMuc)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    CONSTRAINT FK_SP_ThuongHieu
        FOREIGN KEY (ThuongHieuId)
        REFERENCES THUONG_HIEU(IdThuongHieu)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    CONSTRAINT FK_SP_MauSac
        FOREIGN KEY (MauSac)
        REFERENCES SANPHAM_MAUSAC(IdMau)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    CONSTRAINT FK_SP_DungLuong
        FOREIGN KEY (DungLuong)
        REFERENCES SANPHAM_DUNGLUONG(IdDungLuong)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);
GO

-- KHÁCH HÀNG
CREATE TABLE KHACH_HANG (
    IdKH             INT IDENTITY(1,1)     PRIMARY KEY,
    HoTen            NVARCHAR(150)  NOT NULL,
    Email            NVARCHAR(150)  NOT NULL UNIQUE,
    MatKhau          NVARCHAR(255)  NOT NULL,
    Sdt              NVARCHAR(20)   UNIQUE,
    DiaChi           NVARCHAR(255),
    DiemTichLuy      INT            DEFAULT(0),
    TongChiTieu      DECIMAL(18,2)  DEFAULT(0),
    TrangThai        BIT            DEFAULT(1),
    NgayDangKy       DATETIME       DEFAULT GETDATE()
);
GO

-- ĐỊA CHỈ GIAO HÀNG
CREATE TABLE DIA_CHI_GIAO_HANG (
    IdDiaChi       INT IDENTITY(1,1)   PRIMARY KEY,
    KhachHangId    INT ,
    HoTenNguoiNhan NVARCHAR(150),
    SoDienThoai    VARCHAR(20),
    DiaChiChiTiet  NVARCHAR(255),
    CONSTRAINT FK_DCGH_KH FOREIGN KEY (KhachHangId)
        REFERENCES KHACH_HANG(IdKH)
        ON DELETE CASCADE ON UPDATE CASCADE
);
GO

-- THÔNG BÁO
CREATE TABLE THONG_BAO (
    IdThongBao INT IDENTITY(1,1) PRIMARY KEY,
    TieuDe     NVARCHAR(255),
    NoiDung    NVARCHAR(1000),
    NgayGui    DATETIME DEFAULT GETDATE(),
    IdKH       INT NULL,
    DaDoc      BIT DEFAULT 0,
    CONSTRAINT FK_TB_KH FOREIGN KEY (IdKH)
        REFERENCES KHACH_HANG(IdKH)
        ON DELETE CASCADE ON UPDATE CASCADE
);
GO

-- MÃ KHUYẾN MÃI
CREATE TABLE MA_KHUYEN_MAI (
    MaKM        INT IDENTITY(1,1) PRIMARY KEY,
    MoTa        NVARCHAR(100),
    GiaTri      DECIMAL(5,2),
    NgayBatDau  DATE,
    NgayKetThuc DATE,
    SoLuong     INT,
    TrangThai   BIT DEFAULT 1,
    CONSTRAINT CHK_KM_Ngay CHECK (NgayBatDau <= NgayKetThuc),
    CONSTRAINT CHK_KM_GiaTri CHECK (GiaTri >= 0)
);
GO

-- ĐƠN HÀNG
CREATE TABLE DON_HANG (
    IdDonHang        INT IDENTITY(1,1)     PRIMARY KEY,
    KhachHangId      INT NULL,
    TongTienSanPham  DECIMAL(18,2),
    PhiVanChuyen     DECIMAL(18,2),
    TongTienGiamGia  DECIMAL(18,2) NULL,
    TongThanhToan    DECIMAL(18,2),
    NgayDat          DATETIME       DEFAULT GETDATE(),
    TrangThaiId      INT NULL,
    PtttId           INT NULL,
    PtvcId           INT NULL,
    DiaChiGiaoHangId INT NULL,
    GhiChuKhachHang  NVARCHAR(255),
	ThongTinKhachHang NVARCHAR(255),
    MaKM             INT NULL,
    CONSTRAINT FK_DH_KH FOREIGN KEY (KhachHangId)
        REFERENCES KHACH_HANG(IdKH)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_DH_TTDH FOREIGN KEY (TrangThaiId)
        REFERENCES TRANG_THAI_DON_HANG(IdTtdh)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_DH_PTTT FOREIGN KEY (PtttId)
        REFERENCES PHUONG_THUC_THANH_TOAN(IdPttt)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_DH_PTVC FOREIGN KEY (PtvcId)
        REFERENCES PHUONG_THUC_VAN_CHUYEN(IdPtvc)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_DH_DCGH FOREIGN KEY (DiaChiGiaoHangId)
        REFERENCES DIA_CHI_GIAO_HANG(IdDiaChi)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_DH_MaKM FOREIGN KEY (MaKM)
        REFERENCES MA_KHUYEN_MAI(MaKM)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT CHK_DH_TongTienSanPham CHECK (TongTienSanPham >= 0),
    CONSTRAINT CHK_DH_PhiVanChuyen CHECK (PhiVanChuyen >= 0),
    CONSTRAINT CHK_DH_TongThanhToan CHECK (TongThanhToan >= 0)
);
GO

-- CHI TIẾT ĐƠN HÀNG
CREATE TABLE CHI_TIET_DON_HANG (
    IdChiTietDonHang INT IDENTITY(1,1)     PRIMARY KEY,
    DonHangId        INT NULL,
    SanPhamId        INT NULL,
    SoLuong          INT NULL,
    GiaBan           DECIMAL(18,2),
    GiaGiam          DECIMAL(18,2),
    ThanhTien        DECIMAL(18,2),
    CONSTRAINT FK_CTDH_DH FOREIGN KEY (DonHangId)
        REFERENCES DON_HANG(IdDonHang)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_CTDH_SP FOREIGN KEY (SanPhamId)
        REFERENCES SAN_PHAM(IdSP)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT CHK_CTDH_SoLuong CHECK (SoLuong > 0),
    CONSTRAINT CHK_CTDH_GiaBan CHECK (GiaBan >= 0),
    CONSTRAINT CHK_CTDH_ThanhTien CHECK (ThanhTien >= 0)
);
GO

-- LỊCH SỬ ĐƠN HÀNG
CREATE TABLE LICH_SU_DON_HANG (
    IdLsDonHang INT IDENTITY(1,1)     PRIMARY KEY,
    DonHangId   INT NULL,
    TrangThaiId INT NULL,
    NgayTaoDon  DATETIME       DEFAULT GETDATE(),
    CONSTRAINT FK_LSDH_DH   FOREIGN KEY (DonHangId)
        REFERENCES DON_HANG(IdDonHang)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_LSDH_TTDH FOREIGN KEY (TrangThaiId)
        REFERENCES TRANG_THAI_DON_HANG(IdTtdh)
        ON DELETE NO ACTION ON UPDATE NO ACTION
);
GO

-- HÓA ĐƠN
CREATE TABLE HOA_DON (
    IdHoaDon          INT IDENTITY(1,1)     PRIMARY KEY,
    DonHangId         INT NULL,
    KhachHangId       INT NULL,
    TongTien          DECIMAL(18,2),
    TienThue          DECIMAL(18,2),
    NgayTaoDon        DATETIME       DEFAULT GETDATE(),
    TrangThaiId       INT NULL,
    HinhThucThanhToan NVARCHAR(100),
    IdPttt            INT NULL,
    MaKM              INT NULL,
    CONSTRAINT FK_HD_DH   FOREIGN KEY (DonHangId)
        REFERENCES DON_HANG(IdDonHang)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_HD_KH   FOREIGN KEY (KhachHangId)
        REFERENCES KHACH_HANG(IdKH)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_HD_TTTT FOREIGN KEY (TrangThaiId)
        REFERENCES TRANG_THAI_THANH_TOAN(IdTttt)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_HD_PTTT FOREIGN KEY (IdPttt)
        REFERENCES PHUONG_THUC_THANH_TOAN(IdPttt)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_HD_MaKM FOREIGN KEY (MaKM)
        REFERENCES MA_KHUYEN_MAI(MaKM)
        ON DELETE NO ACTION ON UPDATE NO ACTION
);
GO

-- PHIẾU GIAO HÀNG
CREATE TABLE PHIEU_GIAO_HANG (
    IdPgh          INT IDENTITY(1,1)     PRIMARY KEY,
    DonHangId      INT NULL,
    NgayTao        DATETIME       DEFAULT GETDATE(),
    NgayGiaoDuKien DATETIME,
    NguoiGiaoHang  NVARCHAR(150),
    SdtNguoiGiao   VARCHAR(20),
    GhiChu         NVARCHAR(255),
    CONSTRAINT FK_PGH_DH FOREIGN KEY (DonHangId)
        REFERENCES DON_HANG(IdDonHang)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT CK_SoDienThoai_Valid CHECK (
        SdtNguoiGiao LIKE '0%' AND 
        LEN(SdtNguoiGiao) BETWEEN 10 AND 11 AND 
        SdtNguoiGiao NOT LIKE '%[^0-9]%'
    )
);
GO

-- YÊU THÍCH
CREATE TABLE YEUTHICH (
    IdKH     INT,
    IdSP     INT,
    NgayThem DATE DEFAULT GETDATE(),
    PRIMARY KEY (IdKH, IdSP),
    CONSTRAINT FK_YT_KH FOREIGN KEY (IdKH)
        REFERENCES KHACH_HANG(IdKH)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_YT_SP FOREIGN KEY (IdSP)
        REFERENCES SAN_PHAM(IdSP)
        ON DELETE CASCADE ON UPDATE CASCADE
);
GO

-- ĐÁNH GIÁ SẢN PHẨM
CREATE TABLE DANH_GIA_SAN_PHAM (
    IdDG         INT IDENTITY(1,1)   PRIMARY KEY,
    IdSP         INT NULL,
    IdKH         INT NULL,
    DiemDanhGia  INT          CHECK (DiemDanhGia BETWEEN 1 AND 5),
    BinhLuan     NVARCHAR(500),
    NgayDanhGia  DATE         DEFAULT GETDATE(),
    CONSTRAINT FK_DGSP_SP FOREIGN KEY (IdSP)
        REFERENCES SAN_PHAM(IdSP)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_DGSP_KH FOREIGN KEY (IdKH)
        REFERENCES KHACH_HANG(IdKH)
        ON DELETE CASCADE ON UPDATE CASCADE
);
GO

-- THÔNG SỐ KỸ THUẬT
CREATE TABLE THONG_SO_KY_THUAT (
    IdThongSo INT IDENTITY(1,1)     PRIMARY KEY,
    SanPhamId INT NULL,
    MoTa      NVARCHAR(100),
    CONSTRAINT FK_TSKT_SP FOREIGN KEY (SanPhamId)
        REFERENCES SAN_PHAM(IdSP)
        ON DELETE CASCADE ON UPDATE CASCADE
);
GO

-- HÌNH ẢNH SẢN PHẨM
CREATE TABLE SAN_PHAM_HINHANH (
    IdAnh     INT IDENTITY(1,1) PRIMARY KEY,
    IdSP      INT NOT NULL,
    LienKet   VARCHAR(255) NOT NULL,      
    MoTa      NVARCHAR(255) NULL,

    CONSTRAINT FK_SPH_SP FOREIGN KEY (IdSP)
        REFERENCES SAN_PHAM(IdSP)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
GO


-- TÀI KHOẢN
CREATE TABLE TAI_KHOAN (
    TenDangNhap VARCHAR(50)   PRIMARY KEY,
    MatKhau     VARCHAR(100)  NOT NULL,
    IdKH        INT           NULL,
    VaiTro      NVARCHAR(20)  NOT NULL
                 CONSTRAINT CK_TAI_KHOAN_VaiTro 
                 CHECK (VaiTro IN (N'admin', N'customer')),
    CONSTRAINT FK_TK_KH FOREIGN KEY (IdKH)
        REFERENCES KHACH_HANG(IdKH)
        ON DELETE CASCADE 
        ON UPDATE CASCADE
);
GO


-- ĐĂNG KÝ TÀI KHOẢN
CREATE TABLE DANG_KY_TAI_KHOAN (
    IdDangKy    INT IDENTITY(1,1) PRIMARY KEY,
    TenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    Email       NVARCHAR(150) NOT NULL,
    MatKhau     NVARCHAR(255) NOT NULL,
    Sdt         NVARCHAR(20),
    MaXacThuc   VARCHAR(10),
    NgayYeuCau  DATETIME DEFAULT GETDATE(),
    TrangThai   VARCHAR(20) NOT NULL DEFAULT 'Đang chờ',
    CONSTRAINT UQ_DK_TenDangNhap UNIQUE(TenDangNhap),
    CONSTRAINT UQ_DK_Email UNIQUE(Email),
    CONSTRAINT CHK_DK_TrangThai CHECK (TrangThai IN ('Đang chờ','Đã xác nhận','Đã huỷ')),
    CONSTRAINT FK_DK_TK FOREIGN KEY (TenDangNhap)
        REFERENCES TAI_KHOAN(TenDangNhap)
        ON DELETE CASCADE ON UPDATE CASCADE
);
GO

-- LỊCH SỬ SỬ DỤNG KHUYẾN MÃI
CREATE TABLE LICH_SU_SU_DUNG_KM (
    IdLsKM     INT IDENTITY(1,1) PRIMARY KEY,
    MaKM       INT NULL,
    DonHangId  INT NULL,
    IdKH       INT NULL,
    NgaySuDung DATETIME DEFAULT GETDATE(),
    GhiChu     NVARCHAR(255),
    CONSTRAINT FK_LSSDKM_MaKM FOREIGN KEY (MaKM)
        REFERENCES MA_KHUYEN_MAI(MaKM)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_LSSDKM_DonHang FOREIGN KEY (DonHangId)
        REFERENCES DON_HANG(IdDonHang)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_LSSDKM_KH FOREIGN KEY (IdKH)
        REFERENCES KHACH_HANG(IdKH)
        ON DELETE CASCADE ON UPDATE CASCADE
);
GO

-- THEO DÕI GIAO HÀNG
CREATE TABLE THEO_DOI_GIAO_HANG (
    IdTheoDoi INT IDENTITY(1,1) PRIMARY KEY,
    IdDonHang INT NULL,
    TrangThai NVARCHAR(100),
    ThoiGian  DATETIME DEFAULT GETDATE(),
    GhiChu    NVARCHAR(255),
    IdTtdh    INT NOT NULL DEFAULT (0),
    CONSTRAINT FK_TDOGH_DH FOREIGN KEY (IdDonHang)
        REFERENCES DON_HANG(IdDonHang)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_TDOGH_TTDH FOREIGN KEY (IdTtdh)
        REFERENCES TRANG_THAI_DON_HANG(IdTtdh)
        ON DELETE NO ACTION ON UPDATE NO ACTION
);
GO
------------------------------INSERT INTO--------------------------------------
-- TRẠNG THÁI THANH TOÁN
INSERT INTO TRANG_THAI_THANH_TOAN(TenTrangThai)
VALUES
    (N'Chưa thanh toán'),
    (N'Đã thanh toán'),
    (N'Hoàn tiền');
GO

-- PHƯƠNG THỨC THANH TOÁN
INSERT INTO PHUONG_THUC_THANH_TOAN(TenPttt, TrangThai)
VALUES
    (N'Tiền mặt',       1),
    (N'Thẻ tín dụng',   1),
    (N'Chuyển khoản',   1),
    (N'Ví điện tử',      1);
GO

-- TRẠNG THÁI ĐƠN HÀNG
INSERT INTO TRANG_THAI_DON_HANG (TenTrangThai)
VALUES
    (N'Chờ xử lý'),
    (N'Đang xử lý'),
    (N'Đã giao'),
    (N'Hoàn thành'),
    (N'Đã hủy');
GO

-- PHƯƠNG THỨC VẬN CHUYỂN
INSERT INTO PHUONG_THUC_VAN_CHUYEN (TenPtvc, PhiCoBan, TrangThai)
VALUES
    (N'Giao hàng tiêu chuẩn',  30000, 1),
    (N'Giao hàng nhanh',       50000, 1),
    (N'Giao hàng hỏa tốc',     80000, 1);
GO


-- DANH MỤC SẢN PHẨM
INSERT INTO DANH_MUC (TenDanhMuc, TrangThai)
VALUES
    (N'Điện thoại', 1),
    (N'Máy tính bảng', 1),
    (N'Máy ảnh', 1),
    (N'Tivi', 1),
    (N'Laptop', 1),
    (N'Thiết bị đeo', 1);
GO


-- THƯƠNG HIỆU
INSERT INTO THUONG_HIEU (TenThuongHieu, QuocGia)
VALUES
    (N'Apple',        N'Mỹ'),
    (N'Samsung',      N'Hàn Quốc'),
    (N'Sony',         N'Nhật Bản'),
    (N'Xiaomi',       N'Trung Quốc'),
    (N'OPPO',         N'Trung Quốc'),
    (N'OnePlus',      N'Trung Quốc'),
    (N'Google',       N'Mỹ'),
    (N'Dell',         N'Mỹ'),
    (N'HP',           N'Mỹ'),
    (N'Canon',        N'Nhật Bản'),
    (N'Nikon',        N'Nhật Bản'),
    (N'Fujifilm',     N'Nhật Bản'),
    (N'Garmin',       N'Mỹ'),
    (N'Fitbit',       N'Mỹ'),
    (N'Amazfit',      N'Trung Quốc'),
    (N'Polar',        N'Phần Lan'),
    (N'Suunto',       N'Phần Lan'),
    (N'Withings',     N'Pháp'),
    (N'Lenovo',       N'Trung Quốc'),
    (N'Microsoft',    N'Mỹ'),
    (N'Huawei',       N'Trung Quốc'),
    (N'Amazon',       N'Mỹ'),
    (N'Realme',       N'Trung Quốc'),
    (N'ASUS',         N'Đài Loan'),
    (N'Acer',         N'Đài Loan'),
    (N'MSI',          N'Đài Loan'),
    (N'LG',           N'Hàn Quốc'),
    (N'Panasonic',    N'Nhật Bản'),
    (N'Olympus',      N'Nhật Bản'),
    (N'Philips',      N'Hà Lan'),
    (N'Sharp',        N'Nhật Bản'),
    (N'Hisense',      N'Trung Quốc'),
    (N'Skyworth',     N'Trung Quốc'),
    (N'JVC',          N'Nhật Bản'),
    (N'Leica',        N'Đức'),
    (N'GoPro',        N'Mỹ'),
    (N'Razer',        N'Singapore');
GO


-- DANH MỤC CHA CHO SẢN PHẨM
INSERT INTO SAN_PHAM_DANHMUCCHA (TenDanhMucCha)
VALUES
    (N'Điện thoại & Tablet'),
    (N'Laptop & TV');
GO

-- Bảng màu sắc
INSERT INTO SANPHAM_MAUSAC (TenMau) VALUES
  (N'Đen'),
  (N'Trắng'),
  (N'Bạc'),
  (N'Xám'),
  (N'Vàng'),
  (N'Đỏ'),
  (N'Xanh'),
  (N'Hồng'),
  (N'Graphite'),
  (N'Silver'),
  (N'Cream'),
  (N'Obsidian'),
  (N'Bay'),
  (N'Midnight Black'),
  (N'Space Gray'),
  (N'Starlight');
GO

-- Bảng dung lượng
INSERT INTO SANPHAM_DUNGLUONG (DungLuong) VALUES
  (N'64GB'),
  (N'128GB'),
  (N'256GB'),
  (N'512GB'),
  (N'1TB'),
  (N'2TB'),
  (N'4TB');
GO

-- Bảng sản phẩm
INSERT INTO SAN_PHAM
  (TenSanPham, MoTaNgan, GiaBan, SoLuong,
   DanhMucChaId, DanhMucConId, ThuongHieuId, BaoHanh,
   CanNang, KichThuoc, MauSac, DungLuong)
VALUES
  (N'Apple iPhone 15',          N'Smartphone cao cấp của Apple',   27990000, 50, 1, 1, 1, N'12 tháng', '0.201', '14.78 x 7.15 x 0.77', 1, 2),
  (N'Apple iPhone 15 Plus',     N'Phiên bản Plus với màn hình lớn',32990000, 50, 1, 1, 1, N'12 tháng', '0.201', '15.78 x 7.77 x 0.77', 1, 2),
  (N'Apple iPhone 15 Pro',      N'Model Pro với camera nâng cao',   37990000, 50, 1, 1, 1, N'12 tháng', '0.187', '14.67 x 7.15 x 0.77', 1, 2),
  (N'Samsung Galaxy S24',       N'Smartphone flagship của Samsung', 24990000, 50, 1, 1, 2, N'12 tháng', '0.168', '14.69 x 7.15 x 0.78', 1, 2),
  (N'Samsung Galaxy S24+',      N'Phiên bản Plus với pin lớn hơn',  28990000, 50, 1, 1, 2, N'12 tháng', '0.197', '15.83 x 7.57 x 0.78', 1, 2),
  (N'Samsung Galaxy S24 Ultra', N'Ultra với camera 200 MP',         36990000, 50, 1, 1, 2, N'12 tháng', '0.233', '16.38 x 7.54 x 0.78', 1, 2),
  (N'Apple iPad Pro 12.9″ (M4)', N'Máy tính bảng cao cấp Apple',    33990000, 20, 1, 2, 1, N'12 tháng', '0.684', '28.06 x 21.49 x 0.61', 1, 2),
  (N'Apple iPad Air 10.9″ (M2)', N'Máy tính bảng mỏng nhẹ Apple',    21990000, 20, 1, 2, 1, N'12 tháng', '0.461', '24.7 x 17.85 x 0.61', 1, 2),
  (N'Apple iPad 10.2″ (2024)',   N'Máy tính bảng phổ thông Apple',    10490000, 20, 1, 2, 1, N'12 tháng', '0.487', '25.06 x 17.41 x 0.75', 1, 2),
  (N'Samsung Galaxy Tab S9',     N'Máy tính bảng Android cao cấp',    21990000, 20, 1, 2, 2, N'12 tháng', '0.502', '25.05 x 17.64 x 0.57', 1, 2),
  (N'Samsung Galaxy Tab S9+',    N'Phiên bản Plus màn hình lớn',      25990000, 20, 1, 2, 2, N'12 tháng', '0.575', '28.41 x 18.49 x 0.57', 1, 2),
  (N'Samsung Galaxy Tab S9 Ultra',N'Ultra với màn hình 14.6″',       30990000, 20, 1, 2, 2, N'12 tháng', '0.732', '32.76 x 21.17 x 0.57', 1, 2),
  (N'Canon EOS R6 II',      N'Mirrorless full-frame Canon',    49990000, 10, 1, 3, 10, N'12 tháng', '0.62', '14 x 10 x 9', 1, 1),
  (N'Sony A7 IV',            N'Mirrorless Sony full-frame',     47990000, 12, 1, 3, 3, N'12 tháng', '0.65', '14 x 10 x 9', 1, 1),
  (N'Nikon Z6 II',           N'Mirrorless Nikon full-frame',    39990000,  8, 1, 3, 11, N'12 tháng', '0.59', '14 x 10 x 8', 1, 1),
  (N'Fujifilm X-T5',         N'Mirrorless APS-C Fujifilm',      26990000, 15, 1, 3, 12, N'12 tháng', '0.53', '13 x 9 x 7', 1, 1),
  (N'Panasonic Lumix GH6',   N'Micro Four Thirds Panasonic',   33990000,  7, 1, 3, 28, N'12 tháng', '0.82', '14 x 10 x 9', 1, 1),
  (N'Canon EOS 90D',         N'DSLR APS-C Canon',               23990000, 10, 1, 3, 10, N'12 tháng', '0.71', '14 x 11 x 8', 1, 1),
  (N'Samsung QLED Q80C 55″',       N'Tivi QLED 4K Samsung',       17990000, 15, 2, 4, 2, N'24 tháng', '14.5', '123 x 71 x 5', 1, 1),
  (N'LG OLED C3 65″',              N'Tivi OLED LG',               24990000, 10, 2, 4, 27, N'24 tháng', '18.5', '144 x 83 x 5', 1, 1),
  (N'Sony Bravia X90K 50″',        N'Tivi Bravia 4K Sony',        15990000, 12, 2, 4, 3, N'24 tháng', '13.5', '112 x 65 x 6', 1, 1),
  (N'Xiaomi Mi TV P1 43″',         N'Tivi Smart giá tốt Xiaomi',  6490000, 20, 2, 4, 4, N'24 tháng', '8.7', '97 x 57 x 7', 1, 1),
  (N'TCL C725 QLED 55″',           N'Tivi QLED TCL',              9490000, 10, 2, 4, 37, N'24 tháng', '15', '123 x 71 x 5', 1, 1),
  (N'Philips Ambilight 55PUS8807', N'Ambilight 4K Philips',       10990000,  8, 2, 4, 30, N'24 tháng', '16', '124 x 72 x 6', 1, 1),
  (N'Apple MacBook Air M2 13″',      N'Ultralight với chip M2',           29990000, 20, 2, 5, 1, N'12 tháng', '1.24', '30.41 x 21.24 x 1.13', 1, 4),
  (N'Apple MacBook Pro 14″ M2 Pro',   N'Hiệu năng cao với M2 Pro',         52990000, 15, 2, 5, 1, N'12 tháng', '1.57', '31.26 x 22.12 x 1.55', 1, 5),
  (N'Dell XPS 13 (9340)',             N'Siêu mỏng – viền siêu mỏng',       38990000, 25, 2, 5, 8, N'12 tháng', '1.23', '29.57 x 19.9 x 1.48', 1, 4),
  (N'HP Spectre x360 14″',            N'2-in-1 xoay gập 360°',              41990000, 10, 2, 5, 9, N'12 tháng', '1.36', '31.5 x 21.8 x 1.69', 1, 4),
  (N'Lenovo ThinkPad X1 Carbon Gen 11',N'Business ultrabook bền bỉ',        43990000, 18, 2, 5, 19, N'12 tháng', '1.12', '31.2 x 22.2 x 1.5', 1, 4),
  (N'ASUS ZenBook 14 OLED',           N'Màn hình OLED – siêu mỏng nhẹ',    24990000, 22, 2, 5, 24, N'12 tháng', '1.39', '31.9 x 21.2 x 1.49', 1, 4),
  (N'Apple Watch Series 9',      N'Smartwatch Apple',         11990000, 20, 2, 6, 1, N'12 tháng', '0.03', '4.1 x 3.1 x 1.1', 1, 1),
  (N'Samsung Galaxy Watch 6',    N'Smartwatch Samsung',       7990000, 20, 2, 6, 2, N'12 tháng', '0.04', '4.2 x 3.2 x 1.2', 1, 1),
  (N'Garmin Forerunner 265',     N'GPS running watch Garmin',11990000, 15, 2, 6, 13, N'12 tháng', '0.03', '4.0 x 4.0 x 1.1', 1, 1),
  (N'Fitbit Charge 5',           N'Fitness tracker Fitbit',   2990000, 25, 2, 6, 14, N'12 tháng', '0.02', '3.9 x 2.0 x 1.1', 1, 1),
  (N'Huawei Watch GT 3',         N'Smartwatch Huawei',        4690000, 18, 2, 6, 21, N'12 tháng', '0.04', '4.4 x 3.8 x 1.1', 1, 1),
  (N'Amazfit GTR 4',             N'Smartwatch Amazfit',       3490000, 20, 2, 6, 15, N'12 tháng', '0.05', '4.5 x 4.2 x 1.2', 1, 1);
GO
-- KHÁCH HÀNG
INSERT INTO KHACH_HANG (HoTen, Email, MatKhau, Sdt, DiaChi, DiemTichLuy, TongChiTieu, TrangThai, NgayDangKy)
VALUES
    (N'Nguyễn Minh Tuấn', 'tuannguyen@gmail.com', 'matkhau1', '0901234567', N'123 Lê Duẩn, Hải Châu, Đà Nẵng', 120, 15900000, 1, GETDATE()),
    (N'Phạm Thị Mai', 'maipham@gmail.com', 'matkhau2', '0912345678', N'456 Trần Phú, Hải Châu, Đà Nẵng', 340, 58900000, 1, GETDATE()),
    (N'Lê Văn An', 'lean@gmail.com', 'matkhau3', '0923456789', N'789 Nguyễn Văn Linh, Hải Châu, Đà Nẵng', 80, 9900000, 1, GETDATE()),
    (N'Võ Thị Thu Hằng', 'hangvo@gmail.com', 'matkhau4', '0934567890', N'321 Điện Biên Phủ, Thanh Khê, Đà Nẵng', 50, 5900000, 1, GETDATE()),
    (N'Trần Nhật Nam', 'namtran@gmail.com', 'matkhau5', '0945678901', N'654 Ông Ích Khiêm, Hải Châu, Đà Nẵng', 250, 29900000, 1, GETDATE()),
    (N'Đặng Thanh Trúc', 'trucdang@gmail.com', 'matkhau6', '0956789012', N'11 Hoàng Hoa Thám, Thanh Khê, Đà Nẵng', 60, 7900000, 1, GETDATE()),
    (N'Huỳnh Quốc Dũng', 'dunghuynh@gmail.com', 'matkhau7', '0967890123', N'22 Nguyễn Hữu Thọ, Hải Châu, Đà Nẵng', 420, 79900000, 1, GETDATE()),
    (N'Ngô Minh Châu', 'chaungo@gmail.com', 'matkhau8', '0978901234', N'33 Hàm Nghi, Thanh Khê, Đà Nẵng', 30, 4900000, 1, GETDATE()),
    (N'Bùi Hoàng Anh', 'anhbui@gmail.com', 'matkhau9', '0989012345', N'44 Lê Thanh Nghị, Ngũ Hành Sơn, Đà Nẵng', 500, 105900000, 1, GETDATE()),
    (N'Đỗ Thị Kim Ngân', 'ngandtk@gmail.com', 'matkhau10', '0990123456', N'55 Nguyễn Tri Phương, Thanh Khê, Đà Nẵng', 70, 11900000, 1, GETDATE());
GO


-- ĐỊA CHỈ GIAO HÀNG
INSERT INTO DIA_CHI_GIAO_HANG (KhachHangId, HoTenNguoiNhan, SoDienThoai, DiaChiChiTiet)
VALUES
    (1, N'Nguyễn Văn A', '0905123456', N'123 Nguyễn Văn Linh, Quận Hải Châu, Đà Nẵng'),
    (2, N'Trần Thị B', '0916234567', N'45 Lê Duẩn, Quận Hải Châu, Đà Nẵng'),
    (3, N'Lê Văn C', '0927345678', N'78 Nguyễn Hữu Thọ, Quận Hải Châu, Đà Nẵng'),
    (4, N'Phạm Thị D', '0938456789', N'50 Trần Phú, Quận Hải Châu, Đà Nẵng'),
    (5, N'Hoàng Văn E', '0949567890', N'12 Hoàng Diệu, Quận Hải Châu, Đà Nẵng');
GO

-- THÔNG BÁO
INSERT INTO THONG_BAO (TieuDe, NoiDung, NgayGui, IdKH, DaDoc)
VALUES
    (N'Chương trình Mùa Hè Vui Vẻ', 
     N'Giảm 15% cho mọi đơn hàng từ 5 triệu trở lên trong tháng 6. Thời gian áp dụng: 01/06–30/06.', 
     '2025-06-01 09:00:00', 1, 0),
    (N'Đơn hàng DH0001 đã giao thành công', 
     N'Đơn hàng DH0001 của bạn đã được giao thành công vào ngày 02/05. Cảm ơn bạn đã tin tưởng!', 
     '2025-05-02 15:30:00', 1, 1),
    (N'Nhắc nạp điểm tích lũy', 
     N'Tài khoản của bạn hiện có 120 điểm. Thêm 50 điểm cho đơn hàng tiếp theo từ 1 triệu trở lên.', 
     '2025-05-10 10:00:00', 1, 0),
    (N'Ưu đãi dành cho khách VIP', 
     N'Khách VIP được giảm thêm 5% cho tất cả sản phẩm trong tuần này. Đăng nhập để nhận mã.', 
     '2025-05-05 08:30:00', 2, 0),
    (N'Đơn hàng DH0002 đang trên đường giao', 
     N'Đơn hàng DH0002 của bạn sẽ đến trong 1–2 ngày tới. Vui lòng sẵn sàng nhận hàng.', 
     '2025-05-03 13:45:00', 2, 1);
GO

-- MÃ KHUYẾN MÃI
INSERT INTO MA_KHUYEN_MAI (MoTa, GiaTri, NgayBatDau, NgayKetThuc, SoLuong, TrangThai)
VALUES
    (N'Giảm 10% cho đơn hàng Laptop', 10.00, '2025-05-01', '2025-05-31', 50, 1),
    (N'Giảm 5% cho điện thoại Samsung', 5.00, '2025-05-01', '2025-05-20', 30, 1),
    (N'Giảm 7% cho phụ kiện gaming', 7.00, '2025-05-10', '2025-06-10', 40, 1),
    (N'Tặng 100k khi mua TV từ 10 triệu', 100.00, '2025-05-01', '2025-06-01', 20, 1),
    (N'Miễn phí giao hàng đơn từ 500k', 0.00, '2025-05-01', '2025-12-31', 100, 1);
GO


-- ĐƠN HÀNG
INSERT INTO DON_HANG (KhachHangId, TongTienSanPham, PhiVanChuyen, TongTienGiamGia, TongThanhToan, NgayDat, TrangThaiId, PtttId, PtvcId, DiaChiGiaoHangId, GhiChuKhachHang, MaKM)
VALUES
    (1, 27990000, 30000, 2300000, 20703000, '2025-05-01 10:15:00', 1, 1, 1, 1, N'Giao giờ hành chính', 1),
    (2, 32990000, 25000, 525000, 10072500, '2025-05-02 14:30:00', 2, 2, 2, 2, N'Gọi trước khi giao', 2),
    (3, 37990000, 20000, NULL, 4820000, '2025-05-03 09:45:00', 1, 1, 1, 3, N'Không giao buổi tối', NULL),
    (4, 24990000, 15000, 224000, 2986000, '2025-05-04 11:00:00', 3, 3, 2, 4, N'Để hàng tại quầy bảo vệ', 3),
    (5, 28990000, 30000, 375000, 12175000, '2025-05-05 16:20:00', 1, 1, 1, 5, N'Không cần gấp', 4);
GO

-- CHI TIẾT ĐƠN HÀNG
INSERT INTO CHI_TIET_DON_HANG(SanPhamId, SoLuong, GiaBan, GiaGiam, ThanhTien)
VALUES
    (1, 1, 27990000, 2300000, 20700000),
    (2, 1, 32990000, 525000, 9975000),
    (3, 1, 37990000, 0, 4800000),
    (4, 1, 24990000, 224000, 2976000),
    (5, 1, 28990000, 375000, 12125000);
GO

-- LỊCH SỬ ĐƠN HÀNG
INSERT INTO LICH_SU_DON_HANG(TrangThaiId, NgayTaoDon)
VALUES
    (1, '2025-05-01 10:15:00'),
    (2, '2025-05-02 14:30:00'),
    (1, '2025-05-03 09:45:00'),
    (3, '2025-05-04 11:00:00'),
    (1, '2025-05-05 16:20:00');
GO

-- HÓA ĐƠN
INSERT INTO HOA_DON (KhachHangId, TongTien, TienThue, NgayTaoDon, TrangThaiId, HinhThucThanhToan, IdPttt, MaKM)
VALUES
    (1, 20703000, 2070300, '2025-05-01 10:15:00', 1, N'Thanh toán khi nhận hàng', 1, 1),
    (2, 10072500, 1007250, '2025-05-02 14:30:00', 2, N'Chuyển khoản ngân hàng', 2, 2),
    (3, 4820000, 482000, '2025-05-03 09:45:00', 1, N'Thanh toán khi nhận hàng', 1, NULL),
    (4, 2986000, 298600, '2025-05-04 11:00:00', 3, N'Ví điện tử', 3, 3),
    (5, 12175000, 1217500, '2025-05-05 16:20:00', 1, N'Thanh toán khi nhận hàng', 1, 4);
GO

-- PHIẾU GIAO HÀNG
INSERT INTO PHIEU_GIAO_HANG(NgayTao, NgayGiaoDuKien, NguoiGiaoHang, SdtNguoiGiao, GhiChu)
VALUES
    ('2025-05-01 10:20:00', '2025-05-02 14:00:00', N'Nguyễn Văn Tài', '0901234501', N'Giao trong giờ hành chính'),
    ('2025-05-02 14:35:00', '2025-05-03 15:30:00', N'Lê Thị Hoa',     '0912345602', N'Gọi trước khi giao'),
    ('2025-05-03 09:50:00', '2025-05-04 11:00:00', N'Phạm Minh Khoa', '0923456703', N'Không giao buổi tối'),
    ('2025-05-04 11:05:00', '2025-05-05 10:00:00', N'Võ Thị Anh',    '0934567804', N'Để tại quầy bảo vệ'),
    ('2025-05-05 16:25:00', '2025-05-06 13:00:00', N'Đặng Quốc Huy', '0945678905', N'Giao nhanh nếu có thể');
GO

-- YÊU THÍCH
INSERT INTO YEUTHICH (IdKH, IdSP, NgayThem)
VALUES
    (1, 1, '2025-05-01'),
    (1, 3, '2025-05-02'),
    (2, 2, '2025-05-01'),
    (2, 4, '2025-05-03'),
    (3, 5, '2025-05-02'),
    (3, 1, '2025-05-04'),
    (4, 7, '2025-05-03'),
    (5, 3, '2025-05-05'),
    (6, 8, '2025-05-04'),
    (7, 2, '2025-05-06');
GO

-- ĐÁNH GIÁ SẢN PHẨM
INSERT INTO DANH_GIA_SAN_PHAM (IdSP, IdKH, DiemDanhGia, BinhLuan, NgayDanhGia)
VALUES
    (1, 1, 5, N'Chất lượng rất tốt, máy chạy mượt mà.', '2025-05-08'),
    (2, 2, 4, N'Máy đẹp, pin ổn nhưng hơi nóng khi sử dụng lâu.', '2025-05-09'),
    (3, 3, 5, N'Âm thanh trong trẻo, khử ồn hiệu quả.', '2025-05-10'),
    (4, 4, 4, N'Loa nhỏ gọn, bass ổn nhưng tiếng mid chưa thật sự rõ.', '2025-05-11'),
    (5, 5, 5, N'Hình ảnh sắc nét, màu sắc trung thực.', '2025-05-12');
GO

-- HÌNH ẢNH SẢN PHẨM 
INSERT INTO SAN_PHAM_HINHANH (IdSP, LienKet, MoTa)
VALUES
  (1, 'SP1_1.jpg', N'Hình ảnh sản phẩm SP1 - mặt trước'),
  (1, 'SP1_2.jpg', N'Hình ảnh sản phẩm SP1 - góc nghiêng'),
  (1, 'SP1_3.jpg', N'Hình ảnh sản phẩm SP1 - mặt sau'),
  (2, 'SP2_1.jpg', N'Hình ảnh SP2 - ảnh 1'),
  (2, 'SP2_2.jpg', N'Hình ảnh SP2 - ảnh 2'),
  (2, 'SP2_3.jpg', N'Hình ảnh SP2 - ảnh 3'),
  (3, 'SP3_1.jpg', N'Hình ảnh SP3 - ảnh 1'),
  (3, 'SP3_2.jpg', N'Hình ảnh SP3 - ảnh 2'),
  (3, 'SP3_3.jpg', N'Hình ảnh SP3 - ảnh 3'),
  (4, 'SP4_1.jpg', N'Hình ảnh SP4 - ảnh 1'),
  (4, 'SP4_2.jpg', N'Hình ảnh SP4 - ảnh 2'),
  (4, 'SP4_3.jpg', N'Hình ảnh SP4 - ảnh 3'),
  (5, 'SP5_1.jpg', N'Hình ảnh SP5 - ảnh 1'),
  (5, 'SP5_2.jpg', N'Hình ảnh SP5 - ảnh 2'),
  (5, 'SP5_3.jpg', N'Hình ảnh SP5 - ảnh 3'),
  (6, 'SP6_1.jpg', N'Hình ảnh SP6 - ảnh 1'),
  (6, 'SP6_2.jpg', N'Hình ảnh SP6 - ảnh 2'),
  (6, 'SP6_3.jpg', N'Hình ảnh SP6 - ảnh 3'),
  (7, 'SP7_1.jpg', N'Hình ảnh SP7 - ảnh 1'),
  (7, 'SP7_2.jpg', N'Hình ảnh SP7 - ảnh 2'),
  (7, 'SP7_3.jpg', N'Hình ảnh SP7 - ảnh 3'),
  (8, 'SP8_1.jpg', N'Hình ảnh SP8 - ảnh 1'),
  (8, 'SP8_2.jpg', N'Hình ảnh SP8 - ảnh 2'),
  (8, 'SP8_3.jpg', N'Hình ảnh SP8 - ảnh 3'),
  (9, 'SP9_1.jpg', N'Hình ảnh SP9 - ảnh 1'),
  (9, 'SP9_2.jpg', N'Hình ảnh SP9 - ảnh 2'),
  (9, 'SP9_3.jpg', N'Hình ảnh SP9 - ảnh 3'),
  (10, 'SP10_1.jpg', N'Hình ảnh SP10 - ảnh 1'),
  (10, 'SP10_2.jpg', N'Hình ảnh SP10 - ảnh 2'),
  (10, 'SP10_3.jpg', N'Hình ảnh SP10 - ảnh 3'),
  (11, 'SP11_1.jpg', N'Hình ảnh SP11 - ảnh 1'),
  (11, 'SP11_2.jpg', N'Hình ảnh SP11 - ảnh 2'),
  (11, 'SP11_3.jpg', N'Hình ảnh SP11 - ảnh 3'),
  (12, 'SP12_1.jpg', N'Hình ảnh SP12 - ảnh 1'),
  (12, 'SP12_2.jpg', N'Hình ảnh SP12 - ảnh 2'),
  (12, 'SP12_3.jpg', N'Hình ảnh SP12 - ảnh 3'),
  (13, 'SP13_1.jpg', N'Hình ảnh SP13 - ảnh 1'),
  (13, 'SP13_2.jpg', N'Hình ảnh SP13 - ảnh 2'),
  (13, 'SP13_3.jpg', N'Hình ảnh SP13 - ảnh 3'),
  (14, 'SP14_1.jpg', N'Hình ảnh SP14 - ảnh 1'),
  (14, 'SP14_2.jpg', N'Hình ảnh SP14 - ảnh 2'),
  (14, 'SP14_3.jpg', N'Hình ảnh SP14 - ảnh 3'),
  (15, 'SP15_1.jpg', N'Hình ảnh SP15 - ảnh 1'),
  (15, 'SP15_2.jpg', N'Hình ảnh SP15 - ảnh 2'),
  (15, 'SP15_3.jpg', N'Hình ảnh SP15 - ảnh 3'),
  (16, 'SP16_1.jpg', N'Hình ảnh SP16 - ảnh 1'),
  (16, 'SP16_2.jpg', N'Hình ảnh SP16 - ảnh 2'),
  (16, 'SP16_3.jpg', N'Hình ảnh SP16 - ảnh 3'),
  (17, 'SP17_1.jpg', N'Hình ảnh SP17 - ảnh 1'),
  (17, 'SP17_2.jpg', N'Hình ảnh SP17 - ảnh 2'),
  (17, 'SP17_3.jpg', N'Hình ảnh SP17 - ảnh 3'),
  (18, 'SP18_1.jpg', N'Hình ảnh SP18 - ảnh 1'),
  (18, 'SP18_2.jpg', N'Hình ảnh SP18 - ảnh 2'),
  (18, 'SP18_3.jpg', N'Hình ảnh SP18 - ảnh 3'),
  (19, 'SP19_1.jpg', N'Hình ảnh SP19 - ảnh 1'),
  (19, 'SP19_2.jpg', N'Hình ảnh SP19 - ảnh 2'),
  (19, 'SP19_3.jpg', N'Hình ảnh SP19 - ảnh 3'),
  (20, 'SP20_1.jpg', N'Hình ảnh SP20 - ảnh 1'),
  (20, 'SP20_2.jpg', N'Hình ảnh SP20 - ảnh 2'),
  (20, 'SP20_3.jpg', N'Hình ảnh SP20 - ảnh 3'),
  (21, 'SP21_1.jpg', N'Hình ảnh SP21 - ảnh 1'),
  (21, 'SP21_2.jpg', N'Hình ảnh SP21 - ảnh 2'),
  (21, 'SP21_3.jpg', N'Hình ảnh SP21 - ảnh 3'),
  (22, 'SP22_1.jpg', N'Hình ảnh SP22 - ảnh 1'),
  (22, 'SP22_2.jpg', N'Hình ảnh SP22 - ảnh 2'),
  (22, 'SP22_3.jpg', N'Hình ảnh SP22 - ảnh 3'),
  (23, 'SP23_1.jpg', N'Hình ảnh SP23 - ảnh 1'),
  (23, 'SP23_2.jpg', N'Hình ảnh SP23 - ảnh 2'),
  (23, 'SP23_3.jpg', N'Hình ảnh SP23 - ảnh 3'),
  (24, 'SP24_1.jpg', N'Hình ảnh SP24 - ảnh 1'),
  (24, 'SP24_2.jpg', N'Hình ảnh SP24 - ảnh 2'),
  (24, 'SP24_3.jpg', N'Hình ảnh SP24 - ảnh 3'),
  (25, 'SP25_1.jpg', N'Hình ảnh SP25 - ảnh 1'),
  (25, 'SP25_2.jpg', N'Hình ảnh SP25 - ảnh 2'),
  (25, 'SP25_3.jpg', N'Hình ảnh SP25 - ảnh 3'),
  (26, 'SP26_1.jpg', N'Hình ảnh SP26 - ảnh 1'),
  (26, 'SP26_2.jpg', N'Hình ảnh SP26 - ảnh 2'),
  (26, 'SP26_3.jpg', N'Hình ảnh SP26 - ảnh 3'),
  (27, 'SP27_1.jpg', N'Hình ảnh SP27 - ảnh 1'),
  (27, 'SP27_2.jpg', N'Hình ảnh SP27 - ảnh 2'),
  (27, 'SP27_3.jpg', N'Hình ảnh SP27 - ảnh 3'),
  (28, 'SP28_1.jpg', N'Hình ảnh SP28 - ảnh 1'),
  (28, 'SP28_2.jpg', N'Hình ảnh SP28 - ảnh 2'),
  (28, 'SP28_3.jpg', N'Hình ảnh SP28 - ảnh 3'),
  (29, 'SP29_1.jpg', N'Hình ảnh SP29 - ảnh 1'),
  (29, 'SP29_2.jpg', N'Hình ảnh SP29 - ảnh 2'),
  (29, 'SP29_3.jpg', N'Hình ảnh SP29 - ảnh 3'),
  (30, 'SP30_1.jpg', N'Hình ảnh SP30 - ảnh 1'),
  (30, 'SP30_2.jpg', N'Hình ảnh SP30 - ảnh 2'),
  (30, 'SP30_3.jpg', N'Hình ảnh SP30 - ảnh 3'),
  (31, 'SP31_1.jpg', N'Hình ảnh SP31 - ảnh 1'),
  (31, 'SP31_2.jpg', N'Hình ảnh SP31 - ảnh 2'),
  (31, 'SP31_3.jpg', N'Hình ảnh SP31 - ảnh 3'),
  (32, 'SP32_1.jpg', N'Hình ảnh SP32 - ảnh 1'),
  (32, 'SP32_2.jpg', N'Hình ảnh SP32 - ảnh 2'),
  (32, 'SP32_3.jpg', N'Hình ảnh SP32 - ảnh 3'),
  (33, 'SP33_1.jpg', N'Hình ảnh SP33 - ảnh 1'),
  (33, 'SP33_2.jpg', N'Hình ảnh SP33 - ảnh 2'),
  (33, 'SP33_3.jpg', N'Hình ảnh SP33 - ảnh 3'),
  (34, 'SP34_1.jpg', N'Hình ảnh SP34 - ảnh 1'),
  (34, 'SP34_2.jpg', N'Hình ảnh SP34 - ảnh 2'),
  (34, 'SP34_3.jpg', N'Hình ảnh SP34 - ảnh 3'),
  (35, 'SP35_1.jpg', N'Hình ảnh SP35 - ảnh 1'),
  (35, 'SP35_2.jpg', N'Hình ảnh SP35 - ảnh 2'),
  (35, 'SP35_3.jpg', N'Hình ảnh SP35 - ảnh 3'),
  (36, 'SP36_1.jpg', N'Hình ảnh SP36 - ảnh 1'),
  (36, 'SP36_2.jpg', N'Hình ảnh SP36 - ảnh 2'),
  (36, 'SP36_3.jpg', N'Hình ảnh SP36 - ảnh 3');

-- THÔNG SỐ KỸ THUẬT
INSERT INTO THONG_SO_KY_THUAT (SanPhamId, MoTa)
VALUES
    (1, N'CPU Intel Core i5-1240P'),
    (1, N'RAM 16GB DDR4'),
    (1, N'SSD 512GB NVMe'),
    (2, N'Màn hình 6.1 inch Dynamic AMOLED'),
    (2, N'Chip Snapdragon 8 Gen 3'),
    (2, N'Pin 4000mAh, sạc nhanh 25W'),
    (3, N'Chống ồn chủ động ANC'),
    (3, N'Tuổi thọ pin 30 giờ'),
    (3, N'Cổng sạc USB-C'),
    (4, N'Công suất 40W'),
    (4, N'Kháng nước IP67'),
    (4, N'Kết nối Bluetooth 5.1'),
    (5, N'Độ phân giải 4K UHD (3840x2160)'),
    (5, N'WebOS Smart TV'),
    (5, N'Hỗ trợ HDR10+'),
    (6, N'Loại switch GX Blue'),
    (6, N'Kết nối USB-C có dây'),
    (6, N'Đèn nền RGB LIGHTSYNC'),
    (7, N'Cảm biến Focus+ 20K DPI'),
    (7, N'Tốc độ phản hồi 1ms'),
    (7, N'Kết nối không dây HyperSpeed'),
    (8, N'Màn hình Liquid Retina 10.9 inch'),
    (8, N'Chip Apple M1'),
    (8, N'Hỗ trợ Apple Pencil 2'),
    (9, N'Dung lượng 2TB'),
    (9, N'Kết nối USB 3.0'),
    (9, N'Mã hóa phần cứng 256-bit'),
    (10, N'Tần số quét 165Hz'),
    (10, N'Thời gian phản hồi 1ms'),
    (10, N'Độ phủ màu sRGB 99%');
GO



--27. TÀI KHOẢN
INSERT INTO TAI_KHOAN (TenDangNhap, MatKhau, IdKH, VaiTro)
VALUES
    ('tuannguyen',    'matkhau1',  1, N'customer'),
    ('phamthimai',    'matkhau2',  2, N'customer'),
    ('levanan',       'matkhau3',  3, N'customer'),
    ('vothithuhang',  'matkhau4',  4, N'customer'),
    ('trannhatnam',   'matkhau5',  5, N'customer'),
    ('dangthanhtruc', 'matkhau6',  6, N'customer'),
    ('huynhquocdung', 'matkhau7',  7, N'customer'),
    ('ngominhchau',   'matkhau8',  8, N'customer'),
    ('buihoanganh',   'matkhau9',  9, N'customer'),
    ('dothikimngan',  'matkhau10', 10, N'customer'),
	('admin1', 'secureAdminPwd', NULL, N'admin');
GO

-- 28. ĐĂNG KÝ TÀI KHOẢN
INSERT INTO DANG_KY_TAI_KHOAN(TenDangNhap, Email, MatKhau, Sdt, MaXacThuc, NgayYeuCau, TrangThai)
VALUES
    ('tuannguyen',       'tuannguyen@gmail.com',       'matkhau1', '0901234567', 'XM001', '2025-04-25 10:00:00', 'Đã xác nhận'),
    ('phamthimai',       'phamthimai@gmail.com',       'matkhau2', '0912345678', 'XM002', '2025-04-26 11:15:00', 'Đang chờ'),
    ('levanan',          'levanan@gmail.com',          'matkhau3', '0923456789', 'XM003', '2025-04-27 09:30:00', 'Đã huỷ'),
    ('vothithuhang',     'vothithuhang@gmail.com',     'matkhau4', '0934567890', 'XM004', '2025-05-01 14:20:00', 'Đã xác nhận'),
    ('trannhatnam',      'trannhatnam@gmail.com',      'matkhau5', '0945678901', 'XM005', '2025-05-02 16:45:00', 'Đang chờ'),
    ('dangthanhtruc',    'dangthanhtruc@gmail.com',    'matkhau6', '0956789012', 'XM006', '2025-05-03 08:10:00', 'Đã xác nhận'),
    ('huynhquocdung',    'huynhquocdung@gmail.com',    'matkhau7', '0967890123', 'XM007', '2025-05-04 13:55:00', 'Đã huỷ');
GO

-- 29. LỊCH SỬ SỬ DỤNG KHUYẾN MÃI
INSERT INTO LICH_SU_SU_DUNG_KM(MaKM, DonHangId, IdKH, NgaySuDung, GhiChu)
VALUES
    (1, 1, 1, '2025-05-01 10:15:00', N'Sử dụng mã 1 giảm 10%'),
    (2, 2, 2, '2025-05-02 14:30:00', N'Sử dụng mã 2 giảm 5%'),
    (3, 3, 3, '2025-05-04 11:00:00', N'Sử dụng mã 3 giảm 7%'),
    (4, 4, 4, '2025-05-05 16:20:00', N'Sử dụng mã 4 giảm 15%'),
    (5, 5, 5, '2025-05-07 10:00:00', N'Sử dụng mã 5 giảm 20%');
GO

-- 30. THEO DÕI GIAO HÀNG
INSERT INTO THEO_DOI_GIAO_HANG(IdDonHang, TrangThai, ThoiGian, GhiChu, IdTtdh)
VALUES
    (1, N'Chờ xác nhận',     '2025-05-01 10:15:00', N'Đơn hàng được tạo', 1),
    (1, N'Đang giao hàng',   '2025-05-02 09:00:00', N'Đối tác vận chuyển nhận hàng', 2),
    (2, N'Chờ xác nhận',     '2025-05-02 14:30:00', N'Đơn hàng được tạo', 1),
    (2, N'Đang giao hàng',   '2025-05-03 10:00:00', N'Nhân viên kho bàn giao hàng', 2),
    (3, N'Chờ xác nhận',     '2025-05-03 09:45:00', N'Đơn hàng được tạo', 1),
    (3, N'Đang giao hàng',   '2025-05-04 11:00:00', N'Đối tác vận chuyển nhận hàng', 2),
    (4, N'Chờ xác nhận',     '2025-05-04 11:00:00', N'Đơn hàng được tạo', 1),
    (4, N'Đang giao hàng',   '2025-05-05 10:00:00', N'Nhân viên kho bàn giao hàng', 2),
    (5, N'Chờ xác nhận',     '2025-05-05 16:20:00', N'Đơn hàng được tạo', 1),
    (5, N'Đang giao hàng',   '2025-05-06 08:30:00', N'Đối tác vận chuyển nhận hàng', 2);
GO

SELECT * FROM SAN_PHAM_DANHMUCCHA;
SELECT * FROM TRANG_THAI_THANH_TOAN;
SELECT * FROM PHUONG_THUC_THANH_TOAN;
SELECT * FROM TRANG_THAI_DON_HANG;
SELECT * FROM PHUONG_THUC_VAN_CHUYEN;
SELECT * FROM DANH_MUC;
SELECT * FROM THUONG_HIEU;
SELECT * FROM KHACH_HANG;
SELECT * FROM DIA_CHI_GIAO_HANG;
SELECT * FROM SAN_PHAM;
SELECT * FROM THONG_SO_KY_THUAT;
SELECT * FROM MA_KHUYEN_MAI;
SELECT * FROM DON_HANG;
SELECT * FROM CHI_TIET_DON_HANG;
SELECT * FROM LICH_SU_DON_HANG;
SELECT * FROM HOA_DON;
SELECT * FROM PHIEU_GIAO_HANG;
SELECT * FROM YEUTHICH;
SELECT * FROM DANH_GIA_SAN_PHAM;
SELECT * FROM THONG_BAO;
SELECT * FROM THEO_DOI_GIAO_HANG;
SELECT * FROM TAI_KHOAN;
SELECT * FROM DANG_KY_TAI_KHOAN;
SELECT * FROM LICH_SU_SU_DUNG_KM;
SELECT * FROM SAN_PHAM_HINHANH;
GO

-------------------------------------UPDATE-------------------------------------
-- 1. Cập nhật PhiVanChuyen cho DON_HANG
UPDATE dh
SET dh.PhiVanChuyen = pt.PhiCoBan
FROM DON_HANG AS dh
  JOIN PHUONG_THUC_VAN_CHUYEN AS pt
    ON dh.PtvcId = pt.IdPtvc
WHERE dh.PtvcId IS NOT NULL;
GO

-- 2. Cập nhật TongTienGiamGia cho DON_HANG
UPDATE dh
SET TongTienGiamGia =
    CASE
      -- Tặng 100k khi mua TV ...
      WHEN dh.MaKM = 4 THEN 100000
      -- Miễn phí giao hàng ...
      WHEN dh.MaKM = 5 THEN dh.PhiVanChuyen
      -- Các khuyến mãi % khác
      WHEN km.GiaTri > 0 THEN dh.TongTienSanPham * (km.GiaTri / 100.0)
      ELSE 0
    END
FROM DON_HANG AS dh
  LEFT JOIN MA_KHUYEN_MAI AS km
    ON dh.MaKM = km.MaKM
WHERE dh.MaKM IS NOT NULL;
GO

-- 3. Cập nhật TongThanhToan cho DON_HANG
UPDATE DON_HANG
SET TongThanhToan = TongTienSanPham
                     - ISNULL(TongTienGiamGia, 0)
                     + ISNULL(PhiVanChuyen, 0);
GO

-- 4. Đồng bộ lại sang HOA_DON: Tổng Tiền và Tiền Thuế (10%)
UPDATE hd
SET 
  hd.TongTien = dh.TongThanhToan,
  hd.TienThue = dh.TongThanhToan * 0.10
FROM HOA_DON AS hd
  JOIN DON_HANG AS dh
    ON hd.DonHangId = dh.IdDonHang;
GO
SELECT * FROM DON_HANG
GO
-------------------------------------PROCEDURE----------------------------------
-- 1. Khi tìm kiếm sản phẩm theo tên ở thanh tìm kiếm
-- Nếu đã có procedure cùng tên thì xóa đi
IF OBJECT_ID(N'dbo.sp_SearchProductsByName', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_SearchProductsByName;
GO

CREATE PROCEDURE dbo.sp_SearchProductsByName
    @Keyword NVARCHAR(200)  -- Từ khóa tìm kiếm
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        IdSP,
        TenSanPham,
        MoTaNgan,
        GiaBan,
        SoLuong,
        DanhMucChaId,
        DanhMucConId,
        ThuongHieuId,
        BaoHanh,
        CanNang,
        KichThuoc,
        MauSac,
        DungLuong
    FROM dbo.SAN_PHAM
    WHERE TrangThai = 1
      AND TenSanPham LIKE N'%' + @Keyword + N'%'
    ORDER BY TenSanPham;
END;
GO

-- 2. Tìm kiếm, lọc và sắp xếp sản phẩm
-- Xóa procedure nếu đã tồn tại
IF OBJECT_ID(N'dbo.sp_SearchFilterSortProducts', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_SearchFilterSortProducts;
GO

CREATE PROCEDURE dbo.sp_SearchFilterSortProducts
    @Keyword      NVARCHAR(200)    = NULL,        -- Từ khóa tìm kiếm
    @CategoryId   INT               = NULL,        -- Lọc theo DanhMucConId
    @BrandId      INT               = NULL,        -- Lọc theo ThuongHieuId
    @MinPrice     DECIMAL(18,2)    = NULL,        -- Lọc giá >=
    @MaxPrice     DECIMAL(18,2)    = NULL,        -- Lọc giá <=
    @SortBy       NVARCHAR(50)     = N'TenSanPham', -- Cột sắp xếp
    @SortDir      NVARCHAR(4)      = N'ASC'        -- 'ASC' hoặc 'DESC'
AS
BEGIN
    SET NOCOUNT ON;

    -- Danh sách các cột cho phép sắp xếp
    DECLARE @AllowedCols TABLE(Name NVARCHAR(50));
    INSERT INTO @AllowedCols VALUES
      (N'TenSanPham'), (N'GiaBan'), (N'SoLuong');

    -- Validate SortBy & SortDir
    IF NOT EXISTS (SELECT 1 FROM @AllowedCols WHERE Name = @SortBy)
        SET @SortBy = N'TenSanPham';
    SET @SortDir = CASE WHEN UPPER(@SortDir) = 'DESC' THEN 'DESC' ELSE 'ASC' END;

    -- Prepare dynamic SQL
    DECLARE @sql    NVARCHAR(MAX),
            @params NVARCHAR(4000),
            @KeywordFilter NVARCHAR(200) = N'%' + ISNULL(@Keyword, N'') + N'%';

    SET @sql = N'
      SELECT
        IdSP, TenSanPham, MoTaNgan, GiaBan, SoLuong,
        DanhMucChaId, DanhMucConId, ThuongHieuId,
        BaoHanh, CanNang, KichThuoc, MauSac, DungLuong
      FROM dbo.SAN_PHAM
      WHERE TrangThai = 1';

    IF @Keyword IS NOT NULL AND LEN(@Keyword) > 0
        SET @sql += N' AND TenSanPham LIKE @KeywordFilter';
    IF @CategoryId IS NOT NULL
        SET @sql += N' AND DanhMucConId = @CategoryId';
    IF @BrandId IS NOT NULL
        SET @sql += N' AND ThuongHieuId = @BrandId';
    IF @MinPrice IS NOT NULL
        SET @sql += N' AND GiaBan >= @MinPrice';
    IF @MaxPrice IS NOT NULL
        SET @sql += N' AND GiaBan <= @MaxPrice';

    SET @sql += N' ORDER BY ' + QUOTENAME(@SortBy) + N' ' + @SortDir;

    SET @params = N'
      @KeywordFilter NVARCHAR(200),
      @CategoryId    INT,
      @BrandId       INT,
      @MinPrice      DECIMAL(18,2),
      @MaxPrice      DECIMAL(18,2)';

    EXEC sp_executesql
      @sql,
      @params,
      @KeywordFilter = @KeywordFilter,
      @CategoryId    = @CategoryId,
      @BrandId       = @BrandId,
      @MinPrice      = @MinPrice,
      @MaxPrice      = @MaxPrice;
END;
GO

-- 3. GIỎ HÀNG
-- Xóa nếu đã có bảng CART
IF OBJECT_ID(N'dbo.CART', N'U') IS NOT NULL
    DROP TABLE dbo.CART;
GO

CREATE TABLE dbo.CART (
    IdKH       INT          NOT NULL,     -- Khóa ngoại tới KHACH_HANG.IdKH (INT)
    IdSP       INT          NOT NULL,     -- Khóa ngoại tới SAN_PHAM.IdSP (INT)
    SoLuong    INT          NOT NULL      -- Số lượng cho mỗi sản phẩm
        CONSTRAINT CHK_CART_SoLuong CHECK (SoLuong > 0),
    NgayThem   DATETIME     NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_CART PRIMARY KEY (IdKH, IdSP),
    CONSTRAINT FK_CART_KH FOREIGN KEY (IdKH)
        REFERENCES dbo.KHACH_HANG(IdKH)
        ON DELETE CASCADE,
    CONSTRAINT FK_CART_SP FOREIGN KEY (IdSP)
        REFERENCES dbo.SAN_PHAM(IdSP)
        ON DELETE CASCADE
);
GO

-- 4. Procedure thêm vào giỏ
IF OBJECT_ID(N'dbo.sp_AddToCart', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_AddToCart;
GO

CREATE PROCEDURE dbo.sp_AddToCart
    @IdKH    INT,
    @IdSP    INT,
    @Qty     INT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM dbo.CART WHERE IdKH = @IdKH AND IdSP = @IdSP)
    BEGIN
        UPDATE dbo.CART
        SET SoLuong = SoLuong + @Qty,
            NgayThem = GETDATE()
        WHERE IdKH = @IdKH AND IdSP = @IdSP;
    END
    ELSE
    BEGIN
        INSERT INTO dbo.CART (IdKH, IdSP, SoLuong)
        VALUES (@IdKH, @IdSP, @Qty);
    END
END;
GO

-- 5. Procedure đếm số lượng items trong giỏ
IF OBJECT_ID(N'dbo.sp_GetCartCount', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_GetCartCount;
GO

CREATE PROCEDURE dbo.sp_GetCartCount
    @IdKH       INT,
    @TotalItems INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT @TotalItems = ISNULL(SUM(SoLuong), 0)
    FROM dbo.CART
    WHERE IdKH = @IdKH;
END;
GO

-- 6. Procedure lấy danh sách items trong giỏ
IF OBJECT_ID(N'dbo.sp_GetCartItems', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_GetCartItems;
GO

CREATE PROCEDURE dbo.sp_GetCartItems
    @IdKH INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        c.IdSP,
        p.TenSanPham,
        p.GiaBan,
        c.SoLuong,
        (p.GiaBan * c.SoLuong) AS ThanhTien
    FROM dbo.CART c
    INNER JOIN dbo.SAN_PHAM p
        ON c.IdSP = p.IdSP
    WHERE c.IdKH = @IdKH
      AND p.TrangThai = 1
    ORDER BY c.NgayThem DESC;
END;
GO

-- Ví dụ sử dụng giỏ hàng
DECLARE @count INT;
EXEC dbo.sp_AddToCart @IdKH = 1, @IdSP = 1, @Qty = 2;
EXEC dbo.sp_GetCartCount @IdKH = 1, @TotalItems = @count OUTPUT;
SELECT @count AS SoLuongTrongGio;
EXEC dbo.sp_GetCartItems @IdKH = 1;
GO

-- 7. YÊU THÍCH
IF OBJECT_ID(N'dbo.sp_GetFavoriteProducts', N'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_GetFavoriteProducts;
GO

CREATE PROCEDURE dbo.sp_GetFavoriteProducts
    @IdKH INT  -- Mã khách hàng
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        y.IdSP,
        p.TenSanPham,
        p.GiaBan,
        p.SoLuong,
        y.NgayThem AS NgayYeuThich
    FROM dbo.YEUTHICH y
    INNER JOIN dbo.SAN_PHAM p
        ON y.IdSP = p.IdSP
    WHERE y.IdKH = @IdKH
      AND p.TrangThai = 1
    ORDER BY y.NgayThem DESC;
END;
GO

-- ============================================================
--  CHAT HỖ TRỢ KHÁCH HÀNG (WebSocket)
--  TrangThai session: WAITING | OPEN | CLOSED
--  NguoiGui tin nhắn: CUSTOMER | ADMIN | SYSTEM
-- ============================================================
IF OBJECT_ID(N'dbo.CHAT_TIN_NHAN', N'U') IS NOT NULL
    DROP TABLE dbo.CHAT_TIN_NHAN;
GO
IF OBJECT_ID(N'dbo.CHAT_SESSION', N'U') IS NOT NULL
    DROP TABLE dbo.CHAT_SESSION;
GO

CREATE TABLE dbo.CHAT_SESSION (
    IdSession          INT IDENTITY(1,1) PRIMARY KEY,
    IdKH               INT            NULL,
    TenKhach           NVARCHAR(150)  NULL,
    AdminTenDangNhap   NVARCHAR(100)  NULL,
    TrangThai          NVARCHAR(20)   NOT NULL DEFAULT N'WAITING',
    NgayTao            DATETIME       NOT NULL DEFAULT GETDATE(),
    NgayCapNhat        DATETIME       NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_CHAT_SESSION_KH FOREIGN KEY (IdKH)
        REFERENCES dbo.KHACH_HANG(IdKH)
        ON DELETE SET NULL,
    CONSTRAINT CHK_CHAT_SESSION_TrangThai
        CHECK (TrangThai IN (N'WAITING', N'OPEN', N'CLOSED'))
);
GO

CREATE TABLE dbo.CHAT_TIN_NHAN (
    IdTinNhan    INT IDENTITY(1,1) PRIMARY KEY,
    IdSession    INT            NOT NULL,
    NguoiGui     NVARCHAR(20)   NOT NULL,
    TenNguoiGui  NVARCHAR(150)  NULL,
    NoiDung      NVARCHAR(2000) NOT NULL,
    NgayGui      DATETIME       NOT NULL DEFAULT GETDATE(),
    DaDoc        BIT            NOT NULL DEFAULT 0,
    CONSTRAINT FK_CHAT_TN_SESSION FOREIGN KEY (IdSession)
        REFERENCES dbo.CHAT_SESSION(IdSession)
        ON DELETE CASCADE,
    CONSTRAINT CHK_CHAT_TN_NguoiGui
        CHECK (NguoiGui IN (N'CUSTOMER', N'ADMIN', N'SYSTEM'))
);
GO

CREATE INDEX IX_CHAT_SESSION_TrangThai ON dbo.CHAT_SESSION (TrangThai, NgayCapNhat DESC);
CREATE INDEX IX_CHAT_SESSION_IdKH      ON dbo.CHAT_SESSION (IdKH);
CREATE INDEX IX_CHAT_TN_IdSession      ON dbo.CHAT_TIN_NHAN (IdSession, NgayGui);
GO
