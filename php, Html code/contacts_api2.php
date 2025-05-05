<?php
//어람
header('Content-Type: application/json; charset=utf-8');  // UTF-8 인코딩

// 데이터베이스 연결 및 쿼리 처리
$conn = new mysqli("localhost", "root", "1234", "phone");


if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// 문자셋을 UTF-8로 설정
$conn->set_charset("utf8");  // MySQL 데이터베이스 연결 시 문자셋을 UTF-8로 설정

// SQL 쿼리
$sql = "SELECT nam, phone FROM alam";
$result = $conn->query($sql);

// 쿼리 실행이 성공했는지 확인
if ($result === false) {
    echo json_encode(["error" => "Database query failed"]);
    exit;
}

// 결과 배열 초기화
$contacts = array();

// 결과가 있을 때만 fetch_assoc() 호출
while ($row = $result->fetch_assoc()) {
    $contacts[] = $row;
}

// 데이터가 없으면 빈 배열을 반환
if (empty($contacts)) {
    echo json_encode([]);
} else {
    echo json_encode($contacts, JSON_UNESCAPED_UNICODE);
}

// 데이터베이스 연결 종료
$conn->close();
?>
