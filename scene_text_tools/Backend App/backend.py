# This code grabs the entire database and then 
# downloads images and labels using 
# corresponding links and file names

import pyrebase
import urllib
from tqdm import tqdm
import time

firebaseConfig = {
    'apiKey': "AIzaSyAgmU77RJ9Wgqe8FZ92WV-8rXP4Y_Z_SAA",
    'authDomain': "bn-scn-txt.firebaseapp.com",
    'databaseURL': "https://bn-scn-txt-default-rtdb.asia-southeast1.firebasedatabase.app",
    'projectId': "bn-scn-txt",
    'storageBucket': "bn-scn-txt.appspot.com",
    'messagingSenderId': "478957988683",
    'appId': "1:478957988683:web:31931ec1bfcb77e15816ce"

}

firebase = pyrebase.initialize_app(firebaseConfig)
db = firebase.database()
storage = firebase.storage()


data = db.child("data").get() 

for datum in tqdm(data.each(), desc = 'Progress Bar'):
    image_link = datum.val()['imageLink']
    text_link = datum.val()['textLink']
    image_name = datum.val()['imageName']
    text_name = datum.val()['textName']

    storage.child("collected_data").child(image_name).download("","downloads/"+image_name)
    storage.child("collected_data").child(text_name).download("","downloads/"+text_name)
