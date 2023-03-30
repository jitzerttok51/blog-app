package com.example.blog.repositories;

import com.example.blog.entity.Artifact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ArtifactsRepository extends JpaRepository<Artifact, Long> {


    @Query("""
    SELECT a FROM Artifact a \
    WHERE SIZE(a.owners) = 0
    """)
    Set<Artifact> getUnownedFiles();
}
