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

# Screen Shots









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
