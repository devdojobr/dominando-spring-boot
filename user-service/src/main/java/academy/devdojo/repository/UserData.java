package academy.devdojo.repository;

import academy.devdojo.domain.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserData {
    private final List<User> users = new ArrayList<>();

    {
        var goku = User.builder().id(1L).firstName("Goku").lastName("Son").email("goku.son@gbz.com").build();
        var gohan = User.builder().id(2L).firstName("Gohan").lastName("Son").email("gohan.son@dbz.com").build();
        var goten = User.builder().id(3L).firstName("Goten").lastName("Son").email("goten.son@dbz.com").build();
        users.addAll(List.of(goku, gohan, goten));
    }

    public List<User> getUsers() {
        return users;
    }
}
