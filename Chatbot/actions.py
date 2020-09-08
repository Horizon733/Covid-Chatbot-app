# This files contains your custom actions which can be used to run
# custom Python code.
#
# See this guide on how to implement these action:
# https://rasa.com/docs/rasa/core/actions/#custom-actions/


# This is a simple example for a custom action which utters "Hello World!"

from typing import Any, Text, Dict, List

from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
import requests, datetime

class CoronaCasesInfo(Action):

     def name(self) -> Text:
         return "corona_cases_action"

     def run(self, dispatcher: CollectingDispatcher,
             tracker: Tracker,
             domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
         user_message_entity = tracker.latest_message['entities']
         context = ""
         for entity in user_message_entity:
             if (entity['entity'] == "state"):
                 url = "https://api.covid19india.org/data.json"
                 response = requests.get(url).json()
                 state = entity['value'].title()
                 for data in response["statewise"]:
                     if (data["state"] == state):
                         print("confirmed cases: ", data["confirmed"])
                         context = "Confirmed cases: "+data["confirmed"]+"\nActive cases: "+data["active"]+"\nDeaths: "\
                                   +data["deaths"]+"\nRecovered cases: "+data["recovered"]+"\n TimeStamp: "+data["lastupdatedtime"]
             elif(entity['entity']=="country" and entity['entity'] != "state"):
                 url = "https://covid-19-data.p.rapidapi.com/country"
                 country = entity['value'].lower()
                 querystring = {"format": "json", "name": country}
                 headers = {
                     'x-rapidapi-host': "covid-19-data.p.rapidapi.com",
                     'x-rapidapi-key': "19ed115698msh2a7ce58d4de8cf8p19e4ebjsnfcb1afda1413" # you can find this api on https://rapidapi.com/Gramzivi/api/covid-19-data
                 }
                 response = requests.get(url, headers=headers, params=querystring).json()

                 for data in response:
                     date = data['lastUpdate']
                     dt = date.split('T')
                     dat = datetime.datetime.strptime(dt[0], '%Y-%m-%d').strftime('%d-%m-%Y')
                     context = "Confirmed cases: "+str(data["confirmed"])+"\nCritical cases: "+str(data["critical"])+"\nDeaths: "+str(data["deaths"])+"\nRecovered cases: "+str(data["recovered"])+"\nLast Update: "+dat
         dispatcher.utter_message(text=context)
         return []
