#ifndef PATRON_H
#define PATRON_H

#include "book.h"
#include <string>
#include <vector>
using namespace std;

// Patron class with all required declarations
class Patron {
    public:
        string name;
        string patronID;
        vector<Book*> borrowedBooks;
        Patron(string n, string id);
        void display() const;
        void borrowBook(Book& book);
        void returnBook(Book& book);
        string getPatronID() const;
        friend ostream& operator<<(ostream& os, const Patron& patron);
};

#endif //PATRON_H