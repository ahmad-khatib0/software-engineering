# About
The key concepts in each step are italicized.

1. Tweets that mention certain digital currencies (#bitcoin, #ethereum) are being streamed to the tweets topic
    Since each record is JSON-encoded, we need to figure out how to properly deserialize these records into 
    higher-level data classes Unneeded fields should be removed during the deserialization process to simplify 
    our code. Selecting only a subset of fields to work with is referred to as projection, 
    and is one of the most common tasks in stream processing

2. Retweets should be excluded from processing. This will involve some form of data filtering

3. Tweets that aren’t written in English should be branched into a separate stream for translating

4. Non-English tweets need to be translated to English. This involves mapping one input value 
   (the non-English tweet) to a new output value (an English translated tweet)
   
5. The newly translated tweets should be merged with the English tweets stream to create one, unified stream

6. Each tweet should be enriched with a sentiment score, which indicates whether or not Twitter users are 
    conveying positive or negative emotion when discussing certain digital currencies. Since a single 
    tweet could mention multiple cryptocurrencies, we will demonstrate how to convert each input 
    (tweet) into a variable number of outputs using a flatMap operator

7. The enriched tweets should be serialized using Avro, and written to an output topic called crypto-sentiment
   Our fictional trading algorithm will read from this topic and make investment 
   decisions based on the signals it sees

```sh
$ docker-compose up
```

Regarding the Kafka Streams application, there are two easy options for running the example code, depending on whether or not you want to use a dummy client for performing tweet translation and sentiment analysis, or if you actually want to use Google's Natural Language API (which requires a service account) to perform these tasks. If you don't want to bother setting up a service account, no worries. Just follow the steps under **Option 1**.

## Option 1 (dummy translation / sentiment analysis)
First, if you want to see this running without setting up a service account for the translation and 
sentiment analysis service, you can run the following command:

```sh
$ ./gradlew run --info
```

Now, follow the instructions in [Producing Test Data](#-producing-test-data).

## Option 2 (actual translation / sentiment analysis)
    If you want the app to actually perform tweet translation and sentiment analysis, 
    you will need to setup a service account with Google Cloud.

    You can download `gcloud` by following the instructions 
    [here](https://cloud.google.com/sdk/docs/downloads-interactive#mac). Then, run the following commands to 
    enable the translation / NLP (natural language processing) APIs, and to download your service account key.

```bash
# login to your GCP account
$ gcloud auth login <email>

# if you need to create a project
$ gcloud projects create <project-name> # e.g. kafka-streams-demo. must be globally unique so adjust accordingly

# set the project to the appropriate value
# see `gcloud projects list` for a list of valid projects
$ gcloud config set project <project>

# create a service account for making NLP API requests
$ gcloud beta iam service-accounts create <sa-name> \ # e.g. <sa-name> could be "dev-streams"
    --display-name "Kafka Streams"

# enable the NLP API
$ gcloud services enable language.googleapis.com

# enable the translate API
$ gcloud services enable translate.googleapis.com

# create and download a key
$ gcloud iam service-accounts keys create ~/gcp-demo-key.json \
     --iam-account <sa-name>@<project>.iam.gserviceaccount.com
```

Then, set the following environment variable to the location where you saved your key.
```
export GCP_CREDS_PATH=~/gcp-demo-key.json
```

Finally, run the Kafka Streams application using the following command:
```sh
$ ./gradlew run --info
```

Now, follow the instructions in [Producing Test Data](#-producing-test-data).

# Producing Test Data
    We have a couple of test records saved to the `data/test.json` file, which is mounted in the `kafka` 
    container for convenience. Feel free to modify the data in this file as you see fit. Then, run the 
    following command to produce the test data to the source topic (`tweets`).

```sh
$ docker-compose exec kafka bash

$ kafka-console-producer \
  --bootstrap-server kafka:9092 \
  --topic tweets < test.json
```

Then, in another tab, run the following command to consume data from the sink topic (`crypto-sentiment`).
```sh
$ docker-compose exec schema-registry bash

$ kafka-avro-console-consumer \
 --bootstrap-server kafka:9092 \
 --topic crypto-sentiment \
 --from-beginning
 ```
 
 You should see records similar to the following appear in the sink topic.
 ```json
 {"created_at":1577933872630,"entity":"bitcoin","text":"Bitcoin has a lot of promise. I'm not too sure about #ethereum","sentiment_score":0.3444212495322003,"sentiment_magnitude":0.9464683988787772,"salience":0.9316858469669134}
{"created_at":1577933872630,"entity":"ethereum","text":"Bitcoin has a lot of promise. I'm not too sure about #ethereum","sentiment_score":0.1301464314096875,"sentiment_magnitude":0.8274198304784903,"salience":0.9112319163372604}
```
