package com.barsik.backend.service.service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.barsik.backend.api.DTO.request.RegistrationRequestLong;
import com.barsik.backend.api.DTO.request.UserUpdateRequest;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.OwnerRepository;
import com.barsik.backend.repository.SitterRepository;
import com.barsik.backend.repository.UserRepository;
import com.barsik.backend.service.UserService;

/**
 * Unit tests for UserService with reflection-based id setting to avoid requiring a public setId method.
 */
@Disabled
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private SitterRepository sitterRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void setEntityId(Object entity, Long idValue) {
        try {
            Field idField = null;
            Class<?> cls = entity.getClass();
            while (cls != null) {
                try {
                    idField = cls.getDeclaredField("id");
                    break;
                } catch (NoSuchFieldException e) {
                    cls = cls.getSuperclass();
                }
            }
            if (idField == null) {
                throw new RuntimeException("No 'id' field found on class " + entity.getClass());
            }
            idField.setAccessible(true);
            Class<?> type = idField.getType();
            if (type.equals(Long.class)) {
                idField.set(entity, idValue);
            } else if (type.equals(long.class)) {
                idField.setLong(entity, idValue != null ? idValue : 0L);
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                int intVal = idValue == null ? 0 : idValue.intValue();
                if (type.equals(Integer.class)) idField.set(entity, intVal);
                else idField.setInt(entity, intVal);
            } else {
                idField.set(entity, idValue);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set id via reflection", e);
        }
    }

    // ---- saveUser ---------------------------------------------------------
    @Test
    void saveUser_shouldCallRepositoryAndReturnSavedUser() {
        User user = new User();
        setEntityId(user, 1L);
        user.setEmail("a@b.com");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.saveUser(user);

        assertNotNull(saved);
        assertEquals("a@b.com", saved.getEmail());
        // compare id robustly as string to avoid long/Long mismatch
        assertEquals(String.valueOf(1L), String.valueOf(getIdValue(saved)));
        verify(userRepository, times(1)).save(user);
    }

    // ---- findByEmail ------------------------------------------------------
    @Test
    void findByEmail_whenFound_shouldReturnUser() {
        String email = "test@example.com";
        User user = new User();
        setEntityId(user, 2L);
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.findByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_whenNotFound_shouldThrowRuntimeException() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.findByEmail(email));
        assertTrue(ex.getMessage().contains("Owner profile not found"));
        verify(userRepository, times(1)).findByEmail(email);
    }

    // ---- findById ---------------------------------------------------------
    @Test
    void findById_whenFound_shouldReturnUser() {
        Long id = 5L;
        User user = new User();
        setEntityId(user, id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User res = userService.findById(id);

        assertNotNull(res);
        assertEquals(String.valueOf(id), String.valueOf(getIdValue(res)));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void findById_whenNotFound_shouldThrowRuntimeException() {
        Long id = 99L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.findById(id));
        assertTrue(ex.getMessage().contains("User not found"));
        verify(userRepository, times(1)).findById(id);
    }

    // ---- updateUser -------------------------------------------------------
    @Test
    void updateUser_shouldApplyOnlyNonNullFieldsAndSave() {
        Long id = 10L;
        User existing = new User();
        setEntityId(existing, id);
        existing.setEmail("old@mail.com");
        existing.setFirstName("OldFirst");
        existing.setLastName("OldLast");
        existing.setPhoneNumber("000");
        existing.setAvatarUrl("oldAvatar");
        existing.setAddress("Old Address");

        UserUpdateRequest dto = mock(UserUpdateRequest.class);
        when(dto.getEmail()).thenReturn("new@mail.com");       // non-null -> should update
        when(dto.getFirstName()).thenReturn(null);             // null -> should not update
        when(dto.getLastName()).thenReturn("NewLast");         // non-null -> update
        when(dto.getPhoneNumber()).thenReturn(null);           // null -> no change
        when(dto.getAvatarUrl()).thenReturn("newAvatar");      // non-null -> update
        when(dto.getAddress()).thenReturn(null);               // null -> no change

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(id, dto);

        assertNotNull(updated);
        assertEquals("new@mail.com", updated.getEmail());
        assertEquals("OldFirst", updated.getFirstName(), "firstName should remain unchanged (dto null)");
        assertEquals("NewLast", updated.getLastName());
        assertEquals("000", updated.getPhoneNumber(), "phoneNumber should remain unchanged (dto null)");
        assertEquals("newAvatar", updated.getAvatarUrl());
        assertEquals("Old Address", updated.getAddress(), "address should remain unchanged (dto null)");

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(existing);
    }

    @Test
    void updateUser_whenUserNotFound_shouldThrowRuntimeException() {
        Long id = 777L;
        UserUpdateRequest dto = mock(UserUpdateRequest.class);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.updateUser(id, dto));
        assertTrue(ex.getMessage().contains("User not found"));
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any());
    }

    // ---- touchUpdatedAt ---------------------------------------------------
    @Test
    void touchUpdatedAt_whenUserExists_shouldCallSave() {
        Long id = 4L;
        User user = new User();
        setEntityId(user, id);
        user.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.touchUpdatedAt(id);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void touchUpdatedAt_whenUserNotFound_shouldThrowRuntimeException() {
        Long id = 12345L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.touchUpdatedAt(id));
        assertTrue(ex.getMessage().contains("User not found"));
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any());
    }

    // ---- registerUser -----------------------------------------------------
    @Test
    void registerUser_withOwnerRole_shouldCreateUserAndOwner() {
        RegistrationRequestLong req = mock(RegistrationRequestLong.class);
        when(req.getFirstName()).thenReturn("Ivan");
        when(req.getLastName()).thenReturn("Ivanov");
        when(req.getEmail()).thenReturn("ivan@mail.com");
        when(req.getPassword()).thenReturn("plain");
        when(req.getPhoneNumber()).thenReturn("+70000000000");
        when(req.getRole()).thenReturn(com.barsik.backend.api.DTO.request.UserRole.OWNER);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            setEntityId(u, 111L);
            return u;
        });

        when(passwordEncoder.encode("plain")).thenReturn("hashed");

        userService.registerUser(req);

        verify(passwordEncoder, times(1)).encode("plain");
        verify(userRepository, times(1)).save(any(User.class));
        verify(ownerRepository, times(1)).save(any(Owner.class));
        verify(sitterRepository, never()).save(any(Sitter.class));
    }

    @Test
    void registerUser_withSitterRole_shouldCreateUserAndSitter() {
        RegistrationRequestLong req = mock(RegistrationRequestLong.class);
        when(req.getFirstName()).thenReturn("Petya");
        when(req.getLastName()).thenReturn("Petrov");
        when(req.getEmail()).thenReturn("petya@mail.com");
        when(req.getPassword()).thenReturn("pw");
        when(req.getPhoneNumber()).thenReturn("+79999999999");
        when(req.getRole()).thenReturn(com.barsik.backend.api.DTO.request.UserRole.SITTER);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode("pw")).thenReturn("pwHashed");

        userService.registerUser(req);

        verify(passwordEncoder, times(1)).encode("pw");
        verify(userRepository, times(1)).save(any(User.class));
        verify(sitterRepository, times(1)).save(any(Sitter.class));
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void registerUser_whenRoleIsNull_shouldThrowIllegalArgumentException() {
        RegistrationRequestLong req = mock(RegistrationRequestLong.class);
        when(req.getFirstName()).thenReturn("NoRole");
        when(req.getLastName()).thenReturn("User");
        when(req.getEmail()).thenReturn("norole@mail.com");
        when(req.getPassword()).thenReturn("x");
        when(req.getPhoneNumber()).thenReturn("+70000000001");
        when(req.getRole()).thenReturn(null);

        when(passwordEncoder.encode(anyString())).thenReturn("xHashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(req));
        assertTrue(ex.getMessage().contains("User role must be specified"));

        verify(userRepository, times(1)).save(any(User.class));
        verify(ownerRepository, never()).save(any());
        verify(sitterRepository, never()).save(any());
    }

    private Object getIdValue(Object entity) {
        try {
            Field idField = null;
            Class<?> cls = entity.getClass();
            while (cls != null) {
                try {
                    idField = cls.getDeclaredField("id");
                    break;
                } catch (NoSuchFieldException e) {
                    cls = cls.getSuperclass();
                }
            }
            if (idField == null) return null;
            idField.setAccessible(true);
            return idField.get(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
