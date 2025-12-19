package com.epam.rd.autocode.spring.project.service.specifications;

import com.epam.rd.autocode.spring.project.model.Book;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookSpecification {

    public static Specification<Book> nameContains(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Book> genreEquals(String genre) {
        return (root, query, cb) ->
                genre == null ? null : cb.equal(root.get("genre"), genre);
    }

    public static Specification<Book> authorContains(String author) {
        return (root, query, cb) ->
                author == null ? null : cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Book> languageEquals(Enum<?> language) {
        return (root, query, cb) ->
                language == null ? null : cb.equal(root.get("language"), language);
    }

    public static Specification<Book> ageGroupEquals(Enum<?> ageGroup) {
        return (root, query, cb) ->
                ageGroup == null ? null : cb.equal(root.get("ageGroup"), ageGroup);
    }

    public static Specification<Book> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("price"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.between(root.get("price"), min, max);
        };
    }

    public static Specification<Book> publishedBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from == null) return cb.lessThanOrEqualTo(root.get("publicationDate"), to);
            if (to == null) return cb.greaterThanOrEqualTo(root.get("publicationDate"), from);
            return cb.between(root.get("publicationDate"), from, to);
        };
    }
}
