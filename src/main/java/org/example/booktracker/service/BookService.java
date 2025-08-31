package org.example.booktracker.service;

import jakarta.transaction.Transactional;
import org.example.booktracker.dao.BookRepository;
import org.example.booktracker.domain.Book;
import org.example.booktracker.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    private final S3Service s3Service;


    public BookService(BookRepository bookRepository, S3Service s3Service) {
        this.bookRepository = bookRepository;
        this.s3Service = s3Service;
    }

    public List<Book> findAll(){
        return bookRepository.findAll();
    }

    public  Book save(Book book){
        return bookRepository.save(book);
    }

    public List<Book> findByUserId(int userId){
        return bookRepository.findByUserId(userId);
    }

    //save book with image
    public Book saveBookWithImage(Book book, MultipartFile file){
        try {
            if (file != null && !file.isEmpty()){
                String imageKey = s3Service.putObject(file, "book-covers");
                book.setImageKey(imageKey);
                book.setImageUrl(s3Service.getFileUrl(imageKey));
            }
            return bookRepository.save(book);
        }catch (Exception e) {
            throw new RuntimeException("Failed to save book with image: " + e.getMessage());
        }
    }

    //delete book and image
    public void deleteBook(int bookId){
        try{
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (bookOpt.isPresent()){
                Book book = bookOpt.get();

                if (book.getImageKey() != null && !book.getImageKey().isEmpty()){
                    s3Service.deleteFile(book.getImageKey());
                }
                bookRepository.deleteById(bookId);
            }
        }catch (Exception e){
            throw new RuntimeException("Failed to delete book: " +e.getMessage());
        }
    }

    public boolean isBookOwnedByUser(int bookId, int userId){
        return bookRepository.findById(bookId).map(book -> book.getUser().getId() == userId).orElse(false);
    }

}
