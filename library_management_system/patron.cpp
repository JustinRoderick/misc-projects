#include "patron.h"
#include <iostream>
#include <string>
using namespace std;

// Creates Patron object and sets values
Patron::Patron(string n, string id) {
    name = n;
    patronID = id;
}

// Displays patron information and number of borrowed books
void Patron::display() const {
    cout << "Patron Name: " << name << "\nPatron ID: " << patronID << "\nBorrowed Books: " << borrowedBooks.size();
    // Range-based for loop that loops through every Book in borrowedBooks
    // Prints Title and Author for every borrowed book
    for(const auto& book : borrowedBooks) {
        cout << "\n- Title: " << book->title << "\nAuthor:  " << book->author <<endl;
    }
}

// Checks if a book is available and adds it to back of a patrons borrowedBook vector
void Patron::borrowBook(Book& book) {
    if(book.getAvailability()) {
        borrowedBooks.push_back(&book);
        book.borrowBook();
    }
    else {
        cout << book.title << " is not currently available." << endl;
    }

}

// Returns book by looping through a patrons borrowedBooks till it is found and then it is removed based on its index
void Patron::returnBook(Book& book) {
    for(int i = 0; i < borrowedBooks.size(); i++) {
        if(borrowedBooks[i] == &book) {
            borrowedBooks.erase(borrowedBooks.begin() + i);
            // Sets the book to available
            book.returnBook();
            break;
        }
    }
}

// Returns Patron ID
string Patron::getPatronID() const {
    return patronID;
}

// Overloads the << output stream operator to easily print patron info and details on their borrowed books
ostream& operator<<(ostream& os, const Patron& patron) {
    os << "Patron Name: " << patron.name << "\nPatron ID: " << patron.patronID << "\nBorrowed Books: " << patron.borrowedBooks.size();
    for(const auto& book : patron.borrowedBooks) {
        os << "\n- Title: " << book->title << "\nAuthor: " << book->author << "\nISBN: " << book->ISBN << "\nAvailable: " << (book->isAvailable ? "Yes" : "No") << endl;
    }
    return os;
}