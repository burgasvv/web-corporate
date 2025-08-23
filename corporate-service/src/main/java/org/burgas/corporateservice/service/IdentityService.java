package org.burgas.corporateservice.service;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.burgas.corporateservice.dto.identity.IdentityRequest;
import org.burgas.corporateservice.dto.identity.IdentityWithEmployeeResponse;
import org.burgas.corporateservice.entity.Authority;
import org.burgas.corporateservice.entity.Identity;
import org.burgas.corporateservice.entity.Media;
import org.burgas.corporateservice.exception.*;
import org.burgas.corporateservice.mapper.IdentityMapper;
import org.burgas.corporateservice.message.IdentityMessages;
import org.burgas.corporateservice.repository.IdentityRepository;
import org.burgas.corporateservice.service.contract.CrudService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class IdentityService implements CrudService<IdentityRequest, IdentityWithEmployeeResponse> {

    private final IdentityRepository identityRepository;
    private final IdentityMapper identityMapper;
    private final PasswordEncoder passwordEncoder;
    private final MediaService mediaService;

    public Identity findIdentity(final UUID identityId) {
        return this.identityRepository.findById(identityId == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : identityId)
                .orElseThrow(() -> new IdentityNotFoundException(IdentityMessages.IDENTITY_NOT_FOUND.getMessage()));
    }

    @Override
    public List<IdentityWithEmployeeResponse> findAll() {
        return this.identityRepository.findAll()
                .stream()
                .map(this.identityMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Async(value = "asyncExecutor")
    public CompletableFuture<List<IdentityWithEmployeeResponse>> findAllAsync() {
        return CompletableFuture.supplyAsync(this.identityRepository::findAll)
                .thenApplyAsync(
                        identities -> identities.stream()
                                .map(this.identityMapper::toResponse)
                                .collect(Collectors.toList())
                );
    }

    @Override
    public IdentityWithEmployeeResponse findById(UUID uuid) {
        return this.identityRepository.findById(
                        uuid == null ? UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : uuid
                )
                .map(this.identityMapper::toResponse)
                .orElseThrow(() -> new IdentityNotFoundException(IdentityMessages.IDENTITY_NOT_FOUND.getMessage()));
    }

    @Async(value = "asyncExecutor")
    public CompletableFuture<IdentityWithEmployeeResponse> findByIdAsync(final UUID identityId) {
        return CompletableFuture.supplyAsync(
                        () -> this.identityRepository.findById(identityId == null ?
                                UUID.nameUUIDFromBytes("0".getBytes(StandardCharsets.UTF_8)) : identityId)
                )
                .thenApplyAsync(
                        identity -> identity.map(this.identityMapper::toResponse)
                                .orElseThrow(() -> new IdentityNotFoundException(IdentityMessages.IDENTITY_NOT_FOUND.getMessage()))
                );
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public IdentityWithEmployeeResponse createOrUpdate(IdentityRequest identityRequest) {
        return this.identityMapper.toResponse(
                this.identityRepository.save(this.identityMapper.toEntity(identityRequest))
        );
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<IdentityWithEmployeeResponse> createOrUpdateAsync(final IdentityRequest identityRequest) {
        return CompletableFuture.supplyAsync(() -> this.identityMapper.toEntity(identityRequest))
                .thenApplyAsync(this.identityRepository::save)
                .thenApplyAsync(this.identityMapper::toResponse);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String delete(UUID uuid) {
        Identity identity = this.findIdentity(uuid);
        this.identityRepository.delete(identity);
        return IdentityMessages.IDENTITY_DELETED.getMessage();
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<String> deleteAsync(final UUID identityId) {
        return CompletableFuture.supplyAsync(() -> this.findIdentity(identityId))
                .thenApplyAsync(
                        identity -> {
                            this.identityRepository.delete(identity);
                            return IdentityMessages.IDENTITY_DELETED.getMessage();
                        }
                );
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String changePassword(final UUID identityId, final Map<String, String> password) {
        String newPassword = password.get("password");
        if (newPassword == null || newPassword.isBlank())
            throw new PasswordNotFoundException(IdentityMessages.PASSWORD_NOT_FOUND.getMessage());

        Identity identity = this.findIdentity(identityId);

        if (this.passwordEncoder.matches(newPassword, identity.getPassword()))
            throw new PasswordMatchesException(IdentityMessages.PASSWORD_MATCHES.getMessage());

        identity.setPassword(this.passwordEncoder.encode(newPassword));
        this.identityRepository.save(identity);

        return IdentityMessages.PASSWORD_CHANGED.getMessage();
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<String> changePasswordAsync(final UUID identityId, final Map<String, String> password) {
        return CompletableFuture.supplyAsync(() -> this.changePassword(identityId, password));
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String enableDisable(final UUID identityId, final Boolean enable) {
        Identity identity = this.findIdentity(identityId);
        if (identity.getEnabled().equals(enable))
            throw new EnableDisableMatchesException(IdentityMessages.ENABLE_DISABLE_MATCHES.getMessage());

        identity.setEnabled(enable);
        Identity saved = this.identityRepository.save(identity);
        return saved.getEnabled() ? IdentityMessages.IDENTITY_ENABLED.getMessage() : IdentityMessages.IDENTITY_DISABLED.getMessage();
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<String> enableDisableAsync(final UUID identityId, final Boolean enable) {
        return CompletableFuture.supplyAsync(() -> this.enableDisable(identityId, enable));
    }

    private Identity changeAuthority(final UUID identityId, final Authority authority) {
        Identity identity = this.findIdentity(identityId);
        identity.setAuthority(authority);
        return this.identityRepository.save(identity);
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public IdentityWithEmployeeResponse makeEmployee(final UUID identityId) {
        Identity identity = this.changeAuthority(identityId, Authority.WORKER);
        return this.identityMapper.toResponse(identity);
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<IdentityWithEmployeeResponse> makeEmployeeAsync(final UUID identityId) {
        return CompletableFuture.supplyAsync(() -> this.changeAuthority(identityId, Authority.WORKER))
                .thenApplyAsync(this.identityMapper::toResponse);
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public IdentityWithEmployeeResponse makeDirector(final UUID identityId) {
        Identity identity = this.changeAuthority(identityId, Authority.DIRECTOR);
        return this.identityMapper.toResponse(identity);
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<IdentityWithEmployeeResponse> makeDirectorAsync(final UUID identityId) {
        return CompletableFuture.supplyAsync(() -> this.changeAuthority(identityId, Authority.DIRECTOR))
                .thenApplyAsync(this.identityMapper::toResponse);
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public IdentityWithEmployeeResponse makeUser(final UUID identityId) {
        Identity identity = this.changeAuthority(identityId, Authority.USER);
        return this.identityMapper.toResponse(identity);
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<IdentityWithEmployeeResponse> makeUserAsync(final UUID identityId) {
        return CompletableFuture.supplyAsync(() -> this.changeAuthority(identityId, Authority.USER))
                .thenApplyAsync(this.identityMapper::toResponse);
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String uploadImage(final UUID identityId, final Part file) {
        if (file == null)
            throw new PartFileEmptyException(IdentityMessages.PART_FILE_EMPTY.getMessage());

        Identity identity = this.findIdentity(identityId);
        Media image = this.mediaService.upload(file);
        identity.setImage(image);
        this.identityRepository.save(identity);
        return IdentityMessages.IDENTITY_IMAGE_UPLOADED.getMessage();
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<String> uploadImageAsync(final UUID identityId, final Part file) {
        return CompletableFuture.supplyAsync(() -> this.uploadImage(identityId, file));
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String changeImage(final UUID identityId, final Part file) {
        if (file == null)
            throw new PartFileEmptyException(IdentityMessages.PART_FILE_EMPTY.getMessage());

        Identity identity = this.findIdentity(identityId);
        Media image = identity.getImage();
        this.mediaService.change(image.getId(), file);
        return IdentityMessages.IDENTITY_IMAGE_CHANGED.getMessage();
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<String> changeImageAsync(final UUID identityId, final Part file) {
        return CompletableFuture.supplyAsync(() -> this.changeImage(identityId, file));
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public String deleteImage(final UUID identityId) {
        Identity identity = this.findIdentity(identityId);
        Media image = identity.getImage();
        identity.setImage(null);
        this.identityRepository.save(identity);
        this.mediaService.delete(image.getId());
        return IdentityMessages.IDENTITY_IMAGE_DELETED.getMessage();
    }

    @Async(value = "asyncExecutor")
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public CompletableFuture<String> deleteImageAsync(final UUID identityId) {
        return CompletableFuture.supplyAsync(() -> this.deleteImage(identityId));
    }
}
