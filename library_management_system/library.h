#ifndef LIBRARY_H
#define LIBRARY_H

#include "book.h"
#include "patron.h"
#include <string>
#include <vector>
using namespace std;

// Library class with all required declarations
class Library {
    public:
        vector<Book> books;
        vector<Patron> patrons;
        void addBook(const Book& book);
        void addPatron(const Patron& patron);
        void displayAllBooks() const;
        void displayAllPatrons() const;
        Book* findBookByISBN(const string& isbn);
        Patron* findPatronByID(const string& id);
        void borrowBook(const string& patronID, const string& isbn);
        void returnBook(const string& patronID, const string& isbn);
        void saveLibraryToFile(const string& filename) const;
};

#endif //LIBRARY_H