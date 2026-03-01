\# Fleet Management App



Fleet Management is a professional Android mobile application that helps users manage a fleet of vehicles, track maintenance records, and set optional alerts for maintenance schedules.



---



\## 🛠 Features



\- Add, edit, and delete vehicles

\- Track maintenance records for each vehicle

\- Set optional maintenance alerts

\- View detailed information about vehicles and maintenance history

\- Built with \*\*Room database\*\* for local storage

\- Validation ensures data integrity (required fields, date ranges, etc.)



---



\## 📱 Screenshots



!\[Home Screen](screenshots/home\_screen.png)

!\[Vehicle Detail](screenshots/vehicle\_detail.png)

!\[Maintenance Records](screenshots/maintenance\_records.png)



> Screenshots are stored in the `screenshots/` folder.



---



\## 📖 User Guide



The full \*\*interactive user guide\*\* is available in PDF format:



\[View the User Guide](user-guides/FleetManagement\_UserGuide.pdf)



> The PDF includes a clickable table of contents for easy navigation.



---



\## ⚙ How to Operate the Application



\### Launch the App

\- Open the Fleet Management app on your Android device.

\- Home screen displays buttons to view all vehicles.



\### Manage Vehicles

\- \*\*Add Vehicle\*\*: Tap Add → enter details → Save  

\- \*\*Edit Vehicle\*\*: Tap a vehicle → modify fields → Save  

\- \*\*Delete Vehicle\*\*: Tap a vehicle → Delete (cannot delete if maintenance exists)



\### Manage Maintenance

\- Tap a vehicle → View Maintenance → Add/Edit/Delete entries  

\- Maintenance dates must fall within vehicle active dates



\### Search Vehicles

\- Use the search bar to filter by title, make, model, or location



\### Generate Reports

\- Tap \*\*Generate Report\*\* from main screen to view a summary of vehicles and scheduled maintenance



---



\## 🧪 Testing



\- \*\*Unit Tests\*\*: Add/Edit/Delete vehicles, Search functionality, Data validation  

\- Tests location: `app/src/androidTest/java/com/dylanclarke/FleetManagementApp/VehicleRepositoryTest.kt`  

\- Screenshots of test results are included in the `screenshots/` folder



---



\## 📦 APK Deployment



\- \*\*Tested Environment\*\*: Android SDK 35, Minimum SDK 26 (Android 8.0 - Oreo)  

\- \*\*Installation\*\*:

&nbsp; 1. Download the signed APK from \[GitHub Pages link]  

&nbsp; 2. Enable installation from unknown sources if prompted  

&nbsp; 3. Open APK → Install → Launch



---



\## 📁 Repository



\- GitHub repo (main branch):  

\[https://github.com/YourUsername/FleetManagementApp](https://github.com/YourUsername/FleetManagementApp)  



---



\## 🧩 Tech Stack



\- Java / Android SDK  

\- Room Database (local storage)  

\- MVVM architecture  

\- Material Design UI  

\- JUnit for testing

