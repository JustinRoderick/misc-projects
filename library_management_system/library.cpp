#include "library.h"
#include <iostream>
#include <fstream>
#include <string>
using namespace std;

// Adds book to end of Library's book vector
void Library::addBook(const Book& book) {
    books.push_back(book);
}

// Adds patron to Library's patron vector
void Library::addPatron(const Patron& patron) {
    patrons.push_back(patron);
}

// Displays all books in the library by iterating over each book in books
void Library::displayAllBooks() const {
    for(const auto& book : books) {
        // Uses the overloaded operator defined for the book object
        cout << book << endl;
    }
}

// Displays all patrons of the library by iterating over each patron in patrons
void Library::displayAllPatrons() const {
    for(const auto& patron : patrons) {
        // Uses overloaded operator defined for patron object
        cout << patron << endl;
    }
}

// Finds specific book based on ISBN by iterating through each book in books to see is the ISBNs match
Book* Library::findBookByISBN(const string& isbn) {
    for(auto& book : books) {
        if(book.getISBN() == isbn) {
            return &book;
        }
    }
    return nullptr;
}

// Finds specific patron based on ID by iterating through each patron in patrons to see if the patronIDs match
Patron* Library::findPatronByID(const string& id) {
    for(auto& patron : patrons) {
        if(patron.getPatronID() == id) {
            return &patron;
        }
    }
    return nullptr;
}

// Function allows a patron to borrow a book
void Library::borrowBook(const string& patronID, const string& isbn) {
    // Finds patron and the book that they want to borrow
    Patron* patron = findPatronByID(patronID);
    Book* book = findBookByISBN(isbn);
    // Is book and patron exist and book is available then the patron borrows the book
    if((patron != nullptr) && (book != nullptr) && (book->getAvailability()) ) {
        patron->borrowBook(*book);
    }
    else {
        cout << "Book not found or unavailable" << endl;
    }
}

// Function that allows a patron to return a book to the library
void Library::returnBook(const string& patronID, const string& isbn) {
    // Finds specific patron and book and returns book if they both exist
    Patron* patron = findPatronByID(patronID);
    Book* book = findBookByISBN(isbn);
    if((patron != nullptr) && (book != nullptr)) {
        patron->returnBook(*book);
    }
    else {
        cout << "Book or Patron not found" << endl;
    }
}

// Function that prints the patrons and books of the library to a text file
void Library::saveLibraryToFile(const string& fileName) const {
    // Creates file based on file name and opens it for writing
    ofstream file(fileName);
    if(file.is_open()) {
        file << "\nPatrons in the library:\n";
        // Prints each patron in patrons using overloaded operator
        for(const auto& patron : patrons) {
            file << patron << endl;
        }
        file << "\nBooks in the library:\n";
        // Prints each book in books using overloaded operator
        for(const auto& book : books) {
            file << book << endl;
        }
        file.close();
    }
    else {
        cout << "File could not be opened" << endl;
    }
}