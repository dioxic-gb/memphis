# ScalingTest
A Repo for a number of tools to test scaling models (incomplete)

To start Risk Generator run com.mongodb.sapient.datagen we require :

1) applicationConf.json
2) seed files

applicationConf.json is kept under resources of scalingtest-datagen folder & consist of following properties :

    - mongoURI: MongoDB URL (ex : "mongodb://localhost")
    - database: DB Name (ex: "HSBC")
    - collection: Collection Name (ex :"trades")
    - seedFilesDir: Seed files Directory (ex : "~\\datagen\\src\\main\\resources\\seedFiles")
    - startDate : It is valuation date (ex :"20170111")
    - numDays : Number of days for which data need to be generated (ex : 2)
    - templatePath : Path to Json templates

Seed files is placed in ~\datagen\src\main\resources\seedFiles\ consist of hsbcTradeId & tradeId.

Run below command to start insertion in MongoDB :
```
java com.mongodb.sapient.datagen.DataGen -c ~\datagen\src\main\resources\conf\applicationConf.json
```