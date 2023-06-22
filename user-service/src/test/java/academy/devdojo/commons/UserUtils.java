package academy.devdojo.commons;

import academy.devdojo.domain.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserUtils {
    public List<User> newUserList() {
        var ichigo = User.builder().id(1L).firstName("Ichigo").lastName("Kurosaki").email("ichigo.kurosaki@bleach.com").build();
        var toyohisa = User.builder().id(2L).firstName("Toyohisa").lastName("Shimazu").email("toyohisa.shimazu@drifters.com").build();
        var oda = User.builder().id(3L).firstName("Oda").lastName("Nobunaga").email("oda.nobunaga@drifters.com").build();
        return new ArrayList<>(List.of(ichigo, toyohisa, oda));
    }

    public User newUserToSave() {
        return User.builder()
                .id(99L)
                .firstName("Ash")
                .lastName("Ketchum")
                .email("ash.ketchum@pokemon.com")
                .build();
    }

    public User newUserSaved() {
        return User.builder()
                .id(99L)
                .firstName("Ash")
                .lastName("Ketchum")
                .email("ash.ketchum@pokemon.com")
                .build();
    }
}
