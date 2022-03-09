package BetterreadsApp.betterreads.search;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
//
@Controller
public class SearchController {
    private final String COVER_IMAGE_ROOT="http://covers.openlibrary.org/b/id/";

    private final WebClient webclient;

    public SearchController(WebClient.Builder weBuilder) {
        this.webclient = weBuilder.exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build()).baseUrl("http://openlibrary.org/search.json").build();
    }

    @GetMapping(value = "/search")
    public String getSearchResults(@RequestParam String query, Model model) {
        Mono<SearchResult> resultMono = this.webclient.get().uri("?q={query}", query)
                .retrieve().bodyToMono(SearchResult.class);

        SearchResult res = resultMono.block();
        List<SearchResultBook> books=res.getDocs()
        .stream().limit(10).map(bookResult ->{
           bookResult.setKey(bookResult.getKey().replace("/works/", ""));
           String coverId=bookResult.getCover_i();
           if(StringUtils.hasText(coverId))
           {
               coverId=COVER_IMAGE_ROOT+coverId+"-M.jpg";
           }
           else{
               coverId="/images/no-image1.png";
           }
           bookResult.setCover_i(coverId);
           return bookResult;
        })
        .collect(Collectors.toList());
        model.addAttribute("searchResult", books);
        return "search";
    }

}