

<?php
include 'db_connect.php';
$file = $_POST['file'];
if(isset($_POST['submit'])) {
    $row = 0;
    if($file == "장현.csv") {
        if (($users_file = fopen("장현.csv", "r")) !== FALSE) {
            // Line-by-line 으로 읽기
            while (($data = fgetcsv($users_file, 1000, ',')) !== FALSE) {
                        // 첫 번째 행은 헤더로 사용하기에 헤더 다음행 부터 처리
                        if ($row == 0) {
                            $row++;
                            continue;
                        }
                // 한 행씩 읽어서 insert SQL 실행
                $num = count($data);
                echo "<p> $num fields in line: <br /></p>\n";
                
                for ($c = 0; $c < $num; $c++) {
                    echo $data[$c] . "<br />\n";
                }
        
                $nam = $conn->real_escape_string($data[0]); // 첫 번째 열: 이름
                $phone = preg_replace('/\D/', '', $data[1]); // 숫자만 남기기

                $nam = '장현' . $nam;
                // 전화번호가 0으로 시작하지 않으면 앞에 0 추가
                if (!empty($phone) && $phone[0] !== '0') {
                    $phone = '0' . $phone;
                }
                
                $phone = $conn->real_escape_string($phone); // SQL 인젝션 방지
                
                // $phone = $conn->real_escape_string($data[1]); // 두 번째 열: 전화번호

                
                $query = "INSERT INTO janghyun (nam, phone) 
                        VALUES ('" . $nam . "', '" . $phone . "')";
                mysqli_query($conn, $query);
                echo "<p> Insert! <br /></p>\n";
            }
        
            fclose($users_file);
        }
    }

    if($file == "어람.csv") {
        if (($users_file = fopen("어람.csv", "r")) !== FALSE) {
            // Line-by-line 으로 읽기
            while (($data = fgetcsv($users_file, 1000, ',')) !== FALSE) {
                if ($row == 0) {
                    $row++;
                    continue;
                }
    
                $nam = $conn->real_escape_string($data[0]);
                $phone = preg_replace('/\D/', '', $data[1]);
    
                $nam = '어람 ' . $nam;
                
                if (!empty($phone) && $phone[0] !== '0') {
                    $phone = '0' . $phone;
                }
    
                $phone = $conn->real_escape_string($phone);
    
                $query = "INSERT INTO alam (nam, phone) VALUES ('$nam', '$phone')";
    
                if (!mysqli_query($conn, $query)) {
                    $failed_names[] = $nam;
                }
            }
    
            fclose($users_file);
        }
    }
    
    echo "CSV 데이터가 성공적으로 DB에 저장되었습니다.<br>";
    
    // 실패한 이름 출력
    if (!empty($failed_names)) {
        echo "<p>저장되지 않은 이름 목록 (중복된 이름):</p>";
        echo "<ul>";
        foreach ($failed_names as $name) {
            echo "<li>" . htmlspecialchars($name) . "</li>";
        }
        echo "</ul>";
    }
    
}
    ?>

