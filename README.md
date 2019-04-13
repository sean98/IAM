Our app doesn’t support signing-up because the app is designed for businesses that should create the 
users for their employees and not employees signing-up to the business, so we created a user for you.

The app supports both English and Hebrew and the preferred language can be changed from inside the app.
This is since the app looks and feels better in Hebrew when the data itself is in Hebrew, A lot of 
people still use their cell-phones in English but would prefer to use the app in Hebrew.

For the project we wanted simulate a real server of a company with thousands of customers and 
documents. We couldn’t connect to a real working server due to the risk of changing or deleting 
real, important and necessary data to the business.

We created our own ERP (SAP) MSSQL server that runs in our home on a VM. This mean that we could add 
and change data from the server during our development, testing and use freely without any risk of 
effecting something that is necessary to any business.

Note: the MSSQL server is in our house so sometimes there is a bad connection to the server because 
of the low upload speed. We added a local DB which designed to work as cache and as backup when the 
server is un-available. We used a separate server for saving images which runs on google firebase.

Since most of the data is fake a lot of the customers don’t have documents, if you want to see 
customers with documents it is **recommended** to sort them by balance to find them more easily.
