package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.commentDTO.CommentDTO;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.stream.Collectors;

@Service
public class CommentsService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AdsRepository adsRepository;

    public CommentsService(CommentRepository commentRepository, UserRepository userRepository, AdsRepository adsRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.adsRepository = adsRepository;
    }

    public ResponseWrapper<CommentDTO> getAll(Integer adsId) {
        ResponseWrapper<CommentDTO> wrapper = new ResponseWrapper<>();
        wrapper.setResults(commentRepository.findAllByAdsPk(adsId).stream().map(CommentMapper::commentToCommentDto).collect(Collectors.toList()));
        wrapper.setCount(wrapper.getResults().size());
        return wrapper;
    }

    public CommentDTO addComment(Integer id, CommentDTO commentDTO, Authentication authentication) {
        Comment comment = CommentMapper.commentDtoToComment(commentDTO);
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(NotFoundException::new);
        comment.setAuthor(user);
        comment.setAds(adsRepository.findById(id).orElseThrow(NotFoundException::new));
        commentRepository.save(comment);
        return CommentMapper.commentToCommentDto(comment);
    }

    @Transactional
    public void deleteComment(Integer commentId, Integer adId) {
        commentRepository.deleteByPkAndAdsPk(commentId, adId);
    }

    @Transactional
    public CommentDTO update(Integer commentId, Integer adsId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findByPkAndAdsPk(commentId, adsId).orElseThrow(NotFoundException::new);
        comment.setText(commentDTO.getText());
        return CommentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    public Comment getById(Integer id) {
        return commentRepository.findById(id).orElseThrow(NotFoundException::new);
    }
}
