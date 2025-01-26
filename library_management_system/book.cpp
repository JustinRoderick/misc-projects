#include "book.h"
#include <iostream>
#include <string>
using namespace std;

// Initializes the Book constructor to create a book and sets values
Book::Book(string t, string a, string isbn) {
    title = t;
    author = a;
    ISBN = isbn;
    isAvailable = true;
}

// Displays all book attributes and has 'isAvailable' say yes or no
void Book::display() const {
    cout << "Title: " << title << "\nAuthor: " << author << "\nISBN: " << ISBN << "\nAvailable: " << (isAvailable ? "Yes" : "No") << endl;
}

// Sets isAvailable to false
void Book::borrowBook() {
    isAvailable = false;
}

// Sets is available to True
void Book::returnBook() {
    isAvailable = true;
}

// Returns true or false based on book availability
bool Book::getAvailability() const {
    return isAvailable;
}

// Returns book ISBN
string Book::getISBN() const {
    return ISBN;
}

// Function that overloads the output stream operator
ostream& operator<<(ostream& os, const Book& book) {
    // Creates a string with book details
    os << "Title: " << book.title << "\nAuthor: " << book.author
       << "\nISBN: " << book.ISBN << "\nAvailable: "
       << (book.isAvailable ? "Yes" : "No") << endl;
    // Returns the output stream to be used to easily print book details
    return os;
}
