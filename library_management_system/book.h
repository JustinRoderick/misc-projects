#ifndef BOOK_H
#define BOOK_H

#include <string>
using namespace std;

// Book class with all required declarations
class Book {
    public:
        string title;
        string author;
        string ISBN;
        bool isAvailable;
        Book(string t, string a, string isbn);
        void display() const;
        void borrowBook();
        void returnBook();
        bool getAvailability() const;
        string getISBN() const;
        friend ostream& operator<<(ostream& os, const Book& book);
};
#endif //BOOK_H
