# QueryRunner

A tool designed to run queries against a MongoDB schema

## Getting Started

1. Compile the code
2. Create a config file
3. Create query files


### The Config File

The config file looks something like this:

```
{
  "mongoUri": "127.0.0.1",
  "database": "test",
  "collection": "trades",
  "iterations": 100,
  "threads": 20,
  "hashSuffix": "Hash",
  "epochSuffix": "Epoch",
  "hashIndex": ["index.msgId", "index.tradecorrelationId", "index.tradetradeId", "index.party1partyId", "index.party2partyId", "index.tradeproductId", "index.tradeissuer"],
  "epochIndex": ["index.tradecreationTimestamp", "index.tradebusinessDate1", "index.tradebusinessDate2"]
}
```

* fields listed in the *hashIndex* array will be added to queries automatically with the correct hash applied
* fields listed in the *epochIndex* array will be added to queries automatically with the epoch applied
* the *hashSuffix* and *epochSuffix* field denote the suffix of the new field

For example:

```
 find({"index.msgId": "3819820675..."}) will be transposed to find({"index.msgId" : "3819820675...", "index.msgIdHash" : 24470614 })
 aggregate([{"$match": { "index.msgId" : "3819820675...." }}]) --> aggregate([{ "$match" : { "index.msgId" : "3819820675..", "index.msgIdHash" : 24470614 }
```

### Query files

* each file should contain only a single query
* the query format is similar to what you would write in the mongo shell

For example:

```
find({"index.msgId": "38198206752a6b4a3-7003-4ef0-9da5-facf0fe5704e"}, {"index.msgId": 1})

aggregate([{$match: {"index.msgId": "38198206752a6b4a3-7003-4ef0-9da5-facf0fe5704e"}}, {$project: {"index.tradetradeId": 1}}])
``` 

### Running the code

The config file and query file location are passed in as arguements on the command line denoted by -c and -q flags respectively

```
 java -jar QueryRun.jar -c config.json -q queries/simple-query
```

Additionally, there is a **-d** flag for debug mode which will print out the contents of returned records

## Placeholders

Within the code, it is possible to use placeholders to change the query during runtime. This is achieved by added placeholder KV pairs to the Query object when it is created.

```
    query = QueryBuilder.create()
		.addPlaceholder("PLAICE_HOLDER", "123")
		.setQueryFile(Paths.get(config.getQueryFile()))
		.setConfig(config)
		.build();
```

Placeholders must be enclosed by ${} to be identified.

The transformation would do something like this:

```
 find({"index.msgId": "${PLAICE_HOLDER"} }) --> find({"index.msgId": "123"} }) 
 find({"index.msgId": ${PLAICE_HOLDER} }) --> find({"index.msgId": 123} })
``` 

**NB: this needs more work**