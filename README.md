# Drinking-Controller_by-Kotlin

[앱 디자인 UI 보러가기](https://xd.adobe.com/view/60ebbbf1-3937-4249-83f1-eb39b67fc7c1-35b8/screen/0ca46740-12ed-4ccf-8a70-f8d656cc1de5)  

✔ DONE  
```
MainActivity --- by. 서현 (2021-07-18)
  - 백그라운드 이미지, 버튼 및 텍스트 추가 / 타이틀바에 뒤로가기 액션 추가
  - 측정모드/알람모드 버튼 클릭 시 다음 액티비티로 전환
  
알림모드/ 실시간 측정 기록 --- by. 서현
  - 실시간 데이터(Dynamic) 제외하고 UI 생성 완료 (2021-07-22) 
  - 목표량 저장 및 상단에 표시(2021-07-24)
        -> 이전 액티비티에서 입력받은 값 수신
        -> String -> Double 로 처리, null처리
  - 현재 누적량 크게 출력 및 누적량에 따른 문구 (2021-07-22)
        -> 블루투스 수신 대신 업데이트 버튼 클릭 시 값이 계속 커지도록 임시로 처리
        -> 누적량 기준을 5가지로 분류하여 문구와 색상 변경

알림모드/ 목표량 설정 --- by. 태훈
   - UI 생성 완료 (2021-07-24)  
   - 입력 버튼 기능 구현
      -> Double형 값이 입력된 경우 AlarmRecord로 데이터 넘겨줌
   - 목표량 입력 길이 제한 추가
       -> 입력시 문자가 아닌경우 예외처리
       -> 입력시 공백인 경우 예외처리
       
 측정모드/ 결과 확인 --- by. 가희
    - UI 생성 완료 (2021-07-24)
    - 홈화면(2021-07-25)
      -> 홈화면 버튼 누를 시 메인 화면으로 이동 
```

📌 TO DO
```
측정모드/ 실시간 측정 기록 --- by. 정현
  - 현재까지 누적 량 출력
  - 회차별 측정기록 List View 또는 Recycler View로 출력 (스크롤 가능)
  - 뒤로가기, 다시하기, 결과확인 버튼 동작 구현  

측정모드/ 결과 확인--- by. 가희
  - 주량 결과 출력
  - 뒤로가기, 홈화면, 저장하기 동작 구현  

알림모드/ 목표량 설정 --- by. 태훈
    - 각 토글 스위치 기능 구현(소리, 푸시)

알림모드/ 실시간 측정 기록 --- by. 서현
    - 측정 기록 보기 버튼 클릭 시 스크롤 가능한 표 형태로 출력
    - 종료하기, 다시하기 동작 
      
📢 음주 캘린더와 푸시알람은 일시 보류 (액티비티 모두 구현 후 생각)
<<<<<<< HEAD
📢 앱 측 블루투스 수신은 소프트웨어팀과 협업
```

![image](https://user-images.githubusercontent.com/61939286/126867456-a5347a43-22e2-4c18-815c-d7715527b783.png)
![image](https://user-images.githubusercontent.com/61939286/126867465-91bb26a2-d5f8-4f0d-a37f-f3a3ea59e62c.png)
![image](https://user-images.githubusercontent.com/61939286/126867154-19fa3c43-a289-419d-9c5f-ef1699ae3f3b.png)

=======
```  
MainActivity

![image](https://user-images.githubusercontent.com/61939286/126676979-41454819-a81d-4c53-a375-3420303f1e8f.png)
![image](https://user-images.githubusercontent.com/61939286/126677046-d7fab023-175e-4405-962e-70a91c65d37b.png)

![image](https://user-images.githubusercontent.com/61939286/126653687-91e7c8da-1ec5-40a6-88c3-3ef3164e4bba.png)

>>>>>>> th/alarm_mode
