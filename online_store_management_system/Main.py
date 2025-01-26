# Justin Roderick
# ju136012

# Import classes
from Product import Product
from Customer import Customer
from Order import Order
from Store import Store

# Create store
store = Store("Tech Gadget Store")

# Add products
product1 = Product(1, "Laptop", 1000.0, 10)
product2 = Product(2, "Smartphone", 500.0, 20)
store.add_product(product1)
store.add_product(product2)

# Add customer
customer1 = Customer(101, "John Doe", "john@example.com")
store.add_customer(customer1)

# Place an order
order1 = Order(1001, customer1, [(product1, 2), (product2, 1)]) # Ordering 2 Laptops and 1 Smartphone
store.place_order(order1)
customer1.add_order(order1)

# Print store details
print(store)
print("Inventory:")
for product in store.get_inventory():
    print(product)

# Print customer details
print("\nCustomer Orders:")
for order in customer1.get_order_history():
    print(order)

# Export data to file
store.export_data_to_file("store_data.txt")