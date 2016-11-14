/*var cityDistrictMap = {
'Hà Nội':['Quận Ba Đình','Quận Cầu Giấy','Quận Đống Đa','Quận Hai Bà Trưng','Quận Hoàn Kiếm','Quận Hoàng Mai','Quận Long Biên','Quận Tây Hồ','Quận Thanh Xuân','Huyện Đông Anh','Huyện Gia Lâm','Huyện Sóc Sơn','Huyện Thanh Trì','Quận Bắc Từ Liêm','Quận Nam Từ Liêm','Quận Hà Đông','Huyện Ba Vì','Huyện Chương Mỹ','Huyện Đan Phượng','Huyện Hoài Đức','Huyện Mê Linh','Huyện Mỹ Đức','Huyện Phú Xuyên','Huyện Phúc Thọ','Huyện Quốc Oai','Huyện Thạch Thất','Huyện Thanh Oai','Huyện Thường Tín','Huyện Ứng Hòa','Thị xã Sơn Tây'],
'TP HCM':['Quận 1','Quận 2','Quận 3','Quận 4','Quận 5','Quận 6','Quận 7','Quận 8','Quận 9','Quận 10','Quận 11','Quận 12','Quận Bình Tân','Quận Bình Thạnh','Quận Gò Vấp','Quận Tân Bình','Quận Tân Phú','Quận Thủ Đức','Huyện Bình Chánh','Huyện Cần Giờ','Huyện Củ Chi','Huyện Hóc Môn','Huyện Nhà Bè','Quận Phú Nhuận'],
'An Giang':['Thành phố Long Xuyên','Thành phố Châu Đốc','Huyện An Phú','Huyện Châu Phú','Huyện Châu Thành','Huyện Chợ Mới','Huyện Phú Tân','Thị xã Tân Châu','Huyện Thoại Sơn','Huyện Tịnh Biên','Huyện Tri Tôn'],
'Bà Rịa-Vũng Tàu':['Thành phố Vũng Tàu','Thành phố Bà Rịa','Huyện Châu Đức','Thị xã Đất Đỏ','Huyện Long Điền','Huyện Tân Thành','Huyện Xuyên Mộc','Huyện Côn Đảo'],
'Bạc Liêu':['Thành phố Bạc Liêu','Huyện Phước Long','Huyện Hồng Dân','Huyện Vĩnh Lợi','Thị xã Giá Rai','Huyện Đông Hải','Huyện Hòa Bình'],
'Bắc Kạn':['Thành phố Bắc Kạn','Huyện Ba Bể','Huyện Bạch Thông','Huyện Chợ Đồn','Huyện Chợ Mới','Huyện Na Rì','Huyện Ngân Sơn','Huyện Pác Nặm'],
'Bắc Giang':['Thành phố Bắc Giang','Huyện Yên Thế','Huyện Tân Yên','Huyện Lục Ngạn','Huyện Hiệp Hòa','Huyện Lạng Giang','Huyện Sơn Đông','Huyện Lục Nam','Huyện Việt Yên','Huyện Yên Dũng'],
'Bắc Ninh':['Thành phố Bắc Ninh','Huyện Gia Bình','Huyện Lương Tài','Huyện Quế Võ','Huyện Thuận Thành','Huyện Tiên Du','Thị xã Từ Sơn','Huyện Yên Phong'],
'Bến Tre':['Thành phố Bến Tre','Huyện Ba Tri','Huyện Bình Đại','Huyện Châu Thành','Huyện Chợ Lách','Huyện Giồng Trôm','Huyện Mỏ Cày Bắc','Huyện Mỏ Cày Nam'],
'Bình Dương':['Thành phố Thủ Dầu Một','Thị xã Bến Cát','Huyện Dầu Tiếng','Thị xã Tân Uyên','Huyện Phú Giáo','Thị xã Thuận An','Thị xã Dĩ An','Huyện Bàu Bàng','Huyện Bắc Tân Uyên'],
'Bình Định':['Thành phố Qui Nhơn','Huyện An Lão','Thị Xã An Nhơn','Huyện Hoài Ân','Huyện Hoài Nhơn','Huyện Phù Cát','Huyện Phù Mỹ','Huyện Tuy Phước','Huyện Tây Sơn','Huyện Vân Canh','Huyện Vĩnh Thạnh'],
'Bình Phước':['Thị xã Đồng Xoài','Thị Xã Bình Long','Huyện Bù Đăng','Huyện Bù Đốp','Huyện Chơn Thành','Huyện Đồng Phú','Huyện Lộc Ninh','Thị Xã Phước Long','Huyện Bù Gia Mập','Huyện Hớn Quản'],
'Bình Thuận':['Thành phố Phan Thiết','Thị xã La Gi','Huyện Tuy Phong','Huyện Bắc Bình','Huyện Hàm Thuận Bắc','Huyện Hàm Thuận Nam','Huyện Tánh Linh','Huyện Hàm Tân','Huyện Đức Linh','Huyện đảo Phú Quý'],
'Cà Mau':['Thành phố Cà Mau','Huyện Đầm Dơi','Huyện Ngọc Hiển','Huyện Cái Nước','Huyện Trần Văn Thời','Huyện U Minh','Huyện Thới Bình','Huyện Năm Căn','Huyện Phú Tân'],
'Cao Bằng':['Thành phố Cao Bằng','Huyện Bảo Lạc','Huyện Bảo Lâm','Huyện Hạ Lang','Huyện Hà Quảng','Huyện Hoà An','Huyện Nguyên Bình','Huyện Phục Hoà','Huyện Quảng Uyên','Huyện Thạch An','Huyện Thông Nông','Huyện Trà Lĩnh','Huyện Trùng Khánh'],
'Cần Thơ':['Quận Ninh Kiều','Quận Bình Thủy','Quận Cái Răng','Quận Ô Môn','Huyện Phong Điền','Huyện Cờ Đỏ','Quận Thốt Nốt','Huyện Vĩnh Thạnh','Huyện Thới Lai'],
'Đà Nẵng':['Quận Hải Châu','Quận Thanh Khê','Quận Sơn Trà','Quận Ngũ Hành Sơn','Quận Liên Chiểu','Quận Cẩm Lệ','Huyện Hòa Vang','Huyện đảo Hoàng Sa'],
'Đắk Lắk':['Thành phố Buôn Ma Thuột','Huyện Krông Buk','Huyện Krông Pak','Huyện Lắk','Huyện Ea Súp','Huyện M\'Drăk','Huyện Krông Ana','Huyện Krông Bông','Huyện Ea H\'leo','Huyện Cư M\'gar','Huyện Krông Năng','Huyện Buôn Đôn','Huyện Ea Kar','Thị xã Buôn Hồ'],
'Đắk Nông':['Thị xã Gia Nghĩa','Huyện Cư Jút','Huyện Đăk Glong','Huyện Đăk Mil','Huyện Đăk R\'lâp','Huyện Đăk Song','Huyện Krông Nô','Huyện Tuy Đức'],
'Điện Biên':['Thành phố Điện Biên Phủ','Thị xã Mường Lay','Huyện Tủa Chùa','Huyện Tuần Giáo','Huyện Mường Thanh','Huyện Điện Biên Đông','Huyện Mường Nhé','Huyện Mường Chà','Huyện Mường Ảng','Huyện Điện Biên','Huyện Nậm Pồ'],
'Đồng Nai':['Thành phố Biên Hoà','Thị xã Long Khánh','Huyện Định Quán','Huyện Long Thành','Huyện Nhơn Trạch','Huyện Tân Phú','Huyện Thống Nhất','Huyện Vĩnh Cửu','Huyện Xuân Lộc','Huyện Cẩm Mỹ','Huyện Trảng Bom'],
'Đồng Tháp':['Thành phố Cao Lãnh','Thành phố Sa Đéc','Huyện Tân Hồng','Huyện Hồng Ngự','Huyện Tam Nông','Huyện Thanh Bình','Huyện Tháp Mười','Huyện Lấp Vò','Huyện Lai Vung','Huyện Châu Thành','Thị xã Hồng Ngự','Huyện Cao Lãnh','Huyện Chư Pưh'],
'Gia Lai':['Thành phố Pleiku','Thị xã An Khê','Thị xã Ayun Pa','Huyện Chư Păh','Huyện Chư Prông','Huyện Chư Sê','Huyện Đắk Đoa','Huyện Đắk Pơ','Huyện Đức Cơ','Huyện Ia Grai','Huyện Ia Pa','Huyện KBang','Huyện Kông Chro','Huyện Krông Pa','Huyện Mang Yang','Huyện Phú Thiện'],
'Hà Giang':['Thành phố Hà Giang','Huyện Đồng Văn','Huyện Mèo Vạc','Huyện Yên Minh','Huyện Quản Bạ','Huyện Bắc Mê','Huyện Hoàng Su Phì','Huyện Vị Xuyên','Huyện Xín Mần','Huyện Bắc Quang','Huyện Quang Bình'],
'Hà Nam':['Thành phố Phủ Lý','Huyện Bình Lục','Huyện Duy Tiên','Huyện Kim Bảng','Huyện Lý Nhân','Huyện Thanh Liêm'],
'Hà Tây':['Thành phố Hà Đông','Thành phố Sơn Tây','Huyện Ba Vì','Huyện Chương Mỹ','Huyện Đan Phượng','Huyện Hoài Đức','Huyện Mỹ Đức','Huyện Phú Xuyên','Huyện Phúc Thọ','Huyện Quốc Oai','Huyện Thạch Thất','Huyện Thanh Oai','Huyện Thường Tín','Huyện Ứng Hòa'],
'Hà Tĩnh':['Thành phố Hà Tĩnh','Thị xã Hồng Lĩnh','Huyện Cẩm Xuyên','Huyện Can Lộc','Huyện Đức Thọ','Huyện Hương Khê','Huyện Hương Sơn','Thị xã Kỳ Anh','Huyện Nghi Xuân','Huyện Thạch Hà','Huyện Vũ Quang','Huyện Lộc Hà'],
'Hải Dương':['Thành phố Hải Dương','Huyện Tứ Kỳ','Huyện Bình Giang','Huyện Cẩm Giàng','Thị xã Chí Linh','Huyện Gia Lộc','Huyện Kim Thành','Huyện Kinh Môn','Huyện Nam Sách','Huyện Ninh Giang','Huyện Thanh Hà','Huyện Thanh Miện'],
'Hải Phòng':['Quận Đồ Sơn','Quận Hải An','Quận Hồng Bàng','Quận Ngô Quyền','Quận Lê Chân','Quận Kiến An','Huyện Thủy Nguyên','Huyện An Dương','Huyện Tiên Lãng','Huyện Vĩnh Bảo','Huyện An Lão','Huyện Kiến Thụy','Huyện đảo Cát Hải','Huyện đảo Bạch Long Vĩ','Quận Dương Kinh'],
'Hậu Giang':['Thành phố Vị Thanh','Thị xã Ngã Bảy','Huyện Châu Thành','Huyện Châu Thành A','Thị xã Long Mỹ','Huyện Phụng Hiệp','Huyện Vị Thủy'],
'Hòa Bình':['Thành phố Hòa Bình','Huyện Đà Bắc','Huyện Mai Châu','Huyện Kỳ Sơn','Huyện Cao Phong','Huyện Lương Sơn','Huyện Kim Bôi','Huyện Tân Lạc','Huyện Lạc Sơn','Huyện Lạc Thủy','Huyện Yên Thủy'],
'Hưng Yên':['Thành phố Hưng Yên','Ân Thi','Khoái Châu','Kim Động','Mỹ Hào','Phù Cừ','Tiên Lữ','Văn Giang','Văn Lâm','Yên Mỹ'],
'Khánh Hoà':['Thành phố Nha Trang','Thành phố Cam Ranh','Huyện Cam Lâm','Huyện Vạn Ninh','Thị xã Ninh Hòa','Huyện Diên Khánh','Huyện Khánh Vĩnh','Huyện Khánh Sơn','Huyện đảo Trường Sa'],
'Kiên Giang':['Thành phố Rạch Giá','Thị xã Hà Tiên','Huyện An Biên','Huyện An Minh','Huyện Châu Thành','Huyện Giồng Riềng','Huyện Gò Quao','Huyện Hòn Đất','Huyện đảo Kiên Hải','Huyện Kiên Lương','Huyện đảo Phú Quốc','Huyện Tân Hiệp','Huyện Vĩnh Thuận','Huyện U Minh Thượng','Huyện Giang Thành'],
'Kon Tum':['Thị xã Kon Tum','Huyện Đắk Glei','Huyện Đắk Hà','Huyện Đắk Tô','Huyện Kon Plông','Huyện Kon Rẫy','Huyện Ngọc Hồi','Huyện Sa Thầy','Huyện Tu Mơ Rông','Huyện Ia H'+"'"+'Drai'],
'Lai Châu':['Thành phố Lai Châu','Huyện Tam Đường','Huyện Sìn Hồ','Huyện Than Uyên','Huyện Phong Thổ','Huyện Mường Tè','Huyện Mường Cốc','Huyện Tân Uyên','Huyện Nậm Nhùn'],
'Lạng Sơn':['Thành phố Lạng Sơn','Huyện Tràng Định','Huyện Văn Lãng','Huyện Văn Quan','Huyện Bình Gia','Huyện Bắc Sơn','Huyện Hữu Lũng','Huyện Chi Lăng','Huyện Cao Lộc','Huyện Lộc Bình','Huyện Đình Lập'],
'Lào Cai':['Thành phố Lào Cai','Huyện Bảo Thắng','Huyện Bát Xát','Huyện Bảo Yên','Huyện Bắc Hà','Huyện Mường Khương','Huyện Sa Pa','Huyện Si Ma Cai','Huyện Văn Bàn'],
'Lâm Ðồng':['Thành phố Đà Lạt','Thành phố Bảo Lộc','Huyện Lạc Dương','Huyện Đơn Dương','Huyện Đức Trọng','Huyện Lâm Hà','Huyện Đam Rông','Huyện Bảo Lâm','Huyện Di Linh','Huyện Đạ Huoai','Huyện Đạ Tẻh','Huyện Cát Tiên'],
'Long An':['Thành phố Tân An','Huyện Bến Lức','Huyện Cần Đước','Huyện Cần Giuộc','Huyện Châu Thành','Huyện Đức Hòa','Huyện Đức Huệ','Huyện Mộc Hoá','Huyện Tân Hưng','Huyện Tân Thạnh','Huyện Tân Trụ','Huyện Thạnh Hóa','Huyện Thủ Thừa','Huyện Vĩnh Hưng','Thị xã Kiến Tường'],
'Nam Ðịnh':['Thành phố Nam Định','Huyện Giao Thủy','Huyện Hải Hậu','Huyện Mỹ Lộc','Huyện Nam Trực','Huyện Nghĩa Hưng','Huyện Trực Ninh','Huyện Vụ Bản','Huyện Xuân Trường','Huyện Ý Yên'],
'Nghệ An':['Thành phố Vinh','Thị xã Cửa Lò','Huyện Anh Sơn','Huyện Con Cuông','Huyện Diễn Châu','Huyện Đô Lương','Huyện Hưng Nguyên','Huyện Quỳ Châu','Huyện Kỳ Sơn','Huyện Nam Đàn','Huyện Nghi Lộc','Huyện Nghĩa Đàn','Huyện Quế Phong','Huyện Quỳ Hợp','Huyện Quỳnh Lưu','Huyện Tân Kỳ','Huyện Thanh Chương','Huyện Tương Dương','Huyện Yên Thành','Thị xã Hoàng Mai','Thị xã Thái Hòa'],
'Ninh Bình':['Thành phố Ninh Bình','Thành phố Tam Điệp','Huyện Gia Viễn','Huyện Hoa Lư Huyện Kim Sơn','Huyện Nho Quan','Huyện Yên Khánh','Huyện Yên Mô','Huyện Thuận Nam'],
'Ninh Thuận':['Thành phố Phan Rang','Huyện Bác Ái','Huyện Ninh Hải','Huyện Ninh Phước','Huyện Ninh Sơn','Huyện Thuận Bắc'],
'Phú Thọ':['Thành phố Việt Trì','Thị xã Phú Thọ','Huyện Cẩm Khê','Huyện Đoan Hùng','Huyện Hạ Hòa','Huyện Lâm Thao','Huyện Phù Ninh','Huyện Tam Nông','Huyện Tân Sơn','Huyện Thanh Ba','Huyện Thanh Sơn','Huyện Thanh Thuỷ','Huyện Yên Lập','Huyện Tân Sơn'],
'Phú Yên':['Thành phố Tuy Hòa','Huyện Đồng Xuân','Thị xã Sông Cầu','Huyện Tuy An','Huyện Sơn Hòa','Huyện Phú Hòa','Huyện Đông Hoà','Huyện Tây Hoà','Huyện Sông Hinh'],
'Quảng Bình':['Thành Phố Đồng Hới','Huyện Bố Trạch','Huyện Lệ Thủy','Huyện Minh Hóa','Huyện Quảng Trạch','Huyện Quảng Ninh','Huyện Tuyên Hóa','Thị xã Ba Đồn'],
'Quảng Nam':['Thành phố Tam Kỳ','Thành phố Hội An','Huyện Duy Xuyên','Huyện Đại Lộc','Thị xã Điện Bàn','Huyện Đông Giang','Huyện Nam Giang','Huyện Tây Giang','Huyện Quế Sơn','Huyện Hiệp Đức','Huyện Núi Thành','Huyện Nam Trà My','Huyện Bắc Trà My','Huyện Phú Ninh','Huyện Phước Sơn','Huyện Thăng Bình','Huyện Tiên Phước','Huyện Nông Sơn'],
'Quảng Ngãi':['Thành phố Quảng Ngãi','Huyện Ba Tơ','Huyện Bình Sơn','Huyện Đức Phổ','Huyện Minh Long','Huyện Mộ Đức','Huyện Nghĩa Hành','Huyện Sơn Hà','Huyện Sơn Tây','Huyện Sơn Tịnh','Huyện Tây Trà','Huyện Trà Bồng','Huyện Tư Nghĩa','Huyện đảo Lý Sơn'],
'Quảng Ninh':['Thành phố Hạ Long','Thành phố Cẩm Phả','Thành phố Móng Cái','Thành phố Uông Bí','Huyện Ba Chẽ','Huyện Bình Liêu','Huyện Cô Tô','Huyện Đầm Hà','Thị xã Đông Triều','Huyện Hải Hà','Huyện Hoành Bồ','Huyện Tiên Yên','Huyện Vân Đồn','Huyện Yên Hưng','Thị xã Quảng Yên'],
'Quảng Trị':['Thành phố Đông Hà','Thị xã Quảng Trị','Huyện Cam Lộ','Huyện Cồn Cỏ','Huyện Đa Krông','Huyện Gio Linh','Huyện Hải Lăng','Huyện Hướng Hóa','Huyện Triệu Phong','Huyện Vĩnh Linh'],
'Sóc Trăng':['Thành phố Sóc Trăng','Huyện Kế Sách','Huyện Long Phú','Huyện Cù Lao Dung','Huyện Mỹ Tú','Huyện Mỹ Xuyên','Huyện Thạnh Trị','Thị xã Vĩnh Châu','Thị xã Ngã Năm','Huyện Trần Đề','Huyện Châu Thành'],
'Sơn La':['Thành phố Sơn La','Huyện Quỳnh Nhai','Huyện Mường La','Huyện Thuận Châu','Huyện Phù Yên','Huyện Bắc Yên','Huyện Mai Sơn','Huyện Sông Mã','Huyện Yên Châu','Huyện Mộc Châu','Huyện Sốp Cộp','Huyện Vân Hồ','Huyện Chiềng Áng'],
'Tây Ninh':['Thành phố Tây Ninh','Huyện Tân Biên','Huyện Tân Châu','Huyện Dương Minh Châu','Huyện Châu Thành','Huyện Hòa Thành','Huyện Bến Cầu','Huyện Gò Dầu','Huyện Trảng Bàng'],
'Thái Bình':['Thành phố Thái Bình','Huyện Đông Hưng','Huyện Hưng Hà','Huyện Kiến Xương','Huyện Quỳnh Phụ','Huyện Thái Thụy','Huyện Tiền Hải','Huyện Vũ Thư'],
'Thái Nguyên':['Thành phố Thái Nguyên','Thành phố Sông Công','Thị xã Phổ Yên','Huyện Phú Bình','Huyện Đồng Hỷ','Huyện Võ Nhai','Huyện Định Hóa','Huyện Đại Từ','Huyện Phú Lương'],
'Thanh Hoá':['Thành phố Thanh Hóa','Thị xã Bỉm Sơn','Thị xã Sầm Sơn','Huyện Bá Thước','Huyện Cẩm Thủy','Huyện Đông Sơn','Huyện Hà Trung','Huyện Hậu Lộc','Huyện Hoằng Hóa','Huyện Lang Chánh','Huyện Mường Lát','Huyện Nga Sơn','Huyện Ngọc Lặc','Huyện Như Thanh','Huyện Như Xuân','Huyện Nông Cống','Huyện Quan Hóa','Huyện Quan Sơn','Huyện Quảng Xương','Huyện Thạch Thành','Huyện Thiệu Hóa','Huyện Thọ Xuân','Huyện Thường Xuân','Huyện Tĩnh Gia','Huyện Triệu Sơn','Huyện Vĩnh Lộc','Huyện Yên Định'],
'Thừa Thiên-Huế':['Thành phố Huế','Huyện A Lưới','Thị xã Hương Thủy','Thị xã Hương Trà','Huyện Nam Đông','Huyện Phong Điền','Huyện Phú Lộc','Huyện Phú Vang','Huyện Quảng Điền'],
'Tiền Giang':['Thành phố Mỹ Tho','Thị xã Gò Công','Huyện Gò Công Đông','Huyện Gò Công Tây','Huyện Chợ Gạo','Huyện Châu Thành','Huyện Tân Phước','Huyện Cai Lậy','Huyện Cái Bè','Thị xã Cai Lậy','Huyện Tân Phú Đông'],
'Trà Vinh':['Thành phố Trà Vinh','Huyện Càng Long','Huyện Châu Thành','Huyện Cầu Kè','Huyện Tiểu Cần','Huyện Cầu Ngang','Huyện Trà Cú','Huyện Duyên Hải','Thị xã Duyên Hải '],
'Tuyên Quang':['Thành phố Tuyên Quang','Huyện Chiêm Hoá','Huyện Hàm Yên','Huyện Nà Hang','Huyện Sơn Dương','Huyện Yên Sơn','Huyện Lâm Bình'],
'Vĩnh Long':['Thành phố Vĩnh Long','Huyện Long Hồ','Huyện Mang Thít','Thị xã Bình Minh','Huyện Tam Bình','Huyện Trà Ôn','Huyện Vũng Liêm','Huyện Bình Tân'],
'Vĩnh Phúc':['Thành phố Vĩnh Yên','Thị xã Phúc Yên','Huyện Bình Xuyên','Huyện Lập Thạch','Huyện Mê Linh','Huyện Tam Dương','Huyện Tam Đảo','Huyện Vĩnh Tường','Huyện Yên Lạc','Huyện Sông Lô'],
'Yên Bái':['Thành phố Yên Bái','Thị xã Nghĩa Lộ','Huyện Lục Yên','Huyện Mù Cang Chải','Huyện Trấn Yên','Huyện Trạm Tấu','Huyện Văn Chấn','Huyện Văn Yên','Huyện Yên Bình']
};*/

