package library;

//import java.util.Map;
//import java.util.HashMap;
import java.util.Scanner;

public class User {

    Scanner sc = new Scanner(System.in);

    private String name;
    private String password;
    private String phone;
    private int age;
    //private Map <String, Book> myMap_Book = new HashMap<>();


    public User() {
    }

    public User( String _name, int _age, String _phone, String _password){
        age = _age;
        password = _password;
        name = _name;
        phone = _phone;
    }

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /*
    public Map<String, Book> getMyMap_Book() {
        return myMap_Book;
    }


    public void setMyMap_Book(Map<String, Book> myMap_Book) {
        this.myMap_Book = myMap_Book;
    }

    public void rentBook(Book book) {
        myMap_Book.put(book.getNameB(), book);
    }

    public void returnBook(Book book) {
        String nameB = sc.nextLine();
        if (!myMap_Book.containsKey(nameB)){
            System.out.println("ban da tra sach r");
        } else {
            myMap_Book.remove(nameB);
            System.out.println("tra sach thanh cong");
        }
    }

    public void showRentedBook() {
        int count = 1;
        for (Map.Entry<String, Book> entry : myMap_Book.entrySet()){
            System.out.println("");
            System.out.print(count + ", ");
            entry.getValue().showBookUser();
            count++;
        }
    } */
}