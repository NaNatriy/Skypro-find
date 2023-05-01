package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByAdsPk(Integer pk);

    Optional<Comment> findByPkAndAdsPk(Integer pk, Integer adsPk);

    void deleteByPkAndAdsPk(Integer pk, Integer adsPk);
}
