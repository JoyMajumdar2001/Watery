# Watery
Watery is an android app to deliver water to customer like an e-com app. The backend of this app is fully made with Appwrite. From Authentication to Database, the Appwrite powers this app.

##### Apps process
After entering the app you'll have two option of login 

- as a customer
- as a delivery partner


![Main page](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/qwkctpq6kecoyt616m9t.png)

Now if you join as a customer the you need to create account or need to login.
Here I have used Appwrite's authentication flow to create account or to create a new seassion.

![Login](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/vnwp2575goiaighwwuxd.png)  | ![Account create](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/h81qwhe5quomypl6copt.png)

After successful login you'll be redirect to the main dahboard page where you will get all the list of water pdoducts.
In the dashboard I have Google Play Loction to get user location and Appwrite's Database to fetch all products' info and Storage to fetch all the images.


![SS4](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/sfe8jvj63tddoylog2x2.png)

Then you need to cart your desired products and go to cart page and pick your location and mobile number.
In this page the user's all carted products information is collected and using Google Map the loction is selected.

![SS5](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/0fye7g9o4dzdbzwjwb1q.png)![SS6](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/musgppr6ua1hd5rum1r7.png)![SS7](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/jvpov1265le8jy20wwr3.png)

Now you can pay via Cash On Delivery Or Online. Here I have intrigate [Razorpay](https://razorpay.com/) to accept online payments. After successful payment your order will be placed.
When payment is successful the date is saved as a Order object in Appwrite's Database.

![SS8](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/vfj4ck3o6bh7tnj4mji7.png)

![SS9](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/lix7td7nqf3xgcua55uo.png)

![SS10](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/cv7e9vvm1h8va1wqd8eb.png)

Now from the dashboard you can check your order and track your orders.
Hare the orders list is fetched from Appwrite's Database.

![SS11](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/0racol8tacade71bh7sy.png) | ![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/xdkn7r6k269re5k40wgw.png)


Now on the other hand if you join as a delivery partner then you need to login.

![SS12](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/72g78y1tzl2dnyj232wp.png)

After login you will be redirect to the dashboard where the delivery partner can see all the orders from all user and now he can set order status e.g. Order Accepted, Order Shipped, Order Delivered etc.

Here a delivery partner update that order status in Appwrite's Databse which is reflected on user's order list. 


![SS13](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/5l0cmj579ol5zrgar1mp.png)
