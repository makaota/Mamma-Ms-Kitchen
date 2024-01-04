# Overview

Mama M's Kitchen User App is an Android application crafted to enhance the food ordering experience for customers. 
Leveraging my expertise in Android development, 
this app provides an intuitive interface for users to seamlessly browse menus, 
place orders, and manage their profiles.

# Scope

The app encompasses features like user registration, menu browsing, order placement, notifications, and more.

# Target Audience

This app is designed for customers who want to conveniently order food from Mama M's Kitchen.

# Features

- **_User Authentication:_** Implemented secure user registration and login functionalities.
- **_Browse Menu:_** Utilizing RecyclerViews and adapters for efficient menu item display.
- **_Place Orders:_** Seamless integration of cart functionality and order placement.
- **_Order History:_** Utilize Firebase Firestore to store and retrieve user order history.
- **_User Profile Management:_** Efficiently manage user profiles and preferences.
- **_Notifications:_** Implement push notifications for order updates and special offers.
- **_Payment Mode:_** Securely handle transactions at the store, cash on delivery.
- **_Feedback and Ratings:_** Allow users to provide feedback and rate dishes. to be implemented

# Screen Shot

![Screenshot_20240104_154131_com makaota mammamskitchen_284x600_242x512](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/11ad53ae-fc91-41e0-950a-b9dc8ea2623a)
![Screenshot_20240104_154150_com makaota mammamskitchen_284x600_242x512](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/43afe859-c03a-4202-a41c-2a0ff8979881)
![Screenshot_20240104_141150_com makaota mammamskitchen_284x600_242x512](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/faa33a90-1724-4a4e-a2ce-33150c5ce144)
![Screenshot_20240104_141225_com makaota mammamskitchen_284x600_242x512](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/2a69d85b-7cd3-42aa-b924-d5e38479e9a3)
![Screenshot_20240104_141250_com makaota mammamskitchen_284x600_242x512](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/9bdffcd5-6785-4298-8993-28f18db3990c)
![Screenshot_20240104_141353_com makaota mammamskitchen_284x600_242x512](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/a8b64d79-525a-4726-89fc-26f43218e009)


# Project Structure

**Firestore**

Integration with Firestore for real-time database functionalities.

**Interfaces**

Definition of the NotificationAPI interface for handling push notifications.

**Models**

Data models such as Address, CartItem, Favorites, etc., for structured data handling.

**UI**

Utilization of Activities, Fragments, and Adapters designed with Android best practices. RecyclerViews and custom UI components like RUAButton, RUAEditText, RUARadioButton, RUATextView, and RUATextViewBold ensure a consistent and visually appealing user interface.

**Utils**

Constants for maintaining key values, FirebaseService.kt for handling Firebase functionalities, GlideLoader for efficient image loading using Glide library, RetrofitInstance for managing API communication, and other utility classes for a modular and maintainable codebase.

**ViewModel**

CounterViewModel managing counter-related functionality using Android Architecture Components for a robust and scalable application structure.

# System Requirements

**_Supported Platforms:_**

- Android

Minimum OS Version: Android 5.0 (Lollipop) and above
Hardware Requirements: Standard requirements for modern smartphones.

- Installation Guide
  
**_Clone the repository:_**

git clone https://github.com/your-username/mama-ms-kitchen-app.git
Open the project in Android Studio or your preferred IDE.
Build and run the app on your emulator or physical device.

**_Usage_**

Launch the app.
Register or log in to your account.
Explore the menu, add items to your cart, and place orders.
Manage your user profile and addresses.
Receive notifications about order status and promotions.

# Contributing

If you'd like to contribute to Mama M's Kitchen User App, please follow the contribution guidelines in the CONTRIBUTING.md file.

# License

This project is licensed under the MIT License.

# Contact
For any questions or support, please email me at sa.makaota@gmail.com 
