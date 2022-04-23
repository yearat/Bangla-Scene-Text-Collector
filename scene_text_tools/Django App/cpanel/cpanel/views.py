import email
from email import message
import imp
from django import conf
from django.shortcuts import render
import pyrebase
from requests import request, session
from django.contrib import auth

config = {
    'apiKey': "AIzaSyAo4FpW4A6HybjIPHv5gThdN-NAZIczoBo",
    'authDomain': "cpanel-9cf2e.firebaseapp.com",
    'projectId': "cpanel-9cf2e",
    'storageBucket': "cpanel-9cf2e.appspot.com",
    'messagingSenderId': "1074475142786",
    'appId': "1:1074475142786:web:31f502abed330f20f82e9b",
    'measurementId': "G-F664KHB8J5",
    'databaseURL': "https://console.firebase.google.com/project/cpanel-9cf2e/database/cpanel-9cf2e-default-rtdb/data/~2F"
}

firebase = pyrebase.initialize_app(config)

authFb = firebase.auth()
database = firebase.database()

def signIn(request):
    return render(request, "signIn.html")


def postsign(request):
    email = request.POST.get('email')
    password = request.POST.get('password')
    try:
        user = authFb.sign_in_with_email_and_password(email, password)
    except:
        message="Invalid Credentials"
        return render(request, "signIn.html",{"message":message})
    #print(user)
    session_id = user['idToken']
    request.session['uid'] = str(session_id)
    return render(request, "welcome.html", {"email":email})


def logout(request):
    auth.logout(request)
    return render(request, 'signIn.html')


def signUp(request):
    return render(request, 'signUp.html')

def postsignup(request):
    name = request.POST.get('name')
    email = request.POST.get('email')
    password = request.POST.get('password')

    try:
        user = authFb.create_user_with_email_and_password(email, password)
    except:
        message = "unable to create account, try again"
        return(request, 'signIn.html',{"message":message})

        
        uid = user['localId']

        data = {'name':name, 'status':'1'}
        database.child('users').child(uid).child('details').set(data)
    return render(request, 'signIn.html')