var cityDistrictMap = {};
var message = "";
var globalCityId;
var globalDistrictId;

// Maps the cities and districts 
function /*void*/ mapCityDistrict() {
	var cityId = 0; 	//e.g. cityId = 1343 (set cityId to get all districts for the specified city)
    new Ajax.Request (
           'ajaxXML',  // url
           {  // options
              method: 'get', // http method
              parameters: 'provider=CityDistrictMapProvider&cityId=' + cityId,  // request parameters
              //indicator: 'throbbing'
              onSuccess:  processCityDistrictMapSuccess,
              onFailure:  processFailure
           }
    );
}

//ajax success call
function /*void*/ processCityDistrictMapSuccess(xhr) {
	message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
	cityDistrictMap = JSON.parse(message);
}

//ajax failed call
function /*void*/ processFailure(xhr) {
	alert("Failed to load districts of the city.");
}

// Modified by Mark to map city and district using the new "District" table (2016-08-19 07:43PM)
// Rebuild the city-district option fields
function /*void*/ updateCityDistrictOptions(cityId, districtId) {
	var optList = "";
	if ($jq("#" + cityId).val() != 0 && $jq.isArray(cityDistrictMap[$jq("#" + cityId + " option:selected").text()])) {
		// Build list from array in the map
		var districtsToShow = [];
		$jq(cityDistrictMap[$jq("#" + cityId + " option:selected").text()]).each(function(id, value){
			districtsToShow.push(value);
		});
		if (originalDistrictList != null)
			$jq(originalDistrictList).each(function(){
				if ($jq(this).val() == 0 || $jq.inArray($jq(this).text(), districtsToShow) > -1) {
					optList += '<option value="' + $jq(this).val() + '">' + $jq(this).text() + '</option>';
				}
			});
	} else if (originalDistrictList != null) {
		// Restore original list of all districts
		$jq(originalDistrictList).each(function(){
			optList += '<option value="' + $jq(this).val() + '">' + $jq(this).text() + '</option>';
		});
	}
	if (optList.length > 0)
		$jq("#" + districtId).empty().append(optList);
}
