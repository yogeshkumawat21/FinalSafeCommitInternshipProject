package com.App.Yogesh.ResponseDto;
import com.App.Yogesh.Models.Role;
import com.App.Yogesh.Models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
        private Integer id;
        private String firstName;
        private String lastName;
        private String gender;
        private String email;
        private List<Integer> followers;
        private List<Integer> followings;
        private List<Role> authorities;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.gender = user.getGender();
        this.email = user.getEmail();
        this.followers = user.getFollowers();
        this.followings = user.getFollowings();
        this.authorities = user.getRoles();

    }
}






