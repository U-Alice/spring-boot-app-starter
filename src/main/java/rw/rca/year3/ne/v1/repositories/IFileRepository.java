package rw.rca.year3.ne.v1.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.rca.year3.ne.v1.enums.EFileStatus;
import rw.rca.year3.ne.v1.fileHandling.File;

import java.util.UUID;

public interface IFileRepository extends JpaRepository<File, UUID> {
    Page<File> findAllByStatus(Pageable pageable, EFileStatus status);

}
