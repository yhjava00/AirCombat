# AirCombat
  2인용 슈팅게임
  
## 구동 영상
https://www.youtube.com/watch?v=94B7LS4UJ18

## 개발 정보
개발 인원 -> 2명   
개발 기간 -> 4/19 ~ 5/7(총 15일)

## 내가 맡은 부분
1. 소켓 서버 통신   
  <img src=".\img\AirCombatSocket.png" width="450px" height="300px"></img>   
  소켓 서버와 스레드를 통해 실시간 통신과 하나의 서버에 여러 클라이언트가 연결 가능한 다중 통신을 구현했습니다.   
  또한 클라이언트들은 요청을 통해 자신의 방을 만들거나 코드를 알고 있다면 다른 이의 방에 참여를 요청할 수 있습니다.   
  
2. 총알   
  <img src=".\img\gun01.gif" width="200px" height="200px"></img> 
  <img src=".\img\gun02.gif" width="200px" height="200px"></img> 
  <img src=".\img\gun03.gif" width="200px" height="200px"></img>   
  일반 총, 관통 총, 자동 관통 총을 구현 했으며 관통 총 이상은 게이지를 일정량 모아야지 발사 할 수 있도록 구현했습니다.   

## 동료가 맡은 부분
1. 벽   
  <img src=".\img\wall.gif" width="500px" height="200px"></img>   
  게임 중간에 기본 총을 튕겨내는 벽을 구현했습니다.   
2. 아이템   
  <img src=".\img\item2.png" width="50px" height="50px"></img> 
  <img src=".\img\item3.png" width="50px" height="50px"></img> 
  <img src=".\img\item1.png" width="50px" height="50px"></img>    
  플레이어가 타격 당할 때 마다 아이템이 랜덤하게 생성됩니다.
  아이템의 종류는 체력회복, 데미지, 관통 총 충전이 있습니다.   

## 소소한 개발
1. 플레이어들의 체력이 적어지면 배경이 빨라지고 체력이 많아지면 배경이 느려집니다.   
2. 플레이어가 움직이면 좌우로 이미지가 변경됩니다.   
3. 소리를 끄고 킬 수 있습니다.   
4. 플레이어 1은 레벨을 선택할 수 있고 레벨에 따라 벽의 갯수와 속도가 변합니다.   
