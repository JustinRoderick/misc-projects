# Order class
class Order:
    # Create an order object
    def __init__(self, order_id:int, customer, items:list):
        self.order_id = order_id
        self.customer = customer
        self.items = items
        self.status = "Processing"

    # Calculates total price by multiplying price of product with quantity of product in order
    def calculate_total_price(self):
        return sum([product.price * quantity for product, quantity in self.items])

    # Updates status of order
    def update_status(self, status:str):
        self.status = status
    
    # Prints order details
    def __str__(self):
        total_price = f"${self.calculate_total_price():.2f}"
        return f"Order ID: {self.order_id}, Customer: {self.customer.name}, Total Price: {total_price}, Status: {self.status}"