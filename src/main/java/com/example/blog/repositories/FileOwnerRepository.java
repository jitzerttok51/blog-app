package com.example.blog.repositories;

import com.example.blog.entity.FileOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface FileOwnerRepository extends JpaRepository<FileOwner, Long> {

    @Query("""
          SELECT fo FROM FileOwner fo \
          JOIN FETCH fo.artifact a \
          JOIN FETCH fo.owner \
          WHERE a.checksum = :checksum
          """)
    Set<FileOwner> findAllByChecksumInt(@Param("checksum") String checksum);

    default Map<String, FileOwner> findAllByChecksum(String checksum) {
        return findAllByChecksumInt(checksum)
                   .stream()
                   .collect(Collectors.toMap(f->f.getOwner().getUsername(), f->f));
    }

    @Query("""
          SELECT fo FROM FileOwner fo \
          JOIN FETCH fo.artifact \
          JOIN FETCH fo.owner u \
          WHERE u.id = :id
          """)
    Stream<FileOwner> findAllByUser(@Param("id") long id);

    @Query("""
          SELECT fo FROM FileOwner fo \
          JOIN FETCH fo.artifact \
          JOIN FETCH fo.owner u \
          WHERE u.id = :userId AND fo.id = :fileId
          """)
    Optional<FileOwner> findSingleByUser(@Param("userId") long userId, @Param("fileId") long fileId);

    @Query("""
          SELECT fo FROM FileOwner fo \
          JOIN FETCH fo.artifact \
          JOIN FETCH fo.owner u \
          WHERE fo.id = :id
          """)
    Optional<FileOwner> findById(@Param("id") long id);
}