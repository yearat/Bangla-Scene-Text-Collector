import pyrebase
import urllib

firebaseConfig={
    'apiKey': "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
    'authDomain': "fir-course-bxxx.firebaseapp.com",
    'projectId': "fir-course-bxxx",
    'storageBucket': "fir-course-bxxx.appspot.com",
    'messagingSenderId': "xxxxxxxxxxx",
    'appId': "xxxxxxxx",
    'databaseURL': "https://fir-course-bxxx-default-rtdb.firebaseio.com/"
}

firebase = pyrebase.initialize_app(firebaseConfig)

db = firebase.database()
auth = firebase.auth()
storage = firebase.storage()


# Authentication

# Login
email = input("Enter your email ")
password = input("Enter your password ")

try:
    auth.sign_in_with_email_and_password(email, password)
    print("Successfully Signed In!")

except:
    print("Invalid email or password, Try Again")


# SignUp
email = input("Enter your email ")
password = input("Enter your password ")
confirmpass = input("Confirm password ")
if password == confirmpass:
    try:
        auth.create_user_with_email_and_password(email, password)
        print("Success!")
    except:
         print("Sorry Email already exists")



# Storage

# Upload
filename = input("Enter the name of the file to upload ")
cloudfilename = input("Enter the name of the file on the cloud ")
storage.child(cloudfilename).put(filename)
print(storage.child(cloudfilename).get_url(None))


# Download
cloudfilename = input("Enter the name of the file you want to download ")
storage.child(cloudfilename).download("", "downloaded.txt")


# Reading from file
cloudfilename = input("Enter the name of the file you want to download ")
url = storage.child(cloudfilename).get_url(None)
f = urllib.request.urlopen(url).read()
print(f)



# Database

# Create
data = {'age': 27, 'address':"LA", 'employed':False, 'name':"Jason Statham"}
db.child('people').push(data)
db.child('people').child("myid").set(data)


# Update
db.child("people").child("key").update({'name': 'Johnny Depp'})
people = db.child("people").get()   
for person in people.each():
    print(person.val())
    print(person.key())

for person in people.each():
    if person.val()['name']=='Jason Statham':
        db.child("people").child(person.key()).update({'name': 'Dwaine Johnson'})


# Delete
db.child("people").child("person").child("age").remove()
db.child("people").child("person").remove()


# Read
people = db.child("people").child("key").get()
print(people.val())


# Chane Database Rules to
# {
#   "rules": {
#     ".read": true,
#     ".write": true,
#       "people": {
#         ".indexOn":["age", "address", "name", "employed"]
#       }
#   }
# }


# Queries
people = db.child("people").order_by_child("address").equal_to("LA").get() # ==
people = db.child("people").order_by_child("age").equal_to(25).get() # ==
people = db.child("people").order_by_child("age").start_at(35).get() # >=
people = db.child("people").order_by_child("age").end_at(30).get() # <=
people = db.child("people").order_by_child("employed").equal_to(False).get() # ==
people = db.child("people").order_by_child("employed").equal_to(False).limit_to_first(1).get() # First N only
people = db.child("people").order_by_child("employed").equal_to(False).limit_to_last(1).get() # Last N only

for person in people.each():
    print(person.val())
