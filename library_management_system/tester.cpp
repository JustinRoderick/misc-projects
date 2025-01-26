#include "library.h"
#include "iostream"
using namespace std;

int main() {
    Library library;
    // Create books
    Book book1("1984", "George Orwell", "9780451524935");
    Book book2("To Kill a Mockingbird", "Harper Lee", "9780060935467");
    // Create patrons
    Patron patron1("John Doe", "P123");
    Patron patron2("Jane Smith", "P124");
    // Add books and patrons to the library
    library.addBook(book1);
    library.addBook(book2);
    library.addPatron(patron1);
    library.addPatron(patron2);
    // Borrow a book
    library.borrowBook("P123", "9780451524935");
    // Display all books and patrons
    std::cout << "Library Books:\n";
    library.displayAllBooks();
    std::cout << "\nLibrary Patrons:\n";
    library.displayAllPatrons();
    // Return a book
    library.returnBook("P123", "9780451524935");
    // Display all books and patrons again
    std::cout << "\nLibrary Books After Return:\n";
    library.displayAllBooks();
    std::cout << "\nLibrary Patrons After Return:\n";
    library.displayAllPatrons();
    library.saveLibraryToFile("library_data.txt"); // Save library data to a file
    return 0;
}