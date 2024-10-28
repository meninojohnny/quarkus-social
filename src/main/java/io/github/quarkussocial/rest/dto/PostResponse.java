package io.github.quarkussocial.rest.dto;

import io.github.quarkussocial.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {

    private String text;
    private LocalDateTime dateTime;

    public PostResponse(String text, LocalDateTime dateTime) {
        this.text = text;
        this.dateTime = dateTime;
    }

    public static PostResponse fromEntity(Post post) {
        return new PostResponse(post.getText(), post.getDateTime());
    }
}
