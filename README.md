엑셀파일 .xlsx가 아닌 .csv로 바꿔서 업로드 할것
.csv 변경시 전화번호가 깨질경우 사이트내에서 자동으로 바뀌게 했으니 무시할것
한글이 깨질시 메모장으로 열고 UTF-8로 변경할것

# 🔍 Address_Book
**Address_Book**은 DB에 저장되어있는 정보를 휴대폰 주소록에 자동으로 등록, 수정 해주는 어플입니다.

## 🖥️ 프로젝트 개요

- 🧩 **개발 환경**: Android Studio
- 🛠️ **구현 방식**: PHP사이트에서 엑셀 업로드 후 어플에서 저장
- 🗃️ **데이터베이스**: MySQL
- 🎯 **주요 기능**:
  - 주소록 자동 저장 및 수정 기능
  - 사이트에서 엑셀 업로드 후 DB저장 기능

---

## ⚙️ 기술 스택

| 구성 요소      | 사용 기술                |
|----------------|--------------------------|
| 백엔드         | PHP                     |
| 프론트엔드     | HTML                     |
| 서버           | Apache(XAPMM)            |
| 데이터베이스    | MySQL                    |

---

## 🌄 화면 미리보기

Food_Find의 주요 화면들을 아래에서 확인하실 수 있습니다.

<table>
  <tr>
    <td align="center"><b>🏠 어플 메인 화면</b></td>
    <td align="center"><b>📱 저장후 주소록 화면</b></td>
  </tr>
  <tr>
    <td><img src="./images/메인화면.png" width="100%"></td>
    <td><img src="./images/저장후 화면2.png" width="100%"></td>
  </tr>
  <tr>
    <td align="center"><b>📄 저장 순서</b></td>
    <td align="center"><b>📄 상세페이지 지도</b></td>
  </tr>
  <tr>
    <td><img src="./images/저장 순서.png" width="100%"></td>
    <td><img src="./images/상세페이지 지도.png" width="100%"></td>
  </tr>

</table>

---

## 📌 향후 개선 방향

- 어플 디자인
- DB테이블을 미리 만들어 두지 않더라도 사용가능하도록 수정


