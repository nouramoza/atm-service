This is The Implementation of ATM-Emulator

for starting project please follow the steps:

1- make a database in Postgres named "BANK-DB"
2- To have initial data, Run the file "data.sql"
3- go to bankservice\src\main\resources
4- open application-dev.properties file
5- update the postgres url, username, and password
6- start both atmservice and bankservice microservices (default active profile is 'dev')
7- open postman and import the postmancollection that is in src\main\resources
or open http://localhost:8040/atm-service/api/v1/swagger-ui.html to use swagger ui

8- there are three main APIs

8-1- CardVerification check cardNumber validity (for all needed Authorizations
please add "Basic YXRtOmF0bQ==" to Authorization field)

--wrong cardNumber
{
"cardNumber" : 1122323
}

--correct cardNumber
{
"cardNumber" : 6280231451904303
}

--not active cardNumber
{
"cardNumber" : 628023145234765
}

--expired cardNumber
{
"cardNumber" : 628023145412347
}


8-2- CarPinVerification checks inserted pin validity'
--correct pin for carNo 6280231451904303
{
"pin" : 1233
}

--incorrect pin for carNo 6280231451904303
{
"pin" : 1234
}
** after 3 times of entering wrong pin the card will be deactive


8-3-BalanceManagement you can check balance, deposit, withdraw  or get receipt


{
"requestType":"CHECK_BALANCE"
}

{
"requestType":"DEPOSIT",
"description":"deposit 200",
"amount":200
}

{
"requestType":"WITHDRAW",
"description":"withdraw 300",
"amount":300
}

{
"requestType":"WITHDRAW",
"description":"withdraw 1300",
"amount":1300
}


{
"requestType":"GET_RECEIPT",
"fromDate":"2021-11-27",
"toDate":"2021-11-28"
}


9- there are 3 more APIs in bank service PostmanCollection to manage basic information to work with the application.
9-1- add account: the account number should be unique
9-2- add card: the card number should be unique
9-3- active card: is to active blocked card numbers

10- to run tests, please set bank-service env profile to "test"
(its default profile is "dev")
