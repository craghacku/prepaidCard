run: mvn spring-boot:run


REST Endpoints - see Application.java, names should be self-explanatory:

http://127.0.0.1:8080/card/create

http://127.0.0.1:8080/card/{cardId}/pendingTransactions

http://127.0.0.1:8080/card/{cardId}/topUp?amount=5

http://127.0.0.1:8080/card/2/authorize?transactionId=7&amount=5

http://127.0.0.1:8080/card/2/capture?transactionId=8&amount=5

http://127.0.0.1:8080/card/2/reverse?transactionId=7&amount=5

http://127.0.0.1:8080/card/2/refund?transactionId=8&amount=5

http://127.0.0.1:8080/card/2/pendingTransactions

Ids of created cards and transactions are returned in JSON object of corresponding call.