# Cync

Cync is am Android application that relieves the users from posting the contact in the social media or in finding the new contact and updating them in the mobile devices in case of change in the contact number. Cync helps in auto-syncing the user contacts by notifying the user contact list about the new number and provides them with the option of either updating the contact or creating a new contact.
Cync also enable a user to lookup the contact's contacts on approval by the contact.


## Installation

1. open terminal : `git clone https://github.com/kp2601/Cync.git`
2. Import the project into Android Studio 
3. Navigate to Build and Select Generate Signed APK.
4. Select or generate new key store path, click Next and Finish.
5. This should create an APK file.


## Usage

1. Sign up by providing all the details.
2. After logging in, a list of Cync registered contacts are displayed. 
3. Navigate to settings, and update the contact number.
4. The above step sends notifications to all the contacts of your device.
5. For contact look up, select the contact in the home-screen to go to the contact details.
6. Enter the keyword (Example: Sherlock) that you want to search in the contact's mobile.
7. If the user approves the request, list of contacts matching the keyword are displayed. Otherwise, user denied message is displayed.


## Contributing
1. Cync uses Restlet Framework for creating a local Web Service that run on the device.
1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D

## Note

This application works only for the devices on the same network now as there are no static endpoints for the local services. Any ideas are welcome to fix the issue.

## License

Copyright: Kamaleshwar Panapakam 
panapakam.kamaleshwar@gmail.com



