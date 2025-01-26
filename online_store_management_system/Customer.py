# Customer class
class Customer:
    # Creates a new customer object
    def __init__(self, customer_id:int, name:str, email:str):
        self.customer_id = customer_id
        self.name = name
        self.email = email
        self.orders = []

    # Adds order to customers orders
    def add_order(self, order):
        self.orders.append(order)

    # Returns orders of customer
    def get_order_history(self):
        return self.orders

    # Prints customer details
    def __str__(self):
        return f"Customer: {self.name}, Email: {self.email}, Orders: {len(self.orders)}"