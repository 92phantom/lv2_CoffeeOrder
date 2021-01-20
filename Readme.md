
# Coffee Siren Order

* Environment : Azure
* Language : Springboot (JAVA)
* Protocol : HTTP
* Middleware : Kafka

## Eventstorming

![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/_brainstorming.png)

## Scenario 
  
1) Funtional Requirement
  
  1. 고객은 커피를 주문한다.
  2. 주문이 접수되면, 커피 제조(Coffe Brew)로 주문이 전달된다.
  3. 관리자가 커피 제조를 완료처리하면, 배송으로 주문한 커피의 상태가 전달된다.
  4. 고객은 커피 제조 상태가 접수완료인 경우에만 커피 주문을 취소할 수 있다.
  5. 커피를 취소하면 커피 제조를 취소하고 알림이 전달된다.
  6. 고객은 주문한 커피에 대해 제조 상태, 배송 상태를 조회할 수 있다.
  
2) Non-funtional Requirement
  
  1. 커피 제조 상태가 조회되지 않으면 취소 불가능하다. (동기식 호출)
  2. 커피 제조 상태가 조회되지 않으면 취소요청을 보류한다.
  3. 커피 제조나 배송 API가 비정상이라도 고객은 주문할 수 있다. (비동기식 호출)

## Implementation

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 
  
  
1. 주문(Order) 접수
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/01.order.png)
  
2. 커피 제조 상태 확인 후 주문 취소 (취소 가능)
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/01-2.order_success_1.png)
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/01-2.order_success_2.png)

3. 커피 제조 상태 확인 후 주문 취소 (취소 불가)
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/01-3.order_cancel_fail.png)
  
4. 커피 제조 완료
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/02.%20brewComplete.png)
  
5. 커피 제조 완료 이후 배송 완료
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/03.%20Delivery.png)
  
6. 유저앱에서 커피 상태조회  
 6-1. 커피 주문 접수 조회
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/04-1.%20Userapp_order.png)
 6-2. 커피 제조 완료 조회
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/04-2.%20Userapp_brew.png)
 6-3. 배송완료 조회
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/04.%20Userapp_delivery.png)

7. gateway에서 API조회
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/0.gateway%EC%97%90%EC%84%9C%20api%EB%93%A4%EC%A1%B0%ED%9A%8C.png)

 - 주문 등록

![image](https://user-images.githubusercontent.com/75401920/105002099-1b8f1480-5a74-11eb-957f-26f060d0bc5f.png)

 - 주문 등록 후 주문내역 조회 시 배송상태 변경됨 

![image](https://user-images.githubusercontent.com/75401920/105001784-a3c0ea00-5a73-11eb-9c83-1d504502bca3.png)

 - 주문 등록 후 결제 내역 조회

![image](https://user-images.githubusercontent.com/75401920/105001881-c81cc680-5a73-11eb-8b94-c25d03309a84.png)

 - 재고변경

![image](https://user-images.githubusercontent.com/75401920/105002205-3e212d80-5a74-11eb-9d3a-469df1f27d49.png)

 - 주문취소

![image](https://user-images.githubusercontent.com/75401920/105002335-6dd03580-5a74-11eb-860d-66d4062bd18f.png)

 - 주문취소 시 결제 취소 반영됨

![image](https://user-images.githubusercontent.com/75401920/105002401-95270280-5a74-11eb-89c9-069db87220e6.png)

 - 결제취소시 배송 취소
 
![image](https://user-images.githubusercontent.com/75401920/105002466-acfe8680-5a74-11eb-91ba-bc04509a8b10.png)


2. 마이페이지 조회

![image](https://user-images.githubusercontent.com/75401920/105002605-e8995080-5a74-11eb-99ad-15cdb20324ad.png)


3. 결제서비스 장애 시 주문 불가

![image](https://user-images.githubusercontent.com/75401920/105002912-52b1f580-5a75-11eb-8ce0-b661fbbcc1d3.png)



   

5.6 Gateway, Deploy

product 상품 등록 
 - LoadBalanced로 노출된 퍼블릭IP로 상품등록 API 호출

![image](https://user-images.githubusercontent.com/75401920/105001534-42008000-5a73-11eb-8ab7-c955745e7703.png)


애져에 배포되어 있는 상황 조회 kubectl get all

![image](https://user-images.githubusercontent.com/75401920/105000728-06b18180-5a72-11eb-8609-e527c48f7060.png)



7. Istio 적용 캡쳐

  - Istio테스트를 위해서 Payment에 sleep 추가
  
![image](https://user-images.githubusercontent.com/75401920/105005616-e89b4f80-5a78-11eb-82cb-de53e5881e3f.png)

 - payments 서비스에 Istio 적용
   
![image](https://user-images.githubusercontent.com/75401920/105006822-7f1c4080-5a7a-11eb-9191-db35233773d3.png)

 - Istio 적용 후 seige 실행 시 대략 50%정도 확률로 CB가 열려서 처리됨

![image](https://user-images.githubusercontent.com/75401920/105006958-b2f76600-5a7a-11eb-99f3-c8b81a4ec270.png)

8. AutoScale

 - AutoScale 적용된 모습

![image](https://user-images.githubusercontent.com/75401920/105006642-4714fd80-5a7a-11eb-8424-aa2dede45666.png)

 - AutoScale로  order pod 개수가 증가함

![image](https://user-images.githubusercontent.com/75401920/105006308-cf46d300-5a79-11eb-96db-77d865c9bfe9.png)


9. Readness Proobe
 
  - Readiness 적용 전: 소스배포시 500 오류 발생
  
![image](https://user-images.githubusercontent.com/75401920/105004548-7d04b280-5a77-11eb-95cb-d5fe19a40557.png)


  - 적용 후: 소스배포시 100% 수행됨

![image](https://user-images.githubusercontent.com/75401920/105004912-f0a6bf80-5a77-11eb-88ee-f0bcd8f67f45.png)

- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW
