package io.github.quarkussocial.rest;

import io.github.quarkussocial.domain.model.Follower;
import io.github.quarkussocial.domain.model.User;
import io.github.quarkussocial.domain.repository.FollowerRepository;
import io.github.quarkussocial.domain.repository.UserRepository;
import io.github.quarkussocial.rest.dto.CreateFollowerRequest;
import io.github.quarkussocial.rest.dto.FollowerResponse;
import io.github.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private UserRepository userRepository;
    private FollowerRepository followerRepository;

    @Inject
    public FollowerResource(UserRepository userRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, CreateFollowerRequest followerRequest) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (userId.equals(followerRequest.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity("Voce não pode seguir voce mesmo!").build();
        }

        User follower = userRepository.findById(followerRequest.getFollowerId());

        if (!followerRepository.follow(user, follower)) {
            Follower entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);
            followerRepository.persist(entity);
        }

        return Response.noContent().build();
    }

    @GET
    public Response listFollower(@PathParam("userId") Long userId) {
        List<Follower> list = followerRepository.findByUser(userId);

        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowersCount(list.size());
        responseObject.setContent(list.stream().map(FollowerResponse::new).collect(Collectors.toList()));

        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(userId, followerId);
        return Response.noContent().build();
    }
}
