package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.BookedProduct;

public interface BookedProductRepository extends JpaRepository<BookedProduct, Long> {
}
