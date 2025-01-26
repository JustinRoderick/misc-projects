# Store class
class Store:
    # Creates Store object
    def __init__(self, store_name:str):
        self.store_name = store_name
        self.products = []
        self.customers = []
        self.orders = []
    
    # Adds product to list
    def add_product(self, product):
        self.products.append(product)
    
    # Adds customer to list
    def add_customer(self, customer):
        self.customers.append(customer)
    
    # Adds order and updates stock for each product in order
    def place_order(self, order):
        self.orders.append(order)
        for product, quantity in order.items:
            product.update_stock(-quantity)
    
    # Returns products in store   
    def get_inventory(self):
        return self.products
    
    # Prints store details
    def __str__(self):
        return f"Store: {self.store_name}, Products: {len(self.products)}, Customers: {len(self.customers)}, Orders: {len(self.orders)}"
    
    # Exports store data to file in correct format
    def export_data_to_file(self, file_name:str):
        with open(file_name, "w") as file:
            file.write(f"Store Name: {self.store_name}\n")
            file.write("========================================\n\n")
            file.write("Products:\n")
            file.write("----------------------------------------\n")
            for product in self.products:
                file.write(f"{product}\n")
            file.write("\nCustomers:\n")
            file.write("----------------------------------------\n")
            for customer in self.customers:
                file.write(f"{customer}\n")
            file.write("\nOrders:\n")
            file.write("----------------------------------------\n")
            for order in self.orders:
                file.write(f"{order}\n")