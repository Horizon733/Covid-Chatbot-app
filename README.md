<p align="center"><img src="https://github.com/Horizon733/Covid-Chatbot-app/blob/master/Github_image.png" width="70%"></p>
<h2 align="center">Covid Chatbot application</h2>


<p align="center">
  <img src="https://forthebadge.com/images/badges/built-with-love.svg">
  <img src="https://forthebadge.com/images/badges/built-for-android.svg"><br>
  <img src="https://img.shields.io/github/repo-size/horizon733/covid-chatbot-app">
  </p>


## ✨Features:
* Get Corona info 
* Provides Corona symptoms 
* Get your state's corona Info(India)
* Get your country's corona Info

## 🔧Technologies used:
* Android Studio
* Rasa

## 📚 Libraries Used:
* Retrofit

## Medium Tutorial Link:
* Part 1: https://bit.ly/3m1UMmq
* Part 2: https://bit.ly/3h38Icd
* Part 3: https://bit.ly/3k7lSHp

## Run:
### Chatbot
- Train: 
``` bash
rasa train
```
- Run actions server:
``` bash
rasa run actions
```
- Run server(REST API):
``` bash
rasa run -m models --enable-api --endpoints endpoints.yml
```
- Run on shell
 ``` bash
 rasa shell
 ```
 ### App
 - First replace the server url from `MainActivity.kt`
 ```kotlin
 val retrofit = Retrofit.Builder()
            .baseUrl("http://IP Adress:5005/webhooks/rest/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
```
- Build the app
- Run the app on your phone either via usb connection or by building the application.


