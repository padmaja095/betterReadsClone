package BetterreadsApp.betterreads.book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookController {
    @Autowired
    BookRepository bookRepository;

    private final String COVER_IMAGE_ROOT="http://covers.openlibrary.org/b/id/";
    @GetMapping(value = "/books/{bookId}")
    public String getBook(@PathVariable String bookId,Model model)
    {  Optional<Book> optionalBook=bookRepository.findById(bookId);
          if(optionalBook.isPresent())
          {
              Book book=optionalBook.get();
              String coverImageUrl="/images/no-image.jpg";
              List<String> coverIds= new ArrayList<>();
              coverIds=book.getCoverIds();
              if( coverIds!=null)
              {
                  System.out.println("********enteres...");
                   coverImageUrl=COVER_IMAGE_ROOT+book.getCoverIds().get(0)+"-L.jpg";
                  
              }
              model.addAttribute("coverImage", coverImageUrl);
              model.addAttribute("book",book);
              return "book";
            }
            return "book-not-found";
    }
}
