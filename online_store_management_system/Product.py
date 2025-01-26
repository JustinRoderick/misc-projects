# Probuct class
class Product:
    # Creates a product object
    def __init__(self, product_id: int, name: str, price: float, stock_quantity: int):
        self.product_id = product_id
        self.name = name
        self.price = price
        self.stock_quantity = stock_quantity

    # Updates stock quantity
    def update_stock(self, quantity: int):
        self.stock_quantity += quantity
    
    # Returns true is quantity is greater than 0, false if not.
    def check_availability(self):
        return self.stock_quantity > 0
    
    # Prints product details
    def __str__(self):
        formatted_price = f"${self.price:.2f}"
        return f"Product: {self.name}, Price: {formatted_price}, Stock: {self.stock_quantity}"