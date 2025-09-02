package org.example.booktracker.controller;

import org.example.booktracker.domain.Book;
import org.example.booktracker.domain.Role;
import org.example.booktracker.domain.User;
import org.example.booktracker.service.BookService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String listBooks(Authentication authentication, Model model){
        User currentUser = (User) authentication.getPrincipal();

        List<Book> books;
        if (currentUser.getRole() == Role.ADMIN){
            books = bookService.findAll();
        }else {
            books = bookService.findByUserId(currentUser.getId());
        }
        model.addAttribute("books", books);
        model.addAttribute("currentUser", currentUser);
        return "books/list";
    }

    @GetMapping("/add")
    public String addBookForm(Model model, Authentication authentication){
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("book", new Book());
        return "books/add";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book,
                          @RequestParam(value = "imageFile", required = false)MultipartFile imageFile,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes){
        try {
            User currentUser = (User) authentication.getPrincipal();
            book.setUser(currentUser);
            bookService.saveBookWithImage(book, imageFile);

            redirectAttributes.addFlashAttribute("success", "Book added successfully");
            return "redirect:/books/add";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Error adding book: " +e.getMessage());
            return "redirect:/books/add";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable int id, Authentication authentication, RedirectAttributes redirectAttributes){

        try {
            User currentUser = (User) authentication.getPrincipal();

            if (currentUser.getRole() != Role.ADMIN && !bookService.isBookOwnedByUser(id, currentUser.getId())){
                redirectAttributes.addFlashAttribute("error", "You don't have permission to delete this book");
                return "redirect:/books";
            }

            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Book deleted successfully");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error", "Error deleteing book: " + e.getMessage());
        }

        return "redirect:/books";
    }
}
