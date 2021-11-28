This is The Implementation of ATM-Emulator

for starting project please follow the steps:

1- make a database in Postgres named "ATM-EMULATOR" 
2- Run the file "table_data.sql"
3- go to atm-emulator\bankservice\src\main\resources 
4- open application.properties file 
5- update the postgres url, username, and password 
6- start both atmservice and bankservice microservices 
7- go to http://localhost:8040/swagger-ui.html 
8- there are three APIs 

8-1- CardVerification check cardNumber validity 
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
"cardNumber" : 6280231451904303
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

--correct pin for carNo 6280231451904303
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
