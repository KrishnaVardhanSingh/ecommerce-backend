addToCart(userId, request)
↓
1. Find User
   ↓
   not found → 404
   ↓
2. Find Product
   ↓
   not found → 404
   ↓
3. Check Product enabled
   ↓
   false → 404
   ↓
4. Find Inventory
   ↓
5. Check available quantity
   ↓
   insufficient → 409
   ↓
6. Find Cart for User
   ↓
   doesn't exist → create Cart
   ↓
7. Find CartItem for Product
   ↓
   exists → increase quantity
   doesn't exist → create CartItem