
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
  
  
### 1. 주문(Order) 접수
  
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/01.order.png)
  

### 2. 커피 제조 상태 확인 후 주문 취소 (취소 가능)
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/01-2.order_success_1.png)
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/01-2.order_success_2.png)


### 3. 커피 제조 상태 확인 후 주문 취소 (취소 불가)
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/01-3.order_cancel_fail.png)
  

### 4. 커피 제조 완료
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/02.%20brewComplete.png)
  

### 5. 커피 제조 완료 이후 배송 완료
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/03.%20Delivery.png)
  

### 6. 유저앱에서 커피 상태조회
  
###  6-1. 커피 주문 접수 조회
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/04-1.%20Userapp_order.png)
  

###  6-2. 커피 제조 완료 조회
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/04-2.%20Userapp_brew.png)

###  6-3. 배송완료 조회
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/04.%20Userapp_delivery.png)
  

### 7. gateway에서 API조회
![image](https://github.com/92phantom/lv2_CoffeeOrder/blob/main/_report/0.gateway%EC%97%90%EC%84%9C%20api%EB%93%A4%EC%A1%B0%ED%9A%8C.png)

## Operation 
  
### Environment Setting

### @Msaez
Azure에서 Resource Group 생성: skcc10169-rsrcgrp

1. az login  
2. az aks create --resource-group skcc10169-rsrcgrp --name skcc32-cluster --node-count 4 --enable-addons monitoring --generate-ssh-keys  
3. az acr create --resource-group skcc10169-rsrcgrp --name skcc32 --sku Basic  
4. az acr login --name skcc32  
5. az aks update -n skcc32-cluster -g skcc10169-rsrcgrp --attach-acr skcc32  

6. UPLOAD PROJECT  
7. (해당 경로로 이동) cd order  
8. mvn package  
9. (Azure에 Docker order v1 image 올리기) az acr build --registry skcc32 --image skcc32.azurecr.io/order:v1 .

### @Azure

1. helm install 

* curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 > get_helm.sh
* chmod 700 get_helm.sh
* ./get_helm.sh
* helm version

2. helm으로 Ingress Controller install

* helm repo add stable https://charts.helm.sh/stable
* helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
* helm repo update
* kubectl create namespace ingress-basic
* (helm version 3.x 일때) helm install nginx-ingress ingress-nginx/ingress-nginx --namespace=ingress-basic
* (설치확인) kubectl get all --namespace=ingress-basic

3. kafka install 
* kubectl --namespace kube-system create sa tiller
* kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
* helm repo add incubator https://charts.helm.sh/incubator
* helm repo update
* kubectl create ns kafka
* helm install my-kafka --namespace kafka incubator/kafka
* (kafka 수행 확인) kubectl get all -n kafka
  
4. [부하테스트 툴] siege
* kubectl run siege --image=ghcr.io/gkedu/siege-nginx 
* kubectl exec -it siege -c siege -- /bin/bash

### Service Deploy and Expose

#### 1. Image pull and deploy
* kubectl create deploy app --image=skcc32.azurecr.io/app:v1
* kubectl create deploy coffeebrew --image=skcc32.azurecr.io/coffeebrew:v1
* kubectl create deploy delivery --image=skcc32.azurecr.io/delivery:v1
* kubectl create deploy order --image=skcc32.azurecr.io/gateway:v1
* kubectl create deploy gateway --image=skcc32.azurecr.io/gateway:v1

#### 2. Image pull and deploy
* kubectl expose deploy app --port=8080 --type=ClusterIP
* kubectl expose deploy coffeebrew --port=8080 --type=ClusterIP
* kubectl expose deploy delivery --port=8080 --type=ClusterIP
* kubectl expose deploy order --port=8080 --type=ClusterIP
* kubectl expose deploy gateway --type=LoadBalancer --port=8080

---------------------------------------------------------------

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
