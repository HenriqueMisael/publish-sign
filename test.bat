cd out\production
cd server

start "I1" java Server 8881 I1
start "I2" java Server 8882 I2 localhost:8881
start "I3" java Server 8883 I3
start "I4" java Server 8884 I4 localhost:8881,localhost:8883

cd ..\client

start "P1" java Client PUBLISHER P1 localhost 8881
start "P2" java Client PUBLISHER P2 localhost 8883
start "A1" java Client SUBSCRIBER A1 localhost 8882
start "A2" java Client SUBSCRIBER A2 localhost 8882
start "A3" java Client SUBSCRIBER A3 localhost 8884
