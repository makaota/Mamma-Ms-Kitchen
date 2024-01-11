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


![Samsung Galaxy S9 Screenshot 0_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/6bbed120-fe01-42ed-bae6-999cbc84841f)
![Samsung Galaxy S9 Screenshot 1_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/4b291664-065e-4347-b510-55b7e9fe4ac0)
![Samsung Galaxy S9 Screenshot 2_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/2b569711-7c25-42e1-83b0-2a16db620bb7)
![Samsung Galaxy S9 Screenshot 3_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/bc35a444-d1ad-4013-a248-60f4ecd58b84)
![Samsung Galaxy S9 Screenshot 0_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/5a7faac3-7534-4071-82fa-293edf30856f)
![Samsung Galaxy S9 Screenshot 2_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/b3e432b5-f84e-44e4-972c-d389c67ef96b)
![Samsung Galaxy S9 Screenshot 4_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/862e8e57-b0b5-4f78-8216-397e9c665c74)
![Samsung Galaxy S9 Screenshot 1_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/f4e53037-c4a9-40ae-8466-e37619417780)
![Samsung Galaxy S9 Screenshot 5_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/dbdcdbca-b146-4b01-9a44-5e0949298cee)
![Samsung Galaxy S9 Screenshot 6_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/a9df0e0a-8672-4bad-b5e8-707f73612261)
![Samsung Galaxy S9 Screenshot 7_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/84d1fde0-074c-4bf8-af22-932b7f26fb57)
![Samsung Galaxy S9 Screenshot 8_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/fab0d072-b5cb-468d-9a03-31c1ff3b204e)
![Samsung Galaxy S9 Screenshot 9_384x768](https://github.com/makaota/Mamma-Ms-Kitchen/assets/74915165/281d51f2-a9a9-421d-9032-c22157aa9222)

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
